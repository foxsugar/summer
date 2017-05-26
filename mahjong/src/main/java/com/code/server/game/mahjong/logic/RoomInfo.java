package com.code.server.game.mahjong.logic;



import com.code.server.game.mahjong.util.ErrorCode;
import com.code.server.util.timer.GameTimer;
import com.code.server.util.timer.ITimeHandler;
import com.code.server.util.timer.TimerNode;
import net.sf.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 类名称：RoomInfo
 * 类描述：
 * 创建人：Clark
 * 创建时间：2016年12月12日 上午10:35:33
 * 修改人：Clark
 * 修改时间：2016年12月12日 上午10:35:33
 * 修改备注：
 *
 * @version 1.0
 */


public class RoomInfo {

//    private static final Logger logger = Logger.getLogger("game");

    public static final int STATUS_JOIN = 0;
    public static final int STATUS_READY = 1;
    public static final int STATUS_IN_GAME = 2;
    public static final int STATUS_DISSOLUTION = 3;
    public static final int STATUS_AGREE_DISSOLUTION = 4;

    //    public static final long FIVE_MIN = 1000L * 10;
    public static final long FIVE_MIN = 1000L * 60 * 5;

    protected String roomId;
    protected Map<Integer, Integer> userStatus = new HashMap<>();//用户状态
    protected String modeTotal;
    protected String mode;
    protected int multiple;//倍数
    protected int gameNumber;
    protected int curGameNumber = 1;
    protected int personNumber;
    protected int createUser;
    protected int bankerId;//庄家
    protected List<Integer> users = new ArrayList<>();//用户列表
    protected List<User> userList = new ArrayList<>();//用户列表
    protected boolean isInGame;
    protected boolean isHasDissolutionRequest;

    protected transient RoomDao roomDao;
    protected transient UserRecodeDao userRecodeDao;
    protected transient UserDao userDao;
    protected transient GameDao gameDao;
    protected transient ServerContext serverContext;
    protected transient GameInfo gameInfo;
    protected transient Game game;
    protected Map<Integer, Integer> userScores = new HashMap<>();
    protected Map<Integer, Integer> bankerMap = new HashMap<>();
    protected transient TimerNode timerNode;
    protected Map<Integer, Integer> circleNumber = new HashMap<>();//圈数，key存圈数，value存庄家换人的次数
    protected int maxCircle;

    protected String gameType;//麻将项目名称

    protected boolean isHasGangBlackList = true;

    protected Map<Integer, Integer> huNum = new HashMap<>();
    protected Map<Integer, Integer> dianPaoNum = new HashMap<>();
    protected Map<Integer, Integer> lianZhuangNum = new HashMap<>();
    protected Map<Integer, Integer> moBaoNum = new HashMap<>();

    protected boolean isCanDissloution = false;
    
    protected String each = "";//4人平分房卡
    
    public String getEach() {
		return each;
	}


	public void setEach(String each) {
		this.each = each;
	}


	/**
     * @param @param modeTotal
     * @param @param mode
     * @param @param multiple
     * @param @param gameNumber
     * @param @param personNumber
     * @param @param createUser
     * @param @param bankerId    设定文件
     * @return void    返回类型
     * @throws
     * @Title: init
     * @Creater: Clark
     * @Description: 创建房间
     */
    public void init(String roomId, int userId, String modeTotal, String mode, int multiple, int gameNumber, int personNumber, int createUser, int bankerId) {
        this.roomId = roomId;
        this.modeTotal = modeTotal;
        this.mode = mode;
        this.multiple = multiple;
        this.gameNumber = gameNumber;
        this.personNumber = personNumber;
        this.createUser = createUser;
        this.bankerId = bankerId;
        this.isInGame = false;
        this.bankerMap.put(1, bankerId);
        this.maxCircle = gameNumber;
        this.circleNumber.put(1, 1);
        GameManager.getInstance().getRoomLock().put(roomId, new Object());
    }


