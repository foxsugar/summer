package com.code.server.game.room;


import com.code.server.constant.game.UserBean;
import com.code.server.constant.response.*;
import com.code.server.game.room.service.RoomManager;
import com.code.server.util.timer.GameTimer;
import com.code.server.util.timer.TimerNode;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

/**
 * Created by sunxianping on 2017/3/14.
 */
public class Room implements IfaceRoom{


    protected String roomId;
    protected int createType;//房卡或金币
    protected double goldRoomType;
    protected String gameType;//麻将项目名称

    protected int createNeedMoney;
    protected static Random random = new Random();

    protected Map<Long, Integer> userStatus = new HashMap<>();//用户状态
    protected List<Long> users = new ArrayList<>();//用户列表
    protected Map<Long, Double> userScores = new HashMap<>();
//    protected Map<Long,User> userMap = new HashMap<>();//用户列表

    protected double roomType;//几倍房
    protected int multiple;//倍数
    protected int maxZhaCount;//最大炸的个数
    protected int gameNumber;
    protected int curGameNumber = 1;
    protected long createUser;
    protected long bankerId;//庄家

    protected boolean isInGame;

    protected boolean isHasDissolutionRequest;
    protected transient TimerNode timerNode;

    protected Game game;


    protected int personNumber;

    protected boolean isOpen;


    private boolean isLastDraw = false;//是否平局

    private int drawForLeaveChip = 0;//平局留下筹码

    protected int hasNine;

    protected boolean isCanDissloution = false;

    protected Long dealFirstOfRoom;//第一个发牌的人


    public static int joinRoomQuick(Player player,int type){

        return 0;
    }

    protected static String getRoomIdStr(int roomId){
        String s = "000000" + roomId;
        int len = s.length();
        return s.substring(len-6,len);
    }

    public static void main(String[] args) {
        System.out.println(getRoomIdStr(99999));
    }
    protected static int genRoomId(){

        while (true) {
            int id = random.nextInt(999999);

            boolean isHas = RedisManager.getRoomRedisService().isExist(""+id);

            if (!isHas) {
                return id;
            }

        }
    }

    public static int getNeedMoney(int gameNumber) {
        if (gameNumber == 10) {
            return 1;
        } else if (gameNumber == 20) {
            return 2;
        }else {
            return 2;
        }

    }

    public void init(int gameNumber, int multiple) {
        this.multiple = multiple;
        this.gameNumber = gameNumber;
        this.isInGame = false;
        this.maxZhaCount = multiple;

        //todo
        this.createNeedMoney = 1;
    }



    public int joinRoom(long userId) {


        if (this.users.contains(userId)) {
            return ErrorCode.CANNOT_CREATE_ROOM_USER_HAS_IN_ROOM;
        }
        if (this.users.size() >= this.personNumber) {
            return ErrorCode.CANNOT_JOIN_ROOM_IS_FULL;

        }
        if(RedisManager.getUserRedisService().getRoomId(userId) != null){
            return ErrorCode.CANNOT_CREATE_ROOM_USER_HAS_IN_ROOM;
        }
        if (!isCanJoinCheckMoney(userId)) {
            return ErrorCode.CANNOT_JOIN_ROOM_NO_MONEY;
        }



        roomAddUser(userId);
        //加进玩家-房间映射表
        noticeJoinRoom(userId);

        return 0;
    }

    //TODO
    protected void addUser2RoomRedis(long userId){
        RedisManager.getUserRedisService().setRoomId(userId, roomId);
    }

    protected void removeUserRoomRedis(long userId){
        RedisManager.getUserRedisService().removeRoom(userId);
    }
    protected void roomAddUser(long userId) {

        this.users.add(userId);
        this.userStatus.put(userId, 0);
        this.userScores.put(userId, 0D);
        addUser2RoomRedis(userId);
    }

    protected void roomRemoveUser(long userId) {

        this.users.remove(userId);
        this.userStatus.remove(userId);
        this.userScores.remove(userId);
        removeUserRoomRedis(userId);
    }


    public void noticeJoinRoom(long userId){
        List<UserVo> usersList = new ArrayList<>();
        UserOfRoom userOfRoom = new UserOfRoom();
        int readyNumber = 0;
//        for (long uid : users) {
//            User user = this.userMap.get(uid);
//            usersList.add(GameManager.getUserVo(user));
//        }


        userOfRoom.setUserList(RedisManager.getUserRedisService().getUserBeans(users));
        userOfRoom.setInRoomNumber(users.size());
        userOfRoom.setReadyNumber(readyNumber);


        Player.sendMsg2Player(new ResponseVo("roomService","joinRoom",this.toVo()),userId);

        Player.sendMsg2Player(new ResponseVo("roomService","roomNotice",userOfRoom), this.getUsers());


    }

