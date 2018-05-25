package com.code.server.game.poker.zhaguzi;
import com.code.server.game.poker.doudizhu.CardUtil;
import com.code.server.game.poker.hitgoldflower.Player;
import com.code.server.game.poker.pullmice.BaseCardUtils;
import com.code.server.game.poker.pullmice.IfCard;
import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;
import scala.Int;
import java.util.*;

/**
 * Created by dajuejinxian on 2018/5/2.
 */

public class CardUtils extends BaseCardUtils implements CardUtilsError{

    //输赢状况
    //赢了
    public static final int WIN = 0;
    //平手
    public static final int DRAW = 1;
    //输了
    public static final int LOSE = 2;

    //牌型
    //王炸
    public static final int WANG_ZHA = 1;
    //双三
    public static final int SHUANG_SAN = 2;

    //四人炸弹
    public static final int SI_ZHA = 3;
    //三人炸弹
    public static final int SAN_ZHA = 4;
    //对子
    public static final int Dui_ZI = 5;
    //单牌
    public static final int DAN_ZI = 6;
    // 错误牌型
    public static final int ERROR = 7;

    private static Map<Integer, Integer> cardDict = new HashMap<>();

    //牌型转转类
    private static IfCard ifCard = new IfCard() {
        @Override
        public Map<Integer, Integer> cardDict() {
            return CardUtils.getCardDict();
        }
    };

    /*
    * param1 player
    * param2 要出的牌
    * */
    public static int computeCardType(PlayerZhaGuZi playerZhaGuZi, List<Integer> aList){

        Collections.sort(aList);

        if (aList.size() == 1){
            return DAN_ZI;
        }

        if (aList.size() == 2){

            Integer a = aList.get(0);
            Integer b = aList.get(1);
            //王炸
            if (a == 0 && b == 1){

                return WANG_ZHA;
            }

            //六人场中没有双三
            //双三必须是五人场中 并且亮了方片三才会有
            if (playerZhaGuZi.getRoomPersonNum() == 5){

                if (playerZhaGuZi.getLiangList().size() > 0){
                    Integer i = playerZhaGuZi.getLiangList().get(0);

                    if ( i == 9){
                        if (a == 7 && b == 9){
                            return SHUANG_SAN;
                        }
                    }
                }

            }

            if ( a > 1 && b > 1){

                if ((a - 2) / 4 == (b - 2) / 4){
                    return Dui_ZI;
                }else {
                    return ERROR;
                }

            }else {
                return ERROR;
            }
        }

        boolean isFind = true;

        Integer last = aList.get(0);

        for (int i = 1; i < aList.size(); i++){
            Integer current = aList.get(i);
            if ((current - 2) / 4 != (last - 2) / 4){
                isFind = false;
                break;
            }
        }

        if (isFind){

            if (aList.size() == 3){
                return SAN_ZHA;
            }else if (aList.size() == 4){
                return SI_ZHA;
            }

        }

        return ERROR;
    }

    //要考虑 亮三 不亮3 5人玩法， 六人玩法
    public static int compare(PlayerZhaGuZi player1, List<Integer> cards1, PlayerZhaGuZi player2, List<Integer> cards2){

        List<Integer> aList = cards1;
        List<Integer> bList = cards2;

        int typeA = computeCardType(player1, cards1);
        int typeB = computeCardType(player2, cards2);

        List<Integer> lList = new ArrayList<>();
        List<Integer> rList = new ArrayList<>();
        lList.addAll(aList);
        rList.addAll(bList);
        Collections.sort(lList);
        Collections.sort(rList);

        int ret = -1;

        if (typeA == ERROR){
            System.out.println("A类型错误");
            return LEFT_CARDS_ERROR;
        }else if(typeB == ERROR){
            System.out.println("B类型错误");
            return RIGHT_CARDS_ERROR;
        }

        if (typeA != typeB){

            //先比较特殊牌型
            //双三和王炸一样打
            if (player1.getRoomPersonNum() == 5){

                if (typeA == SHUANG_SAN || typeB == WANG_ZHA){
                    return DRAW;
                }

            }


            //右边的人比牌比较大
            if (typeA > typeB){

                if (typeB == Dui_ZI || typeB == DAN_ZI){
                    return RIGHT_CARDS_ERROR;
                }

                return LOSE;

            }else {

                if (typeA == Dui_ZI || typeA == DAN_ZI){
                    return LEFT_CARDS_ERROR;
                }

                return WIN;

            }

        }else {

            Integer a = (lList.get(0) - 2) / 4;
            Integer b = (rList.get(0) - 2) / 4;

            //五人玩法中 红三能不能打4
            if (player1.getRoomPersonNum() == 5){

                //如果是单子
                if (typeA == DAN_ZI){

                    //判断下红桃三可以管4
                    if (lList.get(0) == 7){
                        if (b == 0){
                            return WIN;
                        }
                    }

                    //判断下红桃三可以管4
                    if (rList.get(0) == 7){
                        if (a == 0){
                            return LOSE;
                        }
                    }

                    //方片三是否可以管4
                    if (lList.get(0) == 9){

                        boolean isLiang = false;
                        if (player1.getLiangList().size() != 0){

                            //亮的是 方片饭
                            if (player1.getLiangList().contains(9)){
                                isLiang = true;
                            }

                        }

                        //如果亮3的情况下
                        if (b == 0 && isLiang){
                            return WIN;
                        }
                    }
                    //方片三是否可以管4
                    if (rList.get(0) == 9){


                        boolean isLiang = false;
                        if (player2.getLiangList().size() != 0){

                            Integer liang = player2.getLiangList().get(0);

                            //亮的是 方片饭
                            if (liang == 9){
                                isLiang = true;

                            }
                        }

                        if (a == 0){
                            return LOSE;
                        }
                    }

                }

            }

            switch (typeA){

                case SI_ZHA:

                    ret = compareAB(a, b);
                    break;

                case SAN_ZHA:

                    ret = compareAB(a, b);
                    break;

                case Dui_ZI:

                    ret = compareAB(a, b);
                    break;

                case DAN_ZI:

                    ret = compareAB(a, b);
                    break;
            }
        }

        return ret;

    }


