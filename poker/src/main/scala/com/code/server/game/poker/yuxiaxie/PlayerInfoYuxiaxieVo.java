package com.code.server.game.poker.yuxiaxie;

import com.code.server.constant.response.IfacePlayerInfoVo;

/**
 * Created by sunxianping on 2018-12-10.
 */
public class PlayerInfoYuxiaxieVo implements IfacePlayerInfoVo {


    private Bet bet;

    public Bet getBet() {
        return bet;
    }

    public PlayerInfoYuxiaxieVo setBet(Bet bet) {
        this.bet = bet;
        return this;
    }

}
