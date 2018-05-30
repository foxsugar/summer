package com.code.server.login.service;

import com.code.server.constant.game.AgentBean;
import com.code.server.db.dao.IChargeDao;
import com.code.server.db.dao.IGameAgentDao;
import com.code.server.db.dao.IUserDao;
import com.code.server.db.model.Charge;
import com.code.server.db.model.GameAgent;
import com.code.server.db.model.User;
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
    private IChargeDao chargeDao;

    @Override
    public Map<Object, Object> findChargeInfoByAgentId(long agentId) {

        AgentBean agentBean = RedisManager.getAgentRedisService().getAgentBean(agentId);
        Date begin = DateUtil.getDayBegin();
        Date end = new Date();
        //查询今日充值
        List<Charge> list = chargeDao.getChargesByUseridInAndCreatetimeBetween(Arrays.asList(agentBean.getId()), begin, end);

        double total = 0;

        for (int i = 0; i < list.size(); i++){
            Charge charge = list.get(i);
            total += charge.getMoney();
        }

        Map<Object, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("inviteCode", agentBean.getId());
        result.put("rebate", agentBean.getRebate());
        return result;
    }
}
