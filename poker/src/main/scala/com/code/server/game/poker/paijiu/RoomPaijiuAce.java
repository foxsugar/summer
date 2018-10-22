package com.code.server.game.poker.paijiu;

import com.code.server.constant.exception.DataNotFoundException;
import com.code.server.constant.response.ErrorCode;
import com.code.server.constant.response.ResponseVo;
import com.code.server.game.poker.config.ServerConfig;
import com.code.server.game.room.Room;
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
 * Created by sunxianping on 2018-10-22.
 */
public class RoomPaijiuAce extends RoomPaijiu {


    public static final int minMoney = 150;


    public static int createRoomNotInRoom(long userId, String roomType, String gameType, Integer gameNumber, boolean isCreaterJoin, String clubId, String clubRoomModel) throws DataNotFoundException {
        ServerConfig serverConfig = SpringUtil.getBean(ServerConfig.class);
        RoomPaijiuAce roomPaijiu = new RoomPaijiuAce();
        roomPaijiu.setRoomId(Room.getRoomIdStr(Room.genRoomId(serverConfig.getServerId())));
        roomPaijiu.setRoomType(roomType);
        roomPaijiu.setGameType(gameType);
        roomPaijiu.setGameNumber(gameNumber);
        roomPaijiu.setBankerId(0L);
        roomPaijiu.setCreateUser(userId);
        roomPaijiu.setPersonNumber(4);
        roomPaijiu.setCreaterJoin(isCreaterJoin);
        roomPaijiu.init(gameNumber, 1);
        roomPaijiu.setClubId(clubId);
        roomPaijiu.setClubRoomModel(clubRoomModel);

        //代建房 定时解散
        if (!isCreaterJoin && !roomPaijiu.isClubRoom()) {
            //给代建房 开房者 扣钱
            if (RedisManager.getUserRedisService().getUserMoney(userId) < roomPaijiu.getCreateNeedMoney()) {
                RoomManager.removeRoom(roomPaijiu.getRoomId());
                return ErrorCode.CANNOT_JOIN_ROOM_NO_MONEY;
            }

            roomPaijiu.spendMoney();
            TimerNode prepareRoomNode = new TimerNode(System.currentTimeMillis(), IConstant.HOUR_1, false, roomPaijiu::dissolutionRoom);
            roomPaijiu.prepareRoomTimerNode = prepareRoomNode;
            GameTimer.addTimerNode(prepareRoomNode);
        }


        RoomManager.addRoom(roomPaijiu.getRoomId(), "" + serverConfig.getServerId(), roomPaijiu);
        IdWorker idword = new IdWorker(serverConfig.getServerId(), 0);
        roomPaijiu.setUuid(idword.nextId());


        MsgSender.sendMsg2Player(new ResponseVo("pokerRoomService", "createPaijiuRoomNotInRoom", roomPaijiu.toVo(userId)), userId);

        return 0;
    }


    protected boolean isCanJoinCheckMoney(long userId) {

        //代建房
        if (!isCreaterJoin) {
            if (RedisManager.getUserRedisService().getUserMoney(userId) < minMoney) {
                return false;
            }
            return true;
        }
        if (isAA) {
            if (RedisManager.getUserRedisService().getUserMoney(userId) < createNeedMoney + minMoney) {
                return false;
            }
        } else {
            if (userId == createUser) {
                if (RedisManager.getUserRedisService().getUserMoney(userId) < createNeedMoney + minMoney) {
                    return false;
                }
            }
        }
        return true;
    }


    @Override
    public void addUserSocre(long userId, double score) {
        super.addUserSocre(userId, score);

        RedisManager.getUserRedisService().addUserMoney(userId, score);


    }


    @Override
    public void clearReadyStatus(boolean isAddGameNum) {

        super.clearReadyStatus(isAddGameNum);
        //房卡不足 退出
        clearReadyAceRoom(isAddGameNum);
    }


    public void clearReadyAceRoom(boolean isAddGameNum) {
        if (isGoldRoom()) {

            List<Long> removeList = new ArrayList<>();
            for (long userId : this.users) {
                double gold = RedisManager.getUserRedisService().getUserMoney(userId);
                if (gold < 5) {
                    removeList.add(userId);
                }
            }

            for (long userId : removeList) {
                this.quitRoom(userId);
            }

            //

        }
    }
}
