package com.code.server.constant.response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sunxianping on 2017/8/17.
 */
public class RoomPaijiuVo extends RoomVo {
    //庄家设置的分
    public int bankerScore = 0;
    public int bankerInitScore = 0;


    public List<GamePaijiuResult> winnerIndex = new ArrayList<>();

    public Map winnerCountMap = new HashMap<>();

    public List bankerList = new ArrayList();
    public Map bankerScoreMap = new HashMap();
    public long paijiuRemainTime;

    public boolean alreadySet ;
    public boolean usePass;
    public int pass;




    public int getBankerScore() {
        return bankerScore;
    }

    public RoomPaijiuVo setBankerScore(int bankerScore) {
        this.bankerScore = bankerScore;
        return this;
    }

    public int getBankerInitScore() {
        return bankerInitScore;
    }

    public RoomPaijiuVo setBankerInitScore(int bankerInitScore) {
        this.bankerInitScore = bankerInitScore;
        return this;
    }

    public List<GamePaijiuResult> getWinnerIndex() {
        return winnerIndex;
    }

    public RoomPaijiuVo setWinnerIndex(List<GamePaijiuResult> winnerIndex) {
        this.winnerIndex = winnerIndex;
        return this;
    }

    public Map getWinnerCountMap() {
        return winnerCountMap;
    }

    public RoomPaijiuVo setWinnerCountMap(Map winnerCountMap) {
        this.winnerCountMap = winnerCountMap;
        return this;
    }

    public List getBankerList() {
        return bankerList;
    }

    public void setBankerList(List bankerList) {
        this.bankerList = bankerList;
    }

    public Map getBankerScoreMap() {
        return bankerScoreMap;
    }

    public void setBankerScoreMap(Map bankerScoreMap) {
        this.bankerScoreMap = bankerScoreMap;
    }

    public long getPaijiuRemainTime() {
        return paijiuRemainTime;
    }

    public void setPaijiuRemainTime(long paijiuRemainTime) {
        this.paijiuRemainTime = paijiuRemainTime;
    }

    public boolean isAlreadySet() {
        return alreadySet;
    }

    public RoomPaijiuVo setAlreadySet(boolean alreadySet) {
        this.alreadySet = alreadySet;
        return this;
    }

    public boolean isUsePass() {
        return usePass;
    }

    public RoomPaijiuVo setUsePass(boolean usePass) {
        this.usePass = usePass;
        return this;
    }

    public int getPass() {
        return pass;
    }

    public RoomPaijiuVo setPass(int pass) {
        this.pass = pass;
        return this;
    }
}
