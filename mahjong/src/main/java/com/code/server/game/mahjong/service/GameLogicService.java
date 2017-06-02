package com.code.server.game.mahjong.service;


import com.code.server.game.mahjong.logic.GameInfo;
import com.code.server.game.mahjong.logic.GameInfoJiuZhou;
import com.code.server.game.mahjong.logic.RoomInfo;

import com.code.server.game.room.service.RoomManager;
import com.code.server.util.JsonUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import net.sf.json.JSONObject;

import java.util.List;
import java.util.logging.Logger;

/**
 * Created by win7 on 2016/12/1.
 */

public class GameLogicService {
    protected static final Logger logger = Logger.getLogger("game");


    public int dispatch(String method, JSONObject paramsjSONObject) {

        int gameId = paramsjSONObject.getInt("gameId");
        int userId = paramsjSONObject.getInt("userId");

        String card = "";
        if (paramsjSONObject.has("card")) {
            card = paramsjSONObject.getString("card");
        }
        int code = 0;



        String roomId = "";



        switch (method) {
            case "connection":
                break;
            case "createRoomByUser":
                break;
            case "createRoomByEachUser":
                break;
            case "createRoomButNotInRoom":
                break;

            case "playCard":
                code = playCard(roomId, userId, card);
                break;
            case "next"://过
                code = next(roomId, userId);
                break;
            case "peng":
                code = peng(roomId, userId);
                break;
            case "gang":
                code = gang(roomId, userId, card);
                break;
            case "ting":
                code = ting(roomId, userId, card);
                break;
            case "hu":
                code = hu(roomId, userId);
                break;
            case "chi":
                String one = paramsjSONObject.getString("one");
                String two = paramsjSONObject.getString("two");
                code = chi(roomId, userId, one, two);
                break;
            case "chiTing":
                String one1 = paramsjSONObject.getString("one");
                String two1 = paramsjSONObject.getString("two");
                code = chiTing(roomId, userId, one1, two1);
                break;
            case "pengTing":
                code = pengTing(roomId, userId);
                break;
            case "exchange":
                int srcType = paramsjSONObject.getInt("srcType");
                int desType = paramsjSONObject.getInt("desType");
                code = exchange(roomId, userId, srcType, desType);
                break;
            case "needCard":
                int cardType = paramsjSONObject.getInt("cardType");
                code = needCard(roomId, userId, cardType);
                break;
            case "xuanfengdan":
                int xuanfengType = paramsjSONObject.getInt("xuanfengType");
                String xuanfengCard = paramsjSONObject.getString("xuanfengCards");
                List<String> xuanfengCardList = JsonUtil.readValue(xuanfengCard, new TypeReference<List<String>>() {
                });
                code = xuanfengdan(roomId, userId, xuanfengType, xuanfengCardList);
                break;
        }
        return code;
    }


    private GameInfo getGameInfo(String roomId){
        RoomInfo roomInfo = (RoomInfo)RoomManager.getRoom(roomId);
        return roomInfo.getGameInfo();
    }
    public int playCard(String roomId, int userId, String card) {

        RoomInfo roomInfo = (RoomInfo)RoomManager.getRoom(roomId);
        GameInfo gameInfo = getGameInfo(roomId);
        if (roomInfo != null) {
            roomInfo.setCanDissloution(false);
        }
        return gameInfo.chupai(userId, card);
    }

    public int next(String roomId, int userId) {
        return getGameInfo(roomId).guo(userId);
    }

    public int peng(String roomId, int userId) {
        return getGameInfo(roomId).peng(userId);
    }

    public int gang(String roomId, int userId, String card) {

        return getGameInfo(roomId).gang(userId, card);
    }

    public int ting(String roomId, int userId, String card) {

        return getGameInfo(roomId).ting(userId, card);
    }

    public int hu(String roomId, int userId) {

        return getGameInfo(roomId).hu(userId);
    }

    public int chi(String roomId, int userId, String one, String two) {

        return getGameInfo(roomId).chi(userId, one, two);
    }

    public int chiTing(String roomId, int userId, String one, String two) {

        return getGameInfo(roomId).chiTing(userId, one, two);
    }

    public int pengTing(String roomId, int userId) {

        return getGameInfo(roomId).pengTing(userId);
    }

    public int exchange(String roomId, int userId, int srcType, int desType) {

        return getGameInfo(roomId).exchange(userId, srcType, desType);
    }

    public int needCard(String roomId, int userId, int cardType) {

        return getGameInfo(roomId).needCard(userId, cardType);
    }

    public int xuanfengdan(String roomId, int userId, int xuanfengType, List<String> xuanfengCard) {

        return ((GameInfoJiuZhou)getGameInfo(roomId)).xuanfengdan(serverContext, userId, xuanfengType, xuanfengCard);
    }

}
