package com.code.server.game.poker.doudizhu;


import com.code.server.constant.data.DataManager;
import com.code.server.constant.data.StaticDataProto;
import com.code.server.constant.game.CardStruct;
import com.code.server.constant.kafka.IKafaTopic;
import com.code.server.constant.kafka.KafkaMsgKey;
import com.code.server.constant.response.*;
import com.code.server.game.room.Game;
import com.code.server.game.room.Room;
import com.code.server.game.room.kafka.MsgSender;
import com.code.server.game.room.service.RoomManager;
import com.code.server.kafka.MsgProducer;
import com.code.server.util.IdWorker;
import com.code.server.util.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

import static com.code.server.game.poker.doudizhu.GamePaodekuai.mode_不洗牌;

/**
 * Created by sunxianping on 2017/3/13.
 */
public class GameDouDiZhu extends Game {
    protected static final Logger logger = LoggerFactory.getLogger(GameDouDiZhu.class);

    protected int initCardNum = 17;//每人17张
    protected List<Integer> cards = new ArrayList<>();//牌
    protected List<Integer> disCards = new ArrayList<>();//丢弃的牌
    protected List<Integer> tableCards = new ArrayList<>();//底牌
    protected Map<Long, PlayerCardInfoDouDiZhu> playerCardInfos = new HashMap<>();
    protected Random rand = new Random();
    protected long dizhu = -1;//地主
    protected Set<Long> chooseJiaoSet = new HashSet<>();//叫过地主的人
    protected Set<Long> chooseQiangSet = new HashSet<>();//抢过地主的人
    protected Set<Long> bujiaoSet = new HashSet<>();//不叫的集合
    protected Set<Long> buqiangSet = new HashSet<>();//不抢的集合

    protected int lasttype = 0;//上一个人出牌的类型

    protected long canJiaoUser;//可以叫地主的人
    protected long canQiangUser;//可以抢地主的人
    protected long jiaoUser;//叫的人
    protected long qiangUser;//抢的人

    protected long playTurn;//该出牌的人

    protected CardStruct lastCardStruct;//上一个人出的牌

    protected int step;//步骤

    protected int zhaCount;//炸的个数
    protected int multiple = 1;
    protected int maxZha;
    protected RoomDouDiZhu room;
    protected boolean isSpring;//是否春天
    protected Set<Long> userPlayCount = new HashSet<>();
    protected int tableScore;//底分
    protected boolean isNMQiang = false;//农民是否抢过
    protected ReplayDouDiZhu replay = new ReplayDouDiZhu();


    public void startGame(List<Long> users, Room room) {
        this.room = (RoomDouDiZhu)room;
        init(users, room.getBankerId());
        updateLastOperateTime();
        //通知其他人游戏已经开始
        MsgSender.sendMsg2Player(new ResponseVo("gameService", "gameBegin", "ok"), this.getUsers());
    }

    public void init(List<Long> users, long dizhuUser) {
        //初始化玩家
        for (Long uid : users) {
            PlayerCardInfoDouDiZhu playerCardInfo = getGameTypePlayerCardInfo();
            playerCardInfo.userId = uid;
            playerCardInfos.put(uid, playerCardInfo);
        }
        this.users.addAll(users);



        shuffle();
        handleNoShuffle();
        deal();
        //第一局 第一个玩家做地主
        dizhuUser = dizhuUser != 0 ? dizhuUser : users.get(0);
        chooseDizhu(dizhuUser);
    }

    protected void putCard2LastGameCards(List<Integer> cards) {
        if (isNoShuffle()) {
            this.room.getLastGameCards().addAll(cards);
        }

    }

    protected void handleNoShuffle(){
        if (isNoShuffle() && this.room.getLastGameCards().size()>0) {
            this.cards.clear();
            this.cards.addAll(this.room.getLastGameCards());
            //清空 不洗牌的牌堆
            this.room.getLastGameCards().clear();
        }
    }

