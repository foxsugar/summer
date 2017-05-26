package com.code.server.redis.dao;

/**
 * Created by sunxianping on 2017/5/25.
 */
public interface IRoom_Server {
    int getServerId(String roomId);

    void setServerId(String roomId, int serverid);

    void removeServer(String roomId);
}
