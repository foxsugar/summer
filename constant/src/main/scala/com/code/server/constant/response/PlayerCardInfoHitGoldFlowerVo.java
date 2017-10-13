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

    public String call;//跟注
    public String raise;//加注
    public String fold;//弃牌
    public String kill;//比牌
    public String see;//看牌


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

    public String getCall() {
        return call;
    }

    public void setCall(String call) {
        this.call = call;
    }

    public String getRaise() {
        return raise;
    }

    public void setRaise(String raise) {
        this.raise = raise;
    }

    public String getFold() {
        return fold;
    }

    public void setFold(String fold) {
        this.fold = fold;
    }

    public String getKill() {
        return kill;
    }

    public void setKill(String kill) {
        this.kill = kill;
    }

    public String getSee() {
        return see;
    }

    public void setSee(String see) {
        this.see = see;
    }
}
