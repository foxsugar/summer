package com.code.server.game.mahjong.logic;

import com.code.server.constant.response.ResponseVo;
import com.code.server.game.mahjong.response.*;
import com.code.server.game.room.kafka.MsgSender;

import java.util.*;

/**
 * Created by T420 on 2016/11/30.
 */

public class GameInfoHM extends GameInfo {

    private Set<Long> noCanHuList = new HashSet<>();//本轮不能胡的人

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
        fapai();
    }

    protected void handleHu(PlayerCardsInfoMj playerCardsInfo) {
        isAlreadyHu = true;
        sendResult(true, playerCardsInfo.userId, null);
        //圈
        if (this.getFirstTurn() != playerCardsInfo.getUserId()) {
            //换庄
            room.addOneToCircleNumber();
            long nextId = nextTurnId(this.getFirstTurn());
            room.setBankerId(nextId);
        }
        noticeDissolutionResult();
        room.clearReadyStatus(true);
    }

    protected boolean isRoomOver() {
        return room.getCurCircle() > room.maxCircle;
    }

    /**
     * 荒庄的处理
     *
     * @param userId
     */
    protected void  handleHuangzhuang(long userId) {
        //黄庄有杠，下家坐庄；黄庄无杠，继续坐庄
        boolean b = false;
        a:for (Long l: playerCardsInfos.keySet()) {
            if(playerCardsInfos.get(l).anGangType.size()>0){
                b=true;
                break a;
            }
            if(playerCardsInfos.get(l).mingGangType.keySet().size()>0){
                b=true;
                break a;
            }
        }
        if(b){
            //换庄
            room.addOneToCircleNumber();
            long nextId = nextTurnId(this.getFirstTurn());
            room.setBankerId(nextId);
        }

        computeAllGang();
        sendResult(false, userId, null);
        noticeDissolutionResult();
        room.clearReadyStatus(true);
    }

    /**
     * 过
     *
     * @param userId
     */
    public int guo(long userId) {

        PlayerCardsInfoHM guoPlayerCardsInfo = (PlayerCardsInfoHM)playerCardsInfos.get(userId);
        if(guoPlayerCardsInfo.isCanHu_dianpao(disCard)){//能胡点过的人，这一轮不能胡
            noCanHuList.add(userId);
        }

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
        ResponseVo vo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_PLAY_CARD, playCardResp);
        MsgSender.sendMsg2Player(vo, users);

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
                if("HM".equals(this.room.getGameType())){
                    //如果上一次操作为过，这一轮不能再碰和胡
                    if(!noCanHuList.contains(playerCardsInfo.getUserId())){
                        isCanHu = playerCardsInfo.isCanHu_dianpao(card);
                    }else{
                        isCanHu = false;
                    }
                }else{
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
            if (this.room.isYipaoduoxiang &&waitingforList.stream().filter(waitDetail -> waitDetail.isHu).count() >= 2) {
                handleYiPaoDuoXiang();
            } else {
                //比较
                compare(waitingforList);
            }
        }
        return 0;
    }


    /**
     * 摸牌
     *
     * @param userId
     */
    protected void mopai(long userId, String... wz) {
        System.err.println("摸牌===============================userId : " + userId);
        noCanHuList.remove(userId);

        PlayerCardsInfoMj playerCardsInfo = playerCardsInfos.get(userId);
        playerCardsInfo.setGuoHu(false);
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
//        if (isTest && playerCardsInfo.nextNeedCard != -1) {
//            String needCard = getCardByTypeFromRemainCards(playerCardsInfo.nextNeedCard);
//            playerCardsInfo.nextNeedCard = -1;
//            if (needCard != null) {
//                card = needCard;
//                remainCards.remove(needCard);
//            } else {
//                card = remainCards.remove(0);
//            }
//        } else {
//            card = remainCards.remove(0);
//        }

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
}
