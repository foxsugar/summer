package com.code.server.game.room.service;

import com.code.server.game.room.IfaceRoom;
import com.code.server.redis.service.RedisManager;
import com.code.server.game.room.Room;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
        //本地内存删除room
        getInstance().rooms.remove(roomId);
        //删除room-serverId 映射
        RedisManager.getRoomRedisService().removeServer(roomId);
        //删除room-user映射
        RedisManager.getRoomRedisService().removeRoomUsers(roomId);

        //删除user-room
        Set<Long> users =  RedisManager.getRoomRedisService().getUsers(roomId);
        users.forEach(RedisManager.getUserRedisService()::removeRoom);

    }

    public static void addRoom(String roomId,String serverId, Room room) {
        getInstance().rooms.put(roomId, room);
        RedisManager.getRoomRedisService().setServerId(roomId,serverId);
    }
}
