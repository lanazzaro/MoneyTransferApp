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
import java.util.List;

import static org.junit.Assert.*;

public class JdbcTransactionDaoTest extends BaseDaoTests {
    private TransactionDao transDao;
    private JdbcUserDao userDao;
    private JdbcAccountDao accountDao;
    private User testUser;
    private User testUser2;
    private User bob;
    private User user;

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
        bob = userDao.findByUsername("bob");
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

        int transResult = transDao.create(testTransaction);
        Assert.assertNotEquals(0, transResult);
    }

    @Test
    public void getTransactionById(){
        Transaction testTransaction = new Transaction();
        testTransaction.setAmount(new BigDecimal("50.00"));
        testTransaction.setStatus("Approved");
        testTransaction.setFromUserId(testUser.getId());
        testTransaction.setToUserId(testUser2.getId());

        int transResult = transDao.create(testTransaction);

        if(transResult == 0){
            Assert.fail("transaction was not created properly");
        }

        Transaction testResult = transDao.getTransactionById(transResult, testUser.getId());

        Assert.assertEquals(testTransaction.getAmount(),testResult.getAmount());
        Assert.assertEquals(testTransaction.getStatus(), testResult.getStatus());
        Assert.assertEquals(testTransaction.getFromUserId(), testResult.getFromUserId());
        Assert.assertEquals(testTransaction.getToUserId(), testResult.getToUserId());

    }

    @Test
    public void getTransactionsByUser() {
        Transaction testTransaction = new Transaction();
        testTransaction.setAmount(new BigDecimal("50.00"));
        testTransaction.setStatus("Approved");
        testTransaction.setFromUserId(bob.getId());
        testTransaction.setToUserId(testUser2.getId());
        int transResult = transDao.create(testTransaction);

        Transaction testTransaction2 = new Transaction();
        testTransaction2.setAmount(new BigDecimal("100.00"));
        testTransaction2.setStatus("Approved");
        testTransaction2.setFromUserId(bob.getId());
        testTransaction2.setToUserId(testUser2.getId());
        int transResult2 = transDao.create(testTransaction2);

        List<Transaction> testResults = transDao.getTransactionsByUser(bob.getId(), false);

        // assert that only Bob's two transactions show up in the list
        Assert.assertEquals(2, testResults.size());

        // loop through testResults. if result does not belong to bob, fail
        for (Transaction trans: testResults) {
            if (trans.getFromUserId() != bob.getId()){
                Assert.fail("Transaction not belonging to from user found in results from getTransactionsByUser()");
            }
        }
    }

    @Test
    public void getStatusTest() {
        Transaction testTransaction = new Transaction();
        testTransaction.setAmount(new BigDecimal("50.00"));
        testTransaction.setStatus("Approved");
        testTransaction.setFromUserId(testUser.getId());
        testTransaction.setToUserId(testUser2.getId());

        int transResult = transDao.create(testTransaction);

        Assert.assertEquals("Approved", transDao.getStatus(transResult));
    }

    @Test
    public void updateStatusTest() {
        Transaction testTransaction = new Transaction();
        testTransaction.setAmount(new BigDecimal("50.00"));
        testTransaction.setStatus("Pending");
        testTransaction.setFromUserId(testUser.getId());
        testTransaction.setToUserId(testUser2.getId());

        int transResult = transDao.create(testTransaction);

        String updatedResult = transDao.updateStatus(transResult, "Denied", testUser.getId());

        Assert.assertEquals("Denied", updatedResult);
        Assert.assertEquals("Denied", transDao.getStatus(transResult));
    }
}
