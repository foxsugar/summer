package com.code.server.game.poker.yuxiaxie;

import com.code.server.game.room.PlayerCardInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sunxianping on 2018-11-30.
 */
public class PlayerInfoYuxiaxie extends PlayerCardInfo {

    private Bet bet;


    private Map<Integer, Integer> bets = new HashMap<>();



    public void bet(int type, int index1, int index2, int num) {
        this.bet = new Bet(type, index1, index2, num);
    }





    public Map<Integer, Integer> getBets() {
        return bets;
    }

    public PlayerInfoYuxiaxie setBets(Map<Integer, Integer> bets) {
        this.bets = bets;
        return this;
    }
}
