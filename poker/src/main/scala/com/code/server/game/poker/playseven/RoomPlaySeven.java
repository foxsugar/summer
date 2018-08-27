package com.code.server.game.poker.playseven;

import com.code.server.constant.exception.DataNotFoundException;
import com.code.server.constant.game.PrepareRoom;
import com.code.server.constant.game.UserBean;
import com.code.server.constant.response.ErrorCode;
import com.code.server.constant.response.IfaceRoomVo;
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
import org.springframework.beans.BeanUtils;

import java.util.HashMap;
import java.util.Map;

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
    protected long roomLastTime;
    public Integer fengDing;//封顶
    public boolean kouDiJiaJi;//抠底加级
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
        room.isRobotRoom =true;
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

    public IfaceRoomVo toVo(long userId) {
        RoomPlaySevenVo roomVo = new RoomPlaySevenVo();

        BeanUtils.copyProperties(this, roomVo);
        RedisManager.getUserRedisService().getUserBeans(users).forEach(userBean -> roomVo.userList.add(userBean.toVo()));
        if (this.game != null) {
            Map<Long,Double> userScoresTemp = new HashMap<>();
            GamePlaySeven gameTemp = (GamePlaySeven)this.getGame();
            if(gameTemp!=null){
                for (Long l: gameTemp.getPlayerCardInfos().keySet()){
                    userScoresTemp.put(l,this.userScores.get(l)-gameTemp.getPlayerCardInfos().get(l).getScore());
                }
            }
            roomVo.setUserScores(userScoresTemp);
            roomVo.game = this.game.toVo(userId);
        }
        if (this.getTimerNode() != null) {
            long time = this.getTimerNode().getStart() + this.getTimerNode().getInterval() - System.currentTimeMillis();
            roomVo.setRemainTime(time);
        }

        return roomVo;
    }

    public long getRoomLastTime() {
        return roomLastTime;
    }

    public void setRoomLastTime(long roomLastTime) {
        this.roomLastTime = roomLastTime;
    }

    @Override
    public int joinRoom(long userId, boolean isJoin) {

        if (isClubRoom() && userId == 0) {
            return 0;
        }
        if (userId == 0) {
            return ErrorCode.JOIN_ROOM_USERID_IS_0;
        }
        if (this.users.contains(userId)) {
            return ErrorCode.CANNOT_CREATE_ROOM_USER_HAS_IN_ROOM;
        }
        if (this.users.size() >= this.personNumber) {
            return ErrorCode.CANNOT_JOIN_ROOM_IS_FULL;

        }
        if (RedisManager.getUserRedisService().getRoomId(userId) != null) {
            return ErrorCode.CANNOT_CREATE_ROOM_USER_HAS_IN_ROOM;
        }
        if (!isCanJoinCheckMoney(userId)) {
            return ErrorCode.CANNOT_JOIN_ROOM_NO_MONEY;
        }


        if (isJoin) {
            roomAddUser(userId);
            //加进玩家-房间映射表
            noticeJoinRoom(userId);
        }

        return 0;
    }

    @Override
    public void noticeJoinRoom(long userId) {
        UserOfRoomSeven userOfRoom = new UserOfRoomSeven();
        int readyNumber = 0;

        for (UserBean userBean : RedisManager.getUserRedisService().getUserBeans(users)) {
            userOfRoom.getUserList().add(userBean.toVo());
        }

        userOfRoom.setInRoomNumber(users.size());
        userOfRoom.setReadyNumber(readyNumber);
        userOfRoom.setClubId(clubId);
        userOfRoom.setClubRoomModel(clubRoomModel);

        userOfRoom.setCanStartUserId(users.get(0));
        userOfRoom.setUserScores(this.userScores);
        userOfRoom.setFengDing(fengDing);
        userOfRoom.setKouDiJiaJi(kouDiJiaJi);
        userOfRoom.setZhuangDanDaJiaBei(zhuangDanDaJiaBei);

        MsgSender.sendMsg2Player(new ResponseVo("roomService", "joinRoom", this.toVo(userId)), userId);

        MsgSender.sendMsg2Player(new ResponseVo("roomService", "roomNotice", userOfRoom), this.getUsers());

        if (isClubRoom()) {
            noticeClubJoinRoom(userId);
        }
    }

    @Override
    public PrepareRoom getPrepareRoomVo() {
        PrepareRoom prepareRoom = new PrepareRoom();
        prepareRoom.goldRoomType = this.goldRoomType;
        prepareRoom.goldRoomPermission = this.goldRoomPermission;
        prepareRoom.createTime = System.currentTimeMillis();
        prepareRoom.gameType = this.getGameType();
        prepareRoom.roomType = this.getRoomType();
        prepareRoom.roomId = this.roomId;
        prepareRoom.multiple = this.multiple;
        prepareRoom.gameNumber = this.gameNumber;
        prepareRoom.fengDing = this.fengDing;
        prepareRoom.kouDiJiaJi = this.kouDiJiaJi;
        prepareRoom.zhuangDanDaJiaBei = this.zhuangDanDaJiaBei;
        return prepareRoom;
    }
}
