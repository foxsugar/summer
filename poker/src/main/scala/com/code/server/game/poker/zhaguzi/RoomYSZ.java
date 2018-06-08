package com.code.server.game.poker.zhaguzi;

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

public class RoomYSZ extends Room {

    //扎金花专用
    protected double caiFen;
    protected int menPai;
    protected int cricleNumber;//轮数

    protected Map<Long, Integer> baoziNum = new HashMap<>();
    protected Map<Long, Integer> tonghuashunNum = new HashMap<>();
    protected Map<Long, Integer> tonghuaNum = new HashMap<>();
    protected Map<Long, Integer> shunziNum = new HashMap<>();
    protected Map<Long, Integer> duiziNum = new HashMap<>();
    protected Map<Long, Integer> sanpaiNum = new HashMap<>();





//    @Override
//    protected Game getGameInstance() {
//        switch (gameType) {
//            case GAMETYPE_HITGOLDFLOWER:
//                return new GameHitGoldFlower();
//            default:
//                return new GameHitGoldFlower();
//        }
//
//    }


    public static RoomYSZ getRoomInstance(String roomType){
        switch (roomType) {
            case "4":
                return new RoomYSZ();
            default:
                return new RoomYSZ();
        }

    }

    public static int createYSZRoom(long userId, int gameNumber, int personNumber,int cricleNumber,int multiple,int caiFen,int menPai, String gameType, String roomType, boolean isAA, boolean isJoin, String clubId, String clubRoomModel) throws DataNotFoundException {
        ServerConfig serverConfig = SpringUtil.getBean(ServerConfig.class);

        RoomYSZ room = getRoomInstance(roomType);

        room.personNumber = personNumber;
        room.roomId = getRoomIdStr(genRoomId(serverConfig.getServerId()));
        room.createUser = userId;
        room.gameType = gameType;
        room.roomType = roomType;
        room.isAA = isAA;
        room.isCreaterJoin = isJoin;
        room.multiple = multiple;
        room.caiFen = caiFen;
        room.menPai = menPai;
        room.bankerId = userId;
        room.cricleNumber = cricleNumber;
        room.setClubId(clubId);
        room.setClubRoomModel(clubRoomModel);
        room.isRobotRoom = true;
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

        MsgSender.sendMsg2Player(new ResponseVo("pokerRoomService", "createYSZRoom", room.toVo(userId)), userId);

        return 0;
    }

    /**
     * 快速开始
     * @param userId
     * @param roomId
     * @return
     */
    public static int startGameByClient(long userId,String roomId){

        RoomYSZ room = (RoomYSZ)RoomManager.getRoom(roomId);
        if(room==null){
            return ErrorCode.ROOM_START_CAN_NOT;
        }
        //玩家是房主
        /*if (room.createUser != userId){
            return ErrorCode.ROOM_START_NOT_CREATEUSER;
        }*/

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
        MsgSender.sendMsg2Player(new ResponseVo("gameService", "gameYSZBegin", "ok"), room.users);
        MsgSender.sendMsg2Player(new ResponseVo("pokerRoomService", "startYSZGameByClient", 0), userId);


        //开始游戏
        room.startGame();
        return 0;
    }


