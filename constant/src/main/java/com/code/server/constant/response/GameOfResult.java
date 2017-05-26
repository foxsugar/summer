package com.code.server.constant.response;

import java.util.ArrayList;

public class GameOfResult {
	
	private ArrayList<UserOfResult> userList;

	private String endTime;

	public ArrayList<UserOfResult> getUserList() {
		return userList;
	}

	public void setUserList(ArrayList<UserOfResult> userList) {
		this.userList = userList;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
}
