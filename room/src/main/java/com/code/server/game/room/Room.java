package com.code.server.game.room;


import com.code.server.constant.data.DataManager;
import com.code.server.constant.data.StaticDataProto;
import com.code.server.constant.exception.DataNotFoundException;
import com.code.server.constant.game.*;
import com.code.server.constant.kafka.IKafaTopic;
import com.code.server.constant.kafka.KafkaMsgKey;
import com.code.server.constant.response.*;
import com.code.server.game.room.kafka.MsgSender;
import com.code.server.game.room.service.RoomManager;
import com.code.server.kafka.MsgProducer;
import com.code.server.redis.service.RedisManager;
import com.code.server.util.JsonUtil;
import com.code.server.util.SpringUtil;
import com.code.server.util.timer.GameTimer;
import com.code.server.util.timer.TimerNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Created by sunxianping on 2017/3/14.
 */
public class Room implements IfaceRoom {

    private static final Logger logger = LoggerFactory.getLogger(Room.class);

    protected long uuid;
    protected String roomType;
    protected String roomId;
    protected int createType;//房卡或金币
    protected int goldRoomType;
    protected int goldRoomPermission;
    protected String gameType;//项目名称
    protected int createNeedMoney;
    protected static Random random = new Random();
    public Map<Long, Integer> userStatus = new HashMap<>();//用户状态
    public List<Long> users = new ArrayList<>();//用户列表
    public Map<Long, Double> userScores = new HashMap<>();
    protected int multiple;//倍数
    protected int maxZhaCount;//最大炸的个数
    protected int gameNumber;
    public int curGameNumber = 1;
    protected long createUser;
    protected long bankerId;//庄家
    protected boolean isInGame;
    protected boolean isHasDissolutionRequest;
    protected transient TimerNode timerNode;//同意解散的定时器
    public transient TimerNode prepareRoomTimerNode;//代开房定时器
    protected Game game;
    protected int personNumber;
    public boolean isOpen;
    private boolean isLastDraw = false;//是否平局
    private int drawForLeaveChip = 0;//平局留下筹码
    protected int hasNine;
    protected boolean isCanDissloution = false;
    protected long dissloutionUser = -1;//申请解散房间的人
    //    protected Long dealFirstOfRoom;//第一个发牌的人
    protected boolean isAA;//是否共同付费
    protected boolean isCreaterJoin = true;//是否是代开房
    protected boolean isAddGold;
    protected Map<Long, RoomStatistics> roomStatisticsMap = new HashMap<>();//统计
    public int mustZimo = 0;//1是0否
    public boolean showChat;

    private String clubId;
    private String clubRoomModel;

    public Map<Long, Double> userScoresForGold = new HashMap<>();

    public Long canStartUserId = 0L;

    public boolean isRobotRoom;


    public static String getRoomIdStr(int roomId) {
        String s = "000000" + roomId;
        int len = s.length();
        return s.substring(len - 6, len);
    }


    public synchronized static int genRoomId(int serverId) {

        long serverCount = RedisManager.getGameRedisService().getServerCount();
        // 保证房间号不重 对服务器id取余
        while (true) {
            int id = random.nextInt(999999);
            boolean flag = false;
            //服务器只有一个就不取余了
            if (serverCount == 0 || serverCount == 1) {
                flag = true;
            }
            if (flag || (id % serverCount == serverId)) {

                boolean isHas = RedisManager.getRoomRedisService().isExist("" + id);

                if (!isHas) {
                    return id;
                }
            }
        }

    }


    public int getNeedMoney() throws DataNotFoundException {

        StaticDataProto.RoomData roomData = DataManager.data.getRoomDataMap().get(gameType);
        if (roomData == null) {
            throw new DataNotFoundException("roomdata not found : " + gameType);
        }
        //俱乐部 加入不要钱
        if (isClubRoom() || isGoldRoom()) {
            return 0;
        }

        if (isAA) {
            return roomData.getEachMoneyMap().get(gameNumber);
        } else {
            return roomData.getMoneyMap().get(gameNumber);
        }

    }


    public void init(int gameNumber, int multiple) throws DataNotFoundException {
        this.multiple = multiple;
        this.gameNumber = gameNumber;
        this.isInGame = false;
        this.maxZhaCount = multiple;
        this.createNeedMoney = this.getNeedMoney();
        this.isAddGold = DataManager.data.getRoomDataMap().get(this.gameType).getIsAddGold() == 1;

        clubRoomSetId();
    }

