package com.code.server.game.mahjong.logic;

import com.code.server.game.mahjong.response.ErrorCode;
import com.code.server.game.mahjong.response.OperateReqResp;

/**
 * Created by sunxianping on 2017/12/26.
 */
public class GameInfoBengbu extends GameInfo {






    /**
     * 胡牌
     *
     * @param userId
     * @return
     */
    public int hu(long userId) {

        PlayerCardsInfoMj playerCardsInfo = playerCardsInfos.get(userId);
        if (playerCardsInfo == null) {
            return ErrorCode.USER_ERROR;
        }

        //回放
        OperateReqResp operateReqResp = new OperateReqResp();
        operateReqResp.setUserId(userId);
        operateReqResp.setOperateType(OperateReqResp.type_hu);


        if (lastOperateUserId == userId) {//自摸
            if (!playerCardsInfo.isCanHu_zimo(catchCard)) {
                return ErrorCode.CAN_NOT_HU;
            }

            setBanker(userId);
            playerCardsInfo.hu_zm(room, this, catchCard);
            //回放
            replay.getOperate().add(operateReqResp);
            handleHu(playerCardsInfo);
        } else {
            if (this.disCard == null && jieGangHuCard == null) {
                return ErrorCode.CAN_NOT_HU;
            }

            String card = this.disCard;
            //
            if (jieGangHuCard != null) {
                card = jieGangHuCard;
            }

            if (!playerCardsInfo.isCanHu_dianpao(card)) {
                return ErrorCode.CAN_NOT_HU;
            }
            setBanker(userId);
            //从等待列表删除
//            if (waitingforList.size() > 0) {
            WaitDetail waitDetail = waitingforList.get(0);
            if (waitDetail != null && waitDetail.myUserId == userId && waitDetail.isHu) {
                waitingforList.clear();
            } else {
                return ErrorCode.NOT_TURN;
            }
//            }

            //截杠胡 算自摸
            if (jieGangHuCard != null) {
                //todo 是否要摸一张
                playerCardsInfo.mopai(jieGangHuCard);
                playerCardsInfo.hu_zm(room, this, jieGangHuCard);
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


        return 0;

    }

}
