package com.code.server.game.poker.tuitongzi;

import com.code.server.constant.response.IfacePlayerInfoVo;
import com.code.server.game.room.IfacePlayerInfo;

import java.util.ArrayList;
import java.util.List;

public class PlayerTuiTongZi implements IfacePlayerInfo {

    private long userId;
    private List<Integer> playerCards = new ArrayList<>();
    private Bet bet;
    private long score;
    private int bankerScore;
    private boolean open;
    private boolean isWinner;
    //牌型
    private long pattern;
    private long pxId;
    private long potBottom;
    private long zhuangCount;
    private long grab;

    public PlayerTuiTongZi() {
    }

    public long getZhuangCount() {
        return zhuangCount;
    }

    public void setZhuangCount(long zhuangCount) {
        this.zhuangCount = zhuangCount;
    }

    public long getPotBottom() {
        return potBottom;
    }

    public void setPotBottom(long potBottom) {
        this.potBottom = potBottom;
    }

    public PlayerTuiTongZi(long userId, Integer card1, Integer card2) {
        this.userId = userId;
        this.playerCards = new ArrayList<Integer>();
        this.playerCards.add(card1);
        this.playerCards.add(card2);
        try {
            this.pattern = TuiTongZiCardUtils.cardsPatterns(this.playerCards);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long getGrab() {
        return grab;
    }

    public void setGrab(long grab) {
        this.grab = grab;
    }

    public boolean isWinner() {
        return isWinner;
    }

    public long getPxId() {
        return pxId;
    }

    public void setPxId(long pxId) {
        this.pxId = pxId;
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
        vo.setPattern(pattern);
        vo.setWinner(isWinner);
        vo.setOpen(this.isOpen());
        vo.setPotBottom(this.potBottom);
        vo.setGrab(this.grab);
        if (bet == null){
            vo.setZhu(0);
        }else {
            int ret = this.bet.getZhu();
            long score = 0;
            if (ret == 1){
                score = 5;
            }else if(ret == 2){
                score = 10;
            }else if(ret == 3){
                score = 15;
            }else if(ret == 4){
                score = 20;
            }else if(ret == 5){
               score = this.getPotBottom() / 2;
            }else if(ret == 6){
                score = this.getPotBottom();
            }
            vo.setZhu(score);
        }
        return vo;
    }

    @Override
    public IfacePlayerInfoVo toVo(long watchUser) {

        PlayerTuiTongZiVo vo = new PlayerTuiTongZiVo();
        vo.setUserId(this.userId);
        vo.setPlayerCards(this.playerCards);
        vo.setScore(this.score);
        vo.setPattern(pattern);
        vo.setWinner(isWinner);
        vo.setOpen(this.isOpen());
        vo.setZhu(this.bet.getZhu());
        return vo;
    }
}
