package com.code.server.game.poker.tiandakeng;

import com.code.server.constant.exception.DataNotFoundException;
import com.code.server.constant.response.ErrorCode;
import com.code.server.constant.response.ResponseVo;
import com.code.server.game.poker.config.ServerConfig;
import com.code.server.game.poker.service.PokerGoldRoom;
import com.code.server.game.room.kafka.MsgSender;
import com.code.server.game.room.service.RoomManager;
import com.code.server.redis.config.IConstant;
import com.code.server.redis.service.RedisManager;
import com.code.server.util.IdWorker;
import com.code.server.util.SpringUtil;
import com.code.server.util.timer.GameTimer;
import com.code.server.util.timer.TimerNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunxianping on 2018-10-18.
 */
public class RoomTDK extends PokerGoldRoom {

    private List<Long> watchUser = new ArrayList<>();


    public static int createRoom(long userId, int gameNumber, int multiple, String gameType, String roomType,
                                 boolean isAA, boolean isJoin, boolean showChat, int personNum,
                                 String clubId, String clubRoomModel,int otherMode) throws DataNotFoundException {
        ServerConfig serverConfig = SpringUtil.getBean(ServerConfig.class);
        RoomTDK room = new RoomTDK();

        room.personNumber = personNum;

        room.roomId = getRoomIdStr(genRoomId(serverConfig.getServerId()));
        room.createUser = userId;
        room.gameType = gameType;
        room.roomType = roomType;
        room.isAA = isAA;
        room.isCreaterJoin = isJoin;
        room.showChat = showChat;
        room.otherMode = otherMode;


        room.setClubId(clubId);
        room.setClubRoomModel(clubRoomModel);
        room.init(gameNumber, multiple);


        int code = room.joinRoom(userId, isJoin);
        if (code != 0) {
            return code;
        }


        //代建房 定时解散
        if (!isJoin) {
            if (RedisManager.getUserRedisService().getUserMoney(userId) < room.createNeedMoney) {
                RoomManager.removeRoom(room.getRoomId());
                //todo 删除房间
                return ErrorCode.CANNOT_JOIN_ROOM_NO_MONEY;
            }
            //给代建房 开房者 扣钱
            room.spendMoney();
            TimerNode prepareRoomNode = new TimerNode(System.currentTimeMillis(), IConstant.HOUR_1, false, room::dissolutionRoom);
            room.prepareRoomTimerNode = prepareRoomNode;
            GameTimer.addTimerNode(prepareRoomNode);
        }


        RoomManager.addRoom(room.roomId, "" + serverConfig.getServerId(), room);

        IdWorker idWorker = new IdWorker(serverConfig.getServerId(), 0);
        room.setUuid(idWorker.nextId());

        MsgSender.sendMsg2Player(new ResponseVo("pokerRoomService", "createTDKRoom", room.toVo(userId)), userId);
        return 0;
    }

    public List<Long> getWatchUser() {
        return watchUser;
    }

    public RoomTDK setWatchUser(List<Long> watchUser) {
        this.watchUser = watchUser;
        return this;
    }
}
