package com.code.server.redis.dao;

/**
 * Created by sunxianping on 2017/5/25.
 */
public interface IRoom_Server {
    String getServerId(String roomId);

    void setServerId(String roomId, String serverid);

    void removeServer(String roomId);

    boolean isExist(String roomId);
}
