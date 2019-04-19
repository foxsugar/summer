package com.code.server.game.mahjong.logic;

import com.code.server.game.mahjong.util.HuCardType;
import com.code.server.game.mahjong.util.HuLimit;

import java.util.List;
import java.util.Map;

/**
 * Created by sunxianping on 2019-04-15.
 * <p>
 * 血战到底 麻将
 */
public class PlayerCardsInfoXZDD extends PlayerCardsInfoMj {
    @Override
    public void init(List<String> cards) {
        super.init(cards);

        specialHuScore.put(hu_碰碰胡, 1);
        specialHuScore.put(hu_清一色碰碰胡, 3);
        specialHuScore.put(hu_清一色, 2);
        specialHuScore.put(hu_七小对, 2);
        specialHuScore.put(hu_豪华七小对, 3);
        specialHuScore.put(hu_双豪七小对_山西, 3);
        specialHuScore.put(hu_清一色七小对, 4);
        specialHuScore.put(hu_清一色豪华七小对, 5);
        specialHuScore.put(hu_清一色双豪华七小对, 5);
        specialHuScore.put(hu_将对, 3);
        specialHuScore.put(hu_门清, 1);
        specialHuScore.put(hu_中张, 1);
        specialHuScore.put(hu_幺九, 3);
        specialHuScore.put(hu_将七对, 4);

    }


    @Override
    public boolean isHasChi(String card) {
        return false;
    }

    @Override
    public boolean isHasGang() {
        if (isAlreadyHu) return false;
        return super.isHasGang();
    }


    @Override
    public boolean isCanPengAddThisCard(String card) {
        if (isAlreadyHu) return false;
        return super.isCanPengAddThisCard(card);
    }

    @Override
    public boolean isCanGangAddThisCard(String card) {
        if (isAlreadyHu) return false;
        return super.isCanGangAddThisCard(card);
    }

    @Override
    public boolean isCanGangThisCard(String card) {
        if (isAlreadyHu) return false;
        return super.isCanGangThisCard(card);
    }

    @Override
    public boolean isCanHu_dianpao(String card) {
        if (isAlreadyHu) return false;
        return super.isCanHu_dianpao(card);
    }

    @Override
    public boolean isCanHu_zimo(String card) {
        if (isAlreadyHu) return false;
        return super.isCanHu_zimo(card);
    }

    @Override
    public boolean isCanTing(List<String> cards) {
        return false;
//        if (isAlreadyHu) return false;
//        return super.isCanTing(cards);
    }


    /**
     * 获得最大听牌分数
     * @return
     */
    protected int getMaxTingScore(){
        List<HuCardType> hulist = getTingHuCardType(this.cards, new HuLimit(0));
        HuCardType huCardType = getMaxScoreHuCardType(hulist);
        return huCardType.fan;
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

        int score = 1;
        int allScore = 0;
        if (isMing && diangangUser != -1) {
            score = 2;
            PlayerCardsInfoMj diangang = this.gameInfo.playerCardsInfos.get(diangangUser);
            diangang.addScore(-score);
            diangang.addGangScore(-score);
            this.roomInfo.addUserSocre(diangangUser, -score);
            allScore += score;
            this.otherGangScore.put(diangangUser,this.otherGangScore.getOrDefault(diangangUser,0D) +2 );

        } else {
            if(!isMing) score = 2;
            for (PlayerCardsInfoMj playerCardsInfoMj : this.gameInfo.playerCardsInfos.values()) {
                //不是自己并且没胡
                if (playerCardsInfoMj.getUserId() != this.userId) {
                    allScore += score;
                    playerCardsInfoMj.addScore(-score);
                    playerCardsInfoMj.addGangScore(-score);
                    this.roomInfo.addUserSocre(diangangUser, -score);
                    this.otherGangScore.put(playerCardsInfoMj.getUserId(),this.otherGangScore.getOrDefault(playerCardsInfoMj.getUserId(),0D) + score );
                }
            }

        }

        //加杠分
        this.addScore(allScore);
        this.addGangScore(allScore);
        this.roomInfo.addUserSocre(this.userId, allScore);



    }


}
