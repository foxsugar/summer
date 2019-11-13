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

//    @Override
//    public void gangCompute(RoomInfo room, GameInfo gameInfo, boolean isMing, long diangangUser, String card) {
//        this.lastOperate = type_gang;
//        operateList.add(type_gang);
//        this.gameInfo.addUserOperate(this.userId, type_gang);
//
//        if (isMing) {
//            this.roomInfo.addMingGangNum(this.getUserId());
//        } else {
//            this.roomInfo.addAnGangNum(this.getUserId());
//        }
//
//        int gangType = CardTypeUtil.getTypeByCard(card);
//        int score = CardTypeUtil.cardTingScore.get(gangType);
//        int allScore = 0;
//
//        if (isMing && diangangUser != -1) {
//
//            boolean isDianpaoTing = this.gameInfo.getPlayerCardsInfos().get(diangangUser).isTing;
//
//            for (PlayerCardsInfoMj playerCardsInfoMj : this.gameInfo.playerCardsInfos.values()) {
//                if (playerCardsInfoMj.getUserId() != this.userId) {
//                    allScore += score;
//                    if (isDianpaoTing) {
//                        playerCardsInfoMj.addGangScore(-score);
//                        playerCardsInfoMj.addScore(-score);
//                        this.roomInfo.addUserSocre(playerCardsInfoMj.getUserId(), -score);
//                    }
//
//                }
//
//            }
//
//            if (!isDianpaoTing) {
//                PlayerCardsInfoMj dianGangUser = this.gameInfo.getPlayerCardsInfos().get(diangangUser);
//                dianGangUser.addGangScore(-allScore);
//                dianGangUser.addScore(-allScore);
//                this.roomInfo.addUserSocre(dianGangUser.getUserId(), -allScore);
//            }
//        } else {
//            if (!isMing) score *= 2;
//            for (PlayerCardsInfoMj playerCardsInfoMj : this.gameInfo.playerCardsInfos.values()) {
//                if (playerCardsInfoMj.getUserId() != this.userId) {
//                    allScore += score;
//                    playerCardsInfoMj.addGangScore(-score);
//                    playerCardsInfoMj.addScore(-score);
//                    this.roomInfo.addUserSocre(playerCardsInfoMj.getUserId(), -score);
//                }
//            }
//        }
//
//        this.addGangScore(allScore);
//        this.addScore(allScore);
//        this.roomInfo.addUserSocre(this.getUserId(), allScore);
//
//
//        room.pushScoreChange();
//    }


    @Override
    public void gangCompute(RoomInfo room, GameInfo gameInfo, boolean isMing, long diangangUser, String card) {
        int allScore = 0;
        int score = 0;
        if (diangangUser != -1) {
            PlayerCardsInfoMj dianPao = this.gameInfo.getPlayerCardsInfos().get(diangangUser);
            boolean isDianpaoTing = this.gameInfo.getPlayerCardsInfos().get(diangangUser).isTing;

            score =  this.roomInfo.getMultiple();


            for (PlayerCardsInfoMj playerCardsInfoMj : this.gameInfo.playerCardsInfos.values()) {
                if (playerCardsInfoMj.getUserId() != this.userId) {
                    if (isDianpaoTing) {
                        playerCardsInfoMj.addGangScore(-score);
                        playerCardsInfoMj.addScore(-score);
                        this.roomInfo.addUserSocre(playerCardsInfoMj.getUserId(), -score);

                    }
                    allScore += score;

                }

            }

            if (!isDianpaoTing) {

                PlayerCardsInfoMj dianGangUser = this.gameInfo.getPlayerCardsInfos().get(diangangUser);
                dianGangUser.addGangScore(-allScore);
                dianGangUser.addScore(-allScore);
                this.roomInfo.addUserSocre(dianGangUser.getUserId(), -allScore);
            }




            dianPao.addScore(-score);
            this.roomInfo.addUserSocre(diangangUser, -score);

        } else {
            score = isMing ? this.roomInfo.getMultiple() : 2 * this.roomInfo.getMultiple();
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

        this.winType.addAll(huCardType.specialHuList);


        if (score == 0) {
            score = 1;
        }


        if (isZimo) {
            //普通户*2倍   特殊* 3
            if (score == 1) {
                score *= 2;
            }else{
                score *= 3;
            }

        } else {
//            score *= (room.getPersonNumber() - 1);
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
                score *= (room.getPersonNumber() - 1);
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
