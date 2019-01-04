package com.code.server.game.poker.yuxiaxie;

import com.code.server.constant.response.IfacePlayerInfoVo;
import com.code.server.game.room.PlayerCardInfo;
import com.code.server.game.room.Room;
import com.code.server.redis.service.RedisManager;

import java.util.ArrayList;
import java.util.List;

import static com.code.server.game.poker.yuxiaxie.Bet.TYPE_NUO;

/**
 * Created by sunxianping on 2018-11-30.
 */
public class PlayerInfoYuxiaxie extends PlayerCardInfo {

    private Bet bet;

    private List<Bet> bets = new ArrayList<>();



    public void bet(Room room, int type, int index1, int index2, int num) {
        this.bets.add(new Bet(type, index1, index2, num));

        this.setScore(score - num);

        if (room.isClubRoom()) {
            if (type == TYPE_NUO) {
                RedisManager.getClubRedisService().addClubUserMoney(room.getClubId(), this.getUserId(), -5 * num);
            }else{
                RedisManager.getClubRedisService().addClubUserMoney(room.getClubId(), this.getUserId(), -num);
            }
        }
    }



    /**
     * 结算
     * @param dice1
     * @param dice2
     * @return
     */
    public int settle(Room room, int dice1, int dice2) {
        int sum = 0;
        for (Bet bet : bets) {
            sum += settle(room, bet, dice1, dice2);
        }

        room.addUserSocre(userId, sum);
        return sum;
    }


    private int settle(Room room, Bet bet, int dice1, int dice2) {
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
        } else if (bet.type == Bet.TYPE_NUO) {
            if (room.isClubRoom()) {
                RedisManager.getClubRedisService().addClubUserMoney(room.getClubId(), this.getUserId(), 5* bet.num);
            }
            if (bet.index2 == dice1 || bet.index2 == dice2) {
                multiple = 2;
            }
        }
        if (multiple != 0) {
            multiple += 1;
        }

        if (room.isClubRoom()) {
            RedisManager.getClubRedisService().addClubUserMoney(room.getClubId(), this.getUserId(), multiple * bet.getNum());
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
