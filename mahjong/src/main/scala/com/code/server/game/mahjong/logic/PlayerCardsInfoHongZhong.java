package com.code.server.game.mahjong.logic;

import com.code.server.game.mahjong.util.HuCardType;
import com.code.server.game.mahjong.util.HuUtil;

import java.util.List;

/**
 * Created by sunxianping on 2018/8/14.
 */
public class PlayerCardsInfoHongZhong extends PlayerCardsInfoZhuohaozi {
    public static final int DA_HU = 1;
    public static final int HAS_HONGZHONG = 2;
    public static final int HUN_RAND = 3;
    public static final int HUN_NO = 4;
    public static final int NO_FENG = 5;

    @Override
    public void init(List<String> cards) {
        super.init(cards);

        if (isHasMode(this.roomInfo.mode, DA_HU)) {

            specialHuScore.put(hu_七小对, 9);
            specialHuScore.put(hu_豪华七小对, 18);

            specialHuScore.put(hu_清一色, 9);
            specialHuScore.put(hu_一条龙, 9);

        }




        this.TING_MIN_SCORE = 0;
        this.ZIMO_MIN_SCORE = 0;
    }


    @Override
    public boolean isCanHu_dianpao(String card) {
        return this.roomInfo.mustZimo == 0 && super.isCanHu_dianpao(card);
    }

    @Override
    public boolean isCanTing(List<String> cards) {
        if (!this.roomInfo.haveTing) {
            return false;
        }
        if (isTing) {
            return false;
        }

        List<HuCardType> huCardTypes = getTingHuCardTypeWithHun(getCardsNoChiPengGang(cards), this.gameInfo.hun, this.getChiPengGangNum());
        for (HuCardType huCardType : huCardTypes) {
            int point = getMaxPoint(huCardType, false);
            if (point >= TING_MIN_SCORE) {
                return true;
            }

        }
        return false;
    }

    @Override
    public void gangCompute(RoomInfo room, GameInfo gameInfo, boolean isMing, long diangangUser, String card) {
        int allScore = 0;
        int score = 0;
        if (diangangUser != -1) {
            PlayerCardsInfoMj dianPao = this.gameInfo.getPlayerCardsInfos().get(diangangUser);
            score = 3 * this.roomInfo.getMultiple();
            dianPao.addScore(-score);
            this.roomInfo.addUserSocre(diangangUser, -score);
            allScore += score;
        } else {
            score = isMing?this.roomInfo.getMultiple():2*this.roomInfo.getMultiple();
            for (PlayerCardsInfoMj playerCardsInfoMj : this.gameInfo.playerCardsInfos.values()) {
                if (playerCardsInfoMj.userId != this.userId) {
                    playerCardsInfoMj.addScore(-score);
                    playerCardsInfoMj.addGangScore(-score);
                    this.roomInfo.addUserSocre(playerCardsInfoMj.getUserId(), -score);
                    allScore += score;
                }
            }
        }
        this.addScore(allScore);
        this.addGangScore(allScore);
        this.roomInfo.addUserSocre(this.userId, allScore);
    }



    @Override
    public void huCompute(RoomInfo room, GameInfo gameInfo, boolean isZimo, long dianpaoUser, String card) {

        int lastCard = CardTypeUtil.getTypeByCard(card);
        int chiPengGangNum = getChiPengGangNum();
        List<HuCardType> huList = HuUtil.isHu(this, getCardsNoChiPengGang(this.cards), chiPengGangNum, this.gameInfo.hun, lastCard);


        HuCardType huCardType = getMaxScoreHuCardType(huList);
        int score = huCardType.fan;

        if (isZimo && huCardType.fan <= 9 && isHasMode(this.roomInfo.mode, HAS_HONGZHONG) && isHas4Hongzhong()) {
            this.winType.add(hu_四个红中);
            score = 10;
        }

        score *= this.roomInfo.getMultiple();
        int allScore = 0;
        if (isZimo) {
            for (PlayerCardsInfoMj playerCardsInfoMj : this.gameInfo.playerCardsInfos.values()) {
                if (playerCardsInfoMj.userId != this.userId) {
                    playerCardsInfoMj.addScore(-score);
                    this.roomInfo.addUserSocre(playerCardsInfoMj.getUserId(), -score);
                    allScore += score;
                }
            }
        } else {
            PlayerCardsInfoMj dianPao = this.gameInfo.getPlayerCardsInfos().get(dianpaoUser);

            dianPao.addScore(-score);
            this.roomInfo.addUserSocre(dianpaoUser, -score);
            allScore += score;
        }

        this.addScore(allScore);
        this.roomInfo.addUserSocre(this.userId, allScore);


    }

    private boolean isHas4Hongzhong(){
        return this.cards.stream().filter(card->CardTypeUtil.getTypeByCard(card) == 31).count() == 4;
    }
}
