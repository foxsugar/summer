package com.code.server.game.poker.yuxiaxie;

import com.code.server.game.room.Game;
import com.code.server.game.room.Room;

/**
 * Created by sunxianping on 2018-11-30.
 */
public class GameYuxiaxie extends Game {

    private RoomYuxiaxie room;



    /**
     * 是否有模式
     *
     * @param mode
     * @return
     */
    boolean isHasMode(int mode) {
        return Room.isHasMode(mode, this.room.getOtherMode());
    }
}
