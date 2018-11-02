package com.code.server.game.poker.tiandakeng;

import com.code.server.game.room.PlayerCardInfo;

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

    //每轮下注数
    private Map<Integer, Integer> roundBet = new HashMap<>();

    //是否弃牌
    private boolean isGiveUp = false;


    /**
     * 下注
     * @param num
     */
    public void addBet(int num, int round){
        this.bets.add(num);
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
        //todo 公张算不算分
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
        List<Integer> cards = new ArrayList<>();
        cards.add(null);
        cards.add(null);
        for(int i=2;i<this.cards.size();i++){
            cards.add(this.cards.get(i));
        }
        result.put("cards", cards);
        return result;
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
}
