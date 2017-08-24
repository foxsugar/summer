package com.code.server.redis.dao;

import com.code.server.constant.game.PrepareRoom;

import java.util.Map;

/**
 * Created by sunxianping on 2017/5/25.
 */
public interface IUser_Room {

    String getRoomId(long userId);

    void setRoomId(long userId, String roomId);

    void removeRoom(long userId);

    Map<String, PrepareRoom> getPerpareRoom(long userId);

    void removePerpareRoom(long userId, String roomId);

    void addPerpareRoom(long userId, PrepareRoom prepareRoom);
}
