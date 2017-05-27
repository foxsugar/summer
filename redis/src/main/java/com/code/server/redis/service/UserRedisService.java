package com.code.server.redis.service;

import com.code.server.constant.game.IUserBean;
import com.code.server.constant.game.UserBean;
import com.code.server.redis.config.IConstant;
import com.code.server.redis.dao.IUserRedis;
import com.code.server.redis.dao.IUser_Gate;
import com.code.server.redis.dao.IUser_Room;
import com.code.server.redis.dao.IUser_Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * Created by sunxianping on 2017/5/25.
 */
@Service
public class UserRedisService implements IUserRedis,IUser_Room,IUser_Gate,IConstant,IUser_Token {


    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    public String getRoomId(long userId) {
        HashOperations<String,Long,String> user_room = redisTemplate.opsForHash();
        return user_room.get(USERID_ROOMID, userId);
    }

    @Override
    public void setRoomId(long userId, String roomId) {
        HashOperations<String,Long,String> user_room = redisTemplate.opsForHash();
        user_room.put(USERID_ROOMID,userId,roomId);
    }

    @Override
    public void removeRoom(long userId) {
        HashOperations<String,Long,String> user_room = redisTemplate.opsForHash();
        user_room.delete(USERID_ROOMID, userId);
    }

    @Override
    public String getGateId(long userId) {
        HashOperations<String,Long,String> user_gate = redisTemplate.opsForHash();
        return user_gate.get(USERID_GATEID, userId);
    }

    @Override
    public void setGateId(long userId, String gateId) {
        HashOperations<String,Long,String> user_gate = redisTemplate.opsForHash();
        user_gate.put(USERID_GATEID, userId,gateId);
    }

    @Override
    public void removeGate(long userId) {
        HashOperations<String,Long,Integer> user_gate = redisTemplate.opsForHash();
        user_gate.delete(USERID_GATEID, userId);
    }

    @Override
    public double getUserMoney(long userId) {
        HashOperations<String,Long,Double> user_money = redisTemplate.opsForHash();
        return user_money.get(USER_MONEY, userId);
    }

    @Override
    public double addUserMoney(long userId, double money) {
        HashOperations<String,Long,Double> user_money = redisTemplate.opsForHash();
        return user_money.increment(USER_MONEY,userId,money);
    }

    @Override
    public void setUserMoney(long userId, double money) {
        HashOperations<String,Long,Double> user_money = redisTemplate.opsForHash();
        user_money.put(USER_MONEY,userId,money);
    }

    @Override
    public UserBean getUserBean(long userId) {
        HashOperations<String,Long,UserBean> user_bean = redisTemplate.opsForHash();
        return user_bean.get(USER_BEAN, userId);
    }

    @Override
    public void updateUserBean(long userId, UserBean userBean) {
        HashOperations<String,Long,UserBean> user_bean = redisTemplate.opsForHash();
        user_bean.put(USER_BEAN, userId,userBean);
    }


    @Override
    public void setToken(long userId, String token) {
        HashOperations<String,Long,String> user_token = redisTemplate.opsForHash();
        user_token.put(USER_TOKEN, userId,token);
    }

    @Override
    public String getToken(long userId) {
        HashOperations<String,Long,String> user_token = redisTemplate.opsForHash();
        return user_token.get(USER_TOKEN, userId);
    }
}
