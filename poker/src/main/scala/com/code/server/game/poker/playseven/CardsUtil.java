package com.code.server.game.poker.playseven;

import java.util.*;

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
        for (Integer i :hei){
            System.out.println(i);
        }
    }

    /*
           108张牌编号  -54----54   黑红花片
           54       大王  15
           53       小王  14
           49-52    7
           45-48    2
           41-44    A
           17-40    8-K
           1-16     3-6
     */
    public static Map<Integer,Integer> cardsOf108 = new HashMap<>();
    public static List<Integer> hei = new ArrayList<>();
    public static List<Integer> hong = new ArrayList<>();
    public static List<Integer> hua = new ArrayList<>();
    public static List<Integer> pian = new ArrayList<>();

    public static Map<Integer,Integer> cardsOfScore = new HashMap<>();

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
        //黑红花片分组
        List<Integer> tempList = new ArrayList<>();
        tempList.addAll(cardsOf108.keySet());
        tempList.remove((Integer)54);
        tempList.remove((Integer)53);
        tempList.remove((Integer)(-54));
        tempList.remove((Integer)(-53));

        for (Integer i:tempList) {
            if(Math.abs(i)%4==1){
                hei.add(i);
            }else if(Math.abs(i)%4==2){
                hong.add(i);
            }else if(Math.abs(i)%4==3){
                hua.add(i);
            }else if(Math.abs(i)%4==0){
                pian.add(i);
            }
        }

        for (Integer integer:cardsOf108.keySet()) {
            cardsOfScore.put(integer,0);
        }
        cardsOfScore.put(37,10);cardsOfScore.put(38,10);cardsOfScore.put(39,10);cardsOfScore.put(40,10);
        cardsOfScore.put(25,10);cardsOfScore.put(26,10);cardsOfScore.put(27,10);cardsOfScore.put(28,10);
        cardsOfScore.put(9,5);cardsOfScore.put(10,5);cardsOfScore.put(11,5);cardsOfScore.put(12,5);
        cardsOfScore.put(-37,10);cardsOfScore.put(-38,10);cardsOfScore.put(-39,10);cardsOfScore.put(-40,10);
        cardsOfScore.put(-25,10);cardsOfScore.put(-26,10);cardsOfScore.put(-27,10);cardsOfScore.put(-28,10);
        cardsOfScore.put(-9,5);cardsOfScore.put(-10,5);cardsOfScore.put(-11,5);cardsOfScore.put(-12,5);
    }


    public static boolean compareCards(int huase,int chuHuaSe,List<Integer> before,List<Integer> after){

        if(1==before.size() && 1==after.size()){
            return compareOneCard(huase, chuHuaSe,before, after);
        }else if(2==before.size() && 2==after.size()){
            if(after.get(0)+after.get(1)==0){
                return compareOneCard(huase,chuHuaSe, before, after);
            }else{
                return true;
            }

        }else if(before.size()>=4 && after.size()>=4){
            return compareTuoLaJi(before, after);
        }
        return true;
    }

    private static Boolean compareTuoLaJi(List<Integer> before, List<Integer> after) {
        if(isTuoLaJi(before) && !isTuoLaJi(after)){
            return true;
        }else{
            Collections.sort(before);
            Collections.sort(after);
            return before.get(before.size()-1) > after.get(after.size()-1);
        }
    }

    private static Boolean compareOneCard(int huase, int chuHuaSe,List<Integer> before, List<Integer> after) {
        int a = Math.abs(before.get(0));
        int b = Math.abs(after.get(0));
        if(a==b){//大小王
            return true;
        }else if(a==54 && b==53){
            return true;
        }else if(a==54 && b!=53 && b!=54){
            return true;
        }else if(a==53 && b!=53 && b!=54){
            return true;
        }else if(a==53 && b==54){
            return false;
        }else if(a!=54 && a!=53 && (b==53||b==54)){
            return false;
        }else{//主牌
            if(a>44&&b<=44){
                return true;
            }else if(a<=44&&b>44){
                return false;
            }else if(a>44&&b>44){
                if(cardsOf108.get(a)!=cardsOf108.get(b)){
                    return a>b;
                }else {
                    if(huase==a%4 && huase==b%4){
                        return true;
                    }else if(huase==a%4 && huase!=b%4){
                        return true;
                    }else if(huase!=a%4 && huase==b%4){
                        return false;
                    }else if(huase!=a%4 && huase!=b%4){
                        return true;
                    }
                }
            }else {
                if(huase==a%4 && huase==b%4){
                    return a>=b;
                }else if(huase==a%4 && huase!=b%4){
                    return true;
                }else if(huase!=a%4 && huase==b%4){
                    return false;
                }else if(huase!=a%4 && huase!=b%4){
                    //return a>b;
                    /*if(cardsOf108.get(a)>=cardsOf108.get(b)){
                        return true;
                    }else{
                        return false;
                    }*/
                    if(chuHuaSe==a%4 && chuHuaSe==b%4){
                        return a>b;
                    }else{
                        return true;
                    }
                }
            }
        }
        return null;
    }


    public static Boolean isTuoLaJi(List<Integer> list){
        boolean b0;
        boolean b1 = true;
        boolean b2 = true;
        int temp0 = 0;
        for (Integer integer:list) {//判断是否两个一对
            temp0+=integer;
        }
        b0 = temp0==0;

        int temp1 = Math.abs(list.get(0))%4;
        a:for (Integer integer:list) {//判断是否同花色
            if(temp1!=Math.abs(integer)%4){
                b1 = false;
                break a;
            }
        }

        List<Integer> sortList = new ArrayList<>();
        for (Integer integer:list) {
            if(integer>0){
                sortList.add((integer-temp1)/4);
            }
        }
        Collections.sort(sortList);
        a:for (int i = 0; i < sortList.size()-1; i++) {
            b:for (int j = i+1; j < sortList.size(); j++) {
                if(sortList.get(i)+1==sortList.get(j)){
                    break b;
                }else {
                    b2 = false;
                    break a;
                }
            }
        }

        return b0 && b1 && b2;
    }

    //把字符串转化为数组
    public static List<Integer> transfromStringToCards(String str){

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
}
