package com.code.server.game.poker.tiandakeng;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sunxianping on 2018-10-25.
 */
public class CardUtil {

    /**
     * 是否有豹子
     * @param cards
     * @return
     */
    protected static boolean hasBaozi(List<Integer> cards){
        Map<Integer, Integer> map = new HashMap<>();
        cards.forEach(card->map.put(getCardType(card), map.getOrDefault(getCardType(card), 0) + 1));
        return map.values().stream().anyMatch(count -> count == 3);
    }

    /**
     * 是否有双王
     * @param cards
     * @return
     */
    protected static boolean hasShuangWang(List<Integer> cards) {
        return cards.stream().filter(card->getCardType(card) == 14).count() == 2;
    }

    /**
     * 获得牌的类型
     * @param card
     * @return
     */
    protected static int getCardType(int card) {
        return (card - 1)/4 + 1;
    }
}
