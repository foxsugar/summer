package com.code.server.game.poker.paijiu;

import com.code.server.constant.exception.DataNotFoundException;
import com.code.server.constant.game.RoomRecord;
import com.code.server.constant.game.RoomStatistics;
import com.code.server.constant.game.UserBean;
import com.code.server.constant.game.UserRecord;
import com.code.server.constant.kafka.IKafaTopic;
import com.code.server.constant.kafka.KafkaMsgKey;
import com.code.server.constant.response.GameOfResult;
import com.code.server.constant.response.ResponseVo;
import com.code.server.constant.response.UserOfResult;
import com.code.server.game.poker.config.ServerConfig;
import com.code.server.game.room.Room;
import com.code.server.game.room.kafka.MsgSender;
import com.code.server.game.room.service.RoomManager;
import com.code.server.kafka.MsgProducer;
import com.code.server.redis.service.RedisManager;
import com.code.server.util.IdWorker;
import com.code.server.util.SpringUtil;
import com.code.server.util.timer.GameTimer;

import java.time.LocalDateTime;
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
public class RoomGoldPaijiu extends RoomPaijiu {

    protected int isGold;
    protected int goldType;

    public Map<Long, Double> userScoresForGold = new HashMap<>();


    public static int createGoldRoom(Long userId,String roomType,String gameType,int gameNumber,int isGold,int goldType) throws DataNotFoundException {
        RoomGoldPaijiu roomGoldPaijiu = new RoomGoldPaijiu();
        roomGoldPaijiu.setRoomId(Room.getRoomIdStr(Room.genRoomId()));
        roomGoldPaijiu.setRoomType(roomType);
        roomGoldPaijiu.setGameType(gameType);
        roomGoldPaijiu.setGameNumber(gameNumber);
        roomGoldPaijiu.setBankerId(userId);
        roomGoldPaijiu.setCreateUser(userId);
        roomGoldPaijiu.setPersonNumber(4);
        roomGoldPaijiu.setIsGold(isGold);
        roomGoldPaijiu.setGoldType(goldType);
        roomGoldPaijiu.init(gameNumber, 1);
        int code = roomGoldPaijiu.joinRoom(userId, true);
        if (code != 0){
            return code;
        }

        ServerConfig serverConfig = SpringUtil.getBean(ServerConfig.class);
        RoomManager.addRoom(roomGoldPaijiu.getRoomId(), "" + serverConfig.getServerId(), roomGoldPaijiu);
        IdWorker idword = new IdWorker(serverConfig.getServerId(), 0);
        roomGoldPaijiu.setUuid(idword.nextId());

        MsgSender.sendMsg2Player(new ResponseVo("pokerRoomService", "createPaijiuGoldRoom", roomGoldPaijiu.toVo(userId)), userId);
        return 0;
    }

    @Override
    public void roomAddUser(long userId) {

        this.users.add(userId);
        this.userStatus.put(userId, 0);
        this.userScores.put(userId, RedisManager.getUserRedisService().getUserMoney(userId));
        this.roomStatisticsMap.put(userId, new RoomStatistics(userId));
        this.canStartUserId = users.get(0);
        this.userScoresForGold.put(userId, 0.0);
        addUser2RoomRedis(userId);
    }

    /**
     * 解散房间
     */
    @Override
    public void dissolutionRoom() {
        //庄家初始分 再减掉
        RoomManager.removeRoom(this.roomId);
        // 结果类
        List<UserOfResult> userOfResultList = getUserOfResult();

        //代开房 并且游戏未开始
        if (!isCreaterJoin && !this.isInGame && (this.curGameNumber == 1)) {
            drawBack();
            GameTimer.removeNode(this.prepareRoomTimerNode);
        }
        this.isInGame = false;

        // 存储返回
        GameOfResult gameOfResult = new GameOfResult();
        for (UserOfResult u:userOfResultList) {
            double d = Double.parseDouble(u.getScores());
            if(u.getUserId()== this.getBankerId()){
                u.setScores(d+"");
                RedisManager.getUserRedisService().addUserMoney(u.getUserId(), d - this.bankerInitScore());//userId-money
            }else{
                u.setScores(d-RedisManager.getUserRedisService().getUserMoney(u.getUserId())+"");
                RedisManager.getUserRedisService().addUserMoney(u.getUserId(), d - RedisManager.getUserRedisService().getUserMoney(u.getUserId()));//userId-money
            }
            MsgSender.sendMsg2Player(new ResponseVo("userService", "refresh", 0), u.getUserId());

        }
        gameOfResult.setUserList(userOfResultList);
        gameOfResult.setEndTime(LocalDateTime.now().toString());
        MsgSender.sendMsg2Player(new ResponseVo("gameService", "askNoticeDissolutionResult", gameOfResult), users);

        //庄家初始分 再减掉
        this.addUserSocre(this.getBankerId(), -this.bankerInitScore());

        //战绩
        genRoomRecord();
    }

