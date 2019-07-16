package com.code.server.game.mahjong.logic;


import com.code.server.constant.kafka.IKafaTopic;
import com.code.server.constant.kafka.KafkaMsgKey;
import com.code.server.constant.response.GameOfResult;
import com.code.server.constant.response.ResponseVo;
import com.code.server.constant.response.UserOfResult;
import com.code.server.game.mahjong.response.*;
import com.code.server.game.room.Game;
import com.code.server.game.room.kafka.MsgSender;
import com.code.server.game.room.service.RoomManager;
import com.code.server.kafka.MsgProducer;
import com.code.server.util.IdWorker;
import com.code.server.util.SpringUtil;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by T420 on 2016/11/30.
 */

public class GameInfo extends Game {

    protected static final Logger logger = Logger.getLogger("game");
    protected static final boolean isTest = true;

    protected static final String GSJ_NOFENG = "303";//303，3表示拐三角玩法，03表示无风
    protected static final String DPH_NOFENG = "6";//点炮胡，表示无风

    protected int gameId;
    protected int playerSize;//玩家个数
    protected int cardSize;//牌数量

    protected int gameModel;//游戏类型
    protected List<String> remainCards = new ArrayList<>();//剩下的牌User
    protected String disCard;//出的牌
    protected long turnId;//轮到谁出牌
    protected long lastPlayUserId = -1;//上个出牌的人
    protected long lastMoPaiUserId = -1;//上个摸牌的人
    protected String catchCard;//摸得牌

    protected long lastOperateUserId;//上个操作的人
    protected Map<Long, PlayerCardsInfoMj> playerCardsInfos = new HashMap<>();//玩家手上牌的信息
    protected long firstTurn = 0;//第一个出牌的人
    protected List<WaitDetail> waitingforList = new ArrayList<>();//需要等待玩家的列表

    protected boolean isAlreadyHu;//是否已经胡过
    protected int baoType = -1;
    protected String baoCard = null;

    protected boolean isHasJieGangHu = true;

    protected String jieGangHuCard = null;
    protected long beJieGangUser = -1;

    protected String jieXuanfengCard = null;
    protected long beJieXuanfengUser = -1;
    protected long jieXuanfengCardType = -1;

    protected transient RoomInfo room;

    private boolean isHasWaitCache;
    protected int changeBaoSize = 0;

    protected List<Map<Long, Integer>> userOperateList = new ArrayList<>();

    protected List<Integer> hun = new ArrayList<>();
    protected List<String> hunRemoveCards = new ArrayList<>();

    protected ReplayMj replay = new ReplayMj();

    protected List<String> chanCards = new ArrayList<>();//铲的牌

    protected boolean afterTingShowCard = false;//听牌后是否扣牌

    protected boolean yiPaoDuoXiangAppear = false;

    public boolean autoPlay = false;

    protected boolean isAlreadyComputeGang = false;

    private long lastCatchCardUser = 0;//最后抓牌的玩家 繁峙下雨

    private boolean isTurnZeroAfterHuangZhuang = false;

    public int rand = 0;

    protected int state;

    protected boolean nowYipaoduoxiang = false;

    protected String yipaoduoxiangCard = null;

//    private Set<Long> noCanHuList = new HashSet<>();//本轮不能胡的人

    /**
     * 初始化方法
     *
     * @param firstTurn
     * @param users
     */
    public void init(int gameId, long firstTurn, List<Long> users, RoomInfo room) {
        this.gameId = gameId;

        this.firstTurn = firstTurn;
        this.turnId = firstTurn;
        remainCards.addAll(CardTypeUtil.ALL_CARD);
        this.users.addAll(users);
        this.room = room;
        this.cardSize = 13;
        this.playerSize = room.getPersonNumber();
        //不带风
        if ("3".equals(room.getMode()) || "4".equals(room.getMode()) || "13".equals(room.getMode()) || "14".equals(room.getMode()) || "108".equals(room.getModeTotal())) {
            remainCards.removeAll(CardTypeUtil.FENG_CARD);
            remainCards.removeAll(CardTypeUtil.ZI_CARD);
        } else if (GSJ_NOFENG.equals(room.getMode())) {
            remainCards.removeAll(CardTypeUtil.FENG_CARD);
            remainCards.removeAll(CardTypeUtil.ZI_CARD);
            int point = 16;
            while (point > 0) {
                remainCards.remove(0);
                point--;
            }
        } else if (DPH_NOFENG.equals(room.getModeTotal())) {
            remainCards.removeAll(CardTypeUtil.FENG_CARD);
            remainCards.removeAll(CardTypeUtil.ZI_CARD);

        }

        if (this.room.curGameNumber == 1){
            this.room.getBankerMap().put(1, firstTurn);
        }

        fapai();
    }

