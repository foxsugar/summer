package com.code.server.game.poker.playseven;

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

/**
 * 项目名称：${project_name}
 * 类名称：${type_name}
 * 类描述：
 * 创建人：Clark
 * 创建时间：${date} ${time}
 * 修改人：Clark
 * 修改时间：${date} ${time}
 * 修改备注：
 *
 * @version 1.0
 */
public class RoomPlaySeven extends Room {

    public Integer fengDing;//封顶
    public boolean kouDiJiaJi;//扣底加级
    public boolean zhuangDanDaJiaBei;//庄单打加倍

    public long fanZhuUserId=0l;

    public static RoomPlaySeven getRoomInstance(String roomType){
        switch (roomType) {
            case "10":
                return new RoomPlaySeven();
            default:
                return new RoomPlaySeven();
        }
    }

    public static int createPlaySevenRoom(long userId, int gameNumber,int fengDing,boolean kouDiJiaJi,boolean zhuangDanDaJiaBei, int personNumber,int multiple, String gameType, String roomType, boolean isAA, boolean isJoin, String clubId, String clubRoomModel) throws DataNotFoundException {
        ServerConfig serverConfig = SpringUtil.getBean(ServerConfig.class);

        RoomPlaySeven room = getRoomInstance(roomType);

        room.fengDing = fengDing;
        room.kouDiJiaJi = kouDiJiaJi;
        room.zhuangDanDaJiaBei = zhuangDanDaJiaBei;

        room.personNumber = personNumber;
        room.roomId = getRoomIdStr(genRoomId(serverConfig.getServerId()));
        room.createUser = userId;
        room.gameType = gameType;
        room.roomType = roomType;
        room.isAA = isAA;
        room.isCreaterJoin = isJoin;
        room.multiple = multiple;
        room.bankerId = userId;
        room.setClubId(clubId);
        room.setClubRoomModel(clubRoomModel);

        room.init(gameNumber, multiple);

        int code = room.joinRoom(userId, isJoin);
        if (code != 0) {
            return code;
        }


        //代建房 定时解散
        if (!isJoin) {
            //给代建房 开房者 扣钱
            if(RedisManager.getUserRedisService().getUserMoney(userId) < room.createNeedMoney){
                return ErrorCode.CANNOT_CREATE_ROOM_MONEY;
            }
            room.spendMoney();
            TimerNode prepareRoomNode = new TimerNode(System.currentTimeMillis(), IConstant.HOUR_1, false, room::dissolutionRoom);
            room.prepareRoomTimerNode = prepareRoomNode;
            GameTimer.addTimerNode(prepareRoomNode);
        }


        RoomManager.addRoom(room.roomId, "" + serverConfig.getServerId(), room);
        IdWorker idWorker = new IdWorker(serverConfig.getServerId(), 0);
        room.setUuid(idWorker.nextId());
        MsgSender.sendMsg2Player(new ResponseVo("pokerRoomService", "createPlaySevenRoom", room.toVo(userId)), userId);

        return 0;
    }

    public Integer getFengDing() {
        return fengDing;
    }

    public void setFengDing(Integer fengDing) {
        this.fengDing = fengDing;
    }

    public boolean isKouDiJiaJi() {
        return kouDiJiaJi;
    }

    public void setKouDiJiaJi(boolean kouDiJiaJi) {
        this.kouDiJiaJi = kouDiJiaJi;
    }

    public boolean isZhuangDanDaJiaBei() {
        return zhuangDanDaJiaBei;
    }

    public void setZhuangDanDaJiaBei(boolean zhuangDanDaJiaBei) {
        this.zhuangDanDaJiaBei = zhuangDanDaJiaBei;
    }

    public long getFanZhuUserId() {
        return fanZhuUserId;
    }

    public void setFanZhuUserId(long fanZhuUserId) {
        this.fanZhuUserId = fanZhuUserId;
    }
}
