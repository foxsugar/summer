package com.code.server.game.poker.zhaguzi;
import com.code.server.constant.exception.DataNotFoundException;
import com.code.server.constant.game.*;
import com.code.server.constant.response.*;
import com.code.server.game.poker.config.ServerConfig;
import com.code.server.game.room.RoomExtendGold;
import com.code.server.game.room.kafka.MsgSender;
import com.code.server.game.room.service.RoomManager;
import com.code.server.redis.config.IConstant;
import com.code.server.redis.service.RedisManager;
import com.code.server.util.DateUtil;
import com.code.server.util.IdWorker;
import com.code.server.util.SpringUtil;
import com.code.server.util.timer.GameTimer;
import com.code.server.util.timer.TimerNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class RoomYSZ extends RoomExtendGold {

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

    protected long lastReadyTime;
    protected long timerTick;
    protected long leaveSecond;
    protected long lastOverTime;
    public static int BASE_TIME = 3;
    protected static final Logger logger = LoggerFactory.getLogger(RoomYSZ.class);


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

    public static int createYSZRoom(long userId, int gameNumber, int personNumber, int cricleNumber, int multiple, int caiFen,
                                    int menPai, String gameType, String roomType, boolean isAA, boolean isJoin,
                                    String clubId, String clubRoomModel, int goldRoomType, int goldRoomPermission) throws DataNotFoundException {

       return  createYSZRoom(userId, gameNumber, personNumber, cricleNumber, multiple,caiFen, menPai, gameType, roomType, isAA, isJoin, clubId, clubRoomModel, goldRoomType, goldRoomPermission, null);
    }


    public static RoomYSZ createYSZRoom_(long userId, int gameNumber, int personNumber, int cricleNumber, int multiple, int caiFen,
                                    int menPai, String gameType, String roomType, boolean isAA, boolean isJoin,
                                    String clubId, String clubRoomModel, int goldRoomType, int goldRoomPermission) throws DataNotFoundException {
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
//        room.isRobotRoom = true;
        room.goldRoomType = goldRoomType;
        room.goldRoomPermission = goldRoomPermission;
        room.init(gameNumber, multiple);

//        RoomManager.addRoom(room.roomId, "" + serverConfig.getServerId(), room);
        IdWorker idWorker = new IdWorker(serverConfig.getServerId(), 0);
        room.setUuid(idWorker.nextId());

        return room;
    }



    public static int createYSZRoom(long userId, int gameNumber, int personNumber, int cricleNumber, int multiple, int caiFen,
                                    int menPai, String gameType, String roomType, boolean isAA, boolean isJoin,
                                    String clubId, String clubRoomModel, int goldRoomType, int goldRoomPermission, Supplier<RoomYSZ> supplier) throws DataNotFoundException {
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
//        room.isRobotRoom = true;
        room.goldRoomType = goldRoomType;
        room.goldRoomPermission = goldRoomPermission;
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

        if (supplier != null){
            supplier.get();
        }

        MsgSender.sendMsg2Player(new ResponseVo("pokerRoomService", "createYSZRoom", room.toVo(userId)), userId);


        return 0;
    }


    @Override
    public void addUserSocre(long userId, double score) {

        double s = userScores.get(userId);
        userScores.put(userId, s + score);
        //todo 金币改变
        if (isGoldRoom()) {
            RedisManager.getUserRedisService().addUserGold(userId, score);
        }
    }

//    public void addUserSocre(long userId, double score) {
//        double s = userScores.get(userId);
//        userScores.put(userId, s + score);
//        RoomStatistics roomStatistics = roomStatisticsMap.get(userId);
//        if (roomStatistics != null) {
//            roomStatistics.maxScore = roomStatistics.maxScore > score ? roomStatistics.maxScore : score;
//            if (score >= 0) {
//                roomStatistics.winTime += 1;
//            } else {
//                roomStatistics.failedTime += 1;
//            }
//        }
//    }
//
//    @Override
//    public void addUserSocre(long userId, double score) {
//        super.addUserSocre(userId, score);
//        //todo 金币改变
//        if (isGoldRoom()) {
//            RedisManager.getUserRedisService().addUserGold(userId, score);
//
//        }
//
//    }


    protected void goldRoomStart() {
        if (isGoldRoom()) {
            if (!this.users.contains(this.bankerId)) {
                this.bankerId = this.users.get(0);
            }
            double cost = this.getGoldRoomType() / 10;
            //50底分 抽成翻倍
            if (this.getGoldRoomType() == 50) {
                cost *= 2;
            }

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

        //防止多次点开始
        if(room.game != null) return ErrorCode.ROOM_START_CAN_NOT;

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
    protected int getOutGold() {
        if (isGoldRoom() && this.goldRoomPermission != GOLD_ROOM_PERMISSION_DEFAULT) {
            //todo 根据闷牌 得到出场限制
//            return super.getOutGold();
            return computeEnterGold() / 2;
        } else{
            return super.getOutGold();
        }
    }

    @Override
    protected int getEnterGold() {
        if (isGoldRoom() && this.goldRoomPermission != GOLD_ROOM_PERMISSION_DEFAULT) {

            //todo 根据闷牌 得到进场限制
//            return super.getEnterGold();
            return computeEnterGold();
        } else{
            return super.getEnterGold();
        }
    }


    public  int computeEnterGold(){
        int enter = 0;
        if (this.menPai == 0){

            if (this.goldRoomType == 50){
                enter = 1000;
            }else if (this.goldRoomType == 100){
                enter = 2000;
            }else if (this.goldRoomType == 200){
                enter = 5000;
            }else if (this.goldRoomType == 500){
                enter = 10000;
            }

        }else if (this.menPai == 3){
            if (this.goldRoomType == 50){
                enter = 1000;
            }else if (this.goldRoomType == 100){
                enter = 2000;
            }else if (this.goldRoomType == 200){
                enter = 5000;
            }else if (this.goldRoomType == 500){
                enter = 10000;
            }

        }else if (this.menPai == 5){

            if (this.goldRoomType == 50){
                enter = 1000;
            }else if (this.goldRoomType == 100){
                enter = 2000;
            }else if (this.goldRoomType == 200){
                enter = 5000;
            }else if (this.goldRoomType == 500){
                enter = 15000;
            }
        }

        return enter;
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

        isTickTimer();

        if (this.game != null && this.isInGame){
            GameYSZ gameYSZ = (GameYSZ) this.game;
            gameYSZ.updateAliveUsers();
        }

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
        prepareRoom.goldRoomType = this.goldRoomType;
        prepareRoom.goldRoomPermission = this.goldRoomPermission;
        return prepareRoom;
    }


    @Override
    public GameLogKey getGameLogKey() {
        GameLogKey gameLogKey = super.getGameLogKey();
        gameLogKey.getParams().put("menpai", "" + this.menPai);

        return gameLogKey;
    }

    protected void roomAddUser(long userId) {

        this.users.add(userId);
        this.userStatus.put(userId, 0);

        if("30".equals(getGameType())){
            this.userScores.put(userId, 1000.0);
        }else{
            this.userScores.put(userId, 0.0);
        }
        if (isGoldRoom()) {
            this.userScores.put(userId, RedisManager.getUserRedisService().getUserGold(userId));
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


    @Override
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

        //开始游戏 房卡场直接开始 金币场走倒计时

        if (readyNum >= personNumber && !isGoldRoom()) {
            startGame();
        }
        MsgSender.sendMsg2Player(new ResponseVo("roomService", "getReady", 0), userId);
        if (isGoldRoom()) {
            MsgSender.sendMsg2Player(new ResponseVo("roomService", "getReadyGoldRoom", 0), userId);
        }






            this.lastReadyTime = System.currentTimeMillis();

        isTickTimer();
        return 0;
    }

    public void isTickTimer(){

        long lastTimerTick = timerTick;
        if (this.users.size() < 2){
            timerTick = 0;
        }else {
            boolean ret = this.userStatus.values()
                    .stream()
                    .allMatch( (x) -> x == STATUS_READY);

            if (ret){
                timerTick = 1;
            }else {
                timerTick = 0;
            }
        }

        if (timerTick != lastTimerTick){

            if (this.game != null) return;

            long deta = (System.currentTimeMillis() - this.lastReadyTime) / ((long)(10 * Math.pow(10, 9)));
            this.leaveSecond = BASE_TIME - deta;
            Map<String, Object> result = new HashMap<>();
            result.put("second", this.leaveSecond);
            if (timerTick == 0){
                result.put("second", 0);
            }
            result.put("timerTick", timerTick);
            logger.info("================tickTimer{},当前时间{}===={}", result, System.currentTimeMillis(), DateUtil.timeStampToTimeString(System.currentTimeMillis()));
            MsgSender.sendMsg2Player(new ResponseVo("roomService", "tickTimer", result), this.users);
        }
    }

    protected void noticeQuitRoom(long userId) {
        super.noticeQuitRoom(userId);
        this.lastReadyTime = System.currentTimeMillis();
        this.isTickTimer();

        if (this.game != null && this.isInGame){
            GameYSZ gameYSZ = (GameYSZ) this.game;
            gameYSZ.updateAliveUsers();
        }
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
        RoomYSZVo roomVo = new RoomYSZVo();
        BeanUtils.copyProperties(this, roomVo);
        RedisManager.getUserRedisService().getUserBeans(users).forEach(userBean -> roomVo.userList.add(userBean.toVo()));
        if (this.game != null) {
            Map<Long,Double> userScoresTemp = new HashMap<>();
            GameBaseYSZ gameTemp = (GameBaseYSZ)this.getGame();//暂时这样写
            if(gameTemp!=null){
                for (Long l: gameTemp.getPlayerCardInfos().keySet()){
//                    userScoresTemp.put(l,gameTemp.startUserScores.get(l)-gameTemp.getPlayerCardInfos().get(l).getAllScore());
                    userScoresTemp.put(l, this.userScores.get(l));
                }
            }
            roomVo.setUserScores(userScoresTemp);
            roomVo.game = this.game.toVo(userId);
        }
        if (this.getTimerNode() != null) {
            long time = this.getTimerNode().getStart() + this.getTimerNode().getInterval() - System.currentTimeMillis();
            roomVo.setRemainTime(time);
        }

        if (this.game != null){
            this.timerTick = 0;
        }

        long deta = (System.currentTimeMillis() - this.lastReadyTime) / ((long)(10 * Math.pow(10, 9)));
        this.leaveSecond = BASE_TIME - deta;
        if (this.timerTick == 0){
            this.leaveSecond = 0;
        }

        roomVo.timerTick = this.timerTick;
        roomVo.leaveSecond = this.leaveSecond;
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

    public long getLastReadyTime() {
        return lastReadyTime;
    }

    public RoomYSZ setLastReadyTime(long lastReadyTime) {
        this.lastReadyTime = lastReadyTime;
        return this;
    }
}
