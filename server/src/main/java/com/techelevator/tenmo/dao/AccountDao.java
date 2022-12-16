package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;

import java.math.BigDecimal;

public interface AccountDao {

    BigDecimal addToBalance(int accountId, BigDecimal amount);

    BigDecimal subtractFromBalance(int accountId, BigDecimal amount);

    BigDecimal getBalanceByUserId(int userId);

}
