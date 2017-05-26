package com.code.server.game.room;

import com.code.server.constant.response.ErrorCode;
import net.sf.json.JSONObject;

/**
 * Created by sunxianping on 2017/5/23.
 */
public class RoomMsgDispatch {



    private int dispatchRoomService(String method, JSONObject params, long userId) {
        Player player = GameManager.getPlayerByCtx(ctx);
        if (player == null) {
            return ErrorCode.YOU_HAVE_NOT_LOGIN;
        }

        switch (method) {
            case "createRoom":{

                int gameNumber = params.getInt("gameNumber");
                int multiple = params.getInt("maxMultiple");
                String gameType = params.optString("gameType", "0");
                return RoomDouDiZhu.createRoom(player, gameNumber, multiple,gameType);
            }
            case "createRoomTDK":{

                int gameNumber = params.getInt("gameNumber");
                double multiple = params.getDouble("maxMultiple");
                int personNumber = params.getInt("personNumber");
                int hasNine = params.getInt("hasNine");
                return RoomTanDaKeng.createRoom(player, gameNumber,multiple,personNumber,hasNine);
            }
            case "joinRoom": {
                String roomId = params.getString("roomId");
                Room room = GameManager.getInstance().rooms.get(roomId);
                if (room == null) {
                    return ErrorCode.CANNOT_JOIN_ROOM_NOT_EXIST;
                }
                return room.joinRoom(player);
            }
            case "joinRoomQuick":{
                double type = params.getDouble("type");
                return GoldRoomPool.getInstance().addRoom(player, type);


            }
            case "quitRoom": {
                Room room = getRoomByPlayer(player);
                if (room == null) {
                    return ErrorCode.CAN_NOT_NO_ROOM;
                }
                return room.quitRoom(player);
            }
            case "getReady": {
                Room room = getRoomByPlayer(player);
                if (room == null) {
                    return ErrorCode.CAN_NOT_NO_ROOM;
                }
                return room.getReady(player);
            }
            case "dissolveRoom": {
                Room room = getRoomByPlayer(player);
                if (room == null) {
                    return ErrorCode.CAN_NOT_NO_ROOM;
                }
                return room.dissolution(player, true, method);
            }
            case "answerIfDissolveRoom":
                Room room = getRoomByPlayer(player);
                if (room == null) {
                    return ErrorCode.CAN_NOT_NO_ROOM;
                }
                boolean isAgree = "2".equals(params.getString("answer"));
                return room.dissolution(player, isAgree, method);
            default:
                return ErrorCode.REQUEST_PARAM_ERROR;
        }
    }
}
