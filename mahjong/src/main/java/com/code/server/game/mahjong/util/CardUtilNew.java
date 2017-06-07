/**  
 * @Title: Card.java
 * @Package com.milan.majiang
 * @Description: 
 * @author Clark
 * @date 2016年9月19日 下午10:42:49
 * @Version 1.0 
 */
package com.code.server.game.mahjong.util;


import com.code.server.game.mahjong.logic.CardTypeUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * ClassName: Card
 * 
 * @Description:
 * @author Clark
 * @date 2016年9月19日 下午10:42:49
 * @Version 1.0
 * 
 */
public class CardUtilNew {
	
	/**
	* @Title: 去掉某个字段中的手牌
	* @Creater: Clark  
	* @param @param cardType
	* @param @param cardTypeNumber
	* @param @return    设定文件
	* @return Map<String,Integer>    返回类型
	* @throws
	 */
	public static ArrayList<String> get(ArrayList<String> cards,int cardTypeNumber){
		ArrayList<String> newCards = new ArrayList<>();
		for (String string : cards) {
			if(CardTypeUtil.cardType.get(string).equals(cardTypeNumber)){
				newCards.add(string);
			}
		}
		for (String string : newCards) {
			cards.remove(string);
		}
		return cards;
	}
	
	/**
	* @Title: list转String
	* @Creater: Clark  
	* @Description: 
	* @param @param cards
	* @param @return    设定文件
	* @return String    返回类型
	* @throws
	 */
	public static String listToString(List<String> cards){
		StringBuffer sb = new StringBuffer();
		for (String string : cards) {
			sb.append(string);
			sb.append(",");
		}
		return sb.toString().substring(0,sb.length()-1);
	}
}