    public PlayerCardInfoDouDiZhu getGameTypePlayerCardInfo() {
        switch (room.getGameType()) {
            case Room.GAMETYPE_LINFEN:
            case Room.GAMETYPE_LONGQI_LINFEN:
            case Room.GAMETYPE_LONGQI_LINFEN_NO_QIANG:
                return new PlayerCardInfoDouDiZhuLinfen();
            case Room.GAMETYPE_QIANAN:
            case Room.GAMETYPE_LONGQI:
                return new PlayerCardInfoDouDiZhu();
            case Room.GAMETYPE_LONGQI_JIXIAN:
                return new PlayerCardInfoDouDiZhuJixian();
            case Room.GAMETYPE_LONGQI_JIXIAN_NO_QIANG:
                return new PlayerCardInfoDouDiZhuJixian();
            case Room.GAMETYPE_MAOSAN:
            case Room.GAMETYPE_MAOSAN_NO_QIANG:
                return new PlayerCardInfoMaoSan();
            case GAMETYPE_HUAPAI:
            case GAMETYPE_HUAPAI_NO_QIANG:
                return new PlayerCardInfoHuaPai();
            default:
                return new PlayerCardInfoDouDiZhu();
        }
    }

    protected boolean isNoShuffle(){
        return isHasModel(mode_不洗牌);
    }


    /**
     * 是否有此模式
     * @param type
     * @return
     */
    protected boolean isHasModel(int type) {
        return Room.isHasMode(type,this.room.getOtherMode());
    }

    /**
     * 出牌
     *
     * @param userId
     */
    public int play(long userId, CardStruct cardStruct) {
        PlayerCardInfoDouDiZhu playerCardInfo = playerCardInfos.get(userId);
        //不可出牌
        if (!playerCardInfo.checkPlayCard(lastCardStruct, cardStruct, lasttype)) {
            return ErrorCode.CAN_NOT_PLAY;
        }

        //记录这局的出牌
        putCard2LastGameCards(cardStruct.cards);

        userPlayCount.add(userId);
        playerCardInfo.setPlayCount(playerCardInfo.getPlayCount() + 1);

        long nextUserCard = nextTurnId(cardStruct.getUserId()); //下一个出牌的人

        cardStruct.setNextUserId(nextUserCard);
        cardStruct.setUserId(userId);

        playTurn = nextUserCard;

        MsgSender.sendMsg2Player(new ResponseVo("gameService", "playResponse", cardStruct), this.users);
        lasttype = cardStruct.getType();//保存这次出牌的类型
        lastCardStruct = cardStruct;//保存这次出牌的牌型


        //删除牌
        playerCardInfo.cards.removeAll(cardStruct.getCards());

        //处理炸
        handleBomb(cardStruct);

        //回放
        replay.getOperate().add(Operate.getOperate_PLAY(userId, cardStruct, false));

        //牌打完
        if (playerCardInfo.cards.size() == 0) {
            PlayerCardInfoDouDiZhu playerCardInfoDizhu = playerCardInfos.get(dizhu);
            //是否是春天

            if (userPlayCount.size() == 1 || playerCardInfoDizhu.getPlayCount() == 1) {
                isSpring = true;
                //炸 封顶的情况
                if (zhaCount < room.getMultiple() || room.getMultiple() == -1) {
                    multiple *= 2;
                }
            }

            compute(playerCardInfo.getUserId() == dizhu);

            sendResult(false, playerCardInfo.getUserId() == dizhu);


            //生成记录
            genRecord();

            //下局是否是赢得人做地主
            boolean isWinner2Dizhu = false;
            StaticDataProto.RoomData roomData = DataManager.data.getRoomDataMap().get(this.room.getGameType());
            if (roomData != null) {
                isWinner2Dizhu = DataManager.data.getRoomDataMap().get(this.room.getGameType()).getIsWinner2Dizhu() == 1;
            }
            if (isWinner2Dizhu) {
                this.room.setBankerId(userId);
            }

            room.clearReadyStatus(true);

            sendFinalResult();

        }
        MsgSender.sendMsg2Player("gameService", "play", 0, userId);
//        userId.sendMsg("gameService", "play", 0);
        updateLastOperateTime();
        return 0;
    }

