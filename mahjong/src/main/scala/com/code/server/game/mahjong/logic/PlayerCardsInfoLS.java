package com.code.server.game.mahjong.logic;



import com.code.server.game.mahjong.util.HuCardType;
import com.code.server.game.mahjong.util.HuLimit;
import com.code.server.game.mahjong.util.HuUtil;

import java.util.*;

/**
 * 
 * 项目名称：mj_server
 * 类名称：PlayerCardsInfoLS
 * 类描述：
 * 创建人：jifang
 * 创建时间：2016年12月12日 上午10:06:47
 * 修改人：clark
 * 修改时间：2016年12月12日 上午10:06:47
 * 修改备注：
 */
public class PlayerCardsInfoLS extends PlayerCardsInfoMj {
	
	List<String> first_four = new ArrayList<String>();//立四 前四张牌
	

	/**
     * 根据发的牌初始化
     * @param cards
     */
	@Override
    public void init(List<String> cards) {
        this.cards = cards;
        first_four.add(cards.get(0));
        first_four.add(cards.get(1));
        first_four.add(cards.get(2));
        first_four.add(cards.get(3));
        
        
        specialHuScore.put(hu_缺一门,1);
        specialHuScore.put(hu_夹张,1);
        specialHuScore.put(hu_边张,1);
        specialHuScore.put(hu_吊张,1);
        specialHuScore.put(hu_一条龙,10);
        specialHuScore.put(hu_清一色,10);
        specialHuScore.put(hu_清龙,20);
    }
	
	
	

	/**
     * 是否可以胡这张牌
     * @param card
     * @return
     */
    public boolean isCanHu_dianpao(String card) {
    	if(!isTing){
    		return false;
    	}
        return super.isCanHu_dianpao(card);
    }
	public boolean isHasChi(String card){
		return false;
	}
    /**
     * 是否可胡 自摸
     * @param card
     * @return
     */
    public boolean isCanHu_zimo(String card) {
    	if(!isTing){
    		return false;
    	}
        return super.isCanHu_zimo(card);

    }
	
	
	
