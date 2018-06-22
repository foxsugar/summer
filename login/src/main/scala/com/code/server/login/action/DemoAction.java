package com.code.server.login.action;

import com.code.server.db.dao.IUserDao;
import com.code.server.db.model.User;
import com.code.server.login.util.AgentUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by dajuejinxian on 2018/6/22.
 */
@RestController
public class DemoAction{

    @Autowired
    private IUserDao userDao;

    @RequestMapping("/login")
    public AgentResponse agentLogin(String username, String password){

        User user = userDao.getUserByAccountAndPassword(username, password);
        if (user != null){
            AgentUtil.yy_Caches.put("id", user.getId());
            AgentUtil.yy_Caches.put("username", user.getUsername());
        }else {


        }
        return null;
    }


//    @RequestMapping("/info")
//    public AgentResponse userInfo(){
//
//    }
}
