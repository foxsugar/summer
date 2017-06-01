package com.code.server.game.room.service;

import com.code.server.game.room.Room;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sunxianping on 2017/5/31.
 */
public class RoomManager {


    private Map<String, Room> rooms = new HashMap<>();

    private static RoomManager ourInstance = new RoomManager();

    public static RoomManager getInstance() {
        return ourInstance;
    }

    private RoomManager() {
    }

    public static Room getRoom(String roomId){
        return getInstance().rooms.get(roomId);
    }

    public static void removeRoom(String roomId) {
        getInstance().rooms.remove(roomId);
    }

    public static void addRoom(String roomId, Room room) {
        getInstance().rooms.put(roomId, room);
    }
}
