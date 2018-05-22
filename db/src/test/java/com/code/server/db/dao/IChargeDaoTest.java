package com.code.server.db.dao;

import com.code.server.db.model.Charge;
import org.assertj.core.util.DateUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by dajuejinxian on 2018/5/15.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class IChargeDaoTest {

    @Autowired
    private IChargeDao chargeDao;

    @Test
    public void getChargesByUserrAndRechargeSourceAndDate() throws Exception {

        List<Charge> list = chargeDao.getChargesByUserrAndRechargeSourceAndDate(new Long(1), "11", new Date(), new Date());

        System.out.println(list);
    }



    @Test
    public void getChargeByOrderId() throws Exception {

    }

    @Test
    public void getChargesByUseridInAndCreatetimeBetween() throws Exception {


    }

    @Test
    public void getSumMoneyByUsersAndDate() throws Exception {
    }


}