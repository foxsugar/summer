package com.code.server.redis.dao;

import com.code.server.constant.exception.RegisterFailedException;

/**
 * Created by sunxianping on 2017/6/13.
 */
public interface IGateRedis {
    void register(int gateId,String host,int port) throws RegisterFailedException;

    void heart(int gateId);

    void cleanGate(int gateId);

    long getLastHeart(int gateId);
}
