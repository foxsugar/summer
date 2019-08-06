package com.code.server.game.mahjong.logic;

import com.code.server.game.mahjong.util.HuCardType;
import com.code.server.game.mahjong.util.HuUtil;

import java.util.List;

/**
 * Created by sunxianping on 2019-07-26.
 */
public class PlayerKDLongcheng extends PlayerCardsInfoKD {

    @Override
    public void init(List<String> cards) {
        super.init(cards);
        specialHuScore.put(hu_清一色,10);
        specialHuScore.put(hu_一条龙,10);
        specialHuScore.put(hu_七小对,10);
//        specialHuScore.put(hu_十三幺,4);
        specialHuScore.put(hu_豪华七小对,10);
        specialHuScore.put(hu_双豪七小对_山西,10);
//        specialHuScore.put(hu_清一色七小对,10);
        specialHuScore.put(hu_清七对,10);
        specialHuScore.put(hu_清龙,10);
    }



    @Override
    public void gangCompute(RoomInfo room, GameInfo gameInfo, boolean isMing, long diangangUser, String card) {
        this.lastOperate = type_gang;
        operateList.add(type_gang);
        this.gameInfo.addUserOperate(this.userId, type_gang);

        int allScore = 0;

        int cardType = CardTypeUtil.getTypeByCard(card);
        int score = CardTypeUtil.cardTingScore.get(cardType) * this.roomInfo.getMultiple() * 10;
        if (isMing && diangangUser != -1) {


            boolean isBaoAll = true;

            for (PlayerCardsInfoMj playerCardsInfoMj : this.gameInfo.playerCardsInfos.values()) {
                if (playerCardsInfoMj.getUserId() != this.userId) {
                    allScore += score;
                    if (!isBaoAll) {
                        playerCardsInfoMj.addGangScore(-score);
                        playerCardsInfoMj.addScore(-score);
                        this.roomInfo.addUserSocre(playerCardsInfoMj.getUserId(), -score);
                    }

                }

            }

            if (isBaoAll) {
                PlayerCardsInfoMj dianGangUser = this.gameInfo.getPlayerCardsInfos().get(diangangUser);
                dianGangUser.addGangScore(-allScore);
                dianGangUser.addScore(-allScore);
                this.roomInfo.addUserSocre(dianGangUser.getUserId(), -allScore);
            }
        } else {
            if (!isMing) score *= 2;
            for (PlayerCardsInfoMj playerCardsInfoMj : this.gameInfo.playerCardsInfos.values()) {
                if (playerCardsInfoMj.getUserId() != this.userId) {
                    playerCardsInfoMj.addGangScore(-score);
                    playerCardsInfoMj.addScore(-score);
                    allScore += score;
                    this.roomInfo.addUserSocre(playerCardsInfoMj.getUserId(), -score);
                }
            }
        }

        this.addGangScore(allScore);
        this.addScore(allScore);
        this.roomInfo.addUserSocre(this.getUserId(), allScore);
    }

    @Override
    public void huCompute(RoomInfo room, GameInfo gameInfo, boolean isZimo, long dianpaoUser, String card) {

        this.gameInfo.computeAllGang();


        int lastCard = CardTypeUtil.getTypeByCard(card);
        int chiPengGangNum = getChiPengGangNum();
        List<HuCardType> huList = HuUtil.isHu(this, getCardsNoChiPengGang(this.cards), chiPengGangNum, this.gameInfo.hun, lastCard);


        HuCardType huCardType = getMaxScoreHuCardType(huList);
        int score = huCardType.fan ;

        this.winType.addAll(huCardType.specialHuList);


        score += CardTypeUtil.cardTingScore.get(huCardType.tingCardType);

        if (isZimo) {
            score *= 2;
        }else{
            score *= 3;
        }

        score *= this.roomInfo.getMultiple() * 10;
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
}
