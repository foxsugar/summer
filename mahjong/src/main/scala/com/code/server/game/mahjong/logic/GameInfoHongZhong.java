package com.code.server.game.mahjong.logic;

import com.code.server.game.mahjong.util.HuWithHun;
import com.code.server.game.room.kafka.MsgSender;

import java.util.List;
import java.util.Random;

/**
 * Created by sunxianping on 2018/8/14.
 */
public class GameInfoHongZhong extends GameInfoNew {


    @Override
    public void init(int gameId, long firstTurn, List<Long> users, RoomInfo room) {
        this.gameId = gameId;

        this.firstTurn = firstTurn;
        this.turnId = firstTurn;
        remainCards.addAll(CardTypeUtil.ALL_CARD);
        this.users.addAll(users);
        this.room = room;
        this.cardSize = 13;
        this.playerSize = room.getPersonNumber();

        if (this.room.isHasMode(PlayerCardsInfoHongZhong.NO_FENG)) {
            remainCards.removeAll(CardTypeUtil.FENG_CARD);
            remainCards.removeAll(CardTypeUtil.ZI_CARD);

            if (!this.room.isHasMode(PlayerCardsInfoHongZhong.HUN_RAND) &&
                    !this.room.isHasMode(PlayerCardsInfoHongZhong.HUN_NO) &&
                    !this.room.isHasMode(PlayerCardsInfoHongZhong.TWO_HUN) ) {
                remainCards.add("124");
                remainCards.add("125");
                remainCards.add("126");
                remainCards.add("127");
            }
        }

        initHun();
        //不带风
        fapai();
    }


    public void initHun() {

        int hunIndex = 0;
        Random rand = new Random();
        hunIndex = rand.nextInt(31);
        if (this.room.isHasMode(PlayerCardsInfoHongZhong.HUN_RAND)) {
            this.hun.add(hunIndex);
        } else {
            this.hun.add(31);
        }

        if (this.room.isHasMode(PlayerCardsInfoHongZhong.TWO_HUN)) {
            this.hun = HuWithHun.getHunType(hunIndex);
        }

        if (this.room.isHasMode(PlayerCardsInfoHongZhong.HUN_NO)) {
            this.hun.clear();
        }


        //通知混
        MsgSender.sendMsg2Player("gameService", "noticeHun", this.hun, users);
        replay.getHun().addAll(this.hun);
    }


}
