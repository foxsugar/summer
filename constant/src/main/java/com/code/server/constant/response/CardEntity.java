package com.code.server.constant.response;

import net.sf.json.JSONObject;

/**
 * 实体转换为JsonObject
 * @author ly
 *
 */
public class CardEntity {

	private String shoupai;
	
	private String currentUserId;//当前人
	
	private String currentCard;//当前牌
	
	private String currentCardFrom; //当前牌的来源

	private String matter;//当前牌的事件，吃碰杠胡等
	
	private int restNumber;//当前剩余牌数

	
	public String getCurrentCardFrom() {
		return currentCardFrom;
	}

	public void setCurrentCardFrom(String currentCardFrom) {
		this.currentCardFrom = currentCardFrom;
	}

	public String getShoupai() {
		return shoupai;
	}

	public void setShoupai(String shoupai) {
		this.shoupai = shoupai;
	}

	public String getCurrentUserId() {
		return currentUserId;
	}

	public void setCurrentUserId(String currentUserId) {
		this.currentUserId = currentUserId;
	}

	public String getCurrentCard() {
		return currentCard;
	}

	public void setCurrentCard(String currentCard) {
		this.currentCard = currentCard;
	}
	
	
	public String getMatter() {
		return matter;
	}

	public void setMatter(String matter) {
		this.matter = matter;
	}
	
	

	public int getRestNumber() {
		return restNumber;
	}

	public void setRestNumber(int restNumber) {
		this.restNumber = restNumber;
	}

	public JSONObject toJSONObjectOfMopai() {
		JSONObject jSONObject = new JSONObject();
		jSONObject.put("currentUserId", this.currentUserId);
		jSONObject.put("currentCard", this.currentCard);
		jSONObject.put("restNumber", this.restNumber);
		return jSONObject;
	}
	
	public JSONObject toJSONObjectOfShoupai() {
		JSONObject jSONObject = new JSONObject();
		jSONObject.put("currentUserId", this.currentUserId);
		jSONObject.put("shoupai", this.shoupai);
		jSONObject.put("currentCard", this.currentCard);
		jSONObject.put("restNumber", this.restNumber);
		return jSONObject;
	}

	public JSONObject toJSONObjectOfMatter() {
		JSONObject jSONObject = new JSONObject();
		jSONObject.put("currentUserId", this.currentUserId);
		jSONObject.put("matter", this.matter);
		jSONObject.put("restNumber", this.restNumber);
		jSONObject.put("currentCardFrom", this.currentCardFrom);
		return jSONObject;
	}
	
	public JSONObject toJSONObjectOfMatters() {
		JSONObject jSONObject = new JSONObject();
		jSONObject.put("currentCard", this.currentCard);
		jSONObject.put("currentUserId", this.currentUserId);
		jSONObject.put("matter", this.matter);
		jSONObject.put("restNumber", this.restNumber);
		jSONObject.put("currentCardFrom", this.currentCardFrom);
		return jSONObject;
	}
	
	
	
}