package com.code.server.game.mahjong.logic;


import com.code.server.constant.game.PrepareRoom;
import com.code.server.constant.game.PrepareRoomMj;
import com.code.server.constant.game.UserBean;
import com.code.server.constant.response.*;
import com.code.server.game.room.Room;
import com.code.server.game.room.kafka.MsgSender;
import com.code.server.game.room.service.RoomManager;
import com.code.server.redis.service.RedisManager;
import com.code.server.util.timer.GameTimer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RoomInfo extends Room {


//    private static final Logger logger = Logger.getLogger("game");

    protected String modeTotal;
    protected String mode;
    protected Map<Integer, Long> bankerMap = new HashMap<>();
    protected Map<Integer, Integer> circleNumber = new HashMap<>();//圈数，key存圈数，value存庄家换人的次数
    protected int maxCircle;

    protected boolean isHasGangBlackList = true;

    protected Map<Long, Integer> huNum = new HashMap<>();
    protected Map<Long, Integer> dianPaoNum = new HashMap<>();
    protected Map<Long, Integer> lianZhuangNum = new HashMap<>();
    protected Map<Long, Integer> moBaoNum = new HashMap<>();
    //荒庄后是否换庄家
    private boolean isChangeBankerAfterHuangZhuang = false;


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
    public void init(String roomId, long userId, String modeTotal, String mode, int multiple, int gameNumber, int personNumber, long createUser, long bankerId,int mustZimo) {
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
        this.mustZimo = mustZimo;
    }


    @Override
    protected boolean isCanJoinCheckMoney(long userId) {
        if(!isCreaterJoin){
            return true;
        }
        double money = RedisManager.getUserRedisService().getUserMoney(userId);
        if (userId == createUser) {
            if ("SY".equals(gameType)) {
                if (money < 1) {
                    return false;
                }
            } else if ("LQ".equals(gameType)) {
                if (each.equals("0") && money < 30) {
                    return false;
                }
            } else if ("HL".equals(gameType)) {
                if (each.equals("0") && money < 1) {
                    return false;
                }
            }else {
                if (money < 3) {
                    return false;
                }
            }
        } else {
            if ("LQ".equals(gameType)) {
                if (each.equals("1") && money < 10 && this.gameNumber == 8) {
                    return false;
                }
                if (each.equals("1") && money < 20 && this.gameNumber == 16) {
                    return false;
                }
            }
        }
        return true;
    }


    protected boolean isHasMode(int type) {
        int c = Integer.parseInt(this.mode);
        return (c & (1 << type)) >> type == 1;
    }

    public void setUserSocre(long userId, double score) {
        if (!userScores.containsKey(userId)) {
//            logger.error("===设置分数时出错 userId = "+userId +"users: "+userScores.toString());
            return;
        }
        double s = userScores.get(userId);
        userScores.put(userId, s + score);
    }

    public void clearReadyStatus() {
//        GameManager.getInstance().remove(game);
        this.setGame(null);

        this.setInGame(false);
        for (Map.Entry<Long, Integer> entry : this.userStatus.entrySet()) {
            entry.setValue(STATUS_JOIN);
        }
        this.curGameNumber += 1;
        //每局的庄家
        this.bankerMap.put(curGameNumber, bankerId);


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
            case "TJ":
                return new GameInfoTJ().setHasJieGangHu(true);
            default:
                return new GameInfo();
        }
    }

    public void startGame() {
        //确定庄家
        if (this.bankerId == 0) {
            this.bankerId = users.get(0);
        }
        this.isInGame = true;

        if (this.gameType.equals("JC") && this.modeTotal.equals("124")) {
            this.gameType = "124";
        } else if (this.gameType.equals("JC") && this.modeTotal.equals("13")) {
            this.gameType = "JCSS";
        }
        GameInfo gameInfo = getGameInfoInstance();
        //扣钱
        if (curGameNumber == 1) {
            spendMoney();
        }
        //游戏开始 代建房 去除定时解散
        if(!isOpen && !this.isCreaterJoin()){
            GameTimer.removeNode(prepareRoomTimerNode);
        }

        gameInfo.init(0, this.bankerId, this.users, this);
        gameInfo.fapai();
        this.game = gameInfo;


        //通知其他人游戏已经开始


        MsgSender.sendMsg2Player(new ResponseVo("gameService", "gameBegin", toJSONObjectOfGameBegin()), this.getUsers());
        pushScoreChange();
    }


    //游戏开始
    public Map toJSONObjectOfGameBegin() {
        Map<String, Object> result = new HashMap<>();
        result.put("gameId", 0);
        result.put("roomId", this.roomId);

        result.put("currentBanker", ((GameInfo) this.game).getFirstTurn());
        result.put("gameNumber", this.getCurGameNumber());
        result.put("circleNum", this.getCurCircle());

        return result;
    }

    public List<UserOfResult> getUserOfResult(){
        // 结果类
        ArrayList<UserOfResult> userOfResultList = new ArrayList<>();

        long time = System.currentTimeMillis();
        for (UserBean eachUser : RedisManager.getUserRedisService().getUserBeans(this.users)) {
            UserOfResult resultObj = new UserOfResult();
            resultObj.setUsername(eachUser.getUsername());
            resultObj.setImage(eachUser.getImage());
            resultObj.setScores(this.userScores.get(eachUser.getId()) + "");
            resultObj.setUserId(eachUser.getId());
            resultObj.setTime(time);

            //设置胡牌次数

            if (this.getHuNum().containsKey(eachUser.getId())) {
                resultObj.setHuNum(this.getHuNum().get(eachUser.getId()));
            }
            if (this.getLianZhuangNum().containsKey(eachUser.getId())) {
                resultObj.setLianZhuangNum(this.getLianZhuangNum().get(eachUser.getId()));
            }
            if (this.getDianPaoNum().containsKey(eachUser.getId())) {
                resultObj.setDianPaoNum((this.getDianPaoNum().get(eachUser.getId())));
            }
            if (this.getMoBaoNum().containsKey(eachUser.getId())) {
                resultObj.setMoBaoNum(this.getMoBaoNum().get(eachUser.getId()));
            }

            userOfResultList.add(resultObj);

            //删除映射关系
//            RedisManager.getUserRedisService().removeRoom(eachUser.getId());
        }
        return userOfResultList;
    }
    public void dissolutionRoom() {

        //算杠
        GameInfo gameInfo = (GameInfo) this.game;
        if (gameInfo != null && !gameInfo.isAlreadyHu) {
            gameInfo.computeAllGang();
        }

        RoomManager.removeRoom(this.roomId);

        // 结果类
        List<UserOfResult> userOfResultList = getUserOfResult();


        boolean isChange = scoreIsChange();
        if (this.isInGame && this.curGameNumber == 1 && !isChange) {
            drawBack();
        }

        if (isChange && gameInfo!=null) {
            gameInfo.genRecord();
        }

        this.isInGame = false;
        // 存储返回
        GameOfResult gameOfResult = new GameOfResult();
        gameOfResult.setUserList(userOfResultList);


        MsgSender.sendMsg2Player(new ResponseVo("gameService", "askNoticeDissolutionResult", gameOfResult), users);
//        serverContext.sendToOnlinePlayer(noticeEndResult, this.users);

        //战绩
        genRoomRecord();

    }


    public static int getCreateMoney(String gameType , int gameNumber){
        int result = 0;
        if ("JC".equals(gameType)) {
            if (1 == gameNumber) {
                result = 3;
            } else if (2 == gameNumber) {
                result = 6;
            }
        } else if ("SY".equals(gameType)) {
            if (4 == gameNumber) {
                result = 1;
            } else if (8 == gameNumber) {
                result = 2;
            }
        } else if ("LQ".equals(gameType)) {
            if (8 == gameNumber) {
                result = 30;
            } else if (16 == gameNumber) {
                result = 60;
            }
        }else if("HL".equals(gameType)){
            result = 1;
        } else {
            result = 3;
        }
        return result;
    }
    protected int getCreateMoney() {
        return getCreateMoney(gameType, gameNumber);
    }

    public boolean isAddGold() {
        return "LQ".equals(gameType);
    }



    public void drawBack() {

        if ("1".equals(each)) {
            drawBackEach();
        } else {
            int money = getCreateMoney();
            RedisManager.getUserRedisService().addUserMoney(this.createUser, money);
            if (isAddGold()) {
                RedisManager.addGold(this.createUser, -money / 10);
            }


        }
    }

    public void drawBackEach() {
        for (long userId : users) {
            int money = 10;
            if (gameNumber == 16) {
                money = 20;
            }
            RedisManager.getUserRedisService().addUserMoney(userId, money);
            if (isAddGold()) {
                RedisManager.addGold(this.createUser, -money / 10);
            }
        }
    }

    public void spendMoney() {

        if ("1".equals(each)) {
            spendMoneyEach();
        } else if ("2".equals(each)) {
            return;
        } else {
            int money = -getCreateMoney();
            RedisManager.getUserRedisService().addUserMoney(this.createUser, money);
            if (isAddGold()) {

                RedisManager.addGold(this.createUser, -money / 10);
            }
        }
    }

    public void spendMoneyEach() {
        for (long userId : users) {
            int money = 10;
            if (gameNumber == 16) {
                money = 20;
            }
            RedisManager.getUserRedisService().addUserMoney(userId, -money);
            if (isAddGold()) {
                RedisManager.addGold(this.createUser, money / 10);
            }
        }
    }

    public void addHuNum(long userId) {
        if (huNum.containsKey(userId)) {
            huNum.put(userId, huNum.get(userId) + 1);
        } else {
            huNum.put(userId, 1);
        }
    }

    public void addLianZhuangNum(long userId) {
        if (lianZhuangNum.containsKey(userId)) {
            lianZhuangNum.put(userId, lianZhuangNum.get(userId) + 1);
        } else {
            lianZhuangNum.put(userId, 1);
        }
    }

    public void addDianPaoNum(long userId) {
        if (dianPaoNum.containsKey(userId)) {
            dianPaoNum.put(userId, dianPaoNum.get(userId) + 1);
        } else {
            dianPaoNum.put(userId, 1);
        }
    }

    public void addMoBaoNum(long userId) {
        if (moBaoNum.containsKey(userId)) {
            moBaoNum.put(userId, moBaoNum.get(userId) + 1);
        } else {
            moBaoNum.put(userId, 1);
        }
    }


    public Map<String, Object> toJSONObject() {
        Map<String, Object> result = new HashMap<>();
        result.put("roomType", this.roomType);
        result.put("roomId", this.roomId);
        result.put("modeTotal", this.modeTotal);
        result.put("mode", this.mode);
        result.put("multiple", this.multiple);
        result.put("gameNumber", this.gameNumber);
        result.put("personNumber", this.personNumber);
        result.put("createUser", this.createUser);
        result.put("userList", RedisManager.getUserRedisService().getUserBeans(this.users));
        result.put("mustZimo",this.mustZimo);
        result.put("each", this.each);//1是4个分开付，0是user付


        return result;
    }


    /**
     * 设定文件
     *
     * @return void    返回类型
     * @throws
     * @Title: 添加1
     * @Creater: Clark
     * @Description:
     */
    public void addOneToCircleNumber() {
        int temp = getCurCircle();
        this.circleNumber.put(temp, this.circleNumber.get(temp) + 1);
        if (this.circleNumber.get(temp) >= 5) {//4人轮完，下一圈
            this.circleNumber.put(temp + 1, 1);
        }
    }

    public int getCurCircle() {
        int temp = 1;
        for (Integer i : this.circleNumber.keySet()) {
            if (i > temp) {
                temp = i;
            }
        }
        return temp;
    }

    public String getModeTotal() {
        return modeTotal;
    }

    public RoomInfo setModeTotal(String modeTotal) {
        this.modeTotal = modeTotal;
        return this;
    }

    public String getMode() {
        return mode;
    }

    public RoomInfo setMode(String mode) {
        this.mode = mode;
        return this;
    }

    public Map<Integer, Long> getBankerMap() {
        return bankerMap;
    }

    public RoomInfo setBankerMap(Map<Integer, Long> bankerMap) {
        this.bankerMap = bankerMap;
        return this;
    }

    public Map<Integer, Integer> getCircleNumber() {
        return circleNumber;
    }

    public RoomInfo setCircleNumber(Map<Integer, Integer> circleNumber) {
        this.circleNumber = circleNumber;
        return this;
    }

    public int getMaxCircle() {
        return maxCircle;
    }

    public RoomInfo setMaxCircle(int maxCircle) {
        this.maxCircle = maxCircle;
        return this;
    }

    public boolean isHasGangBlackList() {
        return isHasGangBlackList;
    }

    public RoomInfo setHasGangBlackList(boolean hasGangBlackList) {
        isHasGangBlackList = hasGangBlackList;
        return this;
    }

    public Map<Long, Integer> getHuNum() {
        return huNum;
    }

    public RoomInfo setHuNum(Map<Long, Integer> huNum) {
        this.huNum = huNum;
        return this;
    }

    public Map<Long, Integer> getDianPaoNum() {
        return dianPaoNum;
    }

    public RoomInfo setDianPaoNum(Map<Long, Integer> dianPaoNum) {
        this.dianPaoNum = dianPaoNum;
        return this;
    }

    public Map<Long, Integer> getLianZhuangNum() {
        return lianZhuangNum;
    }

    public RoomInfo setLianZhuangNum(Map<Long, Integer> lianZhuangNum) {
        this.lianZhuangNum = lianZhuangNum;
        return this;
    }

    public Map<Long, Integer> getMoBaoNum() {
        return moBaoNum;
    }

    public RoomInfo setMoBaoNum(Map<Long, Integer> moBaoNum) {
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

    public boolean isChangeBankerAfterHuangZhuang() {
        return isChangeBankerAfterHuangZhuang;
    }

    public RoomInfo setChangeBankerAfterHuangZhuang(boolean changeBankerAfterHuangZhuang) {
        isChangeBankerAfterHuangZhuang = changeBankerAfterHuangZhuang;
        return this;
    }

    public int getMustZimo() {
        return mustZimo;
    }

    public void setMustZimo(int mustZimo) {
        this.mustZimo = mustZimo;
    }

    @Override
    public IfaceRoomVo toVo(long userId) {
        RoomInfoVo roomVo = new RoomInfoVo();
        roomVo.roomType = this.getRoomType();
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
        roomVo.isOpen = this.isOpen;
        roomVo.setMode(this.getMode());
        roomVo.setModeTotal(this.getModeTotal());
        roomVo.setEach(this.getEach());
        roomVo.setMustZimo(this.mustZimo);
        RedisManager.getUserRedisService().getUserBeans(users).forEach(userBean -> roomVo.userList.add(userBean.toVo()));
        if (this.getGame() != null) {
            roomVo.game = this.game.toVo(userId);
        }
        return roomVo;
    }

    @Override
    public PrepareRoom getPrepareRoomVo() {
        PrepareRoomMj prepareRoom = new PrepareRoomMj();

        prepareRoom.createTime = System.currentTimeMillis();
        prepareRoom.gameType = this.getGameType();
        prepareRoom.roomType = this.getRoomType();
        prepareRoom.roomId = this.roomId;
        prepareRoom.multiple = this.multiple;
        prepareRoom.gameNumber = this.gameNumber;
        prepareRoom.mode = this.mode;
        prepareRoom.modeTotal = this.modeTotal;
        prepareRoom.mustZimo = this.mustZimo;
        return prepareRoom;
    }
}
