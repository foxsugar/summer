package com.code.server.game.poker.pullmice;

import com.code.server.constant.response.IfacePlayerInfoVo;
import com.code.server.game.room.IfacePlayerInfo;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

public class PlayerPullMice implements IfacePlayerInfo {

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

    public boolean isWinner() {
        return isWinner;
    }

    public void setWinner(boolean winner) {
        isWinner = winner;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }

    public boolean isEscape() {
        return isEscape;
    }

    public void setEscape(boolean escape) {
        isEscape = escape;
    }

    public boolean isAlreadyFeng() {
        return alreadyFeng;
    }

    public void setAlreadyFeng(boolean alreadyFeng) {
        this.alreadyFeng = alreadyFeng;
    }

    public List<Bet> getBetList() {
        return betList;
    }

    public void setBetList(List<Bet> betList) {
        this.betList = betList;
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

    public long getPoint() {
        return point;
    }

    public void setPoint(long point) {
        this.point = point;
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

    @Override
    public IfacePlayerInfoVo toVo() {
        PlayerPullMiceVo vo = new PlayerPullMiceVo();
        BeanUtils.copyProperties(this, vo);
        return vo;
    }

    @Override
    public IfacePlayerInfoVo toVo(long watchUser) {
        return null;
    }
}
