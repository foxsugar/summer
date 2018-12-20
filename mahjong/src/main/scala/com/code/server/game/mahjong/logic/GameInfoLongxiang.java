package com.code.server.game.mahjong.logic;

import com.code.server.constant.response.ResponseVo;
import com.code.server.game.mahjong.response.HandCardsResp;
import com.code.server.game.mahjong.response.ResponseType;
import com.code.server.game.room.kafka.MsgSender;

import java.util.*;

/**
 * Created by sunxianping on 2018-12-14.
 */
public class GameInfoLongxiang extends GameInfoNew {
    static final int mode_不带风 = 1;

    static final int mode_底分_1 = 2;
    static final int mode_底分_2 = 3;
    static final int mode_底分_4 = 4;

    static final int mode_扣听 = 5;
    static final int mode_扣听_2 = 6;
    static final int mode_扣听_4 = 7;
    static final int mode_扣听_6 = 8;

    static final int mode_跑分 = 9;
    static final int mode_跑分_2 = 10;
    static final int mode_跑分_4 = 11;
    static final int mode_跑分_6 = 12;

    static final int mode_幺九谱 = 13;
    static final int mode_幺九谱_1 = 14;
    static final int mode_幺九谱_2 = 15;
    static final int mode_幺九谱_4 = 16;

    static final int mode_杠牌_2 = 17;
    static final int mode_杠牌_4 = 18;
    static final int mode_杠牌_6 = 19;

    public void init(int gameId, long firstTurn, List<Long> users, RoomInfo room) {
        this.gameId = gameId;

        this.firstTurn = firstTurn;
        this.turnId = firstTurn;
        remainCards.addAll(CardTypeUtil.ALL_CARD);
        this.room = room;
        this.cardSize = 13;
        if (this.room.isHasMode(mode_不带风)) {
            remainCards.removeAll(CardTypeUtil.FENG_CARD);
            remainCards.removeAll(CardTypeUtil.ZI_CARD);
        }

        this.users.addAll(users);
        this.playerSize = room.getPersonNumber();

        for (int i = 0; i < playerSize; i++) {
            PlayerCardsInfoMj playerCardsInfo = PlayerCardsInfoFactory.getInstance(room);
            playerCardsInfo.setGameInfo(this);
            long userId = users.get(i);
            //设置id
            playerCardsInfo.setUserId(userId);

            //放进map
            playerCardsInfos.put(userId, playerCardsInfo);
        }

        if (this.room.isHasMode(mode_跑分)) {
            pushPaofen();
        }else{
            fapai();
        }
    }

    /**
     * 发牌
     */
    public void fapai() {
        //打乱顺序
        Collections.shuffle(remainCards);
        for (int i = 0; i < playerSize; i++) {
//            PlayerCardsInfoMj playerCardsInfo = PlayerCardsInfoFactory.getInstance(room);
//            playerCardsInfo.setGameInfo(this);
            long userId = users.get(i);
            //设置id
            PlayerCardsInfoMj playerCardsInfo = playerCardsInfos.get(userId);
            playerCardsInfo.setUserId(userId);
            List<String> playerCards = new ArrayList<>();
            //发牌
            for (int j = 0; j < cardSize; j++) {
                playerCards.add(remainCards.remove(0));
            }
            //初始化
            playerCardsInfo.init(playerCards);


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
        //第一个人抓牌
        mopai(firstTurn, "发牌");

    }

    private void pushPaofen() {
        ResponseVo vo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, "choosePaofen", 0);

        MsgSender.sendMsg2Player(vo, this.users);
    }




    /**
     * 跑分
     * @param userId
     * @return
     */
    public int paofen(long userId, int status) {
        PlayerCardsInfoMj playerCardsInfoMj = playerCardsInfos.get(userId);
        playerCardsInfoMj.setPaofen(status);

        //回放
        replay.getPaofen().put(userId, status);

        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("status", status);

        ResponseVo vo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, "paofenResp", result);

        MsgSender.sendMsg2Player(vo, this.users);


        //全部选择 就发牌
        if (playerCardsInfos.values().stream().filter(playerCardsInfoMj1 -> playerCardsInfoMj1.paofen == -1).count() == 0) {
            fapai();
        }
        return 0;
    }






}
