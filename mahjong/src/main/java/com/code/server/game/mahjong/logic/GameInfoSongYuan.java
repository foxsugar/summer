package com.code.server.game.mahjong.logic;



import com.code.server.game.mahjong.response.*;
import com.code.server.game.mahjong.util.HuType;
import com.code.server.game.room.MsgSender;

import java.util.*;

/**
 * Created by T420 on 2016/11/30.
 */

public class GameInfoSongYuan extends GameInfo {

    private static final int BAO_RAND_NUM = 12;
    private List<String> playCards = new ArrayList<>();


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
        remainCards.removeAll(CardTypeUtil.FENG_CARD);
        remainCards.removeAll(CardTypeUtil.ZI_CARD);
        //加入红中
        remainCards.add("124");
        remainCards.add("125");
        remainCards.add("126");
        remainCards.add("127");

        System.out.println("发牌的数量 : "+remainCards.size());

    }


    private void initBao() {

    }

    private void pushBaoChange(String... bao) {
        System.out.println("===========换宝================ " + baoCard);
        Map<String, String> map = new HashMap<>();
        map.put("baoCard", baoCard);
        map.put("changeBaoCount", ""+changeBaoSize);
        if (bao.length > 0) {
            map.put("baoCard", bao[0]);
        }
        MsgSender.sendMsg2Player(new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_CHANGE_BAO, map).toJsonObject(), users);

    }

    /**
     * 荒庄的处理
     *
     * @param userId
     */
    protected void handleHuangzhuang(long userId) {
        computeAllGang();
        sendResult(false, userId);
//        room.addOneToCircleNumber();
//        int nextId = nextTurnId(this.getFirstTurn());
//        room.setBankerId(nextId);
        noticeDissolutionResult();
        //通知所有玩家结束
        room.clearReadyStatus();

    }

    /**
     * 换宝牌
     */
    public void changeBao() {

        initBao();
        int size = remainCards.size();
        if (changeBaoSize > 6 || size < BAO_RAND_NUM||isAlreadyHu) {
//            if (baoType != -1) {
//                baoType = -1;
//                baoCard = null;
//                pushBaoChange(serverContext);
//            }
            return;
        }

        changeBaoSize++;
        Random rand = new Random();
        if (size <= BAO_RAND_NUM || changeBaoSize > 6) {
            this.baoType = -1;
            this.baoCard = null;
            pushBaoChange();

        } else {
            int r = rand.nextInt(BAO_RAND_NUM);
            int index = size - 1 - r;
            String bao = remainCards.get(index);

            this.baoCard = bao;
            this.baoType = CardTypeUtil.cardType.get(bao);
            pushBaoChange();
        }
    }


    private void handleHaidilao(long userId, List<Long> huList) {
        if (remainCards.size() > 0 && huList.size()==0) {

            String card = remainCards.remove(0);
            PlayerCardsInfo playerCardsInfo = playerCardsInfos.get(userId);
            boolean isCanHu = playerCardsInfo.isCanHu_zimo(card);
            if (isCanHu) {
                playerCardsInfo.mopai(card);
                playerCardsInfo.winType.add(HuType.hu_海底捞);
                playerCardsInfo.hu_zm(room, this, card);
                huList.add(playerCardsInfo.getUserId());
            } else {
                long nextUser = nextTurnId(userId);
                handleHaidilao(nextUser, huList);
            }


        }
    }

    /**
     * 摸牌
     *
     * @param userId
     */
    @Override
    protected void mopai(long userId, String... wz) {
        if (isAlreadyHu) {
            return;
        }
        System.err.println("摸牌=============================== : "+userId);
        //还剩最后四张牌
        List<Long> huList = new ArrayList<>();
        if (remainCards.size() == users.size()) {
            System.out.println("处理海底捞");
            //海底捞
            handleHaidilao(userId, huList);
        }
        if (huList.size() > 0) {
            handleHu(playerCardsInfos.get(huList.get(0)));
            return;
        }
        //荒庄
        PlayerCardsInfo playerCardsInfo = playerCardsInfos.get(userId);
        if (playerCardsInfo.isHuangzhuang(this)) {
            handleHuangzhuang(userId);
            return;
        }

        if (playerCardsInfo.isMoreOneCard()) {
            if (wz.length > 0) {
                logger.info("====1操作后的摸牌 : "+wz[0]);
            }
            logger.info("===1 more one card 抓牌时多一张牌");
            logger.info("操作列表: "+playerCardsInfo.operateList.toString());

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
        for (long user : users) {
            GetCardResp getCardResp = new GetCardResp();
            getCardResp.setRemainNum(remainSize);
            getCardResp.setUserId(userId);
            if (user == userId) {
                getCardResp.setCard(card);
            }
            ResponseVo responseVo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_GET_CARD, getCardResp);
            MsgSender.sendMsg2Player(responseVo.toJsonObject(), user);

            //能做的操作全置成不能
            PlayerCardsInfo other = playerCardsInfos.get(user);

            resetCanBeOperate(other);
        }

        //已经胡了
        if (playerCardsInfo.isAlreadyHu) {
            playerCardsInfo.hu_zm(room, this, card);
            handleHu(playerCardsInfo);
            return;
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
        MsgSender.sendMsg2Player(OperateResponseVo.toJsonObject(), userId);

    }

    protected void handleHu(PlayerCardsInfo playerCardsInfo) {
        isAlreadyHu = true;
        sendResult(true, playerCardsInfo.userId);
        //圈
        if (this.getFirstTurn() != playerCardsInfo.getUserId()) {
            //换庄
            room.addOneToCircleNumber();
            long nextId = nextTurnId(this.getFirstTurn());
            room.setBankerId(nextId);
        }
        noticeDissolutionResult();
        room.clearReadyStatus();
    }


    private int chuPai_ting(long userId, String card) {
        //出牌的玩家
        PlayerCardsInfo chupaiPlayerCardsInfo = playerCardsInfos.get(userId);
        if (this.turnId != userId||isAlreadyHu) {
            return ErrorCode.CAN_NOT_PLAYCARD;
        }

        playCards.add(card);
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
        MsgSender.sendMsg2Player(vo.toJsonObject(), users);

        //是否换宝
        if (changeBaoSize > 0 && baoCard != null && getPlayCardNum(baoCard) >= 3) {
            changeBao();

            CheckInfo checkInfo = new CheckInfo();
            checkZhiDui(chupaiPlayerCardsInfo, checkInfo);
            if (checkInfo.huUser != -1) {
                handleHu(playerCardsInfos.get(checkInfo.huUser));
                return 0;
            }
        }

        //其他人能做的操作
        for (Map.Entry<Long, PlayerCardsInfo> entry : playerCardsInfos.entrySet()) {
            OperateResp operateResp = new OperateResp();

            //其他玩家的处理 碰杠等 如果有加入等待列表(要等待这些玩家"过")
            if (userId != entry.getKey()) {
                //通知其他玩家出了什么牌 自己能有什么操作
                PlayerCardsInfo playerCardsInfo = entry.getValue();
                boolean isCanGang = playerCardsInfo.isCanGangAddThisCard(card);
                boolean isCanPeng = playerCardsInfo.isCanPengAddThisCard(card);
                boolean isCanHu = playerCardsInfo.isCanHu_dianpao(card);
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
//                    this.waitingforList.add(new WaitDetail(entry.getKey(), isCanHu, isCanGang, isCanPeng, isCanChi, isCanChiTing, isCanPengTing));
                    add2WaitingList(entry.getKey(), isCanHu, isCanGang, isCanPeng, isCanChi, isCanChiTing, isCanPengTing);
                }
            }

            //可能的操作
            ResponseVo OperateVo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_OPERATE, operateResp);
            MsgSender.sendMsg2Player(OperateVo.toJsonObject(), entry.getKey());
        }

        resetCanBeOperate(chupaiPlayerCardsInfo);


        //如果等待列表为空 就轮到下个人摸牌
        if (this.waitingforList.size() == 0) {
            pushAllPass();
            long nextId = nextTurnId(turnId);
            mopai(nextId,"听后出牌");
        } else {
            //比较
            compare(waitingforList);
        }
        return 0;

    }

    /**
     * 出牌
     * @param userId
     * @param card
     */
    public int chupai(long userId, String card) {
        if (isAlreadyHu) {
            return ErrorCode.CAN_PLAYCARD_IS_HU;
        }
        System.out.println("出牌的人 = " + userId);
        //出牌的玩家
        PlayerCardsInfo chupaiPlayerCardsInfo = playerCardsInfos.get(userId);
        if (this.turnId != userId) {
            return ErrorCode.CAN_NOT_PLAYCARD;
        }
        if (!chupaiPlayerCardsInfo.checkPlayCard(card)) {
            return ErrorCode.CAN_NOT_PLAYCARD;
        }
        playCards.add(card);
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
        MsgSender.sendMsg2Player(vo.toJsonObject(), users);

        //是否换宝
        if (changeBaoSize > 0 && baoCard != null && getPlayCardNum(baoCard) >= 3) {
            changeBao();

            CheckInfo checkInfo = new CheckInfo();
            checkZhiDui(chupaiPlayerCardsInfo, checkInfo);
            if (checkInfo.huUser != -1) {
                handleHu(playerCardsInfos.get(checkInfo.huUser));
                return 0;
            }
        }

        //其他人能做的操作
        for (Map.Entry<Long, PlayerCardsInfo> entry : playerCardsInfos.entrySet()) {
            OperateResp operateResp = new OperateResp();

            //其他玩家的处理 碰杠等 如果有加入等待列表(要等待这些玩家"过")
            if (userId != entry.getKey()) {
                //通知其他玩家出了什么牌 自己能有什么操作
                PlayerCardsInfo playerCardsInfo = entry.getValue();
                boolean isCanGang = playerCardsInfo.isCanGangAddThisCard(card);
                boolean isCanPeng = playerCardsInfo.isCanPengAddThisCard(card);
                boolean isCanHu = playerCardsInfo.isCanHu_dianpao(card);
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
//                    this.waitingforList.add(new WaitDetail(entry.getKey(), isCanHu, isCanGang, isCanPeng, isCanChi, isCanChiTing, isCanPengTing));
                    add2WaitingList(entry.getKey(), isCanHu, isCanGang, isCanPeng, isCanChi, isCanChiTing, isCanPengTing);
                }
            }

            //可能的操作
            ResponseVo OperateVo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_OPERATE, operateResp);
            MsgSender.sendMsg2Player(OperateVo.toJsonObject(),  entry.getKey());
        }

        resetCanBeOperate(chupaiPlayerCardsInfo);


        System.out.println("======= waiting list : "+waitingforList.size());
        //如果等待列表为空 就轮到下个人摸牌
        if (this.waitingforList.size() == 0) {
            pushAllPass();
            long nextId = nextTurnId(turnId);
            mopai(nextId,"出牌");
        } else {
            //比较
            compare(waitingforList);
        }
        return 0;

    }

    private void add2WaitingList(long userId, boolean isHu, boolean isGang, boolean isPeng, boolean isChi, boolean isChiTing, boolean isPengTing) {
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
        PlayerCardsInfo playerCardsInfo = playerCardsInfos.get(userId);
        if (playerCardsInfo == null) {
            return ErrorCode.USER_ERROR;
        }
        OperateReqResp operateReqResp = new OperateReqResp();
        operateReqResp.setOperateType(OperateReqResp.type_gang);
        ResponseVo vo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_OTHER_OPERATE, operateReqResp);

        if (lastOperateUserId == userId) {//杠手里的牌
            if ("".equals(card)) {
                System.err.println("=======================杠牌时 没传数据========");
            }
            if(!playerCardsInfo.isCanGangThisCard(card)){
                return ErrorCode.CAN_NOT_GANG;
            }
            int gangType = CardTypeUtil.getTypeByCard(card);
            boolean isMing = playerCardsInfo.getPengType().containsKey(gangType);
            //自摸不吃牌
            operateReqResp.setFromUserId(userId);
            operateReqResp.setUserId(userId);
            operateReqResp.setCard(card);
            operateReqResp.setIsMing(isMing);
            //通知所有人有杠
            MsgSender.sendMsg2Player(vo.toJsonObject(), users);


            if (isMing) {

                for (Map.Entry<Long, PlayerCardsInfo> entry : playerCardsInfos.entrySet()) {
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
                        boolean isWait = isCanHu;
                        if (isWait) {
                            add2WaitingList(entry.getKey(), isCanHu, false, false, false, false, false);
                        }
                    }

                    //可能的操作
                    ResponseVo OperateVo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_OPERATE, operateResp);
                    MsgSender.sendMsg2Player(OperateVo.toJsonObject(), entry.getKey());
                }
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
            if (disCard == null) {
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

    protected void doGang_hand(PlayerCardsInfo playerCardsInfo, long userId, String card){
        playerCardsInfo.gang_hand(room, this, userId, card);
        mopai(playerCardsInfo.getUserId(),"杠后摸牌");
        turnId = playerCardsInfo.getUserId();
        lastOperateUserId = playerCardsInfo.getUserId();
    }

    protected void doGang(PlayerCardsInfo playerCardsInfo, long userId) {
        OperateReqResp operateReqResp = new OperateReqResp();
        operateReqResp.setOperateType(OperateReqResp.type_gang);
        ResponseVo vo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_OTHER_OPERATE, operateReqResp);
        //删除弃牌
        deleteDisCard(lastPlayUserId, disCard);

        playerCardsInfo.gang_discard(room, this, lastPlayUserId, disCard);
        operateReqResp.setFromUserId(lastPlayUserId);//谁出的牌

        operateReqResp.setUserId(userId);
        operateReqResp.setCard(disCard);
        operateReqResp.setIsMing(true);
        //通知所有人有杠
        MsgSender.sendMsg2Player(vo.toJsonObject(), users);

        mopai(userId,"杠后摸牌 点杠");
        turnId = userId;
        this.disCard = null;
        lastOperateUserId = userId;

        resetOtherOperate(userId);
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
        PlayerCardsInfo playerCardsInfo = playerCardsInfos.get(userId);
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

    protected void doPeng(PlayerCardsInfo playerCardsInfo, long userId){
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
        MsgSender.sendMsg2Player(vo.toJsonObject(), users);


        //碰完能听,杠,不能胡
        boolean isCanTing = playerCardsInfo.isCanTing(playerCardsInfo.cards);//多一张牌
        boolean isCanGang = playerCardsInfo.isHasGang();
        turnId = userId;
        // 通知客户端 操作
        OperateResp operateResp = new OperateResp();
        operateResp.setIsCanTing(isCanTing);
        operateResp.setIsCanGang(isCanGang);
        ResponseVo operateVo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_OPERATE, operateResp);
        MsgSender.sendMsg2Player(operateVo.toJsonObject(),userId);
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
                    doGang_hand(playerCardsInfos.get(turnId),turnId,jieGangHuCard);
                    beJieGangUser = -1;
                    jieGangHuCard = null;
                } else {
                    //告诉全部点过
                    pushAllPass();
                    long nextId = nextTurnId(turnId);
                    //下个人摸牌
                    mopai(nextId,"过后摸牌");
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

    private void pushAllPass(){
        Map map = new HashMap();
        map.put("isAllPass", true);
        map.put("lastPlayUser", lastPlayUserId);
        ResponseVo responseVo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_ALL_PASS, map);
        MsgSender.sendMsg2Player(responseVo.toJsonObject(),users);
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
            //听
            playerCardsInfo.ting(card);
            //通知其他玩家听
            MsgSender.sendMsg2Player(vo.toJsonObject(), users);


            //通知其他玩家出牌信息
//            PlayCardResp playCardResp = new PlayCardResp();
//            playCardResp.setUserId(userId);
//            playCardResp.setCard(card);
//
//            ResponseVo chupaiVo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_PLAY_CARD, playCardResp);
//            MsgSender.sendMsg2Player(chupaiVo.toJsonObject(), users);

            //第一次换宝
            if (changeBaoSize == 0) {
                changeBao();
            }
            //检测直对
            CheckInfo checkInfo = new CheckInfo();
            checkZhiDui(playerCardsInfo, checkInfo);
            //有人胡了 退出
            if (checkInfo.huUser != -1) {
                playerCardsInfo.chupai(card);
                handleHu(playerCardsInfos.get(checkInfo.huUser));
                return 0;
            }

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
        PlayerCardsInfo playerCardsInfo = playerCardsInfos.get(userId);
        if (playerCardsInfo == null) {
            return ErrorCode.USER_ERROR;
        }
        if (lastOperateUserId == userId) {//自摸
            if (playerCardsInfo.isCanHu_zimo(catchCard)) {
                playerCardsInfo.hu_zm(room, this, catchCard);
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

    protected void doHu(PlayerCardsInfo playerCardsInfo, long userId){
        if (jieGangHuCard != null) {
            playerCardsInfo.hu_dianpao(room, this, beJieGangUser, jieGangHuCard);
            playerCardsInfos.get(beJieGangUser).cards.remove(jieGangHuCard);
            beJieGangUser = -1;
            jieGangHuCard = null;
        } else {
            //删除弃牌
            deleteDisCard(lastPlayUserId, disCard);
            playerCardsInfo.hu_dianpao(room, this, lastPlayUserId, disCard);
            this.disCard = null;
        }
        handleHu(playerCardsInfo);
    }

    protected boolean isRoomOver() {
        return room.getCurCircle() > room.maxCircle;
    }

    public int chi(long userId, String one, String two) {
        if (isAlreadyHu) {
            return ErrorCode.CAN_NOT_CHI;
        }
        PlayerCardsInfo playerCardsInfo = playerCardsInfos.get(userId);
        if (disCard == null) {
            return ErrorCode.CAN_NOT_CHI;
        }
        boolean isCanChi = playerCardsInfo.isCanChiThisCard(disCard, one, two);
        if (!isCanChi) {
            return ErrorCode.CAN_NOT_CHI;
        }

        handleWait(userId, WaitDetail.chiPoint,one,two);
        return 0;
    }

    protected void doChi(PlayerCardsInfo playerCardsInfo, long userId, String one, String two) {
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
        MsgSender.sendMsg2Player(vo.toJsonObject(), users);


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
        MsgSender.sendMsg2Player(operateVo.toJsonObject(), userId);
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
        PlayerCardsInfo playerCardsInfo = playerCardsInfos.get(userId);
        if (disCard == null) {
            return ErrorCode.CAN_NOT_CHI_TING;
        }
        boolean isCanChi = playerCardsInfo.isCanChiThisCard(disCard, one, two);
        if (!isCanChi) {
            return ErrorCode.CAN_NOT_CHI_TING;
        }

        handleWait(userId, WaitDetail.chiTingPoint,one,two);
        return 0;
    }

    protected void doChiTing(PlayerCardsInfo playerCardsInfo, long userId, String one, String two) {
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
        MsgSender.sendMsg2Player(vo.toJsonObject(), users);

        //吃
        playerCardsInfo.chi(disCard, one, two);

        //吃完只能听
        turnId = userId;
        // 通知客户端 操作
        OperateResp operateResp = new OperateResp();
        operateResp.setIsCanTing(false);//客户端特殊处理
        operateResp.setIsCanGang(false);
        ResponseVo operateVo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_OPERATE, operateResp);
        MsgSender.sendMsg2Player(operateVo.toJsonObject(), userId);
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
        PlayerCardsInfo playerCardsInfo = playerCardsInfos.get(userId);

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

    protected void doPengTing(PlayerCardsInfo playerCardsInfo, long userId) {
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
        MsgSender.sendMsg2Player(vo.toJsonObject(), users);


        //碰完能听,杠,不能胡
        turnId = userId;
        // 通知客户端 操作
        OperateResp operateResp = new OperateResp();
        operateResp.setIsCanTing(false);//客户端特殊处理
        operateResp.setIsCanGang(false);
        ResponseVo operateVo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_OPERATE, operateResp);
        MsgSender.sendMsg2Player(operateVo.toJsonObject(), userId);
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
                    waitDetail1.operate(this,operateType, params);
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

    public static class CheckInfo {
        List<Long> userList = new ArrayList<>();
        boolean isOver = false;
        long huUser = -1;
        boolean isChangeBao = false;
    }

    private int getPlayCardNum(String card) {
        if(card == null){
            return 0;
        }
        int ct = CardTypeUtil.getTypeByCard(card);
        int count = 0;
        Set<String> cs = new HashSet<>();
        for (PlayerCardsInfo playerCardsInfo : playerCardsInfos.values()) {
            cs.addAll(playerCardsInfo.disCards);

            for (List<String> chiCards : playerCardsInfo.chiCards) {
                cs.addAll(chiCards);
            }
            for (int pengType : playerCardsInfo.pengType.keySet()) {
                if (ct == pengType) {
                    count += 3;
                }
            }
//            for (int anGangType : playerCardsInfo.anGangType) {
//                if (ct == anGangType) {
//                    count += 4;
//                }
//            }
            for (int mingGangType : playerCardsInfo.mingGangType.keySet()) {
                if (ct == mingGangType) {
                    count += 4;
                }
            }
        }
        for (String c : cs) {
            int type = CardTypeUtil.getTypeByCard(c);
            if (type == ct) {
                count++;
            }
        }
        return count;
    }

    protected void checkZhiDui(PlayerCardsInfo playerCardsInfo, CheckInfo checkInfo) {

        if (isAlreadyHu||changeBaoSize == 0 && this.baoType == -1 || checkInfo.isOver || checkInfo.userList.size() >= this.users.size() || checkInfo.huUser!=-1) {
            return;
        }
        System.out.println("检测直对");
        checkInfo.userList.add(playerCardsInfo.getUserId());

        if (playerCardsInfo.isTing) {

            if (playerCardsInfo.tingSet.contains(this.baoType)) {
                playerCardsInfo.winType.add(HuType.hu_直对);
                this.isAlreadyHu = true;
                playerCardsInfo.mopai(this.baoCard);
                //直接胡
                System.out.println("================== 直对胡牌=================");
                playerCardsInfo.hu_zm(room, this, this.baoCard);
                checkInfo.huUser = playerCardsInfo.getUserId();
                return;

            } else {
                //宝明蛋
                Set<Integer> keySet = new HashSet<>();
                keySet.addAll(playerCardsInfo.pengType.keySet());
                for (int pt : keySet) {
                    if (pt == this.baoType) {
//                        playerCardsInfo.mopai(baoCard);
                        playerCardsInfo.baoDan(baoCard);


                        playerCardsInfo.baoMingDan.add(pt);
                        playerCardsInfo.mingGangType.put(pt, -1L);
                        playerCardsInfo.pengType.remove(pt);
                        this.remainCards.remove(baoCard);
                        //蛋 推送
                        pushDan(playerCardsInfo.getUserId(), true);
                        //换宝
                        changeBao();
                        checkInfo.userList = new ArrayList<>();
                        checkInfo.isChangeBao = true;
                    }
                }
                //宝暗蛋
                List<String> noCPG = playerCardsInfo.getCardsNoChiPengGang(playerCardsInfo.cards);
                Map<Integer, Integer> cardNum = PlayerCardsInfo.getCardNum(noCPG);
                for (Map.Entry<Integer, Integer> entry : cardNum.entrySet()) {
                    if (entry.getKey() == this.baoType && entry.getValue() == 3) {
                        //加一个暗杠
//                        playerCardsInfo.mopai(baoCard);
                        playerCardsInfo.baoDan(baoCard);
                        playerCardsInfo.anGangType.add(this.baoType);
                        playerCardsInfo.baoAnDan.add(this.baoType);
                        this.remainCards.remove(this.baoCard);
                        //蛋 推送
                        pushDan(playerCardsInfo.getUserId(), false);
                        //换宝
                        changeBao();
                        checkInfo.userList = new ArrayList<>();
                        checkInfo.isChangeBao = true;

                    }
                }


            }
        }
        //next
        //换宝后再检测自己一次
        long nextId = playerCardsInfo.userId;
        if (!checkInfo.isChangeBao) {//没有换宝
            nextId = nextTurnId(playerCardsInfo.userId);
        } else {
            checkInfo.isChangeBao = false;
        }
        PlayerCardsInfo nextPlayerCardsInfo = playerCardsInfos.get(nextId);
        checkZhiDui(nextPlayerCardsInfo, checkInfo);


        //宝牌 三张都被抓出 换宝
        if (checkInfo.huUser==-1 && checkInfo.userList.size() == this.users.size() && getPlayCardNum(baoCard) >= 3) {
            checkInfo.userList = new ArrayList<>();
            changeBao();
            checkZhiDui(nextPlayerCardsInfo, checkInfo);
        }

    }

    private void pushTing(int userId) {
        //通知其他玩家
        OperateReqResp operateReqResp = new OperateReqResp();
        operateReqResp.setOperateType(OperateReqResp.type_peng_ting);
        operateReqResp.setCard(disCard);
        operateReqResp.setFromUserId(lastPlayUserId);
        operateReqResp.setUserId(userId);
        //通知其他人
        ResponseVo vo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_OTHER_OPERATE, operateReqResp);
        MsgSender.sendMsg2Player(vo.toJsonObject(), users);
    }

    private void pushDan(long userId, boolean isMing) {
        for (long id : users) {

            OperateReqResp operateReqResp = new OperateReqResp();
            operateReqResp.setUserId(userId);
            operateReqResp.setOperateType(OperateReqResp.type_dan);
            operateReqResp.setIsMing(isMing);
//            if (id != userId && !isMing) {//不是自己并且是暗的
//                operateReqResp.setCard(null);
//            } else {
            operateReqResp.setCard(baoCard);
//            }
            ResponseVo vo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_OTHER_OPERATE, operateReqResp);
            MsgSender.sendMsg2Player(vo.toJsonObject(), id);
        }
    }



}


