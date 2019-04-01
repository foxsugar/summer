package com.code.server.redis.service;

import com.code.server.redis.config.IConstant;
import com.code.server.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by sunxianping on 2019-04-01.
 */
@Service
public class ConstantRedisService implements IConstant {

    @Autowired
    private RedisTemplate redisTemplate;

    public void updateConstant(Map<String,Object> constant){
        BoundValueOperations<String,String> redis = redisTemplate.boundValueOps("constant");

        redis.set(JsonUtil.toJson(constant));
    }

    public Map getConstant(){

        BoundValueOperations<String,String> redis_data = redisTemplate.boundValueOps("constant");

        return JsonUtil.readValue(redis_data.get(), Map.class);

    }
}
