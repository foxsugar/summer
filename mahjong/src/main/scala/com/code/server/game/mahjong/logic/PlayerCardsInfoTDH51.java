package com.code.server.game.mahjong.logic;

import com.code.server.game.mahjong.util.HuCardType;
import com.code.server.game.mahjong.util.HuUtil;

import java.util.List;

/**
 * Created by sunxianping on 2019-03-22.
 */
public class PlayerCardsInfoTDH51 extends PlayerCardsInfoHongZhong {


    @Override
    public void init(List<String> cards) {
        super.init(cards);

        specialHuScore.clear();

        specialHuScore.put(hu_七小对, 1);
        specialHuScore.put(hu_豪华七小对, 1);
        specialHuScore.put(hu_双豪七小对_山西, 1);

        if (isHasMode(this.roomInfo.mode, DA_HU)) {

            specialHuScore.put(hu_七小对, 3);
            specialHuScore.put(hu_豪华七小对, 6);
            specialHuScore.put(hu_双豪七小对_山西, 6);

            specialHuScore.put(hu_清一色, 3);
            specialHuScore.put(hu_一条龙, 3);
            specialHuScore.put(hu_清龙, 3);
            specialHuScore.put(hu_十三幺, 9);

        }




        this.TING_MIN_SCORE = 0;
        this.ZIMO_MIN_SCORE = 0;
        this.DIANPAO_MIN_SCORE = 0;
    }

    @Override
    public void huCompute(RoomInfo room, GameInfo gameInfo, boolean isZimo, long dianpaoUser, String card) {

        int lastCard = CardTypeUtil.getTypeByCard(card);
        int chiPengGangNum = getChiPengGangNum();
        List<HuCardType> huList = HuUtil.isHu(this, getCardsNoChiPengGang(this.cards), chiPengGangNum, this.gameInfo.hun, lastCard);


        HuCardType huCardType = getMaxScoreHuCardType(huList);
        int score = huCardType.fan;

        this.winType.addAll(huCardType.specialHuList);


        if (score == 0) {
            score = 1;
        }


        if (isZimo) {
            score *= 3;
        } else {
            score *= (room.getPersonNumber() - 1);
        }

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
            //点炮三家出
            if (isHasMode(this.roomInfo.mode, DIANPAOSANJIACHU)) {
                boolean isBao = !dianPao.isTing;
                for (PlayerCardsInfoMj playerCardsInfoMj : this.gameInfo.playerCardsInfos.values()) {
                    if (playerCardsInfoMj.userId != this.userId) {
                        if (!isBao) {
                            playerCardsInfoMj.addScore(-score);
                            this.roomInfo.addUserSocre(playerCardsInfoMj.getUserId(), -score);
                        }
                        allScore += score;
                    }
                }

                if (isBao) {
                    dianPao.addScore(-allScore);
                    this.roomInfo.addUserSocre(dianPao.getUserId(), -allScore);
                }


            } else {//点炮一个人出
                //不带包听
                dianPao.addScore(-score);
                this.roomInfo.addUserSocre(dianpaoUser, -score);
                allScore += score;
            }
        }

        this.addScore(allScore);
        this.roomInfo.addUserSocre(this.userId, allScore);


    }
}
