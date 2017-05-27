package com.code.server.redis.dao;

import com.code.server.constant.game.IUserBean;
import com.code.server.constant.game.UserBean;

/**
 * Created by sunxianping on 2017/5/25.
 */
public interface IUserRedis {

    double getUserMoney(long userId);

    double addUserMoney(long userId, double money);

    void setUserMoney(long userId, double money);


    UserBean getUserBean(long userId);


    void updateUserBean(long userId, UserBean userBean);




}
