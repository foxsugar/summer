package com.code.server.constant.response;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunxianping on 2017/3/20.
 */
public class PlayerCardInfoHitGoldFlowerVo implements IfacePlayerInfoVo {
    public long userId;
    public List<Integer> handcards = new ArrayList<>();//手上的牌
    public double score;
    public double caifen;
    public String cardType;


    public PlayerCardInfoHitGoldFlowerVo() {

    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public List<Integer> getHandcards() {
        return handcards;
    }

    public void setHandcards(List<Integer> handcards) {
        this.handcards = handcards;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public double getCaifen() {
        return caifen;
    }

    public void setCaifen(double caifen) {
        this.caifen = caifen;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }
}
