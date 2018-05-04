package com.code.server.game.poker.zhaguzi;
import com.code.server.game.poker.pullmice.BaseCardUtils;
import com.code.server.game.poker.pullmice.IfCard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
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
    //四人炸弹
    public static final int SI_ZHA = 2;
    //三人炸弹
    public static final int SAN_ZHA = 3;
    //对子
    public static final int Dui_ZI = 4;
    //单牌
    public static final int DAN_ZI = 5;
    // 错误牌型
    public static final int ERROR = 6;

    private static Map<Integer, Integer> cardDict = new HashMap<>();

    //要考虑 亮三 不亮3 5人玩法， 六人玩法
    public static int compare(PlayerZhaGuZi player1, PlayerZhaGuZi player2){

//        return cardsCompare(player1.cards, player2.cards);
        return 1;
    }

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

                    if (a < b){
                        ret = WIN;
                    }else if (a == b){
                        ret = DRAW;
                    }else {
                        ret = LOSE;
                    }
                    break;

                case SAN_ZHA:

                    if (a < b){
                        ret = WIN;
                    }else if (a == b){
                        ret = DRAW;
                    }else {
                        ret = LOSE;
                    }
                    break;

                case Dui_ZI:

                    if (a < b){
                        ret = WIN;
                    }else if (a == b){
                        ret = DRAW;
                    }else {
                        ret = LOSE;
                    }
                    break;

                case DAN_ZI:

                    if (a < b){
                        ret = WIN;
                    }else if (a == b){
                        ret = DRAW;
                    }else {
                        ret = LOSE;
                    }
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

}
