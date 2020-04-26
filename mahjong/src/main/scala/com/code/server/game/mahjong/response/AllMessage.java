package com.code.server.game.mahjong.response;


import com.code.server.constant.response.UserVo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 断线重连的方法
 * @author Administrator
 *
 */
public class AllMessage {

	private boolean isExist;

	private Map<String,Object> room;



	private List<UserVo> users;

//	private ArrayList<CardMessage> cardMessages;
	
	private Map<Long, Integer> userStatus = new HashMap<>();//用户状态

	private Map<Long, Boolean> offlineStatus = new HashMap<>();//在线状态

	private Map<Long, Double> userScores = new HashMap<>();
	
	private ReconnectResp reconnectResp;

	private int gameId;

	private long banker;
	private int curGameNumber;
	private int cardNumber;
	private int circleNum;
	private long remainTime = -1;

	private Map<Long, Integer> laZhuang = new HashMap<>();
	private Map<Long, Boolean> laZhuangStatus = new HashMap<>();
	private Map<Integer, Long> seatMap = new HashMap<>();


	public Map<String, Object> getRoom() {
		return room;
	}

	public AllMessage setRoom(Map<String, Object> room) {
		this.room = room;
		return this;
	}

	public List<UserVo> getUsers() {
		return users;
	}

	public AllMessage setUsers(List<UserVo> users) {
		this.users = users;
		return this;
	}

	public Map<Long, Integer> getUserStatus() {
		return userStatus;
	}

	public AllMessage setUserStatus(Map<Long, Integer> userStatus) {
		this.userStatus = userStatus;
		return this;
	}

	public Map<Long, Boolean> getOfflineStatus() {
		return offlineStatus;
	}

	public AllMessage setOfflineStatus(Map<Long, Boolean> offlineStatus) {
		this.offlineStatus = offlineStatus;
		return this;
	}

	public AllMessage setBanker(long banker) {
		this.banker = banker;
		return this;
	}

	public int getCardNumber() {
		return cardNumber;
	}

	public AllMessage setCardNumber(int cardNumber) {
		this.cardNumber = cardNumber;
		return this;
	}

	public int getGameId() {
		return gameId;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
	}



	public ReconnectResp getReconnectResp() {
		return reconnectResp;
	}

	public void setReconnectResp(ReconnectResp reconnectResp) {
		this.reconnectResp = reconnectResp;
	}


	public long getBanker() {
		return banker;
	}

	public int getCircleNum() {
		return circleNum;
	}

	public AllMessage setCircleNum(int circleNum) {
		this.circleNum = circleNum;
		return this;
	}

	public boolean isExist() {
		return isExist;
	}

	public AllMessage setExist(boolean exist) {
		isExist = exist;
		return this;
	}

	public int getCurGameNumber() {
		return curGameNumber;
	}

	public AllMessage setCurGameNumber(int curGameNumber) {
		this.curGameNumber = curGameNumber;
		return this;
	}

	public Map<Long, Double> getUserScores() {
		return userScores;
	}

	public AllMessage setUserScores(Map<Long, Double> userScores) {
		this.userScores = userScores;
		return this;
	}

	public long getRemainTime() {
		return remainTime;
	}

	public AllMessage setRemainTime(long remainTime) {
		this.remainTime = remainTime;
		return this;
	}

	public Map<Long, Integer> getLaZhuang() {
		return laZhuang;
	}

	public AllMessage setLaZhuang(Map<Long, Integer> laZhuang) {
		this.laZhuang = laZhuang;
		return this;
	}

	public Map<Long, Boolean> getLaZhuangStatus() {
		return laZhuangStatus;
	}

	public AllMessage setLaZhuangStatus(Map<Long, Boolean> laZhuangStatus) {
		this.laZhuangStatus = laZhuangStatus;
		return this;
	}

	public Map<Integer, Long> getSeatMap() {
		return seatMap;
	}

	public AllMessage setSeatMap(Map<Integer, Long> seatMap) {
		this.seatMap = seatMap;
		return this;
	}
}