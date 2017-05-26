package com.code.server.constant.response;


import java.util.HashMap;
import java.util.Map;

public class NoticeReady {
	
	private Map<String, Integer> userStatus = new HashMap<>();//用户状态
	
	public Map<String, Integer> getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(Map<String, Integer> userStatus) {
		this.userStatus = userStatus;
	}


	
}
