package com.code.server.game.mahjong.logic;


import com.code.server.game.mahjong.util.HuCardType;
import com.code.server.game.mahjong.util.HuLimit;
import com.code.server.game.mahjong.util.HuType;
import com.code.server.game.mahjong.util.HuUtil;

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
public class PlayerCardsInfoXXPB extends PlayerCardsInfoMj {

	@Override
	public void init(List<String> cards) {
		super.init(cards);
		specialHuScore.put(hu_清一色,4);
		specialHuScore.put(hu_一条龙,2);
		specialHuScore.put(hu_七小对,2);
		specialHuScore.put(hu_十三幺,6);
		specialHuScore.put(hu_杠上开花,2);
		specialHuScore.put(hu_混一色,2);
		specialHuScore.put(hu_字一色,6);
		specialHuScore.put(hu_三碰,2);
		specialHuScore.put(hu_豪华七小对,4);
	}


	//杠牌分数计算(不用提前计算)
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


    //胡牌分数计算（）
	/*点炮（点炮包胡）：	杠随胡走（只有胡牌的玩家杠分才算分）
						闲家给闲家点炮输5分
						闲家给庄家点炮输8分
						庄家给闲家点炮输8分
						自摸：  闲家自摸，每人输2分，庄家输4分
						庄家自摸，每人输4分
						三碰、混一色、七小对、一条龙、杠上开花：底分X2
						清一色、豪华七小对：底分X4
						风头清、十三幺：底分X6
						多个牌型同是存在时算分为：底分X（牌型+牌型）
	*/
	@Override
    public void huCompute(RoomInfo room, GameInfo gameInfo, boolean isZimo, long dianpaoUser, String card){

		//算杠分
		//gameInfo.computeAllGang();

		int maxFan = 1;//基础番
		System.out.println("胡的牌 : "+this.cards);
		List<String> cs = getCardsNoChiPengGang(cards);
		List<HuCardType> huList = HuUtil.isHu(cs, this, CardTypeUtil.cardType.get(card), new HuLimit(0));

		//是否是杠开
		boolean isGangKai = isGangKai();
		if (isGangKai) this.winType.add(HuType.hu_杠上开花);


		for (HuCardType huCardType : huList) {
			//是否是三碰
			boolean isSanPeng = (huCardType.peng.size()+huCardType.ke.size())>=3;
			if (isSanPeng) this.winType.add(HuType.hu_三碰);

			System.out.println("胡牌拥有的类型: " + huCardType.specialHuList);
			int s = huCardType.fan+0;
			//2倍
			if (isSanPeng){
				s+=2;
			}
			if (isGangKai){
				s+=2;
			}


			huCardType.fan = s;
			System.out.println("牌型的番数 : "+s);
			if (s >= maxFan) {
				maxFan = s;
			}
		}

		//设置胡牌类型
		setWinTypeResult(getMaxScoreHuCardType(huList));
		/*if(maxFan>1){
			maxFan=maxFan-1;
		}*/

		if(isZimo){
			if(gameInfo.getFirstTurn() == userId){//庄赢
				for (Long i : gameInfo.getPlayerCardsInfos().keySet()){
					gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - 4 * maxFan * room.getMultiple());
					room.setUserSocre(i, - 4 * maxFan * room.getMultiple());
				}
				this.score = this.score + 16 * maxFan * room.getMultiple();
				room.setUserSocre(this.userId, 16 * maxFan * room.getMultiple());
				this.fan = maxFan;
			}else{//闲赢
				for (Long i : gameInfo.getPlayerCardsInfos().keySet()){
					if(gameInfo.getFirstTurn() == i){
						gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - 4 * maxFan * room.getMultiple());
						room.setUserSocre(i, - 4 * maxFan * room.getMultiple());
					}else{
						gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - 2 * maxFan * room.getMultiple());
						room.setUserSocre(i, - 2 * maxFan * room.getMultiple());
					}
				}
				this.score = this.score + 10 * maxFan * room.getMultiple();
				room.setUserSocre(this.userId, 10 * maxFan * room.getMultiple());
				this.fan = maxFan;
			}
		}else{
			if(gameInfo.getFirstTurn() == userId){//庄赢
				gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - 8 * maxFan * room.getMultiple());
				this.score = this.score + 8 * maxFan * room.getMultiple();
				room.setUserSocre(dianpaoUser, - 8 * maxFan * room.getMultiple());
				room.setUserSocre(this.userId, 8 * maxFan * room.getMultiple());
				this.fan = maxFan;
			}else{//闲赢
				if(gameInfo.getFirstTurn() == dianpaoUser){//庄点炮
					gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - 8 * maxFan * room.getMultiple());
					this.score = this.score + 8 * maxFan * room.getMultiple();
					room.setUserSocre(dianpaoUser, - 8 * maxFan * room.getMultiple());
					room.setUserSocre(this.userId, 8 * maxFan * room.getMultiple());
				}else{
					gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - 5 * maxFan * room.getMultiple());
					this.score = this.score + 5 * maxFan * room.getMultiple();
					room.setUserSocre(dianpaoUser, - 5 * maxFan * room.getMultiple());
					room.setUserSocre(this.userId, 5 * maxFan * room.getMultiple());
				}
				this.fan = maxFan;
			}
		}


    }

	/**
	 * 处理杠分，杠随胡走（只有胡牌的玩家杠分才算分）
	 */
	/*public void computeALLGang(long dianpaoUser){
		Map<Long,Integer> scores = new HashMap<>();//分数计算key:use,value:score
		for (long i : gameInfo.users) {
			scores.put(i, 0);
		}

		for (PlayerCardsInfoMj playerCardsInfo : gameInfo.getPlayerCardsInfos().values()) {
			if(this.userId == playerCardsInfo.userId){
				//暗杠计算
				for (long i : scores.keySet()) {
					scores.put(i, scores.get(i) - playerCardsInfo.getAnGangType().size()*2);
				}
				scores.put(playerCardsInfo.getUserId(), scores.get(playerCardsInfo.getUserId())+playerCardsInfo.getAnGangType().size()*2*4);
				//明杠计算
				for (Integer ii : playerCardsInfo.getMingGangType().keySet()) {
					long dianGangUser = playerCardsInfo.getMingGangType().get(ii);
					if (dianGangUser != -1) {
						for (long i : scores.keySet()) {
							scores.put(i, scores.get(i) - 1);
						}
						scores.put(playerCardsInfo.getUserId(), scores.get(playerCardsInfo.getUserId()) + 4);
					}else{
						scores.put(playerCardsInfo.getUserId(), scores.get(playerCardsInfo.getUserId()) + 4);
						scores.put(dianpaoUser, scores.get(dianpaoUser) - 4);
					}
				}
			}
		}

		for (long i : scores.keySet()) {
			gameInfo.getPlayerCardsInfos().get(i).setScore(scores.get(i));
			roomInfo.setUserSocre(i, scores.get(i));
		}
	}*/

	public boolean isHasChi(String card){
		return false;
	}

	@Override
	public boolean isCanTing(List<String> cards) {
		return false;
	}
}
