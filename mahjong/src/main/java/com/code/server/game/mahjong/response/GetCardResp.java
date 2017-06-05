package com.code.server.game.mahjong.response;

import java.util.List;

/**
 * Created by win7 on 2016/12/1.
 */
public class GetCardResp {
    private long userId;
    private String card;
    private List<String> allCards;
    private int remainNum;

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }

    public long getUserId() {
        return userId;
    }

    public GetCardResp setUserId(long userId) {
        this.userId = userId;
        return this;
    }

    public List<String> getAllCards() {
        return allCards;
    }

    public void setAllCards(List<String> allCards) {
        this.allCards = allCards;
    }

    public int getRemainNum() {
        return remainNum;
    }

    public void setRemainNum(int remainNum) {
        this.remainNum = remainNum;
    }
}
