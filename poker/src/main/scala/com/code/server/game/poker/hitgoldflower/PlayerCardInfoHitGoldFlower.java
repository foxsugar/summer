package com.code.server.game.poker.hitgoldflower;

import com.code.server.constant.response.IfacePlayerInfoVo;
import com.code.server.constant.response.PlayerCardInfoHitGoldFlowerVo;
import com.code.server.game.room.IfacePlayerInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunxianping on 2017/3/14.
 */
public class PlayerCardInfoHitGoldFlower implements IfacePlayerInfo {

    public long userId;
    public List<Integer> handcards = new ArrayList<>();//手上的牌
    protected double score;
    protected double caifen;
    protected String cardType;
    protected double allScore;

    //1表示显示
    protected String call = "1";//跟注
    protected String raise = "1";//加注
    protected String fold = "1";//弃牌
    protected String kill = "1";//比牌
    protected String see = "1";//看牌



    @Override
    public IfacePlayerInfoVo toVo() {
        PlayerCardInfoHitGoldFlowerVo vo = new PlayerCardInfoHitGoldFlowerVo();
        vo.userId = this.userId;
        vo.handcards = this.handcards;
        vo.score = this.score;
        vo.caifen = this.caifen;
        vo.cardType = this.cardType;
        return vo;
    }

    @Override
    public IfacePlayerInfoVo toVo(long watchUser) {
        return null;
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

    public double getAllScore() {
        return score+caifen;
    }

    public void setAllScore(double allScore) {
        this.allScore = allScore;
    }
}





