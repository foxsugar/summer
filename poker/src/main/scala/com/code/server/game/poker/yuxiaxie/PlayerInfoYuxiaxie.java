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


    public void bet(int index, int num) {
        bets.put(index, bets.getOrDefault(index, 0) + num);
    }



    class Bet{
        int bet1;
        int bet2;
    }


    public Map<Integer, Integer> getBets() {
        return bets;
    }

    public PlayerInfoYuxiaxie setBets(Map<Integer, Integer> bets) {
        this.bets = bets;
        return this;
    }
}
