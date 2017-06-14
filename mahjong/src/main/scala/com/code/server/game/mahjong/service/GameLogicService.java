package com.code.server.game.mahjong.service;


import com.code.server.game.mahjong.logic.GameInfo;
import com.code.server.game.mahjong.logic.RoomInfo;
import com.code.server.game.room.IfaceRoom;
import com.code.server.game.room.MsgSender;
import com.code.server.game.room.service.RoomManager;
import com.code.server.util.JsonUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.logging.Logger;

/**
 * Created by win7 on 2016/12/1.
 */

public class GameLogicService {
    protected static final Logger logger = Logger.getLogger("game");


    public static int dispatch(long userId, String method, String roomId, JsonNode paramsjSONObject) {


        String card = "";
        if (paramsjSONObject.has("card")) {
            card = paramsjSONObject.get("card").asText();
        }
        int code = 0;

        switch (method) {
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
                String one = paramsjSONObject.get("one").asText();
                String two = paramsjSONObject.get("two").asText();
                code = chi(roomId, userId, one, two);
                break;
            case "chiTing":
                String one1 = paramsjSONObject.get("one").asText();
                String two1 = paramsjSONObject.get("two").asText();
                code = chiTing(roomId, userId, one1, two1);
                break;
            case "pengTing":
                code = pengTing(roomId, userId);
                break;
            case "exchange":
                int srcType = paramsjSONObject.get("srcType").asInt();
                int desType = paramsjSONObject.get("desType").asInt();
                code = exchange(roomId, userId, srcType, desType);
                break;
            case "needCard":
                int cardType = paramsjSONObject.get("cardType").asInt();
                code = needCard(roomId, userId, cardType);
                break;
            case "xuanfengdan":
                int xuanfengType = paramsjSONObject.get("xuanfengType").asInt();
                String xuanfengCard = paramsjSONObject.get("xuanfengCards").asText();
                List<String> xuanfengCardList = JsonUtil.readValue(xuanfengCard, new TypeReference<List<String>>() {
                });
//                code = xuanfengdan(roomId, userId, xuanfengType, xuanfengCardList);
                break;
        }
        if (code == 0) {
            MsgSender.sendMsg2Player("gameLogicService",method,code, userId);
        }
        return code;
    }


    private static GameInfo getGameInfo(String roomId) {
        IfaceRoom room = RoomManager.getRoom(roomId);
        if (room != null) {
            return (GameInfo) room.getGame();
        }
        return null;

    }

    public static int playCard(String roomId, long userId, String card) {

        RoomInfo roomInfo = (RoomInfo) RoomManager.getRoom(roomId);
        GameInfo gameInfo = getGameInfo(roomId);
        if (roomInfo != null) {
            roomInfo.setCanDissloution(false);
        }
        return gameInfo.chupai(userId, card);
    }

    public static int next(String roomId, long userId) {
        return getGameInfo(roomId).guo(userId);
    }

    public static int peng(String roomId, long userId) {
        return getGameInfo(roomId).peng(userId);
    }

    public static int gang(String roomId, long userId, String card) {

        return getGameInfo(roomId).gang(userId, card);
    }

    public static int ting(String roomId, long userId, String card) {

        return getGameInfo(roomId).ting(userId, card);
    }

    public static int hu(String roomId, long userId) {

        return getGameInfo(roomId).hu(userId);
    }

    public static int chi(String roomId, long userId, String one, String two) {

        return getGameInfo(roomId).chi(userId, one, two);
    }

    public static int chiTing(String roomId, long userId, String one, String two) {

        return getGameInfo(roomId).chiTing(userId, one, two);
    }

    public static int pengTing(String roomId, long userId) {

        return getGameInfo(roomId).pengTing(userId);
    }

    public static int exchange(String roomId, long userId, int srcType, int desType) {

        return getGameInfo(roomId).exchange(userId, srcType, desType);
    }

    public static int needCard(String roomId, long userId, int cardType) {

        return getGameInfo(roomId).needCard(userId, cardType);
    }

    public int xuanfengdan(String roomId, long userId, int xuanfengType, List<String> xuanfengCard) {

//        return (GameInfoJiuZhou)getGameInfo(roomId).xuanfengdan(serverContext, userId, xuanfengType, xuanfengCard);
        return 0;
    }

}
