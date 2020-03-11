package com.code.server.constant.response;

import java.util.Map;

/**
 * Created by sunxianping on 2017/6/12.
 */
public class RoomInfoVo extends RoomVo {

    public String modeTotal;
    public String mode;
    public String each;
    public boolean yipaoduoxiang;
    public boolean canChi;
    public boolean haveTing;
    private Map<Integer,Long> seatMap;

    public Map<Integer, Long> getSeatMap() {
        return seatMap;
    }

    public void setSeatMap(Map<Integer, Long> seatMap) {
        this.seatMap = seatMap;
    }

    public String getModeTotal() {
        return modeTotal;
    }

    public RoomInfoVo setModeTotal(String modeTotal) {
        this.modeTotal = modeTotal;
        return this;
    }

    public String getMode() {
        return mode;
    }

    public RoomInfoVo setMode(String mode) {
        this.mode = mode;
        return this;
    }

    public String getEach() {
        return each;
    }

    public RoomInfoVo setEach(String each) {
        this.each = each;
        return this;
    }

    public boolean isYipaoduoxiang() {
        return yipaoduoxiang;
    }

    public RoomInfoVo setYipaoduoxiang(boolean yipaoduoxiang) {
        this.yipaoduoxiang = yipaoduoxiang;
        return this;
    }

    public boolean isCanChi() {
        return canChi;
    }

    public RoomInfoVo setCanChi(boolean canChi) {
        this.canChi = canChi;
        return this;
    }

    public boolean isHaveTing() {
        return haveTing;
    }

    public RoomInfoVo setHaveTing(boolean haveTing) {
        this.haveTing = haveTing;
        return this;
    }
}
