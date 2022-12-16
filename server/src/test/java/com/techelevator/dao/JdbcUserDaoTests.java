package com.techelevator.dao;


import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.math.BigDecimal;

public class JdbcUserDaoTests extends BaseDaoTests{

    private JdbcUserDao sut;
    private JdbcAccountDao accountDao;

    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        sut = new JdbcUserDao(jdbcTemplate);
        accountDao = new JdbcAccountDao(jdbcTemplate);
    }

    @Test
    public void createNewUser() {
        boolean userCreated = sut.create("TEST_USER","test_password");
        Assert.assertTrue(userCreated);
        User user = sut.findByUsername("TEST_USER");
        Assert.assertEquals("TEST_USER", user.getUsername());
    }

    @Test
    public void userAccountHasDefaultBalance(){
        BigDecimal testBalance = null;
        boolean userCreated = sut.create("TEST_USER","test_password");
        int userId = sut.findIdByUsername("TEST_USER");
        testBalance = accountDao.getBalanceByUserId(userId);
        Assert.assertEquals(testBalance, new BigDecimal("1000.00"));
    }

    @Test
    public void testIdByUsername(){
        boolean userCreated = sut.create("TEST_USER","test_password");
        int userId = sut.findIdByUsername("TEST_USER");
        User user = sut.findByUsername("TEST_USER");
        Assert.assertEquals(user.getId(),userId);
    }
}
