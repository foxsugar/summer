package com.code.server.db.dao;

import com.code.server.db.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * Created by dajuejinxian on 2018/6/8.
 */

@SpringBootTest
@RunWith(SpringRunner.class)
public class IUserDaoTest {

    @Autowired
    private IUserDao userDao;
    @Test
    public void test() {

        User user  = userDao.findOne(100006l);

        System.out.println(user);
    }
}