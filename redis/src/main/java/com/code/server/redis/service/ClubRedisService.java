package com.code.server.redis.service;

import com.code.server.redis.config.IConstant;
import com.code.server.redis.dao.IClubRedis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by sunxianping on 2018-11-23.
 */
@Service
public class ClubRedisService implements IClubRedis, IConstant {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public double getClubUserMoney(String clubId, long userId) {
        HashOperations<String, String, String> clubMoney = redisTemplate.opsForHash();
        return Double.parseDouble(clubMoney.get(getClubMoneyKey(clubId), "" + userId));
    }

    @Override
    public double addClubUserMoney(String clubId, long userId, double money) {
        HashOperations<String, String, Double> clubMoney = redisTemplate.opsForHash();
        // 把修改后的值放入userBean里
        return clubMoney.increment(getClubMoneyKey(clubId), "" + userId, money);
    }

    public Map<String, Double> getMoneyMap(String clubId) {
        HashOperations<String, String, Double> clubMoney = redisTemplate.opsForHash();
        return clubMoney.entries(getClubMoneyKey(clubId));
    }

    public long getUserMoneyCount(String clubId) {
        HashOperations<String, String, Double> clubMoney = redisTemplate.opsForHash();
        return clubMoney.size(getClubMoneyKey(clubId));
    }

    private String getClubMoneyKey(String clubId) {
        return CLUB_MONEY + clubId;
    }

}