    public void getDefaultGoldRoomInstance(long userId, String roomType, String gameType, Integer goldRoomType ) {

    }

    /**
     * 俱乐部 设置id
     */
    public void clubRoomSetId() {
        if (isClubRoom()) {
            MsgProducer msgProducer = SpringUtil.getBean(MsgProducer.class);
            KafkaMsgKey kafkaKey = new KafkaMsgKey();
            kafkaKey.setUserId(0);

            Map<String, String> msg = new HashMap<>();

            msg.put("clubId", this.clubId);
            msg.put("clubModelId", this.clubRoomModel);
            msg.put("roomId", this.roomId);
            ResponseVo responseVo = new ResponseVo("clubService", "clubRoomSetId", msg);
            msgProducer.send("clubService", kafkaKey, responseVo);
        }

    }

    /**
     * 通知 俱乐部游戏开始
     */
    public void notifyCludGameStart() {
        if (!isOpen && isClubRoom()) {

            MsgProducer msgProducer = SpringUtil.getBean(MsgProducer.class);
            KafkaMsgKey kafkaKey = new KafkaMsgKey();
            kafkaKey.setUserId(0);

            Map<String, String> msg = new HashMap<>();

            msg.put("clubId", this.clubId);
            msg.put("clubModelId", this.clubRoomModel);
            msg.put("roomId", this.roomId);
            ResponseVo responseVo = new ResponseVo("clubService", "clubGameStart", msg);
            msgProducer.send("clubService", kafkaKey, responseVo);
        }
    }

    /**
     * 俱乐部找钱
     */
    private void clubDrawBack() {
        MsgProducer msgProducer = SpringUtil.getBean(MsgProducer.class);
        KafkaMsgKey kafkaKey = new KafkaMsgKey();
        kafkaKey.setUserId(0);
        Map<String, Object> msg = new HashMap<>();
        msg.put("clubId", this.clubId);
        msg.put("clubModelId", this.clubRoomModel);
        ResponseVo responseVo = new ResponseVo("clubService", "clubDrawBack", msg);
        msgProducer.send("clubService", kafkaKey, responseVo);
    }

    /**
     * 加入房间
     *
     * @param userId
     * @param isJoin
     * @return
     */
    public int joinRoom(long userId, boolean isJoin) {

        if (isClubRoom() && userId == 0) {
            return 0;
        }
        if (userId == 0) {
            return ErrorCode.JOIN_ROOM_USERID_IS_0;
        }
        if (this.users.contains(userId)) {
            return ErrorCode.CANNOT_CREATE_ROOM_USER_HAS_IN_ROOM;
        }
        if (this.users.size() >= this.personNumber) {
            return ErrorCode.CANNOT_JOIN_ROOM_IS_FULL;

        }
        if (RedisManager.getUserRedisService().getRoomId(userId) != null) {
            return ErrorCode.CANNOT_CREATE_ROOM_USER_HAS_IN_ROOM;
        }
        if (!isCanJoinCheckMoney(userId)) {
            return ErrorCode.CANNOT_JOIN_ROOM_NO_MONEY;
        }


        if (isJoin) {
            roomAddUser(userId);
            //加进玩家-房间映射表
            noticeJoinRoom(userId);
        }

        return 0;
    }

    protected void addUser2RoomRedis(long userId) {
        RedisManager.getUserRedisService().setRoomId(userId, roomId);
        RedisManager.getRoomRedisService().addUser(roomId, userId);
    }

    protected void removeUserRoomRedis(long userId) {
        RedisManager.getUserRedisService().removeRoom(userId);
        RedisManager.getRoomRedisService().removeUser(roomId, userId);
    }

    protected void roomAddUser(long userId) {

        this.users.add(userId);
        this.userStatus.put(userId, 0);
        this.userScores.put(userId, 0D);
        this.roomStatisticsMap.put(userId, new RoomStatistics(userId));
        this.canStartUserId = users.get(0);

        addUser2RoomRedis(userId);
    }

    public void roomRemoveUser(long userId) {

        this.users.remove(userId);
        this.userStatus.remove(userId);
        this.userScores.remove(userId);
        this.roomStatisticsMap.remove(userId);
        removeUserRoomRedis(userId);
    }


