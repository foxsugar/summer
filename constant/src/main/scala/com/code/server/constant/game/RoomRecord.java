package com.code.server.constant.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    String gameType;
    String modelTotal;
    String mode;
    int curGameNum;
    int allGameNum;
    long winnerId;


    Map<String, Object> otherInfo = new HashMap<>();


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

    public String getGameType() {
        return gameType;
    }

    public RoomRecord setGameType(String gameType) {
        this.gameType = gameType;
        return this;
    }

    public String getModelTotal() {
        return modelTotal;
    }

    public RoomRecord setModelTotal(String modelTotal) {
        this.modelTotal = modelTotal;
        return this;
    }

    public String getMode() {
        return mode;
    }

    public RoomRecord setMode(String mode) {
        this.mode = mode;
        return this;
    }

    public int getCurGameNum() {
        return curGameNum;
    }

    public RoomRecord setCurGameNum(int curGameNum) {
        this.curGameNum = curGameNum;
        return this;
    }

    public int getAllGameNum() {
        return allGameNum;
    }

    public RoomRecord setAllGameNum(int allGameNum) {
        this.allGameNum = allGameNum;
        return this;
    }

    public long getWinnerId() {
        return winnerId;
    }

    public RoomRecord setWinnerId(long winnerId) {
        this.winnerId = winnerId;
        return this;
    }

    public Map<String, Object> getOtherInfo() {
        return otherInfo;
    }

    public RoomRecord setOtherInfo(Map<String, Object> otherInfo) {
        this.otherInfo = otherInfo;
        return this;
    }
}
