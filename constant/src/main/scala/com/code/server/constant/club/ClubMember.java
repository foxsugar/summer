package com.code.server.constant.club;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sunxianping on 2018/1/15.
 */
public class ClubMember {
    private long userId;
    private String name;
    private long time;
    private String mark;
    private String image;
    private boolean online;
    private Map<String,ClubStatistics> statistics= new HashMap<>();
    private String lastLoginTime;

    public long getUserId() {
        return userId;
    }

    public ClubMember setUserId(long userId) {
        this.userId = userId;
        return this;
    }

    public long getTime() {
        return time;
    }

    public ClubMember setTime(long time) {
        this.time = time;
        return this;
    }

    public String getMark() {
        return mark;
    }

    public ClubMember setMark(String mark) {
        this.mark = mark;
        return this;
    }

    public String getName() {
        return name;
    }

    public ClubMember setName(String name) {
        this.name = name;
        return this;
    }

    public String getImage() {
        return image;
    }

    public ClubMember setImage(String image) {
        this.image = image;
        return this;
    }

    public boolean isOnline() {
        return online;
    }

    public ClubMember setOnline(boolean online) {
        this.online = online;
        return this;
    }

    public Map<String, ClubStatistics> getStatistics() {
        return statistics;
    }

    public ClubMember setStatistics(Map<String, ClubStatistics> statistics) {
        this.statistics = statistics;
        return this;
    }

    public String getLastLoginTime() {
        return lastLoginTime;
    }

    public ClubMember setLastLoginTime(String lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
        return this;
    }
}
