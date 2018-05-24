package com.code.server.game.poker.tuitongzi;

import java.util.List;

/**
 * Created by dajuejinxian on 2018/5/16.
 */


public class TuiTongTongCardUtils extends TuiTongZiCardUtils{


    public static boolean zhuangIsBiggerThanXian(PlayerTuiTongZi pZhuang, PlayerTuiTongZi pXian) throws Exception {

        int type = cardsPatterns(pXian.getPlayerCards());
        //如果闲家是 0 点 必须输
        if (type == Ling){
            return true;
        }
        return (TuiTongTongCardUtils.mAIsBiggerThanB(pZhuang, pXian) != 2);
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

                //
                Integer a = (listA.get(0) / 4 + 1);
                Integer b = (listA.get(1) / 4 + 1);

                Integer c = (listB.get(0) / 4 + 1);
                Integer d = (listB.get(1) / 4 + 1);

                //平点的情况下在比较手中最大的牌
                Integer maxA = a > b ? a : b;
                Integer maxB = c > d ? c : d;

                if (maxA > maxB){
                    return 0;
                }else if(maxA == maxB){
                    return 1;
                }else {
                    return 2;
                }

            }else {
                return 2;
            }
        }
    }
}
