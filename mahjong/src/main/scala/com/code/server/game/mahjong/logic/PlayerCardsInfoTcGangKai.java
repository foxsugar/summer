package com.code.server.game.mahjong.logic;

import com.code.server.game.mahjong.util.*;

import java.util.List;

/**
 * Created by sunxianping on 2018/6/6.
 */
public class PlayerCardsInfoTcGangKai extends PlayerCardsInfoTDH {


    @Override
    public void huCompute(RoomInfo room, GameInfo gameInfo, boolean isZimo, long dianpaoUser, String card) {
        List<String> cs = getCardsNoChiPengGang(cards);
        List<HuCardType> huList = HuUtil.isHu(cs, this, CardTypeUtil.cardType.get(card), new HuLimit(0));
        //设置胡牌类型
        HuCardType huCardType = getMaxScoreHuCardType(huList);
        this.winType.addAll(huCardType.specialHuList);

//        if(this.roomInfo.getGameType().equals("TC")){
        if (isZimo) {
            if ((room.getMode().equals("1") || room.getMode().equals("3") || room.getMode().equals("11") || room.getMode().equals("13"))) {//平胡
                if (this.userId == this.gameInfo.firstTurn) {//庄赢
                    for (Long i : gameInfo.getPlayerCardsInfos().keySet()) {
                        gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - 4 * room.getMultiple());
                        room.setUserSocre(i, -4 * room.getMultiple());
                    }
                    this.score = this.score + 4 * room.getPersonNumber() * room.getMultiple();
                    room.setUserSocre(this.userId, 4 * room.getPersonNumber() * room.getMultiple());
                    this.fan = 2;
                } else {
                    for (Long i : gameInfo.getPlayerCardsInfos().keySet()) {
                        gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - 2 * room.getMultiple());
                        room.setUserSocre(i, -2 * room.getMultiple());
                    }
                    //庄输2倍，在此多减一倍
                    gameInfo.getPlayerCardsInfos().get(this.gameInfo.firstTurn).setScore(gameInfo.getPlayerCardsInfos().get(this.gameInfo.firstTurn).getScore() - 2 * room.getMultiple());
                    room.setUserSocre(this.gameInfo.firstTurn, -2 * room.getMultiple());

                    this.score = this.score + (room.getPersonNumber() + 1) * 2 * room.getMultiple();
                    room.setUserSocre(this.userId, (room.getPersonNumber() + 1) * 2 * room.getMultiple());
                    this.fan = 2;
                }
                    /*for (Long i : gameInfo.getPlayerCardsInfos().keySet()){
						gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - 2 * room.getMultiple());
						room.setUserSocre(i, - 2 * room.getMultiple());
					}
					this.score = this.score +  2 * room.getPersonNumber() * room.getMultiple();
					room.setUserSocre(this.userId, 2 * room.getPersonNumber() * room.getMultiple());
					this.fan = 2;*/
//        			this.winType.add(HuType.hu_普通胡);
            } else if ((room.getMode().equals("2") || room.getMode().equals("4") || room.getMode().equals("12") || room.getMode().equals("14"))) {//大胡
                if (3 == MahjongCode.HUTOSCOREFORLQ.get("" + CardUtil.huForScores(cards, huCardType) + "")) {
                    if (this.userId == this.gameInfo.firstTurn) {//庄赢
                        for (Long i : gameInfo.getPlayerCardsInfos().keySet()) {
                            gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - 4 * room.getMultiple());
                            room.setUserSocre(i, -4 * room.getMultiple());
                        }
                        this.score = this.score + 4 * room.getPersonNumber() * room.getMultiple();
                        room.setUserSocre(this.userId, 4 * room.getPersonNumber() * room.getMultiple());
                        this.fan = 2;
                    } else {
                        for (Long i : gameInfo.getPlayerCardsInfos().keySet()) {
                            gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - 2 * room.getMultiple());
                            room.setUserSocre(i, -2 * room.getMultiple());
                        }
                        //庄输2倍，在此多减一倍
                        gameInfo.getPlayerCardsInfos().get(this.gameInfo.firstTurn).setScore(gameInfo.getPlayerCardsInfos().get(this.gameInfo.firstTurn).getScore() - 2 * room.getMultiple());
                        room.setUserSocre(this.gameInfo.firstTurn, -2 * room.getMultiple());

                        this.score = this.score + (room.getPersonNumber() + 1) * 2 * room.getMultiple();
                        room.setUserSocre(this.userId, (room.getPersonNumber() + 1) * 2 * room.getMultiple());
                        this.fan = 2;
                    }
                } else {
                    if (this.userId == this.gameInfo.firstTurn) {//庄赢
                        for (Long i : gameInfo.getPlayerCardsInfos().keySet()) {
                            gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - MahjongCode.HUTOSCOREFORLQ.get("" + CardUtil.huForScores(cards, huCardType)) * room.getMultiple() * 2);
                            room.setUserSocre(i, -MahjongCode.HUTOSCOREFORLQ.get("" + CardUtil.huForScores(cards, huCardType)) * room.getMultiple() * 2);
                        }
                        this.score = this.score + MahjongCode.HUTOSCOREFORLQ.get("" + CardUtil.huForScores(cards, huCardType)) * room.getPersonNumber() * room.getMultiple() * 2;
                        room.setUserSocre(this.userId, MahjongCode.HUTOSCOREFORLQ.get("" + CardUtil.huForScores(cards, huCardType)) * room.getPersonNumber() * room.getMultiple() * 2);
                        this.fan = MahjongCode.HUTOSCOREFORLQ.get("" + CardUtil.huForScores(cards, huCardType));
                    } else {
                        for (Long i : gameInfo.getPlayerCardsInfos().keySet()) {
                            gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - MahjongCode.HUTOSCOREFORLQ.get("" + CardUtil.huForScores(cards, huCardType)) * room.getMultiple());
                            room.setUserSocre(i, -MahjongCode.HUTOSCOREFORLQ.get("" + CardUtil.huForScores(cards, huCardType)) * room.getMultiple());
                        }
                        //庄输2倍，在此多减一倍
                        gameInfo.getPlayerCardsInfos().get(this.gameInfo.firstTurn).setScore(gameInfo.getPlayerCardsInfos().get(this.gameInfo.firstTurn).getScore() - MahjongCode.HUTOSCOREFORLQ.get("" + CardUtil.huForScores(cards, huCardType)) * room.getMultiple());
                        room.setUserSocre(this.gameInfo.firstTurn, -MahjongCode.HUTOSCOREFORLQ.get("" + CardUtil.huForScores(cards, huCardType)) * room.getMultiple());

                        this.score = this.score + (room.getPersonNumber() + 1) * MahjongCode.HUTOSCOREFORLQ.get("" + CardUtil.huForScores(cards, huCardType)) * room.getMultiple();
                        room.setUserSocre(this.userId, (room.getPersonNumber() + 1) * MahjongCode.HUTOSCOREFORLQ.get("" + CardUtil.huForScores(cards, huCardType)) * room.getMultiple());
                        this.fan = MahjongCode.HUTOSCOREFORLQ.get("" + CardUtil.huForScores(cards, huCardType));
                    }
						/*for (Long i : gameInfo.getPlayerCardsInfos().keySet()){
							gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - room.getMultiple() * MahjongCode.HUTOSCOREFORLQ.get(""+CardUtil.huForScores(cards,huCardType)));
							room.setUserSocre(i,  - room.getMultiple() * MahjongCode.HUTOSCOREFORLQ.get(""+CardUtil.huForScores(cards,huCardType)));
						}
						this.score = this.score + room.getPersonNumber() * room.getMultiple() * MahjongCode.HUTOSCOREFORLQ.get(""+CardUtil.huForScores(cards,huCardType)+"");
						room.setUserSocre(this.userId, room.getPersonNumber() * room.getMultiple() * MahjongCode.HUTOSCOREFORLQ.get(""+CardUtil.huForScores(cards,huCardType)+""));
						this.fan = MahjongCode.HUTOSCOREFORLQ.get(""+CardUtil.huForScores(cards,huCardType)+"");*/
