package com.code.server.game.mahjong.logic;


import com.code.server.game.mahjong.util.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class PlayerCardsInfoSSGK extends PlayerCardsInfoMj {
	private static final int TING_MIN_SCORE = 0;
	private static final int ZIMO_MIN_SCORE = 0;
	private static final int DIANPAO_MIN_SCORE = 0;

	protected boolean isHasTing;

	@Override
	public void init(List<String> cards) {
		super.init(cards);
		specialHuScore.put(hu_清一色, 9);
		specialHuScore.put(hu_一条龙, 9);
		specialHuScore.put(hu_七小对, 9);
		specialHuScore.put(hu_豪华七小对, 18);
		specialHuScore.put(hu_双豪七小对_山西, 18);
		specialHuScore.put(hu_十三幺, 27);

		if (GameInfoShengShi.SS_Ping_HAVEFENG_CANTING.equals(roomInfo.getMode())
				|| GameInfoShengShi.SS_DA_HAVEFENG_CANTING.equals(roomInfo.getMode())
				|| GameInfoShengShi.SS_Ping_NOFENG_CANTING.equals(roomInfo.getMode())
				|| GameInfoShengShi.SS_DA_NOFENG_CANTING.equals(roomInfo.getMode())) {
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
		// 听之后 杠后的牌还能听
		if (isTing && super.isCanGangAddThisCard(card)) {
			List<String> temp = getCardsAddThisCard(card);
			// 去掉 这张杠牌
			int ct = CardTypeUtil.cardType.get(card);
			return isCanTingAfterGang(temp, ct);

		} else
			return super.isCanGangAddThisCard(card);

	}

	@Override
	public boolean isCanGangThisCard(String card) {
		if (isTing && super.isCanGangThisCard(card)) {
			List<String> temp = new ArrayList<>();
			temp.addAll(cards);
			// 去掉 这张杠牌
			int cardType = CardTypeUtil.cardType.get(card);

			return isCanTingAfterGang(temp, cardType);

		} else
			return super.isCanGangThisCard(card);

	}

	public static void main(String[] args) {
		PlayerCardsInfoSSGK playerCardsInfoKD = new PlayerCardsInfoSSGK();
		// 是否能杠: isTing = true cards = [101, 098, 103, 096, 088, 091, 090, 000,
		// 003, 097, 099, 059, 102, 067, 100]
		String[] s = new String[] { "112", "113", "114", "115", "044", "048",
				"052", "060", "061", "062", "064", "068", "092", "100" };

		playerCardsInfoKD.cards = new ArrayList<>();
		List<String> list = Arrays.asList(s);
		playerCardsInfoKD.cards.addAll(list);
		// playerCardsInfoKD.anGangType.add(28);
		playerCardsInfoKD.mingGangType.put(28, 0L);
		// playerCardsInfoKD.pengType.put(25,1);

		playerCardsInfoKD.isTing = true;
		System.out.println(playerCardsInfoKD.isCanPengAddThisCard("063"));
		System.out.println(playerCardsInfoKD.isCanGangThisCard("063"));
		// System.out.println(playerCardsInfoKD.isCanGangThisCard("100"));

	}

	/**
	 * 杠之后是否能听
	 * 
	 * @param cards
	 * @param cardType
	 * @return
	 */
	protected boolean isCanTingAfterGang(List<String> cards, int cardType) {
		// 先删除这次杠的
		removeCardByType(cards, cardType, 4);
		for (int pt : pengType.keySet()) {// 如果杠的是之前碰过的牌
			if (pt != cardType) {
				removeCardByType(cards, pt, 3);
			}
		}
		// 去掉杠的牌
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
		System.out.println("是否能杠: " + " isTing = " + isTing + "  cards = "
				+ cards);
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

		} else
			return super.isHasGang();
	}

	// 是否能杠: isTing = true cards = [101, 098, 103, 096, 088, 091, 090, 000, 003,
	// 097, 099, 059, 102, 092, 100]
	@Override
	public boolean isCanPengAddThisCard(String card) {
		// 听之后不能碰牌
		if (isTing) {
			return false;
		}
		return super.isCanPengAddThisCard(card);
	}

	@Override
	public boolean isCanHu_dianpao(String card) {
		return (!isHasTing || isTing)
				&& CardTypeUtil.getCardTingScore(card) >= DIANPAO_MIN_SCORE
				&& super.isCanHu_dianpao(card);
	}

	@Override
	public boolean isCanHu_zimo(String card) {
		return (!isHasTing || isTing)
				&& CardTypeUtil.getCardTingScore(card) >= ZIMO_MIN_SCORE
				&& super.isCanHu_zimo(card);
	}

	@Override
	public boolean checkPlayCard(String card) {
		if (isTing) {
			return super.checkPlayCard(card) && card.equals(catchCard);
		} else {
			return super.checkPlayCard(card);
		}
	}

	// 杠牌分数计算
	@Override
	public void gangCompute(RoomInfo room, GameInfo gameInfo, boolean isMing,
                            long diangangUser, String card) {
		super.gangCompute(room, gameInfo, isMing, diangangUser, card);
		if (this.roomInfo.getGameType().equals("HT")
				|| this.roomInfo.getGameType().equals("JL")
				|| this.roomInfo.getGameType().equals("DS")
				|| this.roomInfo.getGameType().equals("SS")
				|| this.roomInfo.getGameType().equals("JCSS")) {
			if (!isMing) {// 暗杠
				for (long i : gameInfo.getPlayerCardsInfos().keySet()) {
					gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - 2 * room.getMultiple());
					room.setUserSocre(i, -2 * room.getMultiple());
				}
				this.score = this.score + 2 * room.getPersonNumber() * room.getMultiple();
				room.setUserSocre(this.userId, 2 * room.getPersonNumber() * room.getMultiple());
			} else {// 明杠
				if (diangangUser == -1) {
					for (long i : gameInfo.getPlayerCardsInfos().keySet()) {
						gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - room.getMultiple());
						room.setUserSocre(i, -room.getMultiple());
					}
					this.score = this.score + room.getPersonNumber() * room.getMultiple();
					room.setUserSocre(this.userId, room.getPersonNumber() * room.getMultiple());
				} else {
					if(this.roomInfo.getGameType().equals("JCSS")){
						if(gameInfo.getPlayerCardsInfos().get(diangangUser).isTing){
							for (long i : gameInfo.getPlayerCardsInfos().keySet()) {
								gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - room.getMultiple());
								room.setUserSocre(i, -room.getMultiple());
							}
							this.score = this.score + room.getPersonNumber() * room.getMultiple();
							room.setUserSocre(this.userId, room.getPersonNumber() * room.getMultiple());
						}else{
							gameInfo.getPlayerCardsInfos().get(diangangUser).setScore(gameInfo.getPlayerCardsInfos().get(diangangUser).getScore() - (room.getPersonNumber() - 1) * room.getMultiple());
							this.score = this.score + (room.getPersonNumber() - 1) * room.getMultiple();
							room.setUserSocre(diangangUser, -(room.getPersonNumber() - 1) * room.getMultiple());
							room.setUserSocre(this.userId, (room.getPersonNumber() - 1) * room.getMultiple());
						}
					}else{
						gameInfo.getPlayerCardsInfos().get(diangangUser).setScore(gameInfo.getPlayerCardsInfos().get(diangangUser).getScore() - (room.getPersonNumber() - 1) * room.getMultiple());
						this.score = this.score + (room.getPersonNumber() - 1) * room.getMultiple();
						room.setUserSocre(diangangUser, -(room.getPersonNumber() - 1) * room.getMultiple());
						room.setUserSocre(this.userId, (room.getPersonNumber() - 1) * room.getMultiple());
					}
				}
			}
			room.pushScoreChange();
		}
	}




	// 胡牌分数计算;
	@Override
	public void huCompute(RoomInfo room, GameInfo gameInfo, boolean isZimo,
						  long dianpaoUser, String card) {
		List<String> cs = getCardsNoChiPengGang(cards);
		List<HuCardType> huList = HuUtil.isHu(cs, this,CardTypeUtil.cardType.get(card), new HuLimit(0));
		// 设置胡牌类型
		HuCardType huCardType = getMaxScoreHuCardType(huList);
		this.winType.addAll(huCardType.specialHuList);

		//是否是杠开
		boolean isGangKai = isGangKai();
		if (isGangKai){
			if (isZimo) {// 自摸
				if (room.getModeTotal().equals("110") && (
						room.getMode().equals(GameInfoShengShi.SS_Ping_HAVEFENG_CANTING)
								|| room.getMode().equals(GameInfoShengShi.SS_Ping_HAVEFENG_NOTING)
								|| room.getMode().equals(GameInfoShengShi.SS_Ping_NOFENG_CANTING)
								|| room.getMode().equals(GameInfoShengShi.SS_Ping_NOFENG_NOTING))) {// 平胡
					for (long i : gameInfo.getPlayerCardsInfos().keySet()){
						gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - 4 * room.getMultiple());
						room.setUserSocre(i, - 4 * room.getMultiple());
					}
					this.score = this.score + 4 * room.getPersonNumber() * room.getMultiple();
					room.setUserSocre(this.userId, 4 * room.getPersonNumber() * room.getMultiple());
					this.fan = 4;
				} else if (room.getModeTotal().equals("110") && (
						room.getMode().equals(GameInfoShengShi.SS_DA_HAVEFENG_CANTING)
								|| room.getMode().equals(GameInfoShengShi.SS_DA_HAVEFENG_NOTING)
								|| room.getMode().equals(GameInfoShengShi.SS_DA_NOFENG_CANTING)
								|| room.getMode().equals(GameInfoShengShi.SS_DA_NOFENG_NOTING))) {// 大胡
					for (long i : gameInfo.getPlayerCardsInfos().keySet()) {
						gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - 2 * room.getMultiple() * MahjongCode.HUTOSCOREFORSS.get(""+ CardUtil.huForScores(cards,huCardType)));
						room.setUserSocre(i,  - 2 * room.getMultiple() * MahjongCode.HUTOSCOREFORSS.get(""+CardUtil.huForScores(cards,huCardType)));
					}
					this.score = this.score + 2*room.getPersonNumber() * room.getMultiple() * MahjongCode.HUTOSCOREFORSS.get(""+CardUtil.huForScores(cards,huCardType)+"");
					room.setUserSocre(this.userId, 2*room.getPersonNumber() * room.getMultiple() * MahjongCode.HUTOSCOREFORSS.get(""+CardUtil.huForScores(cards,huCardType)+""));
					this.fan = 2*MahjongCode.HUTOSCOREFORSS.get(""+CardUtil.huForScores(cards,huCardType)+"");
				}
			} else {// 点炮
				if (room.getModeTotal().equals("110") && (
						room.getMode().equals(GameInfoShengShi.SS_Ping_HAVEFENG_CANTING)
								|| room.getMode().equals(GameInfoShengShi.SS_Ping_HAVEFENG_NOTING)
								|| room.getMode().equals(GameInfoShengShi.SS_Ping_NOFENG_CANTING)
								|| room.getMode().equals(GameInfoShengShi.SS_Ping_NOFENG_NOTING))) {// 平胡
					if (room.getMode().equals(GameInfoShengShi.SS_Ping_HAVEFENG_CANTING)
							|| room.getMode().equals(GameInfoShengShi.SS_Ping_NOFENG_CANTING)) {// 听的
						if (gameInfo.getPlayerCardsInfos().get(dianpaoUser).isTing) {// 上听
							for (long i : gameInfo.getPlayerCardsInfos().keySet()) {
								gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - room.getMultiple() * 2);
								room.setUserSocre(i,  - room.getMultiple() * 2);
							}
							this.score = this.score + 2 * room.getPersonNumber() * room.getMultiple();
							room.setUserSocre(this.userId, 2 * room.getPersonNumber() * room.getMultiple());
							this.fan = 2;
						} else {
							gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - (room.getPersonNumber()-1) * 2 * room.getMultiple());
							room.setUserSocre(dianpaoUser, -(room.getPersonNumber()-1) * 2 * room.getMultiple());

							this.score = this.score + (room.getPersonNumber()-1) * 2 * room.getMultiple();
							room.setUserSocre(this.userId, (room.getPersonNumber()-1) * 2 * room.getMultiple());
							this.fan = 2;
						}
					} else if (room.getMode().equals(GameInfoShengShi.SS_Ping_HAVEFENG_NOTING)
							|| room.getMode().equals(GameInfoShengShi.SS_Ping_NOFENG_NOTING)) {// 不含听的
						gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - (room.getPersonNumber()-1) * 2 * room.getMultiple());
						room.setUserSocre(dianpaoUser, -(room.getPersonNumber()-1) * 2 * room.getMultiple());

						this.score = this.score + (room.getPersonNumber()-1) * 2 * room.getMultiple();
						room.setUserSocre(this.userId, (room.getPersonNumber()-1) * 2 * room.getMultiple());
						this.fan = 2;
					}
				} else if (room.getModeTotal().equals("110")
						&& (room.getMode().equals(GameInfoShengShi.SS_DA_HAVEFENG_CANTING)
						|| room.getMode().equals(GameInfoShengShi.SS_DA_HAVEFENG_NOTING)
						|| room.getMode().equals(GameInfoShengShi.SS_DA_NOFENG_CANTING)
						|| room.getMode().equals(GameInfoShengShi.SS_DA_NOFENG_NOTING))) {// 大胡
					if (room.getMode().equals(GameInfoShengShi.SS_DA_HAVEFENG_CANTING)
							|| room.getMode().equals(GameInfoShengShi.SS_DA_NOFENG_CANTING)) {// 听的
						if (gameInfo.getPlayerCardsInfos().get(dianpaoUser).isTing) {
							if(2==MahjongCode.HUTOSCOREFORSS.get(""+CardUtil.huForScores(cards,huCardType)+"")){
								for (long i : gameInfo.getPlayerCardsInfos().keySet()) {
									gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - room.getMultiple() * 2);
									room.setUserSocre(i,  - room.getMultiple() * 2);
								}
								this.score = this.score + 2 * room.getPersonNumber() * room.getMultiple();
								room.setUserSocre(this.userId, 2 * room.getPersonNumber() * room.getMultiple());
								this.fan = 2;
							}else{
								for (long i : gameInfo.getPlayerCardsInfos().keySet()) {
									gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - 2*room.getMultiple() * MahjongCode.HUTOSCOREFORSS.get(""+CardUtil.huForScores(cards,huCardType)+"")/3);
									room.setUserSocre(i,  - 2*room.getMultiple() * MahjongCode.HUTOSCOREFORSS.get(""+CardUtil.huForScores(cards,huCardType)+"")/3);
								}
								this.score = this.score + 2*MahjongCode.HUTOSCOREFORSS.get(""+CardUtil.huForScores(cards,huCardType)+"")/3 * room.getPersonNumber() * room.getMultiple();
								room.setUserSocre(this.userId, 2*MahjongCode.HUTOSCOREFORSS.get(""+CardUtil.huForScores(cards,huCardType)+"")/3 * room.getPersonNumber() * room.getMultiple());
								this.fan = 2*MahjongCode.HUTOSCOREFORSS.get(""+CardUtil.huForScores(cards,huCardType)+"")/3;
							}
						} else {// 未上听
							if(2==MahjongCode.HUTOSCOREFORSS.get(""+CardUtil.huForScores(cards,huCardType)+"")){
								gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - room.getMultiple() * 6);
								room.setUserSocre(dianpaoUser,  - room.getMultiple() * 6);
								this.score = this.score + room.getMultiple() * 6;
								room.setUserSocre(this.userId, room.getMultiple() * 6);
								this.fan = 6;
							}else{
								gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - 2*room.getMultiple() * MahjongCode.HUTOSCOREFORSS.get(""+CardUtil.huForScores(cards,huCardType)));
								room.setUserSocre(dianpaoUser,  - 2*room.getMultiple() * MahjongCode.HUTOSCOREFORSS.get(""+CardUtil.huForScores(cards,huCardType)));
								this.score = this.score + 2*room.getMultiple() * MahjongCode.HUTOSCOREFORSS.get(""+CardUtil.huForScores(cards,huCardType)+"");
								room.setUserSocre(this.userId, 2*room.getMultiple() * MahjongCode.HUTOSCOREFORSS.get(""+CardUtil.huForScores(cards,huCardType)+""));
								this.fan = 2*MahjongCode.HUTOSCOREFORSS.get(""+CardUtil.huForScores(cards,huCardType)+"");
							}
						}
					} else if (room.getMode().equals(GameInfoShengShi.SS_DA_HAVEFENG_NOTING)
							|| room.getMode().equals(GameInfoShengShi.SS_DA_NOFENG_NOTING)) {// 不带听
						if(2==MahjongCode.HUTOSCOREFORSS.get(""+CardUtil.huForScores(cards,huCardType)+"")){
							gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - room.getMultiple() * 6);
							room.setUserSocre(dianpaoUser,  - room.getMultiple() * 6);
							this.score = this.score + room.getMultiple() * 6;
							room.setUserSocre(this.userId, room.getMultiple() * 6);
							this.fan = 6;
						}else{
							gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - 2*room.getMultiple() * MahjongCode.HUTOSCOREFORSS.get(""+CardUtil.huForScores(cards,huCardType)));
							room.setUserSocre(dianpaoUser,  - 2*room.getMultiple() * MahjongCode.HUTOSCOREFORSS.get(""+CardUtil.huForScores(cards,huCardType)));
							this.score = this.score +  2*room.getMultiple() * MahjongCode.HUTOSCOREFORSS.get(""+CardUtil.huForScores(cards,huCardType)+"");
							room.setUserSocre(this.userId, 2*room.getMultiple() * MahjongCode.HUTOSCOREFORSS.get(""+CardUtil.huForScores(cards,huCardType)+""));
							this.fan = 2*MahjongCode.HUTOSCOREFORSS.get(""+CardUtil.huForScores(cards,huCardType)+"");
						}
					}
				}
			}
		}else{
			if (isZimo) {// 自摸
				if (room.getModeTotal().equals("110") && (
						room.getMode().equals(GameInfoShengShi.SS_Ping_HAVEFENG_CANTING)
								|| room.getMode().equals(GameInfoShengShi.SS_Ping_HAVEFENG_NOTING)
								|| room.getMode().equals(GameInfoShengShi.SS_Ping_NOFENG_CANTING)
								|| room.getMode().equals(GameInfoShengShi.SS_Ping_NOFENG_NOTING))) {// 平胡
					for (long i : gameInfo.getPlayerCardsInfos().keySet()){
						gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - 2 * room.getMultiple());
						room.setUserSocre(i, - 2 * room.getMultiple());
					}
					this.score = this.score + 2 * room.getPersonNumber() * room.getMultiple();
					room.setUserSocre(this.userId, 2 * room.getPersonNumber() * room.getMultiple());
					this.fan = 2;
				} else if (room.getModeTotal().equals("110") && (
						room.getMode().equals(GameInfoShengShi.SS_DA_HAVEFENG_CANTING)
								|| room.getMode().equals(GameInfoShengShi.SS_DA_HAVEFENG_NOTING)
								|| room.getMode().equals(GameInfoShengShi.SS_DA_NOFENG_CANTING)
								|| room.getMode().equals(GameInfoShengShi.SS_DA_NOFENG_NOTING))) {// 大胡
					for (long i : gameInfo.getPlayerCardsInfos().keySet()) {
						gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - room.getMultiple() * MahjongCode.HUTOSCOREFORSS.get(""+ CardUtil.huForScores(cards,huCardType)));
						room.setUserSocre(i,  - room.getMultiple() * MahjongCode.HUTOSCOREFORSS.get(""+CardUtil.huForScores(cards,huCardType)));
					}
					this.score = this.score + room.getPersonNumber() * room.getMultiple() * MahjongCode.HUTOSCOREFORSS.get(""+CardUtil.huForScores(cards,huCardType)+"");
					room.setUserSocre(this.userId, room.getPersonNumber() * room.getMultiple() * MahjongCode.HUTOSCOREFORSS.get(""+CardUtil.huForScores(cards,huCardType)+""));
					this.fan = MahjongCode.HUTOSCOREFORSS.get(""+CardUtil.huForScores(cards,huCardType)+"");
				}
			} else {// 点炮
				if (room.getModeTotal().equals("110") && (
						room.getMode().equals(GameInfoShengShi.SS_Ping_HAVEFENG_CANTING)
								|| room.getMode().equals(GameInfoShengShi.SS_Ping_HAVEFENG_NOTING)
								|| room.getMode().equals(GameInfoShengShi.SS_Ping_NOFENG_CANTING)
								|| room.getMode().equals(GameInfoShengShi.SS_Ping_NOFENG_NOTING))) {// 平胡
					if (room.getMode().equals(GameInfoShengShi.SS_Ping_HAVEFENG_CANTING)
							|| room.getMode().equals(GameInfoShengShi.SS_Ping_NOFENG_CANTING)) {// 听的
						if (gameInfo.getPlayerCardsInfos().get(dianpaoUser).isTing) {// 上听
							for (long i : gameInfo.getPlayerCardsInfos().keySet()) {
								gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - room.getMultiple() * 1);
								room.setUserSocre(i,  - room.getMultiple() * 1);
							}
							this.score = this.score + 1 * room.getPersonNumber() * room.getMultiple();
							room.setUserSocre(this.userId, 1 * room.getPersonNumber() * room.getMultiple());
							this.fan = 1;
						} else {
							gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - (room.getPersonNumber()-1) * 1 * room.getMultiple());
							room.setUserSocre(dianpaoUser, -(room.getPersonNumber()-1) * 1 * room.getMultiple());

							this.score = this.score + (room.getPersonNumber()-1) * 1 * room.getMultiple();
							room.setUserSocre(this.userId, (room.getPersonNumber()-1) * 1 * room.getMultiple());
							this.fan = 1;
						}
					} else if (room.getMode().equals(GameInfoShengShi.SS_Ping_HAVEFENG_NOTING)
							|| room.getMode().equals(GameInfoShengShi.SS_Ping_NOFENG_NOTING)) {// 不含听的
						gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - (room.getPersonNumber()-1) * 1 * room.getMultiple());
						room.setUserSocre(dianpaoUser, -(room.getPersonNumber()-1) * 1 * room.getMultiple());

						this.score = this.score + (room.getPersonNumber()-1) * 1 * room.getMultiple();
						room.setUserSocre(this.userId, (room.getPersonNumber()-1) * 1 * room.getMultiple());
						this.fan = 1;
					}
				} else if (room.getModeTotal().equals("110")
						&& (room.getMode().equals(GameInfoShengShi.SS_DA_HAVEFENG_CANTING)
						|| room.getMode().equals(GameInfoShengShi.SS_DA_HAVEFENG_NOTING)
						|| room.getMode().equals(GameInfoShengShi.SS_DA_NOFENG_CANTING)
						|| room.getMode().equals(GameInfoShengShi.SS_DA_NOFENG_NOTING))) {// 大胡
					if (room.getMode().equals(GameInfoShengShi.SS_DA_HAVEFENG_CANTING)
							|| room.getMode().equals(GameInfoShengShi.SS_DA_NOFENG_CANTING)) {// 听的
						if (gameInfo.getPlayerCardsInfos().get(dianpaoUser).isTing) {
							if(2==MahjongCode.HUTOSCOREFORSS.get(""+CardUtil.huForScores(cards,huCardType)+"")){
								for (long i : gameInfo.getPlayerCardsInfos().keySet()) {
									gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - room.getMultiple() * 1);
									room.setUserSocre(i,  - room.getMultiple() * 1);
								}
								this.score = this.score + 1 * room.getPersonNumber() * room.getMultiple();
								room.setUserSocre(this.userId, 1 * room.getPersonNumber() * room.getMultiple());
								this.fan = 1;
							}else{
								for (long i : gameInfo.getPlayerCardsInfos().keySet()) {
									gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - room.getMultiple() * MahjongCode.HUTOSCOREFORSS.get(""+CardUtil.huForScores(cards,huCardType)+"")/3);
									room.setUserSocre(i,  - room.getMultiple() * MahjongCode.HUTOSCOREFORSS.get(""+CardUtil.huForScores(cards,huCardType)+"")/3);
								}
								this.score = this.score + MahjongCode.HUTOSCOREFORSS.get(""+CardUtil.huForScores(cards,huCardType)+"")/3 * room.getPersonNumber() * room.getMultiple();
								room.setUserSocre(this.userId, MahjongCode.HUTOSCOREFORSS.get(""+CardUtil.huForScores(cards,huCardType)+"")/3 * room.getPersonNumber() * room.getMultiple());
								this.fan = MahjongCode.HUTOSCOREFORSS.get(""+CardUtil.huForScores(cards,huCardType)+"")/3;
							/*gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - room.getMultiple() * MahjongCode.HUTOSCOREFORSS.get(""+CardUtil.huForScores(cards,huCardType)));
		            		room.setUserSocre(dianpaoUser,  - room.getMultiple() * MahjongCode.HUTOSCOREFORSS.get(""+CardUtil.huForScores(cards,huCardType)));
							this.score = this.score + room.getMultiple() * MahjongCode.HUTOSCOREFORSS.get(""+CardUtil.huForScores(cards,huCardType)+"");
		            		room.setUserSocre(this.userId, room.getMultiple() * MahjongCode.HUTOSCOREFORSS.get(""+CardUtil.huForScores(cards,huCardType)+""));
		            		this.fan = MahjongCode.HUTOSCOREFORSS.get(""+CardUtil.huForScores(cards,huCardType)+"");*/
							}
						} else {// 未上听
							if(2==MahjongCode.HUTOSCOREFORSS.get(""+CardUtil.huForScores(cards,huCardType)+"")){
								gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - room.getMultiple() * 3);
								room.setUserSocre(dianpaoUser,  - room.getMultiple() * 3);
								this.score = this.score + room.getMultiple() * 3;
								room.setUserSocre(this.userId, room.getMultiple() * 3);
								this.fan = 3;
							}else{
								gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - room.getMultiple() * MahjongCode.HUTOSCOREFORSS.get(""+CardUtil.huForScores(cards,huCardType)));
								room.setUserSocre(dianpaoUser,  - room.getMultiple() * MahjongCode.HUTOSCOREFORSS.get(""+CardUtil.huForScores(cards,huCardType)));
								this.score = this.score + room.getMultiple() * MahjongCode.HUTOSCOREFORSS.get(""+CardUtil.huForScores(cards,huCardType)+"");
								room.setUserSocre(this.userId, room.getMultiple() * MahjongCode.HUTOSCOREFORSS.get(""+CardUtil.huForScores(cards,huCardType)+""));
								this.fan = MahjongCode.HUTOSCOREFORSS.get(""+CardUtil.huForScores(cards,huCardType)+"");
							}
						}
					} else if (room.getMode().equals(GameInfoShengShi.SS_DA_HAVEFENG_NOTING)
							|| room.getMode().equals(GameInfoShengShi.SS_DA_NOFENG_NOTING)) {// 不带听
						if(2==MahjongCode.HUTOSCOREFORSS.get(""+CardUtil.huForScores(cards,huCardType)+"")){
							gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - room.getMultiple() * 3);
							room.setUserSocre(dianpaoUser,  - room.getMultiple() * 3);
							this.score = this.score + room.getMultiple() * 3;
							room.setUserSocre(this.userId, room.getMultiple() * 3);
							this.fan = 3;
						}else{
							gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - room.getMultiple() * MahjongCode.HUTOSCOREFORSS.get(""+CardUtil.huForScores(cards,huCardType)));
							room.setUserSocre(dianpaoUser,  - room.getMultiple() * MahjongCode.HUTOSCOREFORSS.get(""+CardUtil.huForScores(cards,huCardType)));
							this.score = this.score +  room.getMultiple() * MahjongCode.HUTOSCOREFORSS.get(""+CardUtil.huForScores(cards,huCardType)+"");
							room.setUserSocre(this.userId, room.getMultiple() * MahjongCode.HUTOSCOREFORSS.get(""+CardUtil.huForScores(cards,huCardType)+""));
							this.fan = MahjongCode.HUTOSCOREFORSS.get(""+CardUtil.huForScores(cards,huCardType)+"");
						}
					}
				}
			}
		}



	}
}
