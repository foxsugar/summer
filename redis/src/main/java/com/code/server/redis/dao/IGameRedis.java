package com.code.server.redis.dao;

import com.code.server.constant.exception.RegisterFailedException;
import com.code.server.redis.config.ServerInfo;

import java.util.List;
import java.util.Map;

/**
 * Created by sunxianping on 2017/6/14.
 */
public interface IGameRedis {
    void register(String serverType,int serverId) throws RegisterFailedException;

    void heart(int serverId);

    void cleanGame(int serverId);

    long getLastHeart(int serverId);

    Map<String,String> getAllHeart();

    List<ServerInfo> getAllServer();

    long getServerCount();


}
