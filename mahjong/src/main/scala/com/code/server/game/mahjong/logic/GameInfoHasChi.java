package com.code.server.game.mahjong.logic;



import com.code.server.constant.response.ResponseVo;
import com.code.server.game.mahjong.response.*;
import com.code.server.game.room.kafka.MsgSender;

import java.util.*;

/**
 * Created by T420 on 2016/11/30.
 */

public class GameInfoHasChi extends GameInfoNew {


    private List<String> playCards = new ArrayList<>();


    /**
     * 荒庄的处理
     *
     * @param userId
     */
    protected void handleHuangzhuang(long userId) {
        computeAllGang();
        switch (this.room.modeTotal) {
            case "4":
                break;
            case "6":
                break;
            default:
                turnResultToZeroOnHuangZhuang();
        }
        sendResult(false, userId, null);
//        room.addOneToCircleNumber();
//        int nextId = nextTurnId(this.getFirstTurn());
//        room.setBankerId(nextId);
        noticeDissolutionResult();
        //通知所有玩家结束
        room.clearReadyStatus();

    }








    protected int chuPai_ting(long userId, String card) {
        //出牌的玩家
        PlayerCardsInfoMj chupaiPlayerCardsInfo = playerCardsInfos.get(userId);
        if (this.turnId != userId||isAlreadyHu) {
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
        playCardResp.setCard(null);
        ResponseVo vo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_PLAY_CARD, playCardResp);
        MsgSender.sendMsg2Player(vo, users);


        //其他人能做的操作
        for (Map.Entry<Long, PlayerCardsInfoMj> entry : playerCardsInfos.entrySet()) {
            OperateResp operateResp = new OperateResp();

            //其他玩家的处理 碰杠等 如果有加入等待列表(要等待这些玩家"过")
            if (userId != entry.getKey()) {
                //通知其他玩家出了什么牌 自己能有什么操作
                PlayerCardsInfoMj playerCardsInfo = entry.getValue();
                //设置返回结果
                operateResp.setCanBeOperate(false, false, false, false, false, false, false);

                //设置自己能做的操作
                playerCardsInfo.setCanBeOperate(false, false, false, false, false, false, false);

                boolean isWait = false;
                if (isWait) {
//                    this.waitingforList.add(new WaitDetail(entry.getKey(), isCanHu, isCanGang, isCanPeng, isCanChi, isCanChiTing, isCanPengTing));
                    add2WaitingList(entry.getKey(), false, false, false, false, false, false);
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
            mopai(nextId,"出牌");
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










}


