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
    private int sex;
    //俱乐部计分
    private double money;
    //推荐人
    private long referrer;
    //统计总和
    private ClubStatistics allStatistics = new ClubStatistics();

    private boolean canJoinGame = true;

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

    public int getSex() {
        return sex;
    }

    public ClubMember setSex(int sex) {
        this.sex = sex;
        return this;
    }

    public double getMoney() {
        return money;
    }

    public ClubMember setMoney(double money) {
        this.money = money;
        return this;
    }

    public long getReferrer() {
        return referrer;
    }

    public ClubMember setReferrer(long referrer) {
        this.referrer = referrer;
        return this;
    }

    public ClubStatistics getAllStatistics() {
        return allStatistics;
    }

    public ClubMember setAllStatistics(ClubStatistics allStatistics) {
        this.allStatistics = allStatistics;
        return this;
    }

    public boolean isCanJoinGame() {
        return canJoinGame;
    }

    public ClubMember setCanJoinGame(boolean canJoinGame) {
        this.canJoinGame = canJoinGame;
        return this;
    }
}
