package com.code.server.login.service;

import com.code.server.constant.game.AgentBean;
import com.code.server.db.Service.GameAgentService;
import com.code.server.db.Service.UserService;
import com.code.server.db.model.GameAgent;
import com.code.server.redis.service.AgentRedisService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by sunxianping on 2018/3/14.
 */
@Service
public class AgentService {

    @Autowired
    private AgentRedisService agentRedisService;

    @Autowired
    private UserService userService;

    @Autowired
    private GameAgentService gameAgentService;

    public void userUp2Agent(long userId){

        AgentBean agentBean = agentRedisService.getAgentBean(userId);
        //不是代理
        if(agentBean == null){

        }


    }

    public void user2Partner(long userId){

    }


    public void changeAgent(long ownId, long newAgentId){

    }


    public void loadAllAgent2Redis() {
        //如果redis里没有数据 则load
        userService.getUserDao().count();
        if (agentRedisService.getAgentNum() != 0) {
            gameAgentService.getGameAgentDao().findAll().forEach(gameAgent -> {
                agentRedisService.setAgentBean(gameAgent2AgnetBean(gameAgent));
            });
        }
    }

    private AgentBean gameAgent2AgnetBean(GameAgent gameAgent) {
        AgentBean agentBean = new AgentBean();
        BeanUtils.copyProperties(gameAgent, agentBean);
        return agentBean;
    }

}
