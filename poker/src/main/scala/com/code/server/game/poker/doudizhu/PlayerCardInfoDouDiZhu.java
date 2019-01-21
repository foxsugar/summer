package com.code.server.game.poker.doudizhu;


import com.code.server.constant.game.CardStruct;
import com.code.server.constant.response.IfacePlayerInfoVo;
import com.code.server.constant.response.PlayerCardInfoVo;
import com.code.server.game.room.IfacePlayerInfo;

import java.util.*;

/**
 * Created by sunxianping on 2017/3/14.
 */
public class PlayerCardInfoDouDiZhu implements IfacePlayerInfo {

    public long userId;
    public List<Integer> allCards = new ArrayList<>();
    public List<Integer> cards = new ArrayList<>();//手上的牌
    protected List<Integer> disCards = new ArrayList<>();//丢弃的牌
    protected boolean isQiang;
    protected double score;
    protected int playCount;
    protected int zhaCount;

    //检测出牌是否合法
    public boolean checkPlayCard(CardStruct lastcardStruct , CardStruct currentCardStruct , int lasttype){
        if(!cards.containsAll(currentCardStruct.cards)){
            return false;
        }
        if (lastcardStruct == null || lastcardStruct.getUserId()==0) {
            return true;
        }
        if (lastcardStruct.getUserId() == userId) {
            return true;
        }
        boolean results = false;

        //判断牌型是否合法
        if(getListByIsType(currentCardStruct.cards) == 0){
            return false;
        }
        if(0!=lasttype){
             Integer currenttype =  currentCardStruct.type;//获取当前出牌类型
             if(currenttype==lasttype){
                List<Integer> lastList = lastcardStruct.getCards();//获取上次出牌的牌型
                List<Integer> list = currentCardStruct.getCards();//获取当前出牌类型

                 if(CardUtil.getTypeByCard(list.get(0))>CardUtil.getTypeByCard(lastList.get(0))){
                    results = true;
                }
            }else if(currenttype==CardStruct.type_火箭){ // 出牌是火箭
                 results = true;
             }else if(lasttype<CardStruct.type_炸 &&  currenttype == CardStruct.type_炸){ //出牌是炸弹，并且上一次出牌的类型不是火箭也不是炸弹
                 results = true;
             }
        }else{
            results = true;
        }
        return results;
    }

    private boolean isSameType(List<Integer> cards) {
        Set<Integer> types = new HashSet<>();
        for (int card : cards) {
            int cardType = CardUtil.getCardType(card);
            types.add(cardType);
        }
        return types.size() == 1;
    }

    public void text(){

    }


