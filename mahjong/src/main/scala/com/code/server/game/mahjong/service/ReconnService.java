package com.code.server.game.mahjong.service;

import com.code.server.constant.game.UserBean;
import com.code.server.constant.response.ResponseVo;
import com.code.server.constant.response.UserVo;
import com.code.server.game.mahjong.logic.GameInfo;
import com.code.server.game.mahjong.logic.RoomInfo;
import com.code.server.game.mahjong.response.AllMessage;
import com.code.server.game.mahjong.response.ReconnectResp;
import com.code.server.game.room.kafka.MsgSender;
import com.code.server.game.room.service.RoomManager;
import com.code.server.redis.service.RedisManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sunxianping on 2017/6/5.
 */
public class ReconnService {
    public static int dispatch(long userId, String method, String roomId) {
        switch (method) {
            case "reconnection":
                return reconnection(roomId, userId);
            default:
                return -1;
        }
    }

    private static Map<String,Object> getRoomInfo(RoomInfo roomInfo){
        Map<String, Object> result = new HashMap<>();
        result.put("isInGame", roomInfo.isInGame());
        result.put("personNumber", roomInfo.getPersonNumber());
        result.put("roomId", roomInfo.getRoomId());
        result.put("modeTotal", roomInfo.getModeTotal());
        result.put("mode", roomInfo.getMode());
        result.put("multiple", roomInfo.getMultiple());
        result.put("gameNumber", roomInfo.getGameNumber());
        result.put("createUser", roomInfo.getCreateUser());
        result.put("each", roomInfo.getEach());

        return result;
    }

    public static int reconnection(String roomId, long userId) {
        AllMessage allMessage = new AllMessage();
        allMessage.setExist(false);
        RoomInfo roomInfo = (RoomInfo) RoomManager.getRoom(roomId);
        if (roomInfo != null) {
            allMessage.setExist(true);
            List<UserVo> userList = new ArrayList<>();
            allMessage.setRoom(getRoomInfo(roomInfo));
            allMessage.setBanker(roomInfo.getBankerId());
            allMessage.setCurGameNumber(roomInfo.getCurGameNumber());
            allMessage.setUserScores(roomInfo.getUserScores());
            allMessage.setUserStatus(roomInfo.getUserStatus());
            allMessage.setCircleNum(roomInfo.getCurCircle());//圈数
            for (UserBean userBean : RedisManager.getUserRedisService().getUserBeans(roomInfo.getUsers())) {
                userList.add(userBean.toVo());
            }
            GameInfo gameInfo = (GameInfo) roomInfo.getGame();
            if (gameInfo != null) {
                roomInfo.getReady(userId);

                ReconnectResp reconnect = new ReconnectResp(gameInfo, userId);
                allMessage.setGameId(gameInfo.getGameId());
                allMessage.setCardNumber(gameInfo.getRemainCards().size());
                allMessage.setReconnectResp(reconnect);
            } else {
                //
                roomInfo.getReady(userId);

            }
            allMessage.setUsers(userList);
        }
        ResponseVo vo = new ResponseVo("reconnService", "reconnection", allMessage);
        MsgSender.sendMsg2Player(vo, userId);


        return 0;

    }
}
