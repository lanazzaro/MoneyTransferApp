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
        int toUserId = inputTransactionDto.getUserId();
        if (!checksController.userIdsAreDifferent(fromUserId, toUserId)){
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Transaction must go to someone other than yourself.");
        }
        // if check balance fails
        if (!checksController.enoughMoney(fromUserId, inputTransactionDto.getAmount())) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "New transaction amounts cannot exceed a user's current balance.");
        }

        // set status approved
        Transaction newTransaction = new Transaction();
        newTransaction.setStatus("Approved");
        newTransaction.setFromUserId(fromUserId);
        newTransaction.setToUserId(toUserId);
        newTransaction.setAmount(inputTransactionDto.getAmount());

        runTransaction(fromUserId, toUserId, newTransaction.getAmount());

        // create and log transaction
        int transactionResult = transactionDao.create(newTransaction);
        if (transactionResult == 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Transaction did not save to the database. (sendMoney)");
        }
    }

    //create request, get pending request, decide pending request
    @RequestMapping(path = "/request", method = RequestMethod.POST)
    public void requestMoney(@RequestBody @Valid TransactionDTO inputTransactionDto, Principal principal){
        int fromUserId = inputTransactionDto.getUserId();
        int toUserId = userDao.findIdByUsername(principal.getName());
        if (!checksController.userIdsAreDifferent(toUserId, inputTransactionDto.getUserId())){
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "You can't request money from yourself.");
        }

        // set status approved
        Transaction newTransaction = new Transaction();
        newTransaction.setStatus("Pending");
        newTransaction.setFromUserId(fromUserId);
        newTransaction.setToUserId(toUserId);
        newTransaction.setAmount(inputTransactionDto.getAmount());

        // create and log transaction
        int transactionResult = transactionDao.create(newTransaction);
        if (transactionResult == 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Transaction did not save to the database. (requestMoney)");
        }
    }

    @RequestMapping(path = "/view/{onlyPending}", method = RequestMethod.GET)
    public List<Transaction> viewTransactionsByUser(@PathVariable boolean onlyPending, Principal principal){
        return transactionDao.getTransactionsByUser(userDao.findIdByUsername(principal.getName()), onlyPending);
    }

    @RequestMapping(path = "/{transId}", method = RequestMethod.GET)
    public Transaction viewTransById(@PathVariable int transId, Principal principal){
        return transactionDao.getTransactionById(transId, userDao.findIdByUsername(principal.getName()));
    }

    @RequestMapping(path = "/pending-decision/{transId}/{decisionBool}", method = RequestMethod.GET)
    public void decidePendingTransaction(@PathVariable int transId, @PathVariable boolean decisionBool, Principal principal){
        int fromUserId = userDao.findIdByUsername(principal.getName());
        Transaction transaction = transactionDao.getTransactionById(transId, fromUserId);
        if (transaction == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No transactions found for that transaction ID for the authenticated user (decidePendingTransaction).");
        }

        // IF TRANSACTION ACCEPTED THEN TRY TO RUN IT. IF RUN PASSES THEN SET TO APPROVE. ELSE LEAVE PENDING. IF DENIED THEN SET STATUS TO DENIED.
        if (decisionBool){
            boolean transactionResult = runTransaction(fromUserId, transaction.getToUserId(), transaction.getAmount());
            if (!transactionResult){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transaction unsuccessful. Please contact support. (decidePendingTransaction)");
            } else {
                transactionDao.updateStatus(transId, "Approved", fromUserId);
            }
        } else {
            transactionDao.updateStatus(transId, "Rejected", fromUserId);
        }
    }

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

    private boolean runTransaction(int fromUserId, int toUserId, BigDecimal amount) {
        BigDecimal subtractedAmount = null;
        BigDecimal addedAmount = null;

        if(!checksController.enoughMoney(fromUserId, amount)){
            return false;
        }

        // reduce from user's amount by balance amount
        subtractedAmount = accountDao.subtractFromBalance(fromUserId, amount);
        // add to user's amount by balance amount
        addedAmount = accountDao.addToBalance(toUserId, amount);

        // IF ANY VALUES ARE NULL THEN THE LOGIC DID NOT WORK. REVERSE.
        if (subtractedAmount == null && addedAmount != null){
            accountDao.subtractFromBalance(toUserId ,addedAmount);
        } else if (subtractedAmount != null && addedAmount == null) {
            accountDao.addToBalance(fromUserId, subtractedAmount);
        } else if (subtractedAmount == null && addedAmount == null) {
            return false;
        }

        return true;
    }


}