    public double addScore(double score) {
        this.score += score;
        return this.score;
    }
    public Integer getListByIsType(List<Integer> cards) {
        int len = cards.size();
        if (len <= 4) {
            if (cards.size() > 0 && isSameType(cards)) {
                switch (len) {
                    case 1:
                        return CardStruct.type_单;
                    case 2:
                        return CardStruct.type_对;
                    case 3:
                        return CardStruct.type_三;
                    case 4:
                        return CardStruct.type_炸;
                }
            }
            if (len == 2 && CardUtil.getTypeByCard(cards.get(0)) == 13
                    && CardUtil.getTypeByCard(cards.get(1)) == 14) {
                return CardStruct.type_火箭;
            }
            if (len == 4 && CardUtil.getTypeByCard(cards.get(0)).intValue() == CardUtil.getTypeByCard(cards.get(len - 3)).intValue()
                    && CardUtil.getTypeByCard(cards.get(0)).intValue() == CardUtil.getTypeByCard(cards.get(len - 4)).intValue()) {
                return CardStruct.type_三带单;
            } else {
                return 0;
            }
        }

        if (len >= 5) {

            if (len==5 && CardUtil.getTypeByCard(cards.get(0)).intValue() == CardUtil.getTypeByCard(cards.get(len - 3)).intValue()
                    && CardUtil.getTypeByCard(cards.get(0)).intValue() == CardUtil.getTypeByCard(cards.get(len - 4)).intValue()
                    && CardUtil.getTypeByCard(cards.get(len - 1)).intValue() == CardUtil.getTypeByCard(cards.get(len - 2)).intValue()){
                    return CardStruct.type_三带对;
            }
            if (len == 6 && CardUtil.getTypeByCard(cards.get(0)).intValue() == CardUtil.getTypeByCard(cards.get(len - 3)).intValue()
                    && CardUtil.getTypeByCard(cards.get(1)).intValue() == CardUtil.getTypeByCard(cards.get(len - 3)).intValue()
                    && CardUtil.getTypeByCard(cards.get(2)).intValue() == CardUtil.getTypeByCard(cards.get(len - 3)).intValue()
                    && CardUtil.getTypeByCard(cards.get(len - 1)).intValue() != CardUtil.getTypeByCard(cards.get(len - 2)).intValue()) {
                return CardStruct.type_四带二;
            }
            if (len == 8 && CardUtil.getTypeByCard(cards.get(0)).intValue() == CardUtil.getTypeByCard(cards.get(len - 5)).intValue()
                    && CardUtil.getTypeByCard(cards.get(1)).intValue() == CardUtil.getTypeByCard(cards.get(len - 6)).intValue()
                    && CardUtil.getTypeByCard(cards.get(2)).intValue() == CardUtil.getTypeByCard(cards.get(len - 6)).intValue()
                    && CardUtil.getTypeByCard(cards.get(len - 1)).intValue() == CardUtil.getTypeByCard(cards.get(len - 2)).intValue()
                    && CardUtil.getTypeByCard(cards.get(len - 3)).intValue() == CardUtil.getTypeByCard(cards.get(len - 4)).intValue()) {
                return CardStruct.type_四带二;
            }
            List<Integer> cardList = new ArrayList<>();
            for(Integer card :cards){
                cardList.add(CardUtil.getTypeByCard(card));
            }

            if ( getShunZi(cardList) && getShunDel2DaXiao(cards) && CardUtil.getTypeByCard(cards.get(len - 1)) - CardUtil.getTypeByCard(cards.get(0)) == len - 1) {
                return CardStruct.type_顺;
            }
            if ( getLianDui(cardList) && getShunDel2DaXiao(cards) && len % 2 == 0 && (len / 2 == 3 || len / 2 > 3) && CardUtil.getTypeByCard(cards.get(len - 1)) - CardUtil.getTypeByCard(cards.get(0)) == len / 2 - 1) {
                return CardStruct.type_连对;
            }

            if (len % 3 == 0 && (len / 3 == 2 || len / 3 > 2) && getFeiJi(cardList)) {
                return CardStruct.type_飞机;
            }
            if (getFeiJiChiBang(cardList)) {
                return CardStruct.type_飞机带翅膀;
            } else {
                return 0;
            }

        }else{
            return 0;
        }

    }

    public boolean getFeiJiChiBang(List<Integer> cards){
        boolean b = true;
        Map<Integer,Integer> map = new HashMap<>();
        List<Integer> threelist = new ArrayList<>();
        List<Integer> twolist = new ArrayList<>();
        List<Integer> onelist = new ArrayList<>();
        for (Integer i:cards) {
            if(map.containsKey(i)){
                map.put(i,map.get(i)+1);
            }else{
                map.put(i,1);
            }
        }
        for (Integer i:map.keySet()) {
            if(map.get(i)==3){
                threelist.add(i);
            }else if(map.get(i)==2){
                twolist.add(i);
            }else{
                onelist.add(i);
            }
        }
        Collections.sort(threelist);

        if(threelist.size()<2){
            b = false;
        }
        if (onelist.size()!=0 && threelist.size()!=onelist.size()){
            b = false;
        }else if(twolist.size()!=0 && threelist.size()!=twolist.size()){
            b = false;
        }
        for(int i = 0 ;i<threelist.size()-1;i++){
            if(threelist.get(i+1) - threelist.get(i) != 1){
                b =false;
            }
        }
        return b;
    }

    public boolean getFeiJi(List<Integer> cards){
        boolean b = true;
        Map<Integer,Integer> map = new HashMap<>();
        List<Integer> threelist = new ArrayList<>();

        for (Integer i:cards) {
            if(map.containsKey(i)){
                map.put(i,map.get(i)+1);
            }else{
                map.put(i,1);
            }
        }
        for (Integer i:map.keySet()) {
            if(map.get(i)==3){
                threelist.add(i);
            }
        }

        Collections.sort(threelist);

        if(threelist.size()<2){
            b = false;
        }
        for(int i = 0 ;i<threelist.size()-1;i++){
            if(threelist.get(i+1) - threelist.get(i) != 1){
                b =false;
            }
        }

        return b;
    }


