package com.code.server.game.poker.tuitongzi;

import com.code.server.constant.exception.DataNotFoundException;
import com.code.server.constant.game.IGameConstant;
import com.code.server.constant.response.ErrorCode;
import com.code.server.constant.response.IfaceRoomVo;
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

public class RoomTuiTongZi extends PokerGoldRoom {

    private long bankerScore;

    private long bankerInitScore;

    private long potBottom;

    private long zhuangCount;

    protected long firstBankerId = -1;

    protected long firstBanerCount = 0;

    protected long cardsCount;

    protected long roomLastTime;

    protected int quan;

    public int getQuan() {
        return quan;
    }

    public void setQuan(int quan) {
        this.quan = quan;
    }

    protected long offset = 0;

    //作弊的那个人info
    protected Map<String, Object> cheatInfo = new HashMap<>();

    protected List<Integer> cards = new ArrayList<Integer>();

    public void setCardsCount(long cardsCount) {
        this.cardsCount = cardsCount;
    }

    public List<Integer> getCards() {
        return cards;
    }

    public void setCards(List<Integer> cards) {
        this.cards = cards;
    }

    public long getFirstBankerId() {
        return firstBankerId;
    }

    public void setFirstBankerId(long firstBankerId) {
        this.firstBankerId = firstBankerId;
    }

    public long getFirstBanerCount() {
        return firstBanerCount;
    }

    public void setFirstBanerCount(long firstBanerCount) {
        this.firstBanerCount = firstBanerCount;
    }

    public long getPotBottom() {
        return potBottom;
    }

    public long getBankerScore() {
        return bankerScore;
    }

    public long getZhuangCount() {
        return zhuangCount;
    }

    public void setZhuangCount(long zhuangCount) {
        this.zhuangCount = zhuangCount;
    }

    public void setBankerScore(long bankerScore) {
        this.bankerScore = bankerScore;
    }

    public void setPotBottom(long potBottom) {
        this.potBottom = potBottom;
    }

    public long getBankerInitScore() {
        return bankerInitScore;
    }

    public void setBankerInitScore(long bankerInitScore) {
        this.bankerInitScore = bankerInitScore;
    }

    public static RoomTuiTongZi getRoomInstance(String roomType) {
        switch (roomType) {
            case "5":
                return new RoomTuiTongZi();
            default:
                return new RoomTuiTongZi();
        }
    }

    protected long nextTurnId(long curId) {

        int index = users.indexOf(curId);

        int nextId = index + 1;
        if (nextId >= users.size()) {
            nextId = 0;
        }
        return users.get(nextId);
    }

    public IfaceRoomVo toVo(long user) {

        GameTuiTongZi gameTuiTongZi = (GameTuiTongZi) this.getGame();

        RoomTuiTongZiVo roomVo = new RoomTuiTongZiVo();
        BeanUtils.copyProperties(this, roomVo);
        RedisManager.getUserRedisService().getUserBeans(users).forEach(userBean -> roomVo.userList.add(userBean.toVo()));
        if (this.game != null) {
            roomVo.game = this.game.toVo(user);
        }
        if (this.getTimerNode() != null) {
            long time = this.getTimerNode().getStart() + this.getTimerNode().getInterval() - System.currentTimeMillis();
            roomVo.setRemainTime(time);
        }

        roomVo.zhuangCount = this.zhuangCount;
        roomVo.bankerId = this.bankerId;
        roomVo.potBottom = this.potBottom;

        if (users.size() > 0) {
            if (nextTurnId(this.bankerId) == firstBankerId) {
                roomVo.firstBanerCount = this.firstBanerCount - 1;
            } else {
                roomVo.firstBanerCount = this.firstBanerCount;
            }
        } else {
            roomVo.firstBanerCount = 0;
        }

        return roomVo;
    }


