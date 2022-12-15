package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
@Component
public class JdbcAccountDao implements AccountDao{

    private JdbcTemplate jdbcTemplate;

    public JdbcAccountDao(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public BigDecimal addToBalance(int userId, BigDecimal amount) {
        BigDecimal currentBalance = this.getBalanceByUserId(userId);
        BigDecimal newBalance = currentBalance.add(amount);
        String sql = "UPDATE account SET balance = ? WHERE user_id = ? RETURNING balance";
        BigDecimal returningBalance = null;

        try {
            returningBalance = jdbcTemplate.queryForObject(sql, BigDecimal.class, newBalance, userId);
        } catch (DataAccessException e) {
            return null;
        }
        return returningBalance;
    }

    @Override
    public BigDecimal subtractFromBalance(int userId, BigDecimal amount) {
        BigDecimal currentBalance = this.getBalanceByUserId(userId);
        BigDecimal newBalance = currentBalance.subtract(amount);
        String sql = "UPDATE account SET balance = ? WHERE user_id = ? RETURNING balance";
        BigDecimal returningBalance = null;

        // TODO check for new balance after transaction > 0.00
        try {
            returningBalance = jdbcTemplate.queryForObject(sql, BigDecimal.class, newBalance, userId);
        } catch (DataAccessException e) {
            return null;
        }
        return returningBalance;
    }

    @Override
    public BigDecimal getBalanceByUserId(int userId) {
        String sql = "SELECT balance FROM account WHERE user_id = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, userId);
        if (rowSet.next()){
            return rowSet.getBigDecimal("balance");
        }
        throw new UsernameNotFoundException("Balance for User ID:" + userId + " was not found. (getBalanceByUserId)");
    }

//    @Override
//    public BigDecimal getBalanceByAccountId(int acctId) {
//        String sql = "SELECT balance FROM account WHERE account_id = ?";
//        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, acctId);
//        if (rowSet.next()){
//            return rowSet.getBigDecimal("balance");
//        }
//        throw new UsernameNotFoundException("Balance for " + acctId + " was not found.(getBalanceByAccountId)");
//    }

    private Account mapRowToAccount(SqlRowSet rs){
        Account account = new Account();
        account.setAcctId(rs.getInt("account_id"));
        account.setUserId(rs.getInt("user_id"));
        account.setBalance(rs.getBigDecimal("balance"));
        return account;
    }
}