    protected boolean isCanJoinCheckMoney(long userId) {
        if (userId == createUser) {
            if (RedisManager.getUserRedisService().getUserMoney(userId) < createNeedMoney) {
                return false;
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
        //删除玩家房间映射关系
        roomRemoveUser(userId);

//        GameManager.getInstance().getUserRoom().remove(userId);

        if (this.createUser == userId) {//房主解散

            Notice n = new Notice();
            n.setMessage("roomNum "+this.getRoomId()+" :has destroy success!");
            Player.sendMsg2Player(new ResponseVo("roomService","destroyRoom",n), this.getUsers());
            //删除房间
//            GameManager.getInstance().rooms.remove(roomId);
            dissolutionRoom();

        }

        noticeQuitRoom(userId);

        return 0;
    }


    protected void noticeQuitRoom(long userId){
        List<UserVo> usersList = new ArrayList<>();
        UserOfRoom userOfRoom = new UserOfRoom();

        List<Long> noticeList = this.getUsers();

//        for (long uid : users) {
//            User user = this.userMap.get(uid);
//            usersList.add(GameManager.getUserVo(user));
//        }
        int inRoomNumber = this.getUsers().size();
        int readyNumber = 0;

        for (int i : this.getUserStatus().values()) {
            if(i==STATUS_READY){
                readyNumber++;
            }
        }
        userOfRoom.setUserList(RedisManager.getUserRedisService().getUserBeans(users));
        userOfRoom.setInRoomNumber(inRoomNumber);
        userOfRoom.setReadyNumber(readyNumber);

        ResponseVo noticeResult = new ResponseVo("roomService", "roomNotice", userOfRoom);
        Player.sendMsg2Player(noticeResult, noticeList);

        Notice n = new Notice();
        n.setMessage("quit room success!");

        ResponseVo result = new ResponseVo("roomService","quitRoom",n);
        Player.sendMsg2Player(result,userId);

    }

    public int getReady(long userId) {
        if (!this.users.contains(userId)) {
            return ErrorCode.CANNOT_FIND_THIS_USER;

        }
        if (isInGame) {
            return  ErrorCode.CANNOT_FIND_THIS_USER;
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
        Player.sendMsg2Player(new ResponseVo("roomService","noticeReady",noticeReady), this.users);

        //开始游戏
        if (readyNum >= personNumber) {
            startGame();
        }
        Player.sendMsg2Player(new ResponseVo("roomService","getReady",0),userId);
        return 0;
    }



    protected Game getGameInstance(){
        return new Game();
    }



    public void startGame() {
        this.isOpen = true;
        this.isInGame = true;
        Game game = getGameInstance();
        this.game = game;
        //扣钱
        if (curGameNumber == 1) {
            spendMoney();
        }
        game.startGame(users,this);




        //通知其他人游戏已经开始
//        CardEntity cardBegin = new CardEntity();
//        cardBegin.setCurrentUserId(this.getBankerId() + "");
        Player.sendMsg2Player(new ResponseVo("gameService","gameBegin","ok"), this.getUsers());
        pushScoreChange();
    }

    public void pushScoreChange() {
//        Gson gson = new Gson();
//        String json = gson.toJson(userScores);
//        JSONObject beginResult = new JSONObject();
//        beginResult.put("service", "gameService");
//        beginResult.put("method", "scoreChange");
//        beginResult.put("params", json);
//        beginResult.put("code", "0");
        Player.sendMsg2Player(new ResponseVo("gameService","scoreChange",userScores),this.getUsers());
    }


    public int dissolution(long userId, boolean agreeOrNot, String methodName) {
        if (!this.users.contains(userId)) {
            return ErrorCode.CANNOT_FIND_THIS_USER;

        }

        this.userStatus.put(userId, agreeOrNot ? STATUS_AGREE_DISSOLUTION : STATUS_DISSOLUTION);

        //第一个点解散
        if (agreeOrNot && !isHasDissolutionRequest) {
            isCanDissloution = true;
            this.isHasDissolutionRequest = true;
            //第一次申请 五分钟后解散
            long start = System.currentTimeMillis();
            TimerNode node = new TimerNode(start, FIVE_MIN, false, ()-> {

                if (isCanDissloution) {
                    dissolutionRoom();
                }

            });
            this.timerNode = node;
            GameTimer.getInstance().addTimerNode(node);
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
        Player.sendMsg2Player(new ResponseVo("roomService","noticeAnswerIfDissolveRoom",accept), this.users);


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
        if (agreeNum >= personNumber - 1) {
            GameTimer.getInstance().removeNode(timerNode);
            dissolutionRoom();
        }
        //不同意的人数大于2 解散取消
        if (disAgreeNum >= 1) {
            for (Map.Entry<Long, Integer> entry : userStatus.entrySet()) {
                //回到游戏状态
                entry.setValue(STATUS_IN_GAME);
                this.isHasDissolutionRequest = false;
                GameTimer.getInstance().removeNode(timerNode);
            }
        }


        AskQuitRoom accept1 = new AskQuitRoom();
        accept1.setUserId(""+userId);
        Player.sendMsg2Player("roomService","noticeDissolveRoom",accept1,users);

        AskQuitRoom send = new AskQuitRoom();
        send.setNote("ok");
        Player.sendMsg2Player("roomService",methodName,send,userId);

        return 0;
    }

    /**
     * 解散房间
     */
    protected void dissolutionRoom(){

//        GameManager.getInstance().removeRoom(this);
        RoomManager.removeRoom(this.roomId);


        // 结果类
        ArrayList<UserOfResult> userOfResultList = new ArrayList<>();

        long time = System.currentTimeMillis();

        for(UserBean user : RedisManager.getUserRedisService().getUserBeans(users)){
            UserOfResult resultObj = new UserOfResult();
            try {
                resultObj.setUsername(URLDecoder.decode(user.getUsername(), "utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            resultObj.setImage(user.getImage());
            resultObj.setScores(""+this.userScores.get(user.getId()));
            resultObj.setUserId(user.getId());
            resultObj.setTime(time);

            userOfResultList.add(resultObj);
            //删除映射关系
            RedisManager.getUserRedisService().removeRoom(user.getId());
        }



        boolean isChange = scoreIsChange();
        if (this.isInGame && this.curGameNumber == 1 && !isChange) {
//            drawBack();
        }



        this.isInGame = false;
        // 存储返回
        GameOfResult gameOfResult = new GameOfResult();

        gameOfResult.setUserList(userOfResultList);
        gameOfResult.setEndTime(new Date().toLocaleString());

        Player.sendMsg2Player(new ResponseVo("gameService","askNoticeDissolutionResult",gameOfResult), this.users);

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
        RedisManager.getUserRedisService().addUserMoney(this.createUser, createNeedMoney);
//        User user = userMap.get(this.createUser);
//        if (user != null) {
//            user.setMoney(user.getMoney() + createNeedMoney);
//            GameManager.getInstance().getSaveUser2DB().add(user);
//        }
    }

    public void spendMoney() {
        RedisManager.getUserRedisService().addUserMoney(this.createUser, -createNeedMoney);
//        User user = userMap.get(this.createUser);
//        if (user != null) {
//            user.setMoney(user.getMoney() - createNeedMoney);
//            GameManager.getInstance().getSaveUser2DB().add(user);
//        }
    }


    @Override
    public IfaceRoomVo toVo() {
        RoomVo roomVo = new RoomVo();
        roomVo.createType = this.getCreateType();
        roomVo.roomId = this.getRoomId();
        roomVo.multiple = this.getMultiple();
        roomVo.gameNumber = this.getGameNumber();
        roomVo.createUser = this.getCreateUser();
        roomVo.userStatus.putAll(this.getUserStatus());
        roomVo.userScores.putAll(this.getUserScores());
        roomVo.curGameNumber = this.getCurGameNumber();
        roomVo.goldRoomType = this.getGoldRoomType();
        roomVo.isLastDraw = this.isLastDraw();
        roomVo.drawForLeaveChip = this.getDrawForLeaveChip();
        roomVo.personNumber = this.getPersonNumber();
        roomVo.hasNine = this.getHasNine();

//        for(long uid : this.getUsers()){
//            roomVo.userList.add(GameManager.getUserVo(uid));
//        }
        roomVo.userList.addAll(RedisManager.getUserRedisService().getUserBeans(users));

        roomVo.game = this.game.toVo();
        return roomVo;
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

    public double getGoldRoomType() {
        return goldRoomType;
    }

    public Room setGoldRoomType(double goldRoomType) {
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

    public String getGameType(){return gameType;}

    public Long getDealFirstOfRoom() {
        return dealFirstOfRoom;
    }

    public void setDealFirstOfRoom(Long dealFirstOfRoom) {
        this.dealFirstOfRoom = dealFirstOfRoom;
    }


}