    public static int createRoom(long userId, String roomType, String gameType, int gameNumber, int personNumber, boolean isJoin, int multiple, String clubId, String clubRoomModel, int quan) throws DataNotFoundException {
        ServerConfig serverConfig = SpringUtil.getBean(ServerConfig.class);

        RoomTuiTongZi room = getRoomInstance(roomType);
        room.quan = quan;
        room.personNumber = personNumber;
        room.roomId = getRoomIdStr(genRoomId(serverConfig.getServerId()));
        room.createUser = userId;
        room.gameType = gameType;
        room.isCreaterJoin = isJoin;
        room.multiple = multiple;
        room.bankerId = userId;
        room.roomType = roomType;
        room.isRobotRoom = true;
        room.setClubId(clubId);
        room.setClubRoomModel(clubRoomModel);

        room.init(gameNumber, multiple);

        int code = room.joinRoom(userId, isJoin);
        if (code != 0) {
            return code;
        }

        //代建房 定时解散
        if (!isJoin && !room.isClubRoom()) {
            //给代建房 开房者 扣钱
            if (RedisManager.getUserRedisService().getUserMoney(userId) < room.createNeedMoney) {
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

        MsgSender.sendMsg2Player(new ResponseVo("pokerRoomService", "createTTZRoom", room.toVo(userId)), userId);

        return 0;
    }

    @Override
    public int startGameByClient(long userId) {

        if (this.users.get(0) != userId) {
            return ErrorCode.ROOM_START_NOT_CREATEUSER;
        }

//        if (this.curGameNumber != 6) return ErrorCode.ROOM_START_CAN_NOT;

        //第一局
        if (this.curGameNumber != 1) return ErrorCode.ROOM_START_CAN_NOT;

        if (userStatus.get(userId) != IGameConstant.STATUS_READY) return ErrorCode.ROOM_START_CAN_NOT;

        //防止多次点开始
        if (this.game != null) return ErrorCode.ROOM_START_CAN_NOT;

        int readyCount = 0;
        for (Map.Entry<Long, Integer> entry : userStatus.entrySet()) {

            Integer status = entry.getValue();
            if (status == IGameConstant.STATUS_READY) readyCount++;
        }

        if (readyCount < 2) return ErrorCode.READY_NUM_ERROR;

        this.setPersonNumber(userScores.size());
        //没准备的人
        ArrayList<Long> removeList = new ArrayList<>();

        for (Map.Entry<Long, Integer> entry : userStatus.entrySet()) {
            Integer status = entry.getValue();

            if (status != IGameConstant.STATUS_READY) {
                removeList.add(entry.getKey());
            }
        }

        for (Long removeId : removeList) {
            roomRemoveUser(removeId);
        }

//        super.startGame();
        //通知其他人游戏已经开始
        MsgSender.sendMsg2Player(new ResponseVo("gameService", "gameTTZBegin", "ok"), this.getUsers());
        MsgSender.sendMsg2Player(new ResponseVo("roomService", "startTTZGameByClient", 0), userId);

//        GameTuiTongZi gameTuiTongZi = (GameTuiTongZi) getGameInstance();
//        this.game = gameTuiTongZi;

        startGame();
//        game.startGame(users, this);

        //游戏开始 代建房 去除定时解散
        if (!isOpen && !this.isCreaterJoin) GameTimer.removeNode(prepareRoomTimerNode);

        //扣钱
        if (!isOpen && isCreaterJoin) spendMoney();
        this.isInGame = true;
        this.isOpen = true;
//        pushScoreChange();
        return 0;
    }


    public boolean isCheat() {
        if (this.cheatInfo.size() == 0) {
            return false;
        }
        return true;
    }

    public void clearCheat() {
        this.cheatInfo.clear();
    }

    @Override
    protected void dissolutionRoom() {
//        if (this.game != null){
//            this.addUserSocre(this.getBankerId(), this.getPotBottom() - ((GameTuiTongZi)this.game).offset());
//        }
        if (this.users.contains(this.getBankerId())) {

            this.addUserSocre(this.getBankerId(), this.getPotBottom() - this.offset);
        }
        super.dissolutionRoom();
    }

    public long getRoomLastTime() {
        return roomLastTime;
    }

    public void setRoomLastTime(long roomLastTime) {
        this.roomLastTime = roomLastTime;
    }
}
