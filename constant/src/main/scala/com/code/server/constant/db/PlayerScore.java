package com.code.server.constant.db;

/**
 * Created by sunxianping on 2019-07-01.
 */
public class PlayerScore {
    private long userId;
    private double winNum;
    private double loseNum;
    private String name;
    private String image;

    public long getUserId() {
        return userId;
    }

    public PlayerScore setUserId(long userId) {
        this.userId = userId;
        return this;
    }

    public double getWinNum() {
        return winNum;
    }

    public PlayerScore setWinNum(double winNum) {
        this.winNum = winNum;
        return this;
    }

    public double getLoseNum() {
        return loseNum;
    }

    public PlayerScore setLoseNum(double loseNum) {
        this.loseNum = loseNum;
        return this;
    }

    public String getName() {
        return name;
    }

    public PlayerScore setName(String name) {
        this.name = name;
        return this;
    }

    public String getImage() {
        return image;
    }

    public PlayerScore setImage(String image) {
        this.image = image;
        return this;
    }
}
