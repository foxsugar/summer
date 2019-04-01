package com.code.server.game.poker.tiandakeng;

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

import static com.code.server.game.poker.tiandakeng.GameTDK.model_允许观战;

/**
 * Created by sunxianping on 2018-10-18.
 */
public class RoomTDK extends PokerGoldRoom {


    private boolean isLanGuo = false;//是否烂锅

    private Map<Integer, Boolean> languoMap = new HashMap<>();

    private List<Integer> languoBets = new ArrayList<>();

    private int xifen = 0;

    private int noComputeXifen = 0;

    private long shamelessUser = 0;

    private int huanpai = 0;


    public static int createRoom(long userId, int gameNumber, int multiple, String gameType, String roomType,
                                 boolean isAA, boolean isJoin, boolean showChat, int personNum,
                                 String clubId, String clubRoomModel,int clubMode,int otherMode) throws DataNotFoundException {
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
        room.setBankerId(userId);




        room.setClubId(clubId);
        room.setClubRoomModel(clubRoomModel);
        room.setClubMode(clubMode);
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


    @Override
    public int joinRoom(long userId, boolean isJoin) {
        if((this.isOpen || this.users.size()>=this.personNumber) && isHasMode(model_允许观战, this.getOtherMode())){
            return ErrorCode.CANNOT_JOIN_ROOM_WATCH;
        }
        return super.joinRoom(userId, isJoin);
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
        MsgSender.sendMsg2Player(new ResponseVo("pokerRoomService", "startTDKGameByClient", 0), userId);

        GameTDK game = new GameTDK();
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
    public IfaceRoomVo toVo(long userId) {
        RoomTDKVo roomVo = new RoomTDKVo();
        BeanUtils.copyProperties(this, roomVo);
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

    public List<Long> getWatchUser() {
        return watchUser;
    }

    public RoomTDK setWatchUser(List<Long> watchUser) {
        this.watchUser = watchUser;
        return this;
    }

    public boolean isLanGuo() {
        return isLanGuo;
    }

    public RoomTDK setLanGuo(boolean lanGuo) {
        isLanGuo = lanGuo;
        return this;
    }

    public List<Integer> getLanguoBets() {
        return languoBets;
    }

    public RoomTDK setLanguoBets(List<Integer> languoBets) {
        this.languoBets = languoBets;
        return this;
    }

    public int getXifen() {
        return xifen;
    }

    public RoomTDK setXifen(int xifen) {
        this.xifen = xifen;
        return this;
    }

    public int getNoComputeXifen() {
        return noComputeXifen;
    }

    public RoomTDK setNoComputeXifen(int noComputeXifen) {
        this.noComputeXifen = noComputeXifen;
        return this;
    }

    public void addNoComputeXifen(int xifen) {
        this.noComputeXifen += xifen;
        this.xifen += xifen;
    }

    public long getShamelessUser() {
        return shamelessUser;
    }

    public RoomTDK setShamelessUser(long shamelessUser) {
        this.shamelessUser = shamelessUser;
        return this;
    }

    public int getHuanpai() {
        return huanpai;
    }

    public RoomTDK setHuanpai(int huanpai) {
        this.huanpai = huanpai;
        return this;
    }

    public Map<Integer, Boolean> getLanguoMap() {
        return languoMap;
    }

    public RoomTDK setLanguoMap(Map<Integer, Boolean> languoMap) {
        this.languoMap = languoMap;
        return this;
    }
}
