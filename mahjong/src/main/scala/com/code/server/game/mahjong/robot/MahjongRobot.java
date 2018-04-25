package com.code.server.game.mahjong.robot;

import com.code.server.constant.kafka.KafkaMsgKey;
import com.code.server.constant.response.ResponseVo;
import com.code.server.game.mahjong.config.ServerConfig;
import com.code.server.game.mahjong.logic.GameInfo;
import com.code.server.game.mahjong.logic.PlayerCardsInfoMj;
import com.code.server.game.mahjong.logic.RoomInfo;
import com.code.server.kafka.MsgProducer;
import com.code.server.util.SpringUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sunxianping on 2018/4/24.
 */
public class MahjongRobot {

    private static long INTERVAL_TIME = 30000L;

    public static void execute(RoomInfo roomInfo) {
        long now = System.currentTimeMillis();
        GameInfo gameInfo = (GameInfo) roomInfo.getGame();
        if (gameInfo != null) {
            if (gameInfo.getLastOperateTime() - now > INTERVAL_TIME) {
                if (gameInfo.getWaitingforList().size() > 0) {
                    GameInfo.WaitDetail waitDetail = gameInfo.getWaitingforList().get(0);
                    guo(roomInfo, waitDetail.myUserId);
                } else {
                    if (gameInfo.getTurnId() != 0) {
                        playCard(roomInfo);
                    }
                }
            }
        } else {//在准备状态

        }
    }

    public static void guo(RoomInfo roomInfo, long userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        ResponseVo vo = new ResponseVo("gameLogicService", "next",params);
        sendRobotRequest(roomInfo, userId, vo);

    }

    public static void playCard(RoomInfo roomInfo) {
        Map<String, Object> params = new HashMap<>();
        GameInfo gameInfo = (GameInfo) roomInfo.getGame();
        long userId = gameInfo.getTurnId();
        PlayerCardsInfoMj playerCardsInfoMj = gameInfo.getPlayerCardsInfos().get(userId);
        if(playerCardsInfoMj.isMoreOneCard()){
            int cardSize = playerCardsInfoMj.getCards().size();
            String cards = playerCardsInfoMj.getCards().get(cardSize - 1);
            params.put("userId", userId);
            params.put("card", cards);
            ResponseVo vo = new ResponseVo("gameLogicService", "playCard", params);
            sendRobotRequest(roomInfo, userId, vo);
        }

    }

    public static void sendRobotRequest(RoomInfo roomInfo,long userId, ResponseVo request) {

        String roomId = roomInfo.getRoomId();
        int partition = SpringUtil.getBean(ServerConfig.class).getServerId();
        KafkaMsgKey msgKey = new KafkaMsgKey();

        msgKey.setRoomId(roomId);
        msgKey.setPartition(partition);
        msgKey.setUserId(userId);

        SpringUtil.getBean(MsgProducer.class).send2Partition(request.getService(),partition, msgKey, request);


    }


}
