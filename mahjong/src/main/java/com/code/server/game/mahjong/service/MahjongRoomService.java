package com.code.server.game.mahjong.service;

import com.code.server.constant.game.IGameConstant;
import com.code.server.constant.response.ResponseVo;
import com.code.server.game.mahjong.config.ServerConfig;
import com.code.server.game.mahjong.logic.RoomFactory;
import com.code.server.game.mahjong.logic.RoomInfo;
import com.code.server.game.mahjong.util.ErrorCode;
import com.code.server.game.mahjong.util.SpringUtil;
import com.code.server.game.room.MsgSender;
import com.code.server.game.room.Room;
import com.code.server.game.room.service.RoomManager;
import com.code.server.redis.service.RedisManager;
import com.code.server.util.timer.GameTimer;
import com.code.server.util.timer.ITimeHandler;
import com.code.server.util.timer.TimerNode;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Created by sunxianping on 2017/6/5.
 */
public class MahjongRoomService {

    public static int dispatch(long userId, String method, JsonNode paramsjSONObject) {
        int code = 0;
        switch (method) {
            case "createRoomByUser": {
                String modeTotal = paramsjSONObject.get("modeTotal").asText();
                String mode = paramsjSONObject.get("mode").asText();
                int multiple = paramsjSONObject.get("multiple").asInt();
                int gameNumber = paramsjSONObject.get("gameNumber").asInt();
                int personNumber = paramsjSONObject.get("personNumber").asInt();
                String gameType = paramsjSONObject.get("gameType").asText();
                code = createRoomByUser(userId, modeTotal, mode, multiple, gameNumber, personNumber, gameType);
                break;
            }
            case "createRoomByEachUser": {
                String modeTotal = paramsjSONObject.get("modeTotal").asText();
                String mode = paramsjSONObject.get("mode").asText();
                int multiple = paramsjSONObject.get("multiple").asInt();
                int gameNumber = paramsjSONObject.get("gameNumber").asInt();
                int personNumber = paramsjSONObject.get("personNumber").asInt();
                String gameType = paramsjSONObject.get("gameType").asText();
                code = createRoomByEachUser(userId, modeTotal, mode, multiple, gameNumber, personNumber, gameType);
                break;
            }

            case "createRoomButNotInRoom": {
                String modeTotal = paramsjSONObject.get("modeTotal").asText();
                String mode = paramsjSONObject.get("mode").asText();
                int multiple = paramsjSONObject.get("multiple").asInt();
                int gameNumber = paramsjSONObject.get("gameNumber").asInt();
                int personNumber = paramsjSONObject.get("personNumber").asInt();
                String gameType = paramsjSONObject.get("gameType").asText();
                code = createRoomButNotInRoom(userId, modeTotal, mode, multiple, gameNumber, personNumber, gameType);
                break;
            }
        }


        return  code;
    }






