package com.code.server.game.mahjong.response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by T420 on 2016/12/1.
 */
public class ResultResp {
    private long winnerId = -1;
    private String baoCard;
    private List<PlayerCardsResp> userInfos;
    private Map<Long, Integer> laZhuang = new HashMap<>();
    private Map<Long, Boolean> laZhuangStatus = new HashMap<>();
    private List<Long> yipaoduoxiang;
    private String yu;
    private List<String> remainCards = new ArrayList<>();
    private long beginUser ;


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

    public Map<Long, Integer> getLaZhuang() {
        return laZhuang;
    }

    public ResultResp setLaZhuang(Map<Long, Integer> laZhuang) {
        this.laZhuang = laZhuang;
        return this;
    }

    public Map<Long, Boolean> getLaZhuangStatus() {
        return laZhuangStatus;
    }

    public ResultResp setLaZhuangStatus(Map<Long, Boolean> laZhuangStatus) {
        this.laZhuangStatus = laZhuangStatus;
        return this;
    }

    public List<Long> getYipaoduoxiang() {
        return yipaoduoxiang;
    }

    public ResultResp setYipaoduoxiang(List<Long> yipaoduoxiang) {
        this.yipaoduoxiang = yipaoduoxiang;
        return this;
    }

    public String getYu() {
        return yu;
    }

    public ResultResp setYu(String yu) {
        this.yu = yu;
        return this;
    }

    public List<String> getRemainCards() {
        return remainCards;
    }

    public ResultResp setRemainCards(List<String> remainCards) {
        this.remainCards = remainCards;
        return this;
    }

    public long getBeginUser() {
        return beginUser;
    }

    public ResultResp setBeginUser(long beginUser) {
        this.beginUser = beginUser;
        return this;
    }
}
