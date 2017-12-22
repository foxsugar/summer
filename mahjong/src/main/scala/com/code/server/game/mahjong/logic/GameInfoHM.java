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
            room.setBankerId(nextTurnId(room.getBankerId()));
        }

        computeAllGang();
        sendResult(false, userId, null);
        noticeDissolutionResult();
        room.clearReadyStatus();
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
                if("LQ".equals(this.room.getGameType())){
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
}
