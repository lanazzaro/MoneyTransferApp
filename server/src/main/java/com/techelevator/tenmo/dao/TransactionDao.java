package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transaction;

import java.util.List;

public interface TransactionDao {

    int create(Transaction transaction);

    Transaction getTransactionById(int transId, int fromUserId);

    List<Transaction> getTransactionsByUser(int userId, boolean includePending);

    String getStatus(int transId);

    String updateStatus(int transId, String status, int fromUserId);
}