    protected void handleBomb(CardStruct cardStruct) {
        if (zhaCount < room.getMultiple() || room.getMultiple() == -1) {
            if (cardStruct.getType() == CardStruct.type_炸) {
                List<Integer> cards = cardStruct.getCards();
                zhaCount += 1;//记录炸的数量
                multiple *= 2;//记录倍数
            } else if (cardStruct.getType() == CardStruct.type_火箭) {
                zhaCount += 1;//记录炸的数量
                multiple *= 2;//记录倍数
            }
        }
    }


    public int pass(long userId) {
        playTurn = nextTurnId(userId);
        Map<String, Long> rs = new HashMap<>();
        rs.put("userId", userId);
        rs.put("nextUserId", playTurn);

        MsgSender.sendMsg2Player("gameService", "passResponse", rs, this.users);
        MsgSender.sendMsg2Player("gameService", "pass", 0, userId);

        //回放
        replay.getOperate().add(Operate.getOperate_PLAY(userId, null, true));
        updateLastOperateTime();
        return 0;
    }

    public int sayHello() {
        return 0;
    }

    public int test(long userId, String cards) {
        RoomDouDiZhu roomDouDiZhu = (RoomDouDiZhu) this.room;
        roomDouDiZhu.testNextCards = new ArrayList<>();
        for (String s : cards.split(",")) {
            roomDouDiZhu.testNextCards.add(Integer.valueOf(s));
        }
        roomDouDiZhu.testUserId = userId;
        MsgSender.sendMsg2Player("gameService", "test", "ok", userId);
        return 0;
    }

    /**
     * 洗牌
     */
    protected void shuffle() {
        for (int i = 1; i <= 54; i++) {
            cards.add(i);
        }
        Collections.shuffle(cards);
    }

    protected void testDeal() {
        RoomDouDiZhu roomDouDiZhu = (RoomDouDiZhu) room;
        long testUserId = roomDouDiZhu.testUserId;

        playerCardInfos.get(testUserId).cards.addAll(roomDouDiZhu.testNextCards);
        //通知发牌
        MsgSender.sendMsg2Player(new ResponseVo("gameService", "deal", roomDouDiZhu.testNextCards), testUserId);
        cards.removeAll(roomDouDiZhu.testNextCards);

        for (PlayerCardInfoDouDiZhu playerCardInfo : playerCardInfos.values()) {
            if (playerCardInfo.userId != testUserId) {

                for (int i = 0; i < this.initCardNum; i++) {
                    playerCardInfo.cards.add(cards.remove(0));
                }
                //通知发牌
                MsgSender.sendMsg2Player(new ResponseVo("gameService", "deal", playerCardInfo.cards), playerCardInfo.userId);
            }
        }



        roomDouDiZhu.testNextCards = null;
        roomDouDiZhu.testUserId = 0;
    }

    /**
     * 发牌
     */
    protected void deal() {
        RoomDouDiZhu roomDouDiZhu = (RoomDouDiZhu) room;
        if (roomDouDiZhu.testNextCards != null) {
           testDeal();
        } else {

            for (PlayerCardInfoDouDiZhu playerCardInfo : playerCardInfos.values()) {
                for (int i = 0; i < this.initCardNum; i++) {
                    playerCardInfo.cards.add(cards.remove(0));
                }
                //通知发牌
                MsgSender.sendMsg2Player(new ResponseVo("gameService", "deal", playerCardInfo.cards), playerCardInfo.userId);
            }

            //底牌
        }
        tableCards.addAll(cards);

    }


    /**
     * 选叫地主
     *
     * @param lastJiaoUser
     */
    protected void chooseDizhu(long lastJiaoUser) {
        step = STEP_JIAO_DIZHU;
        long canJiao = 0;
        //随机叫地主
        if (lastJiaoUser == 0) {
            int index = rand.nextInt(3);
            canJiao = users.get(index);
        } else {
            canJiao = lastJiaoUser;
        }
        canJiaoUser = canJiao;

        //
        noticeCanJiao(canJiaoUser);


        //下次叫的人
        room.setBankerId(nextTurnId(canJiaoUser));

    }

