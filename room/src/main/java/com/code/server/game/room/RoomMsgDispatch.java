package com.code.server.game.room;

import com.code.server.constant.kafka.KafkaMsgKey;
import com.code.server.constant.response.ErrorCode;
import com.code.server.game.room.service.RoomManager;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;

/**
 * Created by sunxianping on 2017/5/23.
 */
@Service
public class RoomMsgDispatch {


    public void dispatchMsg(KafkaMsgKey msgKey, JsonNode msgValue){

        String service = msgValue.get("service").asText();
        String method = msgValue.get("method").asText();
        JsonNode params = msgValue.get("params");

        String roomId = msgKey.getRoomId();
        long userId = msgKey.getUserId();
        int code = dispatchRoomService(method, params, userId,roomId);
        if(code != 0){
            Player.sendMsg2Player(service,method,code,userId);
        }


    }

    private int dispatchRoomService(String method, JsonNode params, long userId,String roomId) {


        switch (method) {

//            case "createRoom":{
//
//                int gameNumber = params.getInt("gameNumber");
//                int multiple = params.getInt("maxMultiple");
//                String gameType = params.optString("gameType", "0");
//                return RoomDouDiZhu.createRoom(player, gameNumber, multiple,gameType);
//            }
//            case "createRoomTDK":{
//
//                int gameNumber = params.getInt("gameNumber");
//                double multiple = params.getDouble("maxMultiple");
//                int personNumber = params.getInt("personNumber");
//                int hasNine = params.getInt("hasNine");
//                return RoomTanDaKeng.createRoom(player, gameNumber,multiple,personNumber,hasNine);
//            }
            case "joinRoom": {

                Room room = RoomManager.getRoom(roomId);
                if (room == null) {
                    return ErrorCode.CANNOT_JOIN_ROOM_NOT_EXIST;
                }
                return room.joinRoom(userId);
            }
//            case "joinRoomQuick":{
//                double type = params.getDouble("type");
//                return GoldRoomPool.getInstance().addRoom(player, type);
//
//
//            }
            case "quitRoom": {
                Room room = RoomManager.getRoom(roomId);
                if (room == null) {
                    return ErrorCode.CAN_NOT_NO_ROOM;
                }
                return room.quitRoom(userId);
            }
            case "getReady": {
                Room room = RoomManager.getRoom(roomId);
                if (room == null) {
                    return ErrorCode.CAN_NOT_NO_ROOM;
                }
                return room.getReady(userId);
            }
            case "dissolveRoom": {
                Room room = RoomManager.getRoom(roomId);
                if (room == null) {
                    return ErrorCode.CAN_NOT_NO_ROOM;
                }
                return room.dissolution(userId, true, method);
            }
            case "answerIfDissolveRoom":
                Room room = RoomManager.getRoom(roomId);
                if (room == null) {
                    return ErrorCode.CAN_NOT_NO_ROOM;
                }
                boolean isAgree = "2".equals(params.get("answer").asText());
                return room.dissolution(userId, isAgree, method);
            default:
                return ErrorCode.REQUEST_PARAM_ERROR;
        }
    }
}
