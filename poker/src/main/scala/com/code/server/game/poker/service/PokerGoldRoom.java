package com.code.server.game.poker.service;

import com.code.server.constant.exception.DataNotFoundException;
import com.code.server.game.poker.config.ServerConfig;
import com.code.server.game.poker.doudizhu.RoomDouDiZhuGold;
import com.code.server.game.poker.doudizhu.RoomDouDiZhuPlus;
import com.code.server.game.poker.zhaguzi.RoomYSZ;
import com.code.server.game.room.IfaceRoom;
import com.code.server.game.room.Room;
import com.code.server.game.room.RoomExtendGold;
import com.code.server.game.room.service.RoomManager;
import com.code.server.util.SpringUtil;

/**
 * Created by sunxianping on 2018/6/5.
 */
public class PokerGoldRoom extends RoomExtendGold {


    @Override
    public Room getDefaultGoldRoomInstance(long userId, String roomType, String gameType, Integer goldRoomType) {
        Room room =  create(userId, roomType, gameType, goldRoomType);
        int serverId = SpringUtil.getBean(ServerConfig.class).getServerId();
        String roomId = Room.getRoomIdStr(Room.genRoomId(serverId));
        room.setRoomId(roomId);
        room.setGameType(gameType);
        room.setRoomType(roomType);
        room.setGoldRoomType(goldRoomType);
        room.setGoldRoomPermission(GOLD_ROOM_PERMISSION_DEFAULT);
        room.setMultiple(goldRoomType);




        return room;

    }


    public void add2GoldPool() {
        int serverId = SpringUtil.getBean(ServerConfig.class).getServerId();
        RoomManager.getInstance().addNotFullGoldRoom(this);
        RoomManager.addRoom(this.getRoomId(), "" + serverId, this);
    }

    public static Room create(long userId, String roomType, String gameType, int goldRoomType) {

        Room room = null;
        switch (roomType) {
            case "2":
                room = new RoomDouDiZhuGold();
                break;
            case "3":
                room = new RoomDouDiZhuPlus();
                break;


            default:

                break;
        }

        switch (gameType) {
            case "285":

                int gameNumber = 285;
                int personNumber = 5;
                int cricleNumber = 15;
                int multiple = goldRoomType;
                int fen = 0;
                int hidden = 0;
                boolean isAA = true;
                boolean isJoin = true;
                int goldRoomPermission = IfaceRoom.GOLD_ROOM_PERMISSION_DEFAULT;

                RoomYSZ roomYSZ = null;

                try {
                    roomYSZ = RoomYSZ.createYSZRoom_(userId, gameNumber, personNumber, cricleNumber, multiple, fen, hidden,
                            gameType, roomType, isAA, isJoin, null, null, goldRoomType, goldRoomPermission);
                } catch (DataNotFoundException e) {
                    e.printStackTrace();
                }


                return roomYSZ;
        }


        return room;
    }
}
