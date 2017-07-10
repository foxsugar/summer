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
        specialHuScore.remove(hu_清一色);
        specialHuScore.remove(hu_一条龙);
        specialHuScore.remove(hu_七小对);
        specialHuScore.remove(hu_豪华七小对);
        specialHuScore.remove(hu_双豪七小对_山西);
        specialHuScore.remove(hu_字一色);
        specialHuScore.remove(hu_清七对);
        specialHuScore.remove(hu_清龙);
        this.isHasGangBlackList = false;

    }


    @Override
    public void huCompute(RoomInfo room, GameInfo gameInfo, boolean isZimo, long dianpaoUser, String card) {
        specialHuScore.put(hu_门清,1);
        specialHuScore.put(hu_清一色,10);
        specialHuScore.put(hu_一条龙,10);
        specialHuScore.put(hu_七小对,10);
        specialHuScore.put(hu_豪华七小对,25);
        specialHuScore.put(hu_双豪七小对_山西,50);
        specialHuScore.put(hu_字一色,50);
        specialHuScore.put(hu_清七对,50);
        specialHuScore.put(hu_清龙,50);
        super.huCompute(room, gameInfo, isZimo, dianpaoUser, card);
    }
}
