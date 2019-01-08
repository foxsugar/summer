package com.code.server.game.poker.yuxiaxie;

import com.code.server.constant.response.IfacePlayerInfoVo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunxianping on 2018-12-10.
 */
public class PlayerInfoYuxiaxieVo implements IfacePlayerInfoVo {


    private long userId;
    public double score;

    private List<Bet> bets = new ArrayList<>();

    public List<Bet> getBets() {
        return bets;
    }

    public PlayerInfoYuxiaxieVo setBets(List<Bet> bets) {
        this.bets = bets;
        return this;
    }

    public double getScore() {
        return score;
    }

    public PlayerInfoYuxiaxieVo setScore(double score) {
        this.score = score;
        return this;
    }

    public long getUserId() {
        return userId;
    }

    public PlayerInfoYuxiaxieVo setUserId(long userId) {
        this.userId = userId;
        return this;
    }
}
