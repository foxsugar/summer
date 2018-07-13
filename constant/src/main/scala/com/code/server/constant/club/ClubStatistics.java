package com.code.server.constant.club;

/**
 * Created by sunxianping on 2018/1/30.
 */
public class ClubStatistics {

    private int openNum;
    private int completeNum;
    private int winnerNum;
    private int consumeNum;
    private double allScore;

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
}
