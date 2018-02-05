package com.code.server.game.poker.tuitongzi;

import com.code.server.constant.response.IfacePlayerInfoVo;

import java.util.List;

public class PlayerTuiTongZiVo implements IfacePlayerInfoVo {

    private long userId;
    private List<Integer> playerCards;
    private long score;

    private boolean isWinner;
    private boolean open;
    private long zhu;
    private long potBottom;
    private long grab;

    public long getGrab() {
        return grab;
    }

    public void setGrab(long grab) {
        this.grab = grab;
    }

    public long getPotBottom() {
        return potBottom;
    }

    public void setPotBottom(long potBottom) {
        this.potBottom = potBottom;
    }

    public long getZhu() {
        return zhu;
    }

    public void setZhu(long zhu) {
        this.zhu = zhu;
    }

    //牌型
    private long pattern;

    public boolean isWinner() {
        return isWinner;
    }

    public void setWinner(boolean winner) {
        isWinner = winner;
    }

    public long getPattern() {
        return pattern;
    }

    public void setPattern(long pattern) {
        this.pattern = pattern;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
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
}
