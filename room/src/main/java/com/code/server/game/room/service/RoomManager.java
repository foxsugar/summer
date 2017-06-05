package com.code.server.game.room.service;

import com.code.server.game.room.IfaceRoom;
import com.code.server.redis.service.RedisManager;
import com.code.server.game.room.Room;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sunxianping on 2017/5/31.
 */
public class RoomManager {


    private Map<String, IfaceRoom> rooms = new HashMap<>();

    private static RoomManager ourInstance = new RoomManager();

    public static RoomManager getInstance() {
        return ourInstance;
    }

    private RoomManager() {
    }

    public static IfaceRoom getRoom(String roomId){
        return getInstance().rooms.get(roomId);
    }

    public static void removeRoom(String roomId) {
        getInstance().rooms.remove(roomId);
        RedisManager.getRoomRedisService().removeServer(roomId);
    }

    public static void addRoom(String roomId,String serverId, Room room) {
        getInstance().rooms.put(roomId, room);
        RedisManager.getRoomRedisService().setServerId(roomId,serverId);
    }
}
