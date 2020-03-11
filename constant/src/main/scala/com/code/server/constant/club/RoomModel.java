package com.code.server.constant.club;

import java.util.HashMap;
import java.util.Map;

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
    private String serviceName;
    private Map<String, ClubStatistics> statisticsMap = new HashMap<>();
    private int mode;
    private int floor;


    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

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

    public String getServiceName() {
        return serviceName;
    }

    public RoomModel setServiceName(String serviceName) {
        this.serviceName = serviceName;
        return this;
    }

    public Map<String, ClubStatistics> getStatisticsMap() {
        return statisticsMap;
    }

    public RoomModel setStatisticsMap(Map<String, ClubStatistics> statisticsMap) {
        this.statisticsMap = statisticsMap;
        return this;
    }

    public int getMode() {
        return mode;
    }

    public RoomModel setMode(int mode) {
        this.mode = mode;
        return this;
    }
}
