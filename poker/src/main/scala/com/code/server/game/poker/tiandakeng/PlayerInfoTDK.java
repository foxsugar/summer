package com.code.server.game.poker.tiandakeng;

import com.code.server.constant.response.IfacePlayerInfoVo;
import com.code.server.constant.response.PlayerCardInfoTDKVo;
import com.code.server.game.room.PlayerCardInfo;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sunxianping on 2018-10-18.
 */
public class PlayerInfoTDK extends PlayerCardInfo{



    //牌
    private List<Integer> cards = new ArrayList<>();

    //公张
    private int commonCard = 0;

    //下的注
    private List<Integer> bets = new ArrayList<>();

    private int allBet = 0;

    //每轮下注数
    private Map<Integer, Integer> roundBet = new HashMap<>();

    //是否弃牌
    private boolean isGiveUp = false;

    private int cardScore = 0;


    /**
     * 下注
     * @param num
     */
    public void addBet(int num, int round){
        this.bets.add(num);
        this.allBet += num;
        roundBet.put(round, roundBet.getOrDefault(round, 0) + num);
    }


    /**
     *
     * @param card 牌
     * @param isCommon 是否是公张
     */
    public void deal(int card, boolean isCommon) {
        if (isCommon) {
            this.commonCard = card;
        } else {
            this.cards.add(card);
        }
    }

    public int computeScore(boolean isGongZhuangSuiBao, boolean isABiPao, boolean isWangZhongPao) {
        this.cardScore = getCardScore(isGongZhuangSuiBao, isABiPao, isWangZhongPao, true);
        return this.cardScore;
    }

    /**
     *
     * @param isGongZhuangSuiBao 是否公张随豹
     * @param isABiPao 是否A必炮
     * @param isWangZhongPao 是否王中炮
     * @return
     */
    public int getCardScore(boolean isGongZhuangSuiBao,boolean isABiPao, boolean isWangZhongPao,boolean isAddFirstTwo){
        List<Integer> cardList = new ArrayList<>();
        cardList.addAll(cards);
        // 删掉前两张
        if (!isAddFirstTwo) {
            cardList.remove(0);
            cardList.remove(0);
        }
        if (isGongZhuangSuiBao && commonCard != 0) {
            cardList.add(commonCard);
        }
        int score = 0;
        if(CardUtil.hasBaozi(cardList)){
            score += 30;
        }
        if(CardUtil.hasShuangWang(cardList)){
            score += 30;
        }
        if (isWangZhongPao && CardUtil.hasShuangWang(cardList) && CardUtil.hasBaozi(cardList)) {
            score += 60;
        }
        if(CardUtil.hasSiTiao(cardList)){
            score += 60;
        }
        //再加上每张牌的分数

        score += cardList.stream().mapToInt(card->CardUtil.getCardScore(card,isABiPao)).sum();
        return score;
    }

    /**
     * 获得手牌信息
     * @return
     */
    public Map<String,Object> getHandCardsInfo(){
        Map<String, Object> result = new HashMap<>();
        result.put("commonCard", commonCard);
        result.put("cards", getCardsHideFirstTwo());
        return result;
    }

    /**
     * 手牌 隐藏前两张
     * @return
     */
    private List<Integer> getCardsHideFirstTwo() {
        List<Integer> cards = new ArrayList<>();
        cards.add(null);
        cards.add(null);
        for(int i=2;i<this.cards.size();i++){
            cards.add(this.cards.get(i));
        }
        return cards;
    }

    @Override
    public IfacePlayerInfoVo toVo() {
        PlayerCardInfoTDKVo playerCardInfoTDKVo = new PlayerCardInfoTDKVo();
        BeanUtils.copyProperties(this, playerCardInfoTDKVo);
        playerCardInfoTDKVo.setCards(getCardsHideFirstTwo());
        return playerCardInfoTDKVo;
    }

    @Override
    public IfacePlayerInfoVo toVo(long watchUser) {
        return this.toVo();
    }

    public PlayerCardInfoTDKVo toVoShowAllCard() {
        PlayerCardInfoTDKVo playerCardInfoTDKVo = new PlayerCardInfoTDKVo();
        BeanUtils.copyProperties(this, playerCardInfoTDKVo);
        return playerCardInfoTDKVo;
    }

    public List<Integer> getCards() {
        return cards;
    }

    public PlayerInfoTDK setCards(List<Integer> cards) {
        this.cards = cards;
        return this;
    }

    public int getCommonCard() {
        return commonCard;
    }

    public PlayerInfoTDK setCommonCard(int commonCard) {
        this.commonCard = commonCard;
        return this;
    }

    public List<Integer> getBets() {
        return bets;
    }

    public PlayerInfoTDK setBets(List<Integer> bets) {
        this.bets = bets;
        return this;
    }

    public boolean isGiveUp() {
        return isGiveUp;
    }

    public PlayerInfoTDK setGiveUp(boolean giveUp) {
        isGiveUp = giveUp;
        return this;
    }

    public int getAllBet() {
        return allBet;
    }

    public PlayerInfoTDK setAllBet(int allBet) {
        this.allBet = allBet;
        return this;
    }

    public Map<Integer, Integer> getRoundBet() {
        return roundBet;
    }

    public PlayerInfoTDK setRoundBet(Map<Integer, Integer> roundBet) {
        this.roundBet = roundBet;
        return this;
    }

    public int getCardScore() {
        return cardScore;
    }

    public PlayerInfoTDK setCardScore(int cardScore) {
        this.cardScore = cardScore;
        return this;
    }
}
