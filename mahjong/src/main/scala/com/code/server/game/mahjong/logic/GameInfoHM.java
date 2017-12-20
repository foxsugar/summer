package com.code.server.game.mahjong.logic;

import java.util.*;

/**
 * Created by T420 on 2016/11/30.
 */

public class GameInfoHM extends GameInfo {

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
    protected void  handleHuangzhuang(long userId) {
        //黄庄有杠，下家坐庄；黄庄无杠，继续坐庄
        boolean b = false;
        a:for (Long l: playerCardsInfos.keySet()) {
            if(playerCardsInfos.get(l).anGangType.size()>0){
                b=true;
                break a;
            }
            if(playerCardsInfos.get(l).mingGangType.keySet().size()>0){
                b=true;
                break a;
            }
        }
        if(b){
            room.setBankerId(nextTurnId(room.getBankerId()));
        }

        computeAllGang();
        sendResult(false, userId, null);
        noticeDissolutionResult();
        room.clearReadyStatus();
    }
}
