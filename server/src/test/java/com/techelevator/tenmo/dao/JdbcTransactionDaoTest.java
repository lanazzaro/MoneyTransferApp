package com.techelevator.tenmo.dao;

import com.techelevator.dao.BaseDaoTests;
import com.techelevator.tenmo.model.Transaction;
import com.techelevator.tenmo.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.security.Timestamp;

import static org.junit.Assert.*;

public class JdbcTransactionDaoTest extends BaseDaoTests {
    private TransactionDao transDao;
    private JdbcUserDao userDao;
    private JdbcAccountDao accountDao;
    private User testUser;
    private User testUser2;

    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        userDao = new JdbcUserDao(jdbcTemplate);
        accountDao = new JdbcAccountDao(jdbcTemplate);
        transDao = new JdbcTransactionDao(jdbcTemplate);
        userDao.create("TEST_USER", "test_password");
        testUser = userDao.findByUsername("TEST_USER");
        userDao.create("TEST_USER_2", "test_password_2");
        testUser2 = userDao.findByUsername("TEST_USER_2");
    }

    @Test
    public void testUserCreation(){
        boolean testUser2 = userDao.create("TEST_USER_3", "test_password_3");
        Assert.assertTrue(testUser2);
        Assert.assertEquals(testUser.getId()+2, userDao.findIdByUsername("TEST_USER_3"));
    }

    @Test
    public void testTransactionCreated(){
        Transaction testTransaction = new Transaction();
        testTransaction.setAmount(new BigDecimal("50.00"));
        testTransaction.setStatus("Approved");
        testTransaction.setFromUserId(testUser.getId());
        testTransaction.setToUserId(testUser2.getId());

        boolean transResult = transDao.create(testTransaction);
        Assert.assertTrue(transResult);
    }

    @Test
    public void getTransactionById(){
        Transaction testTransaction = new Transaction();
        testTransaction.setAmount(new BigDecimal("50.00"));
        testTransaction.setStatus("Approved");
        testTransaction.setFromUserId(testUser.getId());
        testTransaction.setToUserId(testUser2.getId());

        boolean transResult = transDao.create(testTransaction);

        if(!transResult){
            Assert.fail("transaction was not created properly");
        }

        Transaction testResult = transDao.getTransactionById(3001, testUser.getId());

        Assert.assertEquals(testTransaction.getAmount(),testResult.getAmount());
        Assert.assertEquals(testTransaction.getStatus(), testResult.getStatus());
        Assert.assertEquals(testTransaction.getFromUserId(), testResult.getFromUserId());
        Assert.assertEquals(testTransaction.getToUserId(), testResult.getToUserId());

    }

    /*
    getTransactionById

    getTransactionsByUser

    getStatus

    updateStatus

     */

}
