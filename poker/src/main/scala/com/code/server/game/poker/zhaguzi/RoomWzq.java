package com.code.server.game.poker.zhaguzi;

import com.code.server.constant.response.ResponseVo;
import com.code.server.game.poker.config.ServerConfig;
import com.code.server.game.room.Room;
import com.code.server.game.room.kafka.MsgSender;
import com.code.server.game.room.service.RoomManager;
import com.code.server.util.IdWorker;
import com.code.server.util.SpringUtil;

/**

 */
public class RoomWzq extends Room {


    public static int createRoom(long userId , String roomType,String gameType,int multiple,int personNumber,int gameNumber)  {
        //身上的钱够不够
//        if(RedisManager.getUserRedisService().getUserGold(userId) < multiple){
//            return ErrorCode.NOT_HAVE_MORE_MONEY;
//        }

        ServerConfig serverConfig = SpringUtil.getBean(ServerConfig.class);

        RoomWzq room = new RoomWzq();
        room.personNumber = personNumber;

        room.roomId = getRoomIdStr(genRoomId(serverConfig.getServerId()));
        room.createUser = userId;
        room.gameType = gameType;
        room.roomType = roomType;
        room.bankerId = userId;
        room.multiple = multiple;
        room.gameNumber = gameNumber;


        RoomManager.addRoom(room.roomId, "" + serverConfig.getServerId(), room);

        //扣掉
//        RedisManager.getUserRedisService().addUserGold(userId, -chip);
//        roomGuessCar.bankerScore = chip;
//        roomGuessCar.chip = chip;

        IdWorker idWorker = new IdWorker(serverConfig.getServerId(), 0);
        room.setUuid(idWorker.nextId());

        int code = room.joinRoom(userId, true);
        if (code != 0) {
            return code;
        }

        MsgSender.sendMsg2Player(new ResponseVo("pokerRoomService", "createWZQRoom", room.toVo(userId)), userId);

        return 0;
    }
}
