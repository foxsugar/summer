package com.code.server.game.poker.zhaguzi;

import com.code.server.constant.response.RoomVo;

/**
 * Created by dajuejinxian on 2018/6/25.
 */
public class RoomYSZVo extends RoomVo{

    protected double caiFen;
    protected int menPai;
    protected int cricleNumber;
//    protected long lastReadyTime;
    protected long timerTick;
    protected long leaveSecond;



    public double getCaiFen() {
        return caiFen;
    }

    public void setCaiFen(double caiFen) {
        this.caiFen = caiFen;
    }

    public int getMenPai() {
        return menPai;
    }

    public void setMenPai(int menPai) {
        this.menPai = menPai;
    }

    public int getCricleNumber() {
        return cricleNumber;
    }

    public void setCricleNumber(int cricleNumber) {
        this.cricleNumber = cricleNumber;
    }

    public long getTimerTick() {
        return timerTick;
    }

    public void setTimerTick(long timerTick) {
        this.timerTick = timerTick;
    }

    public long getLeaveSecond() {
        return leaveSecond;
    }

    public void setLeaveSecond(long leaveSecond) {
        this.leaveSecond = leaveSecond;
    }
}
