package com.code.server.game.poker.tuitongzi;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TuiTongZiCardUtils {
    /**
     * 所有牌型
     * */
    public static final int Dui_Jiu = 19;
    public static final int Dui_Ba = 18;
    public static final int Dui_Qi = 17;
    public static final int Dui_Liu = 16;
    public static final int Dui_Wu = 15;
    public static final int Dui_Si = 14;
    public static final int Dui_San = 13;
    public static final int Dui_Er = 12;
    public static final int Dui_Yi = 11;

    public static final int Jiu = 9;
    public static final int Ba = 8;
    public static final int Qi = 7;
    public static final int Liu = 6;
    public static final int Wu = 5;
    public static final int Si = 4;
    public static final int San = 3;
    public static final int Er = 2;
    public static final int Yi = 1;
    public static final int Ling = 0;


    public static boolean zhuangIsBiggerThanXian(PlayerTuiTongZi pZhuang, PlayerTuiTongZi pXian) throws Exception {

        return (mAIsBiggerThanB(pZhuang, pXian) != 2);
    }

    public static int cardsPatterns(List<Integer> list) throws Exception {

        if (isDuiZi(list)){
            int ret = list.get(0) / 4 + 1;
            return ret + 10;
        }

        int pointA = ((list.get(0) / 4 + 1) + (list.get(1) / 4 + 1))  % 10;
        return pointA;
    }

    public static int mAIsBiggerThanB(List<Integer> listA, List<Integer> listB) throws Exception {
        boolean aIsDuiZi = isDuiZi(listA);
        boolean bIsDuiZi = isDuiZi(listB);
        if (aIsDuiZi == true && bIsDuiZi == false){

            return 0;
        }else if (bIsDuiZi == true && aIsDuiZi == false){

            return 2;
        }else if (aIsDuiZi && bIsDuiZi){

            if (listA.get(0) /4 == listB.get(0)/ 4){
                return 1;
            }else if(listA.get(0) / 4 > listB.get(0) / 4){
                return 0;
            }else {
                return 2;
            }
        }else {

            Integer pointA = ((listA.get(0) / 4 + 1) + (listA.get(1) / 4 + 1))  % 10;
            Integer pointB = ((listB.get(0) / 4 + 1) + (listB.get(1) / 4 + 1))  % 10;
            if (pointA > pointB){
                return 0;
            }else if(pointA == pointB){
                return 1;
            }else {
                return 2;
            }
        }
    }

    public static int mAIsBiggerThanB(PlayerTuiTongZi pA, PlayerTuiTongZi pB) throws Exception {

        List<Integer> listA = pA.getPlayerCards();
        List<Integer> listB = pB.getPlayerCards();
        return mAIsBiggerThanB(listA, listB);
    }

    /**
     *综合排序，先根据获胜方的牌大小排序，如果大小相等，
     * */
    public List<PlayerTuiTongZi> findWinner(List<PlayerTuiTongZi> pList) throws Exception {

        for (Integer i = 0; i < pList.size() - 1; i++){

            for (Integer j = i + 1; j < pList.size(); j++){

                int ret = mAIsBiggerThanB(pList.get(i), pList.get(j));

                if (ret == 2){
                    Collections.swap(pList, i, j);
                }
            }
        }

        return pList;
    }

    public static boolean isDuiZi(List<Integer> list) throws Exception {

        if (list.size() != 2){
            throw new Exception("牌数错误！");
        }
        if (list.get(0) / 4 != list.get(1) / 4){
            return false;
        }
        return true;
    }

    public static List<Integer> cheat(List<Integer> source, int cardType) throws Exception {

        List<Integer> list = null;
        if (source.size() == 0){
            return list;
        }else {
            for (int i = 0; i < source.size() - 1; i++) {
                for (int j = i + 1; j < source.size(); j++) {
                    List<Integer> aList = new ArrayList<>();
                    aList.add(source.get(i));
                    aList.add(source.get(j));
                    int ret = TuiTongZiCardUtils.cardsPatterns(aList);
                    if (cardType == ret) {
                        list = aList;
                        break;
                    }
                }
            }
            return list;
        }
    }

}
