package com.code.server.game.mahjong.logic;

import java.util.List;
import java.util.Map;

/**
 * Created by sunxianping on 2019-04-15.
 *
 * 血战到底 麻将
 */
public class PlayerCardsInfoXZDD extends PlayerCardsInfoMj{
    @Override
    public void init(List<String> cards) {
        super.init(cards);

//        specialHuScore.put(hu_清一色,2);
        specialHuScore.put(hu_吊将, 0);

    }


    @Override
    public boolean isHasChi(String card) {
        return false;
    }

    @Override
    public boolean isHasGang() {
        if(isAlreadyHu) return false;
        return super.isHasGang();
    }


    @Override
    public boolean isCanPengAddThisCard(String card) {
        if(isAlreadyHu) return false;
        return super.isCanPengAddThisCard(card);
    }

    @Override
    public boolean isCanGangAddThisCard(String card) {
        if(isAlreadyHu) return false;
        return super.isCanGangAddThisCard(card);
    }

    @Override
    public boolean isCanGangThisCard(String card) {
        if(isAlreadyHu) return false;
        return super.isCanGangThisCard(card);
    }

    @Override
    public boolean isCanHu_dianpao(String card) {
        if(isAlreadyHu) return false;
        return super.isCanHu_dianpao(card);
    }

    @Override
    public boolean isCanHu_zimo(String card) {
        if(isAlreadyHu) return false;
        return super.isCanHu_zimo(card);
    }

    @Override
    public boolean isCanTing(List<String> cards) {
        if(isAlreadyHu) return false;
        return super.isCanTing(cards);
    }



    /**
     * 杠手里的牌
     *
     * @param diangangUser
     * @param card
     * @return
     */

    public boolean gang_hand(RoomInfo room, GameInfo info, long diangangUser, String card) {
        boolean isMing = false;
        int cardType = CardTypeUtil.cardType.get(card);
        Map<Integer, Integer> cardNum = getCardNum(cards);
        long diangang = -1;
        if (cardNum.containsKey(cardType) && cardNum.get(cardType) == 4) {
            if (pengType.containsKey(cardType)) {//碰的类型包含这个 是明杠
                //判断是否过了一圈才杠的
                //最后一个碰的类型
                int lastPengType = pengList.get(pengList.size() - 1);
                if (lastPengType == cardType) {

                }
                pengType.remove(cardType);//从碰中移除
                pengList.remove(Integer.valueOf(cardType));
                mingGangType.put(cardType, diangang);
                isMing = true;

            } else {
                anGangType.add(cardType);
                isMing = false;

            }
        }
        return isMing;
    }


    @Override
    public void gangCompute(RoomInfo room, GameInfo gameInfo, boolean isMing, long diangangUser, String card) {
        super.gangCompute(room, gameInfo, isMing, diangangUser, card);
        //算分

        //碰后杠 过一圈不算分


    }



}
