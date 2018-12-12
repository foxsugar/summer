package com.code.server.game.poker.yuxiaxie;

import com.code.server.constant.response.IfacePlayerInfoVo;
import com.code.server.game.room.PlayerCardInfo;

/**
 * Created by sunxianping on 2018-11-30.
 */
public class PlayerInfoYuxiaxie extends PlayerCardInfo {

    private Bet bet;


    public void bet(int type, int index1, int index2, int num) {
        this.bet = new Bet(type, index1, index2, num);
    }


    /**
     * 结算
     * @param dice1
     * @param dice2
     * @return
     */
    public int settle(int dice1, int dice2) {
        if (bet == null) return 0;
        int multiple = 0;
        if (bet.type == Bet.TYPE_DANYA) {
            if (bet.index1 == dice1 || bet.index1 == dice2) {
                multiple = 2;
            }
        } else if (bet.type == Bet.TYPE_BAOZI) {

            if (bet.index1 == dice1 && bet.index1 == dice2) {
                multiple = 25;
            }
        } else if (bet.type == Bet.TYPE_CHUANLIAN) {
            if((bet.index1 == dice1 && bet.index2 == dice2) ||
                    (bet.index1 == dice2 && bet.index2 == dice1)){
                multiple = 12;
            }
        }
        if (multiple != 0) {
            multiple += 1;
        }

        //设置分数
        this.setScore(this.getScore() + multiple * bet.getNum());

        return multiple * bet.getNum();
    }



    public Bet getBet() {
        return bet;
    }

    public PlayerInfoYuxiaxie setBet(Bet bet) {
        this.bet = bet;
        return this;
    }

    @Override
    public IfacePlayerInfoVo toVo(long watchUser) {

        PlayerInfoYuxiaxieVo vo = new PlayerInfoYuxiaxieVo();
        vo.setBet(this.bet);

        return vo;
    }


}
