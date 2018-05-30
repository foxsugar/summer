package com.code.server.login.service;

import com.code.server.constant.game.AgentBean;
import com.code.server.db.dao.IUserDao;
import com.code.server.db.model.User;
import com.code.server.login.util.CookieUtil;
import com.code.server.login.vo.OneLevelInfoVo;
import com.code.server.login.vo.ThreeLevelInfoVo;
import com.code.server.login.vo.TwoLevelInfoVo;
import com.code.server.redis.service.RedisManager;
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

    @Override
    public List<OneLevelInfoVo> fetchSelfPlayerList() {

        long agentId = CookieUtil.getAgentIdByCookie();
        AgentBean agentBean = RedisManager.getAgentRedisService().getAgentBean(agentId);

        List<Long> aList = new ArrayList<>();
        for (long uid : agentBean.getChildList()){
            if (RedisManager.getAgentRedisService().isExit(uid))continue;
            aList.add(uid);
        }

        List<User> userList = this.userDao.findUsersByIdIn(aList);
        for (User user : userList){

        }

        return null;
    }

    @Override
    public List<TwoLevelInfoVo> fetchTwoLevelDelegateList() {
        return null;
    }

    @Override
    public List<ThreeLevelInfoVo> fetchThreeLevelDelegateList() {
        return null;
    }
}