    //胡牌分数计算
    public void huCompute(RoomInfo room, GameInfo gameInfo, boolean isZimo, long dianpaoUser, String card){
    	int scale = room.getMultiple();
        List<HuCardType> huList = HuUtil.isHu(getCardsNoChiPengGang(cards), this, CardTypeUtil.cardType.get(card), new HuLimit(0));
        
        //胡牌基本分数
        int LSscore = 1;
        
        HuCardType hucardtype = getMaxScoreHuCardType(huList);

		//设置胡牌类型
		setWinTypeResult(hucardtype);
        
        LSscore += hucardtype.getFan();
        
    	//庄家
    	if(gameInfo.getFirstTurn()==userId){
    		LSscore += 1;
    	}
    	//是否是自摸
    	if(isZimo){
    		LSscore = LSscore*2;//自摸翻倍
    		for(PlayerCardsInfoMj playerCardsInfo:gameInfo.getPlayerCardsInfos().values()){
    			if(playerCardsInfo.getUserId()!=userId){
    				playerCardsInfo.addScore(-LSscore * scale);
    			}else{
    				playerCardsInfo.addScore(LSscore*3 *scale);
    			}

    		}
    		if(gameInfo.getFirstTurn()!=userId){
				gameInfo.getPlayerCardsInfos().get(gameInfo.getFirstTurn()).addScore(-1);
				gameInfo.getPlayerCardsInfos().get(userId).addScore(1);
			}
    	}else{
    		//听牌点炮不加分，三家输
    		if(gameInfo.getPlayerCardsInfos().get(dianpaoUser).isTing){
    			for(PlayerCardsInfoMj playerCardsInfo:gameInfo.getPlayerCardsInfos().values()){
        			if(playerCardsInfo.getUserId()!=userId){
        				playerCardsInfo.addScore(-LSscore * scale);
        			}else{
        				playerCardsInfo.addScore(LSscore*3 * scale);
        			}
        			
        		}
    			if(gameInfo.getFirstTurn()!=userId){
    				gameInfo.getPlayerCardsInfos().get(gameInfo.getFirstTurn()).addScore(-1);
    				gameInfo.getPlayerCardsInfos().get(userId).addScore(1);
    			}
    		}else{
    			//没听牌一家输
    			gameInfo.getPlayerCardsInfos().get(userId).addScore((LSscore*3 * scale)+1);
    			gameInfo.getPlayerCardsInfos().get(dianpaoUser).addScore((-(LSscore*3 * scale))-1);
    				
    			if(gameInfo.getFirstTurn()!=userId){
    				gameInfo.getPlayerCardsInfos().get(dianpaoUser).addScore(-1);
    				gameInfo.getPlayerCardsInfos().get(userId).addScore(1);
    			}
    		}
    	}
    	
    	//暗杠三家减分
    	if(gameInfo.getPlayerCardsInfos().get(userId).getAnGangType().size()>0){
    			for(PlayerCardsInfoMj playerCardsInfo:gameInfo.getPlayerCardsInfos().values()){
    				if(playerCardsInfo.getUserId()!=userId){
    					playerCardsInfo.addScore(-(gameInfo.getPlayerCardsInfos().get(userId).getAnGangType().size()*2)*scale);
    				}else{
    					playerCardsInfo.addScore(gameInfo.getPlayerCardsInfos().get(userId).getAnGangType().size()*2*3 * scale);
    				}
    			}
    	}
    	
    	//明杠
    	if(gameInfo.getPlayerCardsInfos().get(userId).getMingGangType().size()>0){
    		for(Long minguser : mingGangType.values()){
    			//判断是否是碰后杠
    			if(minguser!=-1){
    				//听牌三家输
    				if(gameInfo.getPlayerCardsInfos().get(minguser).isTing){
    					for(PlayerCardsInfoMj playerCardsInfo:gameInfo.getPlayerCardsInfos().values()){
    						if(playerCardsInfo.getUserId()!=userId){
    							playerCardsInfo.addScore(-(gameInfo.getPlayerCardsInfos().get(userId).getMingGangType().size()) * scale);
    						}else{
    							playerCardsInfo.addScore(gameInfo.getPlayerCardsInfos().get(userId).getMingGangType().size()*3 * scale);
    						}
    					}
    				}else{
    					//没听牌一家输
    					gameInfo.getPlayerCardsInfos().get(userId).addScore(gameInfo.getPlayerCardsInfos().get(userId).getMingGangType().size()*3 * scale);
    					gameInfo.getPlayerCardsInfos().get(minguser).addScore(-(gameInfo.getPlayerCardsInfos().get(userId).getMingGangType().size()*3) * scale);
    				}
    			}else{
    				//碰后杠 三家包赔
    				if("-1".equals(minguser.toString())){
    					for(PlayerCardsInfoMj playerCardsInfo:gameInfo.getPlayerCardsInfos().values()){
    						if(playerCardsInfo.getUserId()!=userId){
    							playerCardsInfo.addScore(-(gameInfo.getPlayerCardsInfos().get(userId).getMingGangType().size()) * scale);
    						}else{
    							playerCardsInfo.addScore(gameInfo.getPlayerCardsInfos().get(userId).getMingGangType().size()*3 * scale);
    						}
    					}
    				}
    			}
    			
    		}
    	}
    	
    	fan  = (int)gameInfo.getPlayerCardsInfos().get(userId).getScore();
    	
    	//将所有人的分数放入RoomInfo
    	for(PlayerCardsInfoMj playerCardsInfo:gameInfo.getPlayerCardsInfos().values()){
    		room.setUserSocre(playerCardsInfo.getUserId(),playerCardsInfo.getScore()*room.getMultiple());
    	}
    }
    
    
    
    
    
    //是否能听牌
    public boolean isCanTing(List<String> cards) {
    	System.out.println(cards);
    	if(isTing){
    		return false;
    	}
    	List<String> handCards = new ArrayList<>();
        handCards.addAll(this.cards);
        List<String> cardsNoPengGang = getCardsNoChiPengGang(handCards);
        
    	//是否多一张牌
        int size = cardsNoPengGang.size();
        boolean isMore = (size - 2) % 3 == 0;//去掉将如果能整除说明手牌多一张
        List<String> lsList = new ArrayList<>();
        lsList.addAll(first_four);
        if(!isMore){
        	//删除手里的牌，剩余一张
        	handCards.removeAll(cards);
        	if(!first_four.contains(handCards.get(0))){
        		return false;
        	}else{
        		lsList.remove(handCards.get(0));
        	}
        }
    	return getTingCardType(cardsNoPengGang,first_four,null).size()>0;
    }
    	
    
    
    
    public Set<Integer> getTingCardType(List<String> cards,List<String> lsCards, HuLimit limit) {
    	 //获得没有碰和杠的牌
        List<String> handCards = new ArrayList<>();
        handCards.addAll(cards);

        //是否多一张牌
        int size = handCards.size();
        boolean isMore = (size - 2) % 3 == 0;//去掉将如果能整除说明手牌多一张
        Set<Integer> tingList = new HashSet<>();
        if (isMore) {//多一张
            //循环去掉一张看能否听
        	if(lsCards.size()!=0){
        		for (String card : lsCards) {
        			List<String> tempCards = new ArrayList<>();
        			tempCards.addAll(cards);
        			tempCards.remove(card);
        			tingList.addAll(HuUtil.isTing(tempCards, this,limit));
        		}
        	}else{
        		tingList = super.getTingCardType(handCards, limit);
        	}
        } else {
            tingList.addAll(HuUtil.isTing(handCards, this,limit));
        }
        return tingList;
    }
    
