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
    protected boolean display;

    public Integer safeNum = 0;//有效罗数

    //状态 got推送客户端一次，catch表示已经扣过，可以得到分数
    protected boolean gotThree = false;
    protected boolean gotFive = false;
    protected boolean gotSix = false;

    protected boolean catchThree = false;
    protected boolean catchFive = false;
    protected boolean catchSix = false;

    //游戏状态
    //1表示显示
    protected String canSetMultiple;//庄可以加倍 -1默认，0已设置，1未设置
    protected String canChoose;//可出牌
    protected String canSendCard;//可出牌
    protected String canXuan;//可选
    protected String canKou;//可扣
    protected String canGuo;//可过,不选


    public int curRoundNumber;//当前轮数




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

    public Integer getSafeNum() {
        return safeNum;
    }

    public void setSafeNum(Integer safeNum) {
        this.safeNum = safeNum;
    }

    public void addScore(int addScore){
        this.score = score+addScore;
    }

    public void addAllScore(int addScore){
        this.allScore = allScore+addScore;
    }

    public String getCanSendCard() {
        return canSendCard;
    }

    public void setCanSendCard(String canSendCard) {
        this.canSendCard = canSendCard;
    }

    public String getCanXuan() {
        return canXuan;
    }

    public void setCanXuan(String canXuan) {
        this.canXuan = canXuan;
    }

    public String getCanKou() {
        return canKou;
    }

    public void setCanKou(String canKou) {
        this.canKou = canKou;
    }

    public String getCanGuo() {
        return canGuo;
    }

    public void setCanGuo(String canGuo) {
        this.canGuo = canGuo;
    }

    public boolean isDisplay() {
        return display;
    }

    public void setDisplay(boolean display) {
        this.display = display;
    }

    @Override
    public IfacePlayerInfoVo toVo() {
        PlayerCardInfoXuanQiQiVo vo = new PlayerCardInfoXuanQiQiVo();
        vo.userId =this.userId;
        vo.randomCard = this.randomCard;
        //vo.handCards = this.handCards;
        vo.playCards = this.playCards;
        vo.winCards = this.winCards;
        vo.cardsType = this.cardsType;
        vo.score =this.score;
        vo.allScore = this.allScore;
        vo.safeNum = this.safeNum;
        vo.gotThree = this.gotThree;
        vo.gotFive = this.gotFive;
        vo.gotSix = this.gotSix;
        vo.catchThree = this.catchThree;
        vo.catchFive = this.catchFive;
        vo.catchSix = this.catchSix;
        vo.canSetMultiple = this.canSetMultiple;
        vo.canChoose = this.canChoose;
        vo.canSendCard = this.canSendCard;
        vo.canXuan = this.canXuan;
        vo.canGuo = this.canGuo;
        vo.canKou = this.canKou;
        vo.curRoundNumber = this.curRoundNumber;
        vo.cardNum = this.handCards.size();
        vo.display = this.display;
        return vo;
    }

    @Override
    public IfacePlayerInfoVo toVo(long watchUser) {
        PlayerCardInfoXuanQiQiVo vo = new PlayerCardInfoXuanQiQiVo();
        vo.userId =this.userId;
        vo.randomCard = this.randomCard;
        vo.handCards = this.handCards;
        vo.playCards = this.playCards;
        vo.winCards = this.winCards;
        vo.cardsType = this.cardsType;
        vo.score =this.score;
        vo.allScore = this.allScore;
        vo.safeNum = this.safeNum;
        vo.gotThree = this.gotThree;
        vo.gotFive = this.gotFive;
        vo.gotSix = this.gotSix;
        vo.catchThree = this.catchThree;
        vo.catchFive = this.catchFive;
        vo.catchSix = this.catchSix;
        vo.canSetMultiple = this.canSetMultiple;
        vo.canChoose = this.canChoose;
        vo.canSendCard = this.canSendCard;
        vo.canXuan = this.canXuan;
        vo.canGuo = this.canGuo;
        vo.canKou = this.canKou;
        vo.curRoundNumber = this.curRoundNumber;
        vo.cardNum = this.handCards.size();
        vo.display = this.display;
        return vo;
    }
}