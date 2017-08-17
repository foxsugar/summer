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

    private static Map<Double, List<Room>> fullGoldRoom = new HashMap<>();
    private static Map<Double,List<Room>> notFullGoldRoom = new HashMap<>();

    private static RoomManager ourInstance = new RoomManager();

    public static RoomManager getInstance() {
        return ourInstance;
    }

    private RoomManager() {
    }

    public static IfaceRoom getRoom(String roomId){
        return getInstance().rooms.get(roomId);
    }

    public static List<Room> getFullGoldRoom(Double goldRoomType){
        return getInstance().fullGoldRoom.get(goldRoomType);
    }

    public static List<Room> getNotFullGoldRoom(Double goldRoomType){
        return getInstance().notFullGoldRoom.get(goldRoomType);
    }

    public static Map<Double, List<Room>> getFullGoldRoom(){
        return getInstance().fullGoldRoom;
    }

    public static Map<Double, List<Room>> getNotFullGoldRoom(){
        return getInstance().notFullGoldRoom;
    }


    public static void addRoom2Map(Room room){
        List<Room> list = notFullGoldRoom.get(room.getGoldRoomType());
        if (list == null) {
            list = new ArrayList<>();
        }
        ArrayList<Room> templist = new ArrayList<>();
        templist.addAll(list);
        for (Room m:list) {
            if(m.getRoomId().equals(room.getRoomId())){
                templist.remove(m);
            }
        }
        templist.add(room);
        fullGoldRoom.put(room.getGoldRoomType(), templist);
    }


    //删除满房间，添加不满房间
    public static void removeRoom(Room room){
        List<Room> list = fullGoldRoom.get(room.getGoldRoomType());
        ArrayList<Room> templist = new ArrayList<>();
        templist.addAll(list);
        if (list != null) {
            templist.remove(room);
        }
        templist.add(room);
        fullGoldRoom.put(room.getGoldRoomType(),templist);

        List<Room> fulllist = notFullGoldRoom.get(room.getGoldRoomType());
        fulllist.add(room);
        notFullGoldRoom.put(room.getGoldRoomType(),fulllist);
    }


    public static void removeRoomFromMap(Room room){
        List<Room> list = notFullGoldRoom.get(room.getGoldRoomType());
        if (list != null) {
            list.remove(room);
        }
        notFullGoldRoom.put(room.getGoldRoomType(),list);
    }

    public static void removeRoom(String roomId) {
        //本地内存删除room
        getInstance().rooms.remove(roomId);
        RedisManager.removeRoomAllInfo(roomId);
    }

    public static void addRoom(String roomId,String serverId, Room room) {
        if (room.isGoldRoom()){
            if (room.getUsers().size() >= room.getPersonNumber()) {
                //加入已满的
                RoomManager.addRoom2Map(room);
                //删掉未满的
                RoomManager.removeRoomFromMap(room);
            }else{
                List<Room> list = notFullGoldRoom.get(room.getGoldRoomType());
                if (list == null) {
                    list = new ArrayList<>();
                }
                ArrayList<Room> templist = new ArrayList<>();
                templist.addAll(list);
                for (Room m:list) {
                    if(m.getRoomId().equals(room.getRoomId())){
                        templist.remove(m);
                    }
                }
                templist.add(room);
                notFullGoldRoom.put(room.getGoldRoomType(),templist);
            }
        }
        getInstance().rooms.put(roomId, room);
        RedisManager.getRoomRedisService().setServerId(roomId,serverId);
    }

    public static Room getNullRoom(Double roomType){
        List<Room> list = notFullGoldRoom.get(roomType);
        if (list!=null && list.size()>0){
            return list.get(0);
        }else {
            return null;
        }
    }
}
