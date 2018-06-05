package com.code.server.game.room.service;

import com.code.server.game.room.IfaceRoom;
import com.code.server.game.room.Room;
import com.code.server.game.room.RoomExtendGold;
import com.code.server.redis.service.RedisManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sunxianping on 2017/5/31.
 */
public class RoomManager {


    private Map<String, IfaceRoom> rooms = new HashMap<>();

    private Map<String, Map<Integer, List<Room>>> fullGoldRoom = new HashMap<>();
    private Map<String, Map<Integer, List<Room>>> notFullGoldRoom = new HashMap<>();
    private Map<String, Map<Integer, List<Room>>> publicGoldRoom = new HashMap<>();
    private Map<Long, List<String>> prepareRoom = new HashMap<>();


    private List<Room> robotRoom = new ArrayList<>();

    private static RoomManager outInstance = new RoomManager();

    public static RoomManager getInstance() {
        return outInstance;
    }

    private RoomManager() {
    }

    public static IfaceRoom getRoom(String roomId) {
        return getInstance().rooms.get(roomId);
    }


    /**
     * 获得满的房间啊
     *
     * @param gameType
     * @param goldGameType
     * @return
     */
    public List<Room> getFullRoom(String gameType, int goldGameType) {
        Map<Integer, List<Room>> fullRooms = fullGoldRoom.computeIfAbsent(gameType, k -> new HashMap<>());
        return fullRooms.computeIfAbsent(goldGameType, k -> new ArrayList<>());
    }

    /**
     * 获得不满房间
     *
     * @param gameType
     * @param goldGameType
     * @return
     */
    public List<Room> getNotFullRoom(String gameType, int goldGameType) {
        Map<Integer, List<Room>> notFullRooms = notFullGoldRoom.computeIfAbsent(gameType, k -> new HashMap<>());
        return notFullRooms.computeIfAbsent(goldGameType, k -> new ArrayList<>());
    }

    /**
     * 获得公开金币房
     *
     * @param gameType
     * @param goldGameType
     * @return
     */
    public List<Room> getPublicGoldRoom(String gameType, int goldGameType) {
        Map<Integer, List<Room>> publicGoldRoom = this.publicGoldRoom.computeIfAbsent(gameType, k -> new HashMap<>());
        return publicGoldRoom.computeIfAbsent(goldGameType, k -> new ArrayList<>());
    }

    /**
     * 删除满房间，添加不满房间
     *
     * @param room
     */
    public void moveFull2NotFullRoom(Room room) {
        getFullRoom(room.getGameType(), room.getGoldRoomType()).remove(room);
        List<Room> notFull = getNotFullRoom(room.getGameType(), room.getGoldRoomType());
        if (!notFull.contains(room)) {
            notFull.add(room);
        }

    }

    /**
     * 删除不满房间，添加满房间
     *
     * @param room
     */
    public void moveGoldRoomNotFull2Full(Room room) {
        getNotFullRoom(room.getGameType(), room.getGoldRoomType()).remove(room);
        List<Room> full = getFullRoom(room.getGameType(), room.getGoldRoomType());
        if (!full.contains(room)) {
            full.add(room);
        }
    }

    /**
     * 添加一个不满的金币房
     *
     * @param room
     */
    public void addNotFullGoldRoom(Room room) {
        List<Room> rooms = getNotFullRoom(room.getGameType(), room.getGoldRoomType());
        rooms.add(room);
    }


    /**
     * 删除一个满的金币房
     *
     * @param room
     */
    public void removeGoldRoomFromMap(Room room) {
        Map<Integer, List<Room>> notFullRooms = notFullGoldRoom.get(room.getGameType());
//        Map<Double, List<Room>> fullRooms = notFullGoldRoom.get(room.getGameType());
        if (notFullRooms != null) {
            //删除满的房间
            List<Room> rooms_type = notFullRooms.get(room.getGoldRoomType());
            if (rooms_type != null) {
                rooms_type.remove(room);
            }
        }
    }

