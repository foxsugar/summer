package com.code.server.game.mahjong.logic;


import com.code.server.game.mahjong.util.CardUtil;
import com.code.server.game.mahjong.util.HuCardType;
import com.code.server.game.mahjong.util.HuLimit;
import com.code.server.game.mahjong.util.HuUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class PlayerCardsInfoKD extends PlayerCardsInfo {
	private static final int TING_MIN_SCORE = 6;
	private static final int ZIMO_MIN_SCORE = 3;
	private static final int DIANPAO_MIN_SCORE = 6;

	@Override
	public void init(List<String> cards) {
		super.init(cards);
		specialHuScore.put(hu_清一色,2);
		specialHuScore.put(hu_一条龙,2);
		specialHuScore.put(hu_七小对,2);
		specialHuScore.put(hu_十三幺,4);
		specialHuScore.put(hu_豪华七小对,4);
		specialHuScore.put(hu_双豪七小对_山西,4);
		specialHuScore.put(hu_清龙,4);

		if (roomInfo.getMode().equals("5")) {
			specialHuScore.put(hu_清一色,1);
			specialHuScore.put(hu_一条龙,1);
		}

	}


	@Override
	public boolean isCanTing(List<String> cards) {
		if (isTing) {
			return false;
		}
		Set<Integer> set = getTingCardType(getCardsNoChiPengGang(cards),null);
		for (int type : set) {
			if (CardTypeUtil.cardTingScore.get(type) >= TING_MIN_SCORE) {
				return true;
			}
		}
		return false;
	}
	public boolean isHasChi(String card){
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
		PlayerCardsInfoKD playerCardsInfoKD = new PlayerCardsInfoKD();
		//	是否能杠:  isTing = true  cards = [101, 098, 103, 096, 088, 091, 090, 000, 003, 097, 099, 059, 102, 067, 100]
		String[] s = new String[]{"112","113","114","115","044","048","052","060","061","062","064",  "068","092","100"};

		playerCardsInfoKD.cards = new ArrayList<>();
		List<String> list = Arrays.asList(s);
		playerCardsInfoKD.cards.addAll(list);
//		playerCardsInfoKD.anGangType.add(28);
		playerCardsInfoKD.mingGangType.put(28,0L);
//		playerCardsInfoKD.pengType.put(25,1);

		playerCardsInfoKD.isTing = true;
		System.out.println(playerCardsInfoKD.isCanPengAddThisCard("063"));
		System.out.println(playerCardsInfoKD.isCanGangThisCard("063"));
//		System.out.println(playerCardsInfoKD.isCanGangThisCard("100"));




	}

	/**
	 * 杠之后是否能听
	 * @param cards
	 * @param cardType
	 * @return
	 */
	private boolean isCanTingAfterGang(List<String> cards,int cardType){
		//先删除这次杠的
		removeCardByType(cards,cardType,4);
		for(int pt : pengType.keySet()){//如果杠的是之前碰过的牌
			if (pt != cardType) {
				removeCardByType(cards,pt,3);
			}
		}
		//去掉杠的牌
		cards = getCardsNoGang(cards);
		Set<Integer> set = getTingCardType(cards,null);
		for (int type : set) {
			if (CardTypeUtil.cardTingScore.get(type) >= TING_MIN_SCORE) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isHasGang() {
		System.out.println("是否能杠: "+" isTing = "+isTing +"  cards = "+cards);
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

		}else return super.isHasGang();
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
		return isTing && CardTypeUtil.getCardTingScore(card)>=DIANPAO_MIN_SCORE && super.isCanHu_dianpao(card);
	}

	@Override
	public boolean isCanHu_zimo(String card) {
		return isTing && CardTypeUtil.getCardTingScore(card)>=ZIMO_MIN_SCORE && super.isCanHu_zimo(card);
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
    public void gangCompute(RoomInfo room, GameInfo gameInfo, boolean isMing, long diangangUser, String card){
		super.gangCompute(room,gameInfo,isMing,diangangUser,card);
		if(!isMing){//暗杠
        	for (Long i : gameInfo.getPlayerCardsInfos().keySet()){
        		gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - 2 * CardUtil.KDGangForScores(card) * room.getMultiple());
        		room.setUserSocre(i, - 2 * CardUtil.KDGangForScores(card) * room.getMultiple());
        	}
        	this.score = this.score + 2 * CardUtil.KDGangForScores(card) * 4 * room.getMultiple();
        	room.setUserSocre(this.userId, 2 * CardUtil.KDGangForScores(card) * 4 * room.getMultiple());
			System.out.println("======暗杠：" + 2 * CardUtil.KDGangForScores(card) * 3 * room.getMultiple());
		}else{//明杠
    		if (diangangUser==-1) {
    			for (Long i : gameInfo.getPlayerCardsInfos().keySet()){
            		gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - CardUtil.KDGangForScores(card) * room.getMultiple());
            		room.setUserSocre(i, - CardUtil.KDGangForScores(card) * room.getMultiple());
    			}
    			this.score = this.score + CardUtil.KDGangForScores(card) * 4 * room.getMultiple();
    			room.setUserSocre(this.userId, CardUtil.KDGangForScores(card) * 4 * room.getMultiple());
				System.out.println("======明杠：" + CardUtil.KDGangForScores(card) * 3 * room.getMultiple());
			} else {
				if(gameInfo.getPlayerCardsInfos().get(diangangUser).isTing){
	    			for (Long i : gameInfo.getPlayerCardsInfos().keySet()){
	            		gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - CardUtil.KDGangForScores(card) * room.getMultiple());
	            		room.setUserSocre(i, - CardUtil.KDGangForScores(card) * room.getMultiple());
	    			}
	    			this.score = this.score + CardUtil.KDGangForScores(card) * 4 * room.getMultiple();
	    			room.setUserSocre(this.userId, CardUtil.KDGangForScores(card) * 4 * room.getMultiple());
					System.out.println("======明杠（已听）：" + CardUtil.KDGangForScores(card) * 3 * room.getMultiple());
	    		}else{
	    			gameInfo.getPlayerCardsInfos().get(diangangUser).setScore(gameInfo.getPlayerCardsInfos().get(diangangUser).getScore() - 3 * CardUtil.KDGangForScores(card) * room.getMultiple());
	    			gameInfo.getPlayerCardsInfos().get(this.userId).setScore(gameInfo.getPlayerCardsInfos().get(this.userId).getScore() + 3 * CardUtil.KDGangForScores(card) * room.getMultiple());
	    			room.setUserSocre(diangangUser, - 3 * CardUtil.KDGangForScores(card) * room.getMultiple());
	        		room.setUserSocre(this.userId, 3 * CardUtil.KDGangForScores(card) * room.getMultiple());
					System.out.println("======明杠（未听）：" + 3 * CardUtil.KDGangForScores(card) * room.getMultiple());
	    		}
			}
    	}
    	room.pushScoreChange();
    }

    //胡牌分数计算
	@Override
    public void huCompute(RoomInfo room, GameInfo gameInfo, boolean isZimo, long dianpaoUser, String card){
		List<String> cs = getCardsNoChiPengGang(cards);
		List<HuCardType> huList = HuUtil.isHu(cs, this,CardTypeUtil.cardType.get(card) , new HuLimit(0));
		//设置胡牌类型
		HuCardType huCardType = getMaxScoreHuCardType(huList);

		setWinTypeResult(huCardType);
		if(winType.contains(hu_双豪七小对_山西)){
			winType.remove(hu_双豪七小对_山西);
			winType.add(hu_豪华七小对);
		}

    	StringBuffer sb = new StringBuffer();
    	for (String s : cards) {
			if(!s.equals(card)){
				sb.append(s);
				sb.append(",");
			}
		}
//		if (room.getMode().equals("6")){
			if(isZimo){
				for (Long i : gameInfo.getPlayerCardsInfos().keySet()){
					gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - 2 * CardUtil.KDForScoresDoubleScore(sb.toString().substring(0, sb.length() - 1), card,huCardType) * room.getMultiple());
					room.setUserSocre(i, - 2 * CardUtil.KDForScoresDoubleScore(sb.toString().substring(0, sb.length() - 1), card,huCardType) * room.getMultiple());
				}
				this.score = this.score +  8 * CardUtil.KDForScoresDoubleScore(sb.toString().substring(0, sb.length()-1),card,huCardType) * room.getMultiple();
				room.setUserSocre(this.userId, 8 * CardUtil.KDForScoresDoubleScore(sb.toString().substring(0, sb.length()-1),card,huCardType) * room.getMultiple());
				this.fan = 2 * CardUtil.KDForScoresDoubleScore(sb.toString().substring(0, sb.length() - 1), card,huCardType);
				System.out.println("======自摸：" + 6 * CardUtil.KDForScoresDoubleScore(sb.toString().substring(0, sb.length()-1),card,huCardType) * room.getMultiple());
			}else{
				if(gameInfo.getPlayerCardsInfos().get(dianpaoUser).isTing){
					for (Long i : gameInfo.getPlayerCardsInfos().keySet()){
						gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - CardUtil.KDForScoresDoubleScore(sb.toString().substring(0, sb.length() - 1), card,huCardType) * room.getMultiple());
						room.setUserSocre(i, - CardUtil.KDForScoresDoubleScore(sb.toString().substring(0, sb.length() - 1), card,huCardType) * room.getMultiple());
					}
					this.score = this.score + 4 * CardUtil.KDForScoresDoubleScore(sb.toString().substring(0, sb.length() - 1), card,huCardType) * room.getMultiple();
					room.setUserSocre(this.userId, 4 * CardUtil.KDForScoresDoubleScore(sb.toString().substring(0, sb.length() - 1), card,huCardType) * room.getMultiple());
					this.fan = CardUtil.KDForScoresDoubleScore(sb.toString().substring(0, sb.length() - 1), card,huCardType);
					System.out.println("======点炮（已听）：" + 3 * CardUtil.KDForScoresDoubleScore(sb.toString().substring(0, sb.length() - 1), card,huCardType) * room.getMultiple());
				}else{
					gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - 3 * CardUtil.KDForScoresDoubleScore(sb.toString().substring(0, sb.length() - 1), card,huCardType) * room.getMultiple());
					this.score = this.score + 3 * CardUtil.KDForScoresDoubleScore(sb.toString().substring(0, sb.length()-1),card,huCardType) * room.getMultiple();
					room.setUserSocre(dianpaoUser,- 3 * CardUtil.KDForScoresDoubleScore(sb.toString().substring(0, sb.length() - 1), card,huCardType) * room.getMultiple());
					room.setUserSocre(this.userId, 3 * CardUtil.KDForScoresDoubleScore(sb.toString().substring(0, sb.length()-1),card,huCardType) * room.getMultiple());
					this.fan = CardUtil.KDForScoresDoubleScore(sb.toString().substring(0, sb.length() - 1), card,huCardType);
					System.out.println("======点炮（未听）：" +  3 * CardUtil.KDForScoresDoubleScore(sb.toString().substring(0, sb.length()-1),card,huCardType) * room.getMultiple());
				}
			}