//                		this.winType = CardUtil.huForWinType(cards);
                }
            }

            if (isGangKai()) {
                for (PlayerCardsInfoMj playerCardsInfoMj : this.gameInfo.playerCardsInfos.values()) {
                    room.addUserSocre(playerCardsInfoMj.getUserId(), playerCardsInfoMj.getScore());
                    playerCardsInfoMj.setScore(playerCardsInfoMj.getScore() * 2);
                    this.fan *= 2;

                }
            }
        } else {
            if ((room.getMode().equals("1") || room.getMode().equals("3") || room.getMode().equals("11") || room.getMode().equals("13"))) {//平胡
                if (this.userId == this.gameInfo.firstTurn) {//庄赢
                    gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - ((room.getPersonNumber() - 1)) * room.getMultiple() * 2);
                    this.score = this.score + ((room.getPersonNumber() - 1)) * room.getMultiple() * 2;
                    room.setUserSocre(dianpaoUser, -((room.getPersonNumber() - 1)) * room.getMultiple() * 2);
                    room.setUserSocre(this.userId, ((room.getPersonNumber() - 1)) * room.getMultiple() * 2);
                    this.fan = 1;
                } else {
                    if (dianpaoUser == this.gameInfo.firstTurn) {//庄点炮
                        gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - ((room.getPersonNumber() - 1)) * room.getMultiple() * 2);
                        this.score = this.score + ((room.getPersonNumber() - 1)) * room.getMultiple() * 2;
                        room.setUserSocre(dianpaoUser, -((room.getPersonNumber() - 1)) * room.getMultiple() * 2);
                        room.setUserSocre(this.userId, ((room.getPersonNumber() - 1)) * room.getMultiple() * 2);
                        this.fan = 1;
                    } else {
                        gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - ((room.getPersonNumber() - 1)) * room.getMultiple());
                        this.score = this.score + ((room.getPersonNumber() - 1)) * room.getMultiple();
                        room.setUserSocre(dianpaoUser, -((room.getPersonNumber() - 1)) * room.getMultiple());
                        room.setUserSocre(this.userId, ((room.getPersonNumber() - 1)) * room.getMultiple());
                        this.fan = 1;
                    }
                }
					/*gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - 3 * room.getMultiple());
					this.score = this.score + 3 * room.getMultiple();
					room.setUserSocre(dianpaoUser, - 3 * room.getMultiple());
					room.setUserSocre(this.userId, 3 * room.getMultiple());
					this.fan = 3;*/
