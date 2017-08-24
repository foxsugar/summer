package com.code.server.game.mahjong.util;

/**
 * Created by sunxianping on 2017/8/18.
 */
public class CardGroup {
    public int huType;
    public int card;
    public int hunNum;

    public CardGroup(int huType, int card) {
        this.card = card;
        this.huType = huType;
    }

    public CardGroup(int huType, int card, int hunNum) {
        this.huType = huType;
        this.card = card;
        this.hunNum = hunNum;
    }

    @Override
    public String toString() {
//        return "card"
        return "胡类型=" + huType +"|牌 = " + card;
    }


}
