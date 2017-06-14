package com.code.server.redis.dao;

import com.code.server.constant.exception.RegisterFailedException;

/**
 * Created by sunxianping on 2017/6/14.
 */
public interface IGameRedis {
    void register(int serverId) throws RegisterFailedException;

    void heart(int serverId);

    void cleanGame(int serverId);

    long getLastHeart(int serverId);
}