    public static int compareAB(int a, int b){

        int ret = 0;
        if (a < b){
            ret = WIN;
        }else if (a == b){
            ret = DRAW;
        }else {
            ret = LOSE;
        }
        return ret;
    }

    /*
    * 暂时弃用 因为 有些特殊情况不能处理
    * */
    @Deprecated
    public static int cardsCompare(List<Integer> aList, List<Integer> bList){

        int typeA = computeCardType(aList);
        int typeB = computeCardType(bList);

        List<Integer> lList = new ArrayList<>();
        List<Integer> rList = new ArrayList<>();
        lList.addAll(aList);
        rList.addAll(bList);
        Collections.sort(lList);
        Collections.sort(rList);

        int ret = -1;

        if (typeA == ERROR){
            System.out.println("A类型错误");
            return LEFT_CARDS_ERROR;
        }else if(typeB == ERROR){
            System.out.println("B类型错误");
            return RIGHT_CARDS_ERROR;
        }

        if (typeA != typeB){

            //右边的人比牌比较大
            if (typeA > typeB){

                if (typeB == Dui_ZI || typeB == DAN_ZI){
                    return RIGHT_CARDS_ERROR;
                }

                return LOSE;

            }else {

                if (typeA == Dui_ZI || typeA == DAN_ZI){
                    return LEFT_CARDS_ERROR;
                }

                return WIN;

            }

        }else {

            Integer a = (lList.get(0) - 2) / 4;
            Integer b = (rList.get(0) - 2) / 4;

            switch (typeA){

                case SI_ZHA:

                    ret = compareAB(a, b);
                    break;

                case SAN_ZHA:

                    ret = compareAB(a, b);
                    break;

                case Dui_ZI:

                    ret = compareAB(a, b);
                    break;

                case DAN_ZI:

                    ret = compareAB(a, b);
                    break;
            }
        }

        return ret;

    }

    //打印牌型
    public static String cardsTypeDesc(int type){

        String str = null;

        switch (type){

            case WANG_ZHA:
                str = "王炸";
                break;
            case SHUANG_SAN:
                str = "双三";
                break;
            case SI_ZHA:
                str = "四炸";
                break;
            case SAN_ZHA:
                str = "三炸";
                break;
            case Dui_ZI:
                str = "对子";
                break;
            case DAN_ZI:
                str = "单子";
                break;
            default:
                str = "类型错误";

        }

        return str;
    }

    public static String cardsTypeDesc(List<Integer> list){

        int type = computeCardType(list);
        return cardsTypeDesc(type);
    }

    //计算牌型
      /*
    * 暂时弃用 因为 有些特殊情况不能处理
    * */
    @Deprecated
    public static int computeCardType(List<Integer> list){

        List<Integer> aList = new ArrayList<>();
        aList.addAll(list);
        Collections.sort(aList);

        if (aList.size() == 1){
            return DAN_ZI;
        }

        if (aList.size() == 2){

            Integer a = aList.get(0);
            Integer b = aList.get(1);
            //王炸
            if (a == 0 && b == 1){

                return WANG_ZHA;
            }
            //双三 在这里不考虑双三是否起作用
            if (a == 7 && b == 9){
                return SHUANG_SAN;
            }

            if ( a > 1 && b > 1){

                if ((a - 2) / 4 == (b - 2) / 4){
                    return Dui_ZI;
                }else {
                    return ERROR;
                }

            }else {
                return ERROR;
            }
        }
        boolean isFind = true;

        Integer last = aList.get(0);

        for (int i = 1; i < aList.size(); i++){
            Integer current = aList.get(i);
            if ((current - 2) / 4 != (last - 2) / 4){
                isFind = false;
                break;
            }
        }

        if (isFind){

            if (aList.size() == 3){
                return SAN_ZHA;
            }else {
                return SI_ZHA;
            }

        }else {
            return ERROR;
        }
    }

