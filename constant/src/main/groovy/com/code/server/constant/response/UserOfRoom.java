package com.code.server.constant.response;

import java.util.List;

public class UserOfRoom {
    
    //房间人数
    private int inRoomNumber;
    //准备好的人数
    private int readyNumber;
    
    private List<UserVo> userList;
    
    
    
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

	public void setUserList(List<UserVo> userList) {
		this.userList = userList;
	}



}