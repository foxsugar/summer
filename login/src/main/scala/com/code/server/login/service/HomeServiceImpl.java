package com.code.server.login.service;

import com.code.server.constant.game.AgentBean;
import com.code.server.db.dao.IChargeDao;
import com.code.server.db.dao.IGameAgentDao;
import com.code.server.db.dao.IUserDao;
import com.code.server.db.model.Charge;
import com.code.server.db.model.GameAgent;
import com.code.server.db.model.User;
import com.code.server.login.util.CookieUtil;
import com.code.server.login.vo.HomeChargeVo;
import com.code.server.login.vo.HomePageVo;
import com.code.server.redis.service.RedisManager;
import com.code.server.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scala.Char;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by dajuejinxian on 2018/5/8.
 */
@Service
public class HomeServiceImpl implements HomeService{

    @Autowired
    private TodayChargeService todayChargeService;

    @Override
    public HomePageVo showHomePage() {
        long agentId = 0;
        AgentBean agentBean = RedisManager.getAgentRedisService().getAgentBean(agentId);

        HomePageVo homePageVo = new HomePageVo();
        homePageVo.setRebate("¥" + agentBean.getRebate());
        homePageVo.setInvitationCode("" + agentId);

        HomeChargeVo homeChargeVo = todayChargeService.showCharge();
        String total = homeChargeVo.getTotal();
        homePageVo.setTotalMoney(total);

        return homePageVo;
    }
}
