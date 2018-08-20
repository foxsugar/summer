package com.code.server.db.dao;

import com.code.server.db.model.AgentUser;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by dajuejinxian on 2018/8/20.
 */
public class IAgentUserDaoTest {

    @Autowired
    private IAgentUserDao agentUserDao;

    @Test
    public void test(){

        System.out.println(agentUserDao);
        System.out.println(agentUserDao.findAll());
    }

    @Test
    public void findAgentUserByInvite_code() throws Exception {
    }

    @Test
    public void findAgentUserByUsernameAndPassword() throws Exception {
    }

    @Test
    public void findAgentUserByUsername() throws Exception {
    }

}