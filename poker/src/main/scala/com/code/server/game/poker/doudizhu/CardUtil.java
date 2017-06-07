package com.code.server.game.poker.doudizhu;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/21.
 */
public class CardUtil {
    protected static List<List<Integer>> typeCard = new ArrayList<>();//牌大小排列
    private static List<Integer> card = new ArrayList<>();


    static {
        for(int i=9;i<=52;i+=4){
            List<Integer> l = new ArrayList<>();
            l.add(i);
            l.add(i+1);
            l.add(i+2);
            l.add(i+3);
            typeCard.add(l);
        }
        //把A和2,小王，大王放在最后
        List<Integer> cardA = new ArrayList<>();
        cardA.add(1);
        cardA.add(2);
        cardA.add(3);
        cardA.add(4);
        List<Integer> card2 = new ArrayList<>();
        card2.add(6);
        card2.add(8);
        List<Integer> cardxiao = new ArrayList<>();
        cardxiao.add(53);
        List<Integer> cardda = new ArrayList<>();
        cardda.add(54);

        typeCard.add(cardA);
        typeCard.add(card2);
        typeCard.add(cardxiao);
        typeCard.add(cardda);


        card.add(3);
        card.add(4);
        card.add(5);
        card.add(6);
        card.add(7);
        card.add(8);
        card.add(9);
        card.add(10);
        card.add(11);
        card.add(12);
        card.add(13);
        card.add(1);
        card.add(2);
        card.add(14);
        card.add(15);
    }


    public static int getCardType(int card) {
        return (card -1)/4+1;
    }
    public static Integer getTypeByCard (Integer card){
        if(card == 54){
            return 15;
        }
        return CardUtil.card.indexOf((card -1)/4+1);
    }

}

