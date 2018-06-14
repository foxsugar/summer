package com.code.server.game.mahjong.logic;

import com.code.server.constant.response.GameOfResult;
import com.code.server.constant.response.UserOfResult;
import com.code.server.game.room.kafka.MsgSender;
import com.code.server.game.room.service.RoomManager;

import java.util.List;

/**
 * Created by LY on 2018/04/27.
 */

public class GameInfoXXPB extends GameInfo {

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
    protected void handleHuangzhuang(long userId) {
        turnResultToZeroOnHuangZhuang();
        sendResult(false, userId, null);
        noticeDissolutionResult();
        //通知所有玩家结束
        room.clearReadyStatus(true);
    }


    /**
     * 设置庄家
     *
     * @param winnerId
     */
    public void setBanker(long winnerId) {
        if (winnerId == this.getFirstTurn()) {
            room.setBankerId(winnerId);
        } else {
            long nextId = nextTurnId(this.getFirstTurn());
            room.setBankerId(nextId);
        }
    }

    protected void handleHu(PlayerCardsInfoMj playerCardsInfo) {
        isAlreadyHu = true;
        sendResult(true, playerCardsInfo.getUserId(), null);
        noticeDissolutionResult(playerCardsInfo);
        //如果连庄 牌局数不增加
        boolean isLianZhuang = playerCardsInfo.getUserId() == this.getFirstTurn();
        if (isLianZhuang) {

            room.clearReadyStatus(false);
        } else {
            room.clearReadyStatus(true);
        }
    }

    public void noticeDissolutionResult(PlayerCardsInfoMj playerCardsInfo) {
        if (isRoomOver(playerCardsInfo)) {
            List<UserOfResult> userOfResultList = this.room.getUserOfResult();
            // 存储返回
            GameOfResult gameOfResult = new GameOfResult();
            gameOfResult.setUserList(userOfResultList);
            RoomManager.removeRoom(room.getRoomId());

            MsgSender.sendMsg2Player("gameService", "noticeDissolutionResult", gameOfResult, users);

            //战绩
            this.room.genRoomRecord();

        }
    }

    protected boolean isRoomOver(PlayerCardsInfoMj playerCardsInfo) {
        return playerCardsInfo.getUserId() == this.getFirstTurn()?room.curGameNumber>room.getGameNumber():room.curGameNumber>=room.getGameNumber();
    }
}
