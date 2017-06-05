package com.code.server.game.mahjong.response;

import java.util.List;

/**
 * Created by T420 on 2016/12/1.
 */
public class ResultResp {
    private long winnerId = -1;
    private String baoCard;
    private List<PlayerCardsResp> userInfos;


    public String getBaoCard() {
        return baoCard;
    }

    public ResultResp setBaoCard(String baoCard) {
        this.baoCard = baoCard;
        return this;
    }

    public long getWinnerId() {
        return winnerId;
    }

    public ResultResp setWinnerId(long winnerId) {
        this.winnerId = winnerId;
        return this;
    }

    public List<PlayerCardsResp> getUserInfos() {
        return userInfos;
    }

    public void setUserInfos(List<PlayerCardsResp> userInfos) {
        this.userInfos = userInfos;
    }
}
