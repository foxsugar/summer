package com.code.server.game.poker.yuxiaxie;

import com.code.server.constant.game.Bet;
import com.code.server.constant.response.IfacePlayerInfoVo;
import com.code.server.game.room.PlayerCardInfo;
import com.code.server.game.room.Room;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

import static com.code.server.constant.game.Bet.TYPE_NUO;

/**
 * Created by sunxianping on 2018-11-30.
 */
public class PlayerInfoYuxiaxie extends PlayerCardInfo {



    private List<Bet> bets = new ArrayList<>();


    public int getAllBetNum() {
        int num = 0;
        for (Bet bet : bets) {
            num += bet.num;
        }
        return num;
    }

    public void bet(Room room, int type, int index1, int index2, int num) {
        this.bets.add(new Bet(0,type, index1, index2, num));




//        if (room.isClubRoom()) {
            if (type == TYPE_NUO) {
//                RedisManager.getClubRedisService().addClubUserMoney(room.getClubId(), this.getUserId(), -5 * num);
                room.addUserSocre(this.userId, -5*num);
                this.setScore(score - 5*num);
//            }else{
//                RedisManager.getClubRedisService().addClubUserMoney(room.getClubId(), this.getUserId(), -num);
//                room.addUserSocre(this.userId, -num);
//            }
        }else{
            room.addUserSocre(this.userId, -num);
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
        this.setScore(this.getScore() + sum);
        return sum;
    }


    private int settle(Room room, Bet bet, int dice1, int dice2) {
        if (bet == null) return 0;
        int multiple = 0;
        if (bet.type != Bet.TYPE_NUO) {
            room.addUserSocre(userId, bet.getNum());
            this.setScore(this.getScore() + bet.getNum());
        }
        if (bet.type == Bet.TYPE_DANYA) {
            if (bet.index1 == dice1 || bet.index1 == dice2) {
                multiple = 2;
            }
            if (bet.index1 == dice1 && bet.index1 == dice2) {
                multiple = 4;
            }
            if (bet.index1 != dice1 && bet.index1 != dice2) {
                multiple = -1;
            }
        } else if (bet.type == Bet.TYPE_BAOZI) {

            if (bet.index1 == dice1 && bet.index1 == dice2) {
                multiple = 25;
            }else{
                multiple = -1;
            }
        } else if (bet.type == Bet.TYPE_CHUANLIAN) {
            if((bet.index1 == dice1 && bet.index2 == dice2) ||
                    (bet.index1 == dice2 && bet.index2 == dice1)){
                multiple = 12;
            }else{
                multiple = -1;
            }
        } else if (bet.type == Bet.TYPE_NUO) {
//            if (room.isClubRoom()) {
//                RedisManager.getClubRedisService().addClubUserMoney(room.getClubId(), this.getUserId(), 5* bet.num);
//            }
            if (bet.index2 == dice1 && bet.index2 == dice2) {
                multiple += 5;
            }else
            if (bet.index2 == dice1 || bet.index2 == dice2) {
                multiple += 3;
            }

            if (bet.index1 == dice1 && bet.index1 == dice2) {
                multiple -= 5;
            }else
            if (bet.index1 == dice1 || bet.index1 == dice2) {
                multiple -= 3;
            }

        }


        if (bet.type == Bet.TYPE_NUO) {
            room.addUserSocre(userId, 5* bet.num);
//            if (room.isClubRoom()) {
//                RedisManager.getClubRedisService().addClubUserMoney(room.getClubId(), this.getUserId(), 5* bet.num);
////                multiple += 5;
//            }
            this.setScore(this.getScore() + 5 * bet.getNum());

        }


        //设置分数
//        this.setScore(this.getScore() + multiple * bet.getNum());

        return multiple * bet.getNum();

    }


    public List<Bet> getBets() {
        return bets;
    }

    public PlayerInfoYuxiaxie setBets(List<Bet> bets) {
        this.bets = bets;
        return this;
    }

    @Override
    public IfacePlayerInfoVo toVo(long watchUser) {

        PlayerInfoYuxiaxieVo vo = new PlayerInfoYuxiaxieVo();

        BeanUtils.copyProperties(this, vo);
        return vo;
    }

    @Override
    public IfacePlayerInfoVo toVo() {
        PlayerInfoYuxiaxieVo vo = new PlayerInfoYuxiaxieVo();

        BeanUtils.copyProperties(this, vo);
        return vo;
    }
}
