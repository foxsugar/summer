package com.code.server.game.mahjong.logic;

import com.code.server.game.mahjong.response.OperateReqResp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunxianping on 2018/6/6.
 */
public class GameInfoTC_M extends GameInfo {

    protected void handleHu(PlayerCardsInfoMj playerCardsInfo) {
        isAlreadyHu = true;
        sendResult(true, playerCardsInfo.getUserId(), null);
        noticeDissolutionResult();
        //如果连庄 牌局数不增加
        boolean isLianZhuang = playerCardsInfo.getUserId() == this.getFirstTurn();
        if (isLianZhuang) {

            room.clearReadyStatus(false);
        } else {

            room.clearReadyStatus(true);
        }
    }



    protected void handleYiPaoDuoXiang() {

        List<Long> yipaoduoxiang = new ArrayList<>();

        final boolean[] isLianZhuang = {false};
        //删除弃牌
        deleteDisCard(lastPlayUserId, disCard);
        this.waitingforList.forEach(waitDetail -> {
            if (waitDetail.isHu) {
                long uid = waitDetail.myUserId;
                yipaoduoxiang.add(uid);
                PlayerCardsInfoMj playerCardsInfoMj = playerCardsInfos.get(uid);
                playerCardsInfoMj.hu_dianpao(room, this, lastPlayUserId, disCard);
                if (this.getFirstTurn() == uid) {
                    isLianZhuang[0] = true;
                    this.room.setBankerId(uid);
                }
            }
        });


        this.room.setBankerId(yipaoduoxiang.get(0));

        //回放
        OperateReqResp operateReqResp = new OperateReqResp();
        operateReqResp.setYipaoduoxiangUser(yipaoduoxiang);
        operateReqResp.setOperateType(OperateReqResp.type_yipaoduoxiang);
        operateReqResp.setIsMing(true);
        replay.getOperate().add(operateReqResp);

//        handleHu(playerCardsInfo);

        isAlreadyHu = true;
        sendResult(true, -1L, yipaoduoxiang);
        noticeDissolutionResult();
        boolean isAdd = !isLianZhuang[0];
        room.clearReadyStatus(isAdd);
    }
}
