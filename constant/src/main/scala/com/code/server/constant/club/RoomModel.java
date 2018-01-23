package com.code.server.constant.club;

/**
 * Created by sunxianping on 2018/1/15.
 */
public class RoomModel {
    private String id;
    private String createCommand;//创建命令
    private int money;
    private String name;
    private String desc;
    private long time;


    public String getId() {
        return id;
    }

    public RoomModel setId(String id) {
        this.id = id;
        return this;
    }

    public String getCreateCommand() {
        return createCommand;
    }

    public RoomModel setCreateCommand(String createCommand) {
        this.createCommand = createCommand;
        return this;
    }

    public int getMoney() {
        return money;
    }

    public RoomModel setMoney(int money) {
        this.money = money;
        return this;
    }

    public String getDesc() {
        return desc;
    }

    public RoomModel setDesc(String desc) {
        this.desc = desc;
        return this;
    }

    public long getTime() {
        return time;
    }

    public RoomModel setTime(long time) {
        this.time = time;
        return this;
    }

    public String getName() {
        return name;
    }

    public RoomModel setName(String name) {
        this.name = name;
        return this;
    }
}
