package com.code.server.constant.response;

import java.util.List;

public class GameOfResult {
	
	private List<UserOfResult> userList;

	private String endTime;

	private Object other;


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


    public Object getOther() {
        return other;
    }

    public GameOfResult setOther(Object other) {
        this.other = other;
        return this;
    }
}
