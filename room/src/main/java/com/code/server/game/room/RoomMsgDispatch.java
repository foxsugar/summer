package com.code.server.game.room;

import com.code.server.constant.kafka.KafkaMsgKey;
import com.code.server.constant.response.ErrorCode;
import com.code.server.game.room.service.RoomManager;
import com.code.server.game.room.kafka.MsgSender;
import com.code.server.util.JsonUtil;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Service;

/**
 * Created by sunxianping on 2017/5/23.
 */
@Service
public class RoomMsgDispatch {


    public static void dispatch(ConsumerRecord<String, String> record) {
        try {

            String keyValue = record.key();

            String valueStr = record.value();
            KafkaMsgKey msgKey = JsonUtil.readValue(keyValue, KafkaMsgKey.class);


            JsonNode msgValue = JsonUtil.readTree(valueStr);

            String service = msgValue.get("service").asText();
            String method = msgValue.get("method").asText();
            JsonNode params = msgValue.get("params");

            String roomId = msgKey.getRoomId();
            long userId = msgKey.getUserId();
            int code = dispatchRoomService(method, params, userId, roomId);
            if (code != 0) {
                MsgSender.sendMsg2Player(service, method, code, userId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private static int dispatchRoomService(String method, JsonNode params, long userId, String roomId) {


        switch (method) {
            case "joinRoom": {
                IfaceRoom room = RoomManager.getRoom(roomId);
                if (room == null) {
                    return ErrorCode.CANNOT_JOIN_ROOM_NOT_EXIST;
                }
                return room.joinRoom(userId, true);
            }

            case "dissolutionRoom": {
                IfaceRoom room = RoomManager.getRoom(roomId);
                if (room == null) {
                    return ErrorCode.CANNOT_JOIN_ROOM_NOT_EXIST;
                }
                return room.dissolutionRoom(userId);

            }
            case "quitRoom": {
                IfaceRoom room = RoomManager.getRoom(roomId);
                if (room == null) {
                    return ErrorCode.CAN_NOT_NO_ROOM;
                }
                return room.quitRoom(userId);
            }
            case "getReady": {
                IfaceRoom room = RoomManager.getRoom(roomId);
                if (room == null) {
                    return ErrorCode.CAN_NOT_NO_ROOM;
                }
                return room.getReady(userId);
            }
            case "dissolveRoom": {
                IfaceRoom room = RoomManager.getRoom(roomId);
                if (room == null) {
                    return ErrorCode.CAN_NOT_NO_ROOM;
                }
                return room.dissolution(userId, true, method);
            }
            case "answerIfDissolveRoom": {

                IfaceRoom room = RoomManager.getRoom(roomId);
                if (room == null) {
                    return ErrorCode.CAN_NOT_NO_ROOM;
                }
                boolean isAgree = "2".equals(params.get("answer").asText());
                return room.dissolution(userId, isAgree, method);
            }
            case "startGameByClient": {
                IfaceRoom room = RoomManager.getRoom(roomId);
                if (room == null) {
                    return ErrorCode.CAN_NOT_NO_ROOM;
                }
                return room.startGameByClient(userId);

            }

            case "startTTZGameByClient": {
                System.out.println("++++++++++---------------RoomMsgDispatch+startTTZGameByClient");
                IfaceRoom room = RoomManager.getRoom(roomId);
                if (room == null) {
                    return ErrorCode.CAN_NOT_NO_ROOM;
                }
                return room.startGameByClient(userId);

            }

            case "getPrepareRoom": {
                IfaceRoom room = RoomManager.getRoom(roomId);
                return room.getPrepareRoom(userId);
            }
            case "getRoomClubByUser": {
                IfaceRoom room = RoomManager.getRoom(roomId);
                return room.getRoomClubByUser(userId);
            }
            case "pushScoreChange": {
                IfaceRoom room = RoomManager.getRoom(roomId);
                Room r = (Room) room;
                if (r.isGoldRoom()) {
                    r.pushScoreChange();
                }
                return 0;
            }
            case "changeRoom": {
                IfaceRoom room = RoomManager.getRoom(roomId);
                Room r = (Room) room;
                return r.changeRoom(userId);
            }

            case "startAuto": {
                IfaceRoom room = RoomManager.getRoom(roomId);
                Room r = (Room) room;
                if (r.isAllReady() && r.getUsers().size() >= 2) {
                    r.startGame();
                }
                return 0;
            }

            case "joinRoomWatch": {
                IfaceRoom room = RoomManager.getRoom(roomId);
                Room r = (Room) room;
                return r.joinRoomWatch(userId);
            }

            case "quitRoomWatch": {
                IfaceRoom room = RoomManager.getRoom(roomId);
                Room r = (Room) room;
                return r.quitRoomWatch(userId);
            }

            case "getWatchUserInfo": {
                IfaceRoom room = RoomManager.getRoom(roomId);
                Room r = (Room) room;
                return r.getWatchUserInfo(userId);
            }
            case "kickPlayer": {
                IfaceRoom room = RoomManager.getRoom(roomId);
                Room r = (Room) room;
                long kickUser = params.path("kickUser").asLong();
                return r.kickPlayer(userId, kickUser);
            }

            case "getRoomInfo": {
                IfaceRoom room = RoomManager.getRoom(roomId);
                Room r = (Room) room;
                return r.getRoomInfo(userId);
            }

            case "setAutoStatus":{
                IfaceRoom room = RoomManager.getRoom(roomId);
                Room r = (Room) room;
                int status = params.path("status").asInt();
                return r.setAutoStatus(userId,status);
            }


            default:
                return ErrorCode.REQUEST_PARAM_ERROR;
        }
    }
}
