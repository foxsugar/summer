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

    private static Map<String, Object> getRoomInfo(RoomInfo roomInfo) {
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
        result.put("roomType", roomInfo.getRoomType());
        result.put("gameType", roomInfo.getGameType());
        result.put("dissloutionUser", roomInfo.getDissloutionUser());//申请解散房间的人
        result.put("mustZimo",roomInfo.getMustZimo());
        result.put("yipaoduoxiang", roomInfo.isYipaoduoxiang());
        result.put("canChi", roomInfo.isCanChi());
        result.put("haveTing", roomInfo.isHaveTing());
        result.put("clubId", roomInfo.getClubId());
        result.put("clubRoomModel", roomInfo.getClubRoomModel());
        result.put("goldRoomType", roomInfo.getGoldRoomType());
        result.put("goldRoomPermission", roomInfo.getGoldRoomPermission());
        result.put("showChat", roomInfo.showChat);
        result.put("otherMode", roomInfo.getOtherMode());


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
            allMessage.getLaZhuang().putAll(roomInfo.getLaZhuang());
            allMessage.getLaZhuangStatus().putAll(roomInfo.getLaZhuangStatus());
            for (UserBean userBean : RedisManager.getUserRedisService().getUserBeans(roomInfo.getUsers())) {
                userList.add(userBean.toVo());
            }
            //在线状态
            for (long uid : roomInfo.getUsers()) {
                allMessage.getOfflineStatus().put(uid, RedisManager.getUserRedisService().getGateId(uid) != null);
            }
            GameInfo gameInfo = (GameInfo) roomInfo.getGame();
            roomInfo.getReady(userId);
            if (gameInfo != null) {
                ReconnectResp reconnect = new ReconnectResp(gameInfo, userId);
                allMessage.setGameId(gameInfo.getGameId());
                allMessage.setCardNumber(gameInfo.getRemainCards().size() - gameInfo.getNeedRemainCardNum());
                allMessage.setReconnectResp(reconnect);
            }
            allMessage.setUsers(userList);
            if (roomInfo.getTimerNode() != null) {
                long time = roomInfo.getTimerNode().getStart() + roomInfo.getTimerNode().getInterval() - System.currentTimeMillis();
                allMessage.setRemainTime(time);
            }
        }
        ResponseVo vo = new ResponseVo("reconnService", "reconnection", allMessage);
        MsgSender.sendMsg2Player(vo, userId);


        return 0;

    }
}
