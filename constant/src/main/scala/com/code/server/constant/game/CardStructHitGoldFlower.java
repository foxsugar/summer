package com.code.server.constant.game;

import java.util.List;


/**
 * Created by sunxianping on 2017/3/14.
 */
public class CardStructHitGoldFlower {

    public static final int type_三条 = 1;
    public static final int type_同花顺 = 2;
    public static final int type_同花 = 3;
    public static final int type_顺子 = 4;
    public static final int type_一对 = 5;
    public static final int type_单牌 = 6;
    public static final int type_235 = 7;//专杀三条


    public int outCard = 0;//默认是出牌  0
    public long userId;//当前出牌的人
    public long nextUserId;//下一个要出牌的人
    public List<Integer> cards;
    public int type;
    public List<Integer> sanTiao;//三条
    public List<Integer> tongHuaShun; //同花顺
    public List<Integer> tongHua;  //同花
    public List<Integer> shunZi;  //顺子
    public List<Integer> yiDui; //一对
    public List<Integer> danPai; //单牌
    public List<Integer> twoThreeFive; //235


    public  List<Integer> getByTypeList(int type){
        if(type == type_三条){
            return sanTiao;
        }else if(type == type_同花顺){
            return tongHuaShun;
        }else if(type == type_同花){
            return tongHua;
        }else if(type == type_顺子){
            return shunZi;
        }else if(type == type_一对){
            return yiDui;
        }else if(type == type_单牌){
            return danPai;
        }else if(type == type_235){
            return twoThreeFive;
        }else{
            return null;
        }
    }

    @Override
    public String toString() {
        return "CardStructHitGoldFlower{" +
                "outCard=" + outCard +
                ", userId=" + userId +
                ", nextUserId=" + nextUserId +
                ", cards=" + cards +
                ", type=" + type +
                ", sanTiao=" + sanTiao +
                ", tongHuaShun=" + tongHuaShun +
                ", tongHua=" + tongHua +
                ", shunZi=" + shunZi +
                ", yiDui=" + yiDui +
                ", danPai=" + danPai +
                ", twoThreeFive=" + twoThreeFive +
                '}';
    }

    public int getOutCard() {
        return outCard;
    }

    public void setOutCard(int outCard) {
        this.outCard = outCard;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getNextUserId() {
        return nextUserId;
    }

    public void setNextUserId(long nextUserId) {
        this.nextUserId = nextUserId;
    }

    public List<Integer> getCards() {
        return cards;
    }

    public void setCards(List<Integer> cards) {
        this.cards = cards;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<Integer> getSanTiao() {
        return sanTiao;
    }

    public void setSanTiao(List<Integer> sanTiao) {
        this.sanTiao = sanTiao;
    }

    public List<Integer> getTongHuaShun() {
        return tongHuaShun;
    }

    public void setTongHuaShun(List<Integer> tongHuaShun) {
        this.tongHuaShun = tongHuaShun;
    }

    public List<Integer> getTongHua() {
        return tongHua;
    }

    public void setTongHua(List<Integer> tongHua) {
        this.tongHua = tongHua;
    }

    public List<Integer> getShunZi() {
        return shunZi;
    }

    public void setShunZi(List<Integer> shunZi) {
        this.shunZi = shunZi;
    }

    public List<Integer> getYiDui() {
        return yiDui;
    }

    public void setYiDui(List<Integer> yiDui) {
        this.yiDui = yiDui;
    }

    public List<Integer> getDanPai() {
        return danPai;
    }

    public void setDanPai(List<Integer> danPai) {
        this.danPai = danPai;
    }

    public List<Integer> getTwoThreeFive() {
        return twoThreeFive;
    }

    public void setTwoThreeFive(List<Integer> twoThreeFive) {
        this.twoThreeFive = twoThreeFive;
    }
}