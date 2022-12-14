package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transaction;

import java.util.List;

public interface TransactionDao {

    boolean create(Transaction transaction);

    Transaction getTransactionById(int transId);

    List<Transaction> getTransactionsByUser(int userId);

    String getStatus(int transId);

    boolean updateStatus(int transId, String status);
}
