package com.techelevator.tenmo.dao;

import com.techelevator.dao.BaseDaoTests;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import javax.sql.DataSource;
import java.math.BigDecimal;

import static org.junit.Assert.*;

public class JdbcAccountDaoTest extends BaseDaoTests {
    private JdbcAccountDao accountDao;
    private JdbcUserDao userDao;

    @Before
    public void setup(){
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        accountDao = new JdbcAccountDao(jdbcTemplate);
        userDao = new JdbcUserDao(jdbcTemplate);
        userDao.create("TEST_USER", "test_password");
    }


    @Test
    public void testGetBalanceByUserId(){
        userDao.create("TEST_USER_2", "test_password");
        User user = userDao.findByUsername("TEST_USER_2");
        Assert.assertEquals(new BigDecimal("1000.00"), accountDao.getBalanceByUserId(user.getId()));
    }

    @Test
    public void addToBalanceTest(){
        int userId = userDao.findIdByUsername("TEST_USER");
        BigDecimal initialBalance = accountDao.getBalanceByUserId(userId);
        BigDecimal amountToAdd = new BigDecimal("50.00");
        accountDao.addToBalance(userId, amountToAdd);

        Assert.assertEquals(initialBalance.add(amountToAdd), accountDao.getBalanceByUserId(userId));
    }

    @Test
    public void addNegativeBalanceTest(){
        int userId = userDao.findIdByUsername("TEST_USER");
        BigDecimal amountToAdd = new BigDecimal("-50.00");
        BigDecimal testResult = accountDao.addToBalance(userId, amountToAdd);

        Assert.assertNull(testResult);
    }

    @Test
    public void addZeroBalanceTest(){
        int userId = userDao.findIdByUsername("TEST_USER");
        BigDecimal amountToAdd = BigDecimal.ZERO;
        BigDecimal testResult = accountDao.addToBalance(userId, amountToAdd);

        Assert.assertNull(testResult);
    }

    @Test
    public void subtractFromBalanceTest(){
        int userId = userDao.findIdByUsername("TEST_USER");
        BigDecimal initialBalance = accountDao.getBalanceByUserId(userId);
        BigDecimal amountToSubtract = new BigDecimal("50.00");
        accountDao.subtractFromBalance(userId, amountToSubtract);

        Assert.assertEquals(initialBalance.subtract(amountToSubtract), accountDao.getBalanceByUserId(userId));
    }

    @Test
    public void subtractNegativeBalanceTest(){
        int userId = userDao.findIdByUsername("TEST_USER");
        BigDecimal amountToSubtract = new BigDecimal("-50.00");
        BigDecimal testResult = accountDao.subtractFromBalance(userId, amountToSubtract);

        Assert.assertNull(testResult);
    }

    @Test
    public void subtractZeroBalanceTest(){
        int userId = userDao.findIdByUsername("TEST_USER");
        BigDecimal amountToSubtract = BigDecimal.ZERO;
        BigDecimal testResult = accountDao.subtractFromBalance(userId, amountToSubtract);

        Assert.assertNull(testResult);
    }

}
