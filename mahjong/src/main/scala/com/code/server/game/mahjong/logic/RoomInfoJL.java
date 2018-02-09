package com.code.server.game.mahjong.logic;


import com.code.server.redis.service.RedisManager;

public class RoomInfoJL extends RoomInfo {


    public void spendMoney() {
        for (long userId : users) {
            int money = -getCreateMoney();
            RedisManager.getUserRedisService().addUserMoney(userId, -money);
        }
    }


    protected boolean isCanJoinCheckMoney(int userId) {
        if (RedisManager.getUserRedisService().getUserMoney(userId) < 1) {
            return false;
        }
        return true;
    }
}
