package com.code.server.game.mahjong.logic;

import com.code.server.constant.response.ResponseVo;
import com.code.server.game.mahjong.response.*;
import com.code.server.game.room.kafka.MsgSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by sunxianping on 2018/1/12.
 */
public class GameInfoNiuyezi extends GameInfo {


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

    protected void handleHuangzhuang(long userId) {
        computeAllGang();
        sendResult(false, userId, null);
        noticeDissolutionResult();
        //通知所有玩家结束
        room.clearReadyStatus();
        //庄家换下个人
        if (room instanceof RoomInfo) {
            RoomInfo roomInfo = (RoomInfo) room;
            if (roomInfo.isChangeBankerAfterHuangZhuang()) {
                room.setBankerId(nextTurnId(room.getBankerId()));
            }

        }
    }


    @Override
    public int guo(long userId) {


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
            this.disCard = card;
            //听
            playerCardsInfo.ting(card);
            //通知其他玩家听
            MsgSender.sendMsg2Player(vo, users);

            //回放
            replay.getOperate().add(operateReqResp);

            //通知其他玩家出牌信息
            PlayCardResp playCardResp = new PlayCardResp();
            playCardResp.setUserId(userId);
            playCardResp.setCard(card);

            ResponseVo chupaiVo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_PLAY_CARD, playCardResp);
            MsgSender.sendMsg2Player(chupaiVo, users);

            //其他人的操作 全是false 听牌后什么都不能操作

            //其他人能做的操作
            for (Map.Entry<Long, PlayerCardsInfoMj> entry : playerCardsInfos.entrySet()) {
                OperateResp operateResp = new OperateResp();

                //其他玩家的处理 碰杠等 如果有加入等待列表(要等待这些玩家"过")
                if (userId != entry.getKey()) {
                    //通知其他玩家出了什么牌 自己能有什么操作
                    PlayerCardsInfoMj playerInfo = entry.getValue();
                    boolean isCanGang = playerInfo.isCanGangAddThisCard(card);
                    boolean isCanPeng = playerInfo.isCanPengAddThisCard(card);
                    boolean isCanHu;

                    isCanHu = playerInfo.isCanHu_dianpao(card);


                    boolean isCanChi = playerInfo.isHasChi(card);
                    boolean isCanChiTing = playerInfo.isCanChiTing(card);
                    boolean isCanPengTing = playerInfo.isCanPengTing(card);
                    //设置返回结果
                    operateResp.setCanBeOperate(isCanChi, isCanPeng, isCanGang, false, isCanHu, isCanChiTing, isCanPengTing);

                    //设置自己能做的操作
                    playerInfo.setCanBeOperate(isCanChi, isCanPeng, isCanGang, false, isCanHu, isCanChiTing, isCanPengTing);

                    boolean isWait = isCanGang || isCanPeng || isCanHu || isCanChi || isCanChiTing || isCanPengTing;
                    if (isWait) {
                        this.waitingforList.add(new WaitDetail(entry.getKey(), isCanHu, isCanGang, isCanPeng, isCanChi, isCanChiTing, isCanPengTing));
                    }
                }

                //可能的操作
                ResponseVo OperateVo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_OPERATE, operateResp);
                MsgSender.sendMsg2Player(OperateVo, entry.getKey());
            }

            resetCanBeOperate(playerCardsInfo);


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
        }
        return 0;
    }
}
