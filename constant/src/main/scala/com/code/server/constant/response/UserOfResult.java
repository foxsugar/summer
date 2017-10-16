package com.code.server.constant.response;

import com.code.server.constant.game.RoomStatistics;

public class UserOfResult {

    private long userId;//id

    private String image; //头像 

    private String username;

    private String card;

    private int multiple;//倍数

    private String scores;//分数

    private int huNum;
    private int dianPaoNum;
    private int moBaoNum;
    private int lianZhuangNum;
    private long time;

    private int baoziNum;
    private int tonghuashunNum;
    private int tonghuaNum;
    private int shunziNum;
    private int duiziNum;
    private int sanpaiNum;

    private RoomStatistics roomStatistics;

    public long getUserId() {
        return userId;
    }

    public UserOfResult setUserId(long userId) {
        this.userId = userId;
        return this;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }


    public String getImage() {
        return image;
    }


    public void setImage(String image) {
        this.image = image;
    }


    public String getUsername() {
        return username;
    }


    public void setUsername(String username) {
        this.username = username;
    }


    public String getCard() {
        return card;
    }


    public void setCard(String card) {
        this.card = card;
    }


    public int getMultiple() {
        return multiple;
    }


    public void setMultiple(int multiple) {
        this.multiple = multiple;
    }


    public String getScores() {
        return scores;
    }


    public void setScores(String scores) {
        this.scores = scores;
    }

    public int getHuNum() {
        return huNum;
    }

    public UserOfResult setHuNum(int huNum) {
        this.huNum = huNum;
        return this;
    }

    public int getDianPaoNum() {
        return dianPaoNum;
    }

    public UserOfResult setDianPaoNum(int dianPaoNum) {
        this.dianPaoNum = dianPaoNum;
        return this;
    }

    public int getMoBaoNum() {
        return moBaoNum;
    }

    public UserOfResult setMoBaoNum(int moBaoNum) {
        this.moBaoNum = moBaoNum;
        return this;
    }

    public int getLianZhuangNum() {
        return lianZhuangNum;
    }

    public UserOfResult setLianZhuangNum(int lianZhuangNum) {
        this.lianZhuangNum = lianZhuangNum;
        return this;
    }

    public long getTime() {
        return time;
    }

    public UserOfResult setTime(long time) {
        this.time = time;
        return this;
    }

    public RoomStatistics getRoomStatistics() {
        return roomStatistics;
    }

    public UserOfResult setRoomStatistics(RoomStatistics roomStatistics) {
        this.roomStatistics = roomStatistics;
        return this;
    }

    public int getBaoziNum() {
        return baoziNum;
    }

    public void setBaoziNum(int baoziNum) {
        this.baoziNum = baoziNum;
    }

    public int getTonghuashunNum() {
        return tonghuashunNum;
    }

    public void setTonghuashunNum(int tonghuashunNum) {
        this.tonghuashunNum = tonghuashunNum;
    }

    public int getTonghuaNum() {
        return tonghuaNum;
    }

    public void setTonghuaNum(int tonghuaNum) {
        this.tonghuaNum = tonghuaNum;
    }

    public int getShunziNum() {
        return shunziNum;
    }

    public void setShunziNum(int shunziNum) {
        this.shunziNum = shunziNum;
    }

    public int getDuiziNum() {
        return duiziNum;
    }

    public void setDuiziNum(int duiziNum) {
        this.duiziNum = duiziNum;
    }

    public int getSanpaiNum() {
        return sanpaiNum;
    }

    public void setSanpaiNum(int sanpaiNum) {
        this.sanpaiNum = sanpaiNum;
    }
}