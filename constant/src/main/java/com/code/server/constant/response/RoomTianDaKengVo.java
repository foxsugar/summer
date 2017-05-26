package com.code.server.constant.response;

import com.code.server.cardgame.core.GameManager;
import com.code.server.cardgame.core.Player;
import com.code.server.cardgame.core.tiandakeng.RoomTanDaKeng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ClarkKent on 2017/3/15.
 */
public class RoomTianDaKengVo {


    protected String roomId;
    protected double multiple;//倍数
    protected int gameNumber;
    protected long createUser;
    private GameVo game;
    private int curGameNumber;
    private int personNumber;
    protected int hasNine;


    private boolean isLastDraw = false;//是否平局
    private int drawForLeaveChip = 0;//平局留下筹码

    protected Map<Long, Integer> userStatus = new HashMap<>();//用户状态
    protected List<UserVo> userList = new ArrayList<>();//用户列表
    protected Map<Long, Double> userScores = new HashMap<>();


    public RoomTianDaKengVo(){}

    public RoomTianDaKengVo(RoomTanDaKeng roomTianDaKeng, Player player){
        this.roomId = roomTianDaKeng.getRoomId();
        this.multiple = roomTianDaKeng.getMultiple();
        this.gameNumber = roomTianDaKeng.getGameNumber();
        this.createUser = roomTianDaKeng.getCreateUser();
        this.userStatus.putAll(roomTianDaKeng.getUserStatus());
        this.userScores.putAll(roomTianDaKeng.getUserScores());
        this.curGameNumber = roomTianDaKeng.getCurGameNumber();
        this.isLastDraw = roomTianDaKeng.isLastDraw();
        this.drawForLeaveChip = roomTianDaKeng.getDrawForLeaveChip();
        this.personNumber = roomTianDaKeng.getPersonNumber();
        this.hasNine = roomTianDaKeng.getHasNine();

        for(long uid : roomTianDaKeng.getUsers()){
            userList.add(GameManager.getUserVo(roomTianDaKeng.getUserMap().get(uid)));
        }

        if(roomTianDaKeng.getGame()!=null){
            this.game = GameTianDaKengVo.getGameTianDaKengVo(roomTianDaKeng.getGame(),player.getUserId());
        }

    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public double getMultiple() {
        return multiple;
    }

    public void setMultiple(double multiple) {
        this.multiple = multiple;
    }

    public int getGameNumber() {
        return gameNumber;
    }

    public void setGameNumber(int gameNumber) {
        this.gameNumber = gameNumber;
    }

    public long getCreateUser() {
        return createUser;
    }

    public void setCreateUser(long createUser) {
        this.createUser = createUser;
    }

    public GameVo getGame() {
        return game;
    }

    public void setGame(GameVo game) {
        this.game = game;
    }

    public int getCurGameNumber() {
        return curGameNumber;
    }

    public void setCurGameNumber(int curGameNumber) {
        this.curGameNumber = curGameNumber;
    }

    public boolean isLastDraw() {
        return isLastDraw;
    }

    public void setLastDraw(boolean lastDraw) {
        isLastDraw = lastDraw;
    }

    public int getDrawForLeaveChip() {
        return drawForLeaveChip;
    }

    public void setDrawForLeaveChip(int drawForLeaveChip) {
        this.drawForLeaveChip = drawForLeaveChip;
    }

    public Map<Long, Integer> getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(Map<Long, Integer> userStatus) {
        this.userStatus = userStatus;
    }

    public List<UserVo> getUserList() {
        return userList;
    }

    public void setUserList(List<UserVo> userList) {
        this.userList = userList;
    }

    public Map<Long, Double> getUserScores() {
        return userScores;
    }

    public void setUserScores(Map<Long, Double> userScores) {
        this.userScores = userScores;
    }

    public int getPersonNumber() {
        return personNumber;
    }

    public void setPersonNumber(int personNumber) {
        this.personNumber = personNumber;
    }

    public int getHasNine() {
        return hasNine;
    }

    public void setHasNine(int hasNine) {
        this.hasNine = hasNine;
    }
}