    /**
     * 叫地主的人重置为这把叫地主的人(再叫一次)
     */
    protected void resetJiaodizhuUser() {
        long user = nextTurnId(nextTurnId(room.getBankerId()));
        room.setBankerId(user);
    }

    /**
     * 流局处理
     */
    protected void handleLiuju() {
        sendResult(true, false);
        room.clearReadyStatus(false);
        sendFinalResult();
        //重置叫地主的人
        resetJiaodizhuUser();
    }

    /**
     * 叫地主
     *
     * @param userId
     * @param isJiao
     * @return
     */
    public int jiaoDizhu(long userId, boolean isJiao, int score) {

        logger.info(userId + "  叫地主 " + isJiao);
        if (canJiaoUser != userId) {
            return ErrorCode.CAN_NOT_JIAO_TURN;
        }
        if (isJiao && score <= tableScore) {
            return ErrorCode.CAN_NOT_JIAO_SCORE;
        }
        //叫地主列表
        chooseJiaoSet.add(userId);

        //不叫 下个人能叫
        if (!isJiao) {
            bujiaoSet.add(userId);
            if (chooseJiaoSet.size() >= users.size()) {
                //曾经有人叫过
                if (dizhu != -1) {
                    //推送选定地主
                    qiangStepStart();
                } else {
                    handleLiuju();
                }
            } else {
                long nextJiao = nextTurnId(userId);
                canJiaoUser = nextJiao;
                noticeCanJiao(nextJiao);
            }
        } else {//叫了 开始抢
            jiaoUser = userId;
            dizhu = userId;
            tableScore = score;
            //第三个人叫的 直接开始游戏
            RoomDouDiZhu r = (RoomDouDiZhu) this.room;
            //都叫过或已经叫到最大分(默认三分,毛三有选项)
            if (chooseJiaoSet.size() >= users.size() || score == r.jiaoScoreMax) {
                //推送选定地主
                qiangStepStart();
            } else {
                long nextJiao = nextTurnId(userId);
                canJiaoUser = nextJiao;
                noticeCanJiao(nextJiao);
            }

        }

        Map<String, Object> rs = new HashMap<>();
        rs.put("userId", userId);
        rs.put("isJiao", isJiao);
        rs.put("score", score);
        MsgSender.sendMsg2Player("gameService", "jiaoResponse", rs, users);

        MsgSender.sendMsg2Player("gameService", "jiaoDizhu", 0, userId);

        updateLastOperateTime();


        //回放
        replay.getOperate().add(Operate.getOperate_JDZ(userId, score, !isJiao));

        return 0;
    }


    protected void qiangStepStart() {
        //选定地主
        chooseDizhu();
        step = STEP_QIANG_DIZHU;
        long nextId = nextTurnId(dizhu);
        this.canQiangUser = nextId;
        noticeCanQiang(nextId);
    }

    protected void compute(boolean isDizhuWin) {

        double subScore = 0;
        int s = isDizhuWin ? -1 : 1;
        multiple *= tableScore;
        //地主
        PlayerCardInfoDouDiZhu playerCardInfoDizhu = playerCardInfos.get(dizhu);
        if (playerCardInfoDizhu.isQiang()) {
            multiple *= 2;
        }
        for (PlayerCardInfoDouDiZhu playerCardInfo : playerCardInfos.values()) {
            //不是地主 扣分
            if (dizhu != playerCardInfo.getUserId()) {
                double score = multiple * s;
                if (playerCardInfo.isQiang()) {
                    score *= 2;
                }
                subScore += score;
                playerCardInfo.setScore(score);
                room.addUserSocre(playerCardInfo.getUserId(), score);
            }
            //加入上把出的牌
            putCard2LastGameCards(playerCardInfo.cards);
        }

        playerCardInfoDizhu.setScore(-subScore);
        room.addUserSocre(dizhu, -subScore);

    }

