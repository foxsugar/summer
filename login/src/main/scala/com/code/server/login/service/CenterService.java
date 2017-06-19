package com.code.server.login.service;

import com.code.server.constant.game.UserBean;
import com.code.server.db.Service.UserService;
import com.code.server.db.model.User;
import com.code.server.redis.service.RedisManager;
import com.code.server.util.SpringUtil;
import com.code.server.util.ThreadPool;
import com.code.server.util.timer.GameTimer;
import com.code.server.util.timer.TimerNode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by sunxianping on 2017/6/16.
 */
public class CenterService {
    public static void work(){
        //timer
        ThreadPool.execute(()-> GameTimer.getInstance().fire());

        //检测服务器状态
        CheckHeart.check();


        //保存玩家
        GameTimer.addTimerNode(new TimerNode(System.currentTimeMillis(),1000L*5,true, CenterService::saveUser));

    }

    private static void saveUser(){
        Set<String> users = RedisManager.getUserRedisService().getSaveUsers();
        if (users != null) {
            UserService userService = SpringUtil.getBean(UserService.class);
            Set<String> removeList = new HashSet<>();
            users.forEach(userId-> {
                long uid = Long.valueOf(userId);
                UserBean userBean = RedisManager.getUserRedisService().getUserBean(uid);
                User user = GameUserService.userBean2User(userBean);
                //保存
                userService.save(user);
                removeList.add(userId);
            });
            //批量删除 需要保存的玩家
            if (removeList.size() > 0) {
                RedisManager.getUserRedisService().removeSaveUser(removeList.toArray());
            }

        }
    }



}
