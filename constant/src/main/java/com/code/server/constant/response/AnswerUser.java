package com.code.server.constant.response;

import net.sf.json.JSONObject;

public class AnswerUser {
	
	private String userId;
	
	private String answer;
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public JSONObject toJSONObject() {
		JSONObject jSONObject = new JSONObject();
		jSONObject.put("userId", this.userId);
		jSONObject.put("answer", this.answer);
		return jSONObject;
	}
	
}
