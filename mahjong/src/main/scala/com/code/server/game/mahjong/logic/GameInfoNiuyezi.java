package com.code.server.game.mahjong.logic;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunxianping on 2018/1/12.
 */
public class GameInfoNiuyezi extends GameInfo {

    @Override
    public int guo(long userId) {

        //过胡逻辑
        if (playerCardsInfos.get(userId).isCanBeHu()) {
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
}
