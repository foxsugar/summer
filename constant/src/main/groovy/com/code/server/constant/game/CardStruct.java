package com.code.server.constant.game;

import java.util.List;


/**
 * Created by sunxianping on 2017/3/14.
 */
public class CardStruct {

    public static final int type_单 = 1;
    public static final int type_对 = 2;
    public static final int type_三 = 3;
    public static final int type_三带单 = 4;
    public static final int type_三带对 = 5;
    public static final int type_顺 = 6;
    public static final int type_连对 = 7;
    public static final int type_飞机 = 8;
    public static final int type_飞机带翅膀_单 = 9;
    public static final int type_飞机带翅膀_对 = 10;
    public static final int type_四带二 = 11;
    public static final int type_炸 =12;
    public static final int type_火箭 = 13;

    public static final int type_飞机带翅膀 = 20;


    int outCard = 0;//默认是出牌  0
    long userid;//当前出牌的人
    long nextUserId;//下一个要出牌的人
    List<Integer> cards;
    int type;
    List<Integer> dan;// 单
    List<Integer> dui; //对
    List<Integer> san;  //三
    List<Integer> zha;  //炸
    List<Integer> feiji; //飞机
    List<Integer> shun; //顺
    List<Integer> liandui; //连对
    List<Integer> sandaidan; //三带一
    List<Integer> sandaidui; //三带二
    List<Integer> sidaier; //四带二
    List<Integer> feiji_chibang_dan;//飞机带翅膀_单
    List<Integer> feiji_chibang_dui;//飞机带翅膀_对
    List<Integer> huojian; //火箭

    public  List<Integer> getByTypeList(int type){
        if(type == type_单){
            return dan;
        }else if(type == type_对){
            return dui;
        }else if(type == type_三){
            return san;
        }else if(type == type_三带单){
            return sandaidan;
        }else if(type == type_三带对){
            return sandaidui;
        }else if(type == type_顺){
            return shun;
        }else if(type == type_连对){
            return liandui;
        }else if(type == type_飞机){
            return feiji;
        }else if(type == type_飞机带翅膀_单){
            return feiji_chibang_dan;
        }else if(type == type_飞机带翅膀_对){
            return feiji_chibang_dui;
        }else if(type == type_四带二){
            return sidaier;
        }else if(type == type_炸){
            return zha;
        }else if(type == type_火箭){
            return huojian;
        }else{
            return null;
        }
    }

    @Override
    public String toString() {
        return "CardStruct{" +
                "outCard=" + outCard +
                ", userid=" + userid +
                ", nextUserId=" + nextUserId +
                ", cards=" + cards +
                ", type=" + type +
                ", dan=" + dan +
                ", dui=" + dui +
                ", san=" + san +
                ", zha=" + zha +
                ", feiji=" + feiji +
                ", shun=" + shun +
                ", liandui=" + liandui +
                ", sandaidan=" + sandaidan +
                ", sandaidui=" + sandaidui +
                ", sidaier=" + sidaier +
                ", feiji_chibang_dan=" + feiji_chibang_dan +
                ", feiji_chibang_dui=" + feiji_chibang_dui +
                ", huojian=" + huojian +
                '}';
    }

    public int getType(){
        return type;
    }
    public int getOutCard (){
        return outCard;
    }
    public void setOutCard(int card){
        this.outCard = card;
    }

    public long getUserid() {
        return userid;
    }

    public void setUserid(long userid) {
        this.userid = userid;
    }

    public void setNextUserId(long nextUserId){this.nextUserId = nextUserId;}
    public long getNextUserId(){return this.nextUserId;}
    public List<Integer> getCards(){return this.cards;}

    public List<Integer> getDan() {
        return dan;
    }

    public CardStruct setDan(List<Integer> dan) {
        this.dan = dan;
        return this;
    }

    public List<Integer> getDui() {
        return dui;
    }

    public CardStruct setDui(List<Integer> dui) {
        this.dui = dui;
        return this;
    }

    public List<Integer> getSan() {
        return san;
    }

    public CardStruct setSan(List<Integer> san) {
        this.san = san;
        return this;
    }

    public List<Integer> getZha() {
        return zha;
    }

    public CardStruct setZha(List<Integer> zha) {
        this.zha = zha;
        return this;
    }

    public List<Integer> getFeiji() {
        return feiji;
    }

    public CardStruct setFeiji(List<Integer> feiji) {
        this.feiji = feiji;
        return this;
    }

    public List<Integer> getShun() {
        return shun;
    }

    public CardStruct setShun(List<Integer> shun) {
        this.shun = shun;
        return this;
    }

    public List<Integer> getLiandui() {
        return liandui;
    }

    public CardStruct setLiandui(List<Integer> liandui) {
        this.liandui = liandui;
        return this;
    }

    public List<Integer> getSandaidan() {
        return sandaidan;
    }

    public CardStruct setSandaidan(List<Integer> sandaidan) {
        this.sandaidan = sandaidan;
        return this;
    }

    public List<Integer> getSandaidui() {
        return sandaidui;
    }

    public CardStruct setSandaidui(List<Integer> sandaidui) {
        this.sandaidui = sandaidui;
        return this;
    }

    public List<Integer> getSidaier() {
        return sidaier;
    }

    public CardStruct setSidaier(List<Integer> sidaier) {
        this.sidaier = sidaier;
        return this;
    }

    public List<Integer> getFeiji_chibang_dan() {
        return feiji_chibang_dan;
    }

    public CardStruct setFeiji_chibang_dan(List<Integer> feiji_chibang_dan) {
        this.feiji_chibang_dan = feiji_chibang_dan;
        return this;
    }

    public List<Integer> getFeiji_chibang_dui() {
        return feiji_chibang_dui;
    }

    public CardStruct setFeiji_chibang_dui(List<Integer> feiji_chibang_dui) {
        this.feiji_chibang_dui = feiji_chibang_dui;
        return this;
    }

    public List<Integer> getHuojian() {
        return huojian;
    }

    public CardStruct setHuojian(List<Integer> huojian) {
        this.huojian = huojian;
        return this;
    }
}