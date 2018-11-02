package com.code.server.game.mahjong.logic;


import com.code.server.game.mahjong.util.*;

import java.util.List;

/**
*    
* 项目名称：mj_server   
* 类名称：PlayerCardsInfoTDH   
* 类描述：推倒胡
* 创建人：Clark  
* 创建时间：2016年12月5日 上午11:25:53   
* 修改人：Clark  
* 修改时间：2016年12月5日 上午11:25:53   
* 修改备注：   
* @version 1.0    
*
 */
public class PlayerCardsInfoTDH extends PlayerCardsInfoMj {

	@Override
	public void init(List<String> cards) {
		super.init(cards);
		specialHuScore.put(hu_清一色,1);
		specialHuScore.put(hu_一条龙,1);
		specialHuScore.put(hu_七小对,1);
		specialHuScore.put(hu_十三幺,1);
		specialHuScore.put(hu_豪华七小对,1);
		specialHuScore.put(hu_双豪七小对_山西,1);
		if ("TC".equals(this.roomInfo.getGameType()) || "TC1".equals(this.roomInfo.getGameType())) {
			this.roomInfo.setHasGangBlackList(false);
			this.setHasGangBlackList(false);
			MahjongCode.HUTOSCOREFORLQ.put("254", 36);
		}else{
			MahjongCode.HUTOSCOREFORLQ.put("254", 18);
		}
	}

	@Override
	public boolean isCanPengAddThisCard(String card) {
		//听之后不能碰牌
		if (isTing) {
			return false;
		}
		return super.isCanPengAddThisCard(card);
	}

	@Override
	public boolean isCanTing(List<String> cards) {
		if (isTing) {
			return false;
		}
		if("LQ".equals(this.roomInfo.getGameType())&&this.roomInfo.isHaveTing()){
			return getTingCardType(getCardsNoChiPengGang(cards),null).size()>0;
		}
		else if("BAIXING".equals(this.roomInfo.getGameType())&&this.roomInfo.isHaveTing()){
			return getTingCardType(getCardsNoChiPengGang(cards),null).size()>0;
		}
		if("1".equals(this.roomInfo.getMode())||"2".equals(this.roomInfo.getMode())||"3".equals(this.roomInfo.getMode())||"4".equals(this.roomInfo.getMode())){
			return false;
		}else{
			return getTingCardType(getCardsNoChiPengGang(cards),null).size()>0;
		}
	}

	/**
	 * 是否可以胡这张牌
	 *
	 * @param card
	 * @return
	 */
	public boolean isCanHu_dianpao(String card) {
		if (roomInfo.mustZimo == 1) {
			return false;
		}
		if("LQ".equals(this.roomInfo.getGameType())&&this.roomInfo.isHaveTing()){
			if (!isTing){
				return false;
			}
		}else if("BAIXING".equals(this.roomInfo.getGameType())&&this.roomInfo.isHaveTing()){
			if (!isTing){
				return false;
			}
		}
		if ("11".equals(this.roomInfo.getMode()) || "12".equals(this.roomInfo.getMode()) || "13".equals(this.roomInfo.getMode()) || "14".equals(this.roomInfo.getMode())) {
			if (!isTing) {
				return false;
			}
		}
		List<String> temp = getCardsAddThisCard(card);
		List<String> noPengAndGang = getCardsNoChiPengGang(temp);
		System.out.println("检测是否可胡点炮= " + noPengAndGang);
		int cardType = CardTypeUtil.cardType.get(card);
		return HuUtil.isHu(noPengAndGang, this, cardType, null).size() > 0;
	}

	@Override
	public boolean isCanHu_zimo(String card) {
		if("LQ".equals(this.roomInfo.getGameType())&&this.roomInfo.isHaveTing()){
			if (!isTing){
				return false;
			}
		}else if("BAIXING".equals(this.roomInfo.getGameType())&&this.roomInfo.isHaveTing()){
			if (!isTing){
				return false;
			}
		}
		if("11".equals(this.roomInfo.getMode())||"12".equals(this.roomInfo.getMode())||"13".equals(this.roomInfo.getMode())||"14".equals(this.roomInfo.getMode())){
			if (!isTing){
				return false;
			}
		}
		List<String> cs = getCardsNoChiPengGang(cards);
		System.out.println("检测是否可胡自摸= " + cs);
		int cardType = CardTypeUtil.cardType.get(card);
		return HuUtil.isHu(cs, this,cardType , null).size()>0;
	}


