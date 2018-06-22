package com.code.server.login.action;

import com.code.server.constant.response.*;
import com.code.server.constant.response.ErrorCode;
import com.code.server.db.dao.IUserDao;
import com.code.server.db.model.User;
import com.code.server.login.util.AgentUtil;
import com.code.server.login.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dajuejinxian on 2018/6/22.
 */
@RestController
@RequestMapping("/admin")
public class DemoAction{

    @Autowired
    private IUserDao userDao;

    public static String getToken(long userId) {
        return MD5Util.MD5Encode("salt," + userId + System.currentTimeMillis(), "UTF-8");
    }

    @RequestMapping("/login")
    public AgentResponse agentLogin(String username, String password){

        User user = userDao.getUserByAccountAndPassword(username, password);
        AgentResponse agentResponse = null;
        Map<String, Object> result = new HashMap<>();
        if (user != null){
            AgentUtil.caches.put("id", user.getId());
            AgentUtil.caches.put("username", user.getUsername());
            String token = getToken(user.getId());
            AgentUtil.caches.put("token", token);
            result.put("token", token);
            agentResponse = new AgentResponse(0, result);
        }else {
            agentResponse = new AgentResponse(ErrorCode.ROLE_ACCOUNT_OR_PASSWORD_ERROR,result);
            agentResponse.msg = "用户不存在";
        }
        return agentResponse;
    }


//    @RequestMapping("/info")
//    public AgentResponse userInfo(){
//
//    }
}
