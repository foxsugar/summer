package com.code.server.game.mahjong.logic;

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
}