    @Override
    public void noticeJoinRoom(long userId) {
        List<UserVo> usersList = new ArrayList<>();
        UserOfRoom userOfRoom = new UserOfRoom();
        int readyNumber = 0;
//        for (long uid : users) {
//            User user = this.userMap.get(uid);
//            usersList.add(GameManager.getUserVo(user));
//        }


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
        prepareRoom.caiFen = this.caiFen;
        prepareRoom.menPai = this.menPai;
        prepareRoom.cricleNumber = this.cricleNumber;
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
            if (this.getBaoziNum().containsKey(eachUser.getId())) {
                resultObj.setBaoziNum(this.getBaoziNum().get(eachUser.getId()));
            }
            if (this.getTonghuashunNum().containsKey(eachUser.getId())) {
                resultObj.setTonghuashunNum(this.getTonghuashunNum().get(eachUser.getId()));
            }
            if (this.getTonghuaNum().containsKey(eachUser.getId())) {
                resultObj.setTonghuaNum(this.getTonghuaNum().get(eachUser.getId()));
            }
            if (this.getShunziNum().containsKey(eachUser.getId())) {
                resultObj.setShunziNum(this.getShunziNum().get(eachUser.getId()));
            }
            if (this.getDuiziNum().containsKey(eachUser.getId())) {
                resultObj.setDuiziNum(this.getDuiziNum().get(eachUser.getId()));
            }
            if (this.getSanpaiNum().containsKey(eachUser.getId())) {
                resultObj.setSanpaiNum(this.getSanpaiNum().get(eachUser.getId()));
            }

            userOfResultList.add(resultObj);
        }
        return userOfResultList;
    }



    public Map<Long, Integer> getBaoziNum() {
        return baoziNum;
    }

    public void setBaoziNum(Map<Long, Integer> baoziNum) {
        this.baoziNum = baoziNum;
    }

    public Map<Long, Integer> getTonghuashunNum() {
        return tonghuashunNum;
    }

    public void setTonghuashunNum(Map<Long, Integer> tonghuashunNum) {
        this.tonghuashunNum = tonghuashunNum;
    }

    public Map<Long, Integer> getTonghuaNum() {
        return tonghuaNum;
    }

    public void setTonghuaNum(Map<Long, Integer> tonghuaNum) {
        this.tonghuaNum = tonghuaNum;
    }

    public Map<Long, Integer> getShunziNum() {
        return shunziNum;
    }

    public void setShunziNum(Map<Long, Integer> shunziNum) {
        this.shunziNum = shunziNum;
    }

    public Map<Long, Integer> getDuiziNum() {
        return duiziNum;
    }

    public void setDuiziNum(Map<Long, Integer> duiziNum) {
        this.duiziNum = duiziNum;
    }

    public Map<Long, Integer> getSanpaiNum() {
        return sanpaiNum;
    }

    public void setSanpaiNum(Map<Long, Integer> sanpaiNum) {
        this.sanpaiNum = sanpaiNum;
    }

    public void addBaoziNum(long userId) {
        if (baoziNum.containsKey(userId)) {
            baoziNum.put(userId, baoziNum.get(userId) + 1);
        } else {
            baoziNum.put(userId, 1);
        }
    }

    public void addTonghuashunNum(long userId) {
        if (tonghuashunNum.containsKey(userId)) {
            tonghuashunNum.put(userId, tonghuashunNum.get(userId) + 1);
        } else {
            tonghuashunNum.put(userId, 1);
        }
    }

    public void addTonghuaNum(long userId) {
        if (tonghuaNum.containsKey(userId)) {
            tonghuaNum.put(userId, tonghuaNum.get(userId) + 1);
        } else {
            tonghuaNum.put(userId, 1);
        }
    }

    public void addShunziNum(long userId) {
        if (shunziNum.containsKey(userId)) {
            shunziNum.put(userId, shunziNum.get(userId) + 1);
        } else {
            shunziNum.put(userId, 1);
        }
    }

    public void addDuiziNum(long userId) {
        if (duiziNum.containsKey(userId)) {
            duiziNum.put(userId, duiziNum.get(userId) + 1);
        } else {
            duiziNum.put(userId, 1);
        }
    }

    public void addSanpaiNum(long userId) {
        if (sanpaiNum.containsKey(userId)) {
            sanpaiNum.put(userId, sanpaiNum.get(userId) + 1);
        } else {
            sanpaiNum.put(userId, 1);
        }
    }


    public IfaceRoomVo toVo(long userId) {
        RoomHitGoldFlowerVo roomVo = new RoomHitGoldFlowerVo();
        BeanUtils.copyProperties(this, roomVo);
        RedisManager.getUserRedisService().getUserBeans(users).forEach(userBean -> roomVo.userList.add(userBean.toVo()));
        if (this.game != null) {
            Map<Long,Double> userScoresTemp = new HashMap<>();
            GameBaseYSZ gameTemp = (GameBaseYSZ)this.getGame();
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


    public double getCaiFen() {
        return caiFen;
    }

    public void setCaiFen(double caiFen) {
        this.caiFen = caiFen;
    }

    public int getMenPai() {
        return menPai;
    }

    public void setMenPai(int menPai) {
        this.menPai = menPai;
    }

    public int getCricleNumber() {
        return cricleNumber;
    }

    public void setCricleNumber(int cricleNumber) {
        this.cricleNumber = cricleNumber;
    }
}