    /**
     * @param @param roomInfo
     * @param @param userId    设定文件
     * @return void    返回类型
     * @throws
     * @Title: 加入房间
     * @Creater: Clark
     * @Description:
     */
    public synchronized void joinRoom(int userId) {
        if (this.users.contains(userId)) {
            throw new BusinessException(
                    ErrorCode.CANNOT_CREATE_ROOM_USER_HAS_IN_ROOM,
                    "user has in the room");
        }
        if (this.users.size() >= this.personNumber) {
            throw new BusinessException(ErrorCode.CANNOT_JOIN_ROOM_IS_FULL,
                    "cannot join room is full");
        }
        if (!isCanJoinCheckMoney(userId)) {
            throw new BusinessException(ErrorCode.CANNOT_JOIN_ROOM_NO_MONEY,
                    "cannot join room no money");
        }

//        if (isInGame) {
//            throw new BusinessException(ErrorCode.CANNOT_JOIN_ROOM_IS_IN_GAME,
//                    "cannot join room is in game");
//        }
        this.users.add(userId);
        this.userStatus.put(userId, 0);
        this.userScores.put(userId, 0);
        //加进玩家-房间映射表
        GameManager.getInstance().getUserRoom().put(userId, roomId);
    }

    protected boolean isCanJoinCheckMoney(int userId) {
        if (userId == createUser) {
            User user = userDao.getUser(userId);
            if ("SY".equals(gameType)) {
                if (user.getMoney() < 1) {
                    return false;
                }
            }else if ("LQ".equals(gameType)) {
                if (each.equals("0") && user.getMoney() < 30) {
                    return false;
                }
            } else {
                if (user.getMoney() < 3) {
                    return false;
                }
            }
        }else{
        	User user = userDao.getUser(userId);
        	if ("LQ".equals(gameType)) {
                if (each.equals("1") && user.getMoney() < 10 && this.gameNumber==8) {
                    return false;
                }
                if (each.equals("1") && user.getMoney() < 20 && this.gameNumber==16) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * @param @param roomInfo
     * @param @param userId    设定文件
     * @return void    返回类型
     * @throws
     * @Title: 离开房间
     * @Creater: Clark
     * @Description:
     */
    public void quitRoom(int userId) {
        if (!this.users.contains(userId)) {
            throw new BusinessException(ErrorCode.CANNOT_QUIT_ROOM_NOT_EXIST,
                    "cannot quit room not exist");
        }

        if (isInGame) {
            throw new BusinessException(ErrorCode.CANNOT_QUIT_ROOM_IS_IN_GAME,
                    "cannot quit room is in game");
        }

        this.userStatus.remove(userId);
        this.users.remove((Integer) userId);
        this.userScores.remove(userId);
        //删除玩家房间映射关系
        GameManager.getInstance().getUserRoom().remove(userId);
    }

    /**
     * @param @param roomInfo
     * @param @param userId    设定文件
     * @return void    返回类型
     * @throws
     * @Title: 准备开始
     * @Creater: Clark
     * @Description:
     */
    public void getReady(int userId) {
        if (!this.users.contains(userId)) {
            throw new BusinessException(ErrorCode.CANNOT_FIND_THIS_USER,
                    "cannot find this user");
        }
        if (isInGame()) {
            return;
        }

        this.userStatus.put(userId, STATUS_READY);

        int readyNum = 0;
        for (Map.Entry<Integer, Integer> entry : this.userStatus.entrySet()) {
            if (entry.getValue() == STATUS_READY) {
                readyNum += 1;
            }
        }

        //通知客户端谁是否准备
        Map<String, Integer> userStatus = new HashMap<>();
        for (Integer i : this.userStatus.keySet()) {
            userStatus.put(i + "", this.userStatus.get(i));
        }
        NoticeReady noticeReady = new NoticeReady();
        noticeReady.setUserStatus(userStatus);
        JSONObject getReadyResult = new JSONObject();
        getReadyResult.put("service", "roomService");
        getReadyResult.put("method", "noticeReady");
        getReadyResult.put("params", noticeReady.toJSONObject());
        getReadyResult.put("code", "0");
        serverContext.sendToOnlinePlayer(getReadyResult, this.users);

        //开始游戏
        if (readyNum >= personNumber) {
        	if(this.bankerId==0){
        		this.bankerId = users.get(0);
        	}
        	Room room = roomDao.getRoomByRoomId(roomId);
        	room.setRoleIds("1");//0表示未开始，1表示游戏已经开始
        	roomDao.saveRoom(room);
        	
            startGame();
        }
    }


    public void setUserSocre(int userId, int score) {
        if (!userScores.containsKey(userId)) {
//            logger.error("===设置分数时出错 userId = "+userId +"users: "+userScores.toString());
            return;
        }
        int s = userScores.get(userId);
        userScores.put(userId, s + score);
    }

    public void clearReadyStatus() {
//        GameManager.getInstance().remove(game);
        this.setGameInfo(null);
        this.setGame(null);
        this.setInGame(false);
        for (Map.Entry<Integer, Integer> entry : this.userStatus.entrySet()) {
            entry.setValue(STATUS_JOIN);
        }
        this.curGameNumber += 1;
        //每局的庄家
        this.bankerMap.put(curGameNumber, bankerId);

        //
        saveRecode();
    }


    private void insertRecode() {
        long time = System.currentTimeMillis();
        for (int userId : users) {
            User user = userDao.getUser(userId);
            UserRecode userRecode = new UserRecode();
            userRecode.setId(-100);
            userRecode.setUserId("" + userId);
            userRecode.setCreateTime(new Timestamp(System.currentTimeMillis()));
            userRecode.setRoomId(roomId + "|" + time);
            try {
                userRecode.setUserNames(URLEncoder.encode(user.getUsername(), "utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            userRecode.setScores("0");
            userRecodeDao.saveUserRecode(userRecode);
        }
    }

    public void saveRecode() {
        for (int uid : this.users) {
            UserRecode recode = userRecodeDao.getFirstUserRecode(uid);
            if (recode != null) {
                recode.setCreateTime(new Timestamp(System.currentTimeMillis()));
                recode.setScores(this.userScores.get(uid) + "");
                userRecodeDao.updateByPrimaryKeySelective(recode);
            }
        }
    }

    private GameInfo getGameInfoInstance() {
        switch (this.getGameType()) {
            case "SY"://松原麻将
                return new GameInfoSongYuan().setHasJieGangHu(true);
            case "JC"://进城麻将
                return new GameInfoJinCheng().setHasJieGangHu(true);
            case "SS"://盛世麻将
                return new GameInfoShengShi().setHasJieGangHu(true);
            case "124"://进城124麻将
                return new GameInfoJinCheng124().setHasJieGangHu(true);
            case "JCSS"://进城麻将盛世玩法
                return new GameInfoJinChengSS().setHasJieGangHu(true);
            case "JZ"://九州麻将
                return new GameInfoJiuZhou().setHasJieGangHu(true);
            case "JL":
            case "DS":
//            case "HT":
            case "DY":
                return new GameInfo().setHasJieGangHu(true);
            default:
                return new GameInfo();
        }
    }

    private void startGame() {
        this.isInGame = true;
        Game game = gameDao.createGame(roomId);
        gameDao.saveGame(game);
        if(this.gameType.equals("JC") && this.modeTotal.equals("124")){
        	this.gameType = "124";
        }else if(this.gameType.equals("JC") &&this.modeTotal.equals("13")){
        	this.gameType = "JCSS";
        }
        GameInfo gameInfo = getGameInfoInstance();
        //扣钱
        if (curGameNumber == 1) {
            spendMoney();
            insertRecode();
        }
        gameInfo.init(game.getId(), this.bankerId, this.users, this, roomDao, userRecodeDao, userDao, gameDao);
        gameInfo.fapai(this.serverContext);
        this.gameInfo = gameInfo;
        this.game = game;
        game.setGameInfo(gameInfo);


        gameDao.saveGame(game);
        GameManager.getInstance().addGame(game);


        //通知其他人游戏已经开始
        CardEntity cardBegin = new CardEntity();
        cardBegin.setCurrentUserId(this.getBankerId() + "");
        JSONObject beginResult = new JSONObject();
        beginResult.put("service", "gameService");
        beginResult.put("method", "gameBegin");
        beginResult.put("params", game.toJSONObjectOfGameBegin());
        beginResult.put("code", "0");
        serverContext.sendToOnlinePlayer(beginResult, this.getUsers());
        pushScoreChange();
    }

    public void pushScoreChange() {
        Gson gson = new Gson();
        String json = gson.toJson(userScores);
        JSONObject beginResult = new JSONObject();
        beginResult.put("service", "gameService");
        beginResult.put("method", "scoreChange");
        beginResult.put("params", json);
        beginResult.put("code", "0");
        serverContext.sendToOnlinePlayer(beginResult, this.getUsers());
    }

    /**
     * @param @param userId    设定文件
     * @return void    返回类型
     * @throws
     * @Title: 解散房间
     * @Creater: Clark
     * @Description: 解散房间
     */
    public void dissolution(int userId, boolean agreeOrNot, final UserDao userDao, final UserRecodeDao userRecodeDao, final ServerContext serverContext) {
        if (!this.users.contains(userId)) {
            throw new BusinessException(ErrorCode.CANNOT_FIND_THIS_USER,
                    "cannot find this user");
        }
//        if (this.userStatus.get(userId).equals(STATUS_AGREE_DISSOLUTION) || this.userStatus.get(userId).equals(STATUS_DISSOLUTION)) {
//            throw new BusinessException(ErrorCode.THE_DISSOLUTION_HAVE_BEEN_DEAL,
//                    "this dissolution have been deal");
//        }
        this.userStatus.put(userId, agreeOrNot ? STATUS_AGREE_DISSOLUTION : STATUS_DISSOLUTION);

        //第一个点解散
        if (agreeOrNot && !isHasDissolutionRequest) {
            isCanDissloution = true;
            this.isHasDissolutionRequest = true;
            //第一次申请 五分钟后解散
            long start = System.currentTimeMillis();
            TimerNode node = new TimerNode(start, FIVE_MIN, false, new ITimeHandler() {
                @Override
                public void fire() {
                    try {
                        if (isCanDissloution) {
                            dissolutionRoom(userDao, userRecodeDao, serverContext);
//                            logger.info("===2定时解散 roomId: "+roomId);
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            });
            this.timerNode = node;
            GameTimer.getInstance().addTimerNode(node);
        }


        ArrayList<AnswerUser> answerUsers = new ArrayList<AnswerUser>();
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

        JSONObject noticeResult = new JSONObject();
        noticeResult.put("service", "roomService");
        noticeResult.put("method", "noticeAnswerIfDissolveRoom");
        noticeResult.put("params", accept.toJSONObject());
        noticeResult.put("code", "0");

        serverContext.sendToOnlinePlayer(noticeResult, this.users);


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
            try {
                GameTimer.getInstance().removeNode(timerNode);
                dissolutionRoom(userDao, userRecodeDao, serverContext);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        //不同意的人数大于2 解散取消
        if (disAgreeNum >= 1) {
            for (Map.Entry<Integer, Integer> entry : userStatus.entrySet()) {
                //回到游戏状态
                entry.setValue(STATUS_IN_GAME);
                this.isHasDissolutionRequest = false;
                GameTimer.getInstance().removeNode(timerNode);
            }
        }
    }

    private void dissolutionRoom(UserDao userDao, UserRecodeDao userRecodeDao, ServerContext serverContext) throws UnsupportedEncodingException {

        //算杠
        if (gameInfo != null && !gameInfo.isAlreadyHu) {
            gameInfo.computeAllGang();
        }

        GameManager.getInstance().remove(this);

        // 结果类
        ArrayList<UserOfResult> userOfResultList = new ArrayList<>();

        long time = System.currentTimeMillis();
        for (int i = 0; i < this.users.size(); i++) {
            UserOfResult resultObj = new UserOfResult();
            User eashUser = userDao.getUser(this.users.get(i));
            eashUser.setRoomId("0");
            eashUser.setSeatId("0");
            userDao.saveUser(eashUser);

            resultObj.setUsername(URLDecoder.decode(eashUser.getUsername(), "utf-8"));
            resultObj.setImage(eashUser.getImage());
            resultObj.setScores(this.userScores.get(this.users.get(i)) + "");
            resultObj.setUserId(users.get(i));
            resultObj.setTime(time);

            //设置胡牌次数

            if(this.getHuNum().containsKey(eashUser.getId())){

                resultObj.setHuNum(this.getHuNum().get(eashUser.getId()));
            }
            if(this.getLianZhuangNum().containsKey(eashUser.getId())){

                resultObj.setLianZhuangNum(this.getLianZhuangNum().get(eashUser.getId()));
            }
            if(this.getDianPaoNum().containsKey(eashUser.getId())){

                resultObj.setDianPaoNum((this.getDianPaoNum().get(eashUser.getId())));
            }
            if(this.getMoBaoNum().containsKey(eashUser.getId())){

                resultObj.setMoBaoNum(this.getMoBaoNum().get(eashUser.getId()));
            }


            userOfResultList.add(resultObj);
            //删除映射关系
            GameManager.getInstance().getUserRoom().remove(users.get(i));
        }

        boolean isChange = scoreIsChange();
        if (this.isInGame && this.curGameNumber == 1 && !isChange) {
            drawBack();
        }

        if (isChange) {
            //保存
            saveRecode();
        }

        this.isInGame = false;
        // 存储返回
        GameOfResult gameOfResult = new GameOfResult();
        gameOfResult.setUserList(userOfResultList);
        GameManager.getInstance().getRoomLock().remove(roomId);

        JSONObject noticeEndResult = new JSONObject();
        noticeEndResult.put("service", "gameService");
        noticeEndResult.put("method", "askNoticeDissolutionResult");
        noticeEndResult.put("params", gameOfResult.toJSONObject());
        noticeEndResult.put("code", "0");
        serverContext.sendToOnlinePlayer(noticeEndResult, this.users);

    }

    public boolean scoreIsChange() {
        for (int score : userScores.values()) {
            if (score != 0) {
                return true;
            }
        }
        return false;
    }

    public void drawBack() {
    	
    	if("1".equals(each)){
    		drawBackEach();
    	}else{
    		User eachUser = userDao.getUser(this.createUser);
            if("JC".equals(gameType)){
                if(1==gameNumber){
                    eachUser.setMoney(eachUser.getMoney() + 3);
                }else if(2==gameNumber){
                    eachUser.setMoney(eachUser.getMoney() + 6);
                }
            }else if("SY".equals(gameType)){
                if(4==gameNumber){
                    eachUser.setMoney(eachUser.getMoney() + 1);
                }else if(8==gameNumber){
                    eachUser.setMoney(eachUser.getMoney() + 2);
                }
            }else if("LQ".equals(gameType)){
                if(8==gameNumber){
                    eachUser.setMoney(eachUser.getMoney() + 30);
                    eachUser.setMarquee(Integer.parseInt(eachUser.getMarquee())-3+"");
                }else if(16==gameNumber){
                    eachUser.setMoney(eachUser.getMoney() + 60);
                    eachUser.setMarquee(Integer.parseInt(eachUser.getMarquee())-6+"");
                }
            }else{
                eachUser.setMoney(eachUser.getMoney() + 3);
            }
            userDao.saveUser(eachUser);
    	}
    }

    public void drawBackEach() {
    	if(8==gameNumber){
    		for (int userId : users) {
                User eachUser = userDao.getUser(userId);
                eachUser.setMoney(eachUser.getMoney() + 10);
                eachUser.setMarquee(Integer.parseInt(eachUser.getMarquee())-1+"");
                userDao.saveUser(eachUser);
            }
        }else if(16==gameNumber){
        	for (int userId : users) {
                User eachUser = userDao.getUser(userId);
                eachUser.setMoney(eachUser.getMoney() + 20);
                eachUser.setMarquee(Integer.parseInt(eachUser.getMarquee())-2+"");
                userDao.saveUser(eachUser);
            }
        }
    }
    
    public void spendMoney() {
    	
    	if("1".equals(each)){
    		spendMoneyEach();
    	}else if("2".equals(each)){
    		return;
    	}else{
    		User eachUser = userDao.getUser(this.createUser);
            if("JC".equals(gameType)){
                if(1==gameNumber){
                    eachUser.setMoney(eachUser.getMoney() - 3);
                }else if(2==gameNumber){
                    eachUser.setMoney(eachUser.getMoney() - 6);
                }
            }else if("SY".equals(gameType)){
                if(4==gameNumber){
                    eachUser.setMoney(eachUser.getMoney() - 1);
                }else if(8==gameNumber){
                    eachUser.setMoney(eachUser.getMoney() - 2);
                }
            }else if("LQ".equals(gameType)){
                if(8==gameNumber){
                    eachUser.setMoney(eachUser.getMoney() - 30);
                    eachUser.setMarquee(Integer.parseInt(eachUser.getMarquee())+3+"");
                }else if(16==gameNumber){
                    eachUser.setMoney(eachUser.getMoney() - 60);
                    eachUser.setMarquee(Integer.parseInt(eachUser.getMarquee())+6+"");
                }
            } else{
                eachUser.setMoney(eachUser.getMoney() - 3);
            }
            userDao.saveUser(eachUser);
    	}
    }

    public void spendMoneyEach() {
    	if(8==gameNumber){
    		for (int userId : users) {
                User eachUser = userDao.getUser(userId);
                eachUser.setMoney(eachUser.getMoney() - 10);
                eachUser.setMarquee(Integer.parseInt(eachUser.getMarquee())+1+"");
                userDao.saveUser(eachUser);
            }
        }else if(16==gameNumber){
        	for (int userId : users) {
                User eachUser = userDao.getUser(userId);
                eachUser.setMoney(eachUser.getMoney() - 20);
                eachUser.setMarquee(Integer.parseInt(eachUser.getMarquee())+2+"");
                userDao.saveUser(eachUser);
            }
        }
        
    }
    
    public void addHuNum(int userId) {
        if (huNum.containsKey(userId)) {
            huNum.put(userId, huNum.get(userId) + 1);
        } else {
            huNum.put(userId, 1);
        }
    }

    public void addLianZhuangNum(int userId) {
        if (lianZhuangNum.containsKey(userId)) {
            lianZhuangNum.put(userId, lianZhuangNum.get(userId) + 1);
        } else {
            lianZhuangNum.put(userId, 1);
        }
    }

    public void addDianPaoNum(int userId) {
        if (dianPaoNum.containsKey(userId)) {
            dianPaoNum.put(userId, dianPaoNum.get(userId) + 1);
        } else {
            dianPaoNum.put(userId, 1);
        }
    }

    public void addMoBaoNum(int userId) {
        if (moBaoNum.containsKey(userId)) {
            moBaoNum.put(userId, moBaoNum.get(userId) + 1);
        } else {
            moBaoNum.put(userId, 1);
        }
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public Map<Integer, Integer> getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(Map<Integer, Integer> userStatus) {
        this.userStatus = userStatus;
    }

    public String getModeTotal() {
        return modeTotal;
    }

    public void setModeTotal(String modeTotal) {
        this.modeTotal = modeTotal;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public int getMultiple() {
        return multiple;
    }

    public void setMultiple(int multiple) {
        this.multiple = multiple;
    }

    public int getGameNumber() {
        return gameNumber;
    }

    public void setGameNumber(int gameNumber) {
        this.gameNumber = gameNumber;
    }

    public int getPersonNumber() {
        return personNumber;
    }

    public void setPersonNumber(int personNumber) {
        this.personNumber = personNumber;
    }

    public int getCreateUser() {
        return createUser;
    }

    public void setCreateUser(int createUser) {
        this.createUser = createUser;
    }

    public int getBankerId() {
        return bankerId;
    }

    public void setBankerId(int bankerId) {
        this.bankerId = bankerId;
    }

    public List<Integer> getUsers() {
        return users;
    }

    public void setUsers(List<Integer> users) {
        this.users = users;
    }

    public boolean isInGame() {
        return isInGame;
    }

    public void setInGame(boolean isInGame) {
        this.isInGame = isInGame;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    public Object toJSONObject() {
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("roomId", this.roomId);
        jSONObject.put("modeTotal", this.modeTotal);
        jSONObject.put("mode", this.mode);
        jSONObject.put("multiple", this.multiple);
        jSONObject.put("gameNumber", this.gameNumber);
        jSONObject.put("personNumber", this.personNumber);
        jSONObject.put("createUser", this.createUser);
        jSONObject.put("userList", this.userList);
        jSONObject.put("each", this.each);//1是4个分开付，0是user付
        return jSONObject;
    }

    public RoomDao getRoomDao() {
        return roomDao;
    }

    public void setRoomDao(RoomDao roomDao) {
        this.roomDao = roomDao;
    }

    public UserRecodeDao getUserRecodeDao() {
        return userRecodeDao;
    }

    public void setUserRecodeDao(UserRecodeDao userRecodeDao) {
        this.userRecodeDao = userRecodeDao;
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public GameDao getGameDao() {
        return gameDao;
    }

    public void setGameDao(GameDao gameDao) {
        this.gameDao = gameDao;
    }

    public ServerContext getServerContext() {
        return serverContext;
    }

    public void setServerContext(ServerContext serverContext) {
        this.serverContext = serverContext;
    }

    public GameInfo getGameInfo() {
        return gameInfo;
    }

    public void setGameInfo(GameInfo gameInfo) {
        this.gameInfo = gameInfo;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public int getCurGameNumber() {
        return curGameNumber;
    }

    public RoomInfo setCurGameNumber(int curGameNumber) {
        this.curGameNumber = curGameNumber;
        return this;
    }

    public Map<Integer, Integer> getUserScores() {
        return userScores;
    }

    public RoomInfo setUserScores(Map<Integer, Integer> userScores) {
        this.userScores = userScores;
        return this;
    }

    public Map<Integer, Integer> getBankerMap() {
        return bankerMap;
    }

    public RoomInfo setBankerMap(Map<Integer, Integer> bankerMap) {
        this.bankerMap = bankerMap;
        return this;
    }

    public String getGameType() {
        return gameType;
    }

    public RoomInfo setGameType(String gameType) {
        this.gameType = gameType;
        return this;
    }

    public boolean isHasGangBlackList() {
        return isHasGangBlackList;
    }

    public RoomInfo setHasGangBlackList(boolean hasGangBlackList) {
        isHasGangBlackList = hasGangBlackList;
        return this;
    }


    public Map<Integer, Integer> getCircleNumber() {
        return circleNumber;
    }


    public void setCircleNumber(Map<Integer, Integer> circleNumber) {
        this.circleNumber = circleNumber;
    }

    /**
     *   设定文件
     * @return void    返回类型
     * @throws
     * @Title: 添加1
     * @Creater: Clark
     * @Description: TODO(这里用一句话描述这个方法的作用)
     */
    public void addOneToCircleNumber() {
        int temp = getCurCircle();
        this.circleNumber.put(temp, this.circleNumber.get(temp) + 1);
        if (this.circleNumber.get(temp) >= 5) {//4人轮完，下一圈
            this.circleNumber.put(temp + 1, 1);
        }
    }

    public int getCurCircle(){
        int temp = 1;
        for (Integer i : this.circleNumber.keySet()) {
            if (i > temp) {
                temp = i;
            }
        }
        return temp;
    }

    public Map<Integer, Integer> getHuNum() {
        return huNum;
    }

    public RoomInfo setHuNum(Map<Integer, Integer> huNum) {
        this.huNum = huNum;
        return this;
    }

    public Map<Integer, Integer> getDianPaoNum() {
        return dianPaoNum;
    }

    public RoomInfo setDianPaoNum(Map<Integer, Integer> dianPaoNum) {
        this.dianPaoNum = dianPaoNum;
        return this;
    }

    public Map<Integer, Integer> getLianZhuangNum() {
        return lianZhuangNum;
    }

    public RoomInfo setLianZhuangNum(Map<Integer, Integer> lianZhuangNum) {
        this.lianZhuangNum = lianZhuangNum;
        return this;
    }

    public Map<Integer, Integer> getMoBaoNum() {
        return moBaoNum;
    }

    public RoomInfo setMoBaoNum(Map<Integer, Integer> moBaoNum) {
        this.moBaoNum = moBaoNum;
        return this;
    }

    public boolean isCanDissloution() {
        return isCanDissloution;
    }

    public RoomInfo setCanDissloution(boolean canDissloution) {
        isCanDissloution = canDissloution;
        return this;
    }
}
