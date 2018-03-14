package com.code.server.redis.dao;

import com.code.server.constant.game.AgentBean;

/**
 * Created by sunxianping on 2018/3/13.
 */
public interface IAgentRedis {

//    String getParentId(long agentId);


    double addRebate(long agentId, double rebate);


    AgentBean getAgentBean(long agentId);

    void setAgentBean(AgentBean agentBean);


    void updateAgentBean(AgentBean agentBean);

//    boolean isAgent(long userId);



}
