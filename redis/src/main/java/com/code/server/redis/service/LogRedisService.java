package com.code.server.redis.service;

import com.code.server.constant.db.LogInfo;
import com.code.server.redis.config.IConstant;
import com.code.server.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
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



    public Map<String,String> getGameNumInfo(String date){
        HashOperations<String, String, String> gameNum = redisTemplate.opsForHash();

        return gameNum.entries(getKayAddDateStr(LOG_GAMENUM,date));
    }


    public Map<String,String> getGoldIncomeInfo(String date){
        HashOperations<String, String, String> gameNum = redisTemplate.opsForHash();

        return gameNum.entries(getKayAddDateStr(LOG_GOLDINCOME,date));
    }


    public LogInfo getLogInfo(String... date){
        String d = null;
        if (date == null || date.length == 0) {
            d = getTodayStr();
        } else {
            d = date[0];
        }

        HashOperations<String, String, String> logInfo = redisTemplate.opsForHash();

        String json =  logInfo.get(LOG_OTHER_INFO,d);
        if (json == null) {
            return new LogInfo();
        }
        return JsonUtil.readValue(json, LogInfo.class);
    }


    public void setLogInfo(LogInfo logInfo) {
        if (logInfo == null) {
            logInfo = new LogInfo();
        }
        HashOperations<String, String, String> info = redisTemplate.opsForHash();
        info.put(LOG_OTHER_INFO, getTodayStr(), JsonUtil.toJson(logInfo));

    }

    public double getChargeRebate(String date){
//        HashOperations<String, String, Double> gameNum = redisTemplate.opsForHash();
        BoundHashOperations<String,String,String> data = redisTemplate.boundHashOps(LOG_CHARGE_REBATE);
        if (data == null) {
            return 0;
        }
        String result = data.get(date);
        if (result == null) {
            return 0;
        }
        return Double.valueOf(result);
    }

    public void addGameNum(String gamelogKey, int num) {
        HashOperations<String, String, Integer> gameNum = redisTemplate.opsForHash();
//        gameNum.putIfAbsent(getKayAddDateStr(LOG_GAMENUM), gamelogKey, 0);
        gameNum.increment(getKayAddDateStr(LOG_GAMENUM), gamelogKey, num);
    }


    public void addGoldIncome(String gamelogKey, double income){
        HashOperations<String, String, Double> gameNum = redisTemplate.opsForHash();
//        gameNum.putIfAbsent(getKayAddDateStr(LOG_GOLDINCOME), gamelogKey, 0D);
        gameNum.increment(getKayAddDateStr(LOG_GOLDINCOME), gamelogKey, income);
    }

    public void addChargeRebate(double rebate) {
        HashOperations<String, String, Double> gameNum = redisTemplate.opsForHash();
//        gameNum.putIfAbsent(LOG_CHARGE_REBATE, getTodayStr(), 0D);
        gameNum.increment(LOG_CHARGE_REBATE, getTodayStr(), rebate);
    }

    public void putGameNum(Map<String,String> map) {
        HashOperations<String, String, String> gameNum = redisTemplate.opsForHash();
        gameNum.putAll(getKayAddDateStr(LOG_GAMENUM),map);
    }
    public void putGoldIncome(Map<String,String> map) {
        HashOperations<String, String, String> gameNum = redisTemplate.opsForHash();
        gameNum.putAll(getKayAddDateStr(LOG_GOLDINCOME),map);
    }

    public void putChargeRebate(double num) {
        HashOperations<String, String, String> gameNum = redisTemplate.opsForHash();
        gameNum.putIfAbsent(LOG_CHARGE_REBATE, getTodayStr(), ""+num);
    }


    public void logCharge(int orign, double chargeType, double money) {
        LogInfo logInfo = getLogInfo();
        String key = orign +"|"+chargeType;
        double old = logInfo.getChargeInfo().getOrDefault(key, 0D);
        logInfo.getChargeInfo().put(key, money + old);
        setLogInfo(logInfo);
    }

    public void logRegisterUser() {
        LogInfo logInfo = getLogInfo();
        logInfo.setRegisterUser(logInfo.getRegisterUser() + 1);
        setLogInfo(logInfo);
    }
    public void logTakeOutNum(double num) {
        LogInfo logInfo = getLogInfo();
        logInfo.setTakeOutNum(logInfo.getTakeOutNum() + num);
        setLogInfo(logInfo);
    }


    private String getKayAddDateStr(String key) {
        return key + getTodayStr();
    }

    private String getKayAddDateStr(String key,String date) {
        return key + date;
    }

    private String getTodayStr() {
        return LocalDate.now().toString();
    }




    public static void main(String[] args) {
        System.out.println(LocalDate.now().toString());
    }
}
