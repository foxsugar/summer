package com.code.server.game.mahjong.service;


import com.code.server.game.mahjong.logic.GameInfo;
import com.code.server.game.mahjong.logic.GameInfoJiuZhou;
import com.code.server.game.mahjong.logic.RoomInfo;
import com.code.server.game.mahjong.response.ResponseVo;
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
        String roomId = GameManager.getInstance().getUserRoom().get(userId);


                switch (method) {

                    case "playCard":
                        code = playCard(gameId, userId, card);
                        break;
                    case "next"://过
                        code = next(gameId, userId);
                        break;
                    case "peng":
                        code = peng(gameId, userId);
                        break;
                    case "gang":
                        code = gang(gameId, userId, card);
                        break;
                    case "ting":
                        code = ting(gameId, userId, card);
                        break;
                    case "hu":
                        code = hu(gameId, userId);
                        break;
                    case "chi":
                        String one = paramsjSONObject.getString("one");
                        String two = paramsjSONObject.getString("two");
                        code = chi(gameId,userId,one,two);
                        break;
                    case "chiTing":
                        String one1 = paramsjSONObject.getString("one");
                        String two1 = paramsjSONObject.getString("two");
                        code = chiTing(gameId, userId, one1, two1);
                        break;
                    case "pengTing":
                        code = pengTing(gameId, userId);
                        break;
                    case "exchange":
                        int srcType = paramsjSONObject.getInt("srcType");
                        int desType = paramsjSONObject.getInt("desType");
                        code = exchange(gameId, userId, srcType, desType);
                        break;
                    case "needCard":
                        int cardType = paramsjSONObject.getInt("cardType");
                        code = needCard(gameId, userId, cardType);
                        break;
                    case "xuanfengdan":
                        int xuanfengType = paramsjSONObject.getInt("xuanfengType");
                        String xuanfengCard = paramsjSONObject.getString("xuanfengCards");
                        List<String> xuanfengCardList = JsonUtil.readValue(xuanfengCard, new TypeReference<List<String>>() {});
                        code = xuanfengdan(gameId,userId,xuanfengType,xuanfengCardList);
                        break;
        }
        return code;
    }

    public int playCard(int gameId, int userId, String card) {
        Game game = GameManager.getInstance().getGame(gameId);
        if (game == null) {
            logger.error("===4出牌 game为null gameId : "+gameId+" 出的牌 : "+card);
        }
        GameInfo gameInfo = game.getGameInfo();
        RoomInfo roomInfo = gameInfo.getRoom();
        if (roomInfo != null) {
            roomInfo.setCanDissloution(false);
        }
        return gameInfo.chupai(serverContext, userId, card);
    }

    public int next(int gameId, int userId) {
        Game game = GameManager.getInstance().getGame(gameId);
        GameInfo gameInfo = game.getGameInfo();
        return gameInfo.guo(serverContext, userId);
    }

    public int peng(int gameId, int userId) {
        Game game = GameManager.getInstance().getGame(gameId);
        GameInfo gameInfo = game.getGameInfo();
        return gameInfo.peng(serverContext, userId);
    }

    public int gang(int gameId, int userId, String card) {
        Game game = GameManager.getInstance().getGame(gameId);
        GameInfo gameInfo = game.getGameInfo();
        return gameInfo.gang(serverContext,userId, card);
    }

    public int ting(int gameId, int userId, String card) {
        Game game = GameManager.getInstance().getGame(gameId);
        GameInfo gameInfo = game.getGameInfo();
        return gameInfo.ting(serverContext, userId, card);
    }

    public int hu(int gameId, int userId) {
        Game game = GameManager.getInstance().getGame(gameId);
        GameInfo gameInfo = game.getGameInfo();
        return gameInfo.hu(serverContext, userId);
    }

    public int chi(int gameId, int userId, String one, String two) {
        Game game = GameManager.getInstance().getGame(gameId);
        GameInfo gameInfo = game.getGameInfo();
        return gameInfo.chi(serverContext, userId,one,two);
    }

    public int chiTing(int gameId, int userId, String one, String two) {
        Game game = GameManager.getInstance().getGame(gameId);
        GameInfo gameInfo = game.getGameInfo();
        return gameInfo.chiTing(serverContext, userId,one,two);
    }
    public int pengTing(int gameId,int userId){
        Game game = GameManager.getInstance().getGame(gameId);
        GameInfo gameInfo = game.getGameInfo();
        return gameInfo.pengTing(serverContext, userId);
    }

    public int exchange(int gameId, int userId, int srcType, int desType) {
        Game game = GameManager.getInstance().getGame(gameId);
        GameInfo gameInfo = game.getGameInfo();
        return gameInfo.exchange(serverContext, userId,srcType,desType);
    }

    public int needCard(int gameId, int userId, int cardType) {
        Game game = GameManager.getInstance().getGame(gameId);
        GameInfo gameInfo = game.getGameInfo();
        return gameInfo.needCard(serverContext, userId, cardType);
    }

    public int xuanfengdan(int gameId, int userId, int xuanfengType, List<String> xuanfengCard) {
        Game game = GameManager.getInstance().getGame(gameId);
        GameInfoJiuZhou gameInfo = (GameInfoJiuZhou) game.getGameInfo();
        return gameInfo.xuanfengdan(serverContext, userId, xuanfengType,xuanfengCard);
    }

}
