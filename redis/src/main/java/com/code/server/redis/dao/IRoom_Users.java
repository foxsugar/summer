package com.code.server.redis.dao;

import java.util.List;
import java.util.Set;

/**
 * Created by sunxianping on 2017/6/6.
 */
public interface IRoom_Users {
    void addUser(String roomId, long userId);

    void removeUser(String roomId, long userId);

    void removeRoomUsers(String roomId);

    Set<Long> getUsers(String roomId);
}
