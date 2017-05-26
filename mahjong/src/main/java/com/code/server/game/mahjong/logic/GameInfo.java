package com.code.server.game.mahjong.logic;


import com.code.server.game.mahjong.response.*;
import com.code.server.game.mahjong.util.HuUtil;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

/**
 * Created by T420 on 2016/11/30.
 */

public class GameInfo {

    protected static final Logger logger = Logger.getLogger("game");
    protected static final boolean isTest = true;

    protected static final String GSJ_NOFENG = "303";//303，3表示拐三角玩法，03表示无风
    protected static final String DPH_NOFENG = "6";//点炮胡，表示无风

    protected int gameId;
    protected int playerSize;//玩家个数
    protected int cardSize;//牌数量

    protected int gameModel;//游戏类型
    protected List<String> remainCards = new ArrayList<>();//剩下的牌
    protected String disCard;//出的牌
    protected int turnId;//轮到谁出牌
    protected int lastPlayUserId = -1;//上个出牌的人
    protected int lastMoPaiUserId = -1;//上个摸牌的人
    protected String catchCard;//摸得牌

    protected int lastOperateUserId;//上个操作的人
    protected Map<Integer, PlayerCardsInfo> playerCardsInfos = new HashMap<>();//玩家手上牌的信息
    protected int firstTurn = 0;//第一个出牌的人
    protected List<WaitDetail> waitingforList = new ArrayList<>();//需要等待玩家的列表
    protected List<Integer> users = new ArrayList<>();
    protected boolean isAlreadyHu;//是否已经胡过
    protected int baoType = -1;
    protected String baoCard = null;

    protected boolean isHasJieGangHu = true ;

    protected String jieGangHuCard = null;
    protected int beJieGangUser = -1;

    protected List<String> jieXuanfengCard = null;
    protected int beJieXuanfengUser = -1;
    protected int jieXuanfengCardType = -1;

    protected transient RoomInfo room;
    protected transient RoomDao roomDao;
    protected transient UserRecodeDao userRecodeDao;
    protected transient UserDao userDao;
    protected transient GameDao gameDao;
    private boolean isHasWaitCache;
    protected int changeBaoSize = 0;

    protected List<Map<Integer, Integer>> userOperateList = new ArrayList<>();




    /**
     * 初始化方法
     *
     * @param firstTurn
     * @param users
     */
    public void init(int gameId, int firstTurn, List<Integer> users, RoomInfo room, RoomDao roomDao, UserRecodeDao userRecodeDao, UserDao userDao, GameDao gameDao) {
        this.gameId = gameId;
        this.userDao = userDao;
        this.gameDao = gameDao;
        this.roomDao = roomDao;
        this.userRecodeDao = userRecodeDao;
        this.firstTurn = firstTurn;
        this.turnId = firstTurn;
        remainCards.addAll(CardTypeUtil.ALL_CARD);
        this.users.addAll(users);
        this.room = room;
        this.cardSize = 13;
        this.playerSize = room.getPersonNumber();
        //不带风
        if ("3".equals(room.getMode()) || "4".equals(room.getMode())) {
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
            /*int point = 16;
            while (point > 0) {
                remainCards.remove(0);
                point--;
            }*/
        }
    }

    /**
     * 发牌
     */
    public void fapai(ServerContext serverContext) {
        //打乱顺序
        Collections.shuffle(remainCards);
        for (int i = 0; i < playerSize; i++) {
            PlayerCardsInfo playerCardsInfo = PlayerCardsInfoFactory.getInstance(room);
            playerCardsInfo.setGameInfo(this);
            int userId = users.get(i);
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

            serverContext.sendToSinglePlayer(vo.toJsonObject(), "" + userId);


        }
        doAfterFapai();
        //第一个人抓牌
        mopai(serverContext, firstTurn,"发牌");

    }

    protected void doAfterFapai() {

    }


