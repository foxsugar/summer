package com.code.server.game.poker.xuanqiqi;

import com.code.server.constant.response.IfacePlayerInfoVo;
import com.code.server.game.room.IfacePlayerInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 项目名称：${project_name}
 * 类名称：${type_name}
 * 类描述：
 * 创建人：Clark
 * 创建时间：${date} ${time}
 * 修改人：Clark
 * 修改时间：${date} ${time}
 * 修改备注：
 *
 * @version 1.0
 */
public class PlayerCardInfoXuanQiQi  implements IfacePlayerInfo{

    /*
       userId
       handcards
       摞(3张一摞)
        当玩家手边有三摞时，播放“够了”特效
        当玩家手边有五摞时，播放“伍了”特效
        当玩家手边有六摞是，播放“呲了”特效
       宣起次数 count:otherUserId

     */

    public long userId;
    public Integer randomCard;//手上的牌
    public List<Integer> handCards = new ArrayList<>();//手上的牌
    public List<Integer> playCards = new ArrayList<>();//当前出的牌
    public List<Integer> winCards = new ArrayList<>();//罗上的牌
    public Map<Integer,Boolean> cardsType= new HashMap<>();//罗上牌明或扣的状态, true明 ，false扣
    protected double score;
    protected double allScore;



    //状态 got推送客户端一次，catch表示已经扣过，可以得到分数
    protected boolean gotThree = false;
    protected boolean gotFive = false;
    protected boolean gotSix = false;

    protected boolean catchThree = false;
    protected boolean catchFive = false;
    protected boolean catchSix = false;

    //游戏状态
    //1表示显示
    protected String canSetMultiple;//庄可以加倍
    protected String canChoose;//可出牌
    protected String fold;//弃牌
    protected String kill;//比牌
    protected String see;//看牌


    public int curRoundNumber;//当前轮数




    @Override
    public IfacePlayerInfoVo toVo() {
        return null;
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

    public List<Integer> getHandCards() {
        return handCards;
    }

    public void setHandCards(List<Integer> handCards) {
        this.handCards = handCards;
    }

    public List<Integer> getWinCards() {
        return winCards;
    }

    public void setWinCards(List<Integer> winCards) {
        this.winCards = winCards;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
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

    public Integer getRandomCard() {
        return randomCard;
    }

    public void setRandomCard(Integer randomCard) {
        this.randomCard = randomCard;
    }

    public Map<Integer, Boolean> getCardsType() {
        return cardsType;
    }

    public void setCardsType(Map<Integer, Boolean> cardsType) {
        this.cardsType = cardsType;
    }

    public boolean isGotThree() {
        return gotThree;
    }

    public void setGotThree(boolean gotThree) {
        this.gotThree = gotThree;
    }

    public boolean isGotFive() {
        return gotFive;
    }

    public void setGotFive(boolean gotFive) {
        this.gotFive = gotFive;
    }

    public boolean isGotSix() {
        return gotSix;
    }

    public void setGotSix(boolean gotSix) {
        this.gotSix = gotSix;
    }

    public boolean isCatchThree() {
        return catchThree;
    }

    public void setCatchThree(boolean catchThree) {
        this.catchThree = catchThree;
    }

    public boolean isCatchFive() {
        return catchFive;
    }

    public void setCatchFive(boolean catchFive) {
        this.catchFive = catchFive;
    }

    public boolean isCatchSix() {
        return catchSix;
    }

    public void setCatchSix(boolean catchSix) {
        this.catchSix = catchSix;
    }

    public String getCanSetMultiple() {
        return canSetMultiple;
    }

    public void setCanSetMultiple(String canSetMultiple) {
        this.canSetMultiple = canSetMultiple;
    }

    public List<Integer> getPlayCards() {
        return playCards;
    }

    public void setPlayCards(List<Integer> playCards) {
        this.playCards = playCards;
    }

    public String getCanChoose() {
        return canChoose;
    }

    public void setCanChoose(String canChoose) {
        this.canChoose = canChoose;
    }
}