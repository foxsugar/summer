package com.code.server.constant.game;

/**
 * Created by sunxianping on 2017/6/23.
 */
public class RoomStatistics {
    public long userId;
    public int winTime;
    public int failedTime;
    public double maxScore;

    public RoomStatistics() {
    }

    public RoomStatistics(long userId) {
        this.userId = userId;
    }
}
