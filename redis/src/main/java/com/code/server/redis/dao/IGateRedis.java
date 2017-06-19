package com.code.server.redis.dao;

import com.code.server.constant.exception.RegisterFailedException;
import com.code.server.redis.config.ServerInfo;

import java.util.List;
import java.util.Map;

/**
 * Created by sunxianping on 2017/6/13.
 */
public interface IGateRedis {
    void register(String serverType,int gateId,String host,String domain, int port) throws RegisterFailedException;

    void heart(int gateId);

    void cleanGate(int gateId);

    long getLastHeart(int gateId);

    Map<String,String> getAllHeart();

    List<ServerInfo> getAllServer();
}
