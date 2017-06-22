package com.code.server.game.poker.doudizhu;

import com.code.server.constant.game.CardStruct;

import java.util.*;

import static com.code.server.game.poker.doudizhu.CardUtil.getCardType;


/**
 * Created by sunxianping on 2017/3/14.
 */
public class PlayerCardInfoDouDiZhuLinfen extends  PlayerCardInfoDouDiZhu{



    public Integer getMinimumCards(){
        List<Integer> cardList = new ArrayList<>();
        for(Integer card :cards){
            cardList.add(CardUtil.getTypeByCard(card));
        }
        Integer card = Collections.min(cardList);

        Integer index = getListmin(cardList,card);

        return cards.get(index);
    }
    public Integer getListmin(List<Integer> cardList,Integer card){
        Integer index = 0;
        for(int i = 0 ;i<cardList.size();i++){
            if(cardList.get(i)==card){
                index = i;
            }
        }
        return index;
    }

    public boolean isCanPlay(CardStruct cardStruct){
        return false;
    }


    public boolean isCanCard(CardStruct cardStruct){

        return false;
    }

    //检测出牌是否合法
    public boolean checkPlayCard(CardStruct lastcardStruct ,CardStruct currentCardStruct , int lasttype){
        if (lastcardStruct == null || lastcardStruct.getUserId()==0) {
            return true;
        }
        if (lastcardStruct.getUserId() == userId) {
            return true;
        }

        List<Integer> copyCards = new ArrayList<>();
        copyCards.addAll(cards);

        if(currentCardStruct.getType() == CardStruct.type_三 || currentCardStruct.getType() == CardStruct.type_飞机){
            copyCards.removeAll(currentCardStruct.getCards());
            if(copyCards.size()<=0){
                return true;
            }else{
                return false;
            }
        }

        boolean results = false;

        //判断牌型是否合法
        if(getListByIsType(currentCardStruct.cards) == 0){
            return false;
        }

        if(getListByIsType(currentCardStruct.cards) != currentCardStruct.getType()){
            return false;
        }
        if(0!=lasttype){
             Integer currenttype =  currentCardStruct.type;//获取当前出牌类型
             if(currenttype==lasttype){
                List<Integer> lastList = lastcardStruct.getCards();//获取上次出牌的牌型
                List<Integer> list = currentCardStruct.getCards();//获取当前出牌类型

                 if(list.size()>lastList.size()){     //3333 > 22
                    results = true;
                 }else if(CardUtil.getTypeByCard(list.get(0))>CardUtil.getTypeByCard(lastList.get(0))){
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
            int cardType = getCardType(card);
            types.add(cardType);
        }
        return types.size() == 1;
    }
    public Integer getListByIsType(List<Integer> cards) {
        int len = cards.size();
        if (len <= 4) {
            if (cards.size() > 0 &&isSameType(cards)) {
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
            if (len == 2 && CardUtil.getTypeByCard(cards.get(0)) == 12
                    && CardUtil.getTypeByCard(cards.get(1)) == 12) {
                return CardStruct.type_炸;
            }
            if (len == 2 && CardUtil.getTypeByCard(cards.get(0)) == 0
                    && CardUtil.getTypeByCard(cards.get(1)) == 0) {
                return CardStruct.type_炸;
            }
            if (len == 2 && CardUtil.getTypeByCard(cards.get(0)) == 13
                    && CardUtil.getTypeByCard(cards.get(1)) == 14) {
                return CardStruct.type_火箭;
            }
            if (len == 4 && CardUtil.getTypeByCard(cards.get(0)).intValue() == CardUtil.getTypeByCard(cards.get(len - 2)).intValue()
                    && CardUtil.getTypeByCard(cards.get(0)).intValue() == CardUtil.getTypeByCard(cards.get(len - 3)).intValue()) {
                return CardStruct.type_三带单;
            } else {
                return 0;
            }
        }

        if (len >= 5) {
            if (len == 6 && CardUtil.getTypeByCard(cards.get(0)).intValue() == CardUtil.getTypeByCard(cards.get(len - 3)).intValue()
                    && CardUtil.getTypeByCard(cards.get(1)).intValue() == CardUtil.getTypeByCard(cards.get(len - 3)).intValue()
                    && CardUtil.getTypeByCard(cards.get(2)).intValue() == CardUtil.getTypeByCard(cards.get(len - 3)).intValue()
                    && CardUtil.getTypeByCard(cards.get(len-1)).intValue() != CardUtil.getTypeByCard(cards.get(len - 2)).intValue()) {
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

            if (len % 3 == 0 && (len / 3 == 2 || len / 3 > 2)
                    && getFeiJi(cardList) && getShunDel2DaXiao(cards)) {
                return CardStruct.type_飞机;
            }
            if (getFeiJiChiBang(cardList) && getShunDel2DaXiao(cards)) {
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
        List<Integer> fourlist = new ArrayList<>();
        List<Integer> threelist = new ArrayList<>();
        List<Integer> twolist = new ArrayList<>();
        List<Integer> onelist = new ArrayList<>();
        List<Integer> zerohourlist = new ArrayList<>();

        for (Integer i:cards) {
            if(map.containsKey(i)){
                map.put(i,map.get(i)+1);
            }else{
                map.put(i,1);
            }
        }
        for (Integer i:map.keySet()) {
            if(map.get(i)==4){
                fourlist.add(i);
            }else if(map.get(i)==3){
                threelist.add(i);
            }else if(map.get(i)==2){
                twolist.add(i);
            }else{
                onelist.add(i);
            }
        }

        Collections.sort(threelist);

        for(int i = 0 ;i<threelist.size()-1;i++){
            if(threelist.get(i+1) - threelist.get(i) != 1){
                zerohourlist.add(threelist.get(i+1));
                threelist.remove(i+1);
            }
        }

        if(threelist.size()<2){
            b = false;
        }
        if (onelist.size()!=0 && threelist.size()!=onelist.size()){
            b = false;
        }else if(twolist.size()!=0){
            if(threelist.size() - twolist.size() <0 && threelist.size() - twolist.size() !=0){ //dan
                b = false;
            }else{
                b = true;
            }
        }else if(fourlist.size()!=0 && threelist.size()!=4){
            b = false;
        }


        for(int i = 0 ;i<threelist.size()-1;i++){
            if(threelist.get(i+1) - threelist.get(i) != 1){
                b =false;
            }
        }


        return b;
    }





    //顺除 2 大王小王
    public boolean getShunDel2DaXiao(List<Integer> cards){
        if(cards.contains(8) || cards.contains(6) || cards.contains(53) || cards.contains(54)){
            return false;
        }else{
            return true;
        }
    }








    public long getUserId() {
        return userId;
    }

    public PlayerCardInfoDouDiZhuLinfen setUserId(long userId) {
        this.userId = userId;
        return this;
    }

    public List<Integer> getCards() {
        return cards;
    }

    public PlayerCardInfoDouDiZhuLinfen setCards(List<Integer> cards) {
        this.cards = cards;
        return this;
    }

    public List<Integer> getDisCards() {
        return disCards;
    }

    public PlayerCardInfoDouDiZhuLinfen setDisCards(List<Integer> disCards) {
        this.disCards = disCards;
        return this;
    }

    public boolean isQiang() {
        return isQiang;
    }

    public PlayerCardInfoDouDiZhuLinfen setQiang(boolean qiang) {
        isQiang = qiang;
        return this;
    }

    public double getScore() {
        return score;
    }

    public PlayerCardInfoDouDiZhuLinfen setScore(double score) {
        this.score = score;
        return this;
    }

    public int getPlayCount() {
        return playCount;
    }

    public PlayerCardInfoDouDiZhuLinfen setPlayCount(int playCount) {
        this.playCount = playCount;
        return this;
    }
}





