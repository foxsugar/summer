package com.code.server.game.poker.yuxiaxie;

import com.code.server.constant.exception.DataNotFoundException;
import com.code.server.constant.game.*;
import com.code.server.constant.kafka.IKafaTopic;
import com.code.server.constant.kafka.KafkaMsgKey;
import com.code.server.constant.response.*;
import com.code.server.game.poker.config.ServerConfig;
import com.code.server.game.poker.service.PokerGoldRoom;
import com.code.server.game.room.Room;
import com.code.server.game.room.kafka.MsgSender;
import com.code.server.game.room.service.RoomManager;
import com.code.server.kafka.MsgProducer;
import com.code.server.redis.config.IConstant;
import com.code.server.redis.service.RedisManager;
import com.code.server.util.IdWorker;
import com.code.server.util.SpringUtil;
import com.code.server.util.timer.GameTimer;
import com.code.server.util.timer.TimerNode;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sunxianping on 2018-11-30.
 */
public class RoomYuxiaxie extends PokerGoldRoom {


    //单压限分
    private int danya;
    //串联限分
    private int chuanlian;
    //豹子限分
    private int baozi;


    //挪次数
    private int nuo;

    Map<Integer,List<Integer>> diceHistory = new HashMap<>();

    Map<Long, Map<Integer,List<Bet>>> betHistory = new HashMap<>();

    Map<Long, Map<Integer, Integer>> userScoreHistory = new HashMap<>();


    public static int createRoom(long userId, int gameNumber, int multiple, String gameType, String roomType,
                                 boolean isAA, boolean isJoin, boolean showChat, int personNum,
                                 String clubId, String clubRoomModel,int otherMode,int danya, int chuanlian, int baozi, int nuo) throws DataNotFoundException {
        ServerConfig serverConfig = SpringUtil.getBean(ServerConfig.class);
        RoomYuxiaxie room = new RoomYuxiaxie();

        room.personNumber = personNum;

        room.roomId = getRoomIdStr(genRoomId(serverConfig.getServerId()));
        room.createUser = userId;
        room.gameType = gameType;
        room.roomType = roomType;
        room.isAA = isAA;
        room.isCreaterJoin = isJoin;
        room.showChat = showChat;
        room.otherMode = otherMode;
        room.setRobotRoom(true);
        room.setBankerId(userId);




        room.setClubId(clubId);
        room.setClubRoomModel(clubRoomModel);
        room.init(gameNumber, multiple);

        if (room.isClubRoom()) {
            room.setBankerId(0);
        }

        room.setDanya(danya);
        room.setBaozi(baozi);
        room.setChuanlian(chuanlian);
        room.setNuo(nuo);

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

        MsgSender.sendMsg2Player(new ResponseVo("pokerRoomService", "createYXXRoom", room.toVo(userId)), userId);
        return 0;
    }



