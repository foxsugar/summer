package com.code.server.game.mahjong.logic;

import com.code.server.game.mahjong.util.HuCardType;
import com.code.server.game.mahjong.util.HuUtil;

import java.util.List;

import static com.code.server.game.mahjong.logic.GameInfoZhuohaozi.mode_明听;

/**
 * Created by sunxianping on 2018/6/26.
 */
public class PlayerCardsInfoHeleKD extends PlayerCardsInfoZhuohaozi {

    @Override
    public void ting(String card) {
        //出牌 弃牌置为空(客户端扣牌)
        this.cards.remove(card);
        String ifAnKou = this.gameInfo.room.getMode();
        if(!this.gameInfo.room.isHasMode(mode_明听)){
            this.disCards.add(null);
        }else {
            this.disCards.add(card);
        }

        this.isTing = true;
        tingSet = getTingCardType(cards, null);
        this.lastOperate = type_ting;
        operateList.add(type_ting);
        this.gameInfo.addUserOperate(this.userId, type_ting);
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
        if (isJinGang) score = 50;
        int allScore = 0;

        if (isMing && diangangUser != -1) {


            boolean isBaoAll = !this.gameInfo.getPlayerCardsInfos().get(diangangUser).isTing && !isHasMode(room.getMode(), GameInfoZhuohaozi.mode_大包);

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



        room.pushScoreChange();
    }



    @Override
    public void huCompute(RoomInfo room, GameInfo gameInfo, boolean isZimo, long dianpaoUser, String card) {

        //显庄 庄家输赢每家多10分
        //大包 返分情况
        PlayerCardsInfoMj dabaoUser = gameInfo.getPlayerCardsInfos().get(dianpaoUser);
        int opSize = 0;
        if (dabaoUser != null) {
            opSize = dabaoUser.operateList.size();
        }
        boolean isBaoAll = !isZimo && !(dabaoUser.isTing && opSize > 1 && dabaoUser.operateList.get(opSize - 1) != type_ting);
        boolean isDabao = isHasMode(room.getMode(), GameInfoZhuohaozi.mode_大包) && isBaoAll;
        //所有的杠都加回去
        if (isDabao) {

//            PlayerCardsInfoMj dabaoUser = gameInfo.getPlayerCardsInfos().get(dianpaoUser);
            for (PlayerCardsInfoMj playerCardsInfoMj : gameInfo.getPlayerCardsInfos().values()) {
                if (playerCardsInfoMj.getUserId() != dianpaoUser) {
                    if (playerCardsInfoMj.getScore() < 0) {

                        double s = playerCardsInfoMj.getScore();
                        playerCardsInfoMj.addScore(-s);
                        room.addUserSocre(playerCardsInfoMj.getUserId(), -s);

                        dabaoUser.addScore(s);
                        room.addUserSocre(dianpaoUser, s);
                    }


                }
            }
        }


        int lastCard = CardTypeUtil.getTypeByCard(card);
        int chiPengGangNum = getChiPengGangNum();
        List<HuCardType> huList = HuUtil.isHu(this, getCardsNoChiPengGang(this.cards), chiPengGangNum, this.gameInfo.hun, lastCard);
        int maxPoint = 0;
        HuCardType hct = null;
        for (HuCardType huCardType : huList) {

            int temp = getMaxPoint(huCardType, !isZimo) * getTimes(huCardType);
            if(temp > maxPoint){
                maxPoint = temp;
                hct = huCardType;
            }

        }

        setWinTypeResult(hct);

        boolean bankerIsZhuang = this.userId == this.gameInfo.getFirstTurn();

        //显庄 并且 赢得人是庄家
        boolean isBankerWinMore = bankerIsZhuang && isHasMode(this.roomInfo.mode, GameInfoZhuohaozi.mode_显庄);
        if(isBankerWinMore &&(isZimo || !isBaoAll)) maxPoint += 10;

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
                        if (playerCardsInfoMj.getUserId() == dianpaoUser) {
                            tempScore += 10;
                        }else{
                            tempScore += 10;
                        }
                    }
                }
                allScore += tempScore;

                if(!isBaoAll){
                    playerCardsInfoMj.addScore(-tempScore);
                    this.roomInfo.addUserSocre(playerCardsInfoMj.getUserId(), -tempScore);
                }

            }
        }

        if (isBaoAll) {
            PlayerCardsInfoMj dpUser = this.gameInfo.getPlayerCardsInfos().get(dianpaoUser);

            //点炮的是庄
            if (this.gameInfo.getFirstTurn() == dianpaoUser && isHasMode(this.roomInfo.mode, GameInfoZhuohaozi.mode_显庄)) {
                int temp = 10;
                allScore += temp;
            }
            if (isBankerWinMore && this.gameInfo.getFirstTurn() != dianpaoUser) {
                allScore += 10;
            }
            dpUser.addScore(-allScore);
            this.roomInfo.addUserSocre(dpUser.getUserId(), -allScore);
        }

        this.addScore(allScore);
        this.roomInfo.addUserSocre(this.userId, allScore);

    }
}
