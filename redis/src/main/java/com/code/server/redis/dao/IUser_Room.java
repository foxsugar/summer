package com.code.server.redis.dao;

/**
 * Created by sunxianping on 2017/5/25.
 */
public interface IUser_Room {

    String getRoomId(long userId);

    void setRoomId(long userId, String roomId);

    void removeRoom(long... userId);
}
