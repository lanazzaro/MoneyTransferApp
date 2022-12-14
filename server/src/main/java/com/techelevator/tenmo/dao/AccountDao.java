package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;

import java.math.BigDecimal;

public interface AccountDao {
    boolean create(int userId, BigDecimal balance);

    BigDecimal addToBalance(int accountId, BigDecimal amount);

    BigDecimal subtractFromBalance(int accountId, BigDecimal amount);

    Account findAccountByUserId(int userId);

    BigDecimal getBalanceByAccountId(int acctId);
}