    public int dissolution(long userId, boolean agreeOrNot, String methodName) {
        if (!this.users.contains(userId)) {
            return ErrorCode.CANNOT_FIND_THIS_USER;

        }

        this.userStatus.put(userId, agreeOrNot ? STATUS_AGREE_DISSOLUTION : STATUS_DISSOLUTION);

        //第一个点解散
        if (agreeOrNot && !isHasDissolutionRequest) {
            dissloutionUser = userId;
            isCanDissloution = true;
            this.isHasDissolutionRequest = true;
            //第一次申请 五分钟后解散
            long start = System.currentTimeMillis();
            TimerNode node = new TimerNode(start, 1000 * 60, false, () -> {

                if (isCanDissloution) {
                    dissolutionRoom();
                }

            });
            this.timerNode = node;
            GameTimer.addTimerNode(node);
        }


        ArrayList<AnswerUser> answerUsers = new ArrayList<>();
        for (int i = 0; i < this.users.size(); i++) {
            AnswerUser answerUser = new AnswerUser();
            answerUser.setUserId(this.users.get(i) + "");
            if (this.userStatus.get(this.users.get(i)) == STATUS_DISSOLUTION) {
                answerUser.setAnswer("3");
            } else if (this.userStatus.get(this.users.get(i)) == STATUS_AGREE_DISSOLUTION) {
                answerUser.setAnswer("2");
            } else {
                answerUser.setAnswer("1");
            }
            answerUsers.add(answerUser);
        }

        AskQuitRoom accept = new AskQuitRoom();
        accept.setUserId(userId + "");
        accept.setAnswerList(answerUsers);
        MsgSender.sendMsg2Player(new ResponseVo("roomService", "noticeAnswerIfDissolveRoom", accept), this.users);


        int agreeNum = 0;
        int disAgreeNum = 0;
        for (int status : userStatus.values()) {
            if (status == STATUS_AGREE_DISSOLUTION) {
                agreeNum += 1;
            }
            if (status == STATUS_DISSOLUTION) {
                disAgreeNum += 1;
            }
        }

        //同意解散
        if (isCanAgreeDissloution(agreeNum)) {
            GameTimer.removeNode(timerNode);
            dissolutionRoom();
        }
        //不同意的人数大于等于1 解散取消
        if (disAgreeNum >= 1) {
            for (Map.Entry<Long, Integer> entry : userStatus.entrySet()) {
                //回到游戏状态
                entry.setValue(STATUS_IN_GAME);
                this.isHasDissolutionRequest = false;
                GameTimer.removeNode(timerNode);
            }
        }


        AskQuitRoom accept1 = new AskQuitRoom();
        accept1.setUserId("" + userId);
        MsgSender.sendMsg2Player("roomService", "noticeDissolveRoom", accept1, users);

        AskQuitRoom send = new AskQuitRoom();
        send.setNote("ok");
        MsgSender.sendMsg2Player("roomService", methodName, send, userId);

        return 0;
    }

    private long getWinner(){
        double score = 0;
        long uid = 0;
        for (Map.Entry<Long, Double> entry : userScores.entrySet()) {
            if (entry.getKey() != this.bankerId) {
                if (entry.getValue() > score) {
                    score = entry.getValue();
                    uid = entry.getKey();
                }
            }
        }
        return uid;
    }


    protected void roomAddUser(long userId) {

        this.users.add(userId);
        this.userStatus.put(userId, 0);
        this.userScores.put(userId, 0D);
        this.roomStatisticsMap.put(userId, new RoomStatistics(userId));
        if (!isClubRoom()) {
            this.canStartUserId = users.get(0);
        }
        if (isClubRoom()) {
            this.userScores.put(userId, RedisManager.getClubRedisService().getClubUserMoney(this.clubId, userId));
        }

        if (!isCreaterJoin ) this.bankerId = users.get(0);
        addUser2RoomRedis(userId);
    }

    @Override
    public int joinRoom(long userId, boolean isJoin) {
        int rtn = super.joinRoom(userId, isJoin);
        if (rtn != 0) {
            return rtn;
        }
        getReady(userId);

        if (this.game != null) {

            if (!this.game.getUsers().contains(userId)) {

                this.game.users.add(userId);
                PlayerInfoYuxiaxie playerInfoYuxiaxie = new PlayerInfoYuxiaxie();
                playerInfoYuxiaxie.setUserId(userId);
                ((GameYuxiaxie) this.game).playerCardInfos.put(userId, playerInfoYuxiaxie);
            }
        }
        return 0;
    }

    public int getYXXDiceHistory(long userId){
        List<List<Integer>> result = new ArrayList<>();
        diceHistory.forEach((key,value)->{
            if (key != this.curGameNumber) {
                result.add(value);
            }
        });

        MsgSender.sendMsg2Player(new ResponseVo("pokerRoomService", "getYXXDiceHistory", result), userId);
        return 0;
    }