    /**
     * 能否碰这张牌
     * @param card
     * @return
     */
    @Override
    public boolean isCanPengAddThisCard(String card) {
    	boolean results = true;
    	if(isTing){
    		return false;
    	}
    	
    	//除去杠和碰的牌
        List<String> temp = getCardsNoChiPengGang(cards);
        //获取能否碰这张牌的编号
        int cardType = CardTypeUtil.cardType.get(card);
        //获取剩下牌的编号
        Map<Integer, Integer> cardsNum = getCardNum(temp);
        //前四张牌类型
        Map<Integer,Integer> firstFourNum = getCardNum(first_four);
        
        if(firstFourNum.containsKey(cardType) && firstFourNum.get(cardType)>=2){
        	firstFourNum.remove(cardType);
        }
        
	        if(firstFourNum.size()<1){
	        	results = false;
	        }
        if(!(cardsNum.containsKey(cardType) && cardsNum.get(cardType)>=2)){
        	results = false;
        }
	        
        return results;
    }
    
    
    
    /**
     * 能否杠这张牌     自己摸的这张牌，杠之后，前四张必须留一张牌，否则不能杠
     * @param card
     * @return
     */
    @Override
    public boolean isCanGangThisCard(String card) {
    	boolean results = true;
    	
    	if(isTing){
    		return false;
    	}
    	//除去杠和碰的牌
        List<String> temp = getCardsNoGang(cards);
        //获取能否杠这张牌的编号
        int cardType = CardTypeUtil.cardType.get(card);
        //获取剩下牌的编号
        Map<Integer, Integer> cardsNum = getCardNum(temp);
        //前四张牌类型
        Map<Integer,Integer> firstFourNum = getCardNum(first_four);
        
        if(firstFourNum.containsKey(cardType) && firstFourNum.get(cardType)==4){
        	firstFourNum.remove(cardType);
        }
        
        if(firstFourNum.size()<1){
        	results = false;
        }
        if(!(cardsNum.containsKey(cardType) && cardsNum.get(cardType)==4)){
        	results = false;
        }
        
        return results;
    }
	
    /**
     * 加上这张牌能否杠   桌上打的这张牌，杠之后，前四张必须留一张牌，否则不能杠
     * @param card
     * @return
     */
    @Override
    public boolean isCanGangAddThisCard(String card) {
    	boolean results = true;
    	
    	if(isTing){
    		return false;
    	}
    	
    	//除去杠和碰的牌
        List<String> temp = getCardsNoGang(cards);
        //获取能否杠这张牌的编号
        int cardType = CardTypeUtil.cardType.get(card);
        //获取剩下牌的编号
        Map<Integer, Integer> cardsNum = getCardNum(temp);
        //前四张牌类型
        Map<Integer,Integer> firstFourNum = getCardNum(first_four);
        
        //判断前四张是否为杠
        if(firstFourNum.containsKey(cardType) && firstFourNum.get(cardType)==4){
        	firstFourNum.remove(cardType);
        }
        //杠之后前四张是否没有
        if(firstFourNum.size()<1){
        	results = false;
        }
        //判断前四张的类型是否一样，一样则不能杠
        if(!(cardsNum.containsKey(cardType) && cardsNum.get(cardType)==3)){
        	results = false;
        }
        
        boolean isPeng = pengType.containsKey(cardType);
        
        return !isPeng && results;
    }
    
    
    /**
     * 是否有杠   一开始上来的牌，如果由有杠就杠，但是前四张不能杠
     * @return
     */
    @Override
    public boolean isHasGang() {
    	if(isTing){
    		return false;
    	}
        List<String> temp = new ArrayList<>();
        temp.addAll(cards);
        Set set = getHasGangList(temp);
        Map<Integer,Integer> firstFourNum = getCardNum(first_four);
        return set.size()>0 && firstFourNum.size()!=1;
    }
	
