package com.code.server.game.mahjong.logic;


import java.util.List;

/**
 * Created by T420 on 2016/11/30.
 */

public class GameInfoHasChi extends GameInfoNew {


    public void init(int gameId, long firstTurn, List<Long> users, RoomInfo room) {
        this.gameId = gameId;

        this.firstTurn = firstTurn;
        this.turnId = firstTurn;
        remainCards.addAll(CardTypeUtil.ALL_CARD);
        this.users.addAll(users);
        this.room = room;
        this.cardSize = 13;
        this.playerSize = room.getPersonNumber();
        //不带风
        if ("3".equals(room.getMode()) || "4".equals(room.getMode()) || "13".equals(room.getMode()) || "14".equals(room.getMode())) {
            remainCards.removeAll(CardTypeUtil.FENG_CARD);
            remainCards.removeAll(CardTypeUtil.ZI_CARD);
        } else if (GSJ_NOFENG.equals(room.getMode())) {
            remainCards.removeAll(CardTypeUtil.FENG_CARD);
            remainCards.removeAll(CardTypeUtil.ZI_CARD);
            int point = 16;
            while (point > 0) {
                remainCards.remove(0);
                point--;
            }
        } else if (DPH_NOFENG.equals(room.getModeTotal())) {
            remainCards.removeAll(CardTypeUtil.FENG_CARD);
            remainCards.removeAll(CardTypeUtil.ZI_CARD);

        }
        fapai();
    }

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














}


