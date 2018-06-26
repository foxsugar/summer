package com.code.server.game.mahjong.response;

/**
 * Created by T420 on 2016/12/1.
 */
public class PlayCardResp {
    private long userId;
    private String card;
    private boolean isAuto;

    public long getUserId() {
        return userId;
    }

    public PlayCardResp setUserId(long userId) {
        this.userId = userId;
        return this;
    }

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }

    public boolean isAuto() {
        return isAuto;
    }

    public PlayCardResp setAuto(boolean auto) {
        isAuto = auto;
        return this;
    }
}
