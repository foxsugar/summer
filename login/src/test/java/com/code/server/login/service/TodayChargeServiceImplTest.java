package com.code.server.login.service;

import com.code.server.login.vo.OneLevelVo;
import com.code.server.login.vo.ThreeLevelVo;
import com.code.server.login.vo.TwoLevelVo;
import com.code.server.util.DateUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * Created by dajuejinxian on 2018/8/27.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TodayChargeServiceImplTest {

    @Autowired
    TodayChargeServiceImpl todayChargeService;

    @Test
    public void oneLevelChargesNew() throws Exception {

       OneLevelVo oneLevelVo = todayChargeService.oneLevelChargesNew(DateUtil.convertDay2Date("2017-01-01"), new Date(), 10);

       TwoLevelVo twoLevelVo = todayChargeService.twoLevelChargesNew(DateUtil.convertDay2Date("2017-01-01"), new Date(), 10);

       ThreeLevelVo threeLevelVo = todayChargeService.threeLevelChargesNew(DateUtil.convertDay2Date("2017-01-01"), new Date(),10);

        System.out.println(oneLevelVo);
        System.out.println(twoLevelVo);
        System.out.println(threeLevelVo);

        System.out.println("============");
    }

}