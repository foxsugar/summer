package com.code.server.game.mahjong.logic;


import com.code.server.game.mahjong.util.*;

import java.util.*;

public class PlayerCardsInfoJC124 extends PlayerCardsInfoMj {
	private static final int TING_MIN_SCORE = 0;
	private static final int ZIMO_MIN_SCORE = 0;
	private static final int DIANPAO_MIN_SCORE = 0;

	private static final int BASESCORE_DIANPAO = 1;
	private static final int BASESCORE_ZIMO = 2;
	private static final int SCORE_DOUBLE_ZHUANGWIN = 2;
	
	private static final int EXTRA_DIANPAO = 2;
	private static final int BASESCORE_ZHUANG = 2;
	
	private boolean isHasTing;

	@Override
	public void init(List<String> cards) {
		super.init(cards);
		specialHuScore.put(hu_清一色, 3);
		specialHuScore.put(hu_一条龙, 3);
		specialHuScore.put(hu_七小对, 3);
		specialHuScore.put(hu_豪华七小对, 4);
		specialHuScore.put(hu_清龙, 4);

		if (GameInfoJinCheng124.JC124_Ping_HAVEFENG_CANTING.equals(roomInfo.getMode()) || GameInfoJinCheng124.JC124_DA_HAVEFENG_CANTING.equals(roomInfo.getMode())
				|| GameInfoJinCheng124.JC124_Ping_NOFENG_CANTING.equals(roomInfo.getMode()) || GameInfoJinCheng124.JC124_DA_NOFENG_CANTING.equals(roomInfo.getMode())) {
			isHasTing = true;
		}
	}

	@Override
	public boolean isCanTing(List<String> cards) {
		if (!isHasTing) {
			return false;
		}
		if (isTing) {
			return false;
		}
		Set<Integer> set = getTingCardType(getCardsNoChiPengGang(cards), null);
		for (int type : set) {
			if (CardTypeUtil.cardTingScore.get(type) >= TING_MIN_SCORE) {
				return true;
			}
		}
		return false;
	}

	public boolean isHasChi(String card) {
		return false;
	}

	@Override
	public boolean isCanGangAddThisCard(String card) {
		//听之后 杠后的牌还能听
		if (isTing && super.isCanGangAddThisCard(card)) {
			List<String> temp = getCardsAddThisCard(card);
			//去掉 这张杠牌
			int ct = CardTypeUtil.cardType.get(card);
			return isCanTingAfterGang(temp, ct);

		} else return super.isCanGangAddThisCard(card);

	}

	@Override
	public boolean isCanGangThisCard(String card) {
		if (isTing && super.isCanGangThisCard(card)) {
			List<String> temp = new ArrayList<>();
			temp.addAll(cards);
			//去掉 这张杠牌
			int cardType = CardTypeUtil.cardType.get(card);

			return isCanTingAfterGang(temp, cardType);

		} else return super.isCanGangThisCard(card);

	}

	public static void main(String[] args) {
		PlayerCardsInfoJC124 playerCardsInfoKD = new PlayerCardsInfoJC124();
		//	是否能杠:  isTing = true  cards = [101, 098, 103, 096, 088, 091, 090, 000, 003, 097, 099, 059, 102, 067, 100]
		String[] s = new String[]{"112", "113", "114", "115", "044", "048", "052", "060", "061", "062", "064", "068", "092", "100"};

		playerCardsInfoKD.cards = new ArrayList<>();
		List<String> list = Arrays.asList(s);
		playerCardsInfoKD.cards.addAll(list);
//		playerCardsInfoKD.anGangType.add(28);
		playerCardsInfoKD.mingGangType.put(28, 0L);
//		playerCardsInfoKD.pengType.put(25,1);

		playerCardsInfoKD.isTing = true;
		System.out.println(playerCardsInfoKD.isCanPengAddThisCard("063"));
		System.out.println(playerCardsInfoKD.isCanGangThisCard("063"));
//		System.out.println(playerCardsInfoKD.isCanGangThisCard("100"));


	}

