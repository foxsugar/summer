package com.code.server.game.poker.playseven;

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
public class CardsUtil {

    public static void main(String[] args) {
        for (Integer integer:CardsUtil.cardsOf108.keySet()) {
            System.out.println("CardId:"+integer+" ==== CardNum:"+CardsUtil.cardsOf108.get(integer));
        }
        System.out.println(cardsOf108.size());
    }

    /*
           108张牌编号  -54----54   黑红花片
           54       大王  15
           53       小王  14
           49-52    7
           45-48    2
           41-44    A
           17-40    K-8
           1-46     6-3
     */
    public static Map<Integer,Integer> cardsOf108 = new HashMap<>();

    static {

        //大王，小王，7,2,1
        cardsOf108.put(54,15);cardsOf108.put(53,14);
        cardsOf108.put(49,7);cardsOf108.put(50,7);cardsOf108.put(51,7);cardsOf108.put(52,7);
        cardsOf108.put(45,2);cardsOf108.put(46,2);cardsOf108.put(47,2);cardsOf108.put(48,2);
        cardsOf108.put(41,1);cardsOf108.put(42,1);cardsOf108.put(43,1);cardsOf108.put(44,1);

        //标记8-K
        int temp = 0;
        int putTemp = 8;
        for (int i = 17; i < 41; i++) {

            if(temp!=4){
                cardsOf108.put(i,putTemp);
                temp++;
            }else{
                i--;
                putTemp++;
                temp=0;
            }
        }

        //标记3-6
        int temp1 = 0;
        int putTemp1 = 3;
        for (int i = 1; i < 17; i++) {
            if(temp1!=4){
                cardsOf108.put(i,putTemp1);
                temp1++;
            }else{
                i--;
                putTemp1++;
                temp1=0;
            }
        }

        //设置第二幅牌，为负编号
        Map<Integer,Integer> tempMap = new HashMap<>();
        for (Integer integer:cardsOf108.keySet()) {
            tempMap.put(-integer,cardsOf108.get(integer));
        }
        cardsOf108.putAll(tempMap);

        //=========================================================================================



    }





}
