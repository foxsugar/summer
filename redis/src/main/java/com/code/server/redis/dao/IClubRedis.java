package com.code.server.redis.dao;

import java.util.Map;

/**
 * Created by sunxianping on 2018-11-23.
 */
public interface IClubRedis {

    double getClubUserMoney(String clubId, long userId);

    double addClubUserMoney(String clubId, long userId, double money);

    Map<String, Double> getMoneyMap(String clubId);

    long getUserMoneyCount(String clubId);

}
