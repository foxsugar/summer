package com.code.server.game.mahjong.logic;

import com.code.server.game.mahjong.util.HuCardType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sunxianping on 2018/1/10.
 */
public class PlayerCardsInfoNiuyezi extends PlayerCardsInfoHM {


    @Override
    public boolean isCanTing(List<String> cards) {

        List<String> temp = getCardsNoChiPengGang(cards);
        List<HuCardType> list = getTingHuCardType(temp,null);
        for (HuCardType huCardType : list) {
            //如果大于8张 就能胡
            if (getHuGroupCardNum(huCardType)>=8 && !isHasFeng(huCardType)) {
                return true;
            }
        }

        return false;
    }


    private boolean isHasFeng(HuCardType huCardType) {
        //碰
        for(Integer pengType: huCardType.peng){
            int group = CardTypeUtil.getCardGroupByCardType(pengType);
            if(group == CardTypeUtil.GROUP_FENG || group == CardTypeUtil.GROUP_ZI){
                return true;
            }

        }
        //杠
        for(Integer gangType : huCardType.mingGang){
            int group = CardTypeUtil.getCardGroupByCardType(gangType);
            if(group == CardTypeUtil.GROUP_FENG || group == CardTypeUtil.GROUP_ZI){
                return true;
            }
        }
        for(Integer gangType : huCardType.anGang){
            int group = CardTypeUtil.getCardGroupByCardType(gangType);
            if(group == CardTypeUtil.GROUP_FENG || group == CardTypeUtil.GROUP_ZI){
                return true;
            }
        }

        for(String card : huCardType.cards){
            int group = CardTypeUtil.getCardGroup(card);
            if(group == CardTypeUtil.GROUP_FENG || group == CardTypeUtil.GROUP_ZI){
                return true;
            }
        }
        return false;
    }

    private int getHuGroupCardNum(HuCardType huCardType) {
        Map<Integer, Integer> groupSize = new HashMap<>();
        //将
        mapAddCard(groupSize, huCardType.jiang,2);
        //碰
        for(Integer pengType: huCardType.peng){
            mapAddCard(groupSize, pengType,3);
        }
        //杠
        for(Integer gangType : huCardType.mingGang){
            mapAddCard(groupSize, gangType,4);
        }
        for(Integer gangType : huCardType.anGang){
            mapAddCard(groupSize, gangType,4);
        }

        for(Integer ke : huCardType.ke){
            mapAddCard(groupSize, ke,3);
        }
        for(Integer shun : huCardType.ke){
            mapAddCard(groupSize, shun,3);
        }

        int max = 0;
        for (Integer a : groupSize.values()) {
            if (a > max) {
                max = a;
            }
        }
        return max;
    }

    private static void mapAddCard(Map<Integer,Integer> map, int cardType, int size){
        int group = CardTypeUtil.getCardGroupByCardType(cardType);
        if (map.containsKey(group)) {
            map.put(group, map.get(group) + 1);
        }else{
            map.put(group, 1);
        }
    }
    @Override
    protected boolean isCanTingAfterGang(List<String> cards, int cardType, boolean isDianGang) {
        return super.isCanTingAfterGang(cards, cardType, isDianGang);
    }
}
