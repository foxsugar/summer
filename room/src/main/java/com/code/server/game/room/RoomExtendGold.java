package com.code.server.game.room;

import com.code.server.constant.data.DataManager;
import com.code.server.constant.data.StaticDataProto;
import com.code.server.constant.exception.DataNotFoundException;
import com.code.server.constant.game.PrepareRoom;
import com.code.server.constant.game.RoomStatistics;
import com.code.server.constant.game.UserBean;
import com.code.server.constant.kafka.IKafaTopic;
import com.code.server.constant.kafka.IkafkaMsgId;
import com.code.server.constant.kafka.KafkaMsgKey;
import com.code.server.constant.response.ErrorCode;
import com.code.server.constant.response.ResponseVo;
import com.code.server.game.room.kafka.MsgSender;
import com.code.server.game.room.service.RoomManager;
import com.code.server.kafka.MsgProducer;
import com.code.server.redis.service.RedisManager;
import com.code.server.util.SpringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sunxianping on 2018/4/11.
 */
public class RoomExtendGold extends Room {


    public void init(int gameNumber, int multiple) throws DataNotFoundException {
        super.init(gameNumber, multiple);
//        this.isRobotRoom = true;

        if (isGoldRoom()) {
            this.multiple = goldRoomType;
        }
    }


    protected void roomAddUser(long userId) {

        this.users.add(userId);
        this.userStatus.put(userId, 0);
        this.userScores.put(userId, 0D);
        if (isGoldRoom()) {
            this.userScores.put(userId, RedisManager.getUserRedisService().getUserGold(userId));
        }
        this.roomStatisticsMap.put(userId, new RoomStatistics(userId));
        this.canStartUserId = users.get(0);
        //代开房
        if (!isCreaterJoin ||isClubRoom()) this.bankerId = users.get(0);

        addUser2RoomRedis(userId);
    }

    @Override
    public void pushScoreChange() {
        if (isGoldRoom()) {
            for(long userId : users){
                userScores.put(userId, RedisManager.getUserRedisService().getUserGold(userId));
            }
        }
        MsgSender.sendMsg2Player(new ResponseVo("gameService", "scoreChange", userScores), this.getUsers());
    }



    @Override
    public void startGame() {
        goldRoomStart();
        super.startGame();
        //记录局数
        RedisManager.getLogRedisService().addGameNum(getGameLogKeyStr(), 1);
    }

    protected void goldRoomStart() {
        if (isGoldRoom()) {
            if (!this.users.contains(this.bankerId)) {
                this.bankerId = this.users.get(0);
            }
            double cost = this.getGoldRoomType() / 10;

            for (long userId : users) {
                //扣除费用
                RedisManager.getUserRedisService().addUserGold(userId, -cost);
                //返利
                UserBean userBean = RedisManager.getUserRedisService().getUserBean(userId);
                RedisManager.getAgentRedisService().addRebate(userId, userBean.getReferee(), 1, cost / 100,cost);
            }
            //
            RedisManager.getLogRedisService().addGoldIncome(getGameLogKeyStr(), cost * users.size());
        }
    }

    @Override
    public void addUserSocre(long userId, double score) {
        super.addUserSocre(userId, score);
        //todo 金币改变
        if (isGoldRoom()) {
            RedisManager.getUserRedisService().addUserGold(userId, score);

        }

    }

    /**
     * 是否给代理返利
     *
     * @return
     */
    protected boolean isGiveAgentRebate() {

        return false;
    }


    /**
     * 发送返利
     * @param userId
     * @param money
     */
    public void sendCenterAddRebateLongcheng(long userId, double money){
        Map<String, Object> addMoney = new HashMap<>();
        addMoney.put("userId", userId);
        addMoney.put("money", money);
        KafkaMsgKey kafkaMsgKey = new KafkaMsgKey().setMsgId(IkafkaMsgId.KAFKA_MSG_ID_ADD_REBATE_LONGCHENG);
        MsgProducer msgProducer = SpringUtil.getBean(MsgProducer.class);
        msgProducer.send(IKafaTopic.CENTER_TOPIC, kafkaMsgKey, addMoney);
    }


    public void sendSpendMoneyLongcheng(long userId, double money){
        Map<String, Object> addMoney = new HashMap<>();
        addMoney.put("userId", userId);
        addMoney.put("money", money);
        addMoney.put("type", this.roomType);
        KafkaMsgKey kafkaMsgKey = new KafkaMsgKey().setMsgId(IkafkaMsgId.KAFKA_MSG_ID_MONEY_SPEND);
        MsgProducer msgProducer = SpringUtil.getBean(MsgProducer.class);
        msgProducer.send(IKafaTopic.CENTER_TOPIC, kafkaMsgKey, addMoney);
    }

