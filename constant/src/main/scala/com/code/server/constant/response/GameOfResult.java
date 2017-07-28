package com.code.server.constant.response;

import java.util.List;

public class GameOfResult {
	
	private List<UserOfResult> userList;

	private String endTime;

	public List<UserOfResult> getUserList() {
		return userList;
	}

	public GameOfResult setUserList(List<UserOfResult> userList) {
		this.userList = userList;
		return this;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
}
