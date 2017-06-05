package com.code.server.login.action;


import com.code.server.constant.game.UserBean;
import com.code.server.db.Service.ServerService;
import com.code.server.db.Service.UserService;
import com.code.server.db.model.User;
import com.code.server.login.kafka.MsgProducer;
import com.code.server.login.util.MD5Util;
import com.code.server.redis.service.UserRedisService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by win7 on 2017/3/8.
 */

@RestController
@EnableAutoConfiguration
public class LoginAction {

    @Autowired
    private UserService userService;

    @Autowired
    private ServerService serverService;

    @Autowired
    private UserRedisService userRedisService;


    @RequestMapping("/login")
    public Map<String,Object> login(User user,String tokey_user){
        Map<String,Object> results = new HashMap<String,Object>();
        String account = user.getAccount();
        String password = user.getPassword();

        user = userService.getUserByAccountAndPassword(account,password);
        if(user!=null){
            if(tokey_user!=null){
                results.put("url","/login");
                results.put("tokey",tokey_user);
                results.put("code","0");
            }else{
                UserBean userBean = new UserBean();
                userBean.setId(user.getUserId());
                userBean.setUsername(user.getUsername());
                userBean.setImage(user.getImage());
                userBean.setAccount(user.getAccount());
                userBean.setIpConfig(user.getIpConfig());
                userBean.setMoney(user.getMoney());
                userBean.setVip(user.getVip());
                userBean.setUuid(user.getUuid());
                userBean.setOpenId(user.getOpenId());
                userBean.setSex(user.getSex());
                userRedisService.setUserBean(userBean);
                userRedisService.setUserMoney(user.getUserId(),user.getMoney());
                String tokey = MD5Util.MD5Encode(userBean.toString(),"UTF-8");

                results.put("url","/login");
                results.put("tokey",tokey);
                results.put("code","0");
            }
        }else{
            results.put("url","/login");
            results.put("code","100000");
        }

        return results;
    }

    @RequestMapping("/appleCheck")
    public Map<String,Object> appleCheck(){
        Map<String,Object> results = new HashMap<String,Object>();
        Gson gson = new Gson();
        String s = gson.toJson(serverService.getAllServerInfo().get(0));

        results.put("url","/appleCheck");
        results.put("params",s);
        results.put("code","0");

        return results;
    }


    /**
     *  支付demo
     * @return
     */
    private void httpUrlConnection() {

    }

    public static void main(String[] args) {
        Map<Integer,Integer> map = new HashMap<>();
        List<Test> list = new ArrayList<>();
        list.add(new Test());
        list.add(new Test());
        list.add(new Test());
        list.add(new Test());
        list.add(new Test());

        map.put(1, 2);
        map.put(2, 2);
        map.put(3, 2);
        map.put(4, 2);
        map.put(5, 2);


    }


    public static class Test{
        int a = 1;
        String b = "";
    }
}
