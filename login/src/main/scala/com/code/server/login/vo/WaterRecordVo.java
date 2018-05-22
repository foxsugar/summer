package com.code.server.login.vo;

/**
 * Created by dajuejinxian on 2018/5/15.
 */

public class WaterRecordVo {

    private long uid;
    private String timeStamp;
    private String money;

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }


    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }
}