	public boolean isHasChi(String card){
		if(!this.roomInfo.isCanChi()){
			return false;
		}
		return super.isHasChi(card);
//		return false;
	}
	//杠牌分数计算
	@Override
    public void gangCompute(RoomInfo room, GameInfo gameInfo, boolean isMing, long diangangUser, String card){
		super.gangCompute(room,gameInfo,isMing,diangangUser,card);
    	if(this.roomInfo.getGameType().equals("HT") || this.roomInfo.getGameType().equals("JL") ||this.roomInfo.getGameType().equals("DS") ||this.roomInfo.getGameType().equals("LQ") ||this.roomInfo.getGameType().equals("QUANMIN")|| this.roomInfo.getGameType().equals("HL")){
    		if(!isMing){//暗杠
            	for (Long i : gameInfo.getPlayerCardsInfos().keySet()){
            		gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - 2 * room.getMultiple());
            		room.setUserSocre(i, - 2 * room.getMultiple());
            	}
            	this.score = this.score + 2 * room.getPersonNumber() * room.getMultiple();
            	room.setUserSocre(this.userId, 2 * room.getPersonNumber() * room.getMultiple());
        	}else{//明杠
        		if(diangangUser==-1){
        			for (Long i : gameInfo.getPlayerCardsInfos().keySet()){
                		gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - room.getMultiple());
                		room.setUserSocre(i, - room.getMultiple());
                	}
                	this.score = this.score + room.getPersonNumber() * room.getMultiple();
                	room.setUserSocre(this.userId, room.getPersonNumber() * room.getMultiple());
        		}else{
        			if(gameInfo.getPlayerCardsInfos().get(diangangUser).isTing){
						for (Long i : gameInfo.getPlayerCardsInfos().keySet()){
							gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - room.getMultiple());
							room.setUserSocre(i, - room.getMultiple());
						}
						this.score = this.score + room.getPersonNumber() * room.getMultiple();
						room.setUserSocre(this.userId, room.getPersonNumber() * room.getMultiple());
					}else{
						gameInfo.getPlayerCardsInfos().get(diangangUser).setScore(gameInfo.getPlayerCardsInfos().get(diangangUser).getScore() -  (room.getPersonNumber()-1) * room.getMultiple());
						this.score = this.score +  (room.getPersonNumber()-1) * room.getMultiple();
						room.setUserSocre(diangangUser, -  (room.getPersonNumber()-1) * room.getMultiple());
						room.setUserSocre(this.userId, (room.getPersonNumber()-1) * room.getMultiple());
					}
        		}
        	}
        	room.pushScoreChange();
    	}else if(this.roomInfo.getGameType().equals("BAIXING")){
			if(!isMing){//暗杠
				for (Long i : gameInfo.getPlayerCardsInfos().keySet()){
					gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - 3 * room.getMultiple());
					room.setUserSocre(i, - 3 * room.getMultiple());
				}
				this.score = this.score + 3 * room.getPersonNumber() * room.getMultiple();
				room.setUserSocre(this.userId, 3 * room.getPersonNumber() * room.getMultiple());
			}else{//明杠
				if(diangangUser==-1){
					for (Long i : gameInfo.getPlayerCardsInfos().keySet()){
						gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - room.getMultiple());
						room.setUserSocre(i, - room.getMultiple());
					}
					this.score = this.score + room.getPersonNumber() * room.getMultiple();
					room.setUserSocre(this.userId, room.getPersonNumber() * room.getMultiple());
				}else{
					gameInfo.getPlayerCardsInfos().get(diangangUser).setScore(gameInfo.getPlayerCardsInfos().get(diangangUser).getScore() -  (room.getPersonNumber()-1) * room.getMultiple());
					this.score = this.score +  (room.getPersonNumber()-1) * room.getMultiple();
					room.setUserSocre(diangangUser, -  (room.getPersonNumber()-1) * room.getMultiple());
					room.setUserSocre(this.userId, (room.getPersonNumber()-1) * room.getMultiple());
					}
				}
			}
		else if(this.roomInfo.getGameType().equals("TC") || this.roomInfo.getGameType().equals("TC1")){
			if(!isMing){//暗杠
				for (Long i : gameInfo.getPlayerCardsInfos().keySet()){
					gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - 2 * room.getMultiple());
					room.setUserSocre(i, - 2 * room.getMultiple());
				}
				this.score = this.score + 2 * room.getPersonNumber() * room.getMultiple();
				room.setUserSocre(this.userId, 2 * room.getPersonNumber() * room.getMultiple());
			}else{//明杠
				if(diangangUser==-1){
					for (Long i : gameInfo.getPlayerCardsInfos().keySet()){
						gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - room.getMultiple());
						room.setUserSocre(i, - room.getMultiple());
					}
					this.score = this.score + room.getPersonNumber() * room.getMultiple();
					room.setUserSocre(this.userId, room.getPersonNumber() * room.getMultiple());
				}else{
					gameInfo.getPlayerCardsInfos().get(diangangUser).setScore(gameInfo.getPlayerCardsInfos().get(diangangUser).getScore() -  (room.getPersonNumber()-1) * room.getMultiple());
					this.score = this.score +  (room.getPersonNumber()-1) * room.getMultiple();
					room.setUserSocre(diangangUser, -  (room.getPersonNumber()-1) * room.getMultiple());
					room.setUserSocre(this.userId, (room.getPersonNumber()-1) * room.getMultiple());
				}
			}
		}
		else if(this.roomInfo.getGameType().equals("XXPB")){
			if(!isMing){//暗杠
				for (Long i : gameInfo.getPlayerCardsInfos().keySet()){
					gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - 3 * room.getMultiple());
					room.setUserSocre(i, - 3 * room.getMultiple());
				}
				this.score = this.score + 3 * room.getPersonNumber() * room.getMultiple();
				room.setUserSocre(this.userId, 3 * room.getPersonNumber() * room.getMultiple());
			}else{//明杠
				if(diangangUser==-1){
					for (Long i : gameInfo.getPlayerCardsInfos().keySet()){
						gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - 2 * room.getMultiple());
						room.setUserSocre(i, - 2 * room.getMultiple());
					}
					this.score = this.score +  2 * room.getPersonNumber() * room.getMultiple();
					room.setUserSocre(this.userId,  2 * room.getPersonNumber() * room.getMultiple());
				}else{
					gameInfo.getPlayerCardsInfos().get(diangangUser).setScore(gameInfo.getPlayerCardsInfos().get(diangangUser).getScore() -  (room.getPersonNumber()-1) * room.getMultiple());
					this.score = this.score +   2 * (room.getPersonNumber()-1) * room.getMultiple();
					room.setUserSocre(diangangUser, -  (room.getPersonNumber()-1) * room.getMultiple());
					room.setUserSocre(this.userId, (room.getPersonNumber()-1) * room.getMultiple());
				}
			}
		}else if(this.roomInfo.getGameType().equals("TJW")){
			if(!isMing){//暗杠
				for (Long i : gameInfo.getPlayerCardsInfos().keySet()){
					gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - 2 * room.getMultiple());
					room.setUserSocre(i, - 2 * room.getMultiple());
				}
				this.score = this.score + 2 * room.getPersonNumber() * room.getMultiple();
				room.setUserSocre(this.userId, 2 * room.getPersonNumber() * room.getMultiple());
			}else{//明杠
				if(diangangUser==-1){
					for (Long i : gameInfo.getPlayerCardsInfos().keySet()){
						gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - room.getMultiple());
						room.setUserSocre(i, - room.getMultiple());
					}
					this.score = this.score + room.getPersonNumber() * room.getMultiple();
					room.setUserSocre(this.userId, room.getPersonNumber() * room.getMultiple());
				}else{
					gameInfo.getPlayerCardsInfos().get(diangangUser).setScore(gameInfo.getPlayerCardsInfos().get(diangangUser).getScore() -1 * room.getMultiple());
					this.score = this.score +  1 * room.getMultiple();
					room.setUserSocre(diangangUser, -1 * room.getMultiple());
					room.setUserSocre(this.userId, 1 * room.getMultiple());
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
		this.winType.addAll(huCardType.specialHuList);

    	if(this.roomInfo.getGameType().equals("JL") ||this.roomInfo.getGameType().equals("DS")){
    		if(isZimo){
        		if(room.getModeTotal().equals("2") && (room.getMode().equals("1")||room.getMode().equals("3"))){//平胡
        			for (Long i : gameInfo.getPlayerCardsInfos().keySet()){
        				gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - 2 * room.getMultiple());
        				room.setUserSocre(i, - 2 * room.getMultiple());
        			}
        			this.score = this.score + 2 * room.getPersonNumber() * room.getMultiple();
        			room.setUserSocre(this.userId, 2 * room.getPersonNumber() * room.getMultiple());
        			this.fan = 2;
//        			this.winType.add(HuType.hu_普通胡);
            	}else if(room.getModeTotal().equals("2") && (room.getMode().equals("2")||room.getMode().equals("4"))){//大胡
            		for (Long i : gameInfo.getPlayerCardsInfos().keySet()){
            			gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - room.getMultiple() * MahjongCode.HUTOSCOREFORJD.get(""+ CardUtil.huForScores(cards,huCardType)));
            			room.setUserSocre(i,  - room.getMultiple() * MahjongCode.HUTOSCOREFORJD.get(""+CardUtil.huForScores(cards,huCardType)));
            		}
            		this.score = this.score + room.getPersonNumber() * room.getMultiple() * MahjongCode.HUTOSCOREFORJD.get(""+CardUtil.huForScores(cards,huCardType)+"");
            		room.setUserSocre(this.userId, room.getPersonNumber() * room.getMultiple() * MahjongCode.HUTOSCOREFORJD.get(""+CardUtil.huForScores(cards,huCardType)+""));
            		this.fan = MahjongCode.HUTOSCOREFORJD.get(""+CardUtil.huForScores(cards,huCardType)+"");
//            		this.winType = CardUtil.huForWinType(cards);
            	}
        	}else{
        		if(room.getModeTotal().equals("2") && (room.getMode().equals("1")||room.getMode().equals("3"))){//平胡
        			gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - 3 * room.getMultiple());
        			this.score = this.score + 3 * room.getMultiple();
        			room.setUserSocre(dianpaoUser, - 3 * room.getMultiple());
        			room.setUserSocre(this.userId, 3 * room.getMultiple());
        			this.fan = 3;
//        			this.winType.add(HuType.hu_普通胡);
            	}else if(room.getModeTotal().equals("2") && (room.getMode().equals("2")||room.getMode().equals("4"))){//大胡
            		System.out.println(CardUtil.huForScores(cards,huCardType));
            		
            		if(CardUtil.huForScores(cards,huCardType)!=201){
            			gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - room.getMultiple() * MahjongCode.HUTOSCOREFORJD.get(""+CardUtil.huForScores(cards,huCardType)));
            			this.score = this.score + room.getMultiple() * MahjongCode.HUTOSCOREFORJD.get(""+CardUtil.huForScores(cards,huCardType));
                		room.setUserSocre(dianpaoUser, - room.getMultiple() * MahjongCode.HUTOSCOREFORJD.get(""+CardUtil.huForScores(cards,huCardType)));
            			room.setUserSocre(this.userId, room.getMultiple() * MahjongCode.HUTOSCOREFORJD.get(""+CardUtil.huForScores(cards,huCardType)));
            		}else{
            			gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - room.getMultiple() * 3);
            			this.score = this.score + room.getMultiple() * 3;
                		room.setUserSocre(dianpaoUser, - room.getMultiple() * 3);
            			room.setUserSocre(this.userId, room.getMultiple() * 3);
            		}
            		
            		this.fan = MahjongCode.HUTOSCOREFORJD.get(""+CardUtil.huForScores(cards,huCardType)+"");
