package com.code.server.constant.response;

import com.code.server.constant.game.RoomStatistics;

import java.util.Map;

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
    private int zimoNum;
    private int jiePaoNum;
    private int mingGangNum;
    private int anGangNum;
    private long time;

    private int baoziNum;
    private int tonghuashunNum;
    private int tonghuaNum;
    private int shunziNum;
    private int duiziNum;
    private int sanpaiNum;

    //cow
    private int allWinNum;
    private int allLoseNum;
    private int cowCowNum;
    private int nullCowNum;
    private int winNum;

    //选齐齐
    private int numThree;
    private int numFive;
    private int numSix;

    private int winNumXQQ;

    private Map<Integer,Integer> historyScore;



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

    public int getAllWinNum() {
        return allWinNum;
    }

    public void setAllWinNum(int allWinNum) {
        this.allWinNum = allWinNum;
    }

    public int getAllLoseNum() {
        return allLoseNum;
    }

    public void setAllLoseNum(int allLoseNum) {
        this.allLoseNum = allLoseNum;
    }

    public int getCowCowNum() {
        return cowCowNum;
    }

    public void setCowCowNum(int cowCowNum) {
        this.cowCowNum = cowCowNum;
    }

    public int getNullCowNum() {
        return nullCowNum;
    }

    public void setNullCowNum(int nullCowNum) {
        this.nullCowNum = nullCowNum;
    }

    public int getWinNum() {
        return winNum;
    }

    public void setWinNum(int winNum) {
        this.winNum = winNum;
    }

    public int getNumThree() {
        return numThree;
    }

    public void setNumThree(int numThree) {
        this.numThree = numThree;
    }

    public int getNumFive() {
        return numFive;
    }

    public void setNumFive(int numFive) {
        this.numFive = numFive;
    }

    public int getNumSix() {
        return numSix;
    }

    public void setNumSix(int numSix) {
        this.numSix = numSix;
    }

    public int getZimoNum() {
        return zimoNum;
    }

    public UserOfResult setZimoNum(int zimoNum) {
        this.zimoNum = zimoNum;
        return this;
    }

    public int getJiePaoNum() {
        return jiePaoNum;
    }

    public UserOfResult setJiePaoNum(int jiePaoNum) {
        this.jiePaoNum = jiePaoNum;
        return this;
    }

    public int getMingGangNum() {
        return mingGangNum;
    }

    public UserOfResult setMingGangNum(int mingGangNum) {
        this.mingGangNum = mingGangNum;
        return this;
    }

    public int getAnGangNum() {
        return anGangNum;
    }

    public UserOfResult setAnGangNum(int anGangNum) {
        this.anGangNum = anGangNum;
        return this;
    }

    public int getWinNumXQQ() {
        return winNumXQQ;
    }

    public void setWinNumXQQ(int winNumXQQ) {
        this.winNumXQQ = winNumXQQ;
    }

    public Map<Integer, Integer> getHistoryScore() {
        return historyScore;
    }

    public UserOfResult setHistoryScore(Map<Integer, Integer> historyScore) {
        this.historyScore = historyScore;
        return this;
    }
}