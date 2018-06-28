package com.code.server.game.mahjong.logic;

import com.code.server.game.mahjong.util.HuCardType;
import com.code.server.game.mahjong.util.HuUtil;
import com.code.server.redis.service.RedisManager;

import java.util.List;

/**
 * Created by sunxianping on 2018/6/26.
 */
public class PlayerCardsInfoHeleKDGold extends PlayerCardsInfoHeleKD {


    @Override
    public double addScore(double s) {
        if (s < 0) {
            double temp = -s;
            double nowGold = RedisManager.getUserRedisService().getUserGold(userId);
            if (nowGold < temp) {
                this.score = this.score - nowGold;
                return -nowGold;
            }

        }
        this.score = this.score + s;
        return s;

    }


    @Override
    public void gangCompute(RoomInfo room, GameInfo gameInfo, boolean isMing, long diangangUser, String card) {
        this.lastOperate = type_gang;
        operateList.add(type_gang);
        this.gameInfo.addUserOperate(this.userId, type_gang);

        if (isMing) {
            this.roomInfo.addMingGangNum(this.getUserId());
        }else{
            this.roomInfo.addAnGangNum(this.getUserId());
        }

        int gangType = CardTypeUtil.getTypeByCard(card);
        boolean isJinGang = this.gameInfo.hun.contains(gangType);
        int score = CardTypeUtil.cardTingScore.get(gangType);

        if (isHasMode(room.getMode(), GameInfoZhuohaozi.mode_摸四胡五)) {
            score = 5;
        }
        if (isJinGang) score = 100;
        score *= room.getMultiple();
        int allScore = 0;
        int allGangScore = 0;

        if (isMing && diangangUser != -1) {


            boolean isBaoAll = !this.gameInfo.getPlayerCardsInfos().get(diangangUser).isTing && !isHasMode(room.getMode(), GameInfoZhuohaozi.mode_大包);

            for (PlayerCardsInfoMj playerCardsInfoMj : this.gameInfo.playerCardsInfos.values()) {
                if (playerCardsInfoMj.getUserId() != this.userId) {
                    if (!isBaoAll) {
                        playerCardsInfoMj.addGangScore(-score);
                        double realScore = playerCardsInfoMj.addScore(-score);
                        allScore += realScore;
                        allGangScore += score;
                        this.roomInfo.addUserSocre(playerCardsInfoMj.getUserId(), -score);
                    }

                }

            }

            if (isBaoAll) {
                double t = score * 3;
                PlayerCardsInfoMj dianGangUser = this.gameInfo.getPlayerCardsInfos().get(diangangUser);
                dianGangUser.addGangScore(-(int)t);
                double realScore = dianGangUser.addScore(-t);
                this.roomInfo.addUserSocre(dianGangUser.getUserId(), -t);
                allScore += realScore;
                allGangScore += t;
            }
        } else {
            if (!isMing) score *= 2;
            for (PlayerCardsInfoMj playerCardsInfoMj : this.gameInfo.playerCardsInfos.values()) {
                if (playerCardsInfoMj.getUserId() != this.userId) {
                    playerCardsInfoMj.addGangScore(-score);
                    double realScote = playerCardsInfoMj.addScore(-score);
                    allScore += realScote;
                    this.roomInfo.addUserSocre(playerCardsInfoMj.getUserId(), -score);
                    allGangScore += score;
                }
            }
        }

        this.addGangScore(allGangScore);
        this.addScore(allScore);
        this.roomInfo.addUserSocre(this.getUserId(), allScore);



        room.pushScoreChange();
    }


    @Override
    public void huCompute(RoomInfo room, GameInfo gameInfo, boolean isZimo, long dianpaoUser, String card) {

        //显庄 庄家输赢每家多10分
        //大包 返分情况
        boolean isBaoAll = !isZimo && !this.gameInfo.getPlayerCardsInfos().get(dianpaoUser).isTing;
        boolean isDabao = isHasMode(room.getMode(), GameInfoZhuohaozi.mode_大包) && isBaoAll;
        //所有的杠都加回去
        if (isDabao) {
            PlayerCardsInfoMj dabaoUser = gameInfo.getPlayerCardsInfos().get(dianpaoUser);
            for (PlayerCardsInfoMj playerCardsInfoMj : gameInfo.getPlayerCardsInfos().values()) {
                if (playerCardsInfoMj.getUserId() != this.userId) {
                    playerCardsInfoMj.addScore(-playerCardsInfoMj.getScore());
                    room.addUserSocre(playerCardsInfoMj.getUserId(), -playerCardsInfoMj.getScore());

                    double realScore = dabaoUser.addScore(playerCardsInfoMj.getGangScore());
                    room.addUserSocre(dianpaoUser, playerCardsInfoMj.getGangScore());

                    playerCardsInfoMj.addScore(realScore);
                    room.addUserSocre(playerCardsInfoMj.getUserId(), realScore);

                }
            }
        }


        int lastCard = CardTypeUtil.getTypeByCard(card);
        int chiPengGangNum = getChiPengGangNum();
        List<HuCardType> huList = HuUtil.isHu(this, getCardsNoChiPengGang(this.cards), chiPengGangNum, this.gameInfo.hun, lastCard);
        int maxPoint = 0;
        for (HuCardType huCardType : huList) {

            int temp = getMaxPoint(huCardType, !isZimo);
            if(temp > maxPoint){
                maxPoint = temp;
            }

        }
        boolean bankerIsZhuang = this.userId == this.gameInfo.getFirstTurn();

        //显庄 并且 赢得人是庄家
        boolean isBankerWinMore = bankerIsZhuang && isHasMode(this.roomInfo.mode, GameInfoZhuohaozi.mode_显庄);
        if(isBankerWinMore) maxPoint += 10;

        if(isZimo) maxPoint *= 2;



        int allScore = 0;

        for (PlayerCardsInfoMj playerCardsInfoMj : this.gameInfo.playerCardsInfos.values()) {
            if (playerCardsInfoMj.getUserId() != this.userId) {

                int tempScore = maxPoint;
                //庄家多输
                if (playerCardsInfoMj.getUserId() == this.gameInfo.getFirstTurn() && isHasMode(this.roomInfo.mode, GameInfoZhuohaozi.mode_显庄)) {
                    if(isZimo) {
                        tempScore += 20;
                    }else{
                        tempScore += 10;
                    }
                }
                tempScore *= room.getMultiple();
                allScore += tempScore;

                double realScore = 0;
                if(!isBaoAll){
                    realScore = playerCardsInfoMj.addScore(-tempScore);
                    this.roomInfo.addUserSocre(playerCardsInfoMj.getUserId(), -tempScore);
                }else{
                    PlayerCardsInfoMj dpUser = this.gameInfo.getPlayerCardsInfos().get(dianpaoUser);
                    realScore = dpUser.addScore(-tempScore);
                    this.roomInfo.addUserSocre(dpUser.getUserId(), -tempScore);
                }
                allScore += realScore;
            }
        }



        this.addScore(allScore);
        this.roomInfo.addUserSocre(this.userId, allScore);

    }

}
