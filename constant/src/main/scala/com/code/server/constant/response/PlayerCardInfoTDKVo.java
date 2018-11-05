package com.code.server.constant.response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sunxianping on 2018-11-05.
 */
public class PlayerCardInfoTDKVo implements IfacePlayerInfoVo {

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


    public List<Integer> getCards() {
        return cards;
    }

    public PlayerCardInfoTDKVo setCards(List<Integer> cards) {
        this.cards = cards;
        return this;
    }

    public int getCommonCard() {
        return commonCard;
    }

    public PlayerCardInfoTDKVo setCommonCard(int commonCard) {
        this.commonCard = commonCard;
        return this;
    }

    public List<Integer> getBets() {
        return bets;
    }

    public PlayerCardInfoTDKVo setBets(List<Integer> bets) {
        this.bets = bets;
        return this;
    }

    public int getAllBet() {
        return allBet;
    }

    public PlayerCardInfoTDKVo setAllBet(int allBet) {
        this.allBet = allBet;
        return this;
    }

    public Map<Integer, Integer> getRoundBet() {
        return roundBet;
    }

    public PlayerCardInfoTDKVo setRoundBet(Map<Integer, Integer> roundBet) {
        this.roundBet = roundBet;
        return this;
    }

    public boolean isGiveUp() {
        return isGiveUp;
    }

    public PlayerCardInfoTDKVo setGiveUp(boolean giveUp) {
        isGiveUp = giveUp;
        return this;
    }

    public int getCardScore() {
        return cardScore;
    }

    public PlayerCardInfoTDKVo setCardScore(int cardScore) {
        this.cardScore = cardScore;
        return this;
    }
}
