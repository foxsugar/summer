package com.code.server.constant.response;

import net.sf.json.JSONObject;

import java.util.ArrayList;

public class AskQuitRoom {
	
	private String userId;
	
	private String note;
	
	private ArrayList<AnswerUser> answerList;
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public ArrayList<AnswerUser> getAnswerList() {
		return answerList;
	}

	public void setAnswerList(ArrayList<AnswerUser> answerList) {
		this.answerList = answerList;
	}

	public JSONObject toJSONObject() {
		JSONObject jSONObject = new JSONObject();
		jSONObject.put("userId", this.userId);
		jSONObject.put("note", this.note);
		jSONObject.put("answerList", this.answerList);
		return jSONObject;
	}
	
}
