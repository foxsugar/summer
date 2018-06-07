package com.code.server.login.service;

import com.code.server.constant.game.AgentBean;
import com.code.server.db.dao.IUserDao;
import com.code.server.db.model.User;
import com.code.server.login.action.HomeAction;
import com.code.server.login.vo.OneLevelInfoVo;
import com.code.server.login.vo.ThreeLevelInfoVo;
import com.code.server.login.vo.TwoLevelInfoVo;
import com.code.server.redis.service.RedisManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dajuejinxian on 2018/5/30.
 */
@Service
public class DelegateRelataionServiceImpl implements DelegateRelataionService {

    @Autowired
    private IUserDao userDao;

    protected static final Logger logger = LoggerFactory.getLogger(DelegateRelataionServiceImpl.class);

    @Override
    public List<OneLevelInfoVo> fetchSelfPlayerList(long agentId) {

        AgentBean agentBean = RedisManager.getAgentRedisService().getAgentBean(agentId);
        List<Long> aList = new ArrayList<>();
        for (long uid : agentBean.getChildList()){
            if (RedisManager.getAgentRedisService().isExit(uid))continue;
            aList.add(uid);
        }

        List<OneLevelInfoVo> result = new ArrayList<>();
        List<User> userList = this.userDao.findUsersByIdIn(aList);
        for (User user : userList){
            OneLevelInfoVo oneLevelInfoVo = new OneLevelInfoVo();
            oneLevelInfoVo.setImage(user.getImage());
            oneLevelInfoVo.setUsername(user.getUsername());
            oneLevelInfoVo.setUid(user.getId() );
            result.add(oneLevelInfoVo);
        }

        return result;
    }

    @Override
    public List<TwoLevelInfoVo> fetchTwoLevelDelegateList(long agentId) {

        AgentBean agentBean = RedisManager.getAgentRedisService().getAgentBean(agentId);

        logger.info("fetchTwoLevelDelegateList:agentBean{}",agentBean);

        List<Long> aList = new ArrayList<>();
        for (long uid : agentBean.getChildList()){
            if (RedisManager.getAgentRedisService().isExit(uid)){
                aList.add(uid);
            }
        }



        List<TwoLevelInfoVo> result = new ArrayList<>();
        List<User> userList = this.userDao.findUsersByIdIn(aList);
        for (User user : userList){
            TwoLevelInfoVo twoLevelInfoVo = new TwoLevelInfoVo();
            twoLevelInfoVo.setImage(user.getImage());
            twoLevelInfoVo.setUsername(user.getUsername());
            twoLevelInfoVo.setUid(user.getId() );
            result.add(twoLevelInfoVo);
        }

        return result;
    }

    @Override
    public List<ThreeLevelInfoVo> fetchThreeLevelDelegateList(long agentId) {

        AgentBean agentBean = RedisManager.getAgentRedisService().getAgentBean(agentId);
        List<Long> aList = new ArrayList<>();
        for (long uid : agentBean.getChildList()){
            if (RedisManager.getAgentRedisService().isExit(uid)){
                aList.add(uid);
            }
        }

        List<Long> bList = new ArrayList<>();
        for (Long id : aList){
            AgentBean bean = RedisManager.getAgentRedisService().getAgentBean(id);
            for (Long uid : bean.getChildList()){
                if (RedisManager.getAgentRedisService().isExit(uid)){
                    bList.add(uid);
                }
            }
        }

        List<User> cList = userDao.findUsersByIdIn(bList);
        List<ThreeLevelInfoVo> resultList = new ArrayList<>();
        for (User user : cList){
            ThreeLevelInfoVo threeLevelInfoVo = new ThreeLevelInfoVo();
            threeLevelInfoVo.setImage(user.getImage());
            threeLevelInfoVo.setUsername(user.getUsername());
            threeLevelInfoVo.setUid(user.getId());
            resultList.add(threeLevelInfoVo);
        }
        return resultList;
    }
}
