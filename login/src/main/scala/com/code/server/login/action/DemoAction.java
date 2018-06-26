package com.code.server.login.action;

import com.code.server.constant.game.AgentBean;
import com.code.server.constant.response.ErrorCode;
import com.code.server.db.dao.IAgentUserDao;
import com.code.server.db.dao.ILogRecordDao;
import com.code.server.db.dao.IUserDao;
import com.code.server.db.model.AgentUser;
import com.code.server.db.model.LogRecord;
import com.code.server.db.model.User;
import com.code.server.login.util.AgentUtil;
import com.code.server.login.util.MD5Util;
import com.code.server.login.vo.OneLevelInfoVo;
import com.code.server.redis.service.RedisManager;
import com.code.server.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dajuejinxian on 2018/6/22.
 */
@RestController
@RequestMapping("/admin")
public class DemoAction{

    @Autowired
    private IAgentUserDao agentUserDao;

    @Autowired
    private ILogRecordDao logRecordDao;

    @Autowired
    private IUserDao userDao;

    public static String getToken(long userId) {
        return MD5Util.MD5Encode("salt," + userId + System.currentTimeMillis(), "UTF-8");
    }

    public static Map<String, Object> getUserInfo(HttpServletRequest request){
        return (Map<String, Object>)AgentUtil.caches.get("a");
    }

    @RequestMapping("/fetchPlayer")
    public AgentResponse fetchPlayer(long userId){
       User user = userDao.findOne(userId);
       AgentResponse agentResponse = new AgentResponse();
       if (user == null){
           agentResponse.setCode(com.code.server.login.action.ErrorCode.ERROR);
           agentResponse.setMsg("用户不存在");
       }else {
           agentResponse.setCode(0);
           OneLevelInfoVo oneLevelInfoVo = new OneLevelInfoVo();
           oneLevelInfoVo.setGold(user.getGold() + "");
           oneLevelInfoVo.setMoney(user.getMoney() +"");
           oneLevelInfoVo.setImage(user.getImage() + "");
           oneLevelInfoVo.setUid(user.getId());
           oneLevelInfoVo.setUsername(user.getUsername());
           Map<String, Object> rs = new HashMap<>();
           rs.put("result", oneLevelInfoVo);
           agentResponse.setData(rs);
       }
       return agentResponse;
    };

    @RequestMapping("/upward")
    public AgentResponse upwardDelegates(HttpServletRequest request, long userId){

        return null;
    }

    @RequestMapping("/downward")
    public AgentResponse downwardDelegate(HttpServletRequest request, long userId){

        Map<String, Object> userInfo =  getUserInfo(request);
        int agentId = (int) userInfo.get("id");
        AgentBean agentBean = RedisManager.getAgentRedisService().getAgentBean(agentId);

        //直接玩家
        List<Long> aList = new ArrayList<>();
        List<Long> bList = new ArrayList<>();
        List<Long> cList = new ArrayList<>();

        agentBean.getChildList().stream()
                .forEach(x -> {

                    if (RedisManager.getAgentRedisService().isExit(x)){
                        bList.add(x);
                    }else {
                        aList.add(x);
                    }
                });

        bList.stream()
                .forEach(x -> {
                    if (RedisManager.getAgentRedisService().isExit(x)){
                        cList.add(x);
                    }
                });

        List<User> aUsers = userDao.findUsersByIdIn(aList);
        List<User> bUsers = userDao.findUsersByIdIn(bList);
        List<User> cUsers = userDao.findUsersByIdIn(cList);

        return null;
    }

    @RequestMapping("/login")
    public AgentResponse agentLogin(HttpServletRequest request, String username, String password){

        AgentUser agentUser = agentUserDao.findAgentUserByUsernameAndPassword(username, password);
        AgentResponse agentResponse = null;
        Map<String, Object> result = new HashMap<>();
        if (agentUser != null){
            //todo token 和 玩家的关联
            Map<String, Object> rs = new HashMap<>();
            rs.put("id", agentUser.getId());
            rs.put("username", agentUser.getUsername());
            String token = getToken(agentUser.getId());
            AgentUtil.caches.put(token, rs);
            agentResponse = new AgentResponse(0, result);
        }else {
            agentResponse = new AgentResponse(ErrorCode.ROLE_ACCOUNT_OR_PASSWORD_ERROR,result);
            agentResponse.msg = "用户不存在";
        }
        return agentResponse;
    }

    @RequestMapping("/info")
    public AgentResponse userInfo(String token){
        //todo token 验证
        AgentUtil.caches.get(token);
        Map<String, Object> r = new HashMap<>();
        int[] roles = new int[]{1};
        r.put("userId", 1);
        r.put("roles", roles);
        return new AgentResponse(0, r);
    }

    @RequestMapping("/onlineInfo")
    public AgentResponse onlineInfo(String date){
        //todo token 验证
        LogRecord logRecord = logRecordDao.findOne(date);
        return new AgentResponse(0, JsonUtil.toJson(logRecord));

    }

}