	/**
	 * 杠之后是否能听
	 *
	 * @param cards
	 * @param cardType
	 * @return
	 */
	private boolean isCanTingAfterGang(List<String> cards, int cardType) {
		//先删除这次杠的
		removeCardByType(cards, cardType, 4);
		for (int pt : pengType.keySet()) {//如果杠的是之前碰过的牌
			if (pt != cardType) {
				removeCardByType(cards, pt, 3);
			}
		}
		//去掉杠的牌
		cards = getCardsNoGang(cards);
		Set<Integer> set = getTingCardType(cards, null);
		for (int type : set) {
			if (CardTypeUtil.cardTingScore.get(type) >= TING_MIN_SCORE) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isHasGang() {
		System.out.println("是否能杠: " + " isTing = " + isTing + "  cards = " + cards);
		if (isTing) {
			Set<Integer> canGangType = getHasGangList(cards);
			for (int gt : canGangType) {
				List<String> temp = new ArrayList<>();
				temp.addAll(cards);
				if (isCanTingAfterGang(temp, gt)) {
					return true;
				}
			}
			return false;

		} else return super.isHasGang();
	}

	//	是否能杠:  isTing = true  cards = [101, 098, 103, 096, 088, 091, 090, 000, 003, 097, 099, 059, 102, 092, 100]
	@Override
	public boolean isCanPengAddThisCard(String card) {
		//听之后不能碰牌
		if (isTing) {
			return false;
		}
		return super.isCanPengAddThisCard(card);
	}


	@Override
	public boolean isCanHu_dianpao(String card) {
		return (!isHasTing || isTing) && CardTypeUtil.getCardTingScore(card) >= DIANPAO_MIN_SCORE && super.isCanHu_dianpao(card);
	}

	@Override
	public boolean isCanHu_zimo(String card) {
		return (!isHasTing || isTing) && CardTypeUtil.getCardTingScore(card) >= ZIMO_MIN_SCORE && super.isCanHu_zimo(card);
	}

	@Override
	public boolean checkPlayCard(String card) {
		if (isTing) {
			return super.checkPlayCard(card) && card.equals(catchCard);
		} else {
			return super.checkPlayCard(card);
		}
	}

	//杠牌分数计算
	@Override
	public void gangCompute(RoomInfo room, GameInfo gameInfo, boolean isMing, long diangangUser, String card) {
		/*super.gangCompute(room, gameInfo, isMing, diangangUser, card);
		if (!isMing) {//暗杠
			for (Integer i : gameInfo.getPlayerCardsInfos().keySet()) {
				gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - 2 * room.getMultiple());
				room.setUserSocre(i, -2 * room.getMultiple());
			}
			this.score = this.score + 2 * room.getPersonNumber() * room.getMultiple();
			room.setUserSocre(this.userId, 2 * room.getPersonNumber() * room.getMultiple());
		} else {//明杠
			if (diangangUser == -1) {
				for (Integer i : gameInfo.getPlayerCardsInfos().keySet()) {
					gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - room.getMultiple());
					room.setUserSocre(i, -room.getMultiple());
				}
				this.score = this.score + room.getPersonNumber() * room.getMultiple();
				room.setUserSocre(this.userId, room.getPersonNumber() * room.getMultiple());
			} else {
				if (!gameInfo.getPlayerCardsInfos().get(diangangUser).isTing) {//未上听包3家
					gameInfo.getPlayerCardsInfos().get(diangangUser).setScore(gameInfo.getPlayerCardsInfos().get(diangangUser).getScore() - (room.getPersonNumber() - 1) * room.getMultiple());
					this.score = this.score + (room.getPersonNumber() - 1) * room.getMultiple();
					room.setUserSocre(diangangUser, -(room.getPersonNumber() - 1) * room.getMultiple());
					room.setUserSocre(this.userId, (room.getPersonNumber() - 1) * room.getMultiple());
				} else {
					for (Integer i : gameInfo.getPlayerCardsInfos().keySet()) {
						gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - room.getMultiple());
						room.setUserSocre(i, -room.getMultiple());
					}
					this.score = this.score + room.getPersonNumber() * room.getMultiple();
					room.setUserSocre(this.userId, room.getPersonNumber() * room.getMultiple());
				}
			}
			//room.pushScoreChange();
		}*/
	}

	//胡牌分数计算;
		@Override
	public void huCompute (RoomInfo room, GameInfo gameInfo, boolean isZimo, long dianpaoUser, String card){
			List<String> cs = getCardsNoChiPengGang(cards);
			List<HuCardType> huList = HuUtil.isHu(cs, this, CardTypeUtil.cardType.get(card), new HuLimit(0));
			//设置胡牌类型
			HuCardType huCardType = getMaxScoreHuCardType(huList);
			this.winType.addAll(huCardType.specialHuList);
			
			
			if (isZimo) {//自摸
				computeALLGang();
				if (room.getModeTotal().equals("124") && (room.getMode().equals(GameInfoJinCheng124.JC124_Ping_HAVEFENG_CANTING) || room.getMode().equals(GameInfoJinCheng124.JC124_Ping_HAVEFENG_NOTING) || room.getMode().equals(GameInfoJinCheng124.JC124_Ping_NOFENG_CANTING) || room.getMode().equals(GameInfoJinCheng124.JC124_Ping_NOFENG_NOTING))) {//平胡
					if (this.userId == this.gameInfo.firstTurn) {//庄赢
						for (Long i : gameInfo.getPlayerCardsInfos().keySet()) {
							gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - BASESCORE_ZIMO * BASESCORE_ZHUANG * room.getMultiple());
							room.setUserSocre(i, -BASESCORE_ZIMO * BASESCORE_ZHUANG * room.getMultiple());
						}
						this.score = this.score + BASESCORE_ZIMO * room.getPersonNumber() * BASESCORE_ZHUANG * room.getMultiple();
						room.setUserSocre(this.userId, BASESCORE_ZIMO * room.getPersonNumber() * BASESCORE_ZHUANG * room.getMultiple());
						this.fan = BASESCORE_ZIMO * BASESCORE_ZHUANG;
					} else {
						for (Long i : gameInfo.getPlayerCardsInfos().keySet()) {
							gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - BASESCORE_ZIMO * room.getMultiple());
							room.setUserSocre(i, -BASESCORE_ZIMO * room.getMultiple());
						}
						//庄输2倍，在此多减一倍
						gameInfo.getPlayerCardsInfos().get(this.gameInfo.firstTurn).setScore(gameInfo.getPlayerCardsInfos().get(this.gameInfo.firstTurn).getScore() - BASESCORE_ZIMO * room.getMultiple());
						room.setUserSocre(this.gameInfo.firstTurn, -BASESCORE_ZIMO * room.getMultiple());

						this.score = this.score + (room.getPersonNumber() + 1) * BASESCORE_ZIMO * room.getMultiple();
						room.setUserSocre(this.userId, (room.getPersonNumber() + 1) * BASESCORE_ZIMO * room.getMultiple());
						this.fan = BASESCORE_ZIMO;
					}
				} else if (room.getModeTotal().equals("124") && (room.getMode().equals(GameInfoJinCheng124.JC124_DA_HAVEFENG_CANTING) || room.getMode().equals(GameInfoJinCheng124.JC124_DA_HAVEFENG_NOTING) || room.getMode().equals(GameInfoJinCheng124.JC124_DA_NOFENG_CANTING) || room.getMode().equals(GameInfoJinCheng124.JC124_DA_NOFENG_NOTING))) {//大胡
					if (this.userId == this.gameInfo.firstTurn) {//庄赢
						for (Long i : gameInfo.getPlayerCardsInfos().keySet()) {
							gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - BASESCORE_ZIMO * MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType)) * BASESCORE_ZHUANG * room.getMultiple());
							room.setUserSocre(i, - BASESCORE_ZIMO * MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType)) * BASESCORE_ZHUANG * room.getMultiple());
						}
						this.score = this.score + BASESCORE_ZIMO * room.getPersonNumber() * MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType) + "") * BASESCORE_ZHUANG * room.getMultiple();
						room.setUserSocre(this.userId, BASESCORE_ZIMO * room.getPersonNumber() * MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType) + "") * BASESCORE_ZHUANG * room.getMultiple());
						this.fan = BASESCORE_ZIMO * MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType) + "");
					} else {
						for (Long i : gameInfo.getPlayerCardsInfos().keySet()) {
							gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - BASESCORE_ZIMO * MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType)) * room.getMultiple());
							room.setUserSocre(i, - BASESCORE_ZIMO * MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType)) * room.getMultiple());
						}
						//庄输2倍，在此多减一倍
						gameInfo.getPlayerCardsInfos().get(this.gameInfo.firstTurn).setScore(gameInfo.getPlayerCardsInfos().get(this.gameInfo.firstTurn).getScore() - BASESCORE_ZIMO * MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType)) * room.getMultiple());
						room.setUserSocre(this.gameInfo.firstTurn, - BASESCORE_ZIMO * MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType)) * room.getMultiple());

						this.score = this.score + BASESCORE_ZIMO * (room.getPersonNumber() + 1) * MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType) + "") * room.getMultiple();
						room.setUserSocre(this.userId, BASESCORE_ZIMO * (room.getPersonNumber() + 1) * MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType) + "") * room.getMultiple());
						this.fan = BASESCORE_ZIMO * MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType) + "");
					}
				}
			} else {//点炮
				computeALLGang(dianpaoUser);
				if (room.getModeTotal().equals("124") && (room.getMode().equals(GameInfoJinCheng124.JC124_Ping_HAVEFENG_CANTING) || room.getMode().equals(GameInfoJinCheng.JC_Ping_HAVEFENG_NOTING) || room.getMode().equals(GameInfoJinCheng124.JC124_Ping_NOFENG_CANTING) || room.getMode().equals(GameInfoJinCheng124.JC124_Ping_NOFENG_NOTING))) {//平胡
					if (room.getMode().equals(GameInfoJinCheng124.JC124_Ping_HAVEFENG_CANTING) || room.getMode().equals(GameInfoJinCheng124.JC124_Ping_NOFENG_CANTING)) {//听的
						if (gameInfo.getPlayerCardsInfos().get(dianpaoUser).isTing) {//上听
							if (this.userId == this.gameInfo.firstTurn) {//庄赢
								//baoGang(room,dianpaoUser);//点炮的人包杠
								for (long i : gameInfo.getPlayerCardsInfos().keySet()) {
									gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - BASESCORE_ZHUANG * room.getMultiple());
									room.setUserSocre(i, - BASESCORE_ZHUANG * room.getMultiple());
								}

								this.score = this.score + (room.getPersonNumber() * BASESCORE_ZHUANG) * room.getMultiple();
								room.setUserSocre(this.userId, (room.getPersonNumber() * BASESCORE_ZHUANG) * room.getMultiple());
								this.fan = BASESCORE_ZHUANG;
							} else {//庄输
								if (this.gameInfo.firstTurn == dianpaoUser) {//庄点炮
									//baoGang(room,dianpaoUser);//点炮的人包杠
									for (long i : gameInfo.getPlayerCardsInfos().keySet()) {
										gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - BASESCORE_DIANPAO * room.getMultiple());
										room.setUserSocre(i, - BASESCORE_DIANPAO * room.getMultiple());
									}
									//庄再输2倍，再减一倍,在减点炮额外的分
									gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - (BASESCORE_DIANPAO) * room.getMultiple());
									room.setUserSocre(dianpaoUser, -(BASESCORE_DIANPAO) * room.getMultiple());

									this.score = this.score + ((room.getPersonNumber() + 1) * BASESCORE_DIANPAO) * room.getMultiple();
									room.setUserSocre(this.userId, ((room.getPersonNumber() + 1) * BASESCORE_DIANPAO) * room.getMultiple());
									this.fan = BASESCORE_DIANPAO;
								} else {
									//baoGang(room,dianpaoUser);//点炮的人包杠
									for (long i : gameInfo.getPlayerCardsInfos().keySet()) {
										gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - BASESCORE_DIANPAO * room.getMultiple());
										room.setUserSocre(i, -BASESCORE_DIANPAO * room.getMultiple());
									}
									//庄再输2倍，再减一倍
									gameInfo.getPlayerCardsInfos().get(this.gameInfo.firstTurn).setScore(gameInfo.getPlayerCardsInfos().get(this.gameInfo.firstTurn).getScore() - BASESCORE_DIANPAO * room.getMultiple());
									room.setUserSocre(this.gameInfo.firstTurn, -BASESCORE_DIANPAO * room.getMultiple());

									/*//点炮的人额外减2分
									gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - EXTRA_DIANPAO * room.getMultiple());
									room.setUserSocre(dianpaoUser, - EXTRA_DIANPAO * room.getMultiple());*/
									
									this.score = this.score + ((room.getPersonNumber() + 1) * BASESCORE_DIANPAO) * room.getMultiple();
									room.setUserSocre(this.userId, ((room.getPersonNumber() + 1) * BASESCORE_DIANPAO) * room.getMultiple());
									this.fan = 1;
								}
							}
						} else {//未上听包3家
							if (this.userId == this.gameInfo.firstTurn) {//庄赢
								baoGang(room,dianpaoUser,this.userId);//点炮的人包杠
								gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - ((room.getPersonNumber() - 1) * BASESCORE_DIANPAO * BASESCORE_ZHUANG + EXTRA_DIANPAO) * room.getMultiple());
								this.score = this.score + ((room.getPersonNumber() - 1) * BASESCORE_DIANPAO * BASESCORE_ZHUANG  + EXTRA_DIANPAO) * room.getMultiple();
								room.setUserSocre(dianpaoUser, -((room.getPersonNumber() - 1) * BASESCORE_DIANPAO * BASESCORE_ZHUANG + EXTRA_DIANPAO) * room.getMultiple());
								room.setUserSocre(this.userId, ((room.getPersonNumber() - 1) * BASESCORE_DIANPAO * BASESCORE_ZHUANG + EXTRA_DIANPAO) * room.getMultiple());
								
								this.fan = 1;
							} else {
								if(dianpaoUser==this.gameInfo.firstTurn){//庄点炮
									baoGang(room,dianpaoUser,this.userId);//点炮的人包杠
									gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - ((room.getPersonNumber() - 1) * BASESCORE_DIANPAO * BASESCORE_ZHUANG + EXTRA_DIANPAO) * room.getMultiple());
									this.score = this.score + ((room.getPersonNumber() - 1) * BASESCORE_DIANPAO * BASESCORE_ZHUANG  + EXTRA_DIANPAO) * room.getMultiple();
									room.setUserSocre(dianpaoUser, -((room.getPersonNumber() - 1) * BASESCORE_DIANPAO * BASESCORE_ZHUANG + EXTRA_DIANPAO) * room.getMultiple());
									room.setUserSocre(this.userId, ((room.getPersonNumber() - 1) * BASESCORE_DIANPAO * BASESCORE_ZHUANG + EXTRA_DIANPAO) * room.getMultiple());

									this.fan = 1;
								}else{//闲点炮
									baoGang(room,dianpaoUser,this.userId);//点炮的人包杠
									/*for (Integer i : gameInfo.getPlayerCardsInfos().keySet()) {
										gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - BASESCORE_DIANPAO * room.getMultiple());
										room.setUserSocre(i, -BASESCORE_DIANPAO * room.getMultiple());
									}
									//庄再输2倍，再减一倍
									gameInfo.getPlayerCardsInfos().get(this.gameInfo.firstTurn).setScore(gameInfo.getPlayerCardsInfos().get(this.gameInfo.firstTurn).getScore() - BASESCORE_DIANPAO * room.getMultiple());
									room.setUserSocre(this.gameInfo.firstTurn, - BASESCORE_DIANPAO * room.getMultiple());

									this.score = this.score + ((room.getPersonNumber() + 1) * BASESCORE_DIANPAO) * room.getMultiple();
									room.setUserSocre(this.userId, ((room.getPersonNumber() + 1) * BASESCORE_DIANPAO) * room.getMultiple());*/
									
									gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - ((room.getPersonNumber() - 1) * BASESCORE_DIANPAO + EXTRA_DIANPAO + BASESCORE_DIANPAO) * room.getMultiple());
									this.score = this.score + ((room.getPersonNumber() - 1) * BASESCORE_DIANPAO + EXTRA_DIANPAO + BASESCORE_DIANPAO) * room.getMultiple();
									room.setUserSocre(dianpaoUser, -((room.getPersonNumber() - 1) * BASESCORE_DIANPAO + EXTRA_DIANPAO + BASESCORE_DIANPAO) * room.getMultiple());
									room.setUserSocre(this.userId, ((room.getPersonNumber() - 1) * BASESCORE_DIANPAO + EXTRA_DIANPAO + BASESCORE_DIANPAO) * room.getMultiple());
									
									this.fan = 1;
								}
								//闲家未上听点炮竟然不包？
								/*gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - (room.getPersonNumber() * BASESCORE_DIANPAO) * room.getMultiple());
								this.score = this.score + (room.getPersonNumber() * BASESCORE_DIANPAO) * room.getMultiple();
								room.setUserSocre(dianpaoUser, -(room.getPersonNumber() * BASESCORE_DIANPAO) * room.getMultiple());
								room.setUserSocre(this.userId, (room.getPersonNumber() * BASESCORE_DIANPAO) * room.getMultiple());
								this.fan = 1;*/
							}
						}

					} else if (room.getMode().equals(GameInfoJinCheng124.JC124_Ping_HAVEFENG_NOTING) || room.getMode().equals(GameInfoJinCheng124.JC124_Ping_HAVEFENG_NOTING)) {//不含听的
						if (this.userId == this.gameInfo.firstTurn) {//庄赢
							baoGang(room,dianpaoUser,this.userId);//点炮的人包杠
							gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - ((room.getPersonNumber() - 1) * BASESCORE_DIANPAO * BASESCORE_ZHUANG + EXTRA_DIANPAO) * room.getMultiple());
							this.score = this.score + ((room.getPersonNumber() - 1) * BASESCORE_DIANPAO * BASESCORE_ZHUANG  + EXTRA_DIANPAO) * room.getMultiple();
							room.setUserSocre(dianpaoUser, -((room.getPersonNumber() - 1) * BASESCORE_DIANPAO * BASESCORE_ZHUANG + EXTRA_DIANPAO) * room.getMultiple());
							room.setUserSocre(this.userId, ((room.getPersonNumber() - 1) * BASESCORE_DIANPAO * BASESCORE_ZHUANG + EXTRA_DIANPAO) * room.getMultiple());

							this.fan = 1;
						} else {
							if(dianpaoUser==this.gameInfo.firstTurn){//庄点炮
								baoGang(room,dianpaoUser,this.userId);//点炮的人包杠
								gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - ((room.getPersonNumber() - 1) * BASESCORE_DIANPAO * BASESCORE_ZHUANG + EXTRA_DIANPAO) * room.getMultiple());
								this.score = this.score + ((room.getPersonNumber() - 1) * BASESCORE_DIANPAO * BASESCORE_ZHUANG  + EXTRA_DIANPAO) * room.getMultiple();
								room.setUserSocre(dianpaoUser, -((room.getPersonNumber() - 1) * BASESCORE_DIANPAO * BASESCORE_ZHUANG + EXTRA_DIANPAO) * room.getMultiple());
								room.setUserSocre(this.userId, ((room.getPersonNumber() - 1) * BASESCORE_DIANPAO * BASESCORE_ZHUANG + EXTRA_DIANPAO) * room.getMultiple());

								this.fan = 1;
							}else{//闲点炮
								baoGang(room,dianpaoUser,this.userId);//点炮的人包杠
								/*for (Integer i : gameInfo.getPlayerCardsInfos().keySet()) {
									gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - BASESCORE_DIANPAO * room.getMultiple());
									room.setUserSocre(i, -BASESCORE_DIANPAO * room.getMultiple());
								}
								//庄再输2倍，再减一倍
								gameInfo.getPlayerCardsInfos().get(this.gameInfo.firstTurn).setScore(gameInfo.getPlayerCardsInfos().get(this.gameInfo.firstTurn).getScore() - BASESCORE_DIANPAO * room.getMultiple());
								room.setUserSocre(this.gameInfo.firstTurn, - BASESCORE_DIANPAO * room.getMultiple());

								//点炮的人额外减2分
								gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - EXTRA_DIANPAO * room.getMultiple());
								room.setUserSocre(dianpaoUser, - EXTRA_DIANPAO * room.getMultiple());

								this.score = this.score + ((room.getPersonNumber() + 1) * BASESCORE_DIANPAO + EXTRA_DIANPAO) * room.getMultiple();
								room.setUserSocre(this.userId, ((room.getPersonNumber() + 1) * BASESCORE_DIANPAO + EXTRA_DIANPAO) * room.getMultiple());*/
								
								gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - ((room.getPersonNumber() - 1) * BASESCORE_DIANPAO + EXTRA_DIANPAO + BASESCORE_DIANPAO) * room.getMultiple());
								this.score = this.score + ((room.getPersonNumber() - 1) * BASESCORE_DIANPAO + EXTRA_DIANPAO + BASESCORE_DIANPAO) * room.getMultiple();
								room.setUserSocre(dianpaoUser, -((room.getPersonNumber() - 1) * BASESCORE_DIANPAO + EXTRA_DIANPAO + BASESCORE_DIANPAO) * room.getMultiple());
								room.setUserSocre(this.userId, ((room.getPersonNumber() - 1) * BASESCORE_DIANPAO + EXTRA_DIANPAO + BASESCORE_DIANPAO) * room.getMultiple());

								this.fan = 1;
							}
					}
				} 
			}
				else if (room.getModeTotal().equals("124") && (room.getMode().equals(GameInfoJinCheng124.JC124_DA_HAVEFENG_CANTING) || room.getMode().equals(GameInfoJinCheng124.JC124_DA_HAVEFENG_NOTING) || room.getMode().equals(GameInfoJinCheng124.JC124_DA_NOFENG_CANTING) || room.getMode().equals(GameInfoJinCheng124.JC124_DA_NOFENG_NOTING))) {//大胡
					if (room.getMode().equals(GameInfoJinCheng124.JC124_DA_HAVEFENG_CANTING) || room.getMode().equals(GameInfoJinCheng124.JC124_DA_NOFENG_CANTING)) {//听的
						if (gameInfo.getPlayerCardsInfos().get(dianpaoUser).isTing) {//上听
							if (this.userId == this.gameInfo.firstTurn) {//庄赢
								//baoGang(room,dianpaoUser);//点炮的人包杠
								for (long i : gameInfo.getPlayerCardsInfos().keySet()) {
									gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - (MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType)) * SCORE_DOUBLE_ZHUANGWIN) * room.getMultiple());
									room.setUserSocre(i, -(MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType)) * SCORE_DOUBLE_ZHUANGWIN) * room.getMultiple());
								}
								
								this.score = this.score + (room.getPersonNumber() * MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType)) * SCORE_DOUBLE_ZHUANGWIN) * room.getMultiple();
								room.setUserSocre(this.userId, (room.getPersonNumber() * MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType)) * SCORE_DOUBLE_ZHUANGWIN) * room.getMultiple());
								this.fan = MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType) + "") * SCORE_DOUBLE_ZHUANGWIN;
							} else {
								if (this.gameInfo.firstTurn == dianpaoUser) {//庄点炮
									//baoGang(room,dianpaoUser);//点炮的人包杠
									for (long i : gameInfo.getPlayerCardsInfos().keySet()) {
										gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - (MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType))) * room.getMultiple());
										room.setUserSocre(i, -(MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType))) * room.getMultiple());
									}
									//庄点炮，输2倍，再扣一倍
									gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - (MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType))) * room.getMultiple());
									room.setUserSocre(dianpaoUser, -(MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType))) * room.getMultiple());

									this.score = this.score + ((room.getPersonNumber() + 1) * MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType))) * room.getMultiple();
									room.setUserSocre(this.userId, ((room.getPersonNumber() + 1) * MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType))) * room.getMultiple());
									
									
									
									this.fan = MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType) + "");
								} else {//其他人点炮
									//baoGang(room,dianpaoUser);//点炮的人包杠
									for (long i : gameInfo.getPlayerCardsInfos().keySet()) {
										gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - (MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType))) * room.getMultiple());
										room.setUserSocre(i, -(MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType))) * room.getMultiple());
									}
									//庄点炮，输2倍，再扣一倍
									gameInfo.getPlayerCardsInfos().get(this.gameInfo.firstTurn).setScore(gameInfo.getPlayerCardsInfos().get(this.gameInfo.firstTurn).getScore() - (MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType))) * room.getMultiple());
									room.setUserSocre(this.gameInfo.firstTurn, -(MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType))) * room.getMultiple());
									
									//点炮的人额外减2分
									/*gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - EXTRA_DIANPAO * room.getMultiple());
									room.setUserSocre(dianpaoUser, - EXTRA_DIANPAO * room.getMultiple());*/
								
									this.score = this.score + ((room.getPersonNumber() + 1) * MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType))) * room.getMultiple();
									room.setUserSocre(this.userId, ((room.getPersonNumber() + 1) * MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType))) * room.getMultiple());
									this.fan = MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType) + "");
								}
							}
						} else {//未上听
							if (this.userId == this.gameInfo.firstTurn) {//庄赢
								//baoGang(room,dianpaoUser,this.userId);//点炮的人包杠
								gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - ((room.getPersonNumber() - 1) * MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType)) * SCORE_DOUBLE_ZHUANGWIN + EXTRA_DIANPAO) * room.getMultiple());
								room.setUserSocre(dianpaoUser, -((room.getPersonNumber() - 1) * MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType)) * SCORE_DOUBLE_ZHUANGWIN + EXTRA_DIANPAO) * room.getMultiple());
								this.score = this.score + ((room.getPersonNumber() - 1) * MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType)) * SCORE_DOUBLE_ZHUANGWIN + EXTRA_DIANPAO) * room.getMultiple();
								room.setUserSocre(this.userId, ((room.getPersonNumber() - 1) * MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType)) * SCORE_DOUBLE_ZHUANGWIN + EXTRA_DIANPAO) * room.getMultiple());
								this.fan = MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType) + "");
							} else {
								if (this.gameInfo.firstTurn == dianpaoUser) {//庄点炮
									//baoGang(room,dianpaoUser,this.userId);//点炮的人包杠

									gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - ((room.getPersonNumber() - 1) * MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType)) * BASESCORE_ZHUANG + EXTRA_DIANPAO) * room.getMultiple());
									this.score = this.score + ((room.getPersonNumber() - 1) * MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType)) * BASESCORE_ZHUANG  + EXTRA_DIANPAO) * room.getMultiple();
									room.setUserSocre(dianpaoUser, -((room.getPersonNumber() - 1) * MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType)) * BASESCORE_ZHUANG + EXTRA_DIANPAO) * room.getMultiple());
									room.setUserSocre(this.userId, ((room.getPersonNumber() - 1) * MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType)) * BASESCORE_ZHUANG + EXTRA_DIANPAO) * room.getMultiple());

									this.fan = MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType) + "") * EXTRA_DIANPAO;
								} else {//其他人点炮
									//baoGang(room,dianpaoUser,this.userId);//点炮的人包杠
									/*for (Integer i : gameInfo.getPlayerCardsInfos().keySet()) {
										gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - (MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType))) * room.getMultiple());
										room.setUserSocre(i, -(MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType))) * room.getMultiple());
									}
									//庄点炮，输2倍，再扣一倍
									gameInfo.getPlayerCardsInfos().get(this.gameInfo.firstTurn).setScore(gameInfo.getPlayerCardsInfos().get(this.gameInfo.firstTurn).getScore() - (MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType))) * room.getMultiple());
									room.setUserSocre(this.gameInfo.firstTurn, -(MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType))) * room.getMultiple());

									//点炮的人额外减2
									gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - EXTRA_DIANPAO * room.getMultiple());
									room.setUserSocre(dianpaoUser, - EXTRA_DIANPAO * room.getMultiple());

									this.score = this.score + ((room.getPersonNumber() + 1) * MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType))) * room.getMultiple();
									room.setUserSocre(this.userId, ((room.getPersonNumber() + 1) * MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType))) * room.getMultiple());*/
									
									gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - (room.getPersonNumber() * MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType)) + EXTRA_DIANPAO) * room.getMultiple());
									this.score = this.score + (room.getPersonNumber()  * MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType)) + EXTRA_DIANPAO) * room.getMultiple();
									room.setUserSocre(dianpaoUser, -(room.getPersonNumber()  * MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType)) + EXTRA_DIANPAO) * room.getMultiple());
									room.setUserSocre(this.userId, (room.getPersonNumber()* MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType)) + EXTRA_DIANPAO) * room.getMultiple());
							
									this.fan = MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType) + "");
								}
							}
						}
					} else if (room.getMode().equals(GameInfoJinCheng124.JC124_DA_HAVEFENG_NOTING) || room.getMode().equals(GameInfoJinCheng124.JC124_DA_NOFENG_NOTING)) {//不带听
						//未上听
						if (this.userId == this.gameInfo.firstTurn) {//庄赢
							//baoGang(room,dianpaoUser,this.userId);//点炮的人包杠
							gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - ((room.getPersonNumber() - 1) * MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType)) * SCORE_DOUBLE_ZHUANGWIN + EXTRA_DIANPAO) * room.getMultiple());
							room.setUserSocre(dianpaoUser, -((room.getPersonNumber() - 1) * MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType)) * SCORE_DOUBLE_ZHUANGWIN + EXTRA_DIANPAO) * room.getMultiple());

							this.score = this.score + ((room.getPersonNumber() - 1) * MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType)) * SCORE_DOUBLE_ZHUANGWIN + EXTRA_DIANPAO) * room.getMultiple();
							room.setUserSocre(this.userId, ((room.getPersonNumber() - 1) * MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType)) * SCORE_DOUBLE_ZHUANGWIN + EXTRA_DIANPAO) * room.getMultiple());
							this.fan = MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType) + "");
						} else {
							if (this.gameInfo.firstTurn == dianpaoUser) {//庄点炮
								//baoGang(room,dianpaoUser,this.userId);//点炮的人包杠
								/*for (Integer i : gameInfo.getPlayerCardsInfos().keySet()) {
									gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - (MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType)) * EXTRA_DIANPAO) * room.getMultiple());
									room.setUserSocre(i, -(MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType)) * EXTRA_DIANPAO) * room.getMultiple());
								}
								//庄点炮，输2倍，再扣一倍
								gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - (MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType)) * EXTRA_DIANPAO) * room.getMultiple());
								room.setUserSocre(dianpaoUser, -(MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType)) * EXTRA_DIANPAO) * room.getMultiple());

								this.score = this.score + ((room.getPersonNumber() + 1) * MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType))) * room.getMultiple();
								room.setUserSocre(this.userId, ((room.getPersonNumber() + 1) * MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType))) * room.getMultiple());
								*/
								
								gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - ((room.getPersonNumber() - 1) * MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType)) * BASESCORE_ZHUANG + EXTRA_DIANPAO) * room.getMultiple());
								this.score = this.score + ((room.getPersonNumber() - 1) * MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType)) * BASESCORE_ZHUANG  + EXTRA_DIANPAO) * room.getMultiple();
								room.setUserSocre(dianpaoUser, -((room.getPersonNumber() - 1) * MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType)) * BASESCORE_ZHUANG + EXTRA_DIANPAO) * room.getMultiple());
								room.setUserSocre(this.userId, ((room.getPersonNumber() - 1) * MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType)) * BASESCORE_ZHUANG + EXTRA_DIANPAO) * room.getMultiple());
								
								this.fan = MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType) + "") * EXTRA_DIANPAO;
							} else {//其他人点炮
								//baoGang(room,dianpaoUser,this.userId);//点炮的人包杠
								/*for (Integer i : gameInfo.getPlayerCardsInfos().keySet()) {
									gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - (MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType))) * room.getMultiple());
									room.setUserSocre(i, -(MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType))) * room.getMultiple());
								}
								//庄点炮，输2倍，再扣一倍
								gameInfo.getPlayerCardsInfos().get(this.gameInfo.firstTurn).setScore(gameInfo.getPlayerCardsInfos().get(this.gameInfo.firstTurn).getScore() - (MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType))) * room.getMultiple());
								room.setUserSocre(this.gameInfo.firstTurn, -(MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType))) * room.getMultiple());

								//点炮的人额外减2
								gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - EXTRA_DIANPAO * room.getMultiple());
								room.setUserSocre(dianpaoUser, - EXTRA_DIANPAO * room.getMultiple());

								this.score = this.score + ((room.getPersonNumber() + 1) * MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType))) * room.getMultiple();
								room.setUserSocre(this.userId, ((room.getPersonNumber() + 1) * MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType))) * room.getMultiple());
								*/
								
								gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - (room.getPersonNumber() * MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType)) + EXTRA_DIANPAO) * room.getMultiple());
								this.score = this.score + (room.getPersonNumber()  * MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType)) + EXTRA_DIANPAO) * room.getMultiple();
								room.setUserSocre(dianpaoUser, -(room.getPersonNumber()  * MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType)) + EXTRA_DIANPAO) * room.getMultiple());
								room.setUserSocre(this.userId, (room.getPersonNumber()* MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType)) + EXTRA_DIANPAO) * room.getMultiple());
								
								this.fan = MahjongCode.HUTOSCOREFORJC124.get("" + CardUtil.huForScores(cards, huCardType) + "");
							}
						}
					}
				}
		}
	}
		
	@Deprecated
	public void baoGang(RoomInfo room, long dianpaoUser, long winner){
		//点炮的人包杠
		int temp = 0;
		for(PlayerCardsInfoMj playerCardsInfo : gameInfo.getPlayerCardsInfos().values()){
			if(playerCardsInfo.getScore()<0){
				temp += playerCardsInfo.getScore();
				room.setUserSocre(playerCardsInfo.getUserId(), -playerCardsInfo.getScore());
				playerCardsInfo.setScore(0);
			}
		}
		gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() + temp);
		room.setUserSocre(dianpaoUser, temp);
	}

	
	
    public void computeALLGang(long dianpaoUser){
        Map<Long,Integer> scores = new HashMap<>();//分数计算key:use,value:score
        for (long i : gameInfo.users) {
			scores.put(i, 0);
		}

        	if(gameInfo.getPlayerCardsInfos().get(dianpaoUser).isTing){//听牌直接计算
            	for (PlayerCardsInfoMj playerCardsInfo : gameInfo.getPlayerCardsInfos().values()) {
            		//暗杠计算
            		for (long i : scores.keySet()) {
    					scores.put(i, scores.get(i) - playerCardsInfo.getAnGangType().size()*2);
    				}
            		scores.put(playerCardsInfo.getUserId(), scores.get(playerCardsInfo.getUserId())+playerCardsInfo.getAnGangType().size()*2*4);
            		//明杠计算
            		for (Integer ii : playerCardsInfo.getMingGangType().keySet()) {
    					/*if(playerCardsInfo.getMingGangType().get(ii)==-1){*/
    						for (long i : scores.keySet()) {
    							scores.put(i, scores.get(i) - 1);
    						}
    						scores.put(playerCardsInfo.getUserId(), scores.get(playerCardsInfo.getUserId()) + 4);
    					/*}
    					else{//点杠
    						scores.put(playerCardsInfo.getMingGangType().get(ii) , scores.get(playerCardsInfo.getUserId()) - 3);
    						scores.put(this.userId, scores.get(playerCardsInfo.getUserId()) + 3);
    					}*/
    				}
            	}
            }else{//未听牌
            	for (PlayerCardsInfoMj playerCardsInfo : gameInfo.getPlayerCardsInfos().values()) {
            		//暗杠计算
            		scores.put(dianpaoUser, scores.get(dianpaoUser) - playerCardsInfo.getAnGangType().size()*2*3);
            		scores.put(playerCardsInfo.getUserId(), scores.get(playerCardsInfo.getUserId())+playerCardsInfo.getAnGangType().size()*2*3);
            		//明杠计算
            		scores.put(dianpaoUser, scores.get(dianpaoUser) - playerCardsInfo.getMingGangType().size()*3);
            		scores.put(playerCardsInfo.getUserId(), scores.get(playerCardsInfo.getUserId())+playerCardsInfo.getMingGangType().size()*3);
            	}
            }
        

        for (long i : scores.keySet()) {
        	gameInfo.getPlayerCardsInfos().get(i).setScore(scores.get(i));
        	roomInfo.setUserSocre(i, scores.get(i));
		}
    }
    
    public void computeALLGang(){
    	
    	 Map<Long,Integer> scores = new HashMap<>();//分数计算key:use,value:score
         for (long i : gameInfo.users) {
 			scores.put(i, 0);
 		}
    	
    	for (PlayerCardsInfoMj playerCardsInfo : gameInfo.getPlayerCardsInfos().values()) {
    		//暗杠计算
    		for (long i : scores.keySet()) {
				scores.put(i, scores.get(i) - playerCardsInfo.getAnGangType().size()*2);
			}
    		scores.put(playerCardsInfo.getUserId(), scores.get(playerCardsInfo.getUserId())+playerCardsInfo.getAnGangType().size()*2*4);
    		//明杠计算
    		for (Integer ii : playerCardsInfo.getMingGangType().keySet()) {
				/*if(playerCardsInfo.getMingGangType().get(ii)==-1){*/
					for (long i : scores.keySet()) {
						scores.put(i, scores.get(i) - 1);
					}
					scores.put(playerCardsInfo.getUserId(), scores.get(playerCardsInfo.getUserId()) + 4);
					/*}
				else{//点杠
					scores.put(playerCardsInfo.getMingGangType().get(ii) , scores.get(playerCardsInfo.getUserId()) - 3);
					scores.put(this.userId, scores.get(playerCardsInfo.getUserId()) + 3);
				}*/
			}
    	}
    	for (long i : scores.keySet()) {
        	gameInfo.getPlayerCardsInfos().get(i).setScore(scores.get(i));
        	roomInfo.setUserSocre(i, scores.get(i));
		}
    }
}

