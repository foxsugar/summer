package com.code.server.game.mahjong.logic;


import com.code.server.constant.response.GameOfResult;
import com.code.server.constant.response.ResponseVo;
import com.code.server.constant.response.UserOfResult;
import com.code.server.game.mahjong.response.*;
import com.code.server.game.room.kafka.MsgSender;
import com.code.server.game.room.service.RoomManager;

import java.util.*;


public class GameInfoNew extends GameInfo {

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
        logger.info("摸牌: " + userId);
        PlayerCardsInfoMj playerCardsInfo = playerCardsInfos.get(userId);
        if (isHasGuoHu()) {
            playerCardsInfo.setGuoHu(false);
        }
        if (isHuangzhuang(playerCardsInfo)) {
            handleHuangzhuang(userId);
            return;
        }

        //拿出一张
        String card = getMoPaiCard(playerCardsInfo);
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

        //todo 下次的庄家
        setBanker(yipaoduoxiang.get(0));

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


    protected void doGang_hand_after(PlayerCardsInfoMj playerCardsInfo, boolean isMing, int userId, String card) {
        playerCardsInfo.gangCompute(room, this, isMing, -1, card);
        mopai(playerCardsInfo.getUserId(), "userId : " + playerCardsInfo.getUserId() + " 自摸杠抓牌");
        turnId = playerCardsInfo.getUserId();
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
            if ("LQ".equals(this.room.getGameType()) && ("11".equals(this.room.getMode()) || "12".equals(this.room.getMode()) || "13".equals(this.room.getMode()) || "14".equals(this.room.getMode()) || "1".equals(this.room.getMode()) || "2".equals(this.room.getMode()) || "3".equals(this.room.getMode()) || "4".equals(this.room.getMode()))) {
                room.setBankerId(winnerId);
            } else {
                long nextId = nextTurnId(this.getFirstTurn());
                room.setBankerId(nextId);
            }
        }
    }


    protected void handleHu(PlayerCardsInfoMj playerCardsInfo) {
        isAlreadyHu = true;
        sendResult(true, playerCardsInfo.getUserId(), null);
        noticeDissolutionResult();
        room.clearReadyStatus(true);
    }


    public void noticeDissolutionResult() {
        //金币房 不记录
        if (this.room.isGoldRoom()) return;

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


    /**
     * 荒庄的处理
     *
     * @param userId
     */
    protected void handleHuangzhuang(long userId) {
        computeAllGang();
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
    }


    protected int chuPai_ting(long userId, String card) {
        //出牌的玩家
        PlayerCardsInfoMj chupaiPlayerCardsInfo = playerCardsInfos.get(userId);
        if (this.turnId != userId || isAlreadyHu) {
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
        if (afterTingShowCard) {
            playCardResp.setCard(this.disCard);
        } else {
            playCardResp.setCard(null);
        }
        ResponseVo vo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_PLAY_CARD, playCardResp);
        MsgSender.sendMsg2Player(vo, users);


        //其他人能做的操作
        for (Map.Entry<Long, PlayerCardsInfoMj> entry : playerCardsInfos.entrySet()) {
            OperateResp operateResp = new OperateResp();

            //其他玩家的处理 碰杠等 如果有加入等待列表(要等待这些玩家"过")
            if (userId != entry.getKey()) {
                //通知其他玩家出了什么牌 自己能有什么操作
                PlayerCardsInfoMj playerCardsInfo = entry.getValue();
                boolean isCanGang = afterTingShowCard && playerCardsInfo.isCanGangAddThisCard(card);
                boolean isCanPeng = afterTingShowCard && playerCardsInfo.isCanPengAddThisCard(card);
                boolean isCanHu = !(isHasGuoHu() && playerCardsInfo.isGuoHu()) && afterTingShowCard && playerCardsInfo.isCanHu_dianpao(card);
                boolean isNext = afterTingShowCard && (nextTurnId(chupaiPlayerCardsInfo.getUserId()) == playerCardsInfo.getUserId());
                boolean isCanChi = afterTingShowCard && isNext && playerCardsInfo.isHasChi(card);
                boolean isCanChiTing = afterTingShowCard && playerCardsInfo.isCanChiTing(card);
                boolean isCanPengTing = afterTingShowCard && playerCardsInfo.isCanPengTing(card);
                //设置返回结果
                operateResp.setCanBeOperate(isCanChi, isCanPeng, isCanGang, false, isCanHu, isCanChiTing, isCanPengTing);

                //设置自己能做的操作
                playerCardsInfo.setCanBeOperate(isCanChi, isCanPeng, isCanGang, false, isCanHu, isCanChiTing, isCanPengTing);

                boolean isWait = isCanGang || isCanPeng || isCanHu || isCanChi || isCanChiTing || isCanPengTing;
                if (isWait) {
//                    this.waitingforList.add(new WaitDetail(entry.getKey(), isCanHu, isCanGang, isCanPeng, isCanChi, isCanChiTing, isCanPengTing));
                    add2WaitingList(entry.getKey(), isCanHu, isCanGang, isCanPeng, isCanChi, isCanChiTing, isCanPengTing);
                }
            }

            //可能的操作
            ResponseVo OperateVo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_OPERATE, operateResp);
            MsgSender.sendMsg2Player(OperateVo, entry.getKey());
        }

        resetCanBeOperate(chupaiPlayerCardsInfo);

        //如果等待列表为空 就轮到下个人摸牌
        if (this.waitingforList.size() == 0) {
            pushAllPass();
            long nextId = nextTurnId(turnId);
            mopai(nextId, "出牌");
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

    /**
     * 出牌
     *
     * @param userId
     * @param card
     */
    public int chupai(long userId, String card) {
        if (isAlreadyHu) {
            return ErrorCode.CAN_PLAYCARD_IS_HU;
        }
        System.out.println("出牌的人 = " + userId);
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
                boolean isCanHu;
                if (isHasGuoHu() && playerCardsInfo.isGuoHu()) {
                    isCanHu = false;
                } else {
                    isCanHu = playerCardsInfo.isCanHu_dianpao(card);
                }
                boolean isNext = nextTurnId(chupaiPlayerCardsInfo.getUserId()) == playerCardsInfo.getUserId();
                boolean isCanChi = isNext && playerCardsInfo.isHasChi(card);
                boolean isCanChiTing = playerCardsInfo.isCanChiTing(card);
                boolean isCanPengTing = playerCardsInfo.isCanPengTing(card);
                //设置返回结果
                operateResp.setCanBeOperate(isCanChi, isCanPeng, isCanGang, false, isCanHu, isCanChiTing, isCanPengTing);

                //设置自己能做的操作
                playerCardsInfo.setCanBeOperate(isCanChi, isCanPeng, isCanGang, false, isCanHu, isCanChiTing, isCanPengTing);

                boolean isWait = isCanGang || isCanPeng || isCanHu || isCanChi || isCanChiTing || isCanPengTing;
                if (isWait) {
                    add2WaitingList(entry.getKey(), isCanHu, isCanGang, isCanPeng, isCanChi, isCanChiTing, isCanPengTing);
                }
            }

            //可能的操作
            ResponseVo OperateVo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_OPERATE, operateResp);
            MsgSender.sendMsg2Player(OperateVo, entry.getKey());
        }

        resetCanBeOperate(chupaiPlayerCardsInfo);


        System.out.println("======= waiting list : " + waitingforList.size());
        //如果等待列表为空 就轮到下个人摸牌
        if (this.waitingforList.size() == 0) {
            pushAllPass();
            long nextId = nextTurnId(turnId);
            mopai(nextId, "出牌");
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

    protected void add2WaitingList(long userId, boolean isHu, boolean isGang, boolean isPeng, boolean isChi, boolean isChiTing, boolean isPengTing) {
        if (isHu) {
            this.waitingforList.add(new WaitDetail(userId, true, false, false, false, false, false));
        }
        if (isGang) {
            this.waitingforList.add(new WaitDetail(userId, false, true, false, false, false, false));
        }
        if (isPeng) {
            this.waitingforList.add(new WaitDetail(userId, false, false, true, false, false, false));
        }
        if (isChi) {
            this.waitingforList.add(new WaitDetail(userId, false, false, false, true, false, false));
        }
        if (isChiTing) {
            this.waitingforList.add(new WaitDetail(userId, false, false, false, false, true, false));
        }
        if (isPengTing) {
            this.waitingforList.add(new WaitDetail(userId, false, false, false, false, false, true));
        }

    }

    /**
     * 杠
     *
     * @param userId
     */
    public int gang(long userId, String card) {
        if (isAlreadyHu) {
            return ErrorCode.CAN_NOT_GANG;
        }
        PlayerCardsInfoMj playerCardsInfo = playerCardsInfos.get(userId);
        if (playerCardsInfo == null) {
            return ErrorCode.USER_ERROR;
        }
        OperateReqResp operateReqResp = new OperateReqResp();
        operateReqResp.setOperateType(OperateReqResp.type_gang);
        ResponseVo vo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_OTHER_OPERATE, operateReqResp);

        if (lastOperateUserId == userId) {
            //杠手里的牌
            if ("".equals(card)) {
                System.err.println("=======================杠牌时 没传数据========");
            }
            if (!playerCardsInfo.isCanGangThisCard(card)) {
                return ErrorCode.CAN_NOT_GANG;
            }
            int gangType = CardTypeUtil.getTypeByCard(card);
            boolean isMing = playerCardsInfo.getPengType().containsKey(gangType);
            //通知别人有杠
            //回放
            operateReqResp.setFromUserId(userId);
            operateReqResp.setUserId(userId);
            operateReqResp.setCard(card);
            operateReqResp.setIsMing(isMing);
            replay.getOperate().add(operateReqResp);
            //通知所有人有杠
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
                        boolean isWait = isCanHu;
                        if (isWait) {
                            add2WaitingList(entry.getKey(), isCanHu, false, false, false, false, false);
                        }
                    }

                    //可能的操作
                    ResponseVo OperateVo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_OPERATE, operateResp);
                    MsgSender.sendMsg2Player(OperateVo, entry.getKey());
                }

                //todo 多个截杠胡 是否一炮多响
            }

            if (this.waitingforList.size() == 0) {
                doGang_hand(playerCardsInfo, userId, card);
            } else {
                //排序
                compare(waitingforList);
                //出现截杠胡
                this.beJieGangUser = userId;
                this.jieGangHuCard = card;
            }


        } else {
            if (disCard == null || !playerCardsInfo.canBeGang) {
                return ErrorCode.CAN_NOT_GANG;
            }
            boolean isCanGang = playerCardsInfo.isCanGangAddThisCard(disCard);
            if (!isCanGang) {
                return ErrorCode.CAN_NOT_GANG;
            }
            handleWait(userId, WaitDetail.gangPoint);
            lastOperateUserId = userId;
        }


        return 0;


    }

    protected void doGang_hand(PlayerCardsInfoMj playerCardsInfo, long userId, String card) {
        boolean isMing = playerCardsInfo.gang_hand(room, this, userId, card);
        if (isMing) {
            userId = -1;
        }
        playerCardsInfo.gangCompute(room, this, isMing, userId, card);
        mopai(playerCardsInfo.getUserId(), "杠后摸牌");
        turnId = playerCardsInfo.getUserId();
        lastOperateUserId = playerCardsInfo.getUserId();
    }

    protected void doGang(PlayerCardsInfoMj playerCardsInfo, long userId) {
        OperateReqResp operateReqResp = new OperateReqResp();
        operateReqResp.setOperateType(OperateReqResp.type_gang);
        ResponseVo vo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_OTHER_OPERATE, operateReqResp);
        //删除弃牌
        deleteDisCard(lastPlayUserId, disCard);

        //杠
        playerCardsInfo.gang_discard(room, this, lastPlayUserId, disCard);

        operateReqResp.setFromUserId(lastPlayUserId);//谁出的牌
        operateReqResp.setUserId(userId);
        operateReqResp.setCard(disCard);
        operateReqResp.setIsMing(true);
        //回放
        replay.getOperate().add(operateReqResp);

        //通知所有人有杠
        MsgSender.sendMsg2Player(vo, users);

        //摸牌
        mopai(userId, "杠后摸牌 点杠");
        turnId = userId;
        this.disCard = null;
        lastOperateUserId = userId;

    }

    /**
     * 碰牌
     *
     * @param userId
     * @return
     */
    public int peng(long userId) {
        if (isAlreadyHu) {
            return ErrorCode.CAN_NOT_PENG;
        }
        PlayerCardsInfoMj playerCardsInfo = playerCardsInfos.get(userId);
        if (playerCardsInfo == null) {
            return ErrorCode.USER_ERROR;
        }
        if (this.disCard == null) {
            return ErrorCode.CAN_NOT_PENG;
        }
        boolean isCan = playerCardsInfo.isCanPengAddThisCard(this.disCard);
        if (isCan) {
            handleWait(userId, WaitDetail.pengPoint);
        } else {
            return ErrorCode.CAN_NOT_PENG;
        }
        return 0;
    }

    protected void doPeng(PlayerCardsInfoMj playerCardsInfo, long userId) {
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
        playerCardsInfo.canBeHu = false;
        resetOtherOperate(userId);
    }


    /**
     * 过
     *
     * @param userId
     */
    public int guo(long userId) {
        if (isAlreadyHu) {
            return ErrorCode.CAN_NOT_GUO;
        }
        //过胡逻辑
        if (playerCardsInfos.get(userId).isCanHu_dianpao(disCard)) {
            playerCardsInfos.get(userId).setGuoHu(true);
        }
        if (waitingforList.size() > 0) {

            List<WaitDetail> removeList = new ArrayList<>();
            for (WaitDetail waitDetail : waitingforList) {
                if (waitDetail.myUserId == userId) {
                    removeList.add(waitDetail);
                }
            }
            //todo 去掉可以操作集合
            resetCanBeOperate(playerCardsInfos.get(userId));
            waitingforList.removeAll(removeList);

            if (this.waitingforList.size() == 0) {
                //截杠胡
                if (jieGangHuCard != null) {
                    //截的 都是碰的明杠
                    doGang_hand(playerCardsInfos.get(beJieGangUser), -1, jieGangHuCard);
                    beJieGangUser = -1;
                    jieGangHuCard = null;
                } else {
                    //告诉全部点过
                    pushAllPass();
                    long nextId = nextTurnId(turnId);
                    //下个人摸牌
                    mopai(nextId, "过后摸牌");
                }
            } else {
                WaitDetail waitDetail = this.waitingforList.get(0);
                if (waitDetail.operate != -1) {
                    waitDetail.fire();
                    waitingforList.clear();
                }
            }

        }


        return 0;
    }

    protected void pushAllPass() {
        Map map = new HashMap();
        map.put("isAllPass", true);
        map.put("lastPlayUser", lastPlayUserId);
        ResponseVo responseVo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_ALL_PASS, map);
        MsgSender.sendMsg2Player(responseVo, users);
    }

    /**
     * 听
     *
     * @param userId
     * @param card
     * @return
     */
    public int ting(long userId, String card) {
        if (isAlreadyHu) {
            return ErrorCode.CAN_NOT_TING;
        }
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
            //听
            playerCardsInfo.ting(card);
            //通知其他玩家听
            MsgSender.sendMsg2Player(vo, users);

            //回放
            replay.getOperate().add(operateReqResp);

            //通知其他玩家出牌信息
//            PlayCardResp playCardResp = new PlayCardResp();
//            playCardResp.setUserId(userId);
//            playCardResp.setCard(card);
//
//            ResponseVo chupaiVo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_PLAY_CARD, playCardResp);
//            MsgSender.sendMsg2Player(chupaiVo.toJsonObject(), users);

            //出牌
            chuPai_ting(playerCardsInfo.getUserId(), card);
        } else {
            return ErrorCode.CAN_NOT_TING;
        }
        return 0;
    }

    /**
     * 胡牌
     *
     * @param userId
     * @return
     */
    public int hu(long userId) {
        if (isAlreadyHu) {
            return ErrorCode.CAN_NOT_HU_ALREADY;
        }
        PlayerCardsInfoMj playerCardsInfo = playerCardsInfos.get(userId);
        if (playerCardsInfo == null) {
            return ErrorCode.USER_ERROR;
        }

        //回放
        OperateReqResp operateReqResp = new OperateReqResp();
        operateReqResp.setUserId(userId);
        operateReqResp.setOperateType(OperateReqResp.type_hu);

        if (lastOperateUserId == userId) {//自摸
            if (playerCardsInfo.isCanHu_zimo(catchCard)) {
                setBanker(userId);
                playerCardsInfo.hu_zm(room, this, catchCard);
                //回放
                replay.getOperate().add(operateReqResp);
                handleHu(playerCardsInfo);

            } else {
                return ErrorCode.CAN_NOT_HU;
            }
        } else {

            if (this.disCard == null && jieGangHuCard == null) {
                return ErrorCode.CAN_NOT_HU;
            }
            String card = this.disCard;
            //
            if (jieGangHuCard != null) {
                card = jieGangHuCard;
            }

            if (playerCardsInfo.isCanHu_dianpao(card)) {

                handleWait(userId, WaitDetail.huPoint);
            } else {
                return ErrorCode.CAN_NOT_HU;
            }
        }


        return 0;

    }

    protected void doHu(PlayerCardsInfoMj playerCardsInfo, long userId) {
        OperateReqResp operateReqResp = new OperateReqResp();
        operateReqResp.setUserId(userId);
        operateReqResp.setOperateType(OperateReqResp.type_hu);

        setBanker(userId);

        if (jieGangHuCard != null) {
            //截杠胡
            playerCardsInfo.setJieGangHu(true);
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


    public int chi(long userId, String one, String two) {
        if (isAlreadyHu) {
            return ErrorCode.CAN_NOT_CHI;
        }
        PlayerCardsInfoMj playerCardsInfo = playerCardsInfos.get(userId);
        if (disCard == null) {
            return ErrorCode.CAN_NOT_CHI;
        }
        boolean isCanChi = playerCardsInfo.isCanChiThisCard(disCard, one, two);
        if (!isCanChi) {
            return ErrorCode.CAN_NOT_CHI;
        }

        handleWait(userId, WaitDetail.chiPoint, one, two);
        return 0;
    }

    protected void doChi(PlayerCardsInfoMj playerCardsInfo, long userId, String one, String two) {
        deleteDisCard(lastPlayUserId, disCard);
        List<String> chiCards = new ArrayList<>();
        chiCards.add(one);
        chiCards.add(disCard);
        chiCards.add(two);
        OperateReqResp operateReqResp = new OperateReqResp();
        operateReqResp.setOperateType(OperateReqResp.type_chi);
        operateReqResp.setUserId(userId);
        operateReqResp.setChiCards(chiCards);
        operateReqResp.setFromUserId(lastPlayUserId);

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
        playerCardsInfo.canBeHu = false;

        resetOtherOperate(userId);
    }

    public int chiTing(long userId, String one, String two) {
        if (isAlreadyHu) {
            return ErrorCode.CAN_NOT_CHI;
        }
        PlayerCardsInfoMj playerCardsInfo = playerCardsInfos.get(userId);
        if (disCard == null) {
            return ErrorCode.CAN_NOT_CHI_TING;
        }
        boolean isCanChi = playerCardsInfo.isCanChiThisCard(disCard, one, two);
        if (!isCanChi) {
            return ErrorCode.CAN_NOT_CHI_TING;
        }

        handleWait(userId, WaitDetail.chiTingPoint, one, two);
        return 0;
    }

    protected void doChiTing(PlayerCardsInfoMj playerCardsInfo, long userId, String one, String two) {
        deleteDisCard(lastPlayUserId, disCard);
        List<String> chiCards = new ArrayList<>();
        chiCards.add(one);
        chiCards.add(disCard);
        chiCards.add(two);
        OperateReqResp operateReqResp = new OperateReqResp();
        operateReqResp.setOperateType(OperateReqResp.type_chi_ting);
        operateReqResp.setUserId(userId);
        operateReqResp.setChiCards(chiCards);
        operateReqResp.setFromUserId(lastPlayUserId);
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
        operateResp.setIsCanTing(false);//客户端特殊处理
        operateResp.setIsCanGang(false);
        ResponseVo operateVo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_OPERATE, operateResp);
        MsgSender.sendMsg2Player(operateVo, userId);
        this.disCard = null;
        //自己将能做的处理
        playerCardsInfo.canBeChi = false;
        playerCardsInfo.canBeTing = true;
        playerCardsInfo.canBeChiTing = false;
        playerCardsInfo.canBeGang = false;

        resetOtherOperate(userId);
    }

    public int pengTing(long userId) {
        if (isAlreadyHu) {
            return ErrorCode.CAN_NOT_PENG_TING;
        }
        PlayerCardsInfoMj playerCardsInfo = playerCardsInfos.get(userId);

        if (disCard == null) {
            return ErrorCode.CAN_NOT_PENG_TING;
        }
        boolean isCan = playerCardsInfo.isCanPengTing(this.disCard);
        if (!isCan) {
            return ErrorCode.CAN_NOT_PENG_TING;
        }

        handleWait(userId, WaitDetail.pengTingPoint);


        return 0;
    }

    protected void doPengTing(PlayerCardsInfoMj playerCardsInfo, long userId) {
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
        MsgSender.sendMsg2Player(vo, users);


        //碰完能听,杠,不能胡
        turnId = userId;
        // 通知客户端 操作
        OperateResp operateResp = new OperateResp();
        operateResp.setIsCanTing(false);//客户端特殊处理
        operateResp.setIsCanGang(false);
        ResponseVo operateVo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_OPERATE, operateResp);
        MsgSender.sendMsg2Player(operateVo, userId);
        this.disCard = null;
        //自己将能做的处理
        playerCardsInfo.canBePeng = false;
        playerCardsInfo.canBeTing = true;
        playerCardsInfo.canBePengTing = false;
        playerCardsInfo.canBeGang = false;

        resetOtherOperate(userId);
    }

    protected void handleWait(long userId, int operateType, String... params) {
        List<WaitDetail> removeList = new ArrayList<>();
        for (WaitDetail waitDetail1 : waitingforList) {
            if (waitDetail1.myUserId == userId) {
                if (waitDetail1.getPoint() == operateType) {
                    waitDetail1.operate(this, operateType, params);
                } else {
                    removeList.add(waitDetail1);
                }
            }
        }
        waitingforList.removeAll(removeList);
        if (waitingforList.size() > 0) {
            WaitDetail waitDetail = waitingforList.get(0);
            if (waitDetail.operate != -1) {
                waitDetail.fire();
                waitingforList.clear();
            }

        } else {

        }

    }

}
