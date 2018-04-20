package com.code.server.game.room.service;

import com.code.server.game.room.IfaceRoom;
import com.code.server.game.room.Room;
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

    private  Map<String,Map<Double, List<Room>>> fullGoldRoom = new HashMap<>();
    private  Map<String, Map<Double,List<Room>>> notFullGoldRoom = new HashMap<>();
    private  Map<Long, List<String>> prepareRoom = new HashMap<>();

    private  List<Room> robotRoom = new ArrayList<>();

    private static RoomManager outInstance = new RoomManager();

    public static RoomManager getInstance() {
        return outInstance;
    }

    private RoomManager() {
    }

    public static IfaceRoom getRoom(String roomId){
        return getInstance().rooms.get(roomId);
    }







    public List<Room> getFullRoom(String gameType, double goldGameType) {
        Map<Double, List<Room>> fullRooms = fullGoldRoom.computeIfAbsent(gameType, k -> new HashMap<>());

        List<Room> rooms = fullRooms.computeIfAbsent(goldGameType, k -> new ArrayList<>());
        return rooms;
    }

    public List<Room> getNotFullRoom(String gameType, double goldGameType) {
        Map<Double, List<Room>> notFullRooms = notFullGoldRoom.computeIfAbsent(gameType, k -> new HashMap<>());
        List<Room> rooms = notFullRooms.computeIfAbsent(goldGameType, k -> new ArrayList<>());
        return rooms;
    }




    public void removeFromFullRoom(Room room){
        //删除满房间，添加不满房间
        Map<Double, List<Room>> fullRooms = fullGoldRoom.get(room.getGameType());
        if (fullRooms != null) {
            //删除满的房间
            List<Room> rooms_type = fullRooms.get(room.getGoldRoomType());
            if (rooms_type != null) {

                rooms_type.remove(room);
            }
        }
        //加入未满的房间
        Map<Double, List<Room>> notFullRooms = notFullGoldRoom.get(room.getGameType());
        if (notFullRooms != null) {
            List<Room> rooms_type = notFullRooms.get(room.getGoldRoomType());
            if (rooms_type != null) {
                rooms_type.add(room);
            }else{
                notFullRooms.put(room.getGoldRoomType(), new ArrayList<>());
            }
        }
    }

    public void removeFormNotFullRoom(Room room){
        //删除满房间，添加不满房间
        Map<Double, List<Room>> notFullRooms = notFullGoldRoom.get(room.getGameType());
        if (notFullRooms != null) {
            //删除满的房间
            List<Room> rooms_type = notFullRooms.get(room.getGoldRoomType());
            if (rooms_type != null) {

                rooms_type.remove(room);
            }
        }
        //加入未满的房间
        Map<Double, List<Room>> fullRooms = fullGoldRoom.get(room.getGameType());
        if (fullRooms != null) {
            List<Room> rooms_type = fullRooms.get(room.getGoldRoomType());
            if (rooms_type != null) {
                rooms_type.add(room);
            }else{
                fullRooms.put(room.getGoldRoomType(), new ArrayList<>());
            }
        }
    }

    public void addNotFullRoom(Room room) {
        List<Room> rooms = getNotFullRoom(room.getGameType(), room.getGoldRoomType());
        rooms.add(room);

    }


    public  void removeRoomFromMap(Room room){
        Map<Double, List<Room>> notFullRooms = notFullGoldRoom.get(room.getGameType());
//        Map<Double, List<Room>> fullRooms = notFullGoldRoom.get(room.getGameType());
        if (notFullRooms != null) {
            //删除满的房间
            List<Room> rooms_type = notFullRooms.get(room.getGoldRoomType());
            if (rooms_type != null) {
                rooms_type.remove(room);
            }
        }
    }

    public static void removeRoom(String roomId) {
        //本地内存删除room
        IfaceRoom room = getInstance().rooms.get(roomId);
        getInstance().rooms.remove(roomId);
        RedisManager.removeRoomAllInfo(roomId);
        //删除代开房
        if(room != null && room instanceof Room){
            Room rm = (Room)room;
            if(!rm.isCreaterJoin()){
                RedisManager.getUserRedisService().removePerpareRoom(rm.getCreateUser(), rm.getRoomId());
//                if (prepareRoom.containsKey(rm.getCreateUser())) {
//                    prepareRoom.get(rm.getCreateUser()).remove(rm.getRoomId());
//                }
            }
        }
        getInstance().robotRoom.remove(room);
    }

    public static void addRoom(String roomId,String serverId, Room room) {
//        if (room.isGoldRoom()){
//            if (room.getUsers().size() >= room.getPersonNumber()) {
//                //加入已满的
//                RoomManager.addRoom2Map(room);
//                //删掉未满的
//                RoomManager.removeRoomFromMap(room);
//            }else{
//                List<Room> list = notFullGoldRoom.get(room.getGoldRoomType());
//                if (list == null) {
//                    list = new ArrayList<>();
//                }
//                ArrayList<Room> templist = new ArrayList<>();
//                templist.addAll(list);
//                for (Room m:list) {
//                    if(m.getRoomId().equals(room.getRoomId())){
//                        templist.remove(m);
//                    }
//                }
//                templist.add(room);
//                notFullGoldRoom.put(room.getGoldRoomType(),templist);
//            }
//        }
        getInstance().rooms.put(roomId, room);
        getInstance().robotRoom.add(room);
        RedisManager.getRoomRedisService().setServerId(roomId,serverId);
        //加入代开房列表
        if(!room.isCreaterJoin()){
            RedisManager.getUserRedisService().addPerpareRoom(room.getCreateUser(), room.getPrepareRoomVo());
        }
    }

    public static Room getNullRoom(String gameType, Double goldRoomType){
        Map<Double, List<Room>> rooms = getInstance().notFullGoldRoom.get(gameType);
        if (rooms == null) {
            return null;
        }
        List<Room> list = rooms.get(goldRoomType);
        if (list!=null && list.size()>0){
            return list.get(0);
        }else {
            return null;
        }
    }

    public Map<String, Map<Double, List<Room>>> getFullGoldRoom() {
        return fullGoldRoom;
    }

    public RoomManager setFullGoldRoom(Map<String, Map<Double, List<Room>>> fullGoldRoom) {
        this.fullGoldRoom = fullGoldRoom;
        return this;
    }

    public Map<String, Map<Double, List<Room>>> getNotFullGoldRoom() {
        return notFullGoldRoom;
    }

    public RoomManager setNotFullGoldRoom(Map<String, Map<Double, List<Room>>> notFullGoldRoom) {
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
}
