package com.code.server.redis.service;

import com.code.server.constant.game.UserBean;
import com.code.server.util.SpringUtil;

import java.util.Set;

/**
 * Created by sunxianping on 2017/6/1.
 */
public class RedisManager {
    private static RedisManager ourInstance = new RedisManager();

    public static RedisManager getInstance() {
        return ourInstance;
    }

    private RedisManager() {
    }

    public static UserRedisService getUserRedisService(){
        return SpringUtil.getBean(UserRedisService.class);
    }

    public static RoomRedisService getRoomRedisService(){
        return SpringUtil.getBean(RoomRedisService.class);
    }

    public static GateRedisService getGateRedisService(){
        return SpringUtil.getBean(GateRedisService.class);
    }
    public static GameRedisService getGameRedisService(){
        return SpringUtil.getBean(GameRedisService.class);
    }

    public static void addGold(long userId, double add){
        UserBean userBean = RedisManager.getUserRedisService().getUserBean(userId);
        userBean.setGold(userBean.getGold() + add);
        RedisManager.getUserRedisService().updateUserBean(userId, userBean);
    }

    public static void removeRoomAllInfo(Object... roomId) {
        //删除room-serverId 映射
        getRoomRedisService().removeServer(roomId);
        //删除room-user映射
        getRoomRedisService().removeRoomUsers(roomId);

        //删除user-room
        for(Object id : roomId){
            String rid = (String)id;
            Set<Long> users =  getRoomRedisService().getUsers(rid);
            users.forEach(getUserRedisService()::removeRoom);
        }
    }

}