    public int setBankerByClient(long userId, long bankerId) {
        if (!this.users.contains(bankerId)) {
            return ErrorCode.CANNOT_SET_BANKER;
        }
        this.setBankerId(bankerId);
        this.canStartUserId = bankerId;
        Map<String, Object> result = new HashMap<>();
        result.put("bankerId", bankerId);
        List<Long> all = new ArrayList<>();
        all.addAll(users);
        all.addAll(watchUser);
        MsgSender.sendMsg2Player(new ResponseVo("pokerRoomService", "setBanker", result), all);
        MsgSender.sendMsg2Player(new ResponseVo("pokerRoomService", "setBankerByClient", 0), userId);
        return 0;
    }

    public int getYXXBetHistory(long userId, boolean all, int gameNum){

        if (gameNum == 0) {
            gameNum = this.curGameNumber;
        }
        Map<String, Object> result = new HashMap<>();
        result.put("all", all);
        Map<Long, List<Bet>> bets = new HashMap<>();
        result.put("bets", bets);
        if(all){
            int finalGameNum = gameNum;
            this.betHistory.forEach((uid, bs) -> bets.put(uid, bs.getOrDefault(finalGameNum, new ArrayList<>())));
        }else{
            bets.put(userId,this.betHistory.getOrDefault(userId, new HashMap<>()).getOrDefault(gameNum ,new ArrayList<>()));
        }
        MsgSender.sendMsg2Player(new ResponseVo("pokerRoomService", "getYXXBetHistory", result), userId);
        return 0;
    }

    public void genRoomRecord() {
//        if (!isOpen) return;
        RoomRecord roomRecord = new RoomRecord();
        roomRecord.setRoomId(this.roomId);
        roomRecord.setId(this.getUuid());
        roomRecord.setType(this.roomType);
        roomRecord.setTime(System.currentTimeMillis());
        roomRecord.setClubId(clubId);
        roomRecord.setClubRoomModel(clubRoomModel);
        roomRecord.setGameType(gameType);
        roomRecord.setCurGameNum(this.curGameNumber);
        roomRecord.setAllGameNum(this.gameNumber);
        roomRecord.setOpen(this.isOpen);
        //winner
        roomRecord.setWinnerId(getWinner());
        roomRecord.setBankerId(this.bankerId);

        roomRecord.getOtherInfo().put("diceHistory", this.diceHistory);
//        if (!this.isClubRoom()) {
            roomRecord.getOtherInfo().put("betHistory", this.betHistory);
//        }
        roomRecord.getOtherInfo().put("userScoreHistory", this.userScoreHistory);
        roomRecord.getOtherInfo().put("playerNum", 10);
        roomRecord.getOtherInfo().put("otherMode", this.otherMode);


        this.userScores.forEach((key, value) -> {
            UserRecord userRecord = new UserRecord();
            //总分

            int sum = this.userScoreHistory.getOrDefault(key, new HashMap<>()).values().stream().mapToInt(Integer::intValue).sum();
            userRecord.setScore(sum);
            userRecord.setUserId(key);
            UserBean userBean = RedisManager.getUserRedisService().getUserBean(key);
            if (userBean != null) {
                userRecord.setName(userBean.getUsername());
                userRecord.setImage(userBean.getImage());
            }
            roomRecord.getRecords().add(userRecord);
        });

        KafkaMsgKey kafkaMsgKey = new KafkaMsgKey().setMsgId(KAFKA_MSG_ID_ROOM_RECORD);
        MsgProducer msgProducer = SpringUtil.getBean(MsgProducer.class);
        msgProducer.send(IKafaTopic.CENTER_TOPIC, kafkaMsgKey, roomRecord);

    }