    protected void sendResult(boolean isReopen, boolean isDizhuWin) {
        GameResultDouDizhu gameResultDouDizhu = new GameResultDouDizhu();
        gameResultDouDizhu.setMultiple(multiple);
        gameResultDouDizhu.setSpring(isSpring);
        gameResultDouDizhu.setDizhuWin(isDizhuWin);
        gameResultDouDizhu.setReopen(isReopen);
        gameResultDouDizhu.setTableCards(tableCards);
        for (PlayerCardInfoDouDiZhu playerCardInfo : playerCardInfos.values()) {
            gameResultDouDizhu.getPlayerCardInfos().add(playerCardInfo.toVo());

        }
        gameResultDouDizhu.getUserScores().putAll(this.room.getUserScores());
        MsgSender.sendMsg2Player("gameService", "gameResult", gameResultDouDizhu, users);


        replay.setResult(gameResultDouDizhu);
    }

    protected void sendFinalResult() {
        //所有牌局都结束
        if (room.getCurGameNumber() > room.getGameNumber()) {
            List<UserOfResult> userOfResultList = this.room.getUserOfResult();
            // 存储返回
            GameOfResult gameOfResult = new GameOfResult();
            gameOfResult.setUserList(userOfResultList);
            MsgSender.sendMsg2Player("gameService", "gameFinalResult", gameOfResult, users);

            RoomManager.removeRoom(room.getRoomId());

            //战绩
            this.room.genRoomRecord();

        }
    }

    protected void genRecord() {
        long id = IdWorker.getDefaultInstance().nextId();
        genRecord(playerCardInfos.values().stream().collect
                (Collectors.toMap(PlayerCardInfoDouDiZhu::getUserId, PlayerCardInfoDouDiZhu::getScore)), room, id);


        //回放
        replay.setId(id);
        replay.setCount(playerCardInfos.size());
        replay.setRoom_uuid(this.room.getUuid());

        replay.setRoomInfo(this.room.toVo(0));

        KafkaMsgKey kafkaMsgKey = new KafkaMsgKey().setMsgId(KAFKA_MSG_ID_REPLAY);
        MsgProducer msgProducer = SpringUtil.getBean(MsgProducer.class);
        msgProducer.send(IKafaTopic.CENTER_TOPIC, kafkaMsgKey, replay);

    }


    protected void playStepStart(long dizhu) {
        this.canQiangUser = -1;
        this.canJiaoUser = -1;
        this.dizhu = dizhu;
        this.step = STEP_PLAY;
        this.playTurn = dizhu;
        Map<String, Object> whoPlay = new HashMap<>();
        whoPlay.put("userId", dizhu);
        MsgSender.sendMsg2Player(new ResponseVo("gameService", "gameBegin", whoPlay), users);

    }

    /**
     * 底牌加到地主身上
     */
    protected void dizhuAddTableCards() {
        //把底牌加到地主身上
        PlayerCardInfoDouDiZhu playerCardInfo = playerCardInfos.get(dizhu);
        if (playerCardInfo != null) {
            playerCardInfo.cards.addAll(tableCards);
            //给所有人看
            MsgSender.sendMsg2Player(new ResponseVo("gameService", "showTableCard", tableCards), users);
        }
    }

    /**
     * 开始打牌
     *
     * @param dizhu
     */
    protected void startPlay(long dizhu) {
        playStepStart(dizhu);


        doAfterStart();
    }

    /**
     * 开始游戏后的处理
     */
    protected void doAfterStart() {
        //玩家所有的牌
        for (PlayerCardInfoDouDiZhu playerCardInfoDouDiZhu : playerCardInfos.values()) {
            playerCardInfoDouDiZhu.allCards.addAll(playerCardInfoDouDiZhu.cards);

            //回放 玩家的牌
            List<Integer> cs = new ArrayList<>();
            cs.addAll(playerCardInfoDouDiZhu.allCards);
            replay.getCards().put(playerCardInfoDouDiZhu.getUserId(), cs);
        }
    }