    private static boolean isCanCreate(String modeTotal, String mode, String multiple) {
        if (modeTotal.equals("1")) {
            if (!mode.equals("5") && !mode.equals("6")) {
                return false;
            }
            if (!multiple.equals("1") && !multiple.equals("2") && !multiple.equals("5")) {
                return false;
            }
        } else if (modeTotal.equals("2")) {
            if (!mode.equals("1") && !mode.equals("2") && !mode.equals("3") && !mode.equals("4")) {
                return false;
            }
            if (!multiple.equals("1") && !multiple.equals("2") && !multiple.equals("5")) {
                return false;
            }
        } else if (modeTotal.equals("3") || modeTotal.equals("15")) {
            if (!multiple.equals("1") && !multiple.equals("2") && !multiple.equals("5")) {
                return false;
            }
        } else if (modeTotal.equals("4")) {
            if (!mode.equals("303")) {
                return false;
            }
        } else if (modeTotal.equals("5")) {
            if (!multiple.equals("1") && !multiple.equals("2") && !multiple.equals("5")) {
                return false;
            }
        } else if (modeTotal.equals("6")) {
            if (!mode.equals("0")) {
                return false;
            }
        }
        /**
         256封顶、128封顶、杠呲宝、未上听包三家、杠上开花、三七夹	二进制取,1是0否
         */
        else if (modeTotal.equals("10")) {
            if (!multiple.equals("25") && !multiple.equals("50") && !multiple.equals("100") && !multiple.equals("200")) {
                return false;
            }
            if (Integer.parseInt(mode) > 63 || Integer.parseInt(mode) < 0) {
                return false;
            }
        } else if (modeTotal.equals("11")) {
            if (!multiple.equals("25") && !multiple.equals("50") && !multiple.equals("100") && !multiple.equals("200")) {
                return false;
            }
            if (Integer.parseInt(mode) > 63 || Integer.parseInt(mode) < 0) {
                return false;
            }
        } else if (modeTotal.equals("12")) {
            if (!multiple.equals("1") && !multiple.equals("2")) {
                return false;
            }
            if (Integer.parseInt(mode) != 12051314 && Integer.parseInt(mode) != 12051304 && Integer.parseInt(mode) != 12050314 && Integer.parseInt(mode) != 12050304 && Integer.parseInt(mode) != 12151314 && Integer.parseInt(mode) != 12151304 && Integer.parseInt(mode) != 12150314 && Integer.parseInt(mode) != 12150304) {
                return false;
            }
        } else if (modeTotal.equals("13")) {
            if (Integer.parseInt(mode) != 13051314 && Integer.parseInt(mode) != 13051304 && Integer.parseInt(mode) != 13050314 && Integer.parseInt(mode) != 13050304 && Integer.parseInt(mode) != 13151314 && Integer.parseInt(mode) != 13151304 && Integer.parseInt(mode) != 13150314 && Integer.parseInt(mode) != 13150304) {
                return false;
            }
        } else if (modeTotal.equals("14")) {
            if (!multiple.equals("25") && !multiple.equals("50") && !multiple.equals("100") && !multiple.equals("200")) {
                return false;
            }
            if (Integer.parseInt(mode) > 63 || Integer.parseInt(mode) < 0) {
                return false;
            }
        } else if (modeTotal.equals("124")) {
            if (Integer.parseInt(mode) != 124051314 && Integer.parseInt(mode) != 124051304 && Integer.parseInt(mode) != 124050314 && Integer.parseInt(mode) != 124050304 && Integer.parseInt(mode) != 124151314 && Integer.parseInt(mode) != 124151304 && Integer.parseInt(mode) != 124150314 && Integer.parseInt(mode) != 124150304) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    public static RoomInfo createRoom(long userId, String modeTotal, String mode, int multiple, int gameNumber, int personNumber, String gameType, String each, boolean isJoin) {
        if (!isCanCreate(modeTotal, mode, "" + multiple)) {
            return null;
        }

        RoomInfo roomInfo = RoomFactory.getRoomInstance(gameType);
        String roomId = Room.getRoomIdStr(Room.genRoomId());
        roomInfo.init(roomId, userId, modeTotal, mode, multiple, gameNumber, personNumber, userId, 0);
        roomInfo.setEach(each);
        if (isJoin) {

            roomInfo.joinRoom(userId);
        }

        int serverId = SpringUtil.getBean(ServerConfig.class).getServerId();
        RoomManager.addRoom(roomInfo.getRoomId(), "" + serverId, roomInfo);

        return roomInfo;
    }


    public static int createRoomByEachUser(long userId, String modeTotal, String mode, int multiple, int gameNumber, int personNumber, String gameType) {
        RoomInfo roomInfo = createRoom(userId, modeTotal, mode, multiple, gameNumber, personNumber, gameType, "1", true);
        if (roomInfo == null) {
            return ErrorCode.CANNOT_CREATE_ROOM_PARAMETER_IS_ERROR;
        }
        MsgSender.sendMsg2Player(new ResponseVo("mahjongRoomService", "createRoomByEachUser", roomInfo.toJSONObject()), userId);
        return 0;
    }

    public static int createRoomByUser(long userId, String modeTotal, String mode, int multiple, int gameNumber, int personNumber, String gameType) {
        RoomInfo roomInfo = createRoom(userId, modeTotal, mode, multiple, gameNumber, personNumber, gameType, "0", true);
        if (roomInfo == null) {
            return ErrorCode.CANNOT_CREATE_ROOM_PARAMETER_IS_ERROR;
        }
        MsgSender.sendMsg2Player(new ResponseVo("mahjongRoomService", "createRoomByUser", roomInfo.toJSONObject()), userId);
        return 0;
    }

    public static int createRoomButNotInRoom(long userId, String modeTotal, String mode, int multiple, int gameNumber, int personNumber, String gameType) {

        if ("LQ".equals(gameType)) {
            double money = RedisManager.getUserRedisService().getUserMoney(userId);
            if (8 == gameNumber) {
                if (money < 30) {
                    return ErrorCode.CANNOT_JOIN_ROOM_NO_MONEY;
                }
                RedisManager.getUserRedisService().addUserMoney(userId, -30);
                RedisManager.addGold(userId, 3);

            } else if (16 == gameNumber) {
                if (money < 60) {
                    return ErrorCode.CANNOT_JOIN_ROOM_NO_MONEY;
                }
                RedisManager.getUserRedisService().addUserMoney(userId, -60);
                RedisManager.addGold(userId, 6);
            }
        }

        RoomInfo roomInfo = createRoom(userId, modeTotal, mode, multiple, gameNumber, personNumber, gameType, "2", false);

        if (roomInfo == null) {
            return ErrorCode.CANNOT_CREATE_ROOM_PARAMETER_IS_ERROR;
        }

        String roomId = roomInfo.getRoomId();


        long start = System.currentTimeMillis();
        TimerNode node = new TimerNode(start, IGameConstant.ONE_HOUR, false, new ITimeHandler() {
            @Override
            public void fire() {
                try {
                    RoomInfo roomInfo = (RoomInfo) RoomManager.getRoom(roomId);

                    if (roomInfo != null && !roomInfo.isInGame() && roomInfo.getCurGameNumber() == 1) {

                        if ("LQ".equals(roomInfo.getGameType())) {
                            if (8 == roomInfo.getGameNumber()) {

                                RedisManager.getUserRedisService().addUserMoney(userId, 30);
                                RedisManager.addGold(userId, -3);
                            } else if (16 == roomInfo.getGameNumber()) {
                                RedisManager.getUserRedisService().addUserMoney(userId, 60);
                                RedisManager.addGold(userId, -6);
                            }
                        }
                        RoomManager.removeRoom(roomInfo.getRoomId());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        roomInfo.setTimerNode(node);
        GameTimer.getInstance().addTimerNode(node);


        MsgSender.sendMsg2Player(new ResponseVo("mahjongRoomService", "createRoomButNotInRoom", roomInfo.toJSONObject()), userId);
        return 0;
    }


//    public void onlinemethod(GamePlayer gamePlayer) {
//
//        Map<Integer, String> userRoom = GameManager.getInstance().getUserRoom();
//
//        String roomId = userRoom.get(gamePlayer.getUserId());//房间号
//
//        RoomInfo roomInfo = GameManager.getInstance().getRoom(roomId);
//
//        if (roomInfo != null) {
//            List<Integer> roomUserId = roomInfo.getUsers();
//
//            JSONObject result = new JSONObject();
//
//            Map<String, Integer> mapresult = new HashMap<String, Integer>();
//
//            mapresult.put("id", gamePlayer.getUserId());
//            mapresult.put("status", 1);
//
//            result.put("service", "gameService");
//            result.put("method", "offline");
//            result.put("params", new Gson().toJson(mapresult));
//            result.put("code", "0");
//
//            serverContext.sendToOnlinePlayer(result, roomUserId);
//        }
//    }
}