    /**
     * 碰
	 * @param card
     * @param playUser 碰的谁的牌
	 */
    public void peng(String card, long playUser) {
        super.peng(card, playUser);
        List<String> temp = new ArrayList<>();
		for(String cardLs:first_four){
			if(CardTypeUtil.cardType.get(cardLs)==(int)CardTypeUtil.cardType.get(card)){
				temp.add(cardLs);
			}
		}
		first_four.removeAll(temp);
    }
    /**
     * 听
     */
    @Override
	public void ting(String card) {
		super.ting(card);
		 if(first_four.contains(card)){
	        	first_four.remove(card);
	        }
	}

    /**
     * 手里杠
     */
	@Override
	public boolean gang_hand(RoomInfo room, GameInfo info, long diangangUser,
							 String card) {
		boolean r = super.gang_hand(room, info, diangangUser, card);
		List<String> temp = new ArrayList<>();
		for(String cardLs:first_four){
			if(CardTypeUtil.cardType.get(cardLs)==(int)CardTypeUtil.cardType.get(card)){
				temp.add(cardLs);
			}
		}
		first_four.removeAll(temp);
		return r;
		
	}
	/**
	 * 点杠
	 */
	@Override
	public boolean gang_discard(RoomInfo room, GameInfo gameInfo,
								long diangangUser, String disCard) {
		boolean r = super.gang_discard(room, gameInfo, diangangUser, disCard);
		List<String> temp = new ArrayList<>();
		for(String cardLs:first_four){
			if(CardTypeUtil.cardType.get(cardLs)==(int)CardTypeUtil.cardType.get(disCard)){
				temp.add(cardLs);
			}
		}
		first_four.removeAll(temp);
		return r;
		
	}

	/**
     * 前4张牌不能打出，只有报听才能打出，并且报听必须打出四张其中一张
     * 检测出牌是否合法
     */
    @Override
	public boolean checkPlayCard(String card) {
		boolean results = true;
		if(!super.checkPlayCard(card)){
			return false;
		}
		
		if(isTing){
			results = card.equals(catchCard);
		}else{
			for(int i=0;i<first_four.size();i++){
				if(first_four.get(i).equals(card)){
					results = false;
					break;
				}
			}
		}
		return results;
	}
	
	
	
	
	/**
	 * 根据杠判断留下多少张牌
	 * @return
	 */
	public int leavefourteen(GameInfo info){
		int Leave_card = 14;
		int size = anGangType.size()+mingGangType.size();
		if(size!=0){
			if(size % 2 ==0){
				Leave_card = (size+6)*2;
			}else{
				Leave_card = (size+6)*2+1;
			}
		}
		return Leave_card;
	}
	
	
	
	
	public List<String> getFirst_four() {
		return first_four;
	}


	public void setFirst_four(List<String> first_four) {
		this.first_four = first_four;
	}


	public static void main(String[] args) {
		List<String> lis = new ArrayList<>();
		lis.add("104");
		lis.add("005");
		lis.add("006");
		lis.add("008");
		lis.add("009");
		lis.add("010");
		lis.add("011");
		
		lis.add("032"); 
		lis.add("033");
		
		lis.add("045");
		lis.add("049");
		lis.add("053");
		lis.add("057");
		lis.add("061");
		lis.add("041");
		
		PlayerCardsInfoLS PlayerCardsInfo = new PlayerCardsInfoLS();
		

//        String[] s = new String[]{"064","051","097","132","045","067","101","133","092","065","042","124","135"};
//        String[] s = new String[]{"064","051","097","132","045","067","101","133","092","065","134","042","124","135"};
        String[] s = new String[]{"100", "087", "097", "095", "103", "105", "072", "079", "080", "091", "078", "101", "076", "102"};

        PlayerCardsInfo.cards = new ArrayList<>();
        List<String> ls = new ArrayList<>();
        ls.add("104");
        ls.add("130");
        PlayerCardsInfo.setFirst_four(ls);
        List<String> list = Arrays.asList(s);
        PlayerCardsInfo.cards.addAll(list);
        PlayerCardsInfo.pengType.put(22,1L);
        
        
        System.out.println(PlayerCardsInfo.isCanTing(PlayerCardsInfo.getCards()));
        
		
	}
	
}

