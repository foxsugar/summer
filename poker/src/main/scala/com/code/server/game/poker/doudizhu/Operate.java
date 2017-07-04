package com.code.server.game.poker.doudizhu;

import com.code.server.constant.game.CardStruct;

/**
 * Created by sunxianping on 2017/7/4.
 */
public class Operate {

    private int type;
    private long userId;
    private CardStruct cardStruct;

    public int getType() {
        return type;
    }

    public Operate setType(int type) {
        this.type = type;
        return this;
    }

    public long getUserId() {
        return userId;
    }

    public Operate setUserId(long userId) {
        this.userId = userId;
        return this;
    }

    public CardStruct getCardStruct() {
        return cardStruct;
    }

    public Operate setCardStruct(CardStruct cardStruct) {
        this.cardStruct = cardStruct;
        return this;
    }
}
