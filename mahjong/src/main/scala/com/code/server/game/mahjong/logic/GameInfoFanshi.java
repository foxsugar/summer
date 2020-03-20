package com.code.server.game.mahjong.logic;

import com.code.server.constant.response.ResponseVo;
import com.code.server.game.mahjong.response.*;
import com.code.server.game.room.kafka.MsgSender;

import java.util.List;
import java.util.Map;

/**
 * Created by sunxianping on 2018-10-08.
 */
public class GameInfoFanshi extends GameInfoNew {


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
        remainCards.removeAll(CardTypeUtil.FENG_CARD);
        remainCards.removeAll(CardTypeUtil.ZI_CARD);
        this.users.addAll(users);
        this.room = room;
        this.cardSize = 13;
        this.playerSize = room.getPersonNumber();
        //不带风
        fapai();
    }

    @Override
    protected void mopai(long userId, String... wz) {
        //如果剩最后一张 询问是否摸牌
        if (remainCards.size() == 1) {
            ResponseVo responseVo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, "lastCardIsCatch", 0);
            MsgSender.sendMsg2Player(responseVo, userId);
            this.setLastCatchCardUser(userId);
        }else{
            super.mopai(userId, wz);
        }
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

                boolean isCanPeng = playerCardsInfo.isCanPengAddThisCard(card) && this.remainCards.size()>0;
                if (isHasGuoPeng()) {
                    isCanPeng = isCanPeng && !playerCardsInfo.isGuoPeng(card);
                }
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

    public int fanshiGetCard(long userId,boolean isGet) {

        if (userId != this.getLastCatchCardUser()) {
            return ErrorCode.NOT_TURN;
        }
        if (isGet) {
            super.mopai(userId);
        } else {
            handleHuangzhuang(userId);
        }
        this.setLastCatchCardUser(0);
        ResponseVo responseVo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, "fanshiGetCard", 0);
        MsgSender.sendMsg2Player(responseVo, userId);
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
        boolean isCanGang = false;
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
     * 设置庄家
     *
     * @param winnerId
     */
    public void setBanker(long winnerId) {
        room.setBankerId(winnerId);
    }

}
