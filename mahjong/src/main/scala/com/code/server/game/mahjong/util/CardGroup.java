package com.code.server.game.mahjong.util;

/**
 * Created by sunxianping on 2017/8/18.
 */
public class CardGroup {
    public static final int CARD_GROUP_TYPE_JIANG = 0;//将
    public static final int CARD_GROUP_TYPE_SHUN = 1;//顺
    public static final int CARD_GROUP_TYPE_KE = 2;//刻
    public static final int CARD_GROUP_TYPE_GANG = 3;//杠
    public static final int CARD_GROUP_TYPE_FENG_SHUN = 4;//风顺
    public static final int CARD_GROUP_TYPE_ZFB = 5;//中发白
    public static final int CARD_GROUP_TYPE_TWO_HUN = 6;//两个混
    public static final int CARD_GROUP_TYPE_THREE_HUN = 7;//三个混
    public static final int CARD_GROUP_TYPE_TWO_HUN_JIANG = 8;//两个 将

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
