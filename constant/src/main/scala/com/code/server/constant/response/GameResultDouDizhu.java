package com.code.server.constant.response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sunxianping on 2017/3/27.
 */
public class GameResultDouDizhu {
    public List<IfacePlayerInfoVo> playerCardInfos = new ArrayList<>();
    public boolean isDizhuWin;
    public boolean isSpring;
    public int multiple;
    public boolean isReopen;
    public double goldRoomType;
    public List<Integer> tableCards = new ArrayList<>();
    public Map<Long, Double> userScores = new HashMap<>();

    public List<IfacePlayerInfoVo> getPlayerCardInfos() {
        return playerCardInfos;
    }

    public GameResultDouDizhu setPlayerCardInfos(List<IfacePlayerInfoVo> playerCardInfos) {
        this.playerCardInfos = playerCardInfos;
        return this;
    }

    public boolean isSpring() {
        return isSpring;
    }

    public GameResultDouDizhu setSpring(boolean spring) {
        isSpring = spring;
        return this;
    }

    public int getMultiple() {
        return multiple;
    }

    public GameResultDouDizhu setMultiple(int multiple) {
        this.multiple = multiple;
        return this;
    }

    public boolean isDizhuWin() {
        return isDizhuWin;
    }

    public GameResultDouDizhu setDizhuWin(boolean dizhuWin) {
        isDizhuWin = dizhuWin;
        return this;
    }

    public boolean isReopen() {
        return isReopen;
    }

    public GameResultDouDizhu setReopen(boolean reopen) {
        isReopen = reopen;
        return this;
    }

    public List<Integer> getTableCards() {
        return tableCards;
    }

    public GameResultDouDizhu setTableCards(List<Integer> tableCards) {
        this.tableCards = tableCards;
        return this;
    }

    public Map<Long, Double> getUserScores() {
        return userScores;
    }

    public GameResultDouDizhu setUserScores(Map<Long, Double> userScores) {
        this.userScores = userScores;
        return this;
    }

    public double getGoldRoomType() {
        return goldRoomType;
    }

    public void setGoldRoomType(double goldRoomType) {
        this.goldRoomType = goldRoomType;
    }
}
