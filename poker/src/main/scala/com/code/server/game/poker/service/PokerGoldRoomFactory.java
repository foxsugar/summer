package com.code.server.game.poker.service;

import com.code.server.constant.exception.DataNotFoundException;
import com.code.server.game.poker.doudizhu.RoomDouDiZhuGold;
import com.code.server.game.poker.doudizhu.RoomDouDiZhuPlus;
import com.code.server.game.poker.zhaguzi.RoomYSZ;
import com.code.server.game.room.IfaceRoom;
import com.code.server.game.room.Room;

/**
 * Created by sunxianping on 2018/6/5.
 */
public class PokerGoldRoomFactory {

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

//        val roomType = params.get("roomType").asText()
//        val gameNumber = params.get("gameNumber").asInt()
//        val personNumber = params.get("personNumber").asInt()
//        val cricleNumber = params.get("cricleNumber").asInt()
//        val multiple = params.get("multiple").asInt()
//        val caiFen = params.get("caiFen").asInt()
//        val menPai = params.get("menPai").asInt()
//
//        val gameType = params.path("gameType").asText("0")
//        val isAA = params.path("isAA").asBoolean(false)
//        val isJoin = params.path("isJoin").asBoolean(true)
//        val clubId = params.path("clubId").asText
//        val clubRoomModel = params.path("clubRoomModel").asText
//        val goldRoomType = params.path("goldRoomType").asInt(0)
//        val goldRoomPermission = params.path("goldRoomPermission").asInt(0)

        switch (gameType) {
            case "ysz":

                int gameNumber = 285;
                int personNumber = 5;
                int cricleNumber = 15;
                int multiple = 1;
                int fen = 0;
                int hidden = 0;
                boolean isAA = true;
                boolean isJoin = true;
                int goldRoomPermission = IfaceRoom.GOLD_ROOM_PERMISSION_DEFAULT;
                try {
                     int ret  = RoomYSZ.createYSZRoom(userId,gameNumber, personNumber, cricleNumber, multiple, fen, hidden,
                            gameType, roomType, isAA, isJoin, null, null, goldRoomType, goldRoomPermission, (RoomYSZ ro) -> ro);
                } catch (DataNotFoundException e) {
                    e.printStackTrace();
                }

        }

        return room;
    }
}
