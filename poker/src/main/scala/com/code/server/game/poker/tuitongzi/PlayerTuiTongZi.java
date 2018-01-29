package com.code.server.game.poker.tuitongzi;

import com.code.server.constant.response.IfacePlayerInfoVo;
import com.code.server.game.room.IfacePlayerInfo;

import java.util.ArrayList;
import java.util.List;

public class PlayerTuiTongZi implements IfacePlayerInfo {

    private long userId;
    private List<Integer> playerCards;
    private Bet bet;
    private long score;
    private int bankerScore;
    private boolean open;
    //牌型
    private long pattern;
    public PlayerTuiTongZi() {
    }

    public PlayerTuiTongZi(long userId, Integer card1, Integer card2) {
        this.userId = userId;
        this.playerCards = new ArrayList<Integer>();
        this.playerCards.add(card1);
        this.playerCards.add(card2);
    }

    public long getPattern() {
        return pattern;
    }

    public void setPattern(long pattern) {
        this.pattern = pattern;
    }

    public int getBankerScore() {
        return bankerScore;
    }

    public void setBankerScore(int bankerScore) {
        this.bankerScore = bankerScore;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public Bet getBet() {
        return bet;
    }

    public void setBet(Bet bet) {
        this.bet = bet;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public List<Integer> getPlayerCards() {
        return playerCards;
    }

    public void setPlayerCards(List<Integer> playerCards) {
        this.playerCards = playerCards;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }

    @Override
    public IfacePlayerInfoVo toVo() {

        PlayerTuiTongZiVo vo = new PlayerTuiTongZiVo();
        vo.setUserId(this.userId);
        vo.setPlayerCards(this.playerCards);
        vo.setScore(this.score);
        return vo;
    }

    @Override
    public IfacePlayerInfoVo toVo(long watchUser) {

        PlayerTuiTongZiVo vo = new PlayerTuiTongZiVo();
        vo.setUserId(this.userId);
        vo.setPlayerCards(this.playerCards);
        vo.setScore(this.score);
        return vo;
    }
}
