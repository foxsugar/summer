package com.code.server.game.poker.xuanqiqi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    //两张牌判断
    public static List<Integer> jqkBlackCard = new ArrayList<>();
    public static List<Integer> jqkRedCard = new ArrayList<>();

    public static List<Integer> doubleWangList = new ArrayList<>();
    public static List<Integer> redTenlist = new ArrayList<>();

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

        singleCard.put(45,1);
        singleCard.put(46,2);

        singleCard.put(49,1);
        singleCard.put(50,2);

        //大小王
        singleCard.put(53,7);
        singleCard.put(54,8);

        jqkRedCard.add(42);
        jqkRedCard.add(46);
        jqkRedCard.add(50);
        jqkBlackCard.add(41);
        jqkBlackCard.add(45);
        jqkBlackCard.add(49);

        doubleWangList.add(53);
        doubleWangList.add(54);

        redTenlist.add(38);
        redTenlist.add(40);
    }

    public static boolean getOneCardWin(int a ,int b){
        return singleCard.get(a) >= singleCard.get(b);
    }

    public static boolean getTwoCardWin(int a1,int b1,int a2,int b2){
        if(ifTwoCard(a1,b1)!=-1 && ifTwoCard(a2,b2)==-1){
            return true;
        }else if(ifTwoCard(a1,b1)==-1 && ifTwoCard(a2,b2)!=-1){
            return false;
        }else{
            return ifTwoCard(a1,b1)>ifTwoCard(a2,b2);
        }
    }

    public static boolean getThreeCardWin(int a1,int b1,int c1,int a2,int b2,int c2){
        if(ifRed(a1,b1,c1)){
            return true;
        }else if(ifRed(a2,b2,c2)){
            return false;
        }else if(ifBlack(a1,b1,c1) && !ifRed(a2,b2,c2)){
            return true;
        }else if(ifBlack(a2,b2,c2) && !ifRed(a1,b1,c1)){
            return false;
        }
        return true;
    }

    /**
     * 判断三张牌是不是红JQK
     * @param x
     * @param y
     * @param z
     * @return
     */
    private static boolean ifRed(int x,int y,int z){
        boolean b = false;
        List<Integer> redList = new ArrayList<>();
        redList.add(42);
        redList.add(46);
        redList.add(50);
        if(redList.contains(x)&&redList.contains(y) && redList.contains(z)){
            return true;
        }
        return b;
    }

    /**
     * 判断三张牌是不是黑JQK
     * @param x
     * @param y
     * @param z
     * @return
     */
    private static boolean ifBlack(int x,int y,int z){
        boolean b = false;
        List<Integer> blackList = new ArrayList<>();
        blackList.add(41);
        blackList.add(45);
        blackList.add(49);
        if(blackList.contains(x)&&blackList.contains(y) && blackList.contains(z)){
            return true;
        }
        return b;
    }

    /**
     * 是否为两张有效牌
     * @param x
     * @param y
     * @return
     */
    public static int ifTwoCard(int x,int y){
        int result = -1;
        if((x==53 && y==54)||(x==54 && y==53)){
            result = 888;
        }else if((x==38 && y==40)||(x==40 && y==38)){
            result = 887;
        }else if((x==37 && y==39)||(x==39 && y==37)){
            result = 886;
        }else if((x==34 && y==36)||(x==36 && y==34)){
            result = 885;
        }else if((x==33 && y==35)||(x==35 && y==33)){
            result = 884;
        }else if((x==30 && y==32)||(x==32 && y==30)){
            result = 883;
        }else if((x==29 && y==31)||(x==31 && y==29)){
            result = 882;
        }else if((x==26 && y==28)||(x==28 && y==26)){
            result = 881;
        }else if((x==25 && y==27)||(x==27 && y==25)){
            result = 880;
        }else if(jqkRedCard.contains(x)&&jqkRedCard.contains(y)){
            result = 879;
        }else if(jqkBlackCard.contains(x)&&jqkBlackCard.contains(y)){
            result = 878;
        }
        return result;
    }
}