//        			this.winType = CardUtil.huForWinType(cards);
            	}
        	}
    	}
    	else if(this.roomInfo.getGameType().equals("HT")){
    		if(isZimo){
        		if(room.getModeTotal().equals("2") && (room.getMode().equals("1")||room.getMode().equals("3"))){//平胡
        			for (Long i : gameInfo.getPlayerCardsInfos().keySet()){
        				gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - 2 * room.getMultiple());
        				room.setUserSocre(i, - 2 * room.getMultiple());
        			}
        			this.score = this.score +  2 * room.getPersonNumber() * room.getMultiple();
        			room.setUserSocre(this.userId, 2 * room.getPersonNumber() * room.getMultiple());
        			this.fan = 2;
//        			this.winType.add(HuType.hu_普通胡);
            	}else if(room.getModeTotal().equals("2") && (room.getMode().equals("2")||room.getMode().equals("4"))){//大胡
            		if(3==MahjongCode.HUTOSCOREFORHT.get(""+CardUtil.huForScores(cards,huCardType)+"")){
            			for (Long i : gameInfo.getPlayerCardsInfos().keySet()){
            				gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - 2 * room.getMultiple());
            				room.setUserSocre(i, - 2 * room.getMultiple());
            			}
            			this.score = this.score +  2 * room.getPersonNumber() * room.getMultiple();
            			room.setUserSocre(this.userId, 2 * room.getPersonNumber() * room.getMultiple());
            			this.fan = 2;
            		}else{
            			for (Long i : gameInfo.getPlayerCardsInfos().keySet()){
                			gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - room.getMultiple() * MahjongCode.HUTOSCOREFORHT.get(""+CardUtil.huForScores(cards,huCardType)));
                			room.setUserSocre(i,  - room.getMultiple() * MahjongCode.HUTOSCOREFORHT.get(""+CardUtil.huForScores(cards,huCardType)));
                		}
                		this.score = this.score + room.getPersonNumber() * room.getMultiple() * MahjongCode.HUTOSCOREFORHT.get(""+CardUtil.huForScores(cards,huCardType)+"");
                		room.setUserSocre(this.userId, room.getPersonNumber() * room.getMultiple() * MahjongCode.HUTOSCOREFORHT.get(""+CardUtil.huForScores(cards,huCardType)+""));
                		this.fan = MahjongCode.HUTOSCOREFORHT.get(""+CardUtil.huForScores(cards,huCardType)+"");
//                		this.winType = CardUtil.huForWinType(cards);
            		}
            	}
        	}
        	else{
        		if(room.getModeTotal().equals("2") && (room.getMode().equals("1")||room.getMode().equals("3"))){//平胡
        			gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - 3 * room.getMultiple());
        			this.score = this.score + 3 * room.getMultiple();
        			room.setUserSocre(dianpaoUser, - 3 * room.getMultiple());
        			room.setUserSocre(this.userId, 3 * room.getMultiple());
        			this.fan = 3;
//        			this.winType.add(HuType.hu_普通胡);
            	}else if(room.getModeTotal().equals("2") && (room.getMode().equals("2")||room.getMode().equals("4"))){//大胡
            		gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - room.getMultiple() * MahjongCode.HUTOSCOREFORHT.get(""+CardUtil.huForScores(cards,huCardType)));
            		this.score = this.score + room.getMultiple() * MahjongCode.HUTOSCOREFORHT.get(""+CardUtil.huForScores(cards,huCardType));
                	room.setUserSocre(dianpaoUser, - room.getMultiple() * MahjongCode.HUTOSCOREFORHT.get(""+CardUtil.huForScores(cards,huCardType)));
            		room.setUserSocre(this.userId, room.getMultiple() * MahjongCode.HUTOSCOREFORHT.get(""+CardUtil.huForScores(cards,huCardType)));
            		
            		this.fan = MahjongCode.HUTOSCOREFORHT.get(""+CardUtil.huForScores(cards,huCardType)+"");
//        			this.winType = CardUtil.huForWinType(cards);
            	}
        	}
    	}

		else if(this.roomInfo.getGameType().equals("HL")){
							if(isZimo){
								if(room.getModeTotal().equals("2") && (room.getMode().equals("1")||room.getMode().equals("3")||room.getMode().equals("11")||room.getMode().equals("13"))){//平胡
									for (Long i : gameInfo.getPlayerCardsInfos().keySet()){
										gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - 2 * room.getMultiple());
										room.setUserSocre(i, - 2 * room.getMultiple());
									}
									this.score = this.score +  2 * room.getPersonNumber() * room.getMultiple();
									room.setUserSocre(this.userId, 2 * room.getPersonNumber() * room.getMultiple());
									this.fan = 2;
//        			this.winType.add(HuType.hu_普通胡);
								}else if(room.getModeTotal().equals("2") && (room.getMode().equals("2")||room.getMode().equals("4")||room.getMode().equals("12")||room.getMode().equals("14"))){//大胡
									if(3==MahjongCode.HUTOSCOREFORLQ.get(""+CardUtil.huForScores(cards,huCardType)+"")){
										for (Long i : gameInfo.getPlayerCardsInfos().keySet()){
											gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - 2 * room.getMultiple());
											room.setUserSocre(i, - 2 * room.getMultiple());
										}
										this.score = this.score +  2 * room.getPersonNumber() * room.getMultiple();
										room.setUserSocre(this.userId, 2 * room.getPersonNumber() * room.getMultiple());
										this.fan = 2;
									}else{
										for (Long i : gameInfo.getPlayerCardsInfos().keySet()){
							gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - room.getMultiple() * MahjongCode.HUTOSCOREFORLQ.get(""+CardUtil.huForScores(cards,huCardType)));
							room.setUserSocre(i,  - room.getMultiple() * MahjongCode.HUTOSCOREFORLQ.get(""+CardUtil.huForScores(cards,huCardType)));
						}
						this.score = this.score + room.getPersonNumber() * room.getMultiple() * MahjongCode.HUTOSCOREFORLQ.get(""+CardUtil.huForScores(cards,huCardType)+"");
						room.setUserSocre(this.userId, room.getPersonNumber() * room.getMultiple() * MahjongCode.HUTOSCOREFORLQ.get(""+CardUtil.huForScores(cards,huCardType)+""));
						this.fan = MahjongCode.HUTOSCOREFORLQ.get(""+CardUtil.huForScores(cards,huCardType)+"");
//                		this.winType = CardUtil.huForWinType(cards);
					}
				}
			}
			else{
				if(room.getModeTotal().equals("2") && (room.getMode().equals("1")||room.getMode().equals("3"))){//平胡
					gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - 3 * room.getMultiple());
					this.score = this.score + 3 * room.getMultiple();
					room.setUserSocre(dianpaoUser, - 3 * room.getMultiple());
					room.setUserSocre(this.userId, 3 * room.getMultiple());
					this.fan = 3;
//        			this.winType.add(HuType.hu_普通胡);
				}else if(room.getModeTotal().equals("2") && (room.getMode().equals("2")||room.getMode().equals("4"))){//大胡
					gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - room.getMultiple() * MahjongCode.HUTOSCOREFORLQ.get(""+CardUtil.huForScores(cards,huCardType)));
					this.score = this.score + room.getMultiple() * MahjongCode.HUTOSCOREFORLQ.get(""+CardUtil.huForScores(cards,huCardType));
					room.setUserSocre(dianpaoUser, - room.getMultiple() * MahjongCode.HUTOSCOREFORLQ.get(""+CardUtil.huForScores(cards,huCardType)));
					room.setUserSocre(this.userId, room.getMultiple() * MahjongCode.HUTOSCOREFORLQ.get(""+CardUtil.huForScores(cards,huCardType)));

					this.fan = MahjongCode.HUTOSCOREFORLQ.get(""+CardUtil.huForScores(cards,huCardType)+"");
