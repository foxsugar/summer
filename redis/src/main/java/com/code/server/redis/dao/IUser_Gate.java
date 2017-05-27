package com.code.server.redis.dao;

/**
 * Created by sunxianping on 2017/5/25.
 */
public interface IUser_Gate {

    String getGateId(long userId);

    void setGateId(long userId, String gateId);

    void removeGate(long userId);
}
