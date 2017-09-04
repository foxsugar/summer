package com.code.server.constant.db;

/**
 * Created by sunxianping on 2017/5/26.
 */
public class UserInfo {
    private int totalPlayGameNumber;

    private int playGameTime = 0;
    private int shareWXCount = 0;
    private long lastShareTime = 0;

    public int getTotalPlayGameNumber() {
        return totalPlayGameNumber;
    }

    public UserInfo setTotalPlayGameNumber(int totalPlayGameNumber) {
        this.totalPlayGameNumber = totalPlayGameNumber;
        return this;
    }

    public int getPlayGameTime() {
        return playGameTime;
    }

    public UserInfo setPlayGameTime(int playGameTime) {
        this.playGameTime = playGameTime;
        return this;
    }

    public int getShareWXCount() {
        return shareWXCount;
    }

    public UserInfo setShareWXCount(int shareWXCount) {
        this.shareWXCount = shareWXCount;
        return this;
    }

    public long getLastShareTime() {
        return lastShareTime;
    }

    public UserInfo setLastShareTime(long lastShareTime) {
        this.lastShareTime = lastShareTime;
        return this;
    }
}
