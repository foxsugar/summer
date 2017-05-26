package com.code.server.game.mahjong.logic;

import com.byz.mj.util.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**   
* 类名称：PlayerCardsInfoGSJ   
* 类描述： 拐三角  
* 创建人：Clark  
* 创建时间：2016年12月15日 下午2:55:54   
* 修改人：Clark  
* 修改时间：2016年12月15日 下午2:55:54   
* 修改备注：   
* @version 1.0    
*
 */
public class PlayerCardsInfoGSJ extends PlayerCardsInfo {
	
	//庄家基础分
	private static final int BANKER_BASE_SCORE = 3;
	
	//验证牌花色是否大于8张
	private static final int CHECK_CARD_POINT = 7;
	
	//杠
	private static final int GANG_MING_SCORE = 10;
	private static final int GANG_AN_SCORE = 20;
	
	// 庄家赢，闲家扣的分数，自摸在原基础×2
	private static final int PUTONG_HU_SCORE = 11;
	private static final int YI_TIAO_LONG_SCORE = 29;
	private static final int QING_YI_SE_SCORE = 29;
	private static final int QINGYISE_YITIAOLONG_SCORE = 58;
	
	//闲家赢，庄家和闲家扣的分数，自摸在原基础×2
	private static final int PUTONG_HU_SCORE_ZHUANG = 11;
	private static final int YI_TIAO_LONG_SCORE_ZHUANG = 29;
	private static final int QING_YI_SE_SCORE_ZHUANG = 29;
	private static final int QINGYISE_YITIAOLONG_SCORE_ZHUANG = 58;
	
	private static final int PUTONG_HU_SCORE_XIAN = 8;
	private static final int YI_TIAO_LONG_SCORE_XIAN = 26;
	private static final int QING_YI_SE_SCORE_XIAN = 26;
	private static final int QINGYISE_YITIAOLONG_SCORE_XIAN = 52;

	@Override
    public void init(List<String> cards) {
        super.init(cards);

        specialHuScore.put(hu_清一色,1);
        specialHuScore.put(hu_一条龙,1);
        specialHuScore.put(hu_清龙,1);
    }
	
	@Override
	public boolean isCanTing(List<String> cards) {
		return false;
	}

	public boolean isHasChi(String card){
		return false;
	}
	
	/**
     * 是否可以胡这张牌
     * @param card
     * @return
     */
	@Override
    public boolean isCanHu_dianpao(String card) {
		List<String> tempList = new ArrayList<>();
		tempList.addAll(cards);
		tempList.add(card);
        List<String> temp = getCardsAddThisCard(card);
        List<String> noPengAndGang = getCardsNoChiPengGang(temp);
        System.out.println("检测是否可胡点炮= " + noPengAndGang);
        return HuUtil.isHu(noPengAndGang, this, CardTypeUtil.cardType.get(card), null).size()>0 && checkCard(tempList);
    }

