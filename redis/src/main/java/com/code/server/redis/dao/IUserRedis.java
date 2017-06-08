package com.code.server.redis.dao;

import com.code.server.constant.game.IUserBean;
import com.code.server.constant.game.UserBean;

import java.util.List;

/**
 * Created by sunxianping on 2017/5/25.
 */
public interface IUserRedis {

    double getUserMoney(long userId);

    double addUserMoney(long userId, double money);

    void setUserMoney(long userId, double money);


    UserBean getUserBean(long userId);

    void setUserBean(UserBean userBean);

    String getAccountByUserId(String userId);

    void setUserIdAccount(String userId,String account);

    String getUserIdByAccount (String account);

    void setAccountUserId(String account,String userId);

    String getOpenIdByUserId(String userId);

    void setUserIdOpenId(String userId,String openId);

    String getUserIdByOpenId (String openId);

    void setOpenIdUserId(String openId,String userId);

    List<UserBean> getUserBeans(List<Long> users);


    void updateUserBean(long userId, UserBean userBean);




}