//		}else if(room.getMode().equals("5")){
//			if(isZimo){
//				for (Integer i : gameInfo.getPlayerCardsInfos().keySet()){
//					gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - 2 * CardUtil.KDForScores(sb.toString().substring(0, sb.length() - 1), card) * room.getMultiple());
//					room.setUserSocre(i, - 2 * CardUtil.KDForScores(sb.toString().substring(0, sb.length() - 1), card) * room.getMultiple());
//				}
//				this.score = this.score +  8 * CardUtil.KDForScores(sb.toString().substring(0, sb.length()-1),card) * room.getMultiple();
//				room.setUserSocre(this.userId, 8 * CardUtil.KDForScores(sb.toString().substring(0, sb.length()-1),card) * room.getMultiple());
//				this.fan = 2 * CardUtil.KDForScores(sb.toString().substring(0, sb.length()-1),card);
//			}else{
//				if(gameInfo.getPlayerCardsInfos().get(dianpaoUser).isTing){
//					for (Integer i : gameInfo.getPlayerCardsInfos().keySet()){
//						gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - CardUtil.KDForScores(sb.toString().substring(0, sb.length() - 1), card) * room.getMultiple());
//						room.setUserSocre(i, - CardUtil.KDForScores(sb.toString().substring(0, sb.length() - 1), card) * room.getMultiple());
//					}
//					this.score = this.score + 4 * CardUtil.KDForScores(sb.toString().substring(0, sb.length() - 1), card) * room.getMultiple();
//					room.setUserSocre(this.userId, 4 * CardUtil.KDForScores(sb.toString().substring(0, sb.length() - 1), card) * room.getMultiple());
//					this.fan = CardUtil.KDForScores(sb.toString().substring(0, sb.length()-1),card);
//				}else{
//					gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - 3 * CardUtil.KDForScores(sb.toString().substring(0, sb.length() - 1), card) * room.getMultiple());
//					this.score = this.score + 3 * CardUtil.KDForScores(sb.toString().substring(0, sb.length()-1),card) * room.getMultiple();
//					room.setUserSocre(dianpaoUser,- 3 * CardUtil.KDForScores(sb.toString().substring(0, sb.length() - 1), card) * room.getMultiple());
//					room.setUserSocre(this.userId, 3 * CardUtil.KDForScores(sb.toString().substring(0, sb.length()-1),card) * room.getMultiple());
//					this.fan = CardUtil.KDForScores(sb.toString().substring(0, sb.length()-1),card);
//				}
//			}
//		}

    }

    
}
