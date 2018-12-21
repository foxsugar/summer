package com.code.server.game.poker.doudizhu;

import java.util.Collections;
import java.util.List;

/**
 * Created by sunxianping on 2018-12-21.
 */
public class GamePaodekuai extends GameDouDiZhu {



    public void init(List<Long> users, long dizhuUser) {
        //没人16张牌
        initCardNum = 16;
        //初始化玩家
        for (Long uid : users) {
            PlayerCardInfoDouDiZhu playerCardInfo = getGameTypePlayerCardInfo();
            playerCardInfo.userId = uid;
            playerCardInfos.put(uid, playerCardInfo);
        }
        this.users.addAll(users);

        if (!this.room.isUserLastGameCards()) {
            this.room.getLastGameCards().clear();
        }


        shuffle();
        deal();
        //第一局 第一个玩家做地主
        dizhuUser = dizhuUser != 0 ? dizhuUser : users.get(0);
        chooseDizhu(dizhuUser);
    }



    /**
     * 洗牌
     */
    protected void shuffle() {
        for (int i = 1; i <= 54; i++) {
            cards.add(i);
        }
        cards.remove((Integer) 53);
        cards.remove((Integer) 54);
        cards.remove((Integer) 6);
        cards.remove((Integer) 7);
        cards.remove((Integer) 8);
        cards.remove((Integer) 4);

        Collections.shuffle(cards);

        RoomDouDiZhu roomDouDiZhu = (RoomDouDiZhu)room;
        if (roomDouDiZhu.isUserLastGameCards() && roomDouDiZhu.getLastGameCards().size()>0) {
            cards = roomDouDiZhu.getLastGameCards();
        }
    }
}
