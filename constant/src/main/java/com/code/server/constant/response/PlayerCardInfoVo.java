package com.code.server.constant.response;

import com.code.server.cardgame.core.doudizhu.PlayerCardInfoDouDiZhu;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunxianping on 2017/3/20.
 */
public class PlayerCardInfoVo {
    public long userId;
    public List<Integer> cards = new ArrayList<>();//手上的牌
    public int cardNum;
    public boolean isQiang;
    public double score;

    public PlayerCardInfoVo() {
    }

    public PlayerCardInfoVo(PlayerCardInfoDouDiZhu playerCardInfo, long uid) {
        this.userId = playerCardInfo.userId;
        if (playerCardInfo.userId == uid) {
            this.cards.addAll(playerCardInfo.cards);
        } else {
            this.cardNum = playerCardInfo.cards.size();
            this.isQiang = playerCardInfo.isQiang();
        }
    }

    public PlayerCardInfoVo(PlayerCardInfoDouDiZhu playerCardInfo) {
        this.userId = playerCardInfo.userId;
        this.cards.addAll(playerCardInfo.cards);
        this.isQiang = playerCardInfo.isQiang();
        this.score = playerCardInfo.getScore();
    }
}
