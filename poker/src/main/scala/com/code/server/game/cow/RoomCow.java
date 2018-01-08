package com.code.server.game.cow;

import com.code.server.constant.exception.DataNotFoundException;
import com.code.server.constant.game.IGameConstant;
import com.code.server.constant.game.PrepareRoom;
import com.code.server.constant.game.RoomStatistics;
import com.code.server.constant.game.UserBean;
import com.code.server.constant.response.*;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
public class RoomCow  extends Room {

    protected Map<Long, Integer> allWinNum = new HashMap<>();
    protected Map<Long, Integer> allLoseNum = new HashMap<>();
    protected Map<Long, Integer> cowCowNum = new HashMap<>();
    protected Map<Long, Integer> nullCowNum = new HashMap<>();
    protected Map<Long, Integer> winNum = new HashMap<>();

    public static RoomCow getRoomInstance(String roomType){
        switch (roomType) {
            case "1":
                return new RoomCow();
            default:
                return new RoomCow();
        }
    }

    public static int createCowRoom(long userId, int gameNumber, int personNumber,int multiple, String gameType, String roomType, boolean isAA, boolean isJoin) throws DataNotFoundException {
        RoomCow room = getRoomInstance(roomType);

        room.personNumber = personNumber;
        room.roomId = getRoomIdStr(genRoomId());
        room.createUser = userId;
        room.gameType = gameType;
        room.roomType = roomType;
        room.isAA = isAA;
        room.isCreaterJoin = isJoin;
        room.multiple = multiple;
        room.bankerId = userId;

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

        ServerConfig serverConfig = SpringUtil.getBean(ServerConfig.class);
        RoomManager.addRoom(room.roomId, "" + serverConfig.getServerId(), room);

        IdWorker idWorker = new IdWorker(serverConfig.getServerId(), 0);
        room.setUuid(idWorker.nextId());

        MsgSender.sendMsg2Player(new ResponseVo("pokerRoomService", "createCowRoom", room.toVo(userId)), userId);

        return 0;
    }

    /**
     * 快速开始
     * @param userId
     * @param roomId
     * @return
     */
    public static int startGameByClient(long userId,String roomId){

        RoomCow room = (RoomCow)RoomManager.getRoom(roomId);
        if(room==null){
            return ErrorCode.ROOM_START_CAN_NOT;
        }

        //第一局
        if (room.curGameNumber != 1){
            return ErrorCode.ROOM_START_CAN_NOT;
        }
        //房主已经准备
        if (room.userStatus.get(userId) != IGameConstant.STATUS_READY){
            return ErrorCode.ROOM_START_CAN_NOT;
        }

        //准备的人数大于2
        int readyCount = 0;
        ArrayList<Long> removeList = new ArrayList();
        for (long i:room.userStatus.keySet()) {
            if(IGameConstant.STATUS_READY==room.userStatus.get(i)){
                readyCount++;
            }else{
                removeList.add(i);
            }
        }
        if (readyCount < 2) {
            return ErrorCode.READY_NUM_ERROR;
        }

        //设置persionnum
        room.setPersonNumber(room.userScores.size());

        for (long i:removeList) {
            room.roomRemoveUser(i);
        }

        //游戏开始 代建房 去除定时解散
        if(!room.isOpen && !room.isCreaterJoin()){
            GameTimer.removeNode(room.prepareRoomTimerNode);
        }

        //通知其他人游戏已经开始
        MsgSender.sendMsg2Player(new ResponseVo("gameService", "gameCowBegin", "ok"), room.users);
        MsgSender.sendMsg2Player(new ResponseVo("pokerRoomService", "startGameByClient", 0), userId);


        //开始游戏
        room.startGame();
        return 0;
    }

    @Override
    public void noticeJoinRoom(long userId) {
        List<UserVo> usersList = new ArrayList<>();
        UserOfRoom userOfRoom = new UserOfRoom();
        int readyNumber = 0;

        for (UserBean userBean : RedisManager.getUserRedisService().getUserBeans(users)) {
            userOfRoom.getUserList().add(userBean.toVo());
        }

        userOfRoom.setInRoomNumber(users.size());
        userOfRoom.setReadyNumber(readyNumber);

        Map<Long, Double> scoresMap = new HashMap<>();
        if("30".equals(getGameType())){
            for (Long l : users) {
                scoresMap.put(l, 1000.0);
            }
        }else{
            for (Long l : users) {
                scoresMap.put(l, 0.0);
            }
        }
        userOfRoom.setUserScores(scoresMap);
        userOfRoom.setCanStartUserId(users.get(0));

        MsgSender.sendMsg2Player(new ResponseVo("roomService", "joinRoom", this.toVo(userId)), userId);
        MsgSender.sendMsg2Player(new ResponseVo("roomService", "roomNotice", userOfRoom), this.getUsers());
    }

    public void spendMoney() {
        RedisManager.getUserRedisService().addUserMoney(this.createUser, -createNeedMoney);
    }

    @Override
    public PrepareRoom getPrepareRoomVo() {
        PrepareRoom prepareRoom = new PrepareRoom();
        prepareRoom.createTime = System.currentTimeMillis();
        prepareRoom.gameType = this.getGameType();
        prepareRoom.roomType = this.getRoomType();
        prepareRoom.roomId = this.roomId;
        prepareRoom.multiple = this.multiple;
        prepareRoom.gameNumber = this.gameNumber;
        return prepareRoom;
    }

