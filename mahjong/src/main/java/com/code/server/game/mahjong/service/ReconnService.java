package com.code.server.game.mahjong.service;

import com.code.server.constant.game.UserBean;
import com.code.server.constant.response.ResponseVo;
import com.code.server.game.mahjong.logic.GameInfo;
import com.code.server.game.mahjong.logic.RoomInfo;
import com.code.server.game.mahjong.response.AllMessage;
import com.code.server.game.mahjong.response.ReconnectResp;
import com.code.server.game.room.MsgSender;
import com.code.server.game.room.service.RoomManager;
import com.code.server.redis.service.RedisManager;
import com.code.server.util.JsonUtil;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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


    public static int reconnection(String roomId, long userId) {
        JSONObject result = new JSONObject();


        AllMessage allMessage = new AllMessage();
        allMessage.setExist(false);

        RoomInfo roomInfo = (RoomInfo) RoomManager.getRoom(roomId);
        if (roomInfo != null) {
            List<UserBean> userList = new ArrayList<>();


            allMessage.setRoom(roomInfo);
            allMessage.setBanker(roomInfo.getBankerId());
            allMessage.setCurGameNumber(roomInfo.getCurGameNumber());
            allMessage.setUserScores(roomInfo.getUserScores());
            allMessage.setUserStatus(roomInfo.getUserStatus());
            allMessage.setCircleNum(roomInfo.getCurCircle());//圈数
            for (UserBean userBean : RedisManager.getUserRedisService().getUserBeans(roomInfo.getUsers())) {
                UserBean ub = new UserBean();
                ub.setUsername(userBean.getUsername());
                ub.setMoney(userBean.getMoney());
                userList.add(ub);
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


        result.put("params", JsonUtil.toJson(vo));
        result.put("code", "0");

        MsgSender.sendMsg2Player(vo, userId);


        return 0;

    }
}
