package com.code.server.game.mahjong.logic;

import java.util.List;


/**
 * Created by sunxianping on 2017/5/16.
 */
public class PlayerCardsInfoSZ_LQ extends PlayerCardsInfoSZ {



    protected int getNeedFan(int addFan) {
       return TING_MIN_FAN;
    }
    @Override
    public void init(List<String> cards) {
        super.init(cards);
        specialHuScore.remove(hu_门清);
        this.isHasGangBlackList = false;

    }


    @Override
    public void huCompute(RoomInfo room, GameInfo gameInfo, boolean isZimo, long dianpaoUser, String card) {
        specialHuScore.put(hu_门清,1);
        super.huCompute(room, gameInfo, isZimo, dianpaoUser, card);
    }
}
