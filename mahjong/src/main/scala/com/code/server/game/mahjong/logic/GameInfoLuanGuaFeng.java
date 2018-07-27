package com.code.server.game.mahjong.logic;

import com.code.server.constant.response.ResponseVo;
import com.code.server.game.mahjong.response.*;
import com.code.server.game.room.kafka.MsgSender;

import java.util.ArrayList;
import java.util.List;

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
        boolean isCanXuanfeng = playerCardsInfo.isHasXuanfengDan(playerCardsInfo.cards, card);

        //能做的操作
        playerCardsInfo.setCanBeGang(isCanGang);
        playerCardsInfo.setCanBePeng(false);
        playerCardsInfo.setCanBeHu(isCanHu);
        playerCardsInfo.setCanBeTing(isCanTing);
        playerCardsInfo.setCanBeXuanfeng(isCanXuanfeng);

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

    public int buFeng(long userId, String card) {

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




                //回放 抓牌
                OperateReqResp operateReqResp = new OperateReqResp();
                operateReqResp.setCard(c);
                operateReqResp.setUserId(userId);
                operateReqResp.setOperateType(OperateReqResp.type_mopai);
                replay.getOperate().add(operateReqResp);
            }
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

    public int liang(long userId, List<String> cards){




        if (isAlreadyHu) {
            return ErrorCode.CAN_PLAYCARD_IS_HU;
        }


        //出牌的玩家
        PlayerCardsInfoMj playerCardsInfoMj = playerCardsInfos.get(userId);

        if(!playerCardsInfoMj.isCanBeXuanfeng()){
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


        //补牌

        int buSize = cards.size() - 3;


        if (buSize > 0) {
            buPai(userId, buSize);
        }else{

        }








        return 0;

    }
}
