package com.code.server.constant.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sunxianping on 2017/3/24.
 */
public class Record {
    private static final int MAX_SIZE = 20;

    private Map<String, List<RoomRecord>> roomRecords = new HashMap<>();


    public void addRoomRecord(RoomRecord roomRecord) {
        List<RoomRecord> list = roomRecords.get(roomRecord.type);
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(roomRecord);
        if (list.size() > MAX_SIZE) {
            list.remove(0);
        }
        roomRecords.put(roomRecord.type, list);
    }

    public static class RoomRecord {
        String type;
        long time;
        long id;
        List<UserRecord> records = new ArrayList<>();

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
    }

    public static class UserRecord {
        public UserRecord() {
        }

        private long userId;
        private String name;
        private double score;
        private String roomId;


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


    }

    public Map<String, List<RoomRecord>> getRoomRecords() {
        return roomRecords;
    }

    public Record setRoomRecords(Map<String, List<RoomRecord>> roomRecords) {
        this.roomRecords = roomRecords;
        return this;
    }
}
