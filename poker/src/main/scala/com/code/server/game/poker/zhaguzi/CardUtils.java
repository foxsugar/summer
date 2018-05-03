package com.code.server.game.poker.zhaguzi;
import com.code.server.game.poker.pullmice.BaseCardUtils;
import scala.Int;
import java.util.*;

/**
 * Created by dajuejinxian on 2018/5/2.
 */

public class CardUtils extends BaseCardUtils{

    //王炸
    public static final int WANG_ZHA = 1;
    //四人炸弹
    public static final int SI_ZHA = 1;
    //三人炸弹
    public static final int SAN_ZHA = 2;
    //对子
    public static final int Dui_ZI = 3;
    //单牌
    public static final int DAN_ZI = 4;
    // 错误牌型
    public static final int ERROR = 5;

    private static Map<Integer, Integer> cardDict = new HashMap<>();

    public static int cardsCompare(List<Integer> aList, List<Integer> bList){

        int typeA = computeCardType(aList);
        int typeB = computeCardType(bList);

        if (typeA == ERROR){
            System.out.println("A类型错误");
            return -1;
        }else if(typeB == ERROR){
            System.out.println("B类型错误");
            return -2;
        }

        if (typeA != typeB){

        }else {

        }
        return 1;

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