    //胜者组必须有头游并且没有尾游 否则平局
    //0 三家赢 1 平局 2 三家输
    public static int findWinnerList(List<PlayerZhaGuZi> aList){

        int result = 0;

        PlayerZhaGuZi pFirst = null;
        PlayerZhaGuZi pFinal = null;
        for (PlayerZhaGuZi p : aList){

            //去除第一个出完牌的人
            if (p.rank == 1){
                pFirst = p;
            }

            //最有一个人没出完牌 所以排名是没有的
            if (p.rank == 0){
                pFinal = p;
            }
        }

        //平局情况
        if (pFinal.getSanJia() == pFirst.getSanJia()){

            for (PlayerZhaGuZi p : aList){

                p.setIsWinner(PlayerZhaGuZi.DRAW);
            }
            //平局
            return 1;

        }else {

            Integer isSanJia = pFirst.getSanJia();

            for (PlayerZhaGuZi p : aList){

                p.setIsWinner(isSanJia);

            }
        }

        if (pFirst.getSanJia() == PlayerZhaGuZi.SAN_JIA){
            return 0;
        }else {
            return 2;
        }
    }

    public static Map<Integer, Integer> getCardDict() {

        //大王-小王-4-3-2-1-K,Q,J,10,9,8,7,6,5
        if (cardDict.size() == 0){
            cardDict.put(0, 54);
            cardDict.put(1, 53);

            //4
            cardDict.put(2, 13);
            cardDict.put(3, 14);
            cardDict.put(4, 15);
            cardDict.put(5, 16);

            //3
            cardDict.put(6, 9);
            cardDict.put(7, 10);
            cardDict.put(8, 11);
            cardDict.put(9, 12);

            //2
            cardDict.put(10, 5);
            cardDict.put(11, 6);
            cardDict.put(12, 7);
            cardDict.put(13, 8);

            //A
            cardDict.put(14, 1);
            cardDict.put(15, 2);
            cardDict.put(16, 3);
            cardDict.put(17, 4);

            //K
            cardDict.put(18, 49);
            cardDict.put(19, 50);
            cardDict.put(20, 51);
            cardDict.put(21, 52);

            //Q
            cardDict.put(22, 45);
            cardDict.put(23, 46);
            cardDict.put(24, 47);
            cardDict.put(25, 48);

            //J
            cardDict.put(26, 41);
            cardDict.put(27, 42);
            cardDict.put(28, 43);
            cardDict.put(29, 44);

            //10
            cardDict.put(30, 37);
            cardDict.put(31, 38);
            cardDict.put(32, 39);
            cardDict.put(33, 40);

            //9
            cardDict.put(34, 33);
            cardDict.put(35, 34);
            cardDict.put(36, 35);
            cardDict.put(37, 36);

            //8
            cardDict.put(38, 29);
            cardDict.put(39, 30);
            cardDict.put(40, 31);
            cardDict.put(41, 32);

            //7
            cardDict.put(42, 25);
            cardDict.put(43, 26);
            cardDict.put(44, 27);
            cardDict.put(45, 28);

            //6
            cardDict.put(46, 21);
            cardDict.put(47, 22);
            cardDict.put(48, 23);
            cardDict.put(49, 24);

            //5
            cardDict.put(50, 17);
            cardDict.put(51, 18);
            cardDict.put(52, 19);
            cardDict.put(53, 20);

        }
        return cardDict;
    }

    //把字符串转化为数组
    public static List<Integer> transfromStringToCards(String str){

//        if (str.equals("-1")){
//            List<Integer> list = new ArrayList<>();
//            return list;
//        }

        int maxSplit = 1000;
        String[] cards = str.split("_", maxSplit);
        List<Integer> list = new ArrayList<>();

        for (String string : cards){
            if (string.equals("")){
                continue;
            }
            list.add(Integer.valueOf(string));
        }
        return list;
    }

    public static Integer computeIsSanJia(int roomPersonNum, PlayerZhaGuZi playerZhaGuZi) {

        int isSanJia;

        if (roomPersonNum == 5){

            Integer hongtao = 7;
            Integer fangpian = 9;

            if (playerZhaGuZi.cards.size() < 10){
                return PlayerZhaGuZi.UNKNOW;
            }

            if (playerZhaGuZi.cards.contains(hongtao) || playerZhaGuZi.cards.contains(fangpian)){
                isSanJia = PlayerZhaGuZi.SAN_JIA;

            }else {
                isSanJia = PlayerZhaGuZi.GU_JIA;
            }

            return isSanJia;

        }else if (roomPersonNum == 6){

            Integer hongtao = 7;
            Integer fangpian = 9;
            Integer heitao = 6;

            if (playerZhaGuZi.cards.size() < 9){
                return PlayerZhaGuZi.UNKNOW;
            }

            if (playerZhaGuZi.cards.contains(hongtao) || playerZhaGuZi.cards.contains(fangpian) || playerZhaGuZi.cards.contains(heitao)){
                isSanJia = PlayerZhaGuZi.SAN_JIA;
            }else {
                isSanJia = PlayerZhaGuZi.GU_JIA;
            }

            return isSanJia;
        }

        return PlayerZhaGuZi.UNKNOW;

    }

}
