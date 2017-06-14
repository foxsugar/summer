package com.code.server.constant.response;

/**
 * Created by sunxianping on 2017/6/12.
 */
public class RoomInfoVo extends RoomVo {

    public String modeTotal;
    public String mode;
    public String each;


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
}
