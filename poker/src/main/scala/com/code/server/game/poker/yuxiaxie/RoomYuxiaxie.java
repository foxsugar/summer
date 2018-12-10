package com.code.server.game.poker.yuxiaxie;

import com.code.server.constant.exception.DataNotFoundException;
import com.code.server.constant.game.RoomRecord;
import com.code.server.constant.game.UserBean;
import com.code.server.constant.game.UserRecord;
import com.code.server.constant.kafka.IKafaTopic;
import com.code.server.constant.kafka.KafkaMsgKey;
import com.code.server.constant.response.ErrorCode;
import com.code.server.constant.response.ResponseVo;
import com.code.server.game.poker.config.ServerConfig;
import com.code.server.game.poker.service.PokerGoldRoom;
import com.code.server.game.room.kafka.MsgSender;
import com.code.server.game.room.service.RoomManager;
import com.code.server.kafka.MsgProducer;
import com.code.server.redis.config.IConstant;
import com.code.server.redis.service.RedisManager;
import com.code.server.util.IdWorker;
import com.code.server.util.SpringUtil;
import com.code.server.util.timer.GameTimer;
import com.code.server.util.timer.TimerNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by sunxianping on 2018-11-30.
 */
public class RoomYuxiaxie extends PokerGoldRoom {


    //单压限分
    private int danya;
    //串联限分
    private int chuanlian;
    //豹子限分
    private int baozi;
    //挪次数
    private int nuo;

    List<List<Integer>> diceHistory = new ArrayList<>();


    public static int createRoom(long userId, int gameNumber, int multiple, String gameType, String roomType,
                                 boolean isAA, boolean isJoin, boolean showChat, int personNum,
                                 String clubId, String clubRoomModel,int otherMode,int danya, int chuanlian, int baozi, int nuo) throws DataNotFoundException {
        ServerConfig serverConfig = SpringUtil.getBean(ServerConfig.class);
        RoomYuxiaxie room = new RoomYuxiaxie();

        room.personNumber = personNum;

        room.roomId = getRoomIdStr(genRoomId(serverConfig.getServerId()));
        room.createUser = userId;
        room.gameType = gameType;
        room.roomType = roomType;
        room.isAA = isAA;
        room.isCreaterJoin = isJoin;
        room.showChat = showChat;
        room.otherMode = otherMode;
        room.setBankerId(userId);




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

        MsgSender.sendMsg2Player(new ResponseVo("pokerRoomService", "createYXXRoom", room.toVo(userId)), userId);
        return 0;
    }


    private long getWinner(){
        double score = 0;
        long uid = 0;
        for (Map.Entry<Long, Double> entry : userScores.entrySet()) {
            if (entry.getKey() != this.bankerId) {
                if (entry.getValue() > score) {
                    score = entry.getValue();
                    uid = entry.getKey();
                }
            }
        }
        return uid;
    }

    public void genRoomRecord() {
        if (!isOpen) return;
        RoomRecord roomRecord = new RoomRecord();
        roomRecord.setRoomId(this.roomId);
        roomRecord.setId(this.getUuid());
        roomRecord.setType(this.roomType);
        roomRecord.setTime(System.currentTimeMillis());
        roomRecord.setClubId(clubId);
        roomRecord.setClubRoomModel(clubRoomModel);
        roomRecord.setGameType(gameType);
        roomRecord.setCurGameNum(this.curGameNumber);
        roomRecord.setAllGameNum(this.gameNumber);
        //winner
        roomRecord.setWinnerId(getWinner());

        this.userScores.forEach((key, value) -> {
            UserRecord userRecord = new UserRecord();
            userRecord.setScore(value);
            userRecord.setUserId(key);
            UserBean userBean = RedisManager.getUserRedisService().getUserBean(key);
            if (userBean != null) {
                userRecord.setName(userBean.getUsername());
            }
            roomRecord.getRecords().add(userRecord);
        });

        KafkaMsgKey kafkaMsgKey = new KafkaMsgKey().setMsgId(KAFKA_MSG_ID_ROOM_RECORD);
        MsgProducer msgProducer = SpringUtil.getBean(MsgProducer.class);
        msgProducer.send(IKafaTopic.CENTER_TOPIC, kafkaMsgKey, roomRecord);

    }


    public int getDanya() {
        return danya;
    }

    public RoomYuxiaxie setDanya(int danya) {
        this.danya = danya;
        return this;
    }

    public int getChuanlian() {
        return chuanlian;
    }

    public RoomYuxiaxie setChuanlian(int chuanlian) {
        this.chuanlian = chuanlian;
        return this;
    }

    public int getBaozi() {
        return baozi;
    }

    public RoomYuxiaxie setBaozi(int baozi) {
        this.baozi = baozi;
        return this;
    }

    public int getNuo() {
        return nuo;
    }

    public RoomYuxiaxie setNuo(int nuo) {
        this.nuo = nuo;
        return this;
    }

    public List<List<Integer>> getDiceHistory() {
        return diceHistory;
    }

    public RoomYuxiaxie setDiceHistory(List<List<Integer>> diceHistory) {
        this.diceHistory = diceHistory;
        return this;
    }
}
