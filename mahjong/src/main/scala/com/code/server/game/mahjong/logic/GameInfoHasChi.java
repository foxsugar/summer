package com.code.server.game.mahjong.logic;



/**
 * Created by T420 on 2016/11/30.
 */

public class GameInfoHasChi extends GameInfoNew {



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


