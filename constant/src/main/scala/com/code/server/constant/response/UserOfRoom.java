package com.code.server.constant.response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserOfRoom {
    
    //房间人数
    private int inRoomNumber;
    //准备好的人数
    private int readyNumber;
    
    private List<UserVo> userList = new ArrayList<>();

	protected Map<Long, Double> userScores = new HashMap<>();

	protected Long canStartUserId;

    public int getInRoomNumber() {
		return inRoomNumber;
	}

	public void setInRoomNumber(int inRoomNumber) {
		this.inRoomNumber = inRoomNumber;
	}

	public int getReadyNumber() {
		return readyNumber;
	}

	public void setReadyNumber(int readyNumber) {
		this.readyNumber = readyNumber;
	}

	public List<UserVo> getUserList() {
		return userList;
	}

	public UserOfRoom setUserList(List<UserVo> userList) {
		this.userList = userList;
		return this;
	}

	public Map<Long, Double> getUserScores() {
		return userScores;
	}

	public void setUserScores(Map<Long, Double> userScores) {
		this.userScores = userScores;
	}

	public Long getCanStartUserId() {
		return canStartUserId;
	}

	public void setCanStartUserId(Long canStartUserId) {
		this.canStartUserId = canStartUserId;
	}
}