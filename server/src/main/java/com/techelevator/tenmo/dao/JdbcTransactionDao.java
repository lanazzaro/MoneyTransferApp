package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transaction;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
@Component
public class JdbcTransactionDao implements TransactionDao{

    private JdbcTemplate jdbcTemplate;

    public JdbcTransactionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean create(Transaction transaction) {
        String sql = "INSERT INTO transaction (from_user_id, to_user_id, amount, status, trans_date) VALUES (?, ?, ?, ?, ?) RETURNING transaction_id";
        Integer newTransId;
        try {
            newTransId = jdbcTemplate.queryForObject(sql, Integer.class, transaction.getFromUserId(), transaction.getToUserId(), transaction.getAmount(), transaction.getStatus(), transaction.getTransTimestamp());
        } catch (DataAccessException e) {
            return false;
        }
        return true;
    }

    @Override
    public Transaction getTransactionById(int transId) {
        String sql = "SELECT transaction_id ,from_user_id, to_user_id, amount, status, trans_date FROM transaction WHERE transaction_id = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, transId);
        if (rowSet.next()){
            return mapRowToTransactions(rowSet);
        }
        // TODO low priorty - add new custom exceptions
        throw new UsernameNotFoundException("Transaction for " + transId + " was not found.");
    }


    @Override
    public List<Transaction> getTransactionsByUser(int userId) {
        String sql = "SELECT transaction_id ,from_user_id, to_user_id, amount, status, trans_date FROM transaction WHERE user_id = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, userId);
        List<Transaction> outputList = new ArrayList<>();
        while (rowSet.next()){
            outputList.add(mapRowToTransactions(rowSet));
        }
        return outputList;
    }

    @Override
    public String getStatus(int transId) {
        String sql = "SELECT status FROM transaction WHERE transaction_id = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, transId);
        if (rowSet.next()){
            return rowSet.getString("status");
        }
        throw new UsernameNotFoundException("Status for " + transId + " was not found.");
    }

    @Override
    public boolean updateStatus(int transId, String status) {
        String sql = "UPDATE transaction SET status = ? WHERE transaction_id = ? RETURNING status";
        String returnedStatus = null;

        try {
            returnedStatus = jdbcTemplate.queryForObject(sql, String.class, status, transId);
        } catch (DataAccessException e) {
            return false;
        }
        return true;
    }

    private Transaction mapRowToTransactions(SqlRowSet rs) {
        Transaction transaction = new Transaction();
        transaction.setTransId(rs.getInt("transaction_id"));
        transaction.setFromUserId(rs.getInt("from_user_id"));
        transaction.setToUserId(rs.getInt("to_user_id"));
        transaction.setAmount(rs.getBigDecimal("amount"));
        transaction.setStatus(rs.getString("status"));
        transaction.setTransTimestamp(rs.getTimestamp("trans_date"));
        return transaction;
    }
}