    /**
     * 删除一个房间
     *
     * @param roomId
     */
    public static void removeRoom(String roomId) {
        //本地内存删除room
        IfaceRoom room = getInstance().rooms.get(roomId);
        getInstance().rooms.remove(roomId);
        RedisManager.removeRoomAllInfo(roomId);
        //删除代开房
        if (room != null) {
            Room rm = (Room) room;
            //是否是代建房
            if (!rm.isCreaterJoin()) {
                RedisManager.getUserRedisService().removePerpareRoom(rm.getCreateUser(), rm.getRoomId());
            }

            //有托管功能的
            if (rm.isRobotRoom) {
                getInstance().robotRoom.remove(room);
            }

            //金币房
            if (rm.isGoldRoom() && rm.isDefaultGoldRoom()) {
                //默认金币房
                if (rm.isDefaultGoldRoom()) {
                    getInstance().getNotFullRoom(rm.getGameType(), rm.getGoldRoomType()).remove(room);
                    getInstance().getFullRoom(rm.getGameType(), rm.getGoldRoomType()).remove(room);
                } else {
                    //公开金币房
                    getInstance().getPublicGoldRoom(rm.getGameType(), rm.getGoldRoomType()).remove(room);
                }
            }
        }


    }

    /**
     * 添加一个房间
     *
     * @param roomId
     * @param serverId
     * @param room
     */
    public static void addRoom(String roomId, String serverId, Room room) {
        getInstance().rooms.put(roomId, room);
        //加入机器人房
        if (room.isRobotRoom) {
            getInstance().robotRoom.add(room);
        }
        //加入公开金币房
        if (room.isGoldRoom() && room.getGoldRoomType() != RoomExtendGold.GOLD_ROOM_PERMISSION_DEFAULT) {
            getInstance().getPublicGoldRoom(room.getGameType(), room.getGoldRoomType()).add(room);
        }
        //加入redis server-room 列表
        RedisManager.getRoomRedisService().setServerId(roomId, serverId);
        //加入代开房列表
        if (!room.isCreaterJoin()) {
            RedisManager.getUserRedisService().addPerpareRoom(room.getCreateUser(), room.getPrepareRoomVo());
        }

    }

    /**
     * 获得空的金币房
     *
     * @param gameType
     * @param goldRoomType
     * @return
     */
    public static Room getNullGoldRoom(String gameType, int goldRoomType) {
        Map<Integer, List<Room>> rooms = getInstance().notFullGoldRoom.get(gameType);
        if (rooms == null) {
            return null;
        }
        List<Room> list = rooms.get(goldRoomType);
        if (list != null && list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public Map<String, Map<Integer, List<Room>>> getFullGoldRoom() {
        return fullGoldRoom;
    }

    public RoomManager setFullGoldRoom(Map<String, Map<Integer, List<Room>>> fullGoldRoom) {
        this.fullGoldRoom = fullGoldRoom;
        return this;
    }

    public Map<String, Map<Integer, List<Room>>> getNotFullGoldRoom() {
        return notFullGoldRoom;
    }

    public RoomManager setNotFullGoldRoom(Map<String, Map<Integer, List<Room>>> notFullGoldRoom) {
        this.notFullGoldRoom = notFullGoldRoom;
        return this;
    }

    public Map<Long, List<String>> getPrepareRoom() {
        return prepareRoom;
    }

    public RoomManager setPrepareRoom(Map<Long, List<String>> prepareRoom) {
        this.prepareRoom = prepareRoom;
        return this;
    }

    public List<Room> getRobotRoom() {
        return robotRoom;
    }

    public RoomManager setRobotRoom(List<Room> robotRoom) {
        this.robotRoom = robotRoom;
        return this;
    }

    public Map<String, IfaceRoom> getRooms() {
        return rooms;
    }

    public RoomManager setRooms(Map<String, IfaceRoom> rooms) {
        this.rooms = rooms;
        return this;
    }

    public Map<String, Map<Integer, List<Room>>> getPublicGoldRoom() {
        return publicGoldRoom;
    }

    public RoomManager setPublicGoldRoom(Map<String, Map<Integer, List<Room>>> publicGoldRoom) {
        this.publicGoldRoom = publicGoldRoom;
        return this;
    }
}
