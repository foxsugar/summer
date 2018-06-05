package com.code.server.game.poker.service;

import com.code.server.game.room.Room;

/**
 * Created by sunxianping on 2018/6/5.
 */
public class PokerGoldRoomFactory {

    public static Room create(long userId, String roomType, String gameType, int goldRoomType) {

        return new Room();
    }
}
