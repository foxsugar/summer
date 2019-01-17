package com.code.server.game.poker.zhaguzi;
import com.code.server.constant.exception.DataNotFoundException;
import com.code.server.constant.game.IGameConstant;
import com.code.server.constant.response.ErrorCode;
import com.code.server.constant.response.IfaceRoomVo;
import com.code.server.constant.response.ResponseVo;
import com.code.server.game.poker.config.ServerConfig;
import com.code.server.game.poker.tuitongzi.GameTuiTongZi;
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

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by dajuejinxian on 2018/5/2.
 */
public class RoomZhaGuZi extends Room{

    //第一个发牌的人的Id
    protected long lastWinnderId;
    protected String showCard;

    public long getLastWinnderId() {
        return lastWinnderId;
    }

    public void setLastWinnderId(long lastWinnderId) {
        this.lastWinnderId = lastWinnderId;
    }

    public String getShowCard() {
        return showCard;
    }

    public void setShowCard(String showCard) {
        this.showCard = showCard;
    }

    public IfaceRoomVo toVo(long user){

        GameZhaGuZi game = (GameZhaGuZi) this.getGame();

        RoomZhaGuZiVo roomVo = new RoomZhaGuZiVo();
        BeanUtils.copyProperties(this, roomVo);
        RedisManager.getUserRedisService().getUserBeans(users).forEach(userBean -> roomVo.userList.add(userBean.toVo()));
        if (this.game != null) {
            roomVo.game = this.game.toVo(user);
        }
        if (this.getTimerNode() != null) {
            long time = this.getTimerNode().getStart() + this.getTimerNode().getInterval() - System.currentTimeMillis();
            roomVo.setRemainTime(time);
        }

        BeanUtils.copyProperties(this,roomVo);

        return roomVo;
    }

    public static int createRoom(long userId, String roomType,String gameType, int gameNumber, int personNumber,
                                 boolean isJoin, int multiple, String clubId,
                                 String clubRoomModel, String isShowCard, int otherMode) throws DataNotFoundException {

        ServerConfig serverConfig = SpringUtil.getBean(ServerConfig.class);
        RoomZhaGuZi room = new RoomZhaGuZi();
        room.personNumber = personNumber;
        room.roomId = getRoomIdStr(genRoomId(serverConfig.getServerId()));
        room.createUser = userId;
        room.gameType = gameType;
        room.isCreaterJoin = isJoin;
        room.multiple = multiple;
        room.bankerId = userId;
        room.roomType = roomType;
        room.showCard = isShowCard;
        room.isRobotRoom = true;
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

        MsgSender.sendMsg2Player(new ResponseVo("pokerRoomService", "createZGZRoom", room.toVo(userId)), userId);

        return 0;
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

        if (readyCount < 5) return ErrorCode.READY_NUM_ERROR;

        this.setPersonNumber(userScores.size());
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
        MsgSender.sendMsg2Player(new ResponseVo("gameService", "gameZhaGuZiBegin", "ok"), this.getUsers());
        MsgSender.sendMsg2Player(new ResponseVo("roomService", "startGameByClient", 0), userId);

        GameTuiTongZi gameTuiTongZi = (GameTuiTongZi) getGameInstance();
        this.game = gameTuiTongZi;
        game.startGame(users, this);

        //游戏开始 代建房 去除定时解散
        if (!isOpen && !this.isCreaterJoin) GameTimer.removeNode(prepareRoomTimerNode);

        //扣钱
        if (!isOpen && isCreaterJoin) spendMoney();
        this.isInGame = true;
        this.isOpen = true;
        return 0;
    }
}
