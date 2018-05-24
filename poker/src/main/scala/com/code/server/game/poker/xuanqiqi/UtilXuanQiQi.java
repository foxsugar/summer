package com.code.server.game.poker.xuanqiqi;

import java.util.HashMap;
import java.util.Map;

/**
 * 项目名称：${project_name}
 * 类名称：${type_name}
 * 类描述：
 * 创建人：Clark
 * 创建时间：${date} ${time}
 * 修改人：Clark
 * 修改时间：${date} ${time}
 * 修改备注：
 *
 * @version 1.0
 */
public class UtilXuanQiQi {

    //24张牌，扑克点数
    public static Map<Integer,Integer> cards = new HashMap<>();
    //单张牌判断
    public static Map<Integer,Integer> singleCard = new HashMap<>();

    static {
        cards.put(25,7);
        cards.put(26,7);
        cards.put(27,7);
        cards.put(28,7);

        cards.put(29,8);
        cards.put(30,8);
        cards.put(31,8);
        cards.put(32,8);

        cards.put(33,9);
        cards.put(34,9);
        cards.put(35,9);
        cards.put(36,9);

        cards.put(37,10);
        cards.put(38,10);

        cards.put(39,10);
        cards.put(40,10);

        cards.put(41,11);
        cards.put(42,11);

        cards.put(45,12);
        cards.put(46,12);

        cards.put(49,13);
        cards.put(50,13);

        //大小王
        cards.put(53,14);
        cards.put(54,14);

        //=============================

        singleCard.put(25,9);
        singleCard.put(26,10);
        singleCard.put(27,9);
        singleCard.put(28,10);

        singleCard.put(29,11);
        singleCard.put(30,12);
        singleCard.put(31,11);
        singleCard.put(32,12);

        singleCard.put(33,13);
        singleCard.put(34,14);
        singleCard.put(35,13);
        singleCard.put(36,14);

        singleCard.put(37,15);
        singleCard.put(38,16);
        singleCard.put(39,15);
        singleCard.put(40,16);

        singleCard.put(41,1);
        singleCard.put(42,2);

        singleCard.put(45,3);
        singleCard.put(46,4);

        singleCard.put(49,5);
        singleCard.put(50,6);

        //大小王
        cards.put(53,7);
        cards.put(54,8);
    }

    public static boolean getOneCardWin(int a ,int b){
        return singleCard.get(a) >= singleCard.get(b);
    }

    public static boolean getTwoCardWin(int a1,int b1,int a2,int b2){
        //TODO 比牌 两张
        return true;
    }

    public static boolean getThreeCardWin(int a1,int b1,int c1,int a2,int b2,int c2){
        //TODO 比牌 三张
        return true;
    }



}
