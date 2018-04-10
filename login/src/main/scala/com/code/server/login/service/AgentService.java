package com.code.server.login.service;

import com.code.server.constant.game.AgentBean;
import com.code.server.db.Service.GameAgentService;
import com.code.server.db.Service.UserService;
import com.code.server.db.model.GameAgent;
import com.code.server.redis.service.AgentRedisService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

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



    public void user2Partner(long userId){

    }


    public void changeAgent(long ownId, long newAgentId){

    }


    public void loadAllAgent2Redis() {
        //如果redis里没有数据 则load
        userService.getUserDao().count();
        if (agentRedisService.getAgentNum() != 0) {
            gameAgentService.getGameAgentDao().findAll().forEach(gameAgent -> {
                agentRedisService.setAgentBean(gameAgent2AgentBean(gameAgent));
            });
        }
    }


    public static GameAgent agentBean2GameAgent(AgentBean agentBean) {
        GameAgent gameAgent = new GameAgent();
        BeanUtils.copyProperties(agentBean, gameAgent);
        return gameAgent;
    }

    public static AgentBean gameAgent2AgentBean(GameAgent gameAgent) {
        AgentBean agentBean = new AgentBean();
        BeanUtils.copyProperties(gameAgent, agentBean);
        return agentBean;
    }

    /**
     * 找到所有下级代理
     * @param agentBean
     * @param agents
     */
    public void findAllClildAgent(AgentBean agentBean,Set<AgentBean> agents){
        List<Long> ids = agentBean.getChildList();
        if (ids.size() > 0) {
            ids.forEach(id->{
                AgentBean clild = agentRedisService.getAgentBean(id);
                agents.add(clild);
                if (clild.getChildList().size() > 0) {
                    findAllClildAgent(clild, agents);
                }
            });
        }
    }

}
