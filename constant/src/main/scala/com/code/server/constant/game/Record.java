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


    /**
     * 追加一条战绩,超过指定条数删除第一条
     * @param roomRecord
     * @return
     */
    public RoomRecord addRoomRecord(RoomRecord roomRecord) {
        RoomRecord result = null;
        List<RoomRecord> list = roomRecords.get(roomRecord.type);
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(roomRecord);
        if (list.size() > MAX_SIZE) {
            result = list.get(0);
            list.remove(0);
        }
        roomRecords.put(roomRecord.type, list);
        return result;
    }

    public Map<String, List<RoomRecord>> getRoomRecords() {
        return roomRecords;
    }

    public Record setRoomRecords(Map<String, List<RoomRecord>> roomRecords) {
        this.roomRecords = roomRecords;
        return this;
    }
}
