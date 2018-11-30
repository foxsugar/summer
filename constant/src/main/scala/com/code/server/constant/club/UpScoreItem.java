package com.code.server.constant.club;

/**
 * Created by sunxianping on 2018-11-28.
 */
public class UpScoreItem {
    private long srcUserId;
    private String name;
    private long desUserId;
    private long time;
    private int num;
    private int type;



    public long getSrcUserId() {
        return srcUserId;
    }

    public UpScoreItem setSrcUserId(long srcUserId) {
        this.srcUserId = srcUserId;
        return this;
    }

    public String getName() {
        return name;
    }

    public UpScoreItem setName(String name) {
        this.name = name;
        return this;
    }

    public long getDesUserId() {
        return desUserId;
    }

    public UpScoreItem setDesUserId(long desUserId) {
        this.desUserId = desUserId;
        return this;
    }

    public long getTime() {
        return time;
    }

    public UpScoreItem setTime(long time) {
        this.time = time;
        return this;
    }

    public int getNum() {
        return num;
    }

    public UpScoreItem setNum(int num) {
        this.num = num;
        return this;
    }

    public int getType() {
        return type;
    }

    public UpScoreItem setType(int type) {
        this.type = type;
        return this;
    }
}
