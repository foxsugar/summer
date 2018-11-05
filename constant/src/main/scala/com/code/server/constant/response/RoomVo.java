package com.code.server.constant.response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sunxianping on 2017/3/15.
 */
public class RoomVo implements IfaceRoomVo {


    public String roomType;
    public String roomId;
    public int multiple;//倍数
    public int gameNumber;
    public long createUser;
    public IfaceGameVo game;
    public int curGameNumber;
    public int createType;
    public int goldRoomType;
    public int goldRoomPermission;
    public String gameType;

    //填大坑专用
    public boolean isLastDraw;//是否平局
    public int drawForLeaveChip;//平局留下筹码
    public int hasNine;

    public Map<Long, Integer> userStatus = new HashMap<>();//用户状态
    public List<UserVo> userList = new ArrayList<>();//用户列表
    public Map<Long, Double> userScores = new HashMap<>();
    public List<Long> watchUser = new ArrayList<>();
    public int personNumber;
    public boolean isAA;
    public boolean isCreaterJoin;
    public long remainTime = -1;
    public long dissloutionUser = -1;
    public boolean isOpen;

    public int mustZimo = 0;
    public boolean showChat;
    public int chip;
    public String clubId;
    public String clubRoomModel;

    //牌九金币
    public int isGold;
    public int goldType;

    public int otherMode;

    public long canStartUserId;//代建房专用，告诉谁可以手动开始

    public RoomVo() {
    }

    public int getMustZimo() {
        return mustZimo;
    }

    public void setMustZimo(int mustZimo) {
        this.mustZimo = mustZimo;
    }

    public String getRoomType() {
        return roomType;
    }

    public RoomVo setRoomType(String roomType) {
        this.roomType = roomType;
        return this;
    }

    public String getRoomId() {
        return roomId;
    }

    public RoomVo setRoomId(String roomId) {
        this.roomId = roomId;
        return this;
    }

    public int getMultiple() {
        return multiple;
    }

    public RoomVo setMultiple(int multiple) {
        this.multiple = multiple;
        return this;
    }

    public int getGameNumber() {
        return gameNumber;
    }

    public RoomVo setGameNumber(int gameNumber) {
        this.gameNumber = gameNumber;
        return this;
    }

    public long getCreateUser() {
        return createUser;
    }

    public RoomVo setCreateUser(long createUser) {
        this.createUser = createUser;
        return this;
    }


    public IfaceGameVo getGame() {
        return game;
    }

    public RoomVo setGame(IfaceGameVo game) {
        this.game = game;
        return this;
    }

    public int getCurGameNumber() {
        return curGameNumber;
    }

    public RoomVo setCurGameNumber(int curGameNumber) {
        this.curGameNumber = curGameNumber;
        return this;
    }

    public int getCreateType() {
        return createType;
    }

    public RoomVo setCreateType(int createType) {
        this.createType = createType;
        return this;
    }

    public int getGoldRoomType() {
        return goldRoomType;
    }

    public RoomVo setGoldRoomType(int goldRoomType) {
        this.goldRoomType = goldRoomType;
        return this;
    }

    public String getGameType() {
        return gameType;
    }

    public RoomVo setGameType(String gameType) {
        this.gameType = gameType;
        return this;
    }

    public boolean isLastDraw() {
        return isLastDraw;
    }

    public RoomVo setLastDraw(boolean lastDraw) {
        isLastDraw = lastDraw;
        return this;
    }

    public int getDrawForLeaveChip() {
        return drawForLeaveChip;
    }

    public RoomVo setDrawForLeaveChip(int drawForLeaveChip) {
        this.drawForLeaveChip = drawForLeaveChip;
        return this;
    }

    public int getHasNine() {
        return hasNine;
    }

    public RoomVo setHasNine(int hasNine) {
        this.hasNine = hasNine;
        return this;
    }

    public Map<Long, Integer> getUserStatus() {
        return userStatus;
    }

    public RoomVo setUserStatus(Map<Long, Integer> userStatus) {
        this.userStatus = userStatus;
        return this;
    }

    public List<UserVo> getUserList() {
        return userList;
    }

    public RoomVo setUserList(List<UserVo> userList) {
        this.userList = userList;
        return this;
    }

    public Map<Long, Double> getUserScores() {
        return userScores;
    }

    public RoomVo setUserScores(Map<Long, Double> userScores) {
        this.userScores = userScores;
        return this;
    }

    public int getPersonNumber() {
        return personNumber;
    }

    public RoomVo setPersonNumber(int personNumber) {
        this.personNumber = personNumber;
        return this;
    }

    public boolean isAA() {
        return isAA;
    }

    public RoomVo setAA(boolean AA) {
        isAA = AA;
        return this;
    }

    public boolean isCreaterJoin() {
        return isCreaterJoin;
    }

    public RoomVo setCreaterJoin(boolean createrJoin) {
        isCreaterJoin = createrJoin;
        return this;
    }

    public long getRemainTime() {
        return remainTime;
    }

    public RoomVo setRemainTime(long remainTime) {
        this.remainTime = remainTime;
        return this;
    }

    public long getDissloutionUser() {
        return dissloutionUser;
    }

    public RoomVo setDissloutionUser(long dissloutionUser) {
        this.dissloutionUser = dissloutionUser;
        return this;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public RoomVo setOpen(boolean open) {
        isOpen = open;
        return this;
    }



    public long getCanStartUserId() {
        return canStartUserId;
    }

    public void setCanStartUserId(long canStartUserId) {
        this.canStartUserId = canStartUserId;
    }

    public boolean isShowChat() {
        return showChat;
    }

    public RoomVo setShowChat(boolean showChat) {
        this.showChat = showChat;
        return this;
    }

    public int getChip() {
        return chip;
    }

    public RoomVo setChip(int chip) {
        this.chip = chip;
        return this;
    }

    public String getClubId() {
        return clubId;
    }

    public RoomVo setClubId(String clubId) {
        this.clubId = clubId;
        return this;
    }

    public String getClubRoomModel() {
        return clubRoomModel;
    }

    public RoomVo setClubRoomModel(String clubRoomModel) {
        this.clubRoomModel = clubRoomModel;
        return this;
    }

    public int getIsGold() {
        return isGold;
    }

    public void setIsGold(int isGold) {
        this.isGold = isGold;
    }

    public int getGoldType() {
        return goldType;
    }

    public void setGoldType(int goldType) {
        this.goldType = goldType;
    }

    public int getGoldRoomPermission() {
        return goldRoomPermission;
    }

    public RoomVo setGoldRoomPermission(int goldRoomPermission) {
        this.goldRoomPermission = goldRoomPermission;
        return this;
    }

    public int getOtherMode() {
        return otherMode;
    }

    public RoomVo setOtherMode(int otherMode) {
        this.otherMode = otherMode;
        return this;
    }

    public List<Long> getWatchUser() {
        return watchUser;
    }

    public RoomVo setWatchUser(List<Long> watchUser) {
        this.watchUser = watchUser;
        return this;
    }
}
