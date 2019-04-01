package com.code.server.game.poker.pullmice;

import com.code.server.constant.exception.DataNotFoundException;
import com.code.server.constant.game.IGameConstant;
import com.code.server.constant.response.ErrorCode;
import com.code.server.constant.response.IfaceRoomVo;
import com.code.server.constant.response.NoticeReady;
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
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoomPullMice extends PokerGoldRoom {

    protected List<Integer> cards = new ArrayList<>();

    protected long potBottom;

    protected long maxGameCount;

    protected long cardsTotal;

    protected boolean canWuBuFeng;

    private static final int PERSONNUM  = 5;

    protected long lastWinnerId = -1;

    //上一局房间内人数
    protected long lastPersonNum;

    protected List<Object> debugList = new ArrayList<>();
    protected List<Object> debugPlayerList = new ArrayList<>();

    protected Map<String, Object> cheatInfo = new HashMap<>();
    Map<Long, PlayerPullMice> fakePlayerInfos = new HashMap<>();

    public long getPotBottom() {
        return potBottom;
    }

    public void setPotBottom(long potBottom) {
        this.potBottom = potBottom;
    }

    public long getMaxGameCount() {
        return maxGameCount;
    }

    public void setMaxGameCount(long maxGameCount) {
        this.maxGameCount = maxGameCount;
    }

    public long getCardsTotal() {
        return cardsTotal;
    }

    public void setCardsTotal(long cardsTotal) {
        this.cardsTotal = cardsTotal;
    }

    public List<Integer> getCards() {
        return cards;
    }

    public void setCards(List<Integer> cards) {
        this.cards = cards;
    }

    public boolean isCanWuBuFeng() {
        return canWuBuFeng;
    }

    public void setCanWuBuFeng(boolean canWuBuFeng) {
        this.canWuBuFeng = canWuBuFeng;
    }

    @Override
    public IfaceRoomVo toVo(long userId) {

        RoomPullMiceVo roomVo = new RoomPullMiceVo();
        BeanUtils.copyProperties(this, roomVo);
        roomVo.cardsTotal = this.cards.size();
        RedisManager.getUserRedisService().getUserBeans(users).forEach(userBean -> roomVo.userList.add(userBean.toVo()));
        if (this.game != null) {
            roomVo.game = this.game.toVo(userId);
        }
        if (this.getTimerNode() != null) {
            long time = this.getTimerNode().getStart() + this.getTimerNode().getInterval() - System.currentTimeMillis();
            roomVo.setRemainTime(time);
        }

        return roomVo;
    }

    public static int createRoom(long userId, String roomType,String gameType, int gameNumber, int personNumber, boolean isJoin, int multiple,
                                 boolean hasWubuFeng, String clubId, String clubRoomModel,int clubMode) throws DataNotFoundException {

        ServerConfig serverConfig = SpringUtil.getBean(ServerConfig.class);

        RoomPullMice room = new RoomPullMice();
        room.personNumber = PERSONNUM;
        room.roomId = getRoomIdStr(genRoomId(serverConfig.getServerId()));
        room.createUser = userId;
        room.gameType = gameType;
        room.isCreaterJoin = isJoin;
        room.multiple = multiple;
        room.bankerId = userId;
        room.roomType = roomType;
        room.setCanWuBuFeng(hasWubuFeng);
        room.setClubId(clubId);
        room.setClubRoomModel(clubRoomModel);
        room.setClubMode(clubMode);
        room.init(gameNumber, multiple);


        //假设最大局数是8局
        room.maxGameCount = gameNumber;

        int code = room.joinRoom(userId, isJoin);
        if (code != 0) {
            return code;
        }

        //代建房 定时解散
        if (!isJoin) {
            //给代建房 开房者 扣钱
            if(RedisManager.getUserRedisService().getUserMoney(userId) < room.createNeedMoney){
                RoomManager.removeRoom(room.getRoomId());
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

        MsgSender.sendMsg2Player(new ResponseVo("pokerRoomService", "createPullMiceRoom", room.toVo(userId)), userId);

        return 0;
    }

    public void clearDebug(){
        this.debugPlayerList.clear();
        this.debugList.clear();
    }

    @Override
    public int startGameByClient(long userId) {

        if (this.users.get(0) != userId){
            return ErrorCode.ROOM_START_NOT_CREATEUSER;
        }

        if (this.curGameNumber == 1){
            this.clearDebug();
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
        lastPersonNum = userScores.size();
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
        MsgSender.sendMsg2Player(new ResponseVo("gameService", "gamePullMiceBegin", "ok"), this.getUsers());
        MsgSender.sendMsg2Player(new ResponseVo("pokerRoomService", "startPullMiceGameByClient", 0), userId);

        GamePullMice game = (GamePullMice) getGameInstance();
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

    //是否可以作弊
    protected boolean isCheat(){
        return this.fakePlayerInfos.size() == 0 ? false : true;
    }

    //清除作弊信息
    protected void clearCheatInfo(){
        this.fakePlayerInfos.clear();
        this.cheatInfo.clear();
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
        if (this.users.size() >= PERSONNUM) {
            return ErrorCode.CANNOT_JOIN_ROOM_IS_FULL;

        }
        if (RedisManager.getUserRedisService().getRoomId(userId) != null) {
            return ErrorCode.CANNOT_CREATE_ROOM_USER_HAS_IN_ROOM;
        }
        if (!isCanJoinCheckMoney(userId)) {
            return ErrorCode.CANNOT_JOIN_ROOM_NO_MONEY;
        }

        //最后一局不能加入房间
        if (this.gameNumber == this.curGameNumber){
            return ErrorCode.CANNOT_JOIN_ROOM_NO_MONEY;
        }

        if (isJoin) {
            roomAddUser(userId);
            personNumber = this.users.size();
            //加进玩家-房间映射表
            noticeJoinRoom(userId);
        }

        return 0;
    }

    public int getReady(long userId) {
        if (!this.users.contains(userId)) {
            return ErrorCode.CANNOT_FIND_THIS_USER;
        }
        if (isInGame) {
            return ErrorCode.CANNOT_FIND_THIS_USER;
        }

        this.userStatus.put(userId, STATUS_READY);

        int readyNum = 0;
        for (Map.Entry<Long, Integer> entry : this.userStatus.entrySet()) {
            if (entry.getValue() == STATUS_READY) {
                readyNum += 1;
            }
        }

        //通知客户端谁是否准备
        Map<String, Integer> userStatus = new HashMap<>();
        for (Long i : this.userStatus.keySet()) {
            userStatus.put(i + "", this.userStatus.get(i));
        }
        NoticeReady noticeReady = new NoticeReady();
        noticeReady.setUserStatus(userStatus);
        MsgSender.sendMsg2Player(new ResponseVo("roomService", "noticeReady", noticeReady), this.users);

        //开始游戏

        if (this.curGameNumber == 1){
            if (readyNum >= PERSONNUM) {
                startGame();
            }
        }else if (readyNum >= personNumber) {
            startGame();
        }
        MsgSender.sendMsg2Player(new ResponseVo("roomService", "getReady", 0), userId);
        return 0;
    }

}