    /**
     * 是否可胡 自摸
     * @param card
     * @return
     */
	@Override
    public boolean isCanHu_zimo(String card) {
        List<String> cs = getCardsNoChiPengGang(cards);
        System.out.println("检测是否可胡自摸= " + cs );
        return HuUtil.isHu(cs, this, CardTypeUtil.cardType.get(card), null).size()>0 && checkCard(cards);

    }
	
	
	// 杠牌分数计算
	@Override
    public void gangCompute(RoomInfo room,GameInfo gameInfo,boolean isMing,int diangangUser,String card){
		super.gangCompute(room,gameInfo,isMing,diangangUser,card);
    	if(!isMing){//暗杠
        	for (Integer i : gameInfo.getPlayerCardsInfos().keySet()){
        		gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - GANG_AN_SCORE);
        		room.setUserSocre(i, - GANG_AN_SCORE);
        	}
        	this.score = this.score + 3 * GANG_AN_SCORE;
        	room.setUserSocre(this.userId, 3 * GANG_AN_SCORE);
    	}else{//明杠
    		for (Integer i : gameInfo.getPlayerCardsInfos().keySet()){
        		gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - GANG_MING_SCORE);
        		room.setUserSocre(i, - GANG_MING_SCORE);
        	}
        	this.score = this.score + 3 * GANG_MING_SCORE;
        	room.setUserSocre(this.userId, 3 * GANG_MING_SCORE);
    	}
    	room.pushScoreChange();
    }
	
	
	// 胡牌分数计算
	@Override
	public void huCompute(RoomInfo room, GameInfo gameInfo, boolean isZimo,int dianpaoUser, String card) {
		List<String> cs = getCardsNoChiPengGang(cards);
		List<HuCardType> huList = HuUtil.isHu(cs, this,CardTypeUtil.cardType.get(card) , new HuLimit(0));
		//设置胡牌类型
		setWinTypeResult(getMaxScoreHuCardType(huList));
		
		//设置胡牌类型
		HuCardType huCardType = getMaxScoreHuCardType(huList);
		this.winType.addAll(huCardType.specialHuList);

		if(this.userId==gameInfo.getFirstTurn()){//庄赢
			if(isZimo){
				for (Integer i : gameInfo.getPlayerCardsInfos().keySet()) {
					gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - 2 * huForScoresOfZhuangWin(cards,huCardType) - 2 * BANKER_BASE_SCORE * bankerNumber(room.getBankerMap()));
					room.setUserSocre(i,- 2 * huForScoresOfZhuangWin(cards,huCardType) - 2 * BANKER_BASE_SCORE * bankerNumber(room.getBankerMap()));
				}
				this.score = this.score + 6 * huForScoresOfZhuangWin(cards,huCardType) + 6 * BANKER_BASE_SCORE * bankerNumber(room.getBankerMap());
				room.setUserSocre(this.userId,6 * huForScoresOfZhuangWin(cards,huCardType) + 6 * BANKER_BASE_SCORE * bankerNumber(room.getBankerMap()));
				this.fan = huForScoresOfZhuangWin(cards,huCardType);
				//this.winType = CardUtil.huForWinTypeGSJ(cards);
			}else{
				for (Integer i : gameInfo.getPlayerCardsInfos().keySet()) {
					gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - huForScoresOfZhuangWin(cards,huCardType) - BANKER_BASE_SCORE * bankerNumber(room.getBankerMap()));
					room.setUserSocre(i,- huForScoresOfZhuangWin(cards,huCardType) - BANKER_BASE_SCORE * bankerNumber(room.getBankerMap()));
				}
				this.score = this.score + 3 * huForScoresOfZhuangWin(cards,huCardType) + 3 * BANKER_BASE_SCORE * bankerNumber(room.getBankerMap());
				room.setUserSocre(this.userId,3 * huForScoresOfZhuangWin(cards,huCardType) + 3 * BANKER_BASE_SCORE * bankerNumber(room.getBankerMap())) ;
				this.fan = huForScoresOfZhuangWin(cards,huCardType);
				//this.winType = CardUtil.huForWinTypeGSJ(cards);
			}
		}
		else{//闲赢
			if(isZimo){
				for (Integer i : gameInfo.getPlayerCardsInfos().keySet()) {
					if(i==gameInfo.getFirstTurn()){
						gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - 2 * huForScoresOfXianWinDownZhuang(cards,huCardType)- 2 * BANKER_BASE_SCORE * bankerNumber(room.getBankerMap()));
						room.setUserSocre(i,- 2 * huForScoresOfXianWinDownZhuang(cards,huCardType) - 2 * BANKER_BASE_SCORE * bankerNumber(room.getBankerMap()));
					}else{
						gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - 2 * huForScoresOfXianWinDownXian(cards,huCardType));
						room.setUserSocre(i,- 2 * huForScoresOfXianWinDownXian(cards,huCardType));
					}
				}
				this.score = this.score + 2 * huForScoresOfXianWinDownZhuang(cards,huCardType) + 2 * BANKER_BASE_SCORE * bankerNumber(room.getBankerMap()) + 4 * huForScoresOfXianWinDownXian(cards,huCardType);
				room.setUserSocre(this.userId, 2 * huForScoresOfXianWinDownZhuang(cards,huCardType) + 2 * BANKER_BASE_SCORE * bankerNumber(room.getBankerMap()) + 4 * huForScoresOfXianWinDownXian(cards,huCardType));
				this.fan = huForScoresOfXianWinDownXian(cards,huCardType);
				//this.winType = CardUtil.huForWinTypeGSJ(cards);
			}else{
				for (Integer i : gameInfo.getPlayerCardsInfos().keySet()) {
					if(i==gameInfo.getFirstTurn()){
						gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - huForScoresOfXianWinDownZhuang(cards,huCardType) - BANKER_BASE_SCORE * bankerNumber(room.getBankerMap()));
						room.setUserSocre(i,- huForScoresOfXianWinDownZhuang(cards,huCardType) - BANKER_BASE_SCORE * bankerNumber(room.getBankerMap()));
					}else{
						gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - huForScoresOfXianWinDownXian(cards,huCardType));
						room.setUserSocre(i,- huForScoresOfXianWinDownXian(cards,huCardType));
					} 
				}
				this.score = this.score + huForScoresOfXianWinDownZhuang(cards,huCardType) + BANKER_BASE_SCORE * bankerNumber(room.getBankerMap()) + 2 * huForScoresOfXianWinDownXian(cards,huCardType);
				room.setUserSocre(this.userId,huForScoresOfXianWinDownZhuang(cards,huCardType) + BANKER_BASE_SCORE * bankerNumber(room.getBankerMap()) +  2 * huForScoresOfXianWinDownXian(cards,huCardType));
				this.fan = huForScoresOfXianWinDownXian(cards,huCardType);
				//this.winType = CardUtil.huForWinTypeGSJ(cards);
			}
		}
	}

	
	/**
	 * 庄赢，闲家扣的分
	 * @return
	 */
	public static int huForScoresOfZhuangWin(List<String> lists,HuCardType huCardType) {
		ArrayList<String> list = new ArrayList<String>();
		String result = "";
		String scoreResult = "";
		int score = 0;
		for (String string : lists) {
			list.add(MajiangUtil.perfixToSuffix(string));
		}

		if (huCardType.specialHuList.contains(HuType.hu_清龙)) {
			result += MahjongCode.QINGLONG;
			result += ",";
		}
		if (huCardType.specialHuList.contains(HuType.hu_清一色)) {
			result += MahjongCode.QINGYISE;
			result += ",";
		}
		if (huCardType.specialHuList.contains(HuType.hu_一条龙)) {
			result += MahjongCode.YITIAOLONG;
			result += ",";
		} else {
			result += MahjongCode.PUTONGHU;
			result += ",";
		}
		scoreResult = result.substring(0, result.length() - 1);
		if (scoreResult.contains(",")) {
			for (String string : scoreResult.split(",")) {
				if (Integer.parseInt(string) > score) {
					score = Integer.parseInt(string);
				}
			}
		} else {
			score = Integer.parseInt(scoreResult);
		}
		
		if(score==288){
			score = QINGYISE_YITIAOLONG_SCORE;
		}
		else if (score==235||score==244) {
			score = QING_YI_SE_SCORE;
		}
		else {
			score = PUTONG_HU_SCORE;
		}
		return score;
	}
	
	/**
	 * 闲赢，庄家扣的分
	 * @return
	 */
	public static int huForScoresOfXianWinDownZhuang(List<String> lists,HuCardType huCardType) {

		ArrayList<String> list = new ArrayList<String>();
		String result = "";
		String scoreResult = "";
		int score = 0;
		for (String string : lists) {
			list.add(MajiangUtil.perfixToSuffix(string));
		}

		if (huCardType.specialHuList.contains(HuType.hu_清龙)) {
			result += MahjongCode.QINGLONG;
			result += ",";
		}
		if (huCardType.specialHuList.contains(HuType.hu_清一色)) {
			result += MahjongCode.QINGYISE;
			result += ",";
		}
		if (huCardType.specialHuList.contains(HuType.hu_一条龙)) {
			result += MahjongCode.YITIAOLONG;
			result += ",";
		} else {
			result += MahjongCode.PUTONGHU;
			result += ",";
		}
		scoreResult = result.substring(0, result.length() - 1);
		if (scoreResult.contains(",")) {
			for (String string : scoreResult.split(",")) {
				if (Integer.parseInt(string) > score) {
					score = Integer.parseInt(string);
				}
			}
		} else {
			score = Integer.parseInt(scoreResult);
		}
		
		if(score==288){
			score = QINGYISE_YITIAOLONG_SCORE_ZHUANG;
		}
		else if (score==235||score==244) {
			score = QING_YI_SE_SCORE_ZHUANG;
		}
		else {
			score = PUTONG_HU_SCORE_ZHUANG;
		}
		return score;
		
	}
	
	/**
	 * 闲赢，闲家扣的分
	 * @return
	 */
	public static int huForScoresOfXianWinDownXian(List<String> lists,HuCardType huCardType) {
		ArrayList<String> list = new ArrayList<String>();
		String result = "";
		String scoreResult = "";
		int score = 0;
		for (String string : lists) {
			list.add(MajiangUtil.perfixToSuffix(string));
		}

		if (huCardType.specialHuList.contains(HuType.hu_清龙)) {
			result += MahjongCode.QINGLONG;
			result += ",";
		}
		if (huCardType.specialHuList.contains(HuType.hu_清一色)) {
			result += MahjongCode.QINGYISE;
			result += ",";
		}
		if (huCardType.specialHuList.contains(HuType.hu_一条龙)) {
			result += MahjongCode.YITIAOLONG;
			result += ",";
		} else {
			result += MahjongCode.PUTONGHU;
			result += ",";
		}
		scoreResult = result.substring(0, result.length() - 1);
		if (scoreResult.contains(",")) {
			for (String string : scoreResult.split(",")) {
				if (Integer.parseInt(string) > score) {
					score = Integer.parseInt(string);
				}
			}
		} else {
			score = Integer.parseInt(scoreResult);
		}
		
		if(score==288){
			score = QINGYISE_YITIAOLONG_SCORE_XIAN;
		}
		else if (score==235||score==244) {
			score = QING_YI_SE_SCORE_XIAN;
		}
		else {
			score = PUTONG_HU_SCORE_XIAN;
		}
		return score;
	}

	/**
	* @Title: 验证牌花色是否大于8张
	* @Creater: Clark  
	* @Description:
	* @param @param cards
	* @param @return    设定文件
	* @return boolean    返回类型
	* @throws
	 */
	public boolean checkCard(List<String> cards){
		int wanNum = 0;
		int tiaoNum = 0;
		int tongNum = 0;
		for (String string : cards) {
			if(CardTypeUtil.getCardGroup(string)==CardTypeUtil.GROUP_WAN){
				wanNum++;
			}else if(CardTypeUtil.getCardGroup(string)==CardTypeUtil.GROUP_TONG){
				tiaoNum++;
			}else if(CardTypeUtil.getCardGroup(string)==CardTypeUtil.GROUP_TIAO){
				tongNum++;
			}
		}
		return wanNum > CHECK_CARD_POINT || tiaoNum > CHECK_CARD_POINT || tongNum > CHECK_CARD_POINT;
	}

	/**
	* @Title: 查询连庄次数
	* @Creater: Clark  
	* @Description:
	* @param @param bankerMap
	* @param @return    设定文件
	* @return int    返回类型
	* @throws
	 */
	public static int bankerNumber(Map<Integer,Integer> bankerMap){
		int continueNum = 0;
		int maxGameNum = 0;
		Set<Integer> number = bankerMap.keySet();
		for (Integer i : number) {
			if(i>maxGameNum){
				maxGameNum = i;
			}
		}
		for (int i = maxGameNum; i > 1; i--) {
			if(bankerMap.get(i) == (int)bankerMap.get(i-1)){
				continueNum++;
			}else{
				return continueNum;
			}
		}
		return continueNum;
	}
	
	
}
