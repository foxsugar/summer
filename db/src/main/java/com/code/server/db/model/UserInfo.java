package com.code.server.db.model;

/**
 * Created by sunxianping on 2017/4/1.
 */
public class UserInfo {

    private int totalPlayGameNumber;

    private int playGameTime = 0;

    public UserInfo(){}

    public int getTotalPlayGameNumber() {
        return totalPlayGameNumber;
    }

    public void setTotalPlayGameNumber(int totalPlayGameNumber) {
        this.totalPlayGameNumber = totalPlayGameNumber;
    }

    public int getPlayGameTime() {
        return playGameTime;
    }

    public void setPlayGameTime(int playGameTime) {
        this.playGameTime = playGameTime;
    }
}