    public void noticeJoinRoom(long userId) {
        UserOfRoom userOfRoom = new UserOfRoom();
        int readyNumber = 0;

        for (UserBean userBean : RedisManager.getUserRedisService().getUserBeans(users)) {
            userOfRoom.getUserList().add(userBean.toVo());
        }

        userOfRoom.setInRoomNumber(users.size());
        userOfRoom.setReadyNumber(readyNumber);
        userOfRoom.setClubId(clubId);
        userOfRoom.setClubRoomModel(clubRoomModel);

        userOfRoom.setCanStartUserId(users.get(0));
        userOfRoom.setUserScores(this.userScores);

        MsgSender.sendMsg2Player(new ResponseVo("roomService", "joinRoom", this.toVo(userId)), userId);

        MsgSender.sendMsg2Player(new ResponseVo("roomService", "roomNotice", userOfRoom), this.getUsers());


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
        return true;
    }


    public int quitRoom(long userId) {
        if (!this.users.contains(userId)) {
            return ErrorCode.CANNOT_QUIT_ROOM_NOT_EXIST;

        }

        if (isInGame) {
            return ErrorCode.CANNOT_QUIT_ROOM_IS_IN_GAME;
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
            if (!isCreaterJoin && !this.isInGame && this.curGameNumber == 1) {
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


    protected void noticeQuitRoom(long userId) {
        UserOfRoom userOfRoom = new UserOfRoom();
        List<Long> noticeList = this.getUsers();
        int inRoomNumber = this.getUsers().size();
        int readyNumber = 0;

        for (int i : this.getUserStatus().values()) {
            if (i == STATUS_READY) {
                readyNumber++;
            }
        }
        for (UserBean userBean : RedisManager.getUserRedisService().getUserBeans(users)) {
            userOfRoom.getUserList().add(userBean.toVo());
        }
        userOfRoom.setInRoomNumber(inRoomNumber);
        userOfRoom.setReadyNumber(readyNumber);

        ResponseVo noticeResult = new ResponseVo("roomService", "roomNotice", userOfRoom);
        MsgSender.sendMsg2Player(noticeResult, noticeList);

        Notice n = new Notice();
        n.setMessage("quit room success!");

        ResponseVo result = new ResponseVo("roomService", "quitRoom", n);
        MsgSender.sendMsg2Player(result, userId);

    }

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
        if (readyNum >= personNumber) {
            startGame();
        }
        MsgSender.sendMsg2Player(new ResponseVo("roomService", "getReady", 0), userId);
        return 0;
    }


    protected Game getGameInstance() {
        StaticDataProto.RoomData roomData = DataManager.data.getRoomDataMap().get(gameType);
        Game game = new Game();
        if (roomData != null) {
            roomData.getInstanceName();
            try {
                game = (Game) Class.forName(roomData.getInstanceName()).newInstance();
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return game;
    }

    protected void initRoomStatisticsMap(long userId) {
        this.roomStatisticsMap.put(userId, new RoomStatistics(userId));
    }

    public void startGame() {
        this.isInGame = true;
        Game game = getGameInstance();
        this.game = game;
        //扣钱
        if (!isOpen && isCreaterJoin) {
            spendMoney();
        }
        //游戏开始 代建房 去除定时解散
        if (!isOpen && !this.isCreaterJoin()) {
            GameTimer.removeNode(prepareRoomTimerNode);
        }
        game.startGame(users, this);
        notifyCludGameStart();
        this.isOpen = true;
        pushScoreChange();
        //记录局数
        RedisManager.getLogRedisService().addGameNum(getGameLogKeyStr(), 1);
    }

    public void pushScoreChange() {
        MsgSender.sendMsg2Player(new ResponseVo("gameService", "scoreChange", userScores), this.getUsers());
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
            TimerNode node = new TimerNode(start, FIVE_MIN, false, () -> {

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
        if (agreeNum >= personNumber - 1 && agreeNum >= 2) {
            GameTimer.removeNode(timerNode);
            dissolutionRoom();
        }
        //不同意的人数大于2 解散取消
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

    public boolean isRoomOver() {
        return this.getCurGameNumber() >= this.getGameNumber();
    }

    @Override
    public int startGameByClient(long userId) {
        return 0;
    }

    @Override
    public int getPrepareRoom(long userId) {
        List<IfaceRoomVo> result = new ArrayList<>();
        if (RoomManager.getInstance().getPrepareRoom().containsKey(userId)) {
            for (String roomId : RoomManager.getInstance().getPrepareRoom().get(userId)) {
                IfaceRoom room = RoomManager.getRoom(roomId);
                if (room != null) {
                    result.add(room.toVo(userId));
                }
            }
        }
        MsgSender.sendMsg2Player("roomService", "roomList", result, userId);
        MsgSender.sendMsg2Player("roomService", "getPrepareRoom", 0, userId);
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
            userOfResultList.add(resultObj);
        }
        return userOfResultList;
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
        if (!isCreaterJoin && !this.isInGame && this.curGameNumber == 1) {
            drawBack();
            GameTimer.removeNode(this.prepareRoomTimerNode);
        }
        this.isInGame = false;

        // 存储返回
        GameOfResult gameOfResult = new GameOfResult();

        gameOfResult.setUserList(userOfResultList);
        gameOfResult.setEndTime(LocalDateTime.now().toString());
        MsgSender.sendMsg2Player(new ResponseVo("gameService", "askNoticeDissolutionResult", gameOfResult), users);

        //战绩
        genRoomRecord();
    }


    public void clearReadyStatus(boolean isAddGameNum) {
        this.setGame(null);
        this.setInGame(false);
        for (Map.Entry<Long, Integer> entry : this.userStatus.entrySet()) {
            entry.setValue(STATUS_JOIN);
        }
        if (isAddGameNum) {
            this.curGameNumber += 1;
        }
    }

    public void addUserSocre(long userId, double score) {
        double s = userScores.get(userId);
        userScores.put(userId, s + score);
        RoomStatistics roomStatistics = roomStatisticsMap.get(userId);
        if (roomStatistics != null) {
            roomStatistics.maxScore = roomStatistics.maxScore > score ? roomStatistics.maxScore : score;
            if (score >= 0) {
                roomStatistics.winTime += 1;
            } else {
                roomStatistics.failedTime += 1;
            }
        }
    }

    public boolean scoreIsChange() {
        for (double score : userScores.values()) {
            if (score != 0) {
                return true;
            }
        }
        return false;
    }

    public void drawBack() {
        if (isAA) {
            this.users.forEach(userId -> {
                RedisManager.getUserRedisService().addUserMoney(userId, createNeedMoney);
                if (isAddGold()) RedisManager.addGold(userId, -createNeedMoney / 10);
            });
        } else {
            RedisManager.getUserRedisService().addUserMoney(this.createUser, createNeedMoney);
            if (isAddGold()) RedisManager.addGold(this.createUser, -createNeedMoney / 10);
        }
        //俱乐部房间退钱
        if (isClubRoom()) {
            clubDrawBack();
        }
    }

    public void spendMoney() {
        if (isAA) {
            this.users.forEach(userId -> {
                RedisManager.getUserRedisService().addUserMoney(userId, -createNeedMoney);
                if (isAddGold()) RedisManager.addGold(userId, createNeedMoney / 10);
            });
        } else {
            RedisManager.getUserRedisService().addUserMoney(this.createUser, -createNeedMoney);
            if (isAddGold()) RedisManager.addGold(this.createUser, createNeedMoney / 10);
        }
    }

    public static void main(String[] args) {
        Room room = new Room();
        room.setGame(new Game());
        room.setRoomId("11111");
        RoomVo roomVo = new RoomVo();
        BeanUtils.copyProperties(room, roomVo);

        System.out.println(roomVo.roomId);

        System.out.println(43421 % 4 == 2);
    }

    @Override
    public IfaceRoomVo toVo(long userId) {
        RoomVo roomVo = new RoomVo();
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


    @Override
    public PrepareRoom getPrepareRoomVo() {
        PrepareRoom prepareRoom = new PrepareRoom();
        prepareRoom.goldRoomType = this.goldRoomType;
        prepareRoom.createTime = System.currentTimeMillis();
        prepareRoom.gameType = this.getGameType();
        prepareRoom.roomType = this.getRoomType();
        prepareRoom.roomId = this.roomId;
        prepareRoom.multiple = this.multiple;
        prepareRoom.gameNumber = this.gameNumber;
        return prepareRoom;
    }

    public PrepareRoom getSimpleVo(){
        PrepareRoom prepareRoom = this.getPrepareRoomVo();
        prepareRoom.peopleNum = this.users.size();
        return prepareRoom;
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

        this.userScores.forEach((key, value) -> {
            UserRecord userRecord = new UserRecord();
            userRecord.setScore(value);
            userRecord.setUserId(key);
            UserBean userBean = RedisManager.getUserRedisService().getUserBean(key);
            if (userBean != null) {
                userRecord.setName(userBean.getUsername());
            }
            roomRecord.getRecords().add(userRecord);
        });

        KafkaMsgKey kafkaMsgKey = new KafkaMsgKey().setMsgId(KAFKA_MSG_ID_ROOM_RECORD);
        MsgProducer msgProducer = SpringUtil.getBean(MsgProducer.class);
        msgProducer.send(IKafaTopic.CENTER_TOPIC, kafkaMsgKey, roomRecord);

    }

    @Override
    public int getRoomClubByUser(long userId) {
        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        if (this.clubId == null) {
            result.put("clubId", 0);
            MsgSender.sendMsg2Player(new ResponseVo("roomService", "getRoomClubByUser", result), userId);
        } else {

            result.put("clubId", this.clubId);
            KafkaMsgKey kafkaMsgKey = new KafkaMsgKey().setMsgId(KAFKA_MSG_ID_ROOM_CLUB_USER);
            MsgProducer msgProducer = SpringUtil.getBean(MsgProducer.class);
            msgProducer.send(IKafaTopic.CENTER_TOPIC, kafkaMsgKey, result);

        }
        return 0;
    }

    public boolean isDefaultGoldRoom() {
        return goldRoomPermission == GOLD_ROOM_PERMISSION_DEFAULT;
    }

    public boolean isClubRoom() {
        return clubId != null && !"".equals(clubId) && !"0".equals(clubId);
    }

    public String getRoomId() {
        return roomId;
    }

    public Room setRoomId(String roomId) {
        this.roomId = roomId;
        return this;
    }

    public int getCreateNeedMoney() {
        return createNeedMoney;
    }

    public Room setCreateNeedMoney(int createNeedMoney) {
        this.createNeedMoney = createNeedMoney;
        return this;
    }

    public static Random getRandom() {
        return random;
    }

    public static void setRandom(Random random) {
        Room.random = random;
    }

    public Map<Long, Integer> getUserStatus() {
        return userStatus;
    }

    public Room setUserStatus(Map<Long, Integer> userStatus) {
        this.userStatus = userStatus;
        return this;
    }

    public List<Long> getUsers() {
        return users;
    }


    public Room setUsers(List<Long> users) {
        this.users = users;
        return this;
    }

    public Map<Long, Double> getUserScores() {
        return userScores;
    }

    public Room setUserScores(Map<Long, Double> userScores) {
        this.userScores = userScores;
        return this;
    }


    public int getMultiple() {
        return multiple;
    }

    public Room setMultiple(int multiple) {
        this.multiple = multiple;
        return this;
    }

    public int getGameNumber() {
        return gameNumber;
    }

    public Room setGameNumber(int gameNumber) {
        this.gameNumber = gameNumber;
        return this;
    }

    public int getCurGameNumber() {
        return curGameNumber;
    }

    public Room setCurGameNumber(int curGameNumber) {
        this.curGameNumber = curGameNumber;
        return this;
    }

    public int getPersonNumber() {
        return personNumber;
    }

    public Room setPersonNumber(int personNumber) {
        this.personNumber = personNumber;
        return this;
    }

    public long getCreateUser() {
        return createUser;
    }

    public Room setCreateUser(long createUser) {
        this.createUser = createUser;
        return this;
    }

    public long getBankerId() {
        return bankerId;
    }

    public Room setBankerId(long bankerId) {
        this.bankerId = bankerId;
        return this;
    }

    public boolean isInGame() {
        return isInGame;
    }

    public Room setInGame(boolean inGame) {
        isInGame = inGame;
        return this;
    }

    public boolean isHasDissolutionRequest() {
        return isHasDissolutionRequest;
    }

    public Room setHasDissolutionRequest(boolean hasDissolutionRequest) {
        isHasDissolutionRequest = hasDissolutionRequest;
        return this;
    }

    public TimerNode getTimerNode() {
        return timerNode;
    }

    public Room setTimerNode(TimerNode timerNode) {
        this.timerNode = timerNode;
        return this;
    }

    public Game getGame() {
        return game;
    }

    public Room setGame(Game game) {
        this.game = game;
        return this;
    }

    public int getCreateType() {
        return createType;
    }

    public Room setCreateType(int createType) {
        this.createType = createType;
        return this;
    }

    public int getGoldRoomType() {
        return goldRoomType;
    }

    public Room setGoldRoomType(int goldRoomType) {
        this.goldRoomType = goldRoomType;
        return this;
    }

    public static int getRoomCreateTypeConmmon() {
        return ROOM_CREATE_TYPE_CONMMON;
    }

    public boolean isLastDraw() {
        return isLastDraw;
    }

    public void setLastDraw(boolean lastDraw) {
        isLastDraw = lastDraw;
    }

    public int getDrawForLeaveChip() {
        return drawForLeaveChip;
    }

    public void setDrawForLeaveChip(int drawForLeaveChip) {
        this.drawForLeaveChip = drawForLeaveChip;
    }

    public int getHasNine() {
        return hasNine;
    }

    public void setHasNine(int hasNine) {
        this.hasNine = hasNine;
    }

    public String getGameType() {
        return gameType;
    }


    public Room setGameType(String gameType) {
        this.gameType = gameType;
        return this;
    }

    public String getRoomType() {
        return roomType;
    }

    public Room setRoomType(String roomType) {
        this.roomType = roomType;
        return this;
    }

    public boolean isAA() {
        return isAA;
    }

    public Room setAA(boolean AA) {
        isAA = AA;
        return this;
    }

    public boolean isCreaterJoin() {
        return isCreaterJoin;
    }

    public Room setCreaterJoin(boolean createrJoin) {
        isCreaterJoin = createrJoin;
        return this;
    }

    public boolean isAddGold() {
        return isAddGold;
    }

    public Room setAddGold(boolean addGold) {
        isAddGold = addGold;
        return this;
    }

    public Map<Long, RoomStatistics> getRoomStatisticsMap() {
        return roomStatisticsMap;
    }

    public Room setRoomStatisticsMap(Map<Long, RoomStatistics> roomStatisticsMap) {
        this.roomStatisticsMap = roomStatisticsMap;
        return this;
    }

    public long getDissloutionUser() {
        return dissloutionUser;
    }

    public Room setDissloutionUser(long dissloutionUser) {
        this.dissloutionUser = dissloutionUser;
        return this;
    }


    public boolean isGoldRoom() {
        return false;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public Room setOpen(boolean open) {
        isOpen = open;
        return this;
    }

    public int getMustZimo() {
        return mustZimo;
    }

    public void setMustZimo(int mustZimo) {
        this.mustZimo = mustZimo;
    }

    public TimerNode getPrepareRoomTimerNode() {
        return prepareRoomTimerNode;
    }

    public Room setPrepareRoomTimerNode(TimerNode prepareRoomTimerNode) {
        this.prepareRoomTimerNode = prepareRoomTimerNode;
        return this;
    }

    public long getUuid() {
        return uuid;
    }

    public Room setUuid(long uuid) {
        this.uuid = uuid;
        return this;
    }


    public Long getCanStartUserId() {
        return canStartUserId;
    }

    public void setCanStartUserId(Long canStartUserId) {
        this.canStartUserId = canStartUserId;
    }

    public boolean isShowChat() {
        return showChat;
    }

    public Room setShowChat(boolean showChat) {
        this.showChat = showChat;
        return this;
    }

    public String getClubId() {
        return clubId;
    }

    public Room setClubId(String clubId) {
        this.clubId = clubId;
        return this;
    }

    public String getClubRoomModel() {
        return clubRoomModel;
    }

    public Room setClubRoomModel(String clubRoomModel) {
        this.clubRoomModel = clubRoomModel;
        return this;
    }

    public int getMaxZhaCount() {
        return maxZhaCount;
    }

    public Room setMaxZhaCount(int maxZhaCount) {
        this.maxZhaCount = maxZhaCount;
        return this;
    }

    public Map<Long, Double> getUserScoresForGold() {
        return userScoresForGold;
    }

    public void setUserScoresForGold(Map<Long, Double> userScoresForGold) {
        this.userScoresForGold = userScoresForGold;
    }

    public int getGoldRoomPermission() {
        return goldRoomPermission;
    }

    public Room setGoldRoomPermission(int goldRoomPermission) {
        this.goldRoomPermission = goldRoomPermission;
        return this;
    }

    @Override
    public boolean isRobotRoom() {
        return isRobotRoom;
    }

    @Override
    public GameLogKey getGameLogKey() {
        GameLogKey gameLogKey = new GameLogKey();
        gameLogKey.setRoomType(roomType);
        gameLogKey.setGameType(gameType);
        gameLogKey.setGameNumber(gameNumber);
        gameLogKey.setGoldRoomType(goldRoomType);
        gameLogKey.setGoldRoomPermission(goldRoomPermission);

        return gameLogKey;
    }

    public String getGameLogKeyStr(){
        return JsonUtil.toJson(getGameLogKey());
    }

    public Room setRobotRoom(boolean robotRoom) {
        isRobotRoom = robotRoom;
        return this;
    }
}
