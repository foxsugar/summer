package com.code.server.game.mahjong.logic;

/**
 * Created by sunxianping on 2019-11-20.
 */
public class GameInfoLingchuan extends GameInfoNew {



    /**
     * 设置庄家
     *
     * @param winnerId
     */
    public void setBanker(long winnerId) {
        if (winnerId != this.getFirstTurn()) {

            room.setBankerId(nextTurnId(this.getFirstTurn()));
        }
    }

}