//        			this.winType = CardUtil.huForWinType(cards);
				}else if(room.getModeTotal().equals("2") && (room.getMode().equals("11")||room.getMode().equals("13"))){//平胡,不包胡
					for (Long l:gameInfo.getPlayerCardsInfos().keySet()) {
						gameInfo.getPlayerCardsInfos().get(l).setScore(gameInfo.getPlayerCardsInfos().get(l).getScore() - 1 * room.getMultiple());
						room.setUserSocre(l, -1 * room.getMultiple());
					}
					this.score = this.score + 4 * room.getMultiple();
					room.setUserSocre(this.userId, 4 * room.getMultiple());
					this.fan = 3;
//        			this.winType.add(HuType.hu_普通胡);
				}else if(room.getModeTotal().equals("2") && (room.getMode().equals("12")||room.getMode().equals("14"))){//大胡，不包胡
					for (Long l:gameInfo.getPlayerCardsInfos().keySet()) {
						gameInfo.getPlayerCardsInfos().get(l).setScore(gameInfo.getPlayerCardsInfos().get(l).getScore() - room.getMultiple() * MahjongCode.HUTOSCOREFORLQ.get(""+CardUtil.huForScores(cards,huCardType))/3);
						room.setUserSocre(l, - room.getMultiple() * MahjongCode.HUTOSCOREFORLQ.get(""+CardUtil.huForScores(cards,huCardType))/3);
					}
					this.score = this.score + room.getMultiple() * MahjongCode.HUTOSCOREFORLQ.get(""+CardUtil.huForScores(cards,huCardType))/3*4;
					room.setUserSocre(this.userId, room.getMultiple() * MahjongCode.HUTOSCOREFORLQ.get(""+CardUtil.huForScores(cards,huCardType))/3*4);

					this.fan = MahjongCode.HUTOSCOREFORLQ.get(""+CardUtil.huForScores(cards,huCardType)+"");
//        			this.winType = CardUtil.huForWinType(cards);
				}
			}
		}
		else if(this.roomInfo.getGameType().equals("TJW")){
			if(isZimo){
				if(room.getModeTotal().equals("2") && (room.getMode().equals("1")||room.getMode().equals("3")||room.getMode().equals("11")||room.getMode().equals("13"))){//平胡
					for (Long i : gameInfo.getPlayerCardsInfos().keySet()){
						gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - 2 * room.getMultiple());
						room.setUserSocre(i, - 2 * room.getMultiple());
					}
					this.score = this.score +  2 * room.getPersonNumber() * room.getMultiple();
					room.setUserSocre(this.userId, 2 * room.getPersonNumber() * room.getMultiple());
					this.fan = 2;
//        			this.winType.add(HuType.hu_普通胡);
				}else if(room.getModeTotal().equals("2") && (room.getMode().equals("2")||room.getMode().equals("4")||room.getMode().equals("12")||room.getMode().equals("14"))){//大胡
					if(3==MahjongCode.HUTOSCOREFORLQ.get(""+CardUtil.huForScores(cards,huCardType)+"")){
						for (Long i : gameInfo.getPlayerCardsInfos().keySet()){
							gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - 2 * room.getMultiple());
							room.setUserSocre(i, - 2 * room.getMultiple());
						}
						this.score = this.score +  2 * room.getPersonNumber() * room.getMultiple();
						room.setUserSocre(this.userId, 2 * room.getPersonNumber() * room.getMultiple());
						this.fan = 2;
					}else{
						for (Long i : gameInfo.getPlayerCardsInfos().keySet()){
							gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - room.getMultiple() * MahjongCode.HUTOSCOREFORLQ.get(""+CardUtil.huForScores(cards,huCardType)));
							room.setUserSocre(i,  - room.getMultiple() * MahjongCode.HUTOSCOREFORLQ.get(""+CardUtil.huForScores(cards,huCardType)));
						}
						this.score = this.score + room.getPersonNumber() * room.getMultiple() * MahjongCode.HUTOSCOREFORLQ.get(""+CardUtil.huForScores(cards,huCardType)+"");
						room.setUserSocre(this.userId, room.getPersonNumber() * room.getMultiple() * MahjongCode.HUTOSCOREFORLQ.get(""+CardUtil.huForScores(cards,huCardType)+""));
						this.fan = MahjongCode.HUTOSCOREFORLQ.get(""+CardUtil.huForScores(cards,huCardType)+"");
//                		this.winType = CardUtil.huForWinType(cards);
					}
				}
			}
			else{
				if(room.getModeTotal().equals("2") && (room.getMode().equals("1")||room.getMode().equals("3")||room.getMode().equals("11")||room.getMode().equals("13"))){//平胡
					gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - 1 * room.getMultiple());
					this.score = this.score + 1 * room.getMultiple();
					room.setUserSocre(dianpaoUser, - 1 * room.getMultiple());
					room.setUserSocre(this.userId, 1 * room.getMultiple());
					this.fan = 1;
//        			this.winType.add(HuType.hu_普通胡);
				}else if(room.getModeTotal().equals("2") && (room.getMode().equals("2")||room.getMode().equals("4")||room.getMode().equals("12")||room.getMode().equals("14"))){//大胡
					gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - room.getMultiple() * MahjongCode.HUTOSCOREFORLQ.get(""+CardUtil.huForScores(cards,huCardType))/3);
					this.score = this.score + room.getMultiple() * MahjongCode.HUTOSCOREFORLQ.get(""+CardUtil.huForScores(cards,huCardType))/3;
					room.setUserSocre(dianpaoUser, - room.getMultiple() * MahjongCode.HUTOSCOREFORLQ.get(""+CardUtil.huForScores(cards,huCardType))/3);
					room.setUserSocre(this.userId, room.getMultiple() * MahjongCode.HUTOSCOREFORLQ.get(""+CardUtil.huForScores(cards,huCardType))/3);

					this.fan = MahjongCode.HUTOSCOREFORLQ.get(""+CardUtil.huForScores(cards,huCardType)+"")/3;
