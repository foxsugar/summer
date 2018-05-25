package com.code.server.game.poker.zhaguzi;

import com.code.server.constant.response.RoomVo;

/**
 * Created by dajuejinxian on 2018/5/7.
 */
public class RoomZhaGuZiVo extends RoomVo {
    //第一个发牌的人的Id
    protected long lastWinnderId;
    protected String showCard;

    public long getLastWinnderId() {
        return lastWinnderId;
    }

    public void setLastWinnderId(long lastWinnderId) {
        this.lastWinnderId = lastWinnderId;
    }

    public String getShowCard() {
        return showCard;
    }

    public void setShowCard(String showCard) {
        this.showCard = showCard;
    }
}
