package com.code.server.game.poker.tiandakeng;

import com.code.server.constant.response.ResponseVo;
import com.code.server.game.poker.doudizhu.RoomDouDiZhu;
import com.code.server.game.room.Game;
import com.code.server.game.room.Room;
import com.code.server.game.room.kafka.MsgSender;

import java.util.*;

/**
 * Created by sunxianping on 2018-10-18.
 */
public class GameTDK extends Game {

    private static final int model_1 = 1;


    protected Room room;
    protected Map<Long, PlayerInfoTDK> playerCardInfos = new HashMap<>();
    protected List<Integer> cards = new ArrayList<>();//牌

    public void startGame(List<Long> users, Room room) {
        this.room = room;
        init(users, room.getBankerId());
        updateLastOperateTime();
        //通知其他人游戏已经开始
        MsgSender.sendMsg2Player(new ResponseVo("gameService", "gameBegin", "ok"), this.getUsers());
    }

    public void init(List<Long> users, long dizhuUser) {
        //初始化玩家
        for (Long uid : users) {
            PlayerInfoTDK playerCardInfo = new PlayerInfoTDK();
            playerCardInfo.userId = uid;
            playerCardInfos.put(uid, playerCardInfo);
        }
        this.users.addAll(users);


        shuffle();
        deal();
        //第一局 第一个玩家做地主
        dizhuUser = dizhuUser != 0 ? dizhuUser : users.get(0);
//        chooseDizhu(dizhuUser);
    }


    /**
     * 洗牌
     */
    protected void shuffle() {
        for (int i = 1; i <= 54; i++) {
            cards.add(i);
        }
        Collections.shuffle(cards);
    }


    /**
     * 发牌
     */
    protected void deal() {
        //每人发三张牌
        for (PlayerInfoTDK playerInfoTDK : playerCardInfos.values()) {
            playerInfoTDK.getCards().add(cards.remove(0));
            playerInfoTDK.getCards().add(cards.remove(0));
            playerInfoTDK.getCards().add(cards.remove(0));
        }



        RoomDouDiZhu roomDouDiZhu = (RoomDouDiZhu) room;
//        if (roomDouDiZhu.testNextCards != null) {
//            testDeal();
//        } else {
//
//            for (PlayerCardInfoDouDiZhu playerCardInfo : playerCardInfos.values()) {
//                for (int i = 0; i < this.initCardNum; i++) {
//                    playerCardInfo.cards.add(cards.remove(0));
//                }
//                //通知发牌
//                MsgSender.sendMsg2Player(new ResponseVo("gameService", "deal", playerCardInfo.cards), playerCardInfo.userId);
//            }
//
//            //底牌
//        }
//        tableCards.addAll(cards);
//
    }

}
