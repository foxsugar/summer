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
            } else {
                this.score = this.score + s;
            }
        } else {
            this.score = this.score + s;
        }
        return s;

    }


    private int getGangScore(int cardType, boolean isMing, GameInfo gameInfo, RoomInfo room) {
        boolean isJinGang = this.gameInfo.hun.contains(cardType);
        int score = CardTypeUtil.cardTingScore.get(cardType);

        if (isHasMode(room.getMode(), GameInfoZhuohaozi.mode_摸四胡五)) {
            score = 5;
        }
        if (isJinGang) score = 100;
        score *= room.getMultiple();
        if (!isMing) {
            score *= 2;
        }
        return score;
    }

    private int getAllGangScore(PlayerCardsInfoMj playerCardsInfoMj, GameInfo gameInfo, RoomInfo roomInfo) {
        int all = 0;
        for (int ct : playerCardsInfoMj.getMingGangType().keySet()) {
            all += getGangScore(ct, true, gameInfo, roomInfo);
        }
        for (int ct : playerCardsInfoMj.getAnGangType()) {
            all += getGangScore(ct, false, gameInfo, roomInfo);
        }
        return all;
    }

    @Override
    public void gangCompute(RoomInfo room, GameInfo gameInfo, boolean isMing, long diangangUser, String card) {
        this.lastOperate = type_gang;
        operateList.add(type_gang);
        this.gameInfo.addUserOperate(this.userId, type_gang);

        if (isMing) {
            this.roomInfo.addMingGangNum(this.getUserId());
        } else {
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
                        allScore += -realScore;
                        allGangScore += score;
                        this.roomInfo.addUserSocre(playerCardsInfoMj.getUserId(), -score);
                    }

                }

            }

            if (isBaoAll) {
                double t = score * 3;
                PlayerCardsInfoMj dianGangUser = this.gameInfo.getPlayerCardsInfos().get(diangangUser);
                dianGangUser.addGangScore(-(int) t);
                double realScore = dianGangUser.addScore(-t);
                this.roomInfo.addUserSocre(dianGangUser.getUserId(), -t);
                allScore += -realScore;
                allGangScore += t;
            }
        } else {
            if (!isMing) score *= 2;
            for (PlayerCardsInfoMj playerCardsInfoMj : this.gameInfo.playerCardsInfos.values()) {
                if (playerCardsInfoMj.getUserId() != this.userId) {
                    playerCardsInfoMj.addGangScore(-score);
                    double realScore = playerCardsInfoMj.addScore(-score);
                    allScore += -realScore;
                    this.roomInfo.addUserSocre(playerCardsInfoMj.getUserId(), -score);
                    allGangScore += -score;
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
        PlayerCardsInfoMj dabaoUser = gameInfo.getPlayerCardsInfos().get(dianpaoUser);
        int opSize = 0;
        if (dabaoUser != null) {
            opSize = dabaoUser.operateList.size();
        }
        boolean isBaoAll = !isZimo && !(dabaoUser.isTing && opSize > 1 && dabaoUser.operateList.get(opSize - 1) != type_ting);
        boolean isDabao = isHasMode(room.getMode(), GameInfoZhuohaozi.mode_大包) && isBaoAll;

        for (PlayerCardsInfoMj playerCardsInfoMj : gameInfo.getPlayerCardsInfos().values()) {
            System.out.println("玩家 " + playerCardsInfoMj.getUserId() + " : 目前分数" + playerCardsInfoMj.getScore() + " 应该的杠分: " + playerCardsInfoMj.getGangScore());
        }
        //所有的杠都加回去
        if (isDabao) {
            //分数清0


            for (PlayerCardsInfoMj playerCardsInfoMj : gameInfo.getPlayerCardsInfos().values()) {
//                if (playerCardsInfoMj.getUserId() != dianpaoUser) {
                double temp = -playerCardsInfoMj.getScore();
                room.addUserSocre(playerCardsInfoMj.getUserId(), -playerCardsInfoMj.getScore());
                playerCardsInfoMj.addScore(-playerCardsInfoMj.getScore());
//
                System.out.println("先减掉分数 " + playerCardsInfoMj.getUserId() + "  减了 " + temp + "  目前分数: " + playerCardsInfoMj.getScore());
//                    double realScore = dabaoUser.addScore(-playerCardsInfoMj.getGangScore());
//                    room.addUserSocre(dianpaoUser, -playerCardsInfoMj.getGangScore());
//                    System.out.println("点炮者输分 " + -dabaoUser.getGangScore() +" 实际输: " + realScore + "  当前分数: " + dabaoUser.getScore());
//
//                    playerCardsInfoMj.addScore(-realScore);
//                    room.addUserSocre(playerCardsInfoMj.getUserId(), -realScore);
//
//                    System.out.println("加上分数 " + playerCardsInfoMj.getUserId() + " 加了 : " + -realScore + " 当前分数: " + playerCardsInfoMj.getScore());

//                }
            }


            for (PlayerCardsInfoMj playerCardsInfoMj : gameInfo.getPlayerCardsInfos().values()) {

                if (playerCardsInfoMj.getUserId() != dianpaoUser) {
                    int gangScore = getAllGangScore(playerCardsInfoMj, gameInfo, room);
                    double realScore = dabaoUser.addScore(-3 * gangScore);
                    room.addUserSocre(dabaoUser.getUserId(), -3 * gangScore);

                    System.out.println("点炮者输分 " + dabaoUser.getUserId() + " " + " 实际输: " + realScore + "  当前分数: " + dabaoUser.getScore());

                    playerCardsInfoMj.addScore(-realScore);
                    room.addUserSocre(playerCardsInfoMj.getUserId(), -realScore);
                    System.out.println("加上分数 " + playerCardsInfoMj.getUserId() + " 加了 : " + -realScore + " 当前分数: " + playerCardsInfoMj.getScore());
                }
            }
        }


        int lastCard = CardTypeUtil.getTypeByCard(card);
        int chiPengGangNum = getChiPengGangNum();
        List<HuCardType> huList = HuUtil.isHu(this, getCardsNoChiPengGang(this.cards), chiPengGangNum, this.gameInfo.hun, lastCard);
        int maxPoint = 0;
        for (HuCardType huCardType : huList) {

            int temp = getMaxPoint(huCardType, !isZimo);
            if (temp > maxPoint) {
                maxPoint = temp;
            }

        }
        boolean bankerIsZhuang = this.userId == this.gameInfo.getFirstTurn();

        //显庄 并且 赢得人是庄家
        boolean isBankerWinMore = bankerIsZhuang && isHasMode(this.roomInfo.mode, GameInfoZhuohaozi.mode_显庄);
        if (isBankerWinMore) maxPoint += 10;

        if (isZimo) maxPoint *= 2;


        int allScore = 0;

        for (PlayerCardsInfoMj playerCardsInfoMj : this.gameInfo.playerCardsInfos.values()) {
            if (playerCardsInfoMj.getUserId() != this.userId) {

                int tempScore = maxPoint;
                //庄家多输
                if (playerCardsInfoMj.getUserId() == this.gameInfo.getFirstTurn() && isHasMode(this.roomInfo.mode, GameInfoZhuohaozi.mode_显庄)) {
                    if (isZimo) {
                        tempScore += 20;
                    } else {
                        if (playerCardsInfoMj.getUserId() == dianpaoUser) {
                            tempScore += 30;
                        } else {
                            tempScore += 10;
                        }
                    }
                }
                tempScore *= room.getMultiple();
//                allScore += tempScore;

                double realScore = 0;
                if (!isBaoAll) {
                    realScore = -playerCardsInfoMj.addScore(-tempScore);
                    this.roomInfo.addUserSocre(playerCardsInfoMj.getUserId(), -tempScore);
                    System.out.println("胡牌 玩家" + playerCardsInfoMj.getUserId() + " 输 " + realScore + " 目前分数 : " + playerCardsInfoMj.getScore());
                } else {
                    PlayerCardsInfoMj dpUser = this.gameInfo.getPlayerCardsInfos().get(dianpaoUser);
                    realScore = -dpUser.addScore(-tempScore);
                    this.roomInfo.addUserSocre(dpUser.getUserId(), -tempScore);
                    System.out.println("包胡 点炮者" + dpUser.getUserId() + " 输 " + realScore + " 目前分数 : " + dpUser.getScore());
                }
                allScore += realScore;
            }
        }


        this.addScore(allScore);
        this.roomInfo.addUserSocre(this.userId, allScore);
        System.out.println("胡牌 " + this.userId + " 赢: " + allScore + " 当前 : " + this.getScore());

    }

}
