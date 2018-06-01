package com.code.server.login.service;

import com.code.server.constant.game.AgentBean;
import com.code.server.constant.game.UserBean;
import com.code.server.db.Service.GameAgentService;
import com.code.server.db.Service.GameAgentWxService;
import com.code.server.db.Service.RecommendService;
import com.code.server.db.Service.UserService;
import com.code.server.db.dao.IUserDao;
import com.code.server.db.model.GameAgent;
import com.code.server.db.model.GameAgentWx;
import com.code.server.db.model.Recommend;
import com.code.server.db.model.User;
import com.code.server.login.vo.RecommandUserVo;
import com.code.server.redis.service.RedisManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by dajuejinxian on 2018/5/16.
 */

@Service
public class RecommendDelegateServiceImpl implements RecommendDelegateService {

    @Autowired
    private IUserDao userDao;

    @Autowired
    private GameAgentWxService gameAgentWxService;

    @Autowired
    private UserService userService;

    @Autowired
    private RecommendService recommendService;

    @Autowired
    private GameAgentService gameAgentService;

    @Override
    public RecommandUserVo findRecommandUser(long userId, long agentId) {

        UserBean userBean = RedisManager.getUserRedisService().getUserBean(userId);

        RecommandUserVo recommandUserVo = new RecommandUserVo();

        if (userBean != null) {
            recommandUserVo.setUsername("用户不存在");
            recommandUserVo.setUserId(new Long(0));
            //空头像
            recommandUserVo.setImage("");
        } else {

            //推荐代理
            User user = userDao.findOne(userId);
            recommandUserVo.setImage(user.getImage());
            recommandUserVo.setUserId(userId);
            recommandUserVo.setUsername(user.getUsername());
        }

        return recommandUserVo;
    }

    @Override
    public boolean bindDelegate(long userId, long agentId) {


        AgentBean agentBean = RedisManager.getAgentRedisService().getAgentBean(userId);

        //todo 之前是代理
        if (agentBean != null) {
            return false;
        }


        //之前不是代理

        long refereeId = 0;
        UserBean userBean = RedisManager.getUserRedisService().getUserBean(userId);
        if (userBean != null) {
            refereeId = userBean.getReferee();
        } else {
            User user = userService.getUserByUserId(userId);
            if (user != null) {
                refereeId = user.getReferee();
            }
        }
        //有上级代理 或代理不是agent
        if (!(refereeId == 0 || refereeId == agentId)) {
            return false;
        }

        //有推荐人 或 推荐人不是代理
        String unionId = userService.getUserDao().getOpenIdById(userId);
        Recommend recommend = recommendService.getRecommendDao().findOne(unionId);
        boolean isNoRecommend = recommend == null || recommend.getAgentId() == agentId;
        if (!isNoRecommend) {
            return false;
        }

        GameAgentWx gameAgentWx = gameAgentWxService.getGameAgentWxDao().findOne(unionId);

        //没关注公众号
        if (gameAgentWx == null) {
            return false;
        }

        String openId = gameAgentWx.getOpenId();


        AgentBean parentAgentBean = RedisManager.getAgentRedisService().getAgentBean(agentId);
        //变成代理
        user2Agent(userId, openId, unionId, parentAgentBean);


        return true;
    }


    public void user2Agent(long userId, String openId, String unionId, AgentBean parent) {
        GameAgent gameAgent = new GameAgent();
        gameAgent.setId(userId);
        gameAgent.setOpenId(openId);
        gameAgent.setUnionId(unionId);
        //有推荐


        //和上级的partner是同一个
        gameAgent.setPartnerId(parent.getPartnerId());
        gameAgent.setParentId(parent.getId());
        gameAgent.setIsPartner(0);
        //上级代理加一个child
        parent.getChildList().add(userId);
        RedisManager.getAgentRedisService().addSaveAgent(parent.getId());

        //保存到数据库
        gameAgentService.getGameAgentDao().save(gameAgent);
        AgentBean agentBean = AgentService.gameAgent2AgentBean(gameAgent);
        //保存的reids
        RedisManager.getAgentRedisService().setAgent2Redis(agentBean);
        RedisManager.getAgentRedisService().addSaveAgent(agentBean.getId());
    }
}
