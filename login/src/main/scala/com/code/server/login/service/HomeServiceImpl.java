package com.code.server.login.service;

import com.code.server.constant.game.AgentBean;
import com.code.server.login.vo.HomeChargeVo;
import com.code.server.login.vo.HomePageVo;
import com.code.server.redis.service.RedisManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by dajuejinxian on 2018/5/8.
 */
@Service
public class HomeServiceImpl implements HomeService{

    @Autowired
    private TodayChargeService todayChargeService;

    @Override
    public HomePageVo showHomePage(long agentId) {
        AgentBean agentBean = RedisManager.getAgentRedisService().getAgentBean(agentId);
        HomePageVo homePageVo = new HomePageVo();
        homePageVo.setRebate("¥" + agentBean.getRebate());
        homePageVo.setInvitationCode("" + agentId);
        HomeChargeVo homeChargeVo = todayChargeService.showCharge(agentId);
        String total = homeChargeVo.getTotal();
        homePageVo.setTotalMoney(total);
        return homePageVo;
    }
}
