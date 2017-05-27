package com.code.server.redis.dao;

/**
 * Created by sunxianping on 2017/5/25.
 */
public interface IUser_Gate {

    int getGateId(long userId);

    void setGateId(long userId, int gateId);

    void removeGate(long userId);
}