    protected void chooseDizhu() {
        //选定地主
        Map<String, Long> rs = new HashMap<>();
        rs.put("userId", dizhu);
        MsgSender.sendMsg2Player(new ResponseVo("gameService", "chooseDizhu", rs), users);

        //牌加到地主身上
        dizhuAddTableCards();
    }

    /**
     * 抢地主
     *
     * @param userId
     * @param isQiang
     * @return
     */
    public int qiangDizhu(long userId, boolean isQiang) {
        logger.info(userId + "  抢地主 " + isQiang);

        if (userId != canQiangUser) {
            return ErrorCode.CAN_NOT_QIANG_TURN;
        }
        this.chooseQiangSet.add(userId);
        if (!isQiang) {
            this.buqiangSet.add(userId);
        }

        PlayerCardInfoDouDiZhu playerCardInfo = playerCardInfos.get(userId);
        playerCardInfo.setQiang(isQiang);

        //回执
        Map<String, Object> rs = new HashMap<>();
        rs.put("userId", userId);
        rs.put("isQiang", isQiang);
        MsgSender.sendMsg2Player("gameService", "qiangResponse", rs, users);
        MsgSender.sendMsg2Player("gameService", "qiangDizhu", 0, userId);

        //两个农民都没抢
        boolean allNoQiang = buqiangSet.size() == 2 && !isQiang;
        //开始游戏
        if (chooseQiangSet.size() == 3 || allNoQiang) {
            startPlay(dizhu);
        } else {
            canQiangUser = nextTurnId(userId);
            noticeCanQiang(canQiangUser);
        }


        updateLastOperateTime();
        //回放
        replay.getOperate().add(Operate.getOperate_QDZ(userId, !isQiang));
        return 0;
    }


    /**
     * 通知可以叫地主
     *
     * @param userId
     */
    protected void noticeCanJiao(long userId) {
        Map<String, Long> result = new HashMap<>();
        result.put("userId", userId);
        ResponseVo vo = new ResponseVo("gameService", "canjiao", result);
        MsgSender.sendMsg2Player(vo, users);
    }

    /**
     * 通知可以抢地主
     *
     * @param userId
     */
    protected void noticeCanQiang(long userId) {
        Map<String, Long> result = new HashMap<>();
        result.put("userId", userId);
        ResponseVo vo = new ResponseVo("gameService", "canqiang", result);
        MsgSender.sendMsg2Player(vo, users);
    }


    @Override
    public IfaceGameVo toVo(long userId) {
        GameDoudizhuVo vo = new GameDoudizhuVo();
        //设置地主
        vo.dizhu = this.getDizhu();
        vo.step = this.getStep();
        vo.canJiaoUser = this.getCanJiaoUser();
        vo.canQiangUser = this.getCanQiangUser();
        vo.jiaoUser = this.getJiaoUser();
        vo.qiangUser = this.getQiangUser();
        vo.lastCardStruct = this.getLastCardStruct();
        //该出牌的玩家
        vo.playTurn = this.getPlayTurn();
        vo.curMultiple = this.getMultiple();
        vo.tableScore = this.getTableScore();
        vo.getAutoStatus().putAll(this.autoStatus);
        vo.getAutoTimes().putAll(this.autoTimes);
        if (userId == this.getDizhu() || !(this instanceof GameDouDiZhuLinFen)) {//玩家是地主 并且是临汾斗地主
            vo.tableCards.addAll(this.getTableCards());
        }


        //玩家牌信息
        for (PlayerCardInfoDouDiZhu playerCardInfo : this.getPlayerCardInfos().values()) {
            vo.playerCardInfos.put(playerCardInfo.userId, playerCardInfo.toVo(userId));
        }
        return vo;
    }

    public List<Integer> getCards() {
        return cards;
    }

