package com.code.server.game.poker.zhaguzi;

import com.code.server.constant.response.IfacePlayerInfoVo;
import com.code.server.constant.response.PlayerCardInfoHitGoldFlowerVo;
import com.code.server.game.room.IfacePlayerInfo;

import java.util.ArrayList;
import java.util.List;


public class PlayerYSZ implements IfacePlayerInfo {

    public long userId;
    public List<Integer> handcards = new ArrayList<>();//手上的牌
    protected double score;
    protected double caifen;
    protected String cardType;
    protected double allScore;//总下注
    protected double finalScore;//单局输赢，用于判断谁赢了


    //1表示显示
    protected String call;//跟注
    protected String raise;//加注
    protected String fold;//弃牌
    protected String kill;//比牌
    protected String see;//看牌

    public int curRoundNumber;//当前轮数

    @Override
    public IfacePlayerInfoVo toVo() {
        PlayerCardInfoHitGoldFlowerVo vo = new PlayerCardInfoHitGoldFlowerVo();
        vo.userId = this.userId;
        vo.score = this.score;
        vo.caifen = this.caifen;
        vo.cardType = this.cardType;
        vo.allScore = this.allScore;
        vo.finalScore = this.finalScore;

        vo.call = this.getCall();//跟注
        vo.raise = this.getRaise();//加注
        vo.fold = this.getFold();//弃牌
        vo.kill = this.getKill();//比牌
        vo.see = this.getSee();//看牌

        return vo;
    }

    @Override
    public IfacePlayerInfoVo toVo(long watchUser) {
        PlayerCardInfoHitGoldFlowerVo vo = new PlayerCardInfoHitGoldFlowerVo();
        vo.userId = this.userId;
        vo.handcards = this.handcards;
        vo.score = this.score;
        vo.caifen = this.caifen;
        vo.cardType = this.cardType;
        vo.allScore = this.allScore;
        vo.finalScore = this.finalScore;

        vo.call = this.getCall();//跟注
        vo.raise = this.getRaise();//加注
        vo.fold = this.getFold();//弃牌
        vo.kill = this.getKill();//比牌
        vo.see = this.getSee();//看牌

        return vo;
    }

    public IfacePlayerInfoVo toVoHaveHandcards() {
        PlayerCardInfoHitGoldFlowerVo vo = new PlayerCardInfoHitGoldFlowerVo();
        vo.userId = this.userId;
        vo.score = this.score;
        vo.caifen = this.caifen;
        vo.cardType = this.cardType;
        vo.finalScore = this.finalScore;

        vo.call = this.getCall();//跟注
        vo.raise = this.getRaise();//加注
        vo.fold = this.getFold();//弃牌
        vo.kill = this.getKill();//比牌
        vo.see = this.getSee();//看牌
        vo.curRoundNumber = this.getCurRoundNumber();
        vo.handcards = this.getHandcards();

        return vo;
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
        return allScore;
    }

    public void setAllScore(double allScore) {
        this.allScore = allScore;
    }

    public int getCurRoundNumber() {
        return curRoundNumber;
    }

    public void setCurRoundNumber(int curRoundNumber) {
        this.curRoundNumber = curRoundNumber;
    }

    public double getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(double finalScore) {
        this.finalScore = finalScore;
    }
}





