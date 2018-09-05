package com.code.server.db.dao;

import com.code.server.constant.db.AgentInfo;
import com.code.server.constant.db.AgentInfoRecord;
import com.code.server.constant.db.ChildCost;
import com.code.server.db.model.AgentUser;
import com.code.server.db.model.Charge;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit4.SpringRunner;
import scala.Char;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by dajuejinxian on 2018/6/19.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class IChargeDaoTest {

    @Autowired
    private IChargeDao chargeDao;

    @Autowired
    private IAgentUserDao agentUserDao;

    @Test
    public void test(){

        List<AgentUser> list = (List<AgentUser>) agentUserDao.findAll();

        for (AgentUser agentUser : list){
            AgentInfo agentInfo = new AgentInfo();
            AgentInfoRecord agentInfoRecord = new AgentInfoRecord();
            agentUser.setAgentInfo(agentInfo);
            agentUser.setAgentInfoRecord(agentInfoRecord);
            agentUserDao.save(agentUser);

        }
    }

    @Test
    public void findOne(){

//        AgentUser agentUser = agentUserDao.findOne(27);
//        AgentInfo agentInfo = agentUser.getAgentInfo();
//        System.out.println(agentInfo);

        AgentUser agentUser = agentUserDao.findOne(10334);
        System.out.println(agentUser);
    }

    @Test
    public void getChargeByOrderId() throws Exception {

//        List<Charge> list = chargeDao.getChargesByUseridInAndCreatetimeBetweenAndStatusIsAndChargeTypeIn(Arrays.asList(0l),new Date(), new Date(),1,Arrays.asList(1));

    }

//    @Test
//    public void getChargesByUseridInAndCreatetimeBetweenAndStatusIsAndChargeTypeIn() throws Exception {
//    }
//
//    @Test
//    public void getChargesByUseridInAndCreatetimeBetweenAndStatusIsAndRecharge_sourceIs() throws Exception {
//    }
//
//    @Test
//    public void getSumMoneyByUsersAndDate() throws Exception {
//    }
//
//    @Test
//    public void getChargesByUseridAndRechargeSourceAndDate() throws Exception {
//    }

}