    /**
     * 生成房间战绩
     */
    @Override
    public void genRoomRecord() {
        RoomRecord roomRecord = new RoomRecord();
        roomRecord.setRoomId(this.roomId);
        roomRecord.setId(this.getUuid());
        roomRecord.setType(this.roomType);
        roomRecord.setTime(System.currentTimeMillis());
        roomRecord.setClubId(this.getClubId());
        roomRecord.setClubRoomModel(this.getClubRoomModel());

        for(Long l :userScoresForGold.keySet()){
            UserRecord userRecord = new UserRecord();
            userRecord.setScore(userScoresForGold.get(l));
            userRecord.setUserId(l);
            UserBean userBean = RedisManager.getUserRedisService().getUserBean(l);
            if (userBean != null) {
                userRecord.setName(userBean.getUsername());
            }
            roomRecord.getRecords().add(userRecord);
        }


        KafkaMsgKey kafkaMsgKey = new KafkaMsgKey().setMsgId(KAFKA_MSG_ID_ROOM_RECORD);
        MsgProducer msgProducer = SpringUtil.getBean(MsgProducer.class);
        msgProducer.send(IKafaTopic.CENTER_TOPIC, kafkaMsgKey, roomRecord);

    }

    //房间列表
    public static int getAllRoom(long userId){
        List<Map<String,Object>> rooms = new ArrayList<>();
        RoomManager.getInstance().getRooms().values().forEach(r->{
            Map<String, Object> result = new HashMap<>();
            RoomGoldPaijiu roomGoldPaijiu = (RoomGoldPaijiu) r;
            result.put("roomId", roomGoldPaijiu.getRoomId());
            result.put("nickName",RedisManager.getUserRedisService().getUserBean(((RoomGoldPaijiu) r).getCreateUser()).getUsername());
            result.put("persionNum", roomGoldPaijiu.getUsers().size());
            result.put("goldType", roomGoldPaijiu.getGoldType());
            rooms.add(result);
        });
        MsgSender.sendMsg2Player("pokerRoomService", "getAllGoldPaijiuRoom", rooms, userId);
        return 0;
    }


    public int getIsGold() {
        return isGold;
    }

    public void setIsGold(int isGold) {
        this.isGold = isGold;
    }

    public int getGoldType() {
        return goldType;
    }

    public void setGoldType(int goldType) {
        this.goldType = goldType;
    }

    protected boolean isCanJoinCheckMoney(long userId) {
        //代建房
        if (!isCreaterJoin) {
            return true;
        }
        if (isAA) {
            if (RedisManager.getUserRedisService().getUserMoney(userId) < createNeedMoney) {
                return false;
            }
        } else {
            if (userId == createUser) {
                if (RedisManager.getUserRedisService().getUserMoney(userId) < createNeedMoney) {
                    return false;
                }
            }
        }
        if("JBPJ".equals(gameType)){//金币扎金花
            if(RedisManager.getUserRedisService().getUserMoney(userId) <100){
                return false;
            }
        }else if("203".equals(gameType)){
            if(RedisManager.getUserRedisService().getUserMoney(userId) < this.goldType){
                return false;
            }
        }
        return true;
    }

    @Override
    public void pushScoreChange() {
        Map<Long, Double> temp = new HashMap<>();
        for (Long l:userScores.keySet()) {
            if(this.bankerId!=l){
                temp.put(l,userScores.get(l));
            }else{
                temp.put(l,RedisManager.getUserRedisService().getUserMoney(l)+userScores.get(l)-this.bankerInitScore());
            }
        }

        MsgSender.sendMsg2Player(new ResponseVo("gameService", "scoreChange", temp), this.getUsers());
    }

    public Map<Long, Double> getUserScoresForGold() {
        return userScoresForGold;
    }

    public void setUserScoresForGold(Map<Long, Double> userScoresForGold) {
        this.userScoresForGold = userScoresForGold;
    }
}
