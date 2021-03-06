package com.code.server.game.poker.yuxiaxie;

import com.code.server.constant.response.RoomVo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunxianping on 2018-12-12.
 */
public class RoomYuxiaxieVo extends RoomVo{

    //单压限分
    private int danya;
    //串联限分
    private int chuanlian;
    //豹子限分
    private int baozi;


    //挪次数
    private int nuo;

    private long betRemainTime;


    List<List<Integer>> diceHistory = new ArrayList<>();


    public int getDanya() {
        return danya;
    }

    public RoomYuxiaxieVo setDanya(int danya) {
        this.danya = danya;
        return this;
    }

    public int getChuanlian() {
        return chuanlian;
    }

    public RoomYuxiaxieVo setChuanlian(int chuanlian) {
        this.chuanlian = chuanlian;
        return this;
    }

    public int getBaozi() {
        return baozi;
    }

    public RoomYuxiaxieVo setBaozi(int baozi) {
        this.baozi = baozi;
        return this;
    }

    public int getNuo() {
        return nuo;
    }

    public RoomYuxiaxieVo setNuo(int nuo) {
        this.nuo = nuo;
        return this;
    }

    @Override
    public long getRemainTime() {
        return remainTime;
    }

    @Override
    public RoomYuxiaxieVo setRemainTime(long remainTime) {
        this.remainTime = remainTime;
        return this;
    }

    public long getBetRemainTime() {
        return betRemainTime;
    }

    public RoomYuxiaxieVo setBetRemainTime(long betRemainTime) {
        this.betRemainTime = betRemainTime;
        return this;
    }

    public List<List<Integer>> getDiceHistory() {
        return diceHistory;
    }

    public RoomYuxiaxieVo setDiceHistory(List<List<Integer>> diceHistory) {
        this.diceHistory = diceHistory;
        return this;
    }
}
