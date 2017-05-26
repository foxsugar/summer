package com.code.login.action;


import com.code.server.db.Service.ServerService;
import com.code.server.db.Service.UserService;
import com.code.server.db.model.User;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@EnableAutoConfiguration
public class LoginAction{

    @Autowired
    private UserService userService;

    @Autowired
    private ServerService serverService;

    @RequestMapping("/inlogin")
    public Map<String,Object> inlogin(User user){
        Map<String,Object> results = new HashMap<String,Object>();
        String account = user.getAccount();
        String password = user.getPassword();

        user = userService.getUserByAccountAndPassword(account,password);


        /*  if(user!=null){

        }else{
            results.put("code","100000");
        }*/

        return results;
    }

    @RequestMapping("/appleCheck")
    public Map<String,Object> appleCheck(){
        Map<String,Object> results = new HashMap<String,Object>();
        Gson gson = new Gson();
        String s = gson.toJson(serverService.getAllServerInfo().get(0));

        results.put("params",s);
        results.put("code","0");

        return results;
    }
}
