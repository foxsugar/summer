package com.code.server.game.poker.yuxiaxie;

import com.code.server.constant.exception.DataNotFoundException;
import com.code.server.constant.game.IGameConstant;
import com.code.server.constant.game.RoomRecord;
import com.code.server.constant.game.UserBean;
import com.code.server.constant.game.UserRecord;
import com.code.server.constant.kafka.IKafaTopic;
import com.code.server.constant.kafka.KafkaMsgKey;
import com.code.server.constant.response.ErrorCode;
import com.code.server.constant.response.IfaceRoomVo;
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
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.HashMap;
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

    Map<Long, Map<Integer,Bet>> betHistory = new HashMap<>();


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

        if (room.isClubRoom()) {
            room.setBankerId(0);
        }

        room.setDanya(danya);
        room.setBaozi(baozi);
        room.setChuanlian(chuanlian);
        room.setNuo(nuo);

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


    public int getYXXDiceHistory(long userId){
        MsgSender.sendMsg2Player(new ResponseVo("pokerRoomService", "getYXXDiceHistory", diceHistory), userId);
        return 0;
    }

    public int getYXXBetHistory(long userId){
        MsgSender.sendMsg2Player(new ResponseVo("pokerRoomService", "getYXXBetHistory", this.betHistory.get(userId)), userId);
        return 0;
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


    @Override
    public int startGameByClient(long userId) {

        if (this.users.get(0) != userId){
            return ErrorCode.ROOM_START_NOT_CREATEUSER;
        }

        //第一局
        if (this.curGameNumber != 1) return ErrorCode.ROOM_START_CAN_NOT;

        if (userStatus.get(userId) != IGameConstant.STATUS_READY) return ErrorCode.ROOM_START_CAN_NOT;

        //防止多次点开始
        if(this.game != null) return ErrorCode.ROOM_START_CAN_NOT;

        int readyCount = 0;
        for (Map.Entry<Long, Integer> entry : userStatus.entrySet()) {

            Integer status = entry.getValue();
            if(status == IGameConstant.STATUS_READY) readyCount++;
        }

        if (readyCount < 2) return ErrorCode.READY_NUM_ERROR;

        this.setPersonNumber(userScores.size());
//        this.setPersonNumber(PERSONNUM);
        //没准备的人
        ArrayList<Long> removeList = new ArrayList<>();

        for (Map.Entry<Long, Integer> entry : userStatus.entrySet()){
            Integer status = entry.getValue();

            if (status != IGameConstant.STATUS_READY){
                removeList.add(entry.getKey());
            }
        }

        for (Long removeId : removeList){
            roomRemoveUser(removeId);
        }

        //通知其他人游戏已经开始
//        MsgSender.sendMsg2Player(new ResponseVo("gameService", "gamePullMiceBegin", "ok"), this.getUsers());
        MsgSender.sendMsg2Player(new ResponseVo("roomService", "startGameByClient", 0), userId);

        GameYuxiaxie game = new GameYuxiaxie();
        this.game = game;
        game.startGame(users, this);
        notifyCludGameStart();

        //游戏开始 代建房 去除定时解散
        if (!isOpen && !this.isCreaterJoin) GameTimer.removeNode(prepareRoomTimerNode);

        //扣钱
        if (!isOpen && isCreaterJoin) spendMoney();
        this.isInGame = true;
        this.isOpen = true;
        return 0;
    }


    @Override
    public void addUserSocre(long userId, double score) {
        super.addUserSocre(userId, score);
        //todo 俱乐部房间 加减俱乐部分数
        if (isClubRoom()) {
            RedisManager.getClubRedisService().addClubUserMoney(this.clubId, userId, score);
        }
    }

    @Override
    public IfaceRoomVo toVo(long userId) {
        RoomYuxiaxieVo roomVo = new RoomYuxiaxieVo();
        BeanUtils.copyProperties(this, roomVo);
        RedisManager.getUserRedisService().getUserBeans(users).forEach(userBean -> roomVo.userList.add(userBean.toVo()));
        if (this.game != null) {
            roomVo.game = this.game.toVo(userId);
            roomVo.setRemainTime(this.game.lastOperateTime + 60 - System.currentTimeMillis());
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

    public Map<Long, Map<Integer, Bet>> getBetHistory() {
        return betHistory;
    }

    public RoomYuxiaxie setBetHistory(Map<Long, Map<Integer, Bet>> betHistory) {
        this.betHistory = betHistory;
        return this;
    }
}