    /**
     * 荒庄的处理
     *
     * @param serverContext
     * @param userId
     */
    protected void handleHuangzhuang(ServerContext serverContext, int userId) {

        switch (this.room.modeTotal) {
            case "4":
                break;
            case "6":
                break;
            default:
                turnResultToZeroOnHuangZhuang();
        }
        sendResult(serverContext, false, userId);
        noticeDissolutionResult(serverContext);
        //通知所有玩家结束
        room.clearReadyStatus();
        if(HuUtil.getUserAndScore(this.room.getUserScores()).contains("否")){
        	logger.info("<<<牌局得分>>>"+HuUtil.getUserAndScore(this.room.getUserScores()));
        }
    }

    protected void computeAllGang(){
        for (PlayerCardsInfo player : this.getPlayerCardsInfos().values()) {
            player.computeALLGang();
        }
    }



    /**
     * 摸牌
     *
     * @param userId
     */
    protected void mopai(ServerContext serverContext, int userId, String... wz) {
        System.err.println("摸牌===============================userId : " + userId);


        PlayerCardsInfo playerCardsInfo = playerCardsInfos.get(userId);

        if (playerCardsInfo.isHuangzhuang(this)) {
            handleHuangzhuang(serverContext, userId);
            return;
        }


        if (playerCardsInfo.isMoreOneCard()) {
            if (wz.length > 0) {
                logger.info("======1操作后的摸牌 : "+wz[0]);
            }
            logger.info("userId : "+userId +"　===1 more one card 抓牌时多一张牌");
            logger.info("操作列表: "+playerCardsInfo.operateList.toString());
            logger.info("所有操作: " + userOperateList.toString());

        }

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

        playerCardsInfo.mopai(card);
        //
        turnId = userId;
        this.lastMoPaiUserId = userId;
        lastOperateUserId = userId;
        this.catchCard = card;

        // 把摸到的牌 推给摸牌的玩家
        int remainSize = remainCards.size();
        for (int user : users) {
            GetCardResp getCardResp = new GetCardResp();
            getCardResp.setRemainNum(remainSize);
            getCardResp.setUserId(userId);
            if (user == userId) {
                getCardResp.setCard(card);
            }
            ResponseVo responseVo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_GET_CARD, getCardResp);
            serverContext.sendToSinglePlayer(responseVo.toJsonObject(), "" + user);

            //能做的操作全置成不能
            PlayerCardsInfo other = playerCardsInfos.get(user);

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

        //可能的操作
        ResponseVo OperateResponseVo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_OPERATE, resp);
        serverContext.sendToSinglePlayer(OperateResponseVo.toJsonObject(), "" + userId);

    }


    /**
     * 出牌
     *
     * @param userId
     * @param card
     */
    public int chupai(ServerContext serverContext, int userId, String card) {
        //出牌的玩家
        PlayerCardsInfo chupaiPlayerCardsInfo = playerCardsInfos.get(userId);
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
        ResponseVo vo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_PLAY_CARD, playCardResp);
        serverContext.sendToOnlinePlayer(vo.toJsonObject(), users);

        //其他人能做的操作
        for (Map.Entry<Integer, PlayerCardsInfo> entry : playerCardsInfos.entrySet()) {
            OperateResp operateResp = new OperateResp();

            //其他玩家的处理 碰杠等 如果有加入等待列表(要等待这些玩家"过")
            if (userId != entry.getKey()) {
                //通知其他玩家出了什么牌 自己能有什么操作
                PlayerCardsInfo playerCardsInfo = entry.getValue();
                boolean isCanGang = playerCardsInfo.isCanGangAddThisCard(card);
                boolean isCanPeng = playerCardsInfo.isCanPengAddThisCard(card);
                boolean isCanHu = playerCardsInfo.isCanHu_dianpao(card);
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
            serverContext.sendToSinglePlayer(OperateVo.toJsonObject(), "" + entry.getKey());
        }

        resetCanBeOperate(chupaiPlayerCardsInfo);


        //如果等待列表为空 就轮到下个人摸牌
        if (this.waitingforList.size() == 0) {
            int nextId = nextTurnId(turnId);
            mopai(serverContext, nextId,"userid : "+userId + " 出牌");
        } else {
            //比较
            compare(waitingforList);
        }
        return 0;

    }


    /**
     * 删除弃牌
     *
     * @param userId
     * @param disCard
     */
    protected void deleteDisCard(int userId, String disCard) {
        PlayerCardsInfo playerCardsInfo = playerCardsInfos.get(userId);
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
                    List<Integer> turnList = next3TurnId();
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
    private List<Integer> next3TurnId() {
        List<Integer> result = new ArrayList<>();

        int curId = turnId;
        for (int i = 0; i < users.size() - 1; i++) {
            curId = nextTurnId(curId);
            result.add(curId);
        }
        return result;
    }

    protected void resetCanBeOperate(PlayerCardsInfo playerCardsInfo) {
        playerCardsInfo.setCanBeChi(false);
        playerCardsInfo.setCanBeGang(false);
        playerCardsInfo.setCanBePeng(false);
        playerCardsInfo.setCanBeHu(false);
        playerCardsInfo.setCanBeTing(false);
        playerCardsInfo.setCanBeChiTing(false);
        playerCardsInfo.setCanBePengTing(false);
        playerCardsInfo.setCanBeXuanfeng(false);
    }

    protected void resetOtherOperate(int userId) {
        for (PlayerCardsInfo playerCardsInfo : playerCardsInfos.values()) {
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

        int myUserId;
        boolean isHu;
        boolean isGang;
        boolean isPeng;
        boolean isChi;
        boolean isChiTing;
        boolean isPengTing;

        int operate = -1;
        GameInfo gameInfo;
        ServerContext serverContext;
        String[] params;


        public WaitDetail() {
        }

        public WaitDetail(int myUserId, boolean isHu, boolean isGang, boolean isPeng, boolean isChi, boolean isChiTing, boolean isPengTing) {
            this.myUserId = myUserId;
            this.isHu = isHu;
            this.isGang = isGang;
            this.isPeng = isPeng;
            this.isChi = isChi;
            this.isChiTing = isChiTing;
            this.isPengTing = isPengTing;
        }


        void operate(GameInfo gameInfo,int operate, ServerContext serverContext, String... params) {
            //只有第一次操作有效
            if (this.operate == -1) {
                this.operate = operate;
                this.gameInfo = gameInfo;
                this.params = params;
                this.serverContext = serverContext;
            }
        }

        void fire() {
            PlayerCardsInfo playerCardsInfo = gameInfo.getPlayerCardsInfos().get(myUserId);
            switch (operate) {
                case huPoint:
                  gameInfo.doHu(serverContext,playerCardsInfo,myUserId);
                  break;
                case gangPoint:
                    gameInfo.doGang(serverContext,playerCardsInfo,myUserId);
                    break;
                case pengTingPoint:
                    gameInfo.doPengTing(serverContext, playerCardsInfo, myUserId);
                    break;
                case chiTingPoint:
                    gameInfo.doChiTing(serverContext, playerCardsInfo, myUserId,params[0],params[1]);
                    break;
                case pengPoint:
                    gameInfo.doPeng(serverContext, playerCardsInfo, myUserId);
                    break;
                case chiPoint:
                    gameInfo.doChi(serverContext,playerCardsInfo,myUserId,params[0],params[1]);
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
    public int guo(ServerContext serverContext, int userId) {
//        if (waitingforList.size() == 0) {
//            return ErrorCode.CAN_NOT_GUO;
//        }

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
                    PlayerCardsInfo playerCardsInfo = playerCardsInfos.get(beJieGangUser);
                    if (playerCardsInfo != null) {
                        doGang_hand_after(serverContext, playerCardsInfo, true, -1, jieGangHuCard);
                    }
                    beJieGangUser = -1;
                    jieGangHuCard = null;
                } else {

                    int nextId = nextTurnId(turnId);
                    //下个人摸牌
                    mopai(serverContext, nextId,"过后抓牌");
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
    public int gang(ServerContext serverContext, int userId, String card) {
        PlayerCardsInfo playerCardsInfo = playerCardsInfos.get(userId);
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
            operateReqResp.setFromUserId(userId);
            operateReqResp.setUserId(userId);
            operateReqResp.setCard(card);
            operateReqResp.setIsMing(isMing);
            serverContext.sendToOnlinePlayer(vo.toJsonObject(), users);

            if (isHasJieGangHu &&isMing) {

                for (Map.Entry<Integer, PlayerCardsInfo> entry : playerCardsInfos.entrySet()) {
                    OperateResp operateResp = new OperateResp();

                    //其他玩家的处理 碰杠等 如果有加入等待列表(要等待这些玩家"过")
                    if (userId != entry.getKey()) {
                        //通知其他玩家出了什么牌 自己能有什么操作
                        PlayerCardsInfo playerOther = entry.getValue();
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
                    serverContext.sendToSinglePlayer(OperateVo.toJsonObject(), "" + entry.getKey());
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
                doGang_hand_after(serverContext,playerCardsInfo, isMing, -1, card);
            }


        } else {
            if (disCard == null || !playerCardsInfo.canBeGang) {
                return ErrorCode.CAN_NOT_GANG;
            }
            if(!playerCardsInfo.isCanGangAddThisCard(disCard)){
                return ErrorCode.CAN_NOT_GANG;
            }

            //从等待列表删除
//            if (waitingforList.size() > 0) {
                WaitDetail waitDetail = waitingforList.get(0);
                if (waitDetail!= null && waitDetail.myUserId == userId && waitDetail.isGang) {
                    waitingforList.clear();
                } else {
                    return ErrorCode.NOT_TURN;
                }
//            }

            //删除弃牌
            deleteDisCard(lastPlayUserId, disCard);
            lastOperateUserId = userId;

            playerCardsInfo.gang_discard(room, this, lastPlayUserId, disCard);
            operateReqResp.setFromUserId(lastPlayUserId);//谁出的牌

            operateReqResp.setUserId(userId);
            operateReqResp.setCard(disCard);
            operateReqResp.setIsMing(true);
            //通知所有人有杠
            serverContext.sendToOnlinePlayer(vo.toJsonObject(), users);

            mopai(serverContext, userId,"userId : "+userId + " 点杠后抓牌");
            turnId = userId;
            this.disCard = null;
        }


        return 0;


    }

    protected void doGang_hand_after(ServerContext serverContext, PlayerCardsInfo playerCardsInfo,boolean isMing, int userId,String card){
        playerCardsInfo.gangCompute(room, this, isMing, -1,card);
        mopai(serverContext, playerCardsInfo.getUserId(),"userId : "+  playerCardsInfo.getUserId() + " 自摸杠抓牌");
        turnId = playerCardsInfo.getUserId();
    }

    /**
     * 碰牌
     *
     * @param serverContext
     * @param userId
     * @return
     */
    public int peng(ServerContext serverContext, int userId) {
        PlayerCardsInfo playerCardsInfo = playerCardsInfos.get(userId);
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
                if (waitDetail!=null && waitDetail.myUserId == userId && waitDetail.isPeng) {
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

            //通知其他人
            ResponseVo vo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_OTHER_OPERATE, operateReqResp);
            serverContext.sendToOnlinePlayer(vo.toJsonObject(), users);


            //碰完能听,杠,不能胡
            boolean isCanTing = playerCardsInfo.isCanTing(playerCardsInfo.cards);//多一张牌
            boolean isCanGang = playerCardsInfo.isHasGang();
            turnId = userId;
            // 通知客户端 操作
            OperateResp operateResp = new OperateResp();
            operateResp.setIsCanTing(isCanTing);
            operateResp.setIsCanGang(isCanGang);
            ResponseVo operateVo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_OPERATE, operateResp);
            serverContext.sendToSinglePlayer(operateVo.toJsonObject(), "" + userId);
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
     * 胡牌
     *
     * @param serverContext
     * @param userId
     * @return
     */
    public int hu(ServerContext serverContext, int userId) {

        PlayerCardsInfo playerCardsInfo = playerCardsInfos.get(userId);
        if (playerCardsInfo == null) {
            return ErrorCode.USER_ERROR;
        }
        if (lastOperateUserId == userId) {//自摸
            if (!playerCardsInfo.isCanHu_zimo(catchCard)) {
                return ErrorCode.CAN_NOT_HU;
            }
            room.setBankerId(userId);
            playerCardsInfo.hu_zm(room, this, catchCard);
            handleHu(serverContext,playerCardsInfo);
        } else {
            if (this.disCard == null && jieGangHuCard == null) {
                return ErrorCode.CAN_NOT_HU;
            }

            String card = this.disCard;
            //
            if (jieGangHuCard != null) {
                card = jieGangHuCard;
            }

            if(!playerCardsInfo.isCanHu_dianpao(card)){
                return ErrorCode.CAN_NOT_HU;
            }
            room.setBankerId(userId);
            //从等待列表删除
//            if (waitingforList.size() > 0) {
                WaitDetail waitDetail = waitingforList.get(0);
                if (waitDetail!=null && waitDetail.myUserId == userId && waitDetail.isHu) {
                    waitingforList.clear();
                } else {
                    return ErrorCode.NOT_TURN;
                }
//            }

            //截杠胡
            if (jieGangHuCard != null) {
                playerCardsInfo.hu_dianpao(room, this, beJieGangUser, jieGangHuCard);
                PlayerCardsInfo playerCardsInfoBeJie =  playerCardsInfos.get(beJieGangUser);
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
                this.disCard = null;
            }
            handleHu(serverContext,playerCardsInfo);
        }


        if(HuUtil.getUserAndScore(this.room.getUserScores()).contains("否")){
        	logger.info("<<<牌局得分>>>"+HuUtil.getUserAndScore(this.room.getUserScores()));
        }
        
        return 0;
        
    }

    protected void handleHu(ServerContext serverContext, PlayerCardsInfo playerCardsInfo) {
        isAlreadyHu = true;
        sendResult(serverContext, true, playerCardsInfo.getUserId());
        noticeDissolutionResult(serverContext);
        room.clearReadyStatus();
    }

    protected boolean isRoomOver(){
        return room.getCurGameNumber() >= room.getGameNumber();
    }
    public void noticeDissolutionResult(ServerContext serverContext) {

        try {
            if (isRoomOver()) {
                ArrayList<UserOfResult> userOfResultList = new ArrayList<UserOfResult>();
                long time = System.currentTimeMillis();
                for (Integer s : users) {
                    UserOfResult east = new UserOfResult();
                    User user = userDao.getUser(s);
                    user.setRoomId("0");
                    user.setSeatId("0");
                    userDao.saveUser(user);

                    east.setUserId(user.getId());
                    east.setUsername(URLDecoder.decode(user.getUsername(), "utf-8"));
                    east.setImage(user.getImage());
                    east.setScores(room.getUserScores().get(s) + "");
                    east.setTime(time);
                    //设置胡牌次数
                    if(room.getHuNum().containsKey(user.getId())){
                        east.setHuNum(room.getHuNum().get(user.getId()));
                    }
                    if(room.getLianZhuangNum().containsKey(user.getId())){
                        east.setLianZhuangNum(room.getLianZhuangNum().get(user.getId()));
                    }
                    if(room.getDianPaoNum().containsKey(user.getId())){
                        east.setDianPaoNum((room.getDianPaoNum().get(user.getId())));
                    }
                    if(room.getMoBaoNum().containsKey(user.getId())){
                        east.setMoBaoNum(room.getMoBaoNum().get(user.getId()));
                    }

                    userOfResultList.add(east);

                    //删除玩家房间映射关系
                    GameManager.getInstance().getUserRoom().remove(user.getId());
                }

                // 存储返回
                GameOfResult gameOfResult = new GameOfResult();
                gameOfResult.setUserList(userOfResultList);

                JSONObject noticeEndResult = new JSONObject();
                noticeEndResult.put("service", "gameService");
                noticeEndResult.put("method", "noticeDissolutionResult");
                noticeEndResult.put("params", gameOfResult.toJSONObject());
                noticeEndResult.put("code", "0");

                serverContext.sendToOnlinePlayer(noticeEndResult, users);
//                logger.info("===send room over curGameNum: "+room.getCurGameNumber()+"  allGameNum: "+room.getGameNumber());
                GameManager.getInstance().getRoomLock().remove(room.roomId);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


    }

    /**
     * 发送结果
     *
     * @param serverContext
     * @param isHasWinner
     * @param winnerId
     */
    protected void sendResult(ServerContext serverContext, boolean isHasWinner, int winnerId) {
        ResultResp result = new ResultResp();
        ResponseVo vo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_RESULT, result);

        if (isHasWinner) {
            result.setWinnerId(winnerId);
            result.setBaoCard(baoCard);
        }
        List<PlayerCardsResp> list = new ArrayList<>();
        for (PlayerCardsInfo info : playerCardsInfos.values()) {
            PlayerCardsResp resp = new PlayerCardsResp(info);
            resp.setAllScore(room.getUserScores().get(info.getUserId()));
            list.add(resp);
        }
        result.setUserInfos(list);
        serverContext.sendToOnlinePlayer(vo.toJsonObject(), users);

    }

    /**
     * 听
     *
     * @param serverContext
     * @param userId
     * @param card
     * @return
     */
    public int ting(ServerContext serverContext, int userId, String card) {

        PlayerCardsInfo playerCardsInfo = playerCardsInfos.get(userId);
        if (playerCardsInfo == null) {
            return ErrorCode.USER_ERROR;
        }
        OperateReqResp operateReqResp = new OperateReqResp();
        operateReqResp.setOperateType(OperateReqResp.type_ting);
        operateReqResp.setUserId(userId);
        ResponseVo vo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_OTHER_OPERATE, operateReqResp);


        List<String> temp = new ArrayList<>();
        temp.addAll(playerCardsInfo.getCards());
        temp.remove(card);

        boolean isCan = playerCardsInfo.isCanTing(temp);//不多一张
        if (isCan) {
            playerCardsInfo.ting(card);
            //通知其他玩家听
            serverContext.sendToOnlinePlayer(vo.toJsonObject(), users);


            //通知其他玩家出牌信息
            PlayCardResp playCardResp = new PlayCardResp();
            playCardResp.setUserId(userId);
            playCardResp.setCard(null);

            ResponseVo chupaiVo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_PLAY_CARD, playCardResp);
            serverContext.sendToOnlinePlayer(chupaiVo.toJsonObject(), users);

            //其他人的操作 全是false 听牌后什么都不能操作
            for (Map.Entry<Integer, PlayerCardsInfo> entry : playerCardsInfos.entrySet()) {
                PlayerCardsInfo pci = entry.getValue();
                pci.setCanBeGang(false);
                pci.setCanBePeng(false);
                pci.setCanBeHu(false);
                pci.setCanBeTing(false);

                OperateResp operateResp = new OperateResp();
                ResponseVo OperateVo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_OPERATE, operateResp);
                serverContext.sendToSinglePlayer(OperateVo.toJsonObject(), "" + entry.getKey());
            }

            //摸牌
            int nextId = nextTurnId(turnId);
            mopai(serverContext, nextId,"userId : "+ userId + " 听完下家抓牌");
        } else {
            return ErrorCode.CAN_NOT_TING;
        }
        return 0;
    }

    public int chi(ServerContext serverContext, int userId, String one, String two) {
        PlayerCardsInfo playerCardsInfo = playerCardsInfos.get(userId);
        boolean isCanChi = playerCardsInfo.isCanChiThisCard(disCard, one, two);
        if (!isCanChi) {
            return ErrorCode.CAN_NOT_CHI;
        }

        if (disCard == null ||!playerCardsInfo.canBeChi) {
            return ErrorCode.CAN_NOT_CHI;
        }
        //从等待列表删除
//        if (waitingforList.size() > 0) {
            WaitDetail waitDetail = waitingforList.get(0);
            if (waitDetail != null & waitDetail.myUserId == userId && waitDetail.isChi) {
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
        serverContext.sendToOnlinePlayer(vo.toJsonObject(), users);

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
        serverContext.sendToSinglePlayer(operateVo.toJsonObject(), "" + userId);
        this.disCard = null;
        //自己将能做的处理
        playerCardsInfo.canBeChi = false;
        playerCardsInfo.canBeTing = isCanTing;
        playerCardsInfo.canBeGang = isCanGang;


        return 0;
    }

    public int chiTing(ServerContext serverContext, int userId, String one, String two) {
        PlayerCardsInfo playerCardsInfo = playerCardsInfos.get(userId);
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
        serverContext.sendToOnlinePlayer(vo.toJsonObject(), users);

        //吃
        playerCardsInfo.chi(disCard, one, two);

        //吃完只能听
        turnId = userId;
        // 通知客户端 操作
        OperateResp operateResp = new OperateResp();
        operateResp.setIsCanTing(true);
        operateResp.setIsCanGang(false);
        ResponseVo operateVo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_OPERATE, operateResp);
        serverContext.sendToSinglePlayer(operateVo.toJsonObject(), "" + userId);
        this.disCard = null;
        //自己将能做的处理
        playerCardsInfo.canBeChi = false;
        playerCardsInfo.canBeTing = true;
        playerCardsInfo.canBeGang = false;
        return 0;
    }

    public int pengTing(ServerContext serverContext, int userId) {
        PlayerCardsInfo playerCardsInfo = playerCardsInfos.get(userId);

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

        //通知其他人
        ResponseVo vo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_OTHER_OPERATE, operateReqResp);
        serverContext.sendToOnlinePlayer(vo.toJsonObject(), users);


        //碰完能听,杠,不能胡
        turnId = userId;
        // 通知客户端 操作
        OperateResp operateResp = new OperateResp();
        operateResp.setIsCanTing(true);
        operateResp.setIsCanGang(false);
        ResponseVo operateVo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_OPERATE, operateResp);
        serverContext.sendToSinglePlayer(operateVo.toJsonObject(), "" + userId);
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
     * @param serverContext
     * @param userId
     * @param srcType
     * @param desType
     * @return
     */
    public int exchange(ServerContext serverContext, int userId, int srcType, int desType) {
        if (!isTest) {
            return 0;
        }
        PlayerCardsInfo playerCardsInfo = playerCardsInfos.get(userId);
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
        serverContext.sendToSinglePlayer(vo.toJsonObject(), "" + userId);

        return 0;

    }

    public int needCard(ServerContext serverContext, int userId, int cardType) {
        PlayerCardsInfo playerCardsInfo = playerCardsInfos.get(userId);
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

    /**
     * 下一个出牌人id
     *
     * @param curId
     * @return
     */
    public int nextTurnId(int curId) {
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
        for (Integer i : room.getUserScores().keySet()) {
            room.setUserSocre(i, -getPlayerCardsInfos().get(i).getScore());
            if (this.getPlayerCardsInfos().get(i) != null) {
                this.getPlayerCardsInfos().get(i).setScore(0);
            }
        }
    }


    protected void addUserOperate(int userId, int type) {
        Map<Integer, Integer> result = new HashMap<>();
        result.put(userId, type);
        this.userOperateList.add(result);
    }

    protected void doChiTing(ServerContext serverContext, PlayerCardsInfo playerCardsInfo, int userId, String one, String two){}
    protected void doPengTing(ServerContext serverContext, PlayerCardsInfo playerCardsInfo, int userId){}
    protected void doChi(ServerContext serverContext, PlayerCardsInfo playerCardsInfo, int userId, String one, String two){}
    protected void doPeng(ServerContext serverContext, PlayerCardsInfo playerCardsInfo, int userId){}
    protected void doGang(ServerContext serverContext, PlayerCardsInfo playerCardsInfo, int userId){}
    protected void doHu(ServerContext serverContext, PlayerCardsInfo playerCardsInfo, int userId){}

    private static void testCompare() {
        GameInfo info = new GameInfo();
        List<Integer> users = new ArrayList<>();
        users.add(1);
        users.add(2);
        users.add(3);
        users.add(4);
        info.users = users;
        info.turnId = 1;
        List<WaitDetail> list = new ArrayList<>();
        WaitDetail waitDetail1 = new WaitDetail();
        WaitDetail waitDetail2 = new WaitDetail();
        WaitDetail waitDetail3 = new WaitDetail();
        list.add(waitDetail1);
        list.add(waitDetail2);
        list.add(waitDetail3);
        waitDetail1.isHu = true;
        waitDetail1.myUserId = 2;

        waitDetail2.isGang = true;
        waitDetail2.myUserId = 3;

        waitDetail3.isHu = true;
        waitDetail3.myUserId = 4;
        info.compare(list);
        System.out.println(list);
    }


    public int getGameModel() {
        return gameModel;
    }

    public void setGameModel(int gameModel) {
        this.gameModel = gameModel;
    }

    public List<String> getRemainCards() {
        return remainCards;
    }

    public void setRemainCards(List<String> remainCards) {
        this.remainCards = remainCards;
    }

    public String getDisCard() {
        return disCard;
    }

    public void setDisCard(String disCard) {
        this.disCard = disCard;
    }

    public int getTurnId() {
        return turnId;
    }

    public void setTurnId(int turnId) {
        this.turnId = turnId;
    }

    public int getLastPlayUserId() {
        return lastPlayUserId;
    }

    public void setLastPlayUserId(int lastPlayUserId) {
        this.lastPlayUserId = lastPlayUserId;
    }

    public int getLastMoPaiUserId() {
        return lastMoPaiUserId;
    }

    public void setLastMoPaiUserId(int lastMoPaiUserId) {
        this.lastMoPaiUserId = lastMoPaiUserId;
    }

    public Map<Integer, PlayerCardsInfo> getPlayerCardsInfos() {
        return playerCardsInfos;
    }

    public void setPlayerCardsInfos(Map<Integer, PlayerCardsInfo> playerCardsInfos) {
        this.playerCardsInfos = playerCardsInfos;
    }

    public int getFirstTurn() {
        return firstTurn;
    }

    public void setFirstTurn(int firstTurn) {
        this.firstTurn = firstTurn;
    }

    public List<WaitDetail> getWaitingforList() {
        return waitingforList;
    }

    public GameInfo setWaitingforList(List<WaitDetail> waitingforList) {
        this.waitingforList = waitingforList;
        return this;
    }

    public List<Integer> getUsers() {
        return users;
    }

    public void setUsers(List<Integer> users) {
        this.users = users;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public GameDao getGameDao() {
        return gameDao;
    }

    public void setGameDao(GameDao gameDao) {
        this.gameDao = gameDao;
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
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

    public int getPlayerSize() {
        return playerSize;
    }

    public void setPlayerSize(int playerSize) {
        this.playerSize = playerSize;
    }

    public int getCardSize() {
        return cardSize;
    }

    public void setCardSize(int cardSize) {
        this.cardSize = cardSize;
    }

    public RoomInfo getRoom() {
        return room;
    }

    public GameInfo setRoom(RoomInfo room) {
        this.room = room;
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

    public static String getUserAndScore(Map<Integer,Integer> map){
    	StringBuffer sb = new StringBuffer();
    	int temp = 0;
    	for (Integer i : map.keySet()) {
			sb.append("UserId:"+i+"得分为："+map.get(i));
			sb.append(";");
			temp+=map.get(i);
		}
    	sb.append("是不是0和："+ (temp==0?"是":"否"));
    	return sb.toString();
    }
    
    public static void main(String[] args) {
    	
	}

    public boolean isHasJieGangHu() {
        return isHasJieGangHu;
    }

    public GameInfo setHasJieGangHu(boolean hasJieGangHu) {
        isHasJieGangHu = hasJieGangHu;
        return this;
    }

    public int getChangeBaoSize() {
        return changeBaoSize;
    }

    public GameInfo setChangeBaoSize(int changeBaoSize) {
        this.changeBaoSize = changeBaoSize;
        return this;
    }

    public List<String> getJieXuanfengCard() {
        return jieXuanfengCard;
    }

    public GameInfo setJieXuanfengCard(List<String> jieXuanfengCard) {
        this.jieXuanfengCard = jieXuanfengCard;
        return this;
    }

    public int getJieXuanfengCardType() {
        return jieXuanfengCardType;
    }

    public GameInfo setJieXuanfengCardType(int jieXuanfengCardType) {
        this.jieXuanfengCardType = jieXuanfengCardType;
        return this;
    }

    public int getBeJieXuanfengUser() {
        return beJieXuanfengUser;
    }

    public GameInfo setBeJieXuanfengUser(int beJieXuanfengUser) {
        this.beJieXuanfengUser = beJieXuanfengUser;
        return this;
    }
}
