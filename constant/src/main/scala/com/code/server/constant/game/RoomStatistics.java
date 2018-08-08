package com.code.server.constant.game;

/**
 * Created by sunxianping on 2017/6/23.
 */
public class RoomStatistics {
    public long userId;
    public int winTime;
    public int failedTime;
    public double maxScore;
    public int winAllTime;
    public int loseAllTime;
    public String maxCardGroup;
    public String ext;

    public RoomStatistics() {
    }

    public RoomStatistics(long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "RoomStatistics{" +
                "userId=" + userId +
                ", winTime=" + winTime +
                ", failedTime=" + failedTime +
                ", maxScore=" + maxScore +
                ", winAllTime=" + winAllTime +
                ", loseAllTime=" + loseAllTime +
                ", maxCardGroup='" + maxCardGroup + '\'' +
                ", ext='" + ext + '\'' +
                '}';
    }
}
