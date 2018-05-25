package com.code.server.game.mahjong.logic;

import com.code.server.constant.response.ResponseVo;
import com.code.server.game.mahjong.response.*;
import com.code.server.game.room.kafka.MsgSender;

import java.util.*;

/**
 * Created by T420 on 2016/11/30.
 */

public class GameInfoXYKD extends GameInfo {


    private List<String> playCards = new ArrayList<>();

    @Override
    public int ting(long userId, String card) {
        String ifAnKou = room.getMode();
        if(!ifAnKou.isEmpty() && (ifAnKou.startsWith("1",1))){
            tingAT(userId,card);
        }else {
            tingMT(userId,card);
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
    public int tingAT(long userId, String card) {

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


    /**
     * 听
     *
     * @param userId
     * @param card
     * @return
     */
    public int tingMT(long userId, String card) {

        if (isAlreadyHu) {
            return ErrorCode.CAN_NOT_TING;
        }

        PlayerCardsInfoKD_XY playerCardsInfo = (PlayerCardsInfoKD_XY)playerCardsInfos.get(userId);
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

        if (!playerCardsInfo.cards.contains(card)) {
            return ErrorCode.CAN_NOT_TING;
        }
        boolean isCan = playerCardsInfo.isCanTing(temp);//不多一张
        if (isCan) {
            playerCardsInfo.ting(card);
            //通知其他玩家听
            MsgSender.sendMsg2Player(vo, users);

            //回放
            replay.getOperate().add(operateReqResp);

            //通知其他玩家出牌信息
//            PlayCardResp playCardResp = new PlayCardResp();
//            playCardResp.setUserId(userId);
//            playCardResp.setCard(null);
//
//            ResponseVo chupaiVo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_PLAY_CARD, playCardResp);
//            MsgSender.sendMsg2Player(chupaiVo, users);

            //出牌
            chuPai_ting(playerCardsInfo.getUserId(), card);
        } else {
            return ErrorCode.CAN_NOT_TING;
        }
        return 0;
    }

    private int chuPai_ting(long userId, String card) {
        //出牌的玩家
        PlayerCardsInfoKD_XY chupaiPlayerCardsInfo = (PlayerCardsInfoKD_XY)playerCardsInfos.get(userId);
        if (this.turnId != userId||isAlreadyHu) {
            return ErrorCode.CAN_NOT_PLAYCARD;
        }

        playCards.add(card);
        this.lastPlayUserId = userId;//上个出牌的人
        lastOperateUserId = userId;//上个操作的人
        //出的牌
        this.disCard = card;
        //chupaiPlayerCardsInfo.chupai(card);


        //通知其他玩家出牌信息
        PlayCardResp playCardResp = new PlayCardResp();
        playCardResp.setUserId(userId);
        playCardResp.setCard(card);
        ResponseVo vo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_PLAY_CARD, playCardResp);
        MsgSender.sendMsg2Player(vo, users);

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

    private void pushAllPass(){
        Map map = new HashMap();
        map.put("isAllPass", true);
        map.put("lastPlayUser", lastPlayUserId);
        ResponseVo responseVo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_ALL_PASS, map);
        MsgSender.sendMsg2Player(responseVo,users);
    }
}
