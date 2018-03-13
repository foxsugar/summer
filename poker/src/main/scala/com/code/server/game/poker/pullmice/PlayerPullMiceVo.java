package com.code.server.game.poker.pullmice;

import com.code.server.constant.response.IfacePlayerInfoVo;

import java.util.ArrayList;
import java.util.List;

public class PlayerPullMiceVo implements IfacePlayerInfoVo {

    private long userId;

    private List<Integer> cards = new ArrayList<>();

    private long point;

    //座位号
    private int seat;

    //发牌顺序
    private int pxId;

    //是不是弃牌了
    private boolean isEscape;

    //分数
    private long score;

    private boolean isWinner;

    private List<Bet> betList = new ArrayList<>();

    private boolean alreadyFeng;

    public boolean isEscape() {
        return isEscape;
    }

    public void setEscape(boolean escape) {
        isEscape = escape;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }

    public boolean isWinner() {
        return isWinner;
    }

    public void setWinner(boolean winner) {
        isWinner = winner;
    }

    public List<Bet> getBetList() {
        return betList;
    }

    public void setBetList(List<Bet> betList) {
        this.betList = betList;
    }

    public boolean isAlreadyFeng() {
        return alreadyFeng;
    }

    public void setAlreadyFeng(boolean alreadyFeng) {
        this.alreadyFeng = alreadyFeng;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public List<Integer> getCards() {
        return cards;
    }

    public void setCards(List<Integer> cards) {
        this.cards = cards;
    }

    public long getPoint() {
        return point;
    }

    public void setPoint(long point) {
        this.point = point;
    }

    public int getSeat() {
        return seat;
    }

    public void setSeat(int seat) {
        this.seat = seat;
    }

    public int getPxId() {
        return pxId;
    }

    public void setPxId(int pxId) {
        this.pxId = pxId;
    }


    @Override
    public String toString() {
        return "PlayerPullMiceVo{" +
                "userId=" + userId +
                ", cards=" + cards +
                ", point=" + point +
                ", seat=" + seat +
                ", pxId=" + pxId +
                ", isEscape=" + isEscape +
                ", score=" + score +
                ", isWinner=" + isWinner +
                ", betList=" + betList +
                ", alreadyFeng=" + alreadyFeng +
                '}';
    }
}
