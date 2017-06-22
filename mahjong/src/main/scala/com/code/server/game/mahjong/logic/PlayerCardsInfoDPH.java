package com.code.server.game.mahjong.logic;


import com.code.server.game.mahjong.util.*;

import java.util.List;

/**
*    
* 项目名称：mj_server   
* 类名称：PlayerCardsInfoTDH   
* 类描述：点炮胡
* 创建人：Clark  
* 创建时间：2016年12月5日 上午11:25:53   
* 修改人：Clark  
* 修改时间：2016年12月5日 上午11:25:53   
* 修改备注：   
* @version 1.0    
*
 */
public class PlayerCardsInfoDPH extends PlayerCardsInfoMj {

	@Override
	public void init(List<String> cards) {
		super.init(cards);
		specialHuScore.put(hu_清一色,9);
		specialHuScore.put(hu_一条龙,9);
		specialHuScore.put(hu_夹张,4);
		specialHuScore.put(hu_清龙,18);
	}

	public boolean isHasChi(String card){
		return false;
	}

	@Override
	public boolean isCanTing(List<String> cards) {
		return false;
	}

	//杠牌分数计算
	@Override
    public void gangCompute(RoomInfo room, GameInfo gameInfo, boolean isMing, long diangangUser, String card){
		super.gangCompute(room,gameInfo,isMing,diangangUser,card);
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
    //胡牌分数计算
	@Override
    public void huCompute(RoomInfo room, GameInfo gameInfo, boolean isZimo, long dianpaoUser, String card){
		List<String> cs = getCardsNoChiPengGang(cards);
		List<HuCardType> huList = HuUtil.isHu(cs, this,CardTypeUtil.cardType.get(card) , new HuLimit(0));
		//设置胡牌类型
		HuCardType huCardType = getMaxScoreHuCardType(huList);
		this.winType.addAll(huCardType.specialHuList);

    	if(isZimo){
    		if(huCardType.specialHuList.contains(hu_夹张)&& !huCardType.specialHuList.contains(hu_一条龙) && !huCardType.specialHuList.contains(hu_清一色)&&!huCardType.specialHuList.contains(hu_清龙)){
    			for (Long i : gameInfo.getPlayerCardsInfos().keySet()){
            		gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - room.getMultiple() * 4);
            		room.setUserSocre(i,  - room.getMultiple() * 4);
            	}
            	this.score = this.score + room.getPersonNumber() * room.getMultiple() * 4;
            	room.setUserSocre(this.userId, room.getPersonNumber() * room.getMultiple() * 4);
            	this.fan = 4;
//            	Set<Integer> set = new HashSet<>();
//            	set.add(HuType.hu_砍胡);
            	this.winType.add(hu_砍胡);
            	this.winType.remove(hu_夹张);
    		}else{
    			for (Long i : gameInfo.getPlayerCardsInfos().keySet()){
            		gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - room.getMultiple() * MahjongCode.HUTOSCOREDPH.get(""+ CardUtil.huForScoresDPH(getCardsNoChiPengGang(cards),huCardType)));
            		room.setUserSocre(i,  - room.getMultiple() * MahjongCode.HUTOSCOREDPH.get(""+CardUtil.huForScoresDPH(getCardsNoChiPengGang(cards),huCardType)));
            	}
            	this.score = this.score + room.getPersonNumber() * room.getMultiple() * MahjongCode.HUTOSCOREDPH.get(""+CardUtil.huForScoresDPH(getCardsNoChiPengGang(cards),huCardType)+"");
            	room.setUserSocre(this.userId, room.getPersonNumber() * room.getMultiple() * MahjongCode.HUTOSCOREDPH.get(""+CardUtil.huForScoresDPH(getCardsNoChiPengGang(cards),huCardType)+""));
            	this.fan = MahjongCode.HUTOSCOREDPH.get(""+CardUtil.huForScoresDPH(getCardsNoChiPengGang(cards),huCardType)+"");
//            	this.winType = CardUtil.huForWinTypeDPH(getCardsNoChiPengGang(cards));
    		}
    	}else{
    		if(huCardType.specialHuList.contains(hu_夹张)&& !huCardType.specialHuList.contains(hu_一条龙) && !huCardType.specialHuList.contains(hu_清一色)&&!huCardType.specialHuList.contains(hu_清龙)){
/*    			for (Integer i : gameInfo.getPlayerCardsInfos().keySet()){
            		gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - room.getMultiple() * MahjongCode.HUTOSCOREDPH.get(""+CardUtil.huForScoresDPH(getCardsNoChiPengGang(cards),huCardType)));
            		room.setUserSocre(i,  - room.getMultiple() * MahjongCode.HUTOSCOREDPH.get(""+CardUtil.huForScoresDPH(getCardsNoChiPengGang(cards),huCardType)));
            	}
            	this.score = this.score + room.getPersonNumber() * room.getMultiple() * 4;
            	room.setUserSocre(this.userId, room.getPersonNumber() * room.getMultiple() * 4);*/
            	
            	gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - room.getMultiple() * 4);
        		this.score = this.score + room.getMultiple() * 4;
            	room.setUserSocre(dianpaoUser, - room.getMultiple() * 4);
        		room.setUserSocre(this.userId, room.getMultiple() * 4);

            	this.fan = 4;
				this.winType.add(hu_砍胡);
				this.winType.remove(hu_夹张);
//            	Set<Integer> set = new HashSet<>();
//            	set.add(HuType.hu_砍胡);
//            	this.winType = set;
    		}else{
    			gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - room.getMultiple() * MahjongCode.HUTOSCOREDPH.get(""+CardUtil.huForScoresDPH(getCardsNoChiPengGang(cards),huCardType)));
        		this.score = this.score + room.getMultiple() * MahjongCode.HUTOSCOREDPH.get(""+CardUtil.huForScoresDPH(getCardsNoChiPengGang(cards),huCardType));
            	room.setUserSocre(dianpaoUser, - room.getMultiple() * MahjongCode.HUTOSCOREDPH.get(""+CardUtil.huForScoresDPH(getCardsNoChiPengGang(cards),huCardType)));
        		room.setUserSocre(this.userId, room.getMultiple() * MahjongCode.HUTOSCOREDPH.get(""+CardUtil.huForScoresDPH(getCardsNoChiPengGang(cards),huCardType)));
        		this.fan = MahjongCode.HUTOSCOREDPH.get(""+CardUtil.huForScoresDPH(getCardsNoChiPengGang(cards),huCardType)+"");
//        		this.winType = CardUtil.huForWinTypeDPH(getCardsNoChiPengGang(cards));
    		}
    	}
    }
}
