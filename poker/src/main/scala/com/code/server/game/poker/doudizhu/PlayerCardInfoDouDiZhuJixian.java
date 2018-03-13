package com.code.server.game.poker.doudizhu;

import com.code.server.constant.game.CardStruct;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunxianping on 2018/3/5.
 */
public class PlayerCardInfoDouDiZhuJixian extends PlayerCardInfoDouDiZhuLinfen {


    //检测出牌是否合法
    public boolean checkPlayCard(CardStruct lastcardStruct, CardStruct currentCardStruct, int lasttype) {
        //花牌不能单出
        if (currentCardStruct.getCards().size() == 1 && currentCardStruct.getCards().get(0) == 55) {
            return false;
        }
        if (lastcardStruct == null || lastcardStruct.getUserId() == 0) {
            return true;
        }
        if (lastcardStruct.getUserId() == userId) {
            return true;
        }

        List<Integer> copyCards = new ArrayList<>();
        copyCards.addAll(cards);

        if (currentCardStruct.getType() == CardStruct.type_三 || currentCardStruct.getType() == CardStruct.type_飞机) {
            copyCards.removeAll(currentCardStruct.getCards());
            if (copyCards.size() <= 0) {
                return true;
            } else {
                return false;
            }
        }

        boolean results = false;

        //判断牌型是否合法
//        if (getListByIsType(currentCardStruct.cards) == 0) {
//            return false;
//        }

        int currenttype = currentCardStruct.type;//获取当前出牌类型
        int lastType = lastcardStruct.type;//获取当前出牌类型

        if(currenttype == CardStruct.type_炸 || currenttype == CardStruct.type_火箭){
            if(lastType == CardStruct.type_炸 || lastType == CardStruct.type_火箭){
                return getZhaType(currentCardStruct) > getZhaType(lastcardStruct);
            }else{
                return true;
            }

        }

//        if ((currenttype == CardStruct.type_炸 || currenttype == CardStruct.type_火箭) && (lastType == CardStruct.type_炸 || lastType == CardStruct.type_火箭)) {
//            return getZhaType(currentCardStruct) > getZhaType(lastcardStruct);
//        }

        if (currenttype == lasttype) {
            List<Integer> lastList = lastcardStruct.getCards();//获取上次出牌的牌型
            List<Integer> list = currentCardStruct.getCards();//获取当前出牌类型

//            if (list.size() != lastList.size()) {
//                if(list.size() > lastList.size() && currentCardStruct.type == CardStruct.type_炸){// 3333>22
//                    return true;
//                }
//
//            } else {
            if (CardUtil.getTypeByCard(list.get(0)) > CardUtil.getTypeByCard(lastList.get(0))) {
                return true;
            }
//            }

        }

        return results;
    }


    private int getZhaType(CardStruct cardStruct) {

        List<Integer> cards = cardStruct.getCards();
        int firstCard = CardUtil.getTypeByCard(cards.get(0));
        int cardSize = cards.size();
        if (cardSize == 4 && firstCard == 0) {//四个三
            return 23;
        }
        if (firstCard == 14 || firstCard == 13) {//火箭
            return 22;
        }
        if (cardSize == 2 && firstCard == 12) {//两个二
            return 21;
        }

        if (cardSize == 2 && firstCard == 0) {//两个三
            return 1;
        }
        return firstCard;
    }
}