//        			this.winType.add(HuType.hu_普通胡);
            } else if ((room.getMode().equals("2") || room.getMode().equals("4") || room.getMode().equals("12") || room.getMode().equals("14"))) {//大胡
                if (this.userId == this.gameInfo.firstTurn) {//庄赢
                    gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - ((room.getPersonNumber() - 1)) * room.getMultiple() * MahjongCode.HUTOSCOREFORLQ.get("" + CardUtil.huForScores(cards, huCardType)) * 2 / 3);
                    this.score = this.score + ((room.getPersonNumber() - 1)) * room.getMultiple() * MahjongCode.HUTOSCOREFORLQ.get("" + CardUtil.huForScores(cards, huCardType)) * 2 / 3;
                    room.setUserSocre(dianpaoUser, -((room.getPersonNumber() - 1)) * room.getMultiple() * MahjongCode.HUTOSCOREFORLQ.get("" + CardUtil.huForScores(cards, huCardType)) * 2 / 3);
                    room.setUserSocre(this.userId, ((room.getPersonNumber() - 1)) * room.getMultiple() * MahjongCode.HUTOSCOREFORLQ.get("" + CardUtil.huForScores(cards, huCardType)) * 2 / 3);
                    this.fan = MahjongCode.HUTOSCOREFORLQ.get("" + CardUtil.huForScores(cards, huCardType)) * 2 / 3;
                } else {
                    if (dianpaoUser == this.gameInfo.firstTurn) {//庄点炮
                        gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - ((room.getPersonNumber() - 1)) * room.getMultiple() * MahjongCode.HUTOSCOREFORLQ.get("" + CardUtil.huForScores(cards, huCardType)) * 2 / 3);
                        this.score = this.score + ((room.getPersonNumber() - 1)) * room.getMultiple() * MahjongCode.HUTOSCOREFORLQ.get("" + CardUtil.huForScores(cards, huCardType)) * 2 / 3;
                        room.setUserSocre(dianpaoUser, -((room.getPersonNumber() - 1)) * room.getMultiple() * MahjongCode.HUTOSCOREFORLQ.get("" + CardUtil.huForScores(cards, huCardType)) * 2 / 3);
                        room.setUserSocre(this.userId, ((room.getPersonNumber() - 1)) * room.getMultiple() * MahjongCode.HUTOSCOREFORLQ.get("" + CardUtil.huForScores(cards, huCardType)) * 2 / 3);
                        this.fan = 1;
                    } else {
                        gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - ((room.getPersonNumber() - 1)) * room.getMultiple() * MahjongCode.HUTOSCOREFORLQ.get("" + CardUtil.huForScores(cards, huCardType)) / 3);
                        this.score = this.score + ((room.getPersonNumber() - 1)) * room.getMultiple() * MahjongCode.HUTOSCOREFORLQ.get("" + CardUtil.huForScores(cards, huCardType)) / 3;
                        room.setUserSocre(dianpaoUser, -((room.getPersonNumber() - 1)) * room.getMultiple() * MahjongCode.HUTOSCOREFORLQ.get("" + CardUtil.huForScores(cards, huCardType)) / 3);
                        room.setUserSocre(this.userId, ((room.getPersonNumber() - 1)) * room.getMultiple() * MahjongCode.HUTOSCOREFORLQ.get("" + CardUtil.huForScores(cards, huCardType)) / 3);
                        this.fan = 1;
                    }
						/*gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - (room.getPersonNumber()) * room.getMultiple() * MahjongCode.HUTOSCOREFORLQ.get(""+CardUtil.huForScores(cards,huCardType)));
						this.score = this.score + (room.getPersonNumber()) * room.getMultiple() * MahjongCode.HUTOSCOREFORLQ.get(""+CardUtil.huForScores(cards,huCardType));
						room.setUserSocre(dianpaoUser, -(room.getPersonNumber()) * room.getMultiple() * MahjongCode.HUTOSCOREFORLQ.get(""+CardUtil.huForScores(cards,huCardType)));
						room.setUserSocre(this.userId, (room.getPersonNumber()) * room.getMultiple() * MahjongCode.HUTOSCOREFORLQ.get(""+CardUtil.huForScores(cards,huCardType)));
						this.fan = MahjongCode.HUTOSCOREFORLQ.get(""+CardUtil.huForScores(cards,huCardType));*/
                }
					/*gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - room.getMultiple() * MahjongCode.HUTOSCOREFORLQ.get(""+CardUtil.huForScores(cards,huCardType)));
					this.score = this.score + room.getMultiple() * MahjongCode.HUTOSCOREFORLQ.get(""+CardUtil.huForScores(cards,huCardType));
					room.setUserSocre(dianpaoUser, - room.getMultiple() * MahjongCode.HUTOSCOREFORLQ.get(""+CardUtil.huForScores(cards,huCardType)));
					room.setUserSocre(this.userId, room.getMultiple() * MahjongCode.HUTOSCOREFORLQ.get(""+CardUtil.huForScores(cards,huCardType)));

					this.fan = MahjongCode.HUTOSCOREFORLQ.get(""+CardUtil.huForScores(cards,huCardType)+"");*/
//        			this.winType = CardUtil.huForWinType(cards);
//                }
            }
        }

    }
}
