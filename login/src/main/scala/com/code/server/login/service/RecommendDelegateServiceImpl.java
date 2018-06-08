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
import com.code.server.login.action.RecommendDelegateAction;
import com.code.server.login.vo.RecommandUserVo;
import com.code.server.redis.service.RedisManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(RecommendDelegateServiceImpl.class);
    @Override
    public RecommandUserVo findRecommandUser(long userId, long agentId) {

        UserBean userBean = RedisManager.getUserRedisService().getUserBean(userId);

        logger.info("userBean:{}", userBean);
        RecommandUserVo recommandUserVo = new RecommandUserVo();

        if (userBean != null) {
            recommandUserVo.setUsername("用户不存在");
            recommandUserVo.setUserId(new Long(0));
            //空头像
            recommandUserVo.setImage("");
        } else {

            //推荐代理
            User user = userDao.findOne(userId);
            logger.info("user:{}", user);

            recommandUserVo.setImage(user.getImage());
            recommandUserVo.setUserId(userId);
            recommandUserVo.setUsername(user.getUsername());
        }

        return recommandUserVo;
    }

    @Override
    public boolean bindDelegate(long userId, long agentId) {

        AgentBean userAgentBean = RedisManager.getAgentRedisService().getAgentBean(userId);

        //todo 之前是代理
        if (userAgentBean != null) {
            return false;
        }

        //必须先在公众号点击专属链接 成为下级
        AgentBean parentAgentBean = RedisManager.getAgentRedisService().getAgentBean(agentId);
        if (!parentAgentBean.getChildList().contains(userId)) {
            return false;
        }

        String unionId = userService.getUserDao().getOpenIdById(userId);



        //变成代理
        user2Agent(userId, unionId, parentAgentBean);

        return true;
    }


    /**
     * 玩家成为代理
     * @param userId
     * @param unionId
     * @param parent
     */
    public void user2Agent(long userId, String unionId, AgentBean parent) {
        GameAgent gameAgent = new GameAgent();
        gameAgent.setId(userId);
        gameAgent.setUnionId(unionId);
        //有推荐


        //和上级的partner是同一个
        gameAgent.setPartnerId(parent.getPartnerId());
        gameAgent.setParentId(parent.getId());
        gameAgent.setIsPartner(0);
        //上级代理加一个child
        parent.getChildList().add(userId);
        RedisManager.getAgentRedisService().updateAgentBean(parent);

        //保存到数据库
        gameAgentService.getGameAgentDao().save(gameAgent);
        AgentBean agentBean = AgentService.gameAgent2AgentBean(gameAgent);
        //保存的reids
        RedisManager.getAgentRedisService().setAgent2Redis(agentBean);
        RedisManager.getAgentRedisService().updateAgentBean(agentBean);
    }
}