    public GameDouDiZhu setCards(List<Integer> cards) {
        this.cards = cards;
        return this;
    }

    public List<Integer> getDisCards() {
        return disCards;
    }

    public GameDouDiZhu setDisCards(List<Integer> disCards) {
        this.disCards = disCards;
        return this;
    }

    public List<Integer> getTableCards() {
        return tableCards;
    }

    public GameDouDiZhu setTableCards(List<Integer> tableCards) {
        this.tableCards = tableCards;
        return this;
    }

    public Map<Long, PlayerCardInfoDouDiZhu> getPlayerCardInfos() {
        return playerCardInfos;
    }

    public GameDouDiZhu setPlayerCardInfos(Map<Long, PlayerCardInfoDouDiZhu> playerCardInfos) {
        this.playerCardInfos = playerCardInfos;
        return this;
    }


    public Random getRand() {
        return rand;
    }

    public GameDouDiZhu setRand(Random rand) {
        this.rand = rand;
        return this;
    }

    public long getDizhu() {
        return dizhu;
    }

    public GameDouDiZhu setDizhu(long dizhu) {
        this.dizhu = dizhu;
        return this;
    }

    public Set<Long> getChooseJiaoSet() {
        return chooseJiaoSet;
    }

    public GameDouDiZhu setChooseJiaoSet(Set<Long> chooseJiaoSet) {
        this.chooseJiaoSet = chooseJiaoSet;
        return this;
    }

    public Set<Long> getChooseQiangSet() {
        return chooseQiangSet;
    }

    public GameDouDiZhu setChooseQiangSet(Set<Long> chooseQiangSet) {
        this.chooseQiangSet = chooseQiangSet;
        return this;
    }

    public Set<Long> getBujiaoSet() {
        return bujiaoSet;
    }

    public GameDouDiZhu setBujiaoSet(Set<Long> bujiaoSet) {
        this.bujiaoSet = bujiaoSet;
        return this;
    }


    public int getLasttype() {
        return lasttype;
    }

    public GameDouDiZhu setLasttype(int lasttype) {
        this.lasttype = lasttype;
        return this;
    }

    public long getCanJiaoUser() {
        return canJiaoUser;
    }

    public GameDouDiZhu setCanJiaoUser(long canJiaoUser) {
        this.canJiaoUser = canJiaoUser;
        return this;
    }

    public long getCanQiangUser() {
        return canQiangUser;
    }

    public GameDouDiZhu setCanQiangUser(long canQiangUser) {
        this.canQiangUser = canQiangUser;
        return this;
    }

    public long getJiaoUser() {
        return jiaoUser;
    }

    public GameDouDiZhu setJiaoUser(long jiaoUser) {
        this.jiaoUser = jiaoUser;
        return this;
    }

    public long getQiangUser() {
        return qiangUser;
    }

    public GameDouDiZhu setQiangUser(long qiangUser) {
        this.qiangUser = qiangUser;
        return this;
    }

    public long getPlayTurn() {
        return playTurn;
    }

    public GameDouDiZhu setPlayTurn(long playTurn) {
        this.playTurn = playTurn;
        return this;
    }

    public CardStruct getLastCardStruct() {
        return lastCardStruct;
    }

    public GameDouDiZhu setLastCardStruct(CardStruct lastCardStruct) {
        this.lastCardStruct = lastCardStruct;
        return this;
    }

    public int getStep() {
        return step;
    }

    public GameDouDiZhu setStep(int step) {
        this.step = step;
        return this;
    }

    public int getMultiple() {
        return multiple;
    }

    public GameDouDiZhu setMultiple(int multiple) {
        this.multiple = multiple;
        return this;
    }

    public int getTableScore() {
        return tableScore;
    }

    public GameDouDiZhu setTableScore(int tableScore) {
        this.tableScore = tableScore;
        return this;
    }

    public RoomDouDiZhu getRoom() {
        return room;
    }

    public GameDouDiZhu setRoom(RoomDouDiZhu room) {
        this.room = room;
        return this;
    }
}
