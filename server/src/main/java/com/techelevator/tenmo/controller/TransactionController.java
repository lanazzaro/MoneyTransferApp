package com.techelevator.tenmo.controller;


import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransactionDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.PresentableUser;
import com.techelevator.tenmo.model.Transaction;
import com.techelevator.tenmo.model.TransactionDTO;
import com.techelevator.tenmo.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.net.http.HttpRequest;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/transactions")
@PreAuthorize("isAuthenticated()")
public class TransactionController {
    private TransactionDao transactionDao;
    private AccountDao accountDao;
    private UserDao userDao;
    private ChecksController checksController;

    public TransactionController(TransactionDao transactionDao, AccountDao accountDao, UserDao userDao, ChecksController checksController) {
        this.transactionDao = transactionDao;
        this.accountDao = accountDao;
        this.userDao = userDao;
        this.checksController = checksController;
    }

    //    -send to spc user (cant be self, more than $0) --> update balance
    @RequestMapping(path = "/send", method = RequestMethod.POST)
    public void sendMoney(@RequestBody @Valid TransactionDTO inputTransactionDto, Principal principal){
        int fromUserId = userDao.findIdByUsername(principal.getName());
        // if check balance fails
        if (!checksController.enoughMoney(fromUserId, inputTransactionDto.getAmount())) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "New transaction amounts cannot exceed a user's current balance.");
        }

        // set status approved
        Transaction newTransaction = new Transaction();
        newTransaction.setStatus("Approved");
        newTransaction.setFromUserId(fromUserId);
        newTransaction.setToUserId(inputTransactionDto.getToUserId());
        newTransaction.setAmount(inputTransactionDto.getAmount());

        // reduce from user's amount by balance amount
        accountDao.subtractFromBalance(fromUserId, newTransaction.getAmount());

        // add to user's amount by balance amount
        accountDao.addToBalance(newTransaction.getToUserId(), newTransaction.getAmount());

        // create and log transaction
        boolean transactionResult = transactionDao.create(newTransaction);
        if (!transactionResult) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Transaction did not save to the database. (sendMoney)");
        }
    }


//    -request from spc user (cant be self, more than $0)
//    -get transaction details by id
//    -get transaction by user
//    -get List of users --> put in user controller?
    @RequestMapping(path = "/userlist", method = RequestMethod.GET)
    public List<PresentableUser> getUsers(){
        List<PresentableUser> userList = new ArrayList<>();
        List<User> fullUserList = userDao.findAll();

        // loop through the full userlist with all info
        for (User fullUser : fullUserList){
            // create new PresentableUser
            PresentableUser newPresentation = new PresentableUser();

            // Set vars from fullUser to presentableUser
            newPresentation.setId(fullUser.getId());
            newPresentation.setUsername(fullUser.getUsername());

            // Add presentable user
            userList.add(newPresentation);
        }
        return userList;
    }
//    -get List(??) of pending transactions
//    -make pending transaction decision (checks)
//        -if accept, update balance
//        -change status
//        -must have enough $
//    -view transaction history

}
