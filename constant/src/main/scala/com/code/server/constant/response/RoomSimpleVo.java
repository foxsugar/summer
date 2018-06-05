package com.code.server.constant.response;

/**
 * Created by sunxianping on 2018/6/5.
 */
public class RoomSimpleVo {
    public String roomType;
    public String gameType;
    public String roomId;
    public int goldRoomType;
    public int people;

    public String getGameType() {
        return gameType;
    }

    public RoomSimpleVo setGameType(String gameType) {
        this.gameType = gameType;
        return this;
    }

    public String getRoomType() {
        return roomType;
    }

    public RoomSimpleVo setRoomType(String roomType) {
        this.roomType = roomType;
        return this;
    }

    public String getRoomId() {
        return roomId;
    }

    public RoomSimpleVo setRoomId(String roomId) {
        this.roomId = roomId;
        return this;
    }

    public int getGoldRoomType() {
        return goldRoomType;
    }

    public RoomSimpleVo setGoldRoomType(int goldRoomType) {
        this.goldRoomType = goldRoomType;
        return this;
    }

    public int getPeople() {
        return people;
    }

    public RoomSimpleVo setPeople(int people) {
        this.people = people;
        return this;
    }
}
