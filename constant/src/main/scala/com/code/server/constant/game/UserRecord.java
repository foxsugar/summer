package com.code.server.constant.game;

/**
 * Created by sunxianping on 2017/9/6.
 */
public class UserRecord {
    public UserRecord() {
    }

    private long userId;
    private String name;
    private double score;
    private String roomId;
    private String image;


    public long getUserId() {
        return userId;
    }

    public UserRecord setUserId(long userId) {
        this.userId = userId;
        return this;
    }

    public String getName() {
        return name;
    }

    public UserRecord setName(String name) {
        this.name = name;
        return this;
    }

    public double getScore() {
        return score;
    }

    public UserRecord setScore(double score) {
        this.score = score;
        return this;
    }

    public String getRoomId() {
        return roomId;
    }

    public UserRecord setRoomId(String roomId) {
        this.roomId = roomId;
        return this;
    }

    public String getImage() {
        return image;
    }

    public UserRecord setImage(String image) {
        this.image = image;
        return this;
    }
}
