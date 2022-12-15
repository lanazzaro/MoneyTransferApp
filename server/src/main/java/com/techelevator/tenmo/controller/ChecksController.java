package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.dao.JdbcTransactionDao;
import com.techelevator.tenmo.dao.TransactionDao;
import com.techelevator.tenmo.model.Account;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Controller to verify that transfers can be done.
 */

@Component
public class ChecksController {
    private TransactionDao transactionDao;
    private AccountDao accountDao;
    private JdbcTemplate jdbcTemplate;


    public ChecksController(TransactionDao transactionDao, AccountDao accountDao) {
        this.transactionDao = transactionDao;
        this.accountDao = accountDao;
    }

    public boolean enoughMoney(int userId, BigDecimal transactionAmount){
        if (accountDao.getBalanceByUserId(userId).compareTo(transactionAmount) < 0){
            return false;
        }
        return true;
    }

    public boolean userIdsAreDifferent(int fromUser, int toUser){
        if (fromUser == toUser){
            return false;
        } return true;
    }


    /*
    checks status if approved -> then transfer $/change balance, if rejected no change
     */

    //TODO make transaction controller w/ set status to x depending on request/send
    //TODO make sure to include @Valid on balance and amount
}
