package com.code.server.game.mahjong.util;

import java.util.ArrayList;
import java.util.List;

import static com.code.server.game.mahjong.util.Hu.*;

/**
 * Created by sunxianping on 2017/8/18.
 */
public class CardGroup {

    public int huType;
    public int card;
    public int hunNum;

    //带一个混的顺结构
    public ShunHaveHun shunHaveHun;

    public CardGroup(int huType, int card) {
        this.card = card;
        this.huType = huType;
    }

    public CardGroup(int huType, int card, int hunNum) {
        this.huType = huType;
        this.card = card;
        this.hunNum = hunNum;
    }

    public CardGroup(int huType, int card, ShunHaveHun shunHaveHun) {
        this.huType = huType;
        this.card = card;
        this.shunHaveHun = shunHaveHun;
        this.hunNum = 1;
    }

    public int getHunReplaceCard() {
        if(this.huType == CARD_GROUP_TYPE_KE
                || this.huType == CARD_GROUP_TYPE_ONE_HUN_JIANG){
            return this.card;
        }
        if(this.huType == CARD_GROUP_TYPE_SHUN_ONE_HUN){
            List<Integer> shun = new ArrayList<>();
            shun.add(card);
            shun.add(card + 1);
            shun.add(card + 2);
            shun.removeAll(shunHaveHun.getOther());
            return shun.get(0);
        }
        return 0;
    }

    @Override
    public String toString() {
//        return "card"
        return "胡类型=" + huType +"|牌 = " + card;
    }


}