    public void pushScoreChange() {
        if (isClubRoom()) {
            for(long userId : users){
                userScores.put(userId, RedisManager.getClubRedisService().getClubUserMoney(this.clubId, userId));
            }
        }
        MsgSender.sendMsg2Player(new ResponseVo("gameService", "scoreChange", userScores), this.getUsers());
        MsgSender.sendMsg2Player(new ResponseVo("gameService", "scoreChange", userScores), this.watchUser);
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
            resultObj.setHistoryScore(this.userScoreHistory.get(eachUser.getId()));
            resultObj.setBetHistory(this.betHistory.get(eachUser.getId()));
            resultObj.setDiceHistory(this.diceHistory);
            userOfResultList.add(resultObj);

        }
        return userOfResultList;
    }

    protected boolean isCanAgreeDissloution(int agreeNum) {
        return agreeNum >= this.users.size()  && agreeNum >= 2;
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

        //开始游戏
        //开始游戏
        if (this.curGameNumber == 1) {
            if (readyNum > this.users.size()  ) {
                startGame();
            }
        }else{
            if (readyNum >= 1 ) {
                startGame();
            }
        }
        MsgSender.sendMsg2Player(new ResponseVo("roomService", "getReady", 0), userId);
        if (isGoldRoom()) {
            MsgSender.sendMsg2Player(new ResponseVo("roomService", "getReadyGoldRoom", 0), userId);
        }
        return 0;
    }

    public int quitRoom(long userId) {
        if (!this.users.contains(userId)) {
            return ErrorCode.CANNOT_QUIT_ROOM_NOT_EXIST;

        }

        if (!isClubRoom()) {

            if (isInGame) {
                return ErrorCode.CANNOT_QUIT_ROOM_IS_IN_GAME;
            }
        }

        if (isClubRoom()) {
            if (this.game != null) {

                GameYuxiaxie gameYuxiaxie = (GameYuxiaxie) this.game;
                PlayerInfoYuxiaxie playerInfoYuxiaxie = gameYuxiaxie.playerCardInfos.get(userId);
                if (playerInfoYuxiaxie.getBets().size() > 0 ) {
                    return ErrorCode.CANNOT_QUIT_ROOM_IS_IN_GAME;
                }

                //从game中退出
                gameYuxiaxie.playerCardInfos.remove(userId);
                gameYuxiaxie.users.remove(userId);


//                this.setPersonNumber(userScores.size());
            }

        }
        List<Long> noticeList = new ArrayList<>();
        noticeList.addAll(this.getUsers());

        //删除玩家房间映射关系
        roomRemoveUser(userId);

//        GameManager.getInstance().getUserRoom().remove(userId);

        if (this.createUser == userId) {//房主解散

            Notice n = new Notice();
            n.setMessage("roomNum " + this.getRoomId() + " :has destroy success!");
            MsgSender.sendMsg2Player(new ResponseVo("roomService", "destroyRoom", n), noticeList);
            //代开房 并且游戏未开始
            if (!isCreaterJoin && this.curGameNumber == 1) {
                dissolutionRoom();
            }

            Room room_ = (Room) RoomManager.getRoom(this.roomId);
            if (room_ != null) {
                RoomManager.removeRoom(this.roomId);
            }
        }

        noticeQuitRoom(userId);

        return 0;
    }

    @Override
    public int startGameByClient(long userId) {

//        if (this.bankerId != userId){
//            return ErrorCode.ROOM_START_NOT_CREATEUSER;
//        }

        //第一局
        if (this.curGameNumber != 1) return ErrorCode.ROOM_START_CAN_NOT;

//        if (userStatus.get(userId) != IGameConstant.STATUS_READY) return ErrorCode.ROOM_START_CAN_NOT;

        //防止多次点开始
        if(this.game != null) return ErrorCode.ROOM_START_CAN_NOT;

        int readyCount = 0;
        for (Map.Entry<Long, Integer> entry : userStatus.entrySet()) {

            Integer status = entry.getValue();
            if(status == IGameConstant.STATUS_READY) readyCount++;
        }

        if (readyCount < 2) return ErrorCode.READY_NUM_ERROR;

//        this.setPersonNumber(userScores.size());
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
        MsgSender.sendMsg2Player(new ResponseVo("roomService", "startGameByClient", 0), userId);

        GameYuxiaxie game = new GameYuxiaxie();
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
    public void addUserSocre(long userId, double score) {
        if (userScores.containsKey(userId)) {

            double s = userScores.get(userId);
            userScores.put(userId, s + score);
            if (isClubRoom()) {
                RedisManager.getClubRedisService().addClubUserMoney(this.clubId, userId, score);
            }
        }
    }


    /**
     * 解散房间
     */
    protected void dissolutionRoom() {
        if (RoomManager.getRoom(this.roomId) == null) {
            return;
        }
        RoomManager.removeRoom(this.roomId);
        // 结果类
        List<UserOfResult> userOfResultList = getUserOfResult();

        //代开房 并且游戏未开始
//        if (!isCreaterJoin && !this.isInGame && this.curGameNumber == 1) {
        if (this.curGameNumber == 1) {
            drawBack();
            GameTimer.removeNode(this.prepareRoomTimerNode);
        }
//        if (isClubRoom() && !this.isInGame && this.curGameNumber == 1 && this.users.size() == 0) {
//            clubDrawBack();
//        }
        this.isInGame = false;

        //返还下注
        if (this.game != null) {
            GameYuxiaxie gameYuxiaxie = (GameYuxiaxie) this.game;
            if (gameYuxiaxie.getState() == GameYuxiaxie.STATE_BET) {
                gameYuxiaxie.returnBet();
            }
        }

        // 存储返回
        GameOfResult gameOfResult = new GameOfResult();

        gameOfResult.setUserList(userOfResultList);
        gameOfResult.setEndTime(LocalDateTime.now().toString());
        List<Long> all = new ArrayList<>();
        all.addAll(users);
        all.addAll(watchUser);
        MsgSender.sendMsg2Player(new ResponseVo("gameService", "askNoticeDissolutionResult", gameOfResult), all);

        //战绩
        genRoomRecord();

    }


    @Override
    public IfaceRoomVo toVo(long userId) {
        RoomYuxiaxieVo roomVo = new RoomYuxiaxieVo();
        BeanUtils.copyProperties(this, roomVo);
        RedisManager.getUserRedisService().getUserBeans(users).forEach(userBean -> roomVo.userList.add(userBean.toVo()));
        if (this.game != null) {
            roomVo.game = this.game.toVo(userId);
            roomVo.setBetRemainTime( 60000 +this.game.lastOperateTime-System.currentTimeMillis() );
        }
        if (this.getTimerNode() != null) {
            long time = this.getTimerNode().getStart() + this.getTimerNode().getInterval() - System.currentTimeMillis();
            roomVo.setRemainTime(time);
        }
        if (users.size() > 0) {
            roomVo.setCanStartUserId(users.get(0));
        }

        List<List<Integer>> result = new ArrayList<>();
        diceHistory.forEach((key,value)->{
            if (key != this.curGameNumber) {
                result.add(value);
            }
        });
        roomVo.diceHistory.addAll(result);

        return roomVo;
    }

    public int getDanya() {
        return danya;
    }

    public RoomYuxiaxie setDanya(int danya) {
        this.danya = danya;
        return this;
    }

    public int getChuanlian() {
        return chuanlian;
    }

    public RoomYuxiaxie setChuanlian(int chuanlian) {
        this.chuanlian = chuanlian;
        return this;
    }

    public int getBaozi() {
        return baozi;
    }

    public RoomYuxiaxie setBaozi(int baozi) {
        this.baozi = baozi;
        return this;
    }

    public int getNuo() {
        return nuo;
    }

    public RoomYuxiaxie setNuo(int nuo) {
        this.nuo = nuo;
        return this;
    }

    public Map<Integer, List<Integer>> getDiceHistory() {
        return diceHistory;
    }

    public RoomYuxiaxie setDiceHistory(Map<Integer, List<Integer>> diceHistory) {
        this.diceHistory = diceHistory;
        return this;
    }

    public Map<Long, Map<Integer, List<Bet>>> getBetHistory() {
        return betHistory;
    }

    public RoomYuxiaxie setBetHistory(Map<Long, Map<Integer, List<Bet>>> betHistory) {
        this.betHistory = betHistory;
        return this;
    }
}
