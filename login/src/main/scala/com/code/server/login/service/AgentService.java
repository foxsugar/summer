package com.code.server.login.service;

import com.code.server.constant.game.AgentBean;
import com.code.server.constant.game.UserBean;
import com.code.server.db.Service.GameAgentService;
import com.code.server.db.Service.GameAgentWxService;
import com.code.server.db.Service.RecommendService;
import com.code.server.db.Service.UserService;
import com.code.server.db.model.GameAgent;
import com.code.server.db.model.User;
import com.code.server.redis.service.AgentRedisService;
import com.code.server.redis.service.RedisManager;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
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

    @Autowired
    private RecommendService recommendService;

    @Autowired
    private GameAgentWxService gameAgentWxService;



    public void user2Partner(long userId){

    }





    public void loadAllAgent2Redis() {
        //如果redis里没有数据 则load
//        userService.getUserDao().count();
        if (agentRedisService.getAgentNum() == 0) {
            gameAgentService.getGameAgentDao().findAll().forEach(gameAgent -> {
                agentRedisService.setAgentBean(gameAgent2AgentBean(gameAgent));
            });
        }
    }


    /**
     * 改变为合伙人
     * @param userId
     */
    public void change2Partner(long userId){
        AgentBean agentBean = RedisManager.getAgentRedisService().getAgentBean(userId);
        String unionId = userService.getUserDao().getOpenIdById(userId);
        //如果不是代理,先成为代理
        if (agentBean == null) {
            agentBean = player2Agnet(userId,unionId);
        }

        //代理成为合伙人
        //已经是合伙人 返回
        if(agentBean.getIsPartner() == 1) return;

        //如果有上级代理
        long parentId = agentBean.getParentId();
        if (parentId != 0) {
            AgentBean parentAgentBean = RedisManager.getAgentRedisService().getAgentBean(parentId);
            //删掉这个下级代理
            parentAgentBean.getChildList().remove(userId);
            RedisManager.getAgentRedisService().updateAgentBean(parentAgentBean);
        }

        //自己变成合伙人
        agentBean.setIsPartner(1);
        //合伙人id 改成自己
        agentBean.setPartnerId(userId);

        //上级变为0
        agentBean.setParentId(0);

        RedisManager.getAgentRedisService().updateAgentBean(agentBean);

        //user 上的改变 把上级去掉
        changePlayerReferee(userId, 0);

        //自己下级的合伙人 全部改成自己
        Set<AgentBean> allChild = new HashSet<>();
        findAllClildAgent(agentBean, allChild);
        for (AgentBean subAgentBean : allChild) {
            subAgentBean.setPartnerId(userId);
            //加入保存列表
            RedisManager.getAgentRedisService().updateAgentBean(subAgentBean);
        }


    }

    /**
     * 玩家成为代理
     * @param userId
     * @param unionId
     */
    private AgentBean player2Agnet(long userId,String unionId){
        GameAgent gameAgent = new GameAgent();
        gameAgent.setId(userId);
        gameAgent.setUnionId(unionId);
        gameAgent.setIsPartner(0);

        AgentBean agentBean = gameAgent2AgentBean(gameAgent);
        //保存到redis
        agentRedisService.setAgent2Redis(agentBean);
        //保存到数据库
        gameAgentService.getGameAgentDao().save(gameAgent);

        return agentBean;
    }

    /**
     * 变成代理
     * @param userId
     */
    public void change2Agent(long userId){
        AgentBean agentBean = RedisManager.getAgentRedisService().getAgentBean(userId);
        String unionId = userService.getUserDao().getOpenIdById(userId);
        //玩家
        if (agentBean == null) {
            player2Agnet(userId, unionId);

        }else{//代理

            //如果是合伙人的处理  合伙人降级
            if (agentBean.getIsPartner() == 1) {

                agentBean.setIsPartner(0);
                agentBean.setPartnerId(0);
                RedisManager.getAgentRedisService().updateAgentBean(agentBean);
                //合伙人下面所有代理的合伙人 全变成0
                Set<AgentBean> subAgents = new HashSet<>();
                findAllClildAgent(agentBean, subAgents);
                for (AgentBean subAgent : subAgents) {
                    subAgent.setPartnerId(0);
                    //加到保存列表
                    RedisManager.getAgentRedisService().updateAgentBean(subAgent);
                }

            }
        }
    }


    /**
     * 变成玩家
     * @param userId
     */
    public void change2Player(long userId){
        AgentBean agentBean = RedisManager.getAgentRedisService().getAgentBean(userId);
        //玩家
        if (agentBean == null) {

            //玩家转换成玩家 没意思
            //do nothing


        }else{//代理

            //如果是合伙人的处理
            if (agentBean.getIsPartner() == 1) {
                //合伙人下面所有代理的合伙人 全变成0
                Set<AgentBean> subAgents = new HashSet<>();
                findAllClildAgent(agentBean, subAgents);
                for (AgentBean subAgent : subAgents) {
                    //合伙人id变为0
                    subAgent.setPartnerId(0);
                    //合伙人下面一层代理的上级变为0
                    if (subAgent.getParentId() == userId) {
                        changePlayerReferee(subAgent.getParentId(),0);
                        subAgent.setParentId(0);
                    }
                    //加到保存列表
                    RedisManager.getAgentRedisService().updateAgentBean(subAgent);
                }

            }else{//普通代理
                //所有下级的上级 变成 这个代理的上级
                Set<AgentBean> subAgents = new HashSet<>();
                findAllClildAgent(agentBean, subAgents);
                //上级代理id
                long parentId = agentBean.getParentId();
                AgentBean parentAgentBean = RedisManager.getAgentRedisService().getAgentBean(parentId);

                //改变一级下级的上级代理
                for (AgentBean subAgent : subAgents) {

                    //合伙人下面一层代理的上级变为个代理的上级
                    if (subAgent.getParentId() == userId) {
                        subAgent.setParentId(parentId);
                        changePlayerReferee(subAgent.getParentId(),(int)parentId);
                        if (parentAgentBean != null) {
                            parentAgentBean.getChildList().add(subAgent.getId());
                        }
                        //保存
                        RedisManager.getAgentRedisService().updateAgentBean(subAgent);
                    }
                }

                //合伙人不会变

                //从列表中删除
                if (parentAgentBean != null) {
                    parentAgentBean.getChildList().remove(userId);
                    RedisManager.getAgentRedisService().updateAgentBean(parentAgentBean);
                }

            }

            //删除代理
            RedisManager.getAgentRedisService().removeAgentBean(""+userId);
            //删除数据库
            gameAgentService.getGameAgentDao().delete(userId);
        }

    }

    /**
     * 改变player的上级
     * @param userId
     * @param parentId
     */
    private void changePlayerReferee(long userId, int parentId){
        //user 上的改变 把上级去掉
        UserBean userBean = RedisManager.getUserRedisService().getUserBean(userId);
        if (userBean != null) {
            userBean.setReferee(parentId);
            RedisManager.getUserRedisService().updateUserBean(userBean.getId(),userBean);
        }else {
            User user = userService.getUserByUserId(userId);
            if (user != null) {
                user.setReferee(parentId);
                userService.save(user);
            }
        }
    }

    /**
     * 换代理
     * @param userId
     * @param newAgentId
     */
    public void changeAgent(long userId,long newAgentId){
        //自己换自己 推出
        if(userId == newAgentId) return;
        AgentBean agentBean = RedisManager.getAgentRedisService().getAgentBean(userId);
//        String unionId = userService.getUserDao().getOpenIdById(userId);
        //玩家
        if (agentBean == null) {

//            changePlayerReferee(userId, (int)newAgentId);

        }else{//代理

            //如果是合伙人的处理
            if (agentBean.getIsPartner() == 1) {
                // do nothing
                //合伙人 换不了代理
            }else{

                AgentBean newAgentBean = RedisManager.getAgentRedisService().getAgentBean(newAgentId);
                //原来的上级代理
                AgentBean parentBean = RedisManager.getAgentRedisService().getAgentBean(agentBean.getParentId());
                long newPartnerId = 0;
                if (newAgentBean != null) {
                    //自己的合伙人 和 新上级的合伙人是否是同一人
                    newPartnerId = newAgentBean.getPartnerId();
                    newAgentBean.getChildList().add(userId);
                    RedisManager.getAgentRedisService().updateAgentBean(newAgentBean);
                }

                //如果合伙人id换了
                if(agentBean.getPartnerId() != newPartnerId){
                    Set<AgentBean> subAgents = new HashSet<>();
                    findAllClildAgent(agentBean, subAgents);
                    for (AgentBean subAgent : subAgents) {
                        //新的合伙人
                        subAgent.setPartnerId(newPartnerId);
                        RedisManager.getAgentRedisService().updateAgentBean(subAgent);

                    }
                    //自己换合伙人id
                    agentBean.setPartnerId(newPartnerId);
                }

                //自己换上级
                agentBean.setParentId(newAgentId);
                //原来的上级代理 删掉一个下级
                if (parentBean != null) {
                    parentBean.getChildList().remove(userId);
                    RedisManager.getAgentRedisService().updateAgentBean(parentBean);
                }
                //新上级 加一个代理
                if (newAgentBean != null) {
                    newAgentBean.getChildList().add(userId);
                    RedisManager.getAgentRedisService().updateAgentBean(newAgentBean);
                }

                //保存自己
                RedisManager.getAgentRedisService().updateAgentBean(agentBean);
            }
        }

        //换player的上级
        changePlayerReferee(userId, (int)newAgentId);

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
        Set<Long> ids = agentBean.getChildList();
        if (ids.size() > 0) {
            ids.forEach(id->{
                AgentBean child = agentRedisService.getAgentBean(id);
                if (child != null) {
                    agents.add(child);
                    if (child.getChildList().size() > 0) {
                        findAllClildAgent(child, agents);
                    }
                }

            });
        }
    }

}
