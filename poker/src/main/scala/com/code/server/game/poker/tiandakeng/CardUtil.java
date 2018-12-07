package com.code.server.game.poker.tiandakeng;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.code.server.game.poker.tiandakeng.GameTDK.*;

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


    protected static boolean hasSiTiao(List<Integer> cards) {
        Map<Integer, Integer> map = new HashMap<>();
        cards.forEach(card->map.put(getCardType(card), map.getOrDefault(getCardType(card), 0) + 1));
        return map.values().stream().anyMatch(count -> count == 4);
    }


    public static void shuffleCard(GameTDK gameTDK, List<Integer> cards) {
        //必有A
        cards.add(1);
        cards.add(2);
        cards.add(3);
        cards.add(4);

        //必有J Q K
        for (int i = 0; i < 12; i++) {
            cards.add(41 + i);
        }

        //从10起
        if (gameTDK.isHasMode(model_半坑_10)) {
            for (int i = 0; i < 4; i++) {
                cards.add(37 + i);
            }
        }

        //从9起
        if (gameTDK.isHasMode(model_半坑_9)) {
            for (int i = 0; i < 8; i++) {
                cards.add(33 + i);
            }
        }

        //从8起
        if (gameTDK.isHasMode(model_半坑_8)) {
            for (int i = 0; i < 12; i++) {
                cards.add(29 + i);
            }
        }

        //从5起
        if (gameTDK.isHasMode(model_莆田半坑_5)) {
            for (int i = 0; i < 24; i++) {
                cards.add(17 + i);
            }
        }

        //从2起
        if (gameTDK.isHasMode(model_莆田全坑_2) || gameTDK.isHasMode(model_全坑_2)) {
            for (int i = 0; i < 36; i++) {
                cards.add(5 + i);
            }
        }

        //带王
        if (gameTDK.isHasMode(model_带王)) {
            cards.add(53);
            cards.add(54);
        }

        Collections.shuffle(cards);
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

    /**
     * 获得牌的分数
     * @param card
     * @param isABiPao
     * @return
     */
    protected static int getCardScore(int card, boolean isABiPao) {
        int cardType = getCardType(card);
        if (card == 54) {
            return isABiPao?14:16;
        }
        if (card == 53) {
            return 14;
        }
        if (cardType == 1) {
            return 15;
        }
        return cardType;

    }
}
