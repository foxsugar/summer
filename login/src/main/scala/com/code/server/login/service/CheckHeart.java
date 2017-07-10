package com.code.server.login.service;

import com.code.server.redis.service.RedisManager;
import com.code.server.util.timer.GameTimer;
import com.code.server.util.timer.TimerNode;

import java.util.Map;

/**
 * Created by sunxianping on 2017/6/15.
 */
public class CheckHeart {
    private static final long cleanTime = 1000L * 60 * 5;
    public static void check(){
        GameTimer.addTimerNode(new TimerNode(System.currentTimeMillis(),1000L*5,true,CheckHeart::checkAll));
    }

    private static void checkAll(){
        checkGate();
        checkGame();
    }
    private static void checkGate(){

        Map<String,String> map = RedisManager.getGateRedisService().getAllHeart();
        long now = System.currentTimeMillis();
        map.forEach((key, value) -> {
            int serverId = Integer.valueOf(key);
            long lastTime = Long.valueOf(value);
            if (now - lastTime > cleanTime) {
                RedisManager.getGateRedisService().cleanGate(serverId);
            }
        });
    }

    private static void checkGame(){

        Map<String,String> map = RedisManager.getGameRedisService().getAllHeart();
        long now = System.currentTimeMillis();
        map.forEach((key, value) -> {
            int serverId = Integer.valueOf(key);
            long lastTime = Long.valueOf(value);
            if (now - lastTime > cleanTime) {

                RedisManager.getGameRedisService().cleanGame(serverId);
            }
        });
    }
}
