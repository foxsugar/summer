package com.code.server.game.mahjong.response;

/**
 * Created by T420 on 2016/12/1.
 */
public class PlayCardResp {
    private int userId;
    private String card;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }
}
