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


    public static void dispatch(ConsumerRecord<String,String> record){
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
            int code = dispatchRoomService(method, params, userId,roomId);
            if(code != 0){
                MsgSender.sendMsg2Player(service,method,code,userId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private static int dispatchRoomService(String method, JsonNode params, long userId,String roomId) {


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

                IfaceRoom room = RoomManager.getRoom(roomId);
                if (room == null) {
                    return ErrorCode.CANNOT_JOIN_ROOM_NOT_EXIST;
                }
                return room.joinRoom(userId,true);
            }
//            case "joinRoomQuick":{
//                double type = params.getDouble("type");
//                return GoldRoomPool.getInstance().addRoom(player, type);
//
//
//            }
            case "quitRoom": {
                IfaceRoom room = RoomManager.getRoom(roomId);
                if (room == null) {
                    return ErrorCode.CAN_NOT_NO_ROOM;
                }
                return room.quitRoom(userId);
            }
            case "getReady": {
                IfaceRoom room = RoomManager.getRoom(roomId);
                IfaceRoom roomPuls = RoomManager.getPlusRoom(roomId);
                if (room == null && roomPuls == null) {
                    return ErrorCode.CAN_NOT_NO_ROOM;
                }
                if(roomPuls != null){
                    return roomPuls.getReady(userId);
                }else if(room != null){
                    return room.getReady(userId);
                }
            }
            case "dissolveRoom": {
                IfaceRoom room = RoomManager.getRoom(roomId);
                if (room == null) {
                    return ErrorCode.CAN_NOT_NO_ROOM;
                }
                return room.dissolution(userId, true, method);
            }
            case "answerIfDissolveRoom":{

                IfaceRoom room = RoomManager.getRoom(roomId);
                if (room == null) {
                    return ErrorCode.CAN_NOT_NO_ROOM;
                }
                boolean isAgree = "2".equals(params.get("answer").asText());
                return room.dissolution(userId, isAgree, method);
            }
            case "startGameByClient":{
                IfaceRoom room = RoomManager.getRoom(roomId);
                if (room == null) {
                    return ErrorCode.CAN_NOT_NO_ROOM;
                }
                return room.startGameByClient(userId);

            }
            case "getPrepareRoom":{
                IfaceRoom room = RoomManager.getRoom(roomId);
                return room.getPrepareRoom(userId);
            }
            case "getRoomClubByUser":{
                IfaceRoom room = RoomManager.getRoom(roomId);
                return room.getRoomClubByUser(userId);
            }

            default:
                return ErrorCode.REQUEST_PARAM_ERROR;
        }
    }
}
