package com.code.server.game.mahjong.logic;

import com.code.server.constant.response.ResponseVo;
import com.code.server.game.mahjong.response.HandCardsResp;
import com.code.server.game.mahjong.response.ResponseType;
import com.code.server.game.mahjong.util.HuWithHun;
import com.code.server.game.room.kafka.MsgSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by sunxianping on 2019-06-13.
 */
public class GameInfoZhuohaoziZLB extends GameInfoZhuohaozi {


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

//        initHun();
        //不带风
        fapai();
    }





    /**
     * 发牌
     */
    public void fapai() {
        //打乱顺序
        Collections.shuffle(remainCards);
        for (int i = 0; i < playerSize; i++) {
            PlayerCardsInfoMj playerCardsInfo = PlayerCardsInfoFactory.getInstance(room);
            playerCardsInfo.setGameInfo(this);
            long userId = users.get(i);
            //设置id
            playerCardsInfo.setUserId(userId);
            List<String> playerCards = new ArrayList<>();
            //发牌
            for (int j = 0; j < cardSize; j++) {
                playerCards.add(remainCards.remove(0));
            }
            //初始化
            playerCardsInfo.init(playerCards);
            //放进map
            playerCardsInfos.put(userId, playerCardsInfo);

            //发牌状态通知
            HandCardsResp resp = new HandCardsResp();
            resp.setCards(playerCards);
            ResponseVo vo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_GET_HAND_CARDS, resp);

            MsgSender.sendMsg2Player(vo, userId);


        }
        doAfterFapai();
        //回放的牌信息
        for (PlayerCardsInfoMj playerCardsInfoMj : playerCardsInfos.values()) {
            List<String> cs = new ArrayList<>();
            cs.addAll(playerCardsInfoMj.getCards());
            replay.getCards().put(playerCardsInfoMj.getUserId(), cs);
        }





        //确定耗子

        if (!room.isHasMode(mode_不带耗子)) {

            //随机混
            Random rand = new Random();
            int hunIndex = 0;
            if (PlayerCardsInfoMj.isHasMode(this.room.mode, mode_风耗子)) {
                hunIndex = 27 + rand.nextInt(7);
            }else{
                String card = this.remainCards.remove(1);
                hunIndex = CardTypeUtil.getTypeByCard(card);
            }

            if (PlayerCardsInfoMj.isHasMode(this.room.mode, mode_双耗子)) {
                this.hun = HuWithHun.getHunType(hunIndex);
            } else {
//                String card = this.remainCards.get(0);
//                hunIndex = CardTypeUtil.getTypeByCard(card);
                this.hun.add(hunIndex);
            }

            //通知混
            MsgSender.sendMsg2Player("gameService", "noticeHun", this.hun, users);

        }
        //第一个人抓牌
        mopai(firstTurn, "发牌");
        replay.getHun().addAll(this.hun);
    }
}
