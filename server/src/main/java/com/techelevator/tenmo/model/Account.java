package com.techelevator.tenmo.model;

import javax.validation.constraints.DecimalMin;
import java.math.BigDecimal;

public class Account {
    private int acctId;
    private int userId;
    @DecimalMin(value = "0.00", inclusive = true, message = "Cannot have a negative balance.")
    private BigDecimal balance;

    public Account() {
        this.balance = new BigDecimal("1000.00");
    }

    public Account(int acctId, int userId, BigDecimal balance) {
        this.acctId = acctId;
        this.userId = userId;
        this.balance = new BigDecimal("1000.00");
    }

    public int getAcctId() {
        return acctId;
    }

    public void setAcctId(int acctId) {
        this.acctId = acctId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "Account{" +
                "acctId=" + acctId +
                ", userId=" + userId +
                ", balance=" + balance +
                '}';
    }
}