    /**
     * 发牌
     */
    public void fapai() {
        //打乱顺序
        Collections.shuffle(remainCards);
        for (int i = 0; i < playerSize; i++) {
            PlayerCardsInfoMj playerCardsInfo = PlayerCardsInfoFactory.getInstance(room);
            playerCardsInfo.setGameInfo(this);
            long userId = users.get(i);
            //设置id
            playerCardsInfo.setUserId(userId);
            List<String> playerCards = new ArrayList<>();
            //发牌
            for (int j = 0; j < cardSize; j++) {
                playerCards.add(remainCards.remove(0));
            }
            //初始化
            playerCardsInfo.init(playerCards);
            //放进map
            playerCardsInfos.put(userId, playerCardsInfo);

            //发牌状态通知
            HandCardsResp resp = new HandCardsResp();
            resp.setCards(playerCards);
            ResponseVo vo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_GET_HAND_CARDS, resp);

            MsgSender.sendMsg2Player(vo, userId);


        }
        doAfterFapai();
        //回放的牌信息
        for (PlayerCardsInfoMj playerCardsInfoMj : playerCardsInfos.values()) {
            List<String> cs = new ArrayList<>();
            cs.addAll(playerCardsInfoMj.getCards());
            replay.getCards().put(playerCardsInfoMj.getUserId(), cs);
        }
        //第一个人抓牌
        mopai(firstTurn, "发牌");

    }

    protected void doAfterFapai() {

    }


    public void initHun(){
        Random rand = new Random();
        int hunIndex = 0;
        hunIndex = rand.nextInt(34);
        this.hun.add(hunIndex);
    //通知混
        MsgSender.sendMsg2Player("gameService", "noticeHun", this.hun, users);
        replay.getHun().addAll(this.hun);
    }

    /**
     * 荒庄的处理
     *
     * @param userId
     */
    protected void handleHuangzhuang(long userId) {

        switch (this.room.modeTotal) {
            case "4":
                break;
            case "6":
                break;
            default:
                turnResultToZeroOnHuangZhuang();
        }
        sendResult(false, userId, null);
        noticeDissolutionResult();
        //通知所有玩家结束
        room.clearReadyStatus(true);
        //庄家换下个人
        if (room instanceof RoomInfo) {
            RoomInfo roomInfo = (RoomInfo) room;
            if (roomInfo.isChangeBankerAfterHuangZhuang()) {
                room.setBankerId(nextTurnId(room.getBankerId()));
            }

        }
//        if(HuUtil.getUserAndScore(this.room.getUserScores()).contains("否")){
//        	logger.info("<<<牌局得分>>>"+HuUtil.getUserAndScore(this.room.getUserScores()));
//        }
    }

    /**
     * 跑分
     * @param userId
     * @return
     */
    public int paofen(long userId, int status) {
        return 0;
    }

    /**
     * 定缺
     * @param userId
     * @param groupType
     * @return
     */
    public int dingque(long userId, int groupType){
        return 0;
    }

    public int huanpai(long userId, List<String> cards) {

        return 0;
    }

    public int huanpaiCancel(long userId) {
        PlayerCardsInfoMj playerCardsInfoMj = playerCardsInfos.get(userId);
        playerCardsInfoMj.getChangeCards().clear();
        return 0;
    }

    protected void computeAllGang() {
        if(this.isAlreadyComputeGang) return;
        this.isAlreadyComputeGang = true;
        for (PlayerCardsInfoMj player : this.getPlayerCardsInfos().values()) {
            player.computeALLGang();
        }
    }

    /**
     * 摸一张牌
     *
     * @param playerCardsInfo
     * @return
     */
    protected String getMoPaiCard(PlayerCardsInfoMj playerCardsInfo) {
        //拿出一张
        String card = null;
        //有换牌需求
        if (isTest && playerCardsInfo.nextNeedCard != -1) {
            String needCard = getCardByTypeFromRemainCards(playerCardsInfo.nextNeedCard);
            playerCardsInfo.nextNeedCard = -1;
            if (needCard != null) {
                card = needCard;
                remainCards.remove(needCard);
            } else {
                card = remainCards.remove(0);
            }
        } else {
            card = remainCards.remove(0);
        }
        return card;
    }


    /**
     * 是否荒庄
     *
     * @param playerCardsInfo
     * @return
     */
    protected boolean isHuangzhuang(PlayerCardsInfoMj playerCardsInfo) {
        return playerCardsInfo.isHuangzhuang(this);
    }

    /**
     * 摸牌
     *
     * @param userId
     */
    protected void mopai(long userId, String... wz) {
        PlayerCardsInfoMj playerCardsInfo = playerCardsInfos.get(userId);
        if (isHasGuoHu()) {
            playerCardsInfo.setGuoHu(false);
        }
        if (isHasGuoPeng()) {
            playerCardsInfo.guoPengSet.clear();
        }
        if (isHuangzhuang(playerCardsInfo)) {
            handleHuangzhuang(userId);
            return;
        }


        if (playerCardsInfo.isMoreOneCard()) {
            if (wz.length > 0) {
                logger.info("======1操作后的摸牌 : " + wz[0]);
            }
            logger.info("userId : " + userId + "　===1 more one card 抓牌时多一张牌");
            logger.info("操作列表: " + playerCardsInfo.operateList.toString());
            logger.info("所有操作: " + userOperateList.toString());

        }

        //拿出一张
        String card = getMoPaiCard(playerCardsInfo);
        //有换牌需求

        playerCardsInfo.mopai(card);
        //
        turnId = userId;
        this.lastMoPaiUserId = userId;
        lastOperateUserId = userId;
        this.catchCard = card;

        // 把摸到的牌 推给摸牌的玩家
        int remainSize = remainCards.size();
        for (long user : users) {
            GetCardResp getCardResp = new GetCardResp();
            getCardResp.setRemainNum(remainSize);
            getCardResp.setUserId(userId);
            if (user == userId) {
                getCardResp.setCard(card);
            }
            ResponseVo responseVo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_GET_CARD, getCardResp);
            MsgSender.sendMsg2Player(responseVo, user);

            //能做的操作全置成不能
            PlayerCardsInfoMj other = playerCardsInfos.get(user);

            resetCanBeOperate(other);
        }


        boolean isCanGang = playerCardsInfo.isHasGang();
        boolean isCanTing = playerCardsInfo.isCanTing(playerCardsInfo.cards);//多一张
        boolean isCanHu = playerCardsInfo.isCanHu_zimo(catchCard);

        //能做的操作
        playerCardsInfo.setCanBeGang(isCanGang);
        playerCardsInfo.setCanBePeng(false);
        playerCardsInfo.setCanBeHu(isCanHu);
        playerCardsInfo.setCanBeTing(isCanTing);

        OperateResp resp = new OperateResp();
        resp.setIsCanGang(isCanGang);
        resp.setIsCanHu(isCanHu);
        resp.setIsCanTing(isCanTing);

        //回放 抓牌
        OperateReqResp operateReqResp = new OperateReqResp();
        operateReqResp.setCard(card);
        operateReqResp.setUserId(userId);
        operateReqResp.setOperateType(OperateReqResp.type_mopai);
        replay.getOperate().add(operateReqResp);

        //可能的操作
        ResponseVo OperateResponseVo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_OPERATE, resp);
        MsgSender.sendMsg2Player(OperateResponseVo, userId);

    }

    public boolean isHasGuoHu() {
        String gameType = this.room.getGameType();
        String modeTotal = this.room.getModeTotal();
        return ("LQ".equals(gameType) && "2".equals(modeTotal)) ||
                 ("LQ2".equals(gameType) && "2".equals(modeTotal)) ||
                ("HL".equals(gameType) && "2".equals(modeTotal)) ||
                ("THREEA".equals(gameType) && "2".equals(modeTotal)) ||
                ("THREEA2".equals(gameType) && "2".equals(modeTotal)) ||
                ("THREEA3".equals(gameType) && "2".equals(modeTotal)) ||
                "SS".equals(gameType) ||
                "SS3".equals(gameType) ||
                "HS".equals(gameType)||
                "HUASHUI".equals(gameType)||
                "KXHY".equals(gameType)||
                "KXZHZ".equals(gameType)||
                "XYKD".equals(gameType)||
                "HONGZHONG".equals(gameType)||
                "HONGZHONG2".equals(gameType)||
                "HONGZHONG3".equals(gameType)||
                "HONGZHONGSS".equals(gameType)||
                "HONGZHONGSS3".equals(gameType)||
                "FANSHI".equals(gameType)||
                "KXKD".equals(gameType);
    }

    public boolean isHasGuoPeng(){
        String gameType = this.room.getGameType();
        String modeTotal = this.room.getModeTotal();
        return "HS".equals(gameType)||
                "HUASHUI".equals(gameType);

    }

    /**
     * 出牌
     *
     * @param userId
     * @param card
     */
    public int chupai(long userId, String card) {
        //出牌的玩家
        PlayerCardsInfoMj chupaiPlayerCardsInfo = playerCardsInfos.get(userId);
        if (this.turnId != userId) {
            return ErrorCode.CAN_NOT_PLAYCARD;
        }
        if (!chupaiPlayerCardsInfo.checkPlayCard(card)) {
            return ErrorCode.CAN_NOT_PLAYCARD;
        }
        this.lastPlayUserId = userId;//上个出牌的人
        lastOperateUserId = userId;//上个操作的人
        //出的牌
        this.disCard = card;
        chupaiPlayerCardsInfo.chupai(card);


        //通知其他玩家出牌信息
        PlayCardResp playCardResp = new PlayCardResp();
        playCardResp.setUserId(userId);
        playCardResp.setCard(this.disCard);
        playCardResp.setAuto(this.autoPlay);
        ResponseVo vo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_PLAY_CARD, playCardResp);
        MsgSender.sendMsg2Player(vo, users);
        this.autoPlay = false;

        //回放 出牌
        OperateReqResp operateReqResp = new OperateReqResp();
        operateReqResp.setCard(card);
        operateReqResp.setUserId(userId);
        operateReqResp.setOperateType(OperateReqResp.type_play);
        replay.getOperate().add(operateReqResp);

        //其他人能做的操作
        for (Map.Entry<Long, PlayerCardsInfoMj> entry : playerCardsInfos.entrySet()) {
            OperateResp operateResp = new OperateResp();

            //其他玩家的处理 碰杠等 如果有加入等待列表(要等待这些玩家"过")
            if (userId != entry.getKey()) {
                //通知其他玩家出了什么牌 自己能有什么操作
                PlayerCardsInfoMj playerCardsInfo = entry.getValue();
                boolean isCanGang = playerCardsInfo.isCanGangAddThisCard(card);
                boolean isCanPeng = playerCardsInfo.isCanPengAddThisCard(card);
                if (isHasGuoPeng()) {
                    isCanPeng = isCanPeng && !playerCardsInfo.isGuoPeng(card);
                }
                boolean isCanHu;
                if (isHasGuoHu() && playerCardsInfo.isGuoHu()) {
                    isCanHu = false;
                } else {
                    isCanHu = playerCardsInfo.isCanHu_dianpao(card);
                }

                boolean isCanChi = playerCardsInfo.isHasChi(card);
                boolean isCanChiTing = playerCardsInfo.isCanChiTing(card);
                boolean isCanPengTing = playerCardsInfo.isCanPengTing(card);
                //设置返回结果
                operateResp.setCanBeOperate(isCanChi, isCanPeng, isCanGang, false, isCanHu, isCanChiTing, isCanPengTing);

                //设置自己能做的操作
                playerCardsInfo.setCanBeOperate(isCanChi, isCanPeng, isCanGang, false, isCanHu, isCanChiTing, isCanPengTing);

                boolean isWait = isCanGang || isCanPeng || isCanHu || isCanChi || isCanChiTing || isCanPengTing;
                if (isWait) {
                    this.waitingforList.add(new WaitDetail(entry.getKey(), isCanHu, isCanGang, isCanPeng, isCanChi, isCanChiTing, isCanPengTing));
                }
            }

            //可能的操作
            ResponseVo OperateVo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_OPERATE, operateResp);
            MsgSender.sendMsg2Player(OperateVo, entry.getKey());
        }

        resetCanBeOperate(chupaiPlayerCardsInfo);


        //如果等待列表为空 就轮到下个人摸牌
        if (this.waitingforList.size() == 0) {
            long nextId = nextTurnId(turnId);
            mopai(nextId, "userId : " + userId + " 出牌");
        } else {
            //todo 一炮多响
            if (this.room.isYipaoduoxiang && waitingforList.stream().filter(waitDetail -> waitDetail.isHu).count() >= 2) {
                handleYiPaoDuoXiang();
            } else {
                //比较
                compare(waitingforList);
            }
        }
        return 0;

    }

    protected void handleYiPaoDuoXiang() {

        List<Long> yipaoduoxiang = new ArrayList<>();

        //删除弃牌
        deleteDisCard(lastPlayUserId, disCard);
        this.waitingforList.forEach(waitDetail -> {
            if (waitDetail.isHu) {
                long uid = waitDetail.myUserId;
                yipaoduoxiang.add(uid);
                PlayerCardsInfoMj playerCardsInfoMj = playerCardsInfos.get(uid);
                playerCardsInfoMj.hu_dianpao(room, this, lastPlayUserId, disCard);
            }
        });


        this.room.setBankerId(yipaoduoxiang.get(0));

        //回放
        OperateReqResp operateReqResp = new OperateReqResp();
        operateReqResp.setYipaoduoxiangUser(yipaoduoxiang);
        operateReqResp.setOperateType(OperateReqResp.type_yipaoduoxiang);
        operateReqResp.setIsMing(true);
        replay.getOperate().add(operateReqResp);

//        handleHu(playerCardsInfo);

        isAlreadyHu = true;
        sendResult(true, -1L, yipaoduoxiang);
        noticeDissolutionResult();
        room.clearReadyStatus(true);
    }


    /**
     * 删除弃牌
     *
     * @param userId
     * @param disCard
     */
    protected void deleteDisCard(long userId, String disCard) {
        PlayerCardsInfoMj playerCardsInfo = playerCardsInfos.get(userId);
        if (playerCardsInfo != null) {
            playerCardsInfo.getDisCards().remove(disCard);
        }
    }

    /**
     * 比较顺序
     * 胡>杠>碰
     * 分数相同按座位顺序排
     *
     * @param lists
     */
    protected void compare(List<WaitDetail> lists) {
        Collections.sort(lists, new Comparator<WaitDetail>() {
            @Override
            public int compare(WaitDetail o1, WaitDetail o2) {
                if (o1.getPoint() > o2.getPoint()) {
                    return -1;
                } else if (o1.getPoint() < o2.getPoint()) {
                    return 1;
                } else {
                    List<Long> turnList = next3TurnId();
                    int index1 = turnList.indexOf(o1.myUserId);
                    int index2 = turnList.indexOf(o2.myUserId);
                    if (index1 < index2) {
                        return -1;
                    } else {
                        return 1;
                    }
                }
            }
        });
    }


    /**
     * 得到下面三个出牌人的id 按顺序
     *
     * @return
     */
    private List<Long> next3TurnId() {
        List<Long> result = new ArrayList<>();

        long curId = turnId;
        for (int i = 0; i < users.size() - 1; i++) {
            curId = nextTurnId(curId);
            result.add(curId);
        }
        return result;
    }

    protected void resetCanBeOperate(PlayerCardsInfoMj playerCardsInfo) {
        playerCardsInfo.setCanBeChi(false);
        playerCardsInfo.setCanBeGang(false);
        playerCardsInfo.setCanBePeng(false);
        playerCardsInfo.setCanBeHu(false);
        playerCardsInfo.setCanBeTing(false);
        playerCardsInfo.setCanBeChiTing(false);
        playerCardsInfo.setCanBePengTing(false);
        playerCardsInfo.setCanBeXuanfeng(false);
        playerCardsInfo.setCanBeBufeng(false);
    }

    protected void resetOtherOperate(long userId) {
        for (PlayerCardsInfoMj playerCardsInfo : playerCardsInfos.values()) {
            if (playerCardsInfo.getUserId() != userId) {
                resetCanBeOperate(playerCardsInfo);
            }
        }
    }

    /**
     * 等待信息详情 排序用
     */
    public static class WaitDetail {
        static final int huPoint = 1 << 6;
        static final int pengTingPoint = 1 << 5;
        static final int chiTingPoint = 1 << 4;
        static final int gangPoint = 1 << 3;
        static final int pengPoint = 1 << 2;
        static final int chiPoint = 1 << 1;

        public long myUserId;
        public boolean isHu;
        boolean isGang;
        boolean isPeng;
        boolean isChi;
        boolean isChiTing;
        boolean isPengTing;

        int operate = -1;
        GameInfo gameInfo;
        String[] params;

        boolean isFire;


        public WaitDetail() {
        }

        public WaitDetail(long myUserId, boolean isHu, boolean isGang, boolean isPeng, boolean isChi, boolean isChiTing, boolean isPengTing) {
            this.myUserId = myUserId;
            this.isHu = isHu;
            this.isGang = isGang;
            this.isPeng = isPeng;
            this.isChi = isChi;
            this.isChiTing = isChiTing;
            this.isPengTing = isPengTing;
        }


        void operate(GameInfo gameInfo, int operate, String... params) {
            //只有第一次操作有效
            if (this.operate == -1) {
                this.operate = operate;
                this.gameInfo = gameInfo;
                this.params = params;
            }
        }

        void fire() {
            PlayerCardsInfoMj playerCardsInfo = gameInfo.getPlayerCardsInfos().get(myUserId);
            this.isFire = true;
            switch (operate) {
                case huPoint:
                    gameInfo.doHu(playerCardsInfo, myUserId);
                    break;
                case gangPoint:
                    gameInfo.doGang(playerCardsInfo, myUserId);
                    break;
                case pengTingPoint:
                    gameInfo.doPengTing(playerCardsInfo, myUserId);
                    break;
                case chiTingPoint:
                    gameInfo.doChiTing(playerCardsInfo, myUserId, params[0], params[1]);
                    break;
                case pengPoint:
                    gameInfo.doPeng(playerCardsInfo, myUserId);
                    break;
                case chiPoint:
                    gameInfo.doChi(playerCardsInfo, myUserId, params[0], params[1]);
                    break;
            }



        }

        int getPoint() {
            if (isHu) {
                return huPoint;
            }
            if (isPengTing) {
                return pengTingPoint;
            }
            if (isChiTing) {
                return chiTingPoint;
            }
            if (isGang) {
                return gangPoint;
            }
            if (isPeng) {
                return pengPoint;
            }
            if (isChi) {
                return chiPoint;
            }
            return 0;
        }

        @Override
        public String toString() {
            return "isHuCommon = " + isHu + "  isGang = " + isGang + "  isPeng = " + isPeng + "  userId = " + myUserId;
        }
    }


    /**
     * 过
     *
     * @param userId
     */
    public int guo(long userId) {


        //过胡逻辑
        if (playerCardsInfos.get(userId).isCanHu_dianpao(disCard)) {
            playerCardsInfos.get(userId).setGuoHu(true);
        }
        if (isHasGuoPeng() && playerCardsInfos.get(userId).isCanPengAddThisCard(disCard)) {
            playerCardsInfos.get(userId).addGuoPeng(disCard);
        }

        if (waitingforList.size() > 0) {

            List<WaitDetail> removeList = new ArrayList<>();
            for (WaitDetail waitDetail : waitingforList) {
                if (waitDetail.myUserId == userId) {
                    removeList.add(waitDetail);
                }
            }
            waitingforList.removeAll(removeList);
            resetCanBeOperate(playerCardsInfos.get(userId));

            if (this.waitingforList.size() == 0) {
                //有截杠胡 都点了过 要杠出来
                if (jieGangHuCard != null) {
                    PlayerCardsInfoMj playerCardsInfo = playerCardsInfos.get(beJieGangUser);
                    if (playerCardsInfo != null) {
                        doGang_hand_after(playerCardsInfo, true, -1, jieGangHuCard);
                    }
                    beJieGangUser = -1;
                    jieGangHuCard = null;
                } else {

                    long nextId = nextTurnId(turnId);
                    //下个人摸牌
                    mopai(nextId, "过后抓牌");
                }
            }

        }
        return 0;
    }

    /**
     * 杠
     *
     * @param userId
     */
    public int gang(long userId, String card) {
        PlayerCardsInfoMj playerCardsInfo = playerCardsInfos.get(userId);
        if (playerCardsInfo == null) {
            return ErrorCode.USER_ERROR;
        }
        OperateReqResp operateReqResp = new OperateReqResp();
        operateReqResp.setOperateType(OperateReqResp.type_gang);
        ResponseVo vo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_OTHER_OPERATE, operateReqResp);

        if (lastOperateUserId == userId) {//杠手里的牌
            if ("".equals(card)) {
                logger.error("=======================杠牌时 没传数据========");
            }
            if (!playerCardsInfo.isCanGangThisCard(card)) {
                return ErrorCode.CAN_NOT_GANG;
            }
            lastOperateUserId = userId;

            int gangType = CardTypeUtil.getTypeByCard(card);
            boolean isMing = playerCardsInfo.getPengType().containsKey(gangType);

            //通知别人有杠
            //回放
            operateReqResp.setUserId(userId);
            operateReqResp.setCard(card);
            operateReqResp.setIsMing(isMing);
            replay.getOperate().add(operateReqResp);

            MsgSender.sendMsg2Player(vo, users);

            //截杠胡
            if (isHasJieGangHu && isMing) {

                for (Map.Entry<Long, PlayerCardsInfoMj> entry : playerCardsInfos.entrySet()) {
                    OperateResp operateResp = new OperateResp();

                    //其他玩家的处理 碰杠等 如果有加入等待列表(要等待这些玩家"过")
                    if (userId != entry.getKey()) {
                        //通知其他玩家出了什么牌 自己能有什么操作
                        PlayerCardsInfoMj playerOther = entry.getValue();
                        boolean isCanHu = playerOther.isCanHu_dianpao(card);
                        //设置返回结果
                        operateResp.setCanBeOperate(false, false, false, false, isCanHu, false, false);
                        //设置自己能做的操作
                        playerOther.setCanBeOperate(false, false, false, false, isCanHu, false, false);

                        if (isCanHu) {
                            this.waitingforList.add(new WaitDetail(entry.getKey(), true, false, false, false, false, false));
                        }
                    }

                    //可能的操作
                    ResponseVo OperateVo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_OPERATE, operateResp);
                    MsgSender.sendMsg2Player(OperateVo, entry.getKey());
                }
            }


            //杠
            playerCardsInfo.gang_hand(room, this, userId, card);
            //有截杠
            if (this.waitingforList.size() > 0) {
                beJieGangUser = userId;
                jieGangHuCard = card;
                //排序
                compare(waitingforList);
            } else {
                doGang_hand_after(playerCardsInfo, isMing, -1, card);
            }


        } else {
            if (disCard == null || !playerCardsInfo.canBeGang) {
                return ErrorCode.CAN_NOT_GANG;
            }
            if (!playerCardsInfo.isCanGangAddThisCard(disCard)) {
                return ErrorCode.CAN_NOT_GANG;
            }


            //从等待列表删除
//            if (waitingforList.size() > 0) {
            WaitDetail waitDetail = waitingforList.get(0);
            if (waitDetail != null && waitDetail.myUserId == userId && waitDetail.isGang) {
                waitingforList.clear();
            } else {
                return ErrorCode.NOT_TURN;
            }


            //回放
            operateReqResp.setUserId(userId);
            operateReqResp.setFromUserId(lastPlayUserId);
            operateReqResp.setCard(disCard);
            operateReqResp.setIsMing(true);
            replay.getOperate().add(operateReqResp);

            //删除弃牌
            deleteDisCard(lastPlayUserId, disCard);


            playerCardsInfo.gang_discard(room, this, lastPlayUserId, disCard);
            operateReqResp.setFromUserId(lastPlayUserId);//谁出的牌

            operateReqResp.setUserId(userId);
            operateReqResp.setCard(disCard);
            operateReqResp.setIsMing(true);
            //通知所有人有杠
            MsgSender.sendMsg2Player(vo, users);

            mopai(userId, "userId : " + userId + " 点杠后抓牌");
            turnId = userId;
            this.disCard = null;
            lastOperateUserId = userId;
        }


        return 0;


    }

    protected void doGang_hand_after(PlayerCardsInfoMj playerCardsInfo, boolean isMing, int userId, String card) {
        playerCardsInfo.gangCompute(room, this, isMing, -1, card);
        mopai(playerCardsInfo.getUserId(), "userId : " + playerCardsInfo.getUserId() + " 自摸杠抓牌");
        turnId = playerCardsInfo.getUserId();
    }

    /**
     * 碰牌
     *
     * @param userId
     * @return
     */
    public int peng(long userId) {
        PlayerCardsInfoMj playerCardsInfo = playerCardsInfos.get(userId);
        if (playerCardsInfo == null) {
            return ErrorCode.USER_ERROR;
        }
        if (this.disCard == null || !playerCardsInfo.canBePeng) {
            return ErrorCode.CAN_NOT_PENG;
        }
        boolean isCan = playerCardsInfo.isCanPengAddThisCard(this.disCard);
        if (isCan) {
            //从等待列表删除
//            if (waitingforList.size() > 0) {
            WaitDetail waitDetail = waitingforList.get(0);
            if (waitDetail != null && waitDetail.myUserId == userId && waitDetail.isPeng) {
                waitingforList.clear();
            } else {
                return ErrorCode.NOT_TURN;
            }
//            }
            playerCardsInfo.peng(disCard, lastPlayUserId);
            lastOperateUserId = userId;

            //删除弃牌
            deleteDisCard(lastPlayUserId, disCard);


            //通知其他玩家

            OperateReqResp operateReqResp = new OperateReqResp();
            operateReqResp.setOperateType(OperateReqResp.type_peng);
            operateReqResp.setCard(disCard);
            operateReqResp.setFromUserId(lastPlayUserId);
            operateReqResp.setUserId(userId);

            //回放
            replay.getOperate().add(operateReqResp);

            //通知其他人
            ResponseVo vo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_OTHER_OPERATE, operateReqResp);
            MsgSender.sendMsg2Player(vo, users);


            //碰完能听,杠,不能胡
            boolean isCanTing = playerCardsInfo.isCanTing(playerCardsInfo.cards);//多一张牌
            boolean isCanGang = playerCardsInfo.isHasGang();
            turnId = userId;
            // 通知客户端 操作
            OperateResp operateResp = new OperateResp();
            operateResp.setIsCanTing(isCanTing);
            operateResp.setIsCanGang(isCanGang);
            ResponseVo operateVo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_OPERATE, operateResp);
            MsgSender.sendMsg2Player(operateVo, userId);
            this.disCard = null;
            //自己将能做的处理
            playerCardsInfo.canBePeng = false;
            playerCardsInfo.canBeTing = isCanTing;
            playerCardsInfo.canBeGang = isCanGang;

        } else {
            return ErrorCode.CAN_NOT_PENG;
        }
        return 0;
    }

    /**
     * 设置庄家
     *
     * @param winnerId
     */
    public void setBanker(long winnerId) {
        if (winnerId == this.getFirstTurn()) {

            room.setBankerId(winnerId);
        } else {
            if (("LQ".equals(this.room.getGameType()) || "LQ2".equals(this.room.getGameType())) && ("11".equals(this.room.getMode()) || "12".equals(this.room.getMode()) || "13".equals(this.room.getMode()) || "14".equals(this.room.getMode()) || "1".equals(this.room.getMode()) || "2".equals(this.room.getMode()) || "3".equals(this.room.getMode()) || "4".equals(this.room.getMode()))) {
                room.setBankerId(winnerId);
            } else {
                long nextId = nextTurnId(this.getFirstTurn());
                room.setBankerId(nextId);
            }
        }
    }

    /**
     * 胡牌
     *
     * @param userId
     * @return
     */
    public int hu(long userId) {

        PlayerCardsInfoMj playerCardsInfo = playerCardsInfos.get(userId);
        if (playerCardsInfo == null) {
            return ErrorCode.USER_ERROR;
        }

        //回放
        OperateReqResp operateReqResp = new OperateReqResp();
        operateReqResp.setUserId(userId);
        operateReqResp.setOperateType(OperateReqResp.type_hu);


        if (lastOperateUserId == userId) {//自摸
            if (!playerCardsInfo.isCanHu_zimo(catchCard)) {
                return ErrorCode.CAN_NOT_HU;
            }

            setBanker(userId);
            playerCardsInfo.hu_zm(room, this, catchCard);
            //回放
            replay.getOperate().add(operateReqResp);
            handleHu(playerCardsInfo);
        } else {
            if (this.disCard == null && jieGangHuCard == null) {
                return ErrorCode.CAN_NOT_HU;
            }

            String card = this.disCard;
            //
            if (jieGangHuCard != null) {
                card = jieGangHuCard;
            }

            if (!playerCardsInfo.isCanHu_dianpao(card)) {
                return ErrorCode.CAN_NOT_HU;
            }
            setBanker(userId);
            //从等待列表删除
//            if (waitingforList.size() > 0) {
            WaitDetail waitDetail = waitingforList.get(0);
            if (waitDetail != null && waitDetail.myUserId == userId && waitDetail.isHu) {
                waitingforList.clear();
            } else {
                return ErrorCode.NOT_TURN;
            }
//            }

            //截杠胡
            if (jieGangHuCard != null) {
                playerCardsInfo.hu_dianpao(room, this, beJieGangUser, jieGangHuCard);
                //回放
                operateReqResp.setFromUserId(beJieGangUser);
                operateReqResp.setCard(jieGangHuCard);

                PlayerCardsInfoMj playerCardsInfoBeJie = playerCardsInfos.get(beJieGangUser);
                //删除杠
                if (playerCardsInfoBeJie != null) {
                    playerCardsInfoBeJie.cards.remove(jieGangHuCard);
                    playerCardsInfoBeJie.removeGang2Peng(jieGangHuCard);
                }

                beJieGangUser = -1;
                jieGangHuCard = null;


            } else {
                //删除弃牌
                deleteDisCard(lastPlayUserId, disCard);
                playerCardsInfo.hu_dianpao(room, this, lastPlayUserId, disCard);
                //回放
                operateReqResp.setFromUserId(lastOperateUserId);
                operateReqResp.setCard(disCard);

                this.disCard = null;
            }

            //回放
            operateReqResp.setIsMing(true);
            replay.getOperate().add(operateReqResp);
            handleHu(playerCardsInfo);
        }


        return 0;

    }


    protected void handleHu(PlayerCardsInfoMj playerCardsInfo) {
        isAlreadyHu = true;
        sendResult(true, playerCardsInfo.getUserId(), null);
        noticeDissolutionResult();
        room.clearReadyStatus(true);
    }

    protected boolean isRoomOver() {
        return room.isRoomOver();
    }


    public void noticeDissolutionResult() {
        if (isRoomOver()) {
            List<UserOfResult> userOfResultList = this.room.getUserOfResult();
            // 存储返回
            GameOfResult gameOfResult = new GameOfResult();
            gameOfResult.setUserList(userOfResultList);
            RoomManager.removeRoom(room.getRoomId());

            MsgSender.sendMsg2Player("gameService", "noticeDissolutionResult", gameOfResult, users);

            //战绩
            this.room.genRoomRecord();

        }


    }

    /**
     * 发送结果
     *
     * @param isHasWinner
     * @param winnerId
     * @param yipaoduoxiang
     */
    protected void sendResult(boolean isHasWinner, Long winnerId, List<Long> yipaoduoxiang) {
        ResultResp result = new ResultResp();
        ResponseVo vo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_RESULT, result);

        long remainCardBeginUser = 0;
        if (isHasWinner) {
            if (yipaoduoxiang == null) {
                result.setWinnerId(winnerId);
                remainCardBeginUser = nextTurnId(winnerId);
            } else {
                result.setYipaoduoxiang(yipaoduoxiang);
                remainCardBeginUser = nextTurnId(lastPlayUserId);
            }
            result.setBaoCard(baoCard);
        }
        List<PlayerCardsResp> list = new ArrayList<>();
        for (PlayerCardsInfoMj info : playerCardsInfos.values()) {
            PlayerCardsResp resp = new PlayerCardsResp(info);
            resp.setAllScore(room.getUserScores().get(info.getUserId()));
            list.add(resp);
        }
        result.setUserInfos(list);
        result.setLaZhuang(this.room.laZhuang);
        result.setLaZhuangStatus(this.room.laZhuangStatus);

        setReaminCardInfo(result, remainCardBeginUser);
        MsgSender.sendMsg2Player(vo, users);


        //回放
        replay.setResult(result);
        //生成记录
        genRecord();
    }

    public int getNeedRemainCardNum(){
        return 0;
    }

    protected void setReaminCardInfo(ResultResp result,long beginUser){
        List<String> cards = new ArrayList<>(this.remainCards);
//        int last = cards.size() - getNeedRemainCardNum();
//        if (last < 0) {
//            last = 0;
//        }
//        cards = cards.subList(0, last);
        result.getRemainCards().addAll(cards);
        result.setBeginUser(beginUser);
    }

    @Override
    protected void genRecord() {
        long id = IdWorker.getDefaultInstance().nextId();
        genRecord(playerCardsInfos.values().stream().collect
                (Collectors.toMap(PlayerCardsInfoMj::getUserId, PlayerCardsInfoMj::getScore)), room, id);

        replay.setId(id);
        replay.setCount(playerCardsInfos.size());
        replay.setRoom_uuid(this.room.getUuid());
        replay.setRoomInfo(this.getRoom().toJSONObject());

        KafkaMsgKey kafkaMsgKey = new KafkaMsgKey().setMsgId(KAFKA_MSG_ID_REPLAY);
        MsgProducer msgProducer = SpringUtil.getBean(MsgProducer.class);
        msgProducer.send(IKafaTopic.CENTER_TOPIC, kafkaMsgKey, replay);
    }

    /**
     * 听
     *
     * @param userId
     * @param card
     * @return
     */
    public int ting(long userId, String card) {

        PlayerCardsInfoMj playerCardsInfo = playerCardsInfos.get(userId);
        if (playerCardsInfo == null) {
            return ErrorCode.USER_ERROR;
        }

        if (!playerCardsInfo.cards.contains(card)) {
            return ErrorCode.CAN_NOT_TING;
        }

        OperateReqResp operateReqResp = new OperateReqResp();
        operateReqResp.setOperateType(OperateReqResp.type_ting);
        operateReqResp.setUserId(userId);
        operateReqResp.setCard(card);
        ResponseVo vo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_OTHER_OPERATE, operateReqResp);


        List<String> temp = new ArrayList<>();
        temp.addAll(playerCardsInfo.getCards());
        temp.remove(card);

        boolean isCan = playerCardsInfo.isCanTing(temp);//不多一张
        if (isCan) {
            playerCardsInfo.ting(card);
            //通知其他玩家听
            MsgSender.sendMsg2Player(vo, users);

            //回放
            replay.getOperate().add(operateReqResp);

            //通知其他玩家出牌信息
            PlayCardResp playCardResp = new PlayCardResp();
            playCardResp.setUserId(userId);
            playCardResp.setCard(null);

            ResponseVo chupaiVo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_PLAY_CARD, playCardResp);
            MsgSender.sendMsg2Player(chupaiVo, users);

            //其他人的操作 全是false 听牌后什么都不能操作
            for (Map.Entry<Long, PlayerCardsInfoMj> entry : playerCardsInfos.entrySet()) {
                PlayerCardsInfoMj pci = entry.getValue();
                pci.setCanBeGang(false);
                pci.setCanBePeng(false);
                pci.setCanBeHu(false);
                pci.setCanBeTing(false);

                OperateResp operateResp = new OperateResp();
                ResponseVo OperateVo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_OPERATE, operateResp);
                MsgSender.sendMsg2Player(OperateVo, entry.getKey());
            }

            //摸牌
            long nextId = nextTurnId(turnId);
            mopai(nextId, "userId : " + userId + " 听完下家抓牌");
        } else {
            return ErrorCode.CAN_NOT_TING;
        }
        return 0;
    }

    public int chi(long userId, String one, String two) {
        PlayerCardsInfoMj playerCardsInfo = playerCardsInfos.get(userId);
        boolean isCanChi = playerCardsInfo.isCanChiThisCard(disCard, one, two);
        if (!isCanChi) {
            return ErrorCode.CAN_NOT_CHI;
        }

        if (disCard == null || !playerCardsInfo.canBeChi) {
            return ErrorCode.CAN_NOT_CHI;
        }
        //从等待列表删除
//        if (waitingforList.size() > 0) {
        WaitDetail waitDetail = waitingforList.get(0);
        if (waitDetail != null && waitDetail.myUserId == userId && waitDetail.isChi) {
            waitingforList.clear();
        } else {
            return ErrorCode.NOT_TURN;
        }
//        }
        OperateReqResp operateReqResp = new OperateReqResp();
        operateReqResp.setOperateType(OperateReqResp.type_chi);
        operateReqResp.setUserId(userId);
        ResponseVo vo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_OTHER_OPERATE, operateReqResp);
        //通知其他玩家听
        MsgSender.sendMsg2Player(vo, users);

        //回放
        replay.getOperate().add(operateReqResp);

        //吃
        playerCardsInfo.chi(disCard, one, two);

        //吃完能听,杠,不能胡
        boolean isCanTing = playerCardsInfo.isCanTing(playerCardsInfo.cards);//多一张牌
        boolean isCanGang = playerCardsInfo.isHasGang();
        turnId = userId;
        // 通知客户端 操作
        OperateResp operateResp = new OperateResp();
        operateResp.setIsCanTing(isCanTing);
        operateResp.setIsCanGang(isCanGang);
        ResponseVo operateVo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_OPERATE, operateResp);
        MsgSender.sendMsg2Player(operateVo, userId);
        this.disCard = null;
        //自己将能做的处理
        playerCardsInfo.canBeChi = false;
        playerCardsInfo.canBeTing = isCanTing;
        playerCardsInfo.canBeGang = isCanGang;


        return 0;
    }

    public int chiTing(long userId, String one, String two) {
        PlayerCardsInfoMj playerCardsInfo = playerCardsInfos.get(userId);
        boolean isCanChi = playerCardsInfo.isCanChiThisCard(disCard, one, two);
        if (!isCanChi) {
            return ErrorCode.CAN_NOT_CHI_TING;
        }
        if (disCard == null) {
            return ErrorCode.CAN_NOT_CHI_TING;
        }
        //从等待列表删除
        if (waitingforList.size() > 0) {
            WaitDetail waitDetail = waitingforList.get(0);
            if (waitDetail.myUserId == userId && waitDetail.isChiTing) {
//                waitingforList.remove(waitDetail);
                waitingforList.clear();
            } else {
                return ErrorCode.NOT_TURN;
            }
        }
        OperateReqResp operateReqResp = new OperateReqResp();
        operateReqResp.setOperateType(OperateReqResp.type_chi_ting);
        operateReqResp.setUserId(userId);
        ResponseVo vo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_OTHER_OPERATE, operateReqResp);
        //通知其他玩家听
        MsgSender.sendMsg2Player(vo, users);

        //回放
        replay.getOperate().add(operateReqResp);

        //吃
        playerCardsInfo.chi(disCard, one, two);

        //吃完只能听
        turnId = userId;
        // 通知客户端 操作
        OperateResp operateResp = new OperateResp();
        operateResp.setIsCanTing(true);
        operateResp.setIsCanGang(false);
        ResponseVo operateVo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_OPERATE, operateResp);
        MsgSender.sendMsg2Player(operateVo, userId);
        this.disCard = null;
        //自己将能做的处理
        playerCardsInfo.canBeChi = false;
        playerCardsInfo.canBeTing = true;
        playerCardsInfo.canBeGang = false;
        return 0;
    }

    public int pengTing(long userId) {
        PlayerCardsInfoMj playerCardsInfo = playerCardsInfos.get(userId);

        boolean isCan = playerCardsInfo.isCanPengTing(this.disCard);
        if (!isCan) {
            return ErrorCode.CAN_NOT_PENG_TING;
        }

        if (disCard == null) {
            return ErrorCode.CAN_NOT_PENG_TING;
        }
        //从等待列表删除
        if (waitingforList.size() > 0) {
            WaitDetail waitDetail = waitingforList.get(0);
            if (waitDetail.myUserId == userId && waitDetail.isPengTing) {
//                waitingforList.remove(waitDetail);
                waitingforList.clear();
            } else {
                return ErrorCode.NOT_TURN;
            }
        }
        playerCardsInfo.peng(disCard, lastPlayUserId);
        lastOperateUserId = userId;

        //删除弃牌
        deleteDisCard(lastPlayUserId, disCard);


        //通知其他玩家

        OperateReqResp operateReqResp = new OperateReqResp();
        operateReqResp.setOperateType(OperateReqResp.type_peng_ting);
        operateReqResp.setCard(disCard);
        operateReqResp.setFromUserId(lastPlayUserId);
        operateReqResp.setUserId(userId);

        //回放
        replay.getOperate().add(operateReqResp);

        //通知其他人
        ResponseVo vo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_OTHER_OPERATE, operateReqResp);
        MsgSender.sendMsg2Player(vo, users);


        //碰完能听,杠,不能胡
        turnId = userId;
        // 通知客户端 操作
        OperateResp operateResp = new OperateResp();
        operateResp.setIsCanTing(true);
        operateResp.setIsCanGang(false);
        ResponseVo operateVo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_OPERATE, operateResp);
        MsgSender.sendMsg2Player(operateVo, userId);
        this.disCard = null;
        //自己将能做的处理
        playerCardsInfo.canBePeng = false;
        playerCardsInfo.canBeTing = true;
        playerCardsInfo.canBeGang = false;


        return 0;
    }


    /**
     * 交换牌 测试功能
     *
     * @param userId
     * @param srcType
     * @param desType
     * @return
     */
    public int exchange(long userId, int srcType, int desType) {
        if (!isTest) {
            return 0;
        }
        PlayerCardsInfoMj playerCardsInfo = playerCardsInfos.get(userId);
        if (playerCardsInfo == null) {
            return ErrorCode.USER_ERROR;
        }
        boolean isHas = false;
        String desCardStr = "";
        //从剩下的牌中找
        for (String card : remainCards) {
            if (desType == CardTypeUtil.cardType.get(card)) {
                isHas = true;
                //移除
//                remainCards.remove(card);
                desCardStr = card;
                break;

            }
        }
        if (isHas) {

            System.err.println("找到-------------------");
        }
        //如果有
        if (isHas) {
            for (String card : playerCardsInfo.getCards()) {
                //找到直接退出
                if (srcType == CardTypeUtil.cardType.get(card)) {
                    playerCardsInfo.getCards().remove(card);
                    playerCardsInfo.getCards().add(desCardStr);
                    //添加
                    remainCards.add(card);
                    remainCards.remove(desCardStr);

                    break;
                }
            }
        }

        ResponseVo vo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_EXCHANGE, new PlayerCardsResp(playerCardsInfo));
        MsgSender.sendMsg2Player(vo, userId);

        return 0;

    }

    public int needCard(long userId, int cardType) {
        PlayerCardsInfoMj playerCardsInfo = playerCardsInfos.get(userId);
        playerCardsInfo.nextNeedCard = cardType;
        return 0;
    }

    protected String getCardByTypeFromRemainCards(int type) {
        for (String card : remainCards) {
            if (type == CardTypeUtil.cardType.get(card)) {
                return card;
            }
        }
        return null;
    }

    public int tingWhat(long userId) {
        PlayerCardsInfoMj playerCardsInfo = playerCardsInfos.get(userId);
        if (playerCardsInfo == null) {
            return ErrorCode.USER_ERROR;
        }
        Map<Integer, Set<Integer>> result = new HashMap<>();
        playerCardsInfo.getTingWhatInfo().forEach(huCardType -> {
            int removeCardType = CardTypeUtil.getTypeByCard(huCardType.getTingRemoveCard());
            result.putIfAbsent(removeCardType, new HashSet<>());
            result.get(removeCardType).add(huCardType.tingCardType);
        });


        ResponseVo vo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, "tingWhatResp", result);
        MsgSender.sendMsg2Player(vo, userId);

        return 0;
    }

    /**
     * 下一个出牌人id
     *
     * @param curId
     * @return
     */
    public long nextTurnId(long curId) {
        int index = users.indexOf(curId);

        int nextId = index + 1;
        if (nextId >= users.size()) {
            nextId = 0;
        }
        return users.get(nextId);
    }

    /**
     * 荒庄积分清零
     */
    protected void turnResultToZeroOnHuangZhuang() {
        for (long i : room.getUserScores().keySet()) {
            room.setUserSocre(i, -getPlayerCardsInfos().get(i).getScore());
            if (this.getPlayerCardsInfos().get(i) != null) {
                this.getPlayerCardsInfos().get(i).setScore(0);
            }
        }
    }


    protected void addUserOperate(long userId, int type) {
        Map<Long, Integer> result = new HashMap<>();
        result.put(userId, type);
        this.userOperateList.add(result);
        //更新最后操作时间
        updateLastOperateTime();
    }

    /**
     * 牌是否一样
     *
     * @param cards
     * @return
     */
    protected boolean isCardSame(List<String> cards) {
        Set<Integer> set = new HashSet<>();
        cards.forEach(card -> set.add(CardTypeUtil.getTypeByCard(card)));
        return set.size() == 1;

    }

    public int getAllGangNum() {
        int result = 0;
        for (PlayerCardsInfoMj playerCardsInfoMj : this.playerCardsInfos.values()) {
            result += playerCardsInfoMj.getGangNum();
        }
        return result;
    }

    protected void doChiTing(PlayerCardsInfoMj playerCardsInfo, long userId, String one, String two) {
    }

    protected void doPengTing(PlayerCardsInfoMj playerCardsInfo, long userId) {
    }

    protected void doChi(PlayerCardsInfoMj playerCardsInfo, long userId, String one, String two) {
    }

    protected void doPeng(PlayerCardsInfoMj playerCardsInfo, long userId) {
    }

    protected void doGang(PlayerCardsInfoMj playerCardsInfo, long userId) {
    }

    protected void doHu(PlayerCardsInfoMj playerCardsInfo, long userId) {
    }


    public static Logger getLogger() {
        return logger;
    }

    public static boolean isIsTest() {
        return isTest;
    }

    public static String getGsjNofeng() {
        return GSJ_NOFENG;
    }

    public static String getDphNofeng() {
        return DPH_NOFENG;
    }

    public int getGameId() {
        return gameId;
    }

    public GameInfo setGameId(int gameId) {
        this.gameId = gameId;
        return this;
    }

    public int getPlayerSize() {
        return playerSize;
    }

    public GameInfo setPlayerSize(int playerSize) {
        this.playerSize = playerSize;
        return this;
    }

    public int getCardSize() {
        return cardSize;
    }

    public GameInfo setCardSize(int cardSize) {
        this.cardSize = cardSize;
        return this;
    }

    public int getGameModel() {
        return gameModel;
    }

    public GameInfo setGameModel(int gameModel) {
        this.gameModel = gameModel;
        return this;
    }

    public List<String> getRemainCards() {
        return remainCards;
    }

    public GameInfo setRemainCards(List<String> remainCards) {
        this.remainCards = remainCards;
        return this;
    }

    public String getDisCard() {
        return disCard;
    }

    public GameInfo setDisCard(String disCard) {
        this.disCard = disCard;
        return this;
    }

    public long getTurnId() {
        return turnId;
    }

    public GameInfo setTurnId(long turnId) {
        this.turnId = turnId;
        return this;
    }

    public long getLastPlayUserId() {
        return lastPlayUserId;
    }

    public GameInfo setLastPlayUserId(long lastPlayUserId) {
        this.lastPlayUserId = lastPlayUserId;
        return this;
    }

    public long getLastMoPaiUserId() {
        return lastMoPaiUserId;
    }

    public GameInfo setLastMoPaiUserId(long lastMoPaiUserId) {
        this.lastMoPaiUserId = lastMoPaiUserId;
        return this;
    }

    public String getCatchCard() {
        return catchCard;
    }

    public GameInfo setCatchCard(String catchCard) {
        this.catchCard = catchCard;
        return this;
    }

    public long getLastOperateUserId() {
        return lastOperateUserId;
    }

    public GameInfo setLastOperateUserId(long lastOperateUserId) {
        this.lastOperateUserId = lastOperateUserId;
        return this;
    }

    public Map<Long, PlayerCardsInfoMj> getPlayerCardsInfos() {
        return playerCardsInfos;
    }

    public GameInfo setPlayerCardsInfos(Map<Long, PlayerCardsInfoMj> playerCardsInfos) {
        this.playerCardsInfos = playerCardsInfos;
        return this;
    }

    public long getFirstTurn() {
        return firstTurn;
    }

    public GameInfo setFirstTurn(long firstTurn) {
        this.firstTurn = firstTurn;
        return this;
    }

    public List<WaitDetail> getWaitingforList() {
        return waitingforList;
    }

    public GameInfo setWaitingforList(List<WaitDetail> waitingforList) {
        this.waitingforList = waitingforList;
        return this;
    }


    public boolean isAlreadyHu() {
        return isAlreadyHu;
    }

    public GameInfo setAlreadyHu(boolean alreadyHu) {
        isAlreadyHu = alreadyHu;
        return this;
    }

    public int getBaoType() {
        return baoType;
    }

    public GameInfo setBaoType(int baoType) {
        this.baoType = baoType;
        return this;
    }

    public String getBaoCard() {
        return baoCard;
    }

    public GameInfo setBaoCard(String baoCard) {
        this.baoCard = baoCard;
        return this;
    }

    public boolean isHasJieGangHu() {
        return isHasJieGangHu;
    }

    public GameInfo setHasJieGangHu(boolean hasJieGangHu) {
        isHasJieGangHu = hasJieGangHu;
        return this;
    }

    public String getJieGangHuCard() {
        return jieGangHuCard;
    }

    public GameInfo setJieGangHuCard(String jieGangHuCard) {
        this.jieGangHuCard = jieGangHuCard;
        return this;
    }

    public long getBeJieGangUser() {
        return beJieGangUser;
    }

    public GameInfo setBeJieGangUser(long beJieGangUser) {
        this.beJieGangUser = beJieGangUser;
        return this;
    }


    public String getJieXuanfengCard() {
        return jieXuanfengCard;
    }

    public GameInfo setJieXuanfengCard(String jieXuanfengCard) {
        this.jieXuanfengCard = jieXuanfengCard;
        return this;
    }

    public long getBeJieXuanfengUser() {
        return beJieXuanfengUser;
    }

    public GameInfo setBeJieXuanfengUser(long beJieXuanfengUser) {
        this.beJieXuanfengUser = beJieXuanfengUser;
        return this;
    }

    public long getJieXuanfengCardType() {
        return jieXuanfengCardType;
    }

    public GameInfo setJieXuanfengCardType(long jieXuanfengCardType) {
        this.jieXuanfengCardType = jieXuanfengCardType;
        return this;
    }

    public RoomInfo getRoom() {
        return room;
    }

    public GameInfo setRoom(RoomInfo room) {
        this.room = room;
        return this;
    }

    public boolean isHasWaitCache() {
        return isHasWaitCache;
    }

    public GameInfo setHasWaitCache(boolean hasWaitCache) {
        isHasWaitCache = hasWaitCache;
        return this;
    }

    public int getChangeBaoSize() {
        return changeBaoSize;
    }

    public GameInfo setChangeBaoSize(int changeBaoSize) {
        this.changeBaoSize = changeBaoSize;
        return this;
    }

    public List<Map<Long, Integer>> getUserOperateList() {
        return userOperateList;
    }

    public GameInfo setUserOperateList(List<Map<Long, Integer>> userOperateList) {
        this.userOperateList = userOperateList;
        return this;
    }

    public List<Integer> getHun() {
        return hun;
    }

    public GameInfo setHun(List<Integer> hun) {
        this.hun = hun;
        return this;
    }

    public boolean isAfterTingShowCard() {
        return afterTingShowCard;
    }

    public GameInfo setAfterTingShowCard(boolean afterTingShowCard) {
        this.afterTingShowCard = afterTingShowCard;
        return this;
    }

    public long getLastCatchCardUser() {
        return lastCatchCardUser;
    }

    public GameInfo setLastCatchCardUser(long lastCatchCardUser) {
        this.lastCatchCardUser = lastCatchCardUser;
        return this;
    }

    public boolean isTurnZeroAfterHuangZhuang() {
        return isTurnZeroAfterHuangZhuang;
    }

    public GameInfo setTurnZeroAfterHuangZhuang(boolean turnZeroAfterHuangZhuang) {
        isTurnZeroAfterHuangZhuang = turnZeroAfterHuangZhuang;
        return this;
    }

    public int getState() {
        return state;
    }

    public GameInfo setState(int state) {
        this.state = state;
        return this;
    }
}
