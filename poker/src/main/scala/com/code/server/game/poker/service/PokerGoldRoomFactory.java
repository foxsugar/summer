package com.code.server.game.poker.service;

import com.code.server.game.poker.doudizhu.RoomDouDiZhuGold;
import com.code.server.game.poker.doudizhu.RoomDouDiZhuPlus;
import com.code.server.game.poker.zhaguzi.RoomYSZ;
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

        switch (gameType) {
            case "285":
                RoomYSZ r = new RoomYSZ();
                r.setPersonNumber(5);
                r.setCaiFen(100);
                room = r;


        }

        return room;
    }
}