//        			this.winType = CardUtil.huForWinType(cards);
				}
			}
		}
		else if(this.roomInfo.getGameType().equals("TC") || this.roomInfo.getGameType().equals("TC1")){
			if(isZimo){
				if(room.getModeTotal().equals("2") && (room.getMode().equals("1")||room.getMode().equals("3")||room.getMode().equals("11")||room.getMode().equals("13"))){//平胡
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
				}else if(room.getModeTotal().equals("2") && (room.getMode().equals("2")||room.getMode().equals("4")||room.getMode().equals("12")||room.getMode().equals("14"))){//大胡
					if(3==MahjongCode.HUTOSCOREFORLQ.get(""+CardUtil.huForScores(cards,huCardType)+"")){
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
					}else{
						if (this.userId == this.gameInfo.firstTurn) {//庄赢
							for (Long i : gameInfo.getPlayerCardsInfos().keySet()) {
								gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - MahjongCode.HUTOSCOREFORLQ.get(""+CardUtil.huForScores(cards,huCardType)) * room.getMultiple()*2);
								room.setUserSocre(i, -MahjongCode.HUTOSCOREFORLQ.get(""+CardUtil.huForScores(cards,huCardType)) * room.getMultiple()*2);
							}
							this.score = this.score + MahjongCode.HUTOSCOREFORLQ.get(""+CardUtil.huForScores(cards,huCardType)) * room.getPersonNumber() * room.getMultiple()*2;
							room.setUserSocre(this.userId, MahjongCode.HUTOSCOREFORLQ.get(""+CardUtil.huForScores(cards,huCardType)) * room.getPersonNumber() * room.getMultiple()*2);
							this.fan = MahjongCode.HUTOSCOREFORLQ.get(""+CardUtil.huForScores(cards,huCardType));
						} else {
							for (Long i : gameInfo.getPlayerCardsInfos().keySet()) {
								gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - MahjongCode.HUTOSCOREFORLQ.get(""+CardUtil.huForScores(cards,huCardType)) * room.getMultiple());
								room.setUserSocre(i, -MahjongCode.HUTOSCOREFORLQ.get(""+CardUtil.huForScores(cards,huCardType)) * room.getMultiple());
							}
							//庄输2倍，在此多减一倍
							gameInfo.getPlayerCardsInfos().get(this.gameInfo.firstTurn).setScore(gameInfo.getPlayerCardsInfos().get(this.gameInfo.firstTurn).getScore() - MahjongCode.HUTOSCOREFORLQ.get(""+CardUtil.huForScores(cards,huCardType)) * room.getMultiple());
							room.setUserSocre(this.gameInfo.firstTurn, -MahjongCode.HUTOSCOREFORLQ.get(""+CardUtil.huForScores(cards,huCardType)) * room.getMultiple());

							this.score = this.score + (room.getPersonNumber() + 1) * MahjongCode.HUTOSCOREFORLQ.get(""+CardUtil.huForScores(cards,huCardType)) * room.getMultiple();
							room.setUserSocre(this.userId, (room.getPersonNumber() + 1) * MahjongCode.HUTOSCOREFORLQ.get(""+CardUtil.huForScores(cards,huCardType)) * room.getMultiple());
							this.fan = MahjongCode.HUTOSCOREFORLQ.get(""+CardUtil.huForScores(cards,huCardType));
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

			}
			else{
				if(room.getModeTotal().equals("2") && (room.getMode().equals("1")||room.getMode().equals("3")||room.getMode().equals("11")||room.getMode().equals("13"))){//平胡
					if (this.userId == this.gameInfo.firstTurn) {//庄赢
						gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - ((room.getPersonNumber() - 1)) * room.getMultiple()*2);
						this.score = this.score + ((room.getPersonNumber() - 1)) * room.getMultiple() *2;
						room.setUserSocre(dianpaoUser, -((room.getPersonNumber() - 1)) * room.getMultiple() *2);
						room.setUserSocre(this.userId, ((room.getPersonNumber() - 1)) * room.getMultiple() *2);
						this.fan = 1;
					} else {
						if(dianpaoUser==this.gameInfo.firstTurn){//庄点炮
							gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - ((room.getPersonNumber() - 1)) * room.getMultiple()*2);
							this.score = this.score + ((room.getPersonNumber() - 1)) * room.getMultiple()*2;
							room.setUserSocre(dianpaoUser, -((room.getPersonNumber() - 1)) * room.getMultiple()*2);
							room.setUserSocre(this.userId, ((room.getPersonNumber() - 1)) * room.getMultiple()*2);
							this.fan = 1;
						}else{
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
				}else if(room.getModeTotal().equals("2") && (room.getMode().equals("2")||room.getMode().equals("4")||room.getMode().equals("12")||room.getMode().equals("14"))){//大胡
					if (this.userId == this.gameInfo.firstTurn) {//庄赢
						gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - ((room.getPersonNumber() - 1) ) * room.getMultiple() * MahjongCode.HUTOSCOREFORLQ.get(""+CardUtil.huForScores(cards,huCardType))*2/3);
						this.score = this.score + ((room.getPersonNumber() - 1)) * room.getMultiple() * MahjongCode.HUTOSCOREFORLQ.get(""+CardUtil.huForScores(cards,huCardType))*2/3;
						room.setUserSocre(dianpaoUser, -((room.getPersonNumber() - 1)) * room.getMultiple() * MahjongCode.HUTOSCOREFORLQ.get(""+CardUtil.huForScores(cards,huCardType))*2/3);
						room.setUserSocre(this.userId, ((room.getPersonNumber() - 1)) * room.getMultiple() * MahjongCode.HUTOSCOREFORLQ.get(""+CardUtil.huForScores(cards,huCardType))*2/3);
						this.fan = MahjongCode.HUTOSCOREFORLQ.get(""+CardUtil.huForScores(cards,huCardType))*2/3;
					} else {
						if(dianpaoUser==this.gameInfo.firstTurn){//庄点炮
							gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - ((room.getPersonNumber() - 1)) * room.getMultiple()* MahjongCode.HUTOSCOREFORLQ.get(""+CardUtil.huForScores(cards,huCardType))*2/3);
							this.score = this.score + ((room.getPersonNumber() - 1)) * room.getMultiple()* MahjongCode.HUTOSCOREFORLQ.get(""+CardUtil.huForScores(cards,huCardType))*2/3;
							room.setUserSocre(dianpaoUser, -((room.getPersonNumber() - 1)) * room.getMultiple()* MahjongCode.HUTOSCOREFORLQ.get(""+CardUtil.huForScores(cards,huCardType))*2/3);
							room.setUserSocre(this.userId, ((room.getPersonNumber() - 1)) * room.getMultiple()* MahjongCode.HUTOSCOREFORLQ.get(""+CardUtil.huForScores(cards,huCardType))*2/3);
							this.fan = 1;
						}else{
							gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - ((room.getPersonNumber() - 1)) * room.getMultiple()* MahjongCode.HUTOSCOREFORLQ.get(""+CardUtil.huForScores(cards,huCardType))/3);
							this.score = this.score + ((room.getPersonNumber() - 1)) * room.getMultiple()* MahjongCode.HUTOSCOREFORLQ.get(""+CardUtil.huForScores(cards,huCardType))/3;
							room.setUserSocre(dianpaoUser, -((room.getPersonNumber() - 1)) * room.getMultiple()* MahjongCode.HUTOSCOREFORLQ.get(""+CardUtil.huForScores(cards,huCardType))/3);
							room.setUserSocre(this.userId, ((room.getPersonNumber() - 1)) * room.getMultiple()* MahjongCode.HUTOSCOREFORLQ.get(""+CardUtil.huForScores(cards,huCardType))/3);
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
				}
			}
		}

		else if(this.roomInfo.getGameType().equals("LQ")){
			if(isZimo){
				if(room.getModeTotal().equals("2") && (room.getMode().equals("1")||room.getMode().equals("3")||room.getMode().equals("11")||room.getMode().equals("13"))){//平胡
					for (Long i : gameInfo.getPlayerCardsInfos().keySet()){
						gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - 2 * room.getMultiple());
						room.setUserSocre(i, - 2 * room.getMultiple());
					}
					this.score = this.score + 2 * room.getPersonNumber() * room.getMultiple();
					room.setUserSocre(this.userId, 2 * room.getPersonNumber() * room.getMultiple());
					this.fan = 2;
//        			this.winType.add(HuType.hu_普通胡);
				}else if(room.getModeTotal().equals("2") && (room.getMode().equals("2")||room.getMode().equals("4")||room.getMode().equals("12")||room.getMode().equals("14"))){//大胡
					for (Long i : gameInfo.getPlayerCardsInfos().keySet()){
						gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - room.getMultiple() * MahjongCode.HUTOSCOREFORLQ2.get(""+CardUtil.huForScores(cards,huCardType))/3);
						room.setUserSocre(i,  - room.getMultiple() * MahjongCode.HUTOSCOREFORLQ2.get(""+CardUtil.huForScores(cards,huCardType))/3);
						int a =MahjongCode.HUTOSCOREFORLQ2.get(""+CardUtil.huForScores(cards,huCardType))/3;
						int b =CardUtil.huForScores(cards,huCardType);
					}
					this.score = this.score + room.getPersonNumber() * room.getMultiple() * MahjongCode.HUTOSCOREFORLQ2.get(""+CardUtil.huForScores(cards,huCardType)+"")/3;
					room.setUserSocre(this.userId, room.getPersonNumber() * room.getMultiple() * MahjongCode.HUTOSCOREFORLQ2.get(""+CardUtil.huForScores(cards,huCardType)+"")/3);
					this.fan = MahjongCode.HUTOSCOREFORLQ2.get(""+CardUtil.huForScores(cards,huCardType)+"");
//            		this.winType = CardUtil.huForWinType(cards);
				}
			}else{
				if(room.getModeTotal().equals("2") && (room.getMode().equals("1")||room.getMode().equals("3")||room.getMode().equals("11")||room.getMode().equals("13"))){//平胡
					if(gameInfo.getPlayerCardsInfos().get(dianpaoUser).isTing){
						for (Long i : gameInfo.getPlayerCardsInfos().keySet()){
							gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - 1 * room.getMultiple());
							room.setUserSocre(i, - 1 * room.getMultiple());
						}
						this.score = this.score + this.roomInfo.getPersonNumber() *  room.getMultiple();
						room.setUserSocre(this.userId, this.roomInfo.getPersonNumber()  * room.getMultiple());
						this.fan = this.roomInfo.getPersonNumber() - 1;
					}else{
						int n = this.roomInfo.getPersonNumber() - 1;
						gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - n * room.getMultiple());
						this.score = this.score + n * room.getMultiple();
						room.setUserSocre(dianpaoUser, - n * room.getMultiple());
						room.setUserSocre(this.userId, n * room.getMultiple());
						this.fan = n;
					}

				}else if(room.getModeTotal().equals("2") && (room.getMode().equals("2")||room.getMode().equals("4")||room.getMode().equals("12")||room.getMode().equals("14"))){//大胡
					if(gameInfo.getPlayerCardsInfos().get(dianpaoUser).isTing){
						for (Long i : gameInfo.getPlayerCardsInfos().keySet()){
							gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - room.getMultiple() * MahjongCode.HUTOSCORE4LQ2.get(""+CardUtil.huForScores(cards,huCardType))/3);
							room.setUserSocre(i, - room.getMultiple() * MahjongCode.HUTOSCORE4LQ2.get(""+CardUtil.huForScores(cards,huCardType))/3);
						}
						this.score = this.score + this.roomInfo.getPersonNumber() * room.getMultiple() * MahjongCode.HUTOSCORE4LQ2.get(""+CardUtil.huForScores(cards,huCardType))/3;
						room.setUserSocre(this.userId, this.roomInfo.getPersonNumber()  * room.getMultiple() * MahjongCode.HUTOSCORE4LQ2.get(""+CardUtil.huForScores(cards,huCardType))/3);
					}else{
						String s  = ""+CardUtil.huForScores(cards,huCardType);
						System.out.println(s);
						gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - room.getMultiple() * MahjongCode.HUTOSCORE4LQ2.get(""+CardUtil.huForScores(cards,huCardType)));
						this.score = this.score + room.getMultiple() * MahjongCode.HUTOSCORE4LQ2.get(""+CardUtil.huForScores(cards,huCardType));
						room.setUserSocre(dianpaoUser, - room.getMultiple() * MahjongCode.HUTOSCORE4LQ2.get(""+CardUtil.huForScores(cards,huCardType)));
						room.setUserSocre(this.userId, room.getMultiple() * MahjongCode.HUTOSCORE4LQ2.get(""+CardUtil.huForScores(cards,huCardType)));
					}
					this.fan = MahjongCode.HUTOSCORE4LQ2.get(""+CardUtil.huForScores(cards,huCardType)+"");
				}


			}
		}
		else if(this.roomInfo.getGameType().equals("BAIXING")){
			if(isZimo){
				if(room.getModeTotal().equals("2") && (room.getMode().equals("1")||room.getMode().equals("3")||room.getMode().equals("11")||room.getMode().equals("13"))){//平胡
					for (Long i : gameInfo.getPlayerCardsInfos().keySet()){
						gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - 10 * room.getMultiple());
						room.setUserSocre(i, - 10 * room.getMultiple());
					}
					this.score = this.score + 10 * room.getPersonNumber() * room.getMultiple();
					room.setUserSocre(this.userId, 10 * room.getPersonNumber() * room.getMultiple());
					this.fan = 10;
//        			this.winType.add(HuType.hu_普通胡);
				}else if(room.getModeTotal().equals("2") && (room.getMode().equals("2")||room.getMode().equals("4")||room.getMode().equals("12")||room.getMode().equals("14"))){//大胡
					for (Long i : gameInfo.getPlayerCardsInfos().keySet()){
						gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - room.getMultiple() * MahjongCode.HUTOSCOREFORLQ2.get(""+CardUtil.huForScores(cards,huCardType))/3);
						room.setUserSocre(i,  - room.getMultiple() * MahjongCode.HUTOSCOREFORLQ2.get(""+CardUtil.huForScores(cards,huCardType))/3);
						int a =MahjongCode.HUTOSCOREFORLQ2.get(""+CardUtil.huForScores(cards,huCardType))/3;
						int b =CardUtil.huForScores(cards,huCardType);
					}
					this.score = this.score + room.getPersonNumber() * room.getMultiple() * MahjongCode.HUTOSCOREFORLQ2.get(""+CardUtil.huForScores(cards,huCardType)+"")/3;
					room.setUserSocre(this.userId, room.getPersonNumber() * room.getMultiple() * MahjongCode.HUTOSCOREFORLQ2.get(""+CardUtil.huForScores(cards,huCardType)+"")/3);
					this.fan = MahjongCode.HUTOSCOREFORLQ2.get(""+CardUtil.huForScores(cards,huCardType)+"");
//            		this.winType = CardUtil.huForWinType(cards);
				}
			}else{
				if(room.getModeTotal().equals("2") && (room.getMode().equals("1")||room.getMode().equals("3")||room.getMode().equals("11")||room.getMode().equals("13"))){//平胡
					gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - 10 * room.getMultiple());
					this.score = this.score + 10 * room.getMultiple();
					room.setUserSocre(dianpaoUser, - 10 * room.getMultiple());
					room.setUserSocre(this.userId, 10 * room.getMultiple());
					this.fan = 10;
				}else if(room.getModeTotal().equals("2") && (room.getMode().equals("2")||room.getMode().equals("4")||room.getMode().equals("12")||room.getMode().equals("14"))){//大胡
					if(gameInfo.getPlayerCardsInfos().get(dianpaoUser).isTing){
						for (Long i : gameInfo.getPlayerCardsInfos().keySet()){
							gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - room.getMultiple() * MahjongCode.HUTOSCORE4LQ2.get(""+CardUtil.huForScores(cards,huCardType))/3);
							room.setUserSocre(i, - room.getMultiple() * MahjongCode.HUTOSCORE4LQ2.get(""+CardUtil.huForScores(cards,huCardType))/3);
						}
						this.score = this.score + 4 * room.getMultiple() * MahjongCode.HUTOSCORE4LQ2.get(""+CardUtil.huForScores(cards,huCardType))/3;
						room.setUserSocre(this.userId, 4  * room.getMultiple() * MahjongCode.HUTOSCORE4LQ2.get(""+CardUtil.huForScores(cards,huCardType))/3);
					}else{
						gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - room.getMultiple() * MahjongCode.HUTOSCORE4LQ2.get(""+CardUtil.huForScores(cards,huCardType)));
						this.score = this.score + room.getMultiple() * MahjongCode.HUTOSCORE4LQ2.get(""+CardUtil.huForScores(cards,huCardType));
						room.setUserSocre(dianpaoUser, - room.getMultiple() * MahjongCode.HUTOSCORE4LQ2.get(""+CardUtil.huForScores(cards,huCardType)));
						room.setUserSocre(this.userId, room.getMultiple() * MahjongCode.HUTOSCORE4LQ2.get(""+CardUtil.huForScores(cards,huCardType)));
					}
					this.fan = MahjongCode.HUTOSCORE.get(""+CardUtil.huForScores(cards,huCardType)+"");
				}
			}
		}
		else if(this.roomInfo.getGameType().equals("XXPB")){
			if(isZimo){
				if(room.getModeTotal().equals("2") && (room.getMode().equals("1")||room.getMode().equals("3")||room.getMode().equals("11")||room.getMode().equals("13"))){//平胡
					for (Long i : gameInfo.getPlayerCardsInfos().keySet()){
						gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - 5 * room.getMultiple());
						room.setUserSocre(i, - 5 * room.getMultiple());
					}
					this.score = this.score + 5 * room.getPersonNumber() * room.getMultiple();
					room.setUserSocre(this.userId, 5 * room.getPersonNumber() * room.getMultiple());
					this.fan = 5;
//        			this.winType.add(HuType.hu_普通胡);
				}else if(room.getModeTotal().equals("2") && (room.getMode().equals("2")||room.getMode().equals("4")||room.getMode().equals("12")||room.getMode().equals("14"))){//大胡
					for (Long i : gameInfo.getPlayerCardsInfos().keySet()){
						gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - room.getMultiple() * MahjongCode.HUTOSCOREFORXXPB.get(""+CardUtil.huForScores(cards,huCardType))/3);
						room.setUserSocre(i,  - room.getMultiple() * MahjongCode.HUTOSCOREFORXXPB.get(""+CardUtil.huForScores(cards,huCardType))/3);
						int a =MahjongCode.HUTOSCOREFORXXPB.get(""+CardUtil.huForScores(cards,huCardType))/3;
						int b =CardUtil.huForScores(cards,huCardType);
					}
					this.score = this.score + room.getPersonNumber() * room.getMultiple() * MahjongCode.HUTOSCOREFORXXPB.get(""+CardUtil.huForScores(cards,huCardType)+"")/3;
					room.setUserSocre(this.userId, room.getPersonNumber() * room.getMultiple() * MahjongCode.HUTOSCOREFORXXPB.get(""+CardUtil.huForScores(cards,huCardType)+"")/3);
					this.fan = MahjongCode.HUTOSCOREFORXXPB.get(""+CardUtil.huForScores(cards,huCardType)+"");
//            		this.winType = CardUtil.huForWinType(cards);
				}
			}else{
				if(room.getModeTotal().equals("2") && (room.getMode().equals("1")||room.getMode().equals("3")||room.getMode().equals("11")||room.getMode().equals("13"))){//平胡
					gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - 5 * room.getMultiple());
					this.score = this.score + 5 * room.getMultiple();
					room.setUserSocre(dianpaoUser, - 5 * room.getMultiple());
					room.setUserSocre(this.userId, 5 * room.getMultiple());
					this.fan = 5;
				}else if(room.getModeTotal().equals("2") && (room.getMode().equals("2")||room.getMode().equals("4")||room.getMode().equals("12")||room.getMode().equals("14"))){//大胡
					if(gameInfo.getPlayerCardsInfos().get(dianpaoUser).isTing){
						for (Long i : gameInfo.getPlayerCardsInfos().keySet()){
							gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - room.getMultiple() * MahjongCode.HUTOSCOREFORXXPB.get(""+CardUtil.huForScores(cards,huCardType))/3);
							room.setUserSocre(i, - room.getMultiple() * MahjongCode.HUTOSCOREFORXXPB.get(""+CardUtil.huForScores(cards,huCardType))/3);
						}
						this.score = this.score + 4 * room.getMultiple() * MahjongCode.HUTOSCOREFORXXPB.get(""+CardUtil.huForScores(cards,huCardType))/3;
						room.setUserSocre(this.userId, 4  * room.getMultiple() * MahjongCode.HUTOSCOREFORXXPB.get(""+CardUtil.huForScores(cards,huCardType))/3);
					}else{
						gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - room.getMultiple() * MahjongCode.HUTOSCOREFORXXPB.get(""+CardUtil.huForScores(cards,huCardType)));
						this.score = this.score + room.getMultiple() * MahjongCode.HUTOSCOREFORXXPB.get(""+CardUtil.huForScores(cards,huCardType));
						room.setUserSocre(dianpaoUser, - room.getMultiple() * MahjongCode.HUTOSCOREFORXXPB.get(""+CardUtil.huForScores(cards,huCardType)));
						room.setUserSocre(this.userId, room.getMultiple() * MahjongCode.HUTOSCOREFORXXPB.get(""+CardUtil.huForScores(cards,huCardType)));
					}
					this.fan = MahjongCode.HUTOSCORE.get(""+CardUtil.huForScores(cards,huCardType)+"");
				}
			}
		}
		else if(this.roomInfo.getGameType().equals("QUANMIN")){
			if(isZimo){
				if(room.getModeTotal().equals("2") && (room.getMode().equals("1")||room.getMode().equals("3")||room.getMode().equals("11")||room.getMode().equals("13"))){//平胡
					for (Long i : gameInfo.getPlayerCardsInfos().keySet()){
						gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - 2 * room.getMultiple());
						room.setUserSocre(i, - 2 * room.getMultiple());
					}
					this.score = this.score + 2 * room.getPersonNumber() * room.getMultiple();
					room.setUserSocre(this.userId, 2 * room.getPersonNumber() * room.getMultiple());
					this.fan = 2;
//        			this.winType.add(HuType.hu_普通胡);
				}else if(room.getModeTotal().equals("2") && (room.getMode().equals("2")||room.getMode().equals("4")||room.getMode().equals("12")||room.getMode().equals("14"))){//大胡
					for (Long i : gameInfo.getPlayerCardsInfos().keySet()){
						gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - room.getMultiple() * MahjongCode.HUTOSCOREFORLQ2.get(""+CardUtil.huForScores(cards,huCardType))/3);
						room.setUserSocre(i,  - room.getMultiple() * MahjongCode.HUTOSCOREFORLQ2.get(""+CardUtil.huForScores(cards,huCardType))/3);
						int a =MahjongCode.HUTOSCOREFORLQ2.get(""+CardUtil.huForScores(cards,huCardType))/3;
						int b =CardUtil.huForScores(cards,huCardType);
					}
					this.score = this.score + room.getPersonNumber() * room.getMultiple() * MahjongCode.HUTOSCOREFORLQ2.get(""+CardUtil.huForScores(cards,huCardType)+"")/3;
					room.setUserSocre(this.userId, room.getPersonNumber() * room.getMultiple() * MahjongCode.HUTOSCOREFORLQ2.get(""+CardUtil.huForScores(cards,huCardType)+"")/3);
					this.fan = MahjongCode.HUTOSCOREFORLQ2.get(""+CardUtil.huForScores(cards,huCardType)+"");
//            		this.winType = CardUtil.huForWinType(cards);
				}
			}else{
				if(room.getModeTotal().equals("2") && (room.getMode().equals("1")||room.getMode().equals("3")||room.getMode().equals("11")||room.getMode().equals("13"))){//平胡
					if(gameInfo.getPlayerCardsInfos().get(dianpaoUser).isTing){
						for (Long i : gameInfo.getPlayerCardsInfos().keySet()){
							gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - 1 * room.getMultiple());
							room.setUserSocre(i, - 1 * room.getMultiple());
						}
						this.score = this.score + 4 *  room.getMultiple();
						room.setUserSocre(this.userId, 4  * room.getMultiple());
						this.fan = 3;
					}else{
						gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - 3 * room.getMultiple());
						this.score = this.score + 3 * room.getMultiple();
						room.setUserSocre(dianpaoUser, - 3 * room.getMultiple());
						room.setUserSocre(this.userId, 3 * room.getMultiple());
						this.fan = 3;
					}

				}else if(room.getModeTotal().equals("2") && (room.getMode().equals("2")||room.getMode().equals("4")||room.getMode().equals("12")||room.getMode().equals("14"))){//大胡
					if(gameInfo.getPlayerCardsInfos().get(dianpaoUser).isTing){
						for (Long i : gameInfo.getPlayerCardsInfos().keySet()){
							gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - room.getMultiple() * MahjongCode.HUTOSCORE4QUANMIN.get(""+CardUtil.huForScores(cards,huCardType))/9);
							room.setUserSocre(i, - room.getMultiple() * MahjongCode.HUTOSCORE4QUANMIN.get(""+CardUtil.huForScores(cards,huCardType))/9);
						}
						this.score = this.score + 4 * room.getMultiple() * MahjongCode.HUTOSCORE4QUANMIN.get(""+CardUtil.huForScores(cards,huCardType))/9;
						room.setUserSocre(this.userId, 4  * room.getMultiple() * MahjongCode.HUTOSCORE4QUANMIN.get(""+CardUtil.huForScores(cards,huCardType))/9);
					}else{
						gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - room.getMultiple() * MahjongCode.HUTOSCORE4QUANMIN.get(""+CardUtil.huForScores(cards,huCardType))/3);
						this.score = this.score + room.getMultiple() * MahjongCode.HUTOSCORE4QUANMIN.get(""+CardUtil.huForScores(cards,huCardType))/3;
						room.setUserSocre(dianpaoUser, - room.getMultiple() * MahjongCode.HUTOSCORE4QUANMIN.get(""+CardUtil.huForScores(cards,huCardType))/3);
						room.setUserSocre(this.userId, room.getMultiple() * MahjongCode.HUTOSCORE4QUANMIN.get(""+CardUtil.huForScores(cards,huCardType))/3);
					}
					this.fan = MahjongCode.HUTOSCORE.get(""+CardUtil.huForScores(cards,huCardType)+"");
				}


			}
		}

    	else{
    		if(isZimo){
        		if(room.getModeTotal().equals("2") && (room.getMode().equals("1")||room.getMode().equals("3")||room.getMode().equals("11")||room.getMode().equals("13"))){//平胡
        			for (Long i : gameInfo.getPlayerCardsInfos().keySet()){
        				gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - 2 * room.getMultiple());
        				room.setUserSocre(i, - 2 * room.getMultiple());
        			}
        			this.score = this.score + 2 * room.getPersonNumber() * room.getMultiple();
        			room.setUserSocre(this.userId, 2 * room.getPersonNumber() * room.getMultiple());
        			this.fan = 2;
//        			this.winType.add(HuType.hu_普通胡);
            	}else if(room.getModeTotal().equals("2") && (room.getMode().equals("2")||room.getMode().equals("4")||room.getMode().equals("12")||room.getMode().equals("14"))){//大胡
            		for (Long i : gameInfo.getPlayerCardsInfos().keySet()){
            			gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - 2 * room.getMultiple() * MahjongCode.HUTOSCORE.get(""+CardUtil.huForScores(cards,huCardType))/3);
            			room.setUserSocre(i,  - 2 * room.getMultiple() * MahjongCode.HUTOSCORE.get(""+CardUtil.huForScores(cards,huCardType))/3);
            		}
            		this.score = this.score + 2 * room.getPersonNumber() * room.getMultiple() * MahjongCode.HUTOSCORE.get(""+CardUtil.huForScores(cards,huCardType)+"")/3;
            		room.setUserSocre(this.userId, 2 * room.getPersonNumber() * room.getMultiple() * MahjongCode.HUTOSCORE.get(""+CardUtil.huForScores(cards,huCardType)+"")/3);
            		this.fan = 2 * MahjongCode.HUTOSCORE.get(""+CardUtil.huForScores(cards,huCardType)+"");
//            		this.winType = CardUtil.huForWinType(cards);
            	}
        	}else{
        		if(room.getModeTotal().equals("2") && (room.getMode().equals("1")||room.getMode().equals("3")||room.getMode().equals("11")||room.getMode().equals("13"))){//平胡
        			gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - (this.roomInfo.getPersonNumber() - 1) * room.getMultiple());
        			this.score = this.score + (this.roomInfo.getPersonNumber() - 1) * room.getMultiple();
        			room.setUserSocre(dianpaoUser, - (this.roomInfo.getPersonNumber() - 1) * room.getMultiple());
        			room.setUserSocre(this.userId, (this.roomInfo.getPersonNumber() - 1) * room.getMultiple());
        			this.fan = this.roomInfo.getPersonNumber() - 1;
//        			this.winType.add(HuType.hu_普通胡);
            	}else if(room.getModeTotal().equals("2") && (room.getMode().equals("2")||room.getMode().equals("4")||room.getMode().equals("12")||room.getMode().equals("14"))){//大胡
            		System.out.println(CardUtil.huForScores(cards,huCardType));
            		gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - room.getMultiple() * MahjongCode.HUTOSCORE.get(""+CardUtil.huForScores(cards,huCardType)));
        			this.score = this.score + room.getMultiple() * MahjongCode.HUTOSCORE.get(""+CardUtil.huForScores(cards,huCardType));
            		room.setUserSocre(dianpaoUser, - room.getMultiple() * MahjongCode.HUTOSCORE.get(""+CardUtil.huForScores(cards,huCardType)));
        			room.setUserSocre(this.userId, room.getMultiple() * MahjongCode.HUTOSCORE.get(""+CardUtil.huForScores(cards,huCardType)));
        			this.fan = MahjongCode.HUTOSCORE.get(""+CardUtil.huForScores(cards,huCardType)+"");
