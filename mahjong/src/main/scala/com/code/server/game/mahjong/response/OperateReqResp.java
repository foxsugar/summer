package com.code.server.game.mahjong.response;

import java.util.List;

/**
 * Created by win7 on 2016/12/2.
 */
public class OperateReqResp {
    public static final int type_gang = 1;
    public static final int type_peng = 2;
    public static final int type_ting = 3;
    public static final int type_hu = 4;
    public static final int type_chi = 5;
    public static final int type_chi_ting = 6;
    public static final int type_peng_ting = 7;
    public static final int type_dan = 8;
    public static final int type_xuanfengdan = 9;
    public static final int type_mopai = 10;
    public static final int type_play = 11;
    public static final int type_yipaoduoxiang = 12;//一炮多响
    public static final int type_bufeng = 13;//一炮多响





    private int operateType = 0;
    private String card;
    private long fromUserId;
    private long userId;
    private boolean isMing;
    private List<String> chiCards;
    private List<String> xuanfengCards;
    private List<Long> yipaoduoxiangUser;

    public int getOperateType() {
        return operateType;
    }

    public void setOperateType(int operateType) {
        this.operateType = operateType;
    }

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }

    public long getFromUserId() {
        return fromUserId;
    }

    public OperateReqResp setFromUserId(long fromUserId) {
        this.fromUserId = fromUserId;
        return this;
    }

    public long getUserId() {
        return userId;
    }

    public OperateReqResp setUserId(long userId) {
        this.userId = userId;
        return this;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isMing() {
        return isMing;
    }

    public void setIsMing(boolean isMing) {
        this.isMing = isMing;
    }

    public List<String> getChiCards() {
        return chiCards;
    }

    public OperateReqResp setChiCards(List<String> chiCards) {
        this.chiCards = chiCards;
        return this;
    }

    public List<String> getXuanfengCards() {
        return xuanfengCards;
    }

    public OperateReqResp setXuanfengCards(List<String> xuanfengCards) {
        this.xuanfengCards = xuanfengCards;
        return this;
    }

    public List<Long> getYipaoduoxiangUser() {
        return yipaoduoxiangUser;
    }

    public OperateReqResp setYipaoduoxiangUser(List<Long> yipaoduoxiangUser) {
        this.yipaoduoxiangUser = yipaoduoxiangUser;
        return this;
    }
}
