package com.code.server.redis.service;

import com.code.server.constant.game.UserBean;
import com.code.server.redis.config.IConstant;
import com.code.server.redis.dao.IUserRedis;
import com.code.server.redis.dao.IUser_Gate;
import com.code.server.redis.dao.IUser_Room;
import com.code.server.redis.dao.IUser_Token;
import com.code.server.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by sunxianping on 2017/5/25.
 */
@Service
public class UserRedisService implements IUserRedis,IUser_Room,IUser_Gate,IConstant,IUser_Token {


    private static final String USERBEAN = "userBean|";

    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    public String getRoomId(long userId) {
        HashOperations<String,String,String> user_room = redisTemplate.opsForHash();
        return user_room.get(USERID_ROOMID, ""+userId);
    }

    @Override
    public void setRoomId(long userId, String roomId) {
        HashOperations<String,String,String> user_room = redisTemplate.opsForHash();
        user_room.put(USERID_ROOMID,""+userId,roomId);
    }

    @Override
    public void removeRoom(long userId) {
        HashOperations<String,String,String> user_room = redisTemplate.opsForHash();
        user_room.delete(USERID_ROOMID, ""+userId);

    }

    @Override
    public String getGateId(long userId) {
        HashOperations<String,String,String> user_gate = redisTemplate.opsForHash();
        return user_gate.get(USERID_GATEID, ""+userId);
    }

    @Override
    public void setGateId(long userId, String gateId) {
        HashOperations<String,String,String> user_gate = redisTemplate.opsForHash();
        user_gate.put(USERID_GATEID, ""+userId,gateId);
    }

    @Override
    public void removeGate(long userId) {
        HashOperations<String,String,String> user_gate = redisTemplate.opsForHash();
        user_gate.delete(USERID_GATEID, ""+userId);
    }

    @Override
    public double getUserMoney(long userId) {
        HashOperations<String,String,String> user_money = redisTemplate.opsForHash();
        return Double.parseDouble(user_money.get(USER_MONEY, ""+userId));
    }

    @Override
    public double addUserMoney(long userId, double money) {
        HashOperations<String,String,Double> user_money = redisTemplate.opsForHash();
        // 把修改后的值放入userBean里
        double m = user_money.increment(USER_MONEY,""+userId,money);
        UserBean userBean = getUserBean(userId);
        if (userBean != null) {
            userBean.setMoney(m);
            updateUserBean(userId,userBean);
        }
        return m;
    }

    @Override
    public void setUserMoney(long userId, double money) {
        HashOperations<String,String,String> user_money = redisTemplate.opsForHash();
        user_money.put(USER_MONEY,""+userId,""+money);
    }

    @Override
    public UserBean getUserBean(long userId) {
//        ValueOperations<String,UserBean> user_bean = redisTemplate.opsForValue();
        BoundHashOperations<String,String,String> user_bean = redisTemplate.boundHashOps(USER_BEAN);
        String json = user_bean.get(String.valueOf(userId));
        if (json != null) {
            return JsonUtil.readValue(json, UserBean.class);
        }
        return null;
    }

    @Override
    public void setUserBean(UserBean userBean) {
        updateUserBean(userBean.getId(), userBean);
    }

    @Override
    public List<UserBean> getUserBeans(List<Long> users) {
        BoundHashOperations<String,String,String> user_bean = redisTemplate.boundHashOps(USER_BEAN);
        List<String> userStrs = users.stream().map(this::getUserBeanKey).collect(Collectors.toList());
        return user_bean.multiGet(userStrs).stream().map(ub->JsonUtil.readValue(ub,UserBean.class)).collect(Collectors.toList());
    }


    @Override
    public void updateUserBean(long userId, UserBean userBean) {
        BoundHashOperations<String,String,String> user_bean = redisTemplate.boundHashOps(USER_BEAN);
        user_bean.put(String.valueOf(userId),JsonUtil.toJson(userBean));
    }


    @Override
    public void setToken(long userId, String token) {
        HashOperations<String,String,String> user_token = redisTemplate.opsForHash();
        user_token.put(USER_TOKEN, ""+userId,token);
    }

    @Override
    public String getToken(long userId) {
        HashOperations<String,String,String> user_token = redisTemplate.opsForHash();
        return user_token.get(USER_TOKEN, ""+userId);
    }


    @Override
    public String getAccountByUserId(long userId) {
        HashOperations<String,String,String> user_token = redisTemplate.opsForHash();
        return user_token.get(USER_ACCOUNT, ""+userId);
    }

    @Override
    public void setUserIdAccount(long userId, String account) {
        HashOperations<String,String,String> user_token = redisTemplate.opsForHash();
        user_token.put(USER_ACCOUNT, ""+userId,account);
    }

    @Override
    public String getUserIdByAccount(String account) {
        HashOperations<String,String,String> user_token = redisTemplate.opsForHash();
        return user_token.get(USER_ACCOUNT, account);
    }

    @Override
    public void setAccountUserId(String account, long userId) {
        HashOperations<String,String,String> user_token = redisTemplate.opsForHash();
        user_token.put(ACCOUNT_USER, ""+account,""+userId);
    }

    @Override
    public String getOpenIdByUserId(long userId) {
        HashOperations<String,String,String> user_token = redisTemplate.opsForHash();
        return user_token.get(USER_ACCOUNT, ""+userId);
    }

    @Override
    public void setUserIdOpenId(long userId, String openId) {
        HashOperations<String,String,String> user_token = redisTemplate.opsForHash();
        user_token.put(USER_OPENID, ""+userId,openId);
    }

    @Override
    public String getUserIdByOpenId(String openId) {
        HashOperations<String,String,String> user_token = redisTemplate.opsForHash();
        return user_token.get(OPENID_USER, openId);
    }

    @Override
    public void setOpenIdUserId(String openId, long userId) {
        HashOperations<String,String,String> user_token = redisTemplate.opsForHash();
        user_token.put(OPENID_USER, ""+openId,""+userId);
    }


    private String getUserBeanKey(long userId){
        return USERBEAN+userId;
    }

    public void test(long userId){
        HashOperations<String,String,String> user_money = redisTemplate.opsForHash();
        user_money.get(USER_MONEY,user_money);

    }
}
