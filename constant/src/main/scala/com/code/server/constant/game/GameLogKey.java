package com.code.server.constant.game;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/6/3.
 */
public class GameLogKey {

    private String roomType;
    private String gameType;
    private int goldRoomType;
    private int goldRoomPermission;
    private int gameNumber;
    private Map<String,String> params = new HashMap<>();

    public String getRoomType() {
        return roomType;
    }

    public GameLogKey setRoomType(String roomType) {
        this.roomType = roomType;
        return this;
    }

    public String getGameType() {
        return gameType;
    }

    public GameLogKey setGameType(String gameType) {
        this.gameType = gameType;
        return this;
    }

    public int getGoldRoomType() {
        return goldRoomType;
    }

    public GameLogKey setGoldRoomType(int goldRoomType) {
        this.goldRoomType = goldRoomType;
        return this;
    }

    public int getGoldRoomPermission() {
        return goldRoomPermission;
    }

    public GameLogKey setGoldRoomPermission(int goldRoomPermission) {
        this.goldRoomPermission = goldRoomPermission;
        return this;
    }



    public int getGameNumber() {
        return gameNumber;
    }

    public GameLogKey setGameNumber(int gameNumber) {
        this.gameNumber = gameNumber;
        return this;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public GameLogKey setParams(Map<String, String> params) {
        this.params = params;
        return this;
    }
}
