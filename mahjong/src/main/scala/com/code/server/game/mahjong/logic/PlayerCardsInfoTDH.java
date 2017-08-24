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
	}

	@Override
	public boolean isCanTing(List<String> cards) {
		return false;
	}
	public boolean isHasChi(String card){
		return false;
	}
	//杠牌分数计算
	@Override
    public void gangCompute(RoomInfo room, GameInfo gameInfo, boolean isMing, long diangangUser, String card){
		super.gangCompute(room,gameInfo,isMing,diangangUser,card);
    	if(this.roomInfo.getGameType().equals("HT") || this.roomInfo.getGameType().equals("JL") ||this.roomInfo.getGameType().equals("DS") ||this.roomInfo.getGameType().equals("LQ") || this.roomInfo.getGameType().equals("HL")){
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
        	room.pushScoreChange();
    	}
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

		else if(this.roomInfo.getGameType().equals("LQ")){
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
				}
			}
		}

    	else{
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
            			gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - 2 * room.getMultiple() * MahjongCode.HUTOSCORE.get(""+CardUtil.huForScores(cards,huCardType))/3);
            			room.setUserSocre(i,  - 2 * room.getMultiple() * MahjongCode.HUTOSCORE.get(""+CardUtil.huForScores(cards,huCardType))/3);
            		}
            		this.score = this.score + 2 * room.getPersonNumber() * room.getMultiple() * MahjongCode.HUTOSCORE.get(""+CardUtil.huForScores(cards,huCardType)+"")/3;
            		room.setUserSocre(this.userId, 2 * room.getPersonNumber() * room.getMultiple() * MahjongCode.HUTOSCORE.get(""+CardUtil.huForScores(cards,huCardType)+"")/3);
            		this.fan = 2 * MahjongCode.HUTOSCORE.get(""+CardUtil.huForScores(cards,huCardType)+"");
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
            		gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - room.getMultiple() * MahjongCode.HUTOSCORE.get(""+CardUtil.huForScores(cards,huCardType)));
        			this.score = this.score + room.getMultiple() * MahjongCode.HUTOSCORE.get(""+CardUtil.huForScores(cards,huCardType));
            		room.setUserSocre(dianpaoUser, - room.getMultiple() * MahjongCode.HUTOSCORE.get(""+CardUtil.huForScores(cards,huCardType)));
        			room.setUserSocre(this.userId, room.getMultiple() * MahjongCode.HUTOSCORE.get(""+CardUtil.huForScores(cards,huCardType)));
        			this.fan = MahjongCode.HUTOSCORE.get(""+CardUtil.huForScores(cards,huCardType)+"");
//        			this.winType = CardUtil.huForWinType(cards);
            	}
        	}
    	}
    	
    	if(!this.roomInfo.getGameType().equals("HT") && !this.roomInfo.getGameType().equals("JL") && !this.roomInfo.getGameType().equals("DS")){
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
}
