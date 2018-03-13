package com.code.server.constant.db;

/**
 * Created by sunxianping on 2018/2/28.
 */
public class OnlineInfo {

    private int user;
    private int room;

    public int getUser() {
        return user;
    }

    public OnlineInfo setUser(int user) {
        this.user = user;
        return this;
    }

    public int getRoom() {
        return room;
    }

    public OnlineInfo setRoom(int room) {
        this.room = room;
        return this;
    }
}
