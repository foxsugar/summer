package com.code.server.game.poker.doudizhu;


import com.code.server.constant.exception.DataNotFoundException;
import com.code.server.constant.response.ErrorCode;
import com.code.server.constant.response.IfaceRoomVo;
import com.code.server.constant.response.ResponseVo;
import com.code.server.constant.response.RoomDoudizhuVo;
import com.code.server.game.poker.config.ServerConfig;
import com.code.server.game.room.Room;
import com.code.server.game.room.RoomExtendGold;
import com.code.server.game.room.kafka.MsgSender;
import com.code.server.game.room.service.RoomManager;
import com.code.server.redis.config.IConstant;
import com.code.server.redis.service.RedisManager;
import com.code.server.util.IdWorker;
import com.code.server.util.SpringUtil;
import com.code.server.util.timer.GameTimer;
import com.code.server.util.timer.TimerNode;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunxianping on 2017/3/13.
 */
public class RoomDouDiZhu extends RoomExtendGold {


    protected int jiaoScoreMax = 3;
    protected int shuanglong = 0;
    protected List<Integer> testNextCards ;
    protected long testUserId;
    private boolean userLastGameCards = false;
    private List<Integer> lastGameCards = new ArrayList<>();


    protected boolean isCanAgreeDissloution(int agreeNum){
        return agreeNum >= 3;
    }

    public static RoomDouDiZhu getRoomInstance(String roomType) {
        switch (roomType) {
            case "1":
                return new RoomDouDiZhu();
            case "3":
                return new RoomDouDiZhuPlus();
            default:
                return new RoomDouDiZhu();
        }

    }

    public static Room createRoom_(long userId, int gameNumber, int multiple, String gameType, String roomType,
                                   boolean isAA, boolean isJoin, boolean showChat, int personNum, int jiaoScoreMax,int shuanglong,
                                   String clubId, String clubRoomModel,int clubMode,int otherMode) throws DataNotFoundException {
        ServerConfig serverConfig = SpringUtil.getBean(ServerConfig.class);
        RoomDouDiZhu room = new RoomDoudizhuZLB();

        room.personNumber = personNum;

        room.roomId = getRoomIdStr(genRoomId(serverConfig.getServerId()));
        room.createUser = userId;
        room.gameType = gameType;
        room.roomType = roomType;
        room.isAA = isAA;
        room.isCreaterJoin = isJoin;
        room.showChat = showChat;
        room.jiaoScoreMax = jiaoScoreMax;
        room.shuanglong = shuanglong;
        room.otherMode = otherMode;
        room.setClubMode(clubMode);


        room.setClubId(clubId);
        room.setClubRoomModel(clubRoomModel);
        room.init(gameNumber, multiple);
        return room;
    }

    public static int createRoom(long userId, int gameNumber, int multiple, String gameType, String roomType,
                                 boolean isAA, boolean isJoin, boolean showChat, int personNum, int jiaoScoreMax,int shuanglong,
                                 String clubId, String clubRoomModel,int clubMode,int otherMode) throws DataNotFoundException {
        ServerConfig serverConfig = SpringUtil.getBean(ServerConfig.class);
        RoomDouDiZhu room = getRoomInstance(roomType);

        room.personNumber = personNum;

        room.roomId = getRoomIdStr(genRoomId(serverConfig.getServerId()));
        room.createUser = userId;
        room.gameType = gameType;
        room.roomType = roomType;
        room.isAA = isAA;
        room.isCreaterJoin = isJoin;
        room.showChat = showChat;
        room.jiaoScoreMax = jiaoScoreMax;
        room.shuanglong = shuanglong;
        room.otherMode = otherMode;
        room.setClubMode(clubMode);


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

        MsgSender.sendMsg2Player(new ResponseVo("pokerRoomService", "createRoom", room.toVo(userId)), userId);

        return 0;
    }


    @Override
    public IfaceRoomVo toVo(long userId) {
        RoomDoudizhuVo roomVo = new RoomDoudizhuVo();
        BeanUtils.copyProperties(this, roomVo);
        roomVo.setJiaoScoreMax(this.jiaoScoreMax);
        roomVo.setShuanglong(this.shuanglong);
        RedisManager.getUserRedisService().getUserBeans(users).forEach(userBean -> roomVo.userList.add(userBean.toVo()));
        if (this.game != null) {
            roomVo.game = this.game.toVo(userId);
        }
        if (this.getTimerNode() != null) {
            long time = this.getTimerNode().getStart() + this.getTimerNode().getInterval() - System.currentTimeMillis();
            roomVo.setRemainTime(time);
        }
        if (users.size() > 0) {
            roomVo.setCanStartUserId(users.get(0));
        }
        return roomVo;
    }

    protected boolean isAddClubMoney() {
        return SpringUtil.getBean(ServerConfig.class).getAddClubMoney() == 1;
    }


    public boolean isUserLastGameCards() {
        return userLastGameCards;
    }

    public RoomDouDiZhu setUserLastGameCards(boolean userLastGameCards) {
        this.userLastGameCards = userLastGameCards;
        return this;
    }

    public List<Integer> getLastGameCards() {
        return lastGameCards;
    }

    public RoomDouDiZhu setLastGameCards(List<Integer> lastGameCards) {
        this.lastGameCards = lastGameCards;
        return this;
    }
}
