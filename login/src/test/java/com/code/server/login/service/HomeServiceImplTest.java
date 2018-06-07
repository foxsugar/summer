package com.code.server.login.service;

import com.code.server.db.model.GameAgent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by dajuejinxian on 2018/5/14.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class HomeServiceImplTest {

    @Autowired
    private HomeService homeService;


    @Test
    public void save(){

        GameAgent gameAgent = new GameAgent();

//        gameAgent.setChildList(new HashSet<>());
        gameAgent.setParentId(99);
        gameAgent.setIsPartner(30);
        gameAgent.setPartnerId(49);
        gameAgent.setRebate(555);
        gameAgent.setQrTicket("www");
        gameAgent.setOpenId("70");
        gameAgent.setUnionId("88");
//        repository.save(gameAgent);

    }

    @Test
    public void findChargeInfoByOpenId() throws Exception {

//        Map map = homeService.findChargeInfoByOpenId("70");
//        System.out.println(map);
    }

}