    public boolean getShunZi(List<Integer> cards){
        boolean b = true;

        Collections.sort(cards);

        if(cards.size()<5){
            b = false;
        }
        for(int i = 0 ;i<cards.size()-1;i++){
            if(cards.get(i+1) - cards.get(i) != 1){
                b =false;
            }
        }
        return b;
    }

    public boolean getLianDui(List<Integer> cards){
        boolean b = true;
        Map<Integer,Integer> map = new HashMap<>();
        List<Integer> list = new ArrayList<>();

        for (Integer i:cards) {
            if(map.containsKey(i)){
                map.put(i,map.get(i)+1);
            }else{
                map.put(i,1);
            }
        }
        for (Integer i:map.keySet()) {
            if(map.get(i)==2){
                list.add(i);
            }
        }

        Collections.sort(list);

        if(list.size()<3){
            b = false;
        }
        for(int i = 0 ;i<list.size()-1;i++){
            if(list.get(i+1) - list.get(i) != 1){
                b =false;
            }
        }
        return b;
    }


    @Override
    public IfacePlayerInfoVo toVo(long watchUser) {
        PlayerCardInfoVo vo = new PlayerCardInfoVo();
        vo.userId = this.userId;
        vo.isQiang = this.isQiang();
        vo.cardNum = this.cards.size();
        if (watchUser == this.userId) {
            vo.cards.addAll(this.cards);
            vo.allCards.addAll(this.allCards);

        }
        return vo;
    }

    @Override
    public IfacePlayerInfoVo toVo() {
        PlayerCardInfoVo vo = new PlayerCardInfoVo();
        vo.userId = this.userId;
        vo.cards.addAll(this.cards);
        vo.allCards.addAll(this.allCards);
        vo.isQiang = this.isQiang();
        vo.score = this.getScore();
        vo.zhaCount = this.zhaCount;

        return vo;
    }

    //顺除 2 大王小王
    public boolean getShunDel2DaXiao(List<Integer> cards){
        if(cards.contains(8) || cards.contains(6) || cards.contains(5) || cards.contains(7) || cards.contains(53) || cards.contains(54)){
            return false;
        }else{
            return true;
        }
    }


    public long getUserId() {
        return userId;
    }

    public PlayerCardInfoDouDiZhu setUserId(long userId) {
        this.userId = userId;
        return this;
    }

    public List<Integer> getCards() {
        return cards;
    }

    public PlayerCardInfoDouDiZhu setCards(List<Integer> cards) {
        this.cards = cards;
        return this;
    }

    public List<Integer> getDisCards() {
        return disCards;
    }

    public PlayerCardInfoDouDiZhu setDisCards(List<Integer> disCards) {
        this.disCards = disCards;
        return this;
    }

    public boolean isQiang() {
        return isQiang;
    }

    public PlayerCardInfoDouDiZhu setQiang(boolean qiang) {
        isQiang = qiang;
        return this;
    }

    public double getScore() {
        return score;
    }

    public PlayerCardInfoDouDiZhu setScore(double score) {
        this.score = score;
        return this;
    }

    public int getPlayCount() {
        return playCount;
    }

    public PlayerCardInfoDouDiZhu setPlayCount(int playCount) {
        this.playCount = playCount;
        return this;
    }

    public List<Integer> MinimumCards(){
        List<Integer> cardList = new ArrayList<>();
        for(Integer card :cards){
            cardList.add(CardUtil.getTypeByCard(card));
        }
        Integer card = Collections.min(cardList);

        Integer index = getListmin(cardList,card);

        cardList.removeAll(cardList);
        cardList.add(cards.get(index));
        return cardList;
    }

    public Integer getListmin(List<Integer> cardList,Integer card){
        Integer index = 0;
        for(int i = 0 ;i<cardList.size();i++){
            if(cardList.get(i).intValue()==card.intValue()){
                index = i;
            }
        }
        return index;
    }

    public List<Integer> getAllCards() {
        return allCards;
    }

    public PlayerCardInfoDouDiZhu setAllCards(List<Integer> allCards) {
        this.allCards = allCards;
        return this;
    }

    public int getZhaCount() {
        return zhaCount;
    }

    public PlayerCardInfoDouDiZhu setZhaCount(int zhaCount) {
        this.zhaCount = zhaCount;
        return this;
    }
}





