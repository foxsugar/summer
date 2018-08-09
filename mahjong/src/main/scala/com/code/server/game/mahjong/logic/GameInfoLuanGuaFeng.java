package com.code.server.game.mahjong.logic;

import com.code.server.constant.response.ResponseVo;
import com.code.server.game.mahjong.response.*;
import com.code.server.game.room.kafka.MsgSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.code.server.game.mahjong.logic.PlayerCardsInfoMj.isHasMode;
import static com.code.server.game.mahjong.logic.PlayerCardsInfoMj.type_bufeng;

/**
 * Created by sunxianping on 2018/7/25.
 */
public class GameInfoLuanGuaFeng extends GameInfoNew {


    @Override
    public void init(int gameId, long firstTurn, List<Long> users, RoomInfo room) {
        this.gameId = gameId;

        this.firstTurn = firstTurn;
        this.turnId = firstTurn;
        remainCards.addAll(CardTypeUtil.ALL_CARD);
        this.users.addAll(users);
        this.room = room;
        this.cardSize = 13;
        this.playerSize = room.getPersonNumber();
        initHun();
        //不带风
        fapai();
    }


    @Override
    protected void handleHuangzhuang(long userId) {
        if (isHasMode(this.room.mode, PlayerCardInfoLuanGuaFeng.HUANGZHUANG)) {
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
    }

    @Override
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


        boolean isCanTing = playerCardsInfo.isCanTing(playerCardsInfo.cards);//多一张
        boolean isCanHu = playerCardsInfo.isCanHu_zimo(catchCard);
        boolean isCanXuanfeng = playerCardsInfo.isHasXuanfengDan(playerCardsInfo.cards, card);
        boolean isCanGang =!isCanXuanfeng && playerCardsInfo.isHasGang();
        boolean isCanBufeng = playerCardsInfo.isCanBufeng(card);

        //能做的操作
        playerCardsInfo.setCanBeGang(isCanGang);
        playerCardsInfo.setCanBePeng(false);
        playerCardsInfo.setCanBeHu(isCanHu);
        playerCardsInfo.setCanBeTing(isCanTing);
        playerCardsInfo.setCanBeXuanfeng(isCanXuanfeng);
        playerCardsInfo.setCanBeBufeng(isCanBufeng);

        OperateResp resp = new OperateResp();
        resp.setIsCanGang(isCanGang);
        resp.setIsCanHu(isCanHu);
        resp.setIsCanTing(isCanTing);
        resp.setCanBufeng(isCanBufeng);
        resp.setCanXuanfengDan(isCanXuanfeng);

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

    public int buFeng(long userId, String card) {

        if (isAlreadyHu) {
            return ErrorCode.CAN_NOT_GANG;
        }
        PlayerCardsInfoMj playerCardsInfo = playerCardsInfos.get(userId);
        if (playerCardsInfo == null) {
            return ErrorCode.USER_ERROR;
        }
        OperateReqResp operateReqResp = new OperateReqResp();
        operateReqResp.setOperateType(OperateReqResp.type_bufeng);
        ResponseVo vo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_OTHER_OPERATE, operateReqResp);

        if (lastOperateUserId == userId) {
            //杠手里的牌

            if (!playerCardsInfo.isCanBufeng(card)) {
                return ErrorCode.CAN_NOT_GANG;
            }
            int gangType = CardTypeUtil.getTypeByCard(card);
            //通知别人有杠
            //回放
//            operateReqResp.setFromUserId(userId);
            operateReqResp.setUserId(userId);
            operateReqResp.setCard(card);
//            operateReqResp.setIsMing(isMing);
            replay.getOperate().add(operateReqResp);
            //通知所有人有补风
            MsgSender.sendMsg2Player(vo, users);

            //截杠胡
//            if (isHasJieGangHu && isMing) {

            for (Map.Entry<Long, PlayerCardsInfoMj> entry : playerCardsInfos.entrySet()) {
                OperateResp operateResp = new OperateResp();

                //其他玩家的处理 碰杠等 如果有加入等待列表(要等待这些玩家"过")
                if (userId != entry.getKey()) {
                    //通知其他玩家出了什么牌 自己能有什么操作
                    PlayerCardsInfoMj playerOther = entry.getValue();
                    boolean isCanHu = false;
//                        boolean isCanHu = playerOther.isCanHu_dianpao(card);
                    boolean isCanGang = playerOther.isCanGangAddThisCard(card);
                    boolean isCanPeng = playerOther.isCanPengAddThisCard(card);

                    if (isHasGuoHu() && playerOther.isGuoHu()) {
                        isCanHu = false;
                    } else {
                        isCanHu = playerCardsInfo.isCanHu_dianpao(card);
                    }
                    //设置返回结果
                    operateResp.setCanBeOperate(false, isCanPeng, isCanGang, false, isCanHu, false, false);
                    //设置自己能做的操作
                    playerOther.setCanBeOperate(false, isCanPeng, isCanGang, false, isCanHu, false, false);
                    boolean isWait = isCanHu | isCanGang | isCanPeng;
                    if (isWait) {
                        add2WaitingList(entry.getKey(), isCanHu, isCanGang, isCanPeng, false, false, false);
                    }
                }

                //可能的操作
                ResponseVo OperateVo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_OPERATE, operateResp);
                MsgSender.sendMsg2Player(OperateVo, entry.getKey());
            }

            //todo 多个截杠胡 是否一炮多响
//            }
            resetCanBeOperate(playerCardsInfo);

            playerCardsInfo.cards.remove(card);
            playerCardsInfo.setLastOperate(type_bufeng);

            if (this.waitingforList.size() == 0) {
                doBuFeng(playerCardsInfo, card);
            } else {
                this.lastPlayUserId = userId;//上个出牌的人
                lastOperateUserId = userId;//上个操作的人
                this.disCard = card;
                //排序
                compare(waitingforList);
                //出现截杠胡
//                this.beJieGangUser = userId;
//                this.jieGangHuCard = card;
                this.jieXuanfengCard = card;
                this.beJieXuanfengUser = userId;
            }


        } else {
//            if (disCard == null || !playerCardsInfo.canBeGang) {
//                return ErrorCode.CAN_NOT_GANG;
//            }
//            boolean isCanGang = playerCardsInfo.isCanGangAddThisCard(disCard);
//            if (!isCanGang) {
//                return ErrorCode.CAN_NOT_GANG;
//            }
//            handleWait(userId, WaitDetail.gangPoint);
//            lastOperateUserId = userId;
        }


