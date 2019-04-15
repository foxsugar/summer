package com.code.server.game.mahjong.logic;

import com.code.server.constant.response.ResponseVo;
import com.code.server.game.mahjong.response.ErrorCode;
import com.code.server.game.mahjong.response.HandCardsResp;
import com.code.server.game.mahjong.response.ResponseType;
import com.code.server.game.room.kafka.MsgSender;

import java.util.*;

/**
 * Created by sunxianping on 2019-04-15.
 *
 * 血战到底 麻将
 */
public class GameInfoXZDD extends GameInfoNew {


    /**
     * 发牌
     */
    public void fapai() {
        //打乱顺序
        Collections.shuffle(remainCards);
        remainCards.removeAll(CardTypeUtil.FENG_CARD);
        remainCards.removeAll(CardTypeUtil.ZI_CARD);
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


        //第一个人抓牌
      //  mopai(firstTurn, "发牌");

    }

    /**
     * 定缺
     * @param userId
     * @param groupType
     * @return
     */
    public int dingque(long userId, int groupType){
        PlayerCardsInfoMj playerCardsInfoMj = this.playerCardsInfos.get(userId);
        playerCardsInfoMj.dingqueGroupType = groupType;

        if (playerCardsInfoMj.dingqueGroupType != 0) {
            return ErrorCode.CAN_NOT_DINGQUE;
        }
        Map<String, Object> result = new HashMap<>();
        ResponseVo vo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, "dingqueResp", result);

        MsgSender.sendMsg2Player(vo, this.users);

        MsgSender.sendMsg2Player("gameService", "dingque", this.room.laZhuang, users);

        boolean isAllDingque = this.playerCardsInfos.values().stream().noneMatch(playerCardsInfoMj1 -> playerCardsInfoMj.dingqueGroupType == 0);
        if (isAllDingque) {
            MsgSender.sendMsg2Player("gameService", "allDingque", this.room.laZhuang, users);
            //
            mopai(firstTurn, "发牌");
        }
        return 0;
    }

    /**
     * 下一个出牌人id
     *
     * @param curId
     * @return
     */
    public long nextTurnId(long curId) {
        List<Long> us = new ArrayList<>(users);
        for (PlayerCardsInfoMj playerCardsInfoMj : this.playerCardsInfos.values()) {
            if (playerCardsInfoMj.isAlreadyHu) {
                us.remove(playerCardsInfoMj.getUserId());
            }
        }
        int index = us.indexOf(curId);

        int nextId = index + 1;
        if (nextId >= us.size()) {
            nextId = 0;
        }
        return us.get(nextId);
    }


}
