package com.code.server.game.mahjong.logic;

import com.code.server.game.room.kafka.MsgSender;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sunxianping on 2017/10/10.
 */
public class GameInfoDonghu extends GameInfo {


    public static final Map<Integer, Integer> gang_take_out_card = new HashMap<>();

    static {
        gang_take_out_card.put(0, 12);
        gang_take_out_card.put(1, 15);
        gang_take_out_card.put(2, 17);
        gang_take_out_card.put(3, 19);
        gang_take_out_card.put(4, 136);

    }

    /**
     * 是否荒庄
     *
     * @param playerCardsInfo
     * @return
     */
    protected boolean isHuangzhuang(PlayerCardsInfoMj playerCardsInfo) {
        int remain = this.remainCards.size();

        int gangCount = this.playerCardsInfos.values().stream().mapToInt(p ->
                p.mingGangType.size() + p.anGangType.size()
        ).sum();
        int maxRemainCard = gang_take_out_card.get(gangCount);
        //小于牌剁的数量
        return remain <= maxRemainCard;
    }


    @Override
    public int chupai(long userId, String card) {
        int rtn = super.chupai(userId, card);

        boolean isBanker = this.getFirstTurn() == userId;
        //铲
        int chanSize = chanCards.size();
        if ((isBanker && chanSize == 0) || (chanSize > 0 && chanSize < 4)) {

            chanCards.add(card);
            //达成台庄
            if(isCardSame(chanCards.subList(0,4))){
                //换庄家
                long newBanker = nextTurnId(this.firstTurn);
                this.setFirstTurn(newBanker);
                this.room.setBankerId(newBanker);
                Map<String, Object> r = new HashMap<>();
                r.put("banker", this.getFirstTurn());
                MsgSender.sendMsg2Player("gameService", "noticeTaizhuang", r, users);

                //如果 台庄结束模式 直接荒庄

            }
        }

        return rtn;
    }
}
