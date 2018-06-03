package com.code.server.redis.service;

import com.code.server.redis.config.IConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;

/**
 * Created by Administrator on 2018/6/3.
 */
@Service
public class LogRedisService implements IConstant {

    @Autowired
    private RedisTemplate redisTemplate;


    public Map<String,Integer> getGameNumInfo(){
        HashOperations<String, String, Integer> gameNum = redisTemplate.opsForHash();

        return gameNum.entries(getKayAddDateStr(LOG_GAMENUM));
    }


    public Map<String,Double> getGoldIncomeInfo(){
        HashOperations<String, String, Double> gameNum = redisTemplate.opsForHash();

        return gameNum.entries(getKayAddDateStr(LOG_GOLDINCOME));
    }

    public double getChargeRebate(){
        HashOperations<String, String, Double> gameNum = redisTemplate.opsForHash();
        return gameNum.get(LOG_CHARGE_REBATE, getTodayStr());
    }

    public void addGameNum(String gamelogKey, int num) {
        HashOperations<String, String, Integer> gameNum = redisTemplate.opsForHash();
        gameNum.putIfAbsent(getKayAddDateStr(LOG_GAMENUM), gamelogKey, 0);
        gameNum.increment(getKayAddDateStr(LOG_GAMENUM), gamelogKey, num);
    }


    public void addGoldIncome(String gamelogKey, double income){
        HashOperations<String, String, Double> gameNum = redisTemplate.opsForHash();
        gameNum.putIfAbsent(getKayAddDateStr(LOG_GOLDINCOME), gamelogKey, 0D);
        gameNum.increment(getKayAddDateStr(LOG_GOLDINCOME), gamelogKey, income);
    }

    public void addChargeRebate(double rebate) {
        HashOperations<String, String, Double> gameNum = redisTemplate.opsForHash();
        gameNum.putIfAbsent(LOG_CHARGE_REBATE, getTodayStr(), 0D);
        gameNum.increment(LOG_CHARGE_REBATE, getTodayStr(), rebate);
    }

    private String getKayAddDateStr(String key) {
        return key + getTodayStr();
    }

    private String getTodayStr() {
        return LocalDate.now().toString();
    }

    public static void main(String[] args) {
        System.out.println(LocalDate.now().toString());
    }
}