        return 0;
    }


    private void doBuFeng(PlayerCardsInfoMj playerCardsInfoMj, String card) {
        playerCardsInfoMj.bu_feng(this.room, this, card);

        mopai(playerCardsInfoMj.getUserId(), "补风");
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
                } else if (jieXuanfengCard != null) {
                    doBuFeng(playerCardsInfos.get(beJieXuanfengUser),jieXuanfengCard);
                    beJieXuanfengUser = -1;
                    jieXuanfengCard = null;
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



    public int buPai(long userId, int num) {
        List<String> buCards = new ArrayList<>();

        int index = 0;
        while (buCards.size() < num) {
            String card = remainCards.get(index);
            if (!CardTypeUtil.isFeng(card)) {
                buCards.add(card);
            }
            index += 1;
        }

        remainCards.removeAll(buCards);


        PlayerCardsInfoMj playerCardsInfoMj = playerCardsInfos.get(userId);
        for (String c : buCards) {
            playerCardsInfoMj.mopai(c);


            turnId = userId;
            this.lastMoPaiUserId = userId;
            lastOperateUserId = userId;
            this.catchCard = c;

            // 把摸到的牌 推给摸牌的玩家
            int remainSize = remainCards.size();
            for (long user : users) {
                GetCardResp getCardResp = new GetCardResp();
                getCardResp.setRemainNum(remainSize);
                getCardResp.setUserId(userId);
                if (user == userId) {
                    getCardResp.setCard(c);
                }
                ResponseVo responseVo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_GET_CARD, getCardResp);
                MsgSender.sendMsg2Player(responseVo, user);

                //能做的操作全置成不能
                PlayerCardsInfoMj other = playerCardsInfos.get(user);

                resetCanBeOperate(other);


            }
            //回放 抓牌
            OperateReqResp operateReqResp = new OperateReqResp();
            operateReqResp.setCard(c);
            operateReqResp.setUserId(userId);
            operateReqResp.setOperateType(OperateReqResp.type_mopai);
            replay.getOperate().add(operateReqResp);
        }


        //


        boolean isCanGang = playerCardsInfoMj.isHasGang();
        boolean isCanTing = playerCardsInfoMj.isCanTing(playerCardsInfoMj.cards);//多一张
        boolean isCanHu = playerCardsInfoMj.isCanHu_zimo(catchCard);
        boolean isCanXuanfeng = playerCardsInfoMj.isCanBeXuanfeng();

        //能做的操作
        playerCardsInfoMj.setCanBeGang(isCanGang);
        playerCardsInfoMj.setCanBePeng(false);
        playerCardsInfoMj.setCanBeHu(isCanHu);
        playerCardsInfoMj.setCanBeTing(isCanTing);
        playerCardsInfoMj.setCanBeXuanfeng(isCanXuanfeng);

        OperateResp resp = new OperateResp();
        resp.setIsCanGang(isCanGang);
        resp.setIsCanHu(isCanHu);
        resp.setIsCanTing(isCanTing);


        //可能的操作
        ResponseVo OperateResponseVo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_OPERATE, resp);
        MsgSender.sendMsg2Player(OperateResponseVo, userId);


        return 0;
    }

    public int liang(long userId, List<String> cards) {


        if (isAlreadyHu) {
            return ErrorCode.CAN_PLAYCARD_IS_HU;
        }


        //出牌的玩家
        PlayerCardsInfoMj playerCardsInfoMj = playerCardsInfos.get(userId);

        if (!playerCardsInfoMj.isCanBeXuanfeng()) {
            return ErrorCode.CAN_NOT_XUANFENG;
        }

        //todo 其他判断
        playerCardsInfoMj.liang(0, cards);


        //通知其他人亮牌
        OperateReqResp operateReqResp = new OperateReqResp();
        operateReqResp.setOperateType(OperateReqResp.type_xuanfengdan);
        operateReqResp.setXuanfengCards(cards);
        operateReqResp.setUserId(userId);

        //通知其他人
        ResponseVo vo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_OTHER_OPERATE, operateReqResp);
        MsgSender.sendMsg2Player(vo, users);

        //todo 回放


        replay.getOperate().add(operateReqResp);
        //补牌

        int buSize = cards.size() % 3;


        if (buSize > 0) {
            buPai(userId, buSize);
        } else {

        }


        return 0;

    }
}
