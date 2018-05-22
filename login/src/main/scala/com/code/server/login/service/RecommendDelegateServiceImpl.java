package com.code.server.login.service;

import com.code.server.constant.game.AgentBean;
import com.code.server.db.dao.IUserDao;
import com.code.server.db.model.User;
import com.code.server.login.util.CookieUtil;
import com.code.server.login.vo.RecommandUserVo;
import com.code.server.redis.service.RedisManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dajuejinxian on 2018/5/16.
 */

@Service
public class RecommendDelegateServiceImpl implements RecommendDelegateService {

    @Autowired
    private IUserDao userDao;

    @Override
    public List<String> authorizationGameList() {

        long agentId = CookieUtil.getAgentIdByCookie();
        AgentBean agentBean = RedisManager.getAgentRedisService().getAgentBean(agentId);
        String gameItem = "game name";

        List<String> list = new ArrayList<>();
        if (agentBean == null){
            list.add(gameItem);
        }

        return list;
    }

    @Override
    public RecommandUserVo findRecommandUser(long userId) {
        //推荐代理
        User user = userDao.findOne(userId);

        RecommandUserVo recommandUserVo = new RecommandUserVo();

        if (user == null){
            recommandUserVo.setUsername("用户不存在");
            recommandUserVo.setUserId(userId);
            recommandUserVo.setImage(user.getImage());
        }else {
            recommandUserVo.setImage(user.getImage());
            recommandUserVo.setUserId(userId);
            recommandUserVo.setUsername(user.getUsername());
        }

        return recommandUserVo;
    }


}
