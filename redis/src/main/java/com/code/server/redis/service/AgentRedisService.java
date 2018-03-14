package com.code.server.redis.service;

import com.code.server.constant.game.AgentBean;
import com.code.server.redis.config.IConstant;
import com.code.server.redis.dao.IAgentRedis;
import com.code.server.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * Created by sunxianping on 2018/3/13.
 */
@Service
public class AgentRedisService implements IAgentRedis, IConstant {

    private static final String AGENTBEAN = "agentBean|";

    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    public double addRebate(long agentId, double rebate) {
        HashOperations<String, String, Double> agent_rebate = redisTemplate.opsForHash();
        // 把修改后的值放入userBean里
        double m = agent_rebate.increment(AGENT_REBATE, "" + agentId, rebate);
        AgentBean agentBean = getAgentBean(agentId);
        if (agentBean != null) {
            agentBean.setRebate(m);
            updateAgentBean(agentBean);
        }
        return m;
    }

    @Override
    public AgentBean getAgentBean(long agentId) {

        BoundHashOperations<String, String, String> agent_bean = redisTemplate.boundHashOps(AGENTBEAN);
        String json = agent_bean.get(String.valueOf(agentId));
        if (json != null) {
            return JsonUtil.readValue(json, AgentBean.class);
        }
        return null;
    }

    @Override
    public void setAgentBean(AgentBean agentBean) {
        BoundHashOperations<String, String, String> agent_bean = redisTemplate.boundHashOps(AGENTBEAN);
        agent_bean.put(String.valueOf(agentBean.getId()), JsonUtil.toJson(agentBean));
    }

    @Override
    public void updateAgentBean(AgentBean agentBean) {

        BoundHashOperations<String, String, String> agent_bean = redisTemplate.boundHashOps(AGENTBEAN);
        agent_bean.put(String.valueOf(agentBean.getId()), JsonUtil.toJson(agentBean));
        //加入保存列表
        addSaveAgent(agentBean.getId());
    }


    private void addSaveAgent(long agentId) {
        BoundSetOperations<String, String> save_agent = redisTemplate.boundSetOps(SAVE_AGENT);
        save_agent.add("" + agentId);
    }


    private String getAgentBeanKey(long agentId) {
        return String.valueOf(agentId);
    }

}