//        			this.winType = CardUtil.huForWinType(cards);
            	}
        	}
    	}


    	if(!this.roomInfo.getGameType().equals("LQ") && !this.roomInfo.getGameType().equals("BAIXING") && !this.roomInfo.getGameType().equals("QUANMIN") && !this.roomInfo.getGameType().equals("HT") && !this.roomInfo.getGameType().equals("JL") && !this.roomInfo.getGameType().equals("DS") && !this.roomInfo.getGameType().equals("HL") && !this.roomInfo.getGameType().equals("TC") && !this.roomInfo.getGameType().equals("TC1")&& !this.roomInfo.getGameType().equals("XXPB")){
    		if(this.anGangType.size()>0){
    			for (Long i : gameInfo.getPlayerCardsInfos().keySet()){
            		gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - 2 * this.anGangType.size() * room.getMultiple());
            		room.setUserSocre(i, - 2 * this.anGangType.size()* room.getMultiple());
            	}
    			this.score = this.score + 2 * this.anGangType.size() * room.getPersonNumber() * room.getMultiple();
    	    	room.setUserSocre(this.userId, 2 * this.anGangType.size() * room.getPersonNumber() * room.getMultiple());
    		}
    		if(this.mingGangType.size()>0){
    			for (Integer userId : this.mingGangType.keySet()) {
    				if(this.mingGangType.get(userId)==-1){
    					for (Long i : gameInfo.getPlayerCardsInfos().keySet()){
    	            		gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - room.getMultiple());
    	            		room.setUserSocre(i, - room.getMultiple());
    	            	}
    	            	this.score = this.score + room.getPersonNumber() * room.getMultiple();
    	            	room.setUserSocre(this.userId, room.getPersonNumber() * room.getMultiple());
    				}else{
    					gameInfo.getPlayerCardsInfos().get(this.mingGangType.get(userId)).setScore(gameInfo.getPlayerCardsInfos().get(this.mingGangType.get(userId)).getScore() -  (room.getPersonNumber()-1) * room.getMultiple());
    	        		this.score = this.score +  (room.getPersonNumber()-1) * room.getMultiple();
    	        		room.setUserSocre(this.mingGangType.get(userId), -  (room.getPersonNumber()-1) * room.getMultiple());
    	        		room.setUserSocre(this.userId, (room.getPersonNumber()-1) * room.getMultiple());
    				}
    			}
    		}
    	}
    }

	@Override
	public boolean isCanGangAddThisCard(String card) {
		//听之后 杠后的牌还能听
		if (isTing && super.isCanGangAddThisCard(card)) {
			List<String> temp = getCardsAddThisCard(card);
			//去掉 这张杠牌
			int ct = CardTypeUtil.cardType.get(card);
			return isCanTingAfterGang(temp, ct,true);

		} else return super.isCanGangAddThisCard(card);

	}

	/**
	 * 杠之后是否能听
	 * @param cards
	 * @param cardType
	 * @return
	 */
	protected boolean isCanTingAfterGang(List<String> cards,int cardType,boolean isDianGang){
		//先删除这次杠的
		removeCardByType(cards,cardType,4);
		boolean isMing = false;
		//去除碰
		for(int pt : pengType.keySet()){//如果杠的是之前碰过的牌
			if (pt != cardType) {
				removeCardByType(cards, pt, 3);
			} else {
				isMing = true;
			}
		}
		//去掉杠的牌
		cards = getCardsNoGang(cards);
		isMing = isMing||isDianGang;

		//胡牌类型加上杠
		List<HuCardType> list = getTingHuCardType(cards,null);
		return list.size()>0;
	}
}
