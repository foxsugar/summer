package com.code.server.login.action;

import com.code.server.constant.response.ErrorCode;
import com.code.server.db.dao.IAgentUserDao;
import com.code.server.db.dao.ILogRecordDao;
import com.code.server.db.dao.IUserDao;
import com.code.server.db.model.AgentUser;
import com.code.server.db.model.LogRecord;
import com.code.server.login.util.AgentUtil;
import com.code.server.login.util.MD5Util;
import com.code.server.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
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

    public static String getToken(long userId) {
        return MD5Util.MD5Encode("salt," + userId + System.currentTimeMillis(), "UTF-8");
    }

    @RequestMapping("/login")
    public AgentResponse agentLogin(HttpServletRequest request, String username, String password){

        AgentUser agentUser = agentUserDao.findAgentUserByUsernameAndPassword(username, password);
        AgentResponse agentResponse = null;
        Map<String, Object> result = new HashMap<>();
        if (agentUser != null){
            //todo token 和 玩家的关联
            AgentUtil.caches.put("id", agentUser.getId());
            AgentUtil.caches.put("username", agentUser.getUsername());
            String token = getToken(agentUser.getId());
            AgentUtil.caches.put("token", token);
            result.put("token", token);
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
