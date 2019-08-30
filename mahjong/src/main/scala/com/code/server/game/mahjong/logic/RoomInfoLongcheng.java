package com.code.server.game.mahjong.logic;

import com.code.server.constant.exception.DataNotFoundException;
import com.code.server.constant.game.RoomRecord;
import com.code.server.constant.game.RoomStatistics;
import com.code.server.constant.game.UserBean;
import com.code.server.constant.game.UserRecord;
import com.code.server.constant.kafka.IKafaTopic;
import com.code.server.constant.kafka.KafkaMsgKey;
import com.code.server.constant.response.ResponseVo;
import com.code.server.game.room.kafka.MsgSender;
import com.code.server.kafka.MsgProducer;
import com.code.server.redis.service.RedisManager;
import com.code.server.util.SpringUtil;

import java.util.Map;

/**
 * Created by sunxianping on 2019-07-26.
 */
public class RoomInfoLongcheng extends RoomInfo {


    protected void roomAddUser(long userId) {

        this.users.add(userId);
        this.userStatus.put(userId, 0);
        this.userScores.put(userId, 0D);
        this.roomStatisticsMap.put(userId, new RoomStatistics(userId));
        this.canStartUserId = users.get(0);
        //代开房
        if (!isCreaterJoin ||isClubRoom()) this.bankerId = users.get(0);

        addUser2RoomRedis(userId);
    }

    @Override
    public void pushScoreChange() {
//        if (isGoldRoom()) {
//            for(long userId : users){
//                userScores.put(userId, RedisManager.getUserRedisService().getUserGold(userId));
//            }
//        }
        MsgSender.sendMsg2Player(new ResponseVo("gameService", "scoreChange", userScores), this.getUsers());
    }

    @Override
    protected boolean isCanJoinCheckMoney(long userId) {
        if (!super.isCanJoinCheckMoney(userId)) {
            return false;
        }
        UserBean userBean = RedisManager.getUserRedisService().getUserBean(userId);
        long parentId = userBean.getReferee();
        if (parentId == 0) {
            return false;
        }
        if(RedisManager.getUserRedisService().getUserMoney(parentId)<getSameParentNum(parentId)){
            return false;
        }
        return true;
    }
    public void clearReadyStatus(boolean isAddGameNum) {
//        GameManager.getInstance().remove(game);
        lastOperateTime = System.currentTimeMillis();
        this.setGame(null);


        this.setInGame(false);
        for (Map.Entry<Long, Integer> entry : this.userStatus.entrySet()) {
            entry.setValue(STATUS_JOIN);
        }
        if (isAddGameNum) {

            this.curGameNumber += 1;
        }
        //每局的庄家
        this.bankerMap.put(curGameNumber, bankerId);

        //选择状态置成没选过
        this.users.forEach(uid -> {
            if (uid != bankerId) {
                laZhuangStatus.put(uid, false);
            }
        });

    }

    @Override
    public boolean isRoomOver() {
        //金币房 不解散
        return this.getCurGameNumber() >= this.getGameNumber();
    }

    @Override
    public int joinRoom(long userId, boolean isJoin) {
        int rtn =  super.joinRoom(userId, isJoin);
        if (rtn == 0) {
            UserBean userBean = RedisManager.getUserRedisService().getUserBean(userId);
            playerParentMap.put(userId, (long) userBean.getReferee());
            //自动准备
//            getReady(userId);
        }
        return rtn;
    }

    public void spendMoney() {

    }

    public int getNeedMoney() throws DataNotFoundException {

        return 0;
    }


    @Override
    public void addUserSocre(long userId, double score) {
        double s = userScores.get(userId);
        userScores.put(userId, s + score);
//        RedisManager.getUserRedisService().addUserGold(userId, score);
    }


    /**
     * 生成房间战绩
     */
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
        roomRecord.setModelTotal(modeTotal);
        roomRecord.setMode(mode);
        roomRecord.setCurGameNum(curGameNumber);

        final double[] winNum = {0};
        this.userScores.forEach((key, value) -> {
            //抽水
            double newValue = value;
            if (newValue > 0) {
                winNum[0] += newValue;
                newValue = newValue * 92 / 100;
            }
            RedisManager.getUserRedisService().addUserGold(key, newValue);

            UserRecord userRecord = new UserRecord();
            userRecord.setScore(newValue);
            userRecord.setUserId(key);
            UserBean userBean = RedisManager.getUserRedisService().getUserBean(key);
            if (userBean != null) {
                userRecord.setName(userBean.getUsername());
            }
            roomRecord.getRecords().add(userRecord);



        });

        double rebateNum = winNum[0] * 4.8 /100;
        double rebateNum1 = winNum[0] * 3.2 /100;

        this.userScores.forEach((key, value)->{
            sendCenterAddRebateLongcheng(key, rebateNum/4);
        });

        sendCenterAddRebateLongcheng(6789, rebateNum1);

        KafkaMsgKey kafkaMsgKey = new KafkaMsgKey().setMsgId(KAFKA_MSG_ID_ROOM_RECORD);
        MsgProducer msgProducer = SpringUtil.getBean(MsgProducer.class);
        msgProducer.send(IKafaTopic.CENTER_TOPIC, kafkaMsgKey, roomRecord);

    }


    /**
     * 房间中同样的上级玩家个数
     * @param pid
     * @return
     */
    private long getSameParentNum(long pid) {
        return getPlayerParentMap().values().stream().filter(parentId->pid == parentId).count();
    }


    protected void goldRoomStart() {
        for (long userId : users) { //扣除费用
            long parentId = playerParentMap.get(userId);
            RedisManager.getUserRedisService().addUserMoney(parentId, -1);
        }
    }
}