    @Override
    public boolean isGoldRoom() {
        return goldRoomPermission != GOLD_ROOM_PERMISSION_NONE;
    }

    @Override
    public int joinRoom(long userId, boolean isJoin, int... seat) {
        //随机匹配的金币房
        if (isGoldRoom()) {
            int rtn = super.joinRoom(userId, isJoin, seat);
            if (rtn != 0) {
                return rtn;
            }

            //如果房间已满 加入已满房间
            if (goldRoomPermission == GOLD_ROOM_PERMISSION_DEFAULT && this.isRoomFull()) {
                RoomManager.getInstance().moveGoldRoomNotFull2Full(this);
            }

//            getReady(userId);
            return 0;
        } else {
            return super.joinRoom(userId, isJoin,seat);
        }

    }

    @Override
    protected boolean isCanJoinCheckMoney(long userId) {
        //todo 检验金币
        if (isGoldRoom()) {
            double gold = RedisManager.getUserRedisService().getUserGold(userId);
            if (gold < getEnterGold()) {
                return false;
            }
        } else {

            return super.isCanJoinCheckMoney(userId);
        }
        return true;
    }

    @Override
    public int quitRoom(long userId) {
        if (isGoldRoom()) {
            if (!this.users.contains(userId)) {
                return ErrorCode.CANNOT_QUIT_ROOM_NOT_EXIST;
            }

            if (isInGame && this.game.users.contains(userId)) {
                return ErrorCode.CANNOT_QUIT_ROOM_IS_IN_GAME;
            }

//            List<Long> noticeList = new ArrayList<>();
//            noticeList.addAll(this.getUsers());

            //删除玩家房间映射关系
            roomRemoveUser(userId);


            if (goldRoomPermission == GOLD_ROOM_PERMISSION_DEFAULT) {
                RoomManager.getInstance().moveFull2NotFullRoom(this);
            }

            //todo 如果都退出了  删除房间
            if (this.users.size() == 0 ) {

                RoomManager.removeRoom(this.roomId);
            }
            noticeQuitRoom(userId);
            return 0;
        } else return super.quitRoom(userId);
    }

    protected boolean isRoomFull() {
        return this.users.size() >= personNumber;
    }


    @Override
    public boolean isRoomOver() {
        //金币房 不解散
        return !isGoldRoom() && super.isRoomOver();
    }

    @Override
    public void clearReadyStatus(boolean isAddGameNum) {

        super.clearReadyStatus(isAddGameNum);
        //todo 如果 金币不够 退出
        clearReadyStatusGoldRoom(isAddGameNum);
    }


    public void clearReadyStatusGoldRoom(boolean isAddGameNum) {
        if (isGoldRoom()) {
            int minGold = getOutGold();
            List<Long> removeList = new ArrayList<>();
            for (long userId : this.users) {
                double gold = RedisManager.getUserRedisService().getUserGold(userId);
                if (gold < minGold) {
                    removeList.add(userId);
                }
            }

            for (long userId : removeList) {
                this.quitRoom(userId);
            }

            //

        }
    }

    /**
     * 最小金币
     *
     * @return
     */
    protected int getOutGold() {
        StaticDataProto.RoomData roomData = DataManager.data.getRoomDataMap().get(gameType);
        if (roomData != null) {
            return roomData.getOutGoldMap().get(goldRoomType);
        }
        return this.getMultiple() * 20;
    }

    /**
     * 最小进场金币
     *
     * @return
     */
    protected int getEnterGold() {
        StaticDataProto.RoomData roomData = DataManager.data.getRoomDataMap().get(gameType);
        if (roomData != null) {

            return roomData.getEnterGoldMap().get(goldRoomType);
        }
        return this.getMultiple() * 20;
    }


    @Override
    public boolean isRobotRoom() {
        if (isGoldRoom()) {
            return true;
        }else{
            return super.isRobotRoom();
        }
    }

    /**
     * 获得类型下所有金币房
     * @param gameType
     * @return
     */
    public static Map<String, Object> getGoldRoomsVo(String gameType) {
        Map<String, Object> result = new HashMap<>();
        List<PrepareRoom> list = new ArrayList<>();
        Map<Integer, List<Room>> map = RoomManager.getInstance().getPublicGoldRoom().get(gameType);
        if (map != null) {
            for (List<Room> l : map.values()) {
                for (Room r : l) {
                    if (r.getGoldRoomPermission() == Room.GOLD_ROOM_PERMISSION_PUBLIC) {
                        list.add(r.getSimpleVo());
                    }
                }
            }
        }
        result.put("rooms", list);
        return result;
    }
}
