package com.code.server.game.poker.tuitongzi;

import com.code.server.constant.response.IfacePlayerInfoVo;

import java.util.List;

public class PlayerTuiTongZiVo implements IfacePlayerInfoVo {

    private long userId;
    private List<Integer> playerCards;
    private long score;

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
}
