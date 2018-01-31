package com.code.server.constant.game;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunxianping on 2017/9/6.
 */
public class RoomRecord {
    String type;
    long time;
    long id;
    String roomId;
    List<UserRecord> records = new ArrayList<>();
    String clubId;
    String clubRoomModel;
    String name;

    public void addRecord(UserRecord userRecord) {
        records.add(userRecord);
    }

    public String getType() {
        return type;
    }

    public RoomRecord setType(String type) {
        this.type = type;
        return this;
    }

    public long getTime() {
        return time;
    }

    public RoomRecord setTime(long time) {
        this.time = time;
        return this;
    }

    public List<UserRecord> getRecords() {
        return records;
    }

    public RoomRecord setRecords(List<UserRecord> records) {
        this.records = records;
        return this;
    }

    public long getId() {
        return id;
    }

    public RoomRecord setId(long id) {
        this.id = id;
        return this;
    }

    public String getRoomId() {
        return roomId;
    }

    public RoomRecord setRoomId(String roomId) {
        this.roomId = roomId;
        return this;
    }

    public String getClubId() {
        return clubId;
    }

    public RoomRecord setClubId(String clubId) {
        this.clubId = clubId;
        return this;
    }

    public String getClubRoomModel() {
        return clubRoomModel;
    }

    public RoomRecord setClubRoomModel(String clubRoomModel) {
        this.clubRoomModel = clubRoomModel;
        return this;
    }

    public String getName() {
        return name;
    }

    public RoomRecord setName(String name) {
        this.name = name;
        return this;
    }
}
