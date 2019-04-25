package com.code.server.constant.club;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by sunxianping on 2018/1/30.
 */
public class ClubStatistics {

    private int openNum;//开局数
    private int completeNum;
    private int winnerNum;
    private double winScore;
    private int loseNum;
    private double loseScore;
    private int consumeNum;//消耗数
    private double allScore;
    private int playerNum;
    private Set<Long> playerUser = new HashSet<>();
    private double playMinScore;

    public int getOpenNum() {
        return openNum;
    }

    public ClubStatistics setOpenNum(int openNum) {
        this.openNum = openNum;
        return this;
    }

    public int getCompleteNum() {
        return completeNum;
    }

    public ClubStatistics setCompleteNum(int completeNum) {
        this.completeNum = completeNum;
        return this;
    }

    public int getWinnerNum() {
        return winnerNum;
    }

    public ClubStatistics setWinnerNum(int winnerNum) {
        this.winnerNum = winnerNum;
        return this;
    }

    public int getConsumeNum() {
        return consumeNum;
    }

    public ClubStatistics setConsumeNum(int consumeNum) {
        this.consumeNum = consumeNum;
        return this;
    }

    public double getAllScore() {
        return allScore;
    }

    public ClubStatistics setAllScore(double allScore) {
        this.allScore = allScore;
        return this;
    }

    public int getPlayerNum() {
        return playerNum;
    }

    public ClubStatistics setPlayerNum(int playerNum) {
        this.playerNum = playerNum;
        return this;
    }

    public Set<Long> getPlayerUser() {
        return playerUser;
    }

    public ClubStatistics setPlayerUser(Set<Long> playerUser) {
        this.playerUser = playerUser;
        return this;
    }

    public double getWinScore() {
        return winScore;
    }

    public ClubStatistics setWinScore(double winScore) {
        this.winScore = winScore;
        return this;
    }

    public int getLoseNum() {
        return loseNum;
    }

    public ClubStatistics setLoseNum(int loseNum) {
        this.loseNum = loseNum;
        return this;
    }

    public double getLoseScore() {
        return loseScore;
    }

    public ClubStatistics setLoseScore(double loseScore) {
        this.loseScore = loseScore;
        return this;
    }

    public double getPlayMinScore() {
        return playMinScore;
    }

    public ClubStatistics setPlayMinScore(double playMinScore) {
        this.playMinScore = playMinScore;
        return this;
    }
}
