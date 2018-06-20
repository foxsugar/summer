package com.code.server.game.mahjong.logic;

import com.code.server.game.mahjong.util.HuWithHun;
import com.code.server.game.room.kafka.MsgSender;

import java.util.List;
import java.util.Random;

/**
 * Created by sunxianping on 2018/5/21.
 */
public class GameInfoZhuohaozi extends GameInfoNew {

    public static final int mode_风耗子 = 1;
    public static final int mode_随机耗子 = 2;
    public static final int mode_单耗子 = 3;
    public static final int mode_双耗子 = 4;
    public static final int mode_显庄 = 5;
    public static final int mode_扛耗子 = 6;
    public static final int mode_摸三胡六 = 7;
    public static final int mode_摸四胡五 = 8;
    public static final int mode_摸一胡五 = 9;

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

        initHun();
        //不带风
        fapai();
    }


    /**
     * 初始化混
     */
    public void initHun() {

        //随机混
        Random rand = new Random();
        int hunIndex = 0;
        if (PlayerCardsInfoMj.isHasMode(this.room.mode, mode_风耗子)) {
            hunIndex = 27 + rand.nextInt(7);
        }else{
            hunIndex = rand.nextInt(34);
        }

        if (PlayerCardsInfoMj.isHasMode(this.room.mode, mode_双耗子)) {
            this.hun = HuWithHun.getHunType(hunIndex);
        } else {
            this.hun.add(hunIndex);
        }

        //通知混
        MsgSender.sendMsg2Player("gameService", "noticeHun", this.hun, users);

    }


    @Override
    protected void handleHuangzhuang(long userId) {
        turnResultToZeroOnHuangZhuang();
        super.handleHuangzhuang(userId);
    }
}