    protected void roomAddUser(long userId) {

        this.users.add(userId);
        this.userStatus.put(userId, 0);

        if("30".equals(getGameType())){
            this.userScores.put(userId, 1000.0);
        }else{
            this.userScores.put(userId, 0.0);
        }
        this.roomStatisticsMap.put(userId, new RoomStatistics(userId));
        this.canStartUserId = users.get(0);
        addUser2RoomRedis(userId);
    }

    public List<UserOfResult> getUserOfResult() {
        ArrayList<UserOfResult> userOfResultList = new ArrayList<>();
        long time = System.currentTimeMillis();
        for (UserBean eachUser : RedisManager.getUserRedisService().getUserBeans(this.users)) {
            UserOfResult resultObj = new UserOfResult();
            resultObj.setUsername(eachUser.getUsername());
            resultObj.setImage(eachUser.getImage());
            if("30".equals(getGameType())){
                resultObj.setScores(this.userScores.get(eachUser.getId())-1000.0 + "");
            }else{
                resultObj.setScores(this.userScores.get(eachUser.getId()) + "");
            }
            resultObj.setUserId(eachUser.getId());
            resultObj.setTime(time);
            resultObj.setRoomStatistics(roomStatisticsMap.get(eachUser.getId()));

            //设置牌型次数
            if (this.getAllWinNum().containsKey(eachUser.getId())) {
                resultObj.setAllWinNum(this.getAllWinNum().get(eachUser.getId()));
            }
            if (this.getAllLoseNum().containsKey(eachUser.getId())) {
                resultObj.setAllLoseNum(this.getAllLoseNum().get(eachUser.getId()));
            }
            if (this.getCowCowNum().containsKey(eachUser.getId())) {
                resultObj.setCowCowNum(this.getCowCowNum().get(eachUser.getId()));
            }
            if (this.getNullCowNum().containsKey(eachUser.getId())) {
                resultObj.setNullCowNum(this.getNullCowNum().get(eachUser.getId()));
            }
            if (this.getWinNum().containsKey(eachUser.getId())) {
                resultObj.setWinNum(this.getWinNum().get(eachUser.getId()));
            }

            userOfResultList.add(resultObj);
        }
        return userOfResultList;
    }


    public Map<Long, Integer> getAllWinNum() {
        return allWinNum;
    }

    public void setAllWinNum(Map<Long, Integer> allWinNum) {
        this.allWinNum = allWinNum;
    }

    public Map<Long, Integer> getAllLoseNum() {
        return allLoseNum;
    }

    public void setAllLoseNum(Map<Long, Integer> allLoseNum) {
        this.allLoseNum = allLoseNum;
    }

    public Map<Long, Integer> getCowCowNum() {
        return cowCowNum;
    }

    public void setCowCowNum(Map<Long, Integer> cowCowNum) {
        this.cowCowNum = cowCowNum;
    }

    public Map<Long, Integer> getNullCowNum() {
        return nullCowNum;
    }

    public void setNullCowNum(Map<Long, Integer> nullCowNum) {
        this.nullCowNum = nullCowNum;
    }

    public Map<Long, Integer> getWinNum() {
        return winNum;
    }

    public void setWinNum(Map<Long, Integer> winNum) {
        this.winNum = winNum;
    }

    public void addAllWinNum(long userId) {
        if (allWinNum.containsKey(userId)) {
            allWinNum.put(userId, allWinNum.get(userId) + 1);
        } else {
            allWinNum.put(userId, 1);
        }
    }

    public void addAllLoseNum(long userId) {
        if (allLoseNum.containsKey(userId)) {
            allLoseNum.put(userId, allLoseNum.get(userId) + 1);
        } else {
            allLoseNum.put(userId, 1);
        }
    }

    public void addCowCowNum(long userId) {
        if (cowCowNum.containsKey(userId)) {
            cowCowNum.put(userId, cowCowNum.get(userId) + 1);
        } else {
            cowCowNum.put(userId, 1);
        }
    }

    public void addNullCowNum(long userId) {
        if (nullCowNum.containsKey(userId)) {
            nullCowNum.put(userId, nullCowNum.get(userId) + 1);
        } else {
            nullCowNum.put(userId, 1);
        }
    }

    public void addWinNum(long userId) {
        if (winNum.containsKey(userId)) {
            winNum.put(userId, winNum.get(userId) + 1);
        } else {
            winNum.put(userId, 1);
        }
    }

    public IfaceRoomVo toVo(long userId) {
        RoomCowVo roomVo = new RoomCowVo();
        BeanUtils.copyProperties(this, roomVo);
        RedisManager.getUserRedisService().getUserBeans(users).forEach(userBean -> roomVo.userList.add(userBean.toVo()));
        if (this.game != null) {
            roomVo.setUserScores(this.userScores);
            roomVo.game = this.game.toVo(userId);
        }
        if (this.getTimerNode() != null) {
            long time = this.getTimerNode().getStart() + this.getTimerNode().getInterval() - System.currentTimeMillis();
            roomVo.setRemainTime(time);
        }

        return roomVo;
    }
}
