package com.code.server.constant.game;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunxianping on 2017/9/6.
 */
public class GameRecord {
    private int curGameNumber;
    List<UserRecord> records = new ArrayList<>();

    public int getCurGameNumber() {
        return curGameNumber;
    }

    public GameRecord setCurGameNumber(int curGameNumber) {
        this.curGameNumber = curGameNumber;
        return this;
    }

    public List<UserRecord> getRecords() {
        return records;
    }

    public GameRecord setRecords(List<UserRecord> records) {
        this.records = records;
        return this;
    }
}
