package com.code.server.login.service;

import com.code.server.constant.game.AgentBean;
import com.code.server.redis.service.AgentRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by sunxianping on 2018/3/14.
 */
@Service
public class AgentService {

    @Autowired
    private AgentRedisService agentRedisService;

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


}
