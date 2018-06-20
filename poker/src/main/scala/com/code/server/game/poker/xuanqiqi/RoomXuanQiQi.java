package com.code.server.game.poker.xuanqiqi;

import com.code.server.constant.exception.DataNotFoundException;
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
public class RoomXuanQiQi extends Room {

    /*
        底分  multiple
     */
    protected int cricleNumber;//轮数

    /**
     * 结算版3,5,6罗记录
     */
    protected Map<Long, Integer> numThree = new HashMap<>();
    protected Map<Long, Integer> numFive = new HashMap<>();
    protected Map<Long, Integer> numSix = new HashMap<>();

    protected Map<Long, Integer> winNumXQQ = new HashMap<>();


    public static RoomXuanQiQi getRoomInstance(String roomType){
        switch (roomType) {
            case "9":
                return new RoomXuanQiQi();
            default:
                return new RoomXuanQiQi();
        }

    }

    public static int createXuanQiQiRoom(long userId, int gameNumber, int personNumber,int cricleNumber,int multiple, String gameType, String roomType, boolean isAA, boolean isJoin, String clubId, String clubRoomModel) throws DataNotFoundException {
        ServerConfig serverConfig = SpringUtil.getBean(ServerConfig.class);

        RoomXuanQiQi room = getRoomInstance(roomType);

        room.personNumber = personNumber;
        room.roomId = getRoomIdStr(genRoomId(serverConfig.getServerId()));
        room.createUser = userId;
        room.gameType = gameType;
        room.roomType = roomType;
        room.isAA = isAA;
        room.isCreaterJoin = isJoin;
        room.multiple = multiple;
        room.bankerId = userId;
        room.cricleNumber = cricleNumber;
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

        MsgSender.sendMsg2Player(new ResponseVo("pokerRoomService", "createXuanQiQiRoom", room.toVo(userId)), userId);

        return 0;
    }

    public List<UserOfResult> getUserOfResult() {
        ArrayList<UserOfResult> userOfResultList = new ArrayList<>();
        long time = System.currentTimeMillis();
        for (UserBean eachUser : RedisManager.getUserRedisService().getUserBeans(this.users)) {
            UserOfResult resultObj = new UserOfResult();
            resultObj.setUsername(eachUser.getUsername());
            resultObj.setImage(eachUser.getImage());
            resultObj.setScores(this.userScores.get(eachUser.getId()) + "");
            resultObj.setUserId(eachUser.getId());
            resultObj.setTime(time);
            resultObj.setRoomStatistics(roomStatisticsMap.get(eachUser.getId()));

            //设置牌型次数
            if (this.getNumThree().containsKey(eachUser.getId())) {
                resultObj.setNumThree(this.getNumThree().get(eachUser.getId()));
            }
            if (this.getNumFive().containsKey(eachUser.getId())) {
                resultObj.setNumFive(this.getNumFive().get(eachUser.getId()));
            }
            if (this.getNumSix().containsKey(eachUser.getId())) {
                resultObj.setNumSix(this.getNumSix().get(eachUser.getId()));
            }
            if (this.getWinNumXQQ().containsKey(eachUser.getId())) {
                resultObj.setWinNumXQQ(this.getWinNumXQQ().get(eachUser.getId()));
            }

            userOfResultList.add(resultObj);
        }
        return userOfResultList;
    }

    public IfaceRoomVo toVo(long userId) {
        RoomXuanQiQiVo roomVo = new RoomXuanQiQiVo();

        BeanUtils.copyProperties(this, roomVo);
        RedisManager.getUserRedisService().getUserBeans(users).forEach(userBean -> roomVo.userList.add(userBean.toVo()));
        if (this.game != null) {
            Map<Long,Double> userScoresTemp = new HashMap<>();
            GameXuanQiQi gameTemp = (GameXuanQiQi)this.getGame();
            if(gameTemp!=null){
                for (Long l: gameTemp.getPlayerCardInfos().keySet()){
                    userScoresTemp.put(l,this.userScores.get(l)-gameTemp.getPlayerCardInfos().get(l).getAllScore());
                }
            }
            roomVo.setUserScores(userScoresTemp);
            roomVo.game = this.game.toVo(userId);
        }
        if (this.getTimerNode() != null) {
            long time = this.getTimerNode().getStart() + this.getTimerNode().getInterval() - System.currentTimeMillis();
            roomVo.setRemainTime(time);
        }

        return roomVo;
    }






    public void addWinNumXQQ(long userId) {
        if (winNumXQQ.containsKey(userId)) {
            winNumXQQ.put(userId, winNumXQQ.get(userId) + 1);
        } else {
            winNumXQQ.put(userId, 1);
        }
    }


    public void addNumThree(long userId) {
        if (numThree.containsKey(userId)) {
            numThree.put(userId, numThree.get(userId) + 1);
        } else {
            numThree.put(userId, 1);
        }
    }

    public void addNumFive(long userId) {
        if (numFive.containsKey(userId)) {
            numFive.put(userId, numFive.get(userId) + 1);
        } else {
            numFive.put(userId, 1);
        }
    }

    public void addNumSix(long userId) {
        if (numSix.containsKey(userId)) {
            numSix.put(userId, numSix.get(userId) + 1);
        } else {
            numSix.put(userId, 1);
        }
    }

    public int getCricleNumber() {
        return cricleNumber;
    }

    public void setCricleNumber(int cricleNumber) {
        this.cricleNumber = cricleNumber;
    }

    public Map<Long, Integer> getNumThree() {
        return numThree;
    }

    public void setNumThree(Map<Long, Integer> numThree) {
        this.numThree = numThree;
    }

    public Map<Long, Integer> getNumFive() {
        return numFive;
    }

    public void setNumFive(Map<Long, Integer> numFive) {
        this.numFive = numFive;
    }

    public Map<Long, Integer> getNumSix() {
        return numSix;
    }

    public void setNumSix(Map<Long, Integer> numSix) {
        this.numSix = numSix;
    }

    public Map<Long, Integer> getWinNumXQQ() {
        return winNumXQQ;
    }

    public void setWinNumXQQ(Map<Long, Integer> winNumXQQ) {
        this.winNumXQQ = winNumXQQ;
    }
}
