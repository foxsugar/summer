package com.code.server.game.poker.cow;

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
 * @version 1Long.0
 */
public class CardUtils {

    public final static int TONG_HUA_SHUN = 1;
    public final static int ZHA_DAN_NIU = 2;
    public final static int WU_HUA_NIU = 3;
    public final static int WU_XIAO_NIU = 4;
    public final static int HU_LU = 5;
    public final static int TONG_HUA = 6;
    public final static int SHUN_ZI = 7;
    public final static int NIU_NIUI = 8;
    public final static int NIU_JIU  = 9;
    public final static int NIU_BA = 10;
    public final static int NIU_QI = 11;
    public final static int NIU_Liu = 12;
    public final static int NIIU_WU = 13;
    public final static int NIU_SI = 14;
    public final static int NIU_SAN = 15;
    public final static int NIU_ER = 16;
    public final static int NIU_YI = 17;
    public final static int WU_NIU = 18;

    public  static Map<Integer, Integer> cardsDict;

    public static Map<Integer, Integer> multipleMap;

    static {

        cardsDict = new HashMap<Integer, Integer>();
        //A
        cardsDict.put(1, 0);
        cardsDict.put(2, 1);
        cardsDict.put(3, 2);
        cardsDict.put(4, 3);

        //2
        cardsDict.put(5, 48);
        cardsDict.put(6, 49);
        cardsDict.put(7, 50);
        cardsDict.put(8, 51);

        //3
        cardsDict.put(9, 44);
        cardsDict.put(10, 45);
        cardsDict.put(11, 46);
        cardsDict.put(12, 47);

        //4
        cardsDict.put(13, 40);
        cardsDict.put(14, 41);
        cardsDict.put(15, 42);
        cardsDict.put(16, 43);

        //5
        cardsDict.put(17, 36);
        cardsDict.put(18, 37);
        cardsDict.put(19, 38);
        cardsDict.put(20, 39);

        //6
        cardsDict.put(21, 32);
        cardsDict.put(22, 33);
        cardsDict.put(23, 34);
        cardsDict.put(24, 35);

        //7
        cardsDict.put(25, 28);
        cardsDict.put(26, 29);
        cardsDict.put(27, 30);
        cardsDict.put(28, 31);

        //8
        cardsDict.put(29, 24);
        cardsDict.put(30, 25);
        cardsDict.put(31, 26);
        cardsDict.put(32, 27);

        //9
        cardsDict.put(33, 20);
        cardsDict.put(34, 21);
        cardsDict.put(35, 22);
        cardsDict.put(36, 23);

        //10
        cardsDict.put(37, 16);
        cardsDict.put(38, 17);
        cardsDict.put(39, 18);
        cardsDict.put(40, 19);

        //J
        cardsDict.put(41, 12);
        cardsDict.put(42, 13);
        cardsDict.put(43, 14);
        cardsDict.put(44, 15);

        //Q
        cardsDict.put(45, 8);
        cardsDict.put(46, 9);
        cardsDict.put(47, 10);
        cardsDict.put(48, 11);

        //K
        cardsDict.put(49, 4);
        cardsDict.put(50, 5);
        cardsDict.put(51, 6);
        cardsDict.put(52, 7);

        multipleMap.put(18,1);
        multipleMap.put(17,1);
        multipleMap.put(16,2);
        multipleMap.put(15,3);
        multipleMap.put(14,4);
        multipleMap.put(13,5);
        multipleMap.put(12,6);
        multipleMap.put(11,7);
        multipleMap.put(10,8);
        multipleMap.put(9,9);
        multipleMap.put(8,10);
        multipleMap.put(7,15);
        multipleMap.put(6,16);
        multipleMap.put(5,17);
        multipleMap.put(4,18);
        multipleMap.put(3,19);
        multipleMap.put(2,20);
        multipleMap.put(1,25);

    }

    public static Integer transformCardValue(Integer clientValue){
        return cardsDict.get(clientValue);
    }

    public static String getNameWithGrade(Integer grade_){

        if (grade_ > 18 || grade_ < 1){
            throw new NullPointerException("grade_ invalid");
        }
        String str = "";
        if (grade_ == 1){
            str = "同花顺";
        }else if(grade_ == 2){
            str = "炸弹牛";
        }else if(grade_ == 3){
            str = "五花牛";
        }else if (grade_ == 4){
            str = "五小牛";
        }else if (grade_ == 5){
            str = "葫芦";
        }else if (grade_ == 6){
            str = "同花";
        }else if (grade_ == 7){
            str = "顺子";
        }else if (grade_ == 8){
            str = "牛牛";
        }else if (grade_ == 18){
            str = "无牛";
        }else {
            str = "牛" + (18 - grade_);
        }
        return "[" + str +"]";
    }

    public static Integer getTransformPaiXing(List<Integer> list) throws Exception {

        List<Integer> aList = new ArrayList<Integer>();

        for (Integer i = 0; i < list.size(); i++){
            aList.add(cardsDict.get(list.get(i)));
        }
        return getPaiXing(aList);
    }

    public static Integer getPaiXing(List<Integer> list) throws Exception {

        if (list.size() != 5){
            throw new Exception("牌数必须是5张！");
        }

        Collections.sort(list);

        if (isTongHuaShun(list)){
            return TONG_HUA_SHUN;
        }
        if (isZhaDanNiu(list)){
            return ZHA_DAN_NIU;
        }
        if (isWuHuaNiu(list)){
            return WU_HUA_NIU;
        }
        if (isWuXiaoNiu(list)){
            return WU_XIAO_NIU;
        }
        if (isHuLu(list)){
            return HU_LU;
        }
        if (isTongHua(list)){
            return TONG_HUA;
        }
        if (isShunZi(list)){
            return SHUN_ZI;
        }
        return niu_x(list);
    }

    public static Integer calculateDianShu(Integer pokerIndex){

        if (pokerIndex / 4 == 0){
            return 1;
        }else if (pokerIndex / 4 == 1 || pokerIndex / 4 == 2 || pokerIndex / 4 == 3 || pokerIndex / 4 == 4){
            return 10;
        }else {
            return 14 - pokerIndex / 4;
        }
    }

    protected static boolean isTongHuaShun(List<Integer> list){

        return isShunZi(list) && isTongHua(list);
    }

    protected static boolean isZhaDanNiu(List<Integer> list){

        Integer last = list.get(1) / 4;
        for (Integer i = 2; i < list.size() - 1; i++){
            Integer current = list.get(i) / 4;
            if (last != current){
                return false;
            }
            last = current;
        }

        if ((list.get(0) / 4 != list.get(1) / 4) &&  (list.get(list.size() - 1) / 4 != list.get(1) / 4)){
            return false;
        }
        return true;
    }

    protected static boolean isWuHuaNiu(List<Integer> list){

        for (Integer i = 0; i < list.size(); i++){
            if (list.get(i) < 4 || list.get(i) > 15){
                return false;
            }
        }

        int count = 0;

        for (Integer i = 0; i < list.size(); i++){

            // 含有k
            if (list.get(i) / 4 == 1){
                 count += 100;
            }

            // 含有q
            if (list.get(i) / 4 == 2){
                count += 10;
            }

            //含有j
            if (list.get(i) / 4 == 3){
                count += 1;
            }

            if ((count / 100 > 0) && (count /10 % 10 > 0) && (count % 10 > 0)){
                return true;
            }
        }

        return false;
    }

    protected static boolean isWuXiaoNiu(List<Integer> list){

        Integer sum = 0;
        for (Integer i = 0; i < list.size(); i++){
            Integer dianshu = calculateDianShu(list.get(i));
            sum += dianshu;
        }
        return sum < 10;
    }

    protected static boolean isHuLu(List<Integer> list){

        List<Integer> aList = new ArrayList<Integer>();
        aList.addAll(list);
        Integer centerPoker = aList.get(2) / 4;
        if (centerPoker == aList.get(0) / 4 && centerPoker == aList.get(1) / 4){
            if (aList.get(3) / 4 == aList.get(4) / 4){
                return true;
            }
        }

        if(centerPoker == aList.get(3) / 4 && centerPoker == aList.get(4) / 4){
            if (aList.get(0) / 4 == aList.get(1) / 4){
                return true;
            }
        }

        return false;

    }

    protected static boolean isTongHua(List<Integer> list){

        Integer last = list.get(0) % 4;
        for (Integer i = 1; i < list.size(); i++){
            Integer current = list.get(i) % 4;
            if (last != current){
                return false;
            }
            last = current;
        }
        return true;
    }

    protected static boolean isShunZi(List<Integer> list){

        Integer last = list.get(0) / 4;
        //判断是不是12345
        boolean isA2345 = isA2345(list);
        if (isA2345) return true;

        for (Integer i = 1; i < list.size(); i++){
            Integer current = list.get(i) / 4;
            if (current - last != 1){
                return false;
            }
            last  = current;
        }
        return true;
    }

    // 如果是顺子的情况下 判断是不是最小的顺子 12345
    protected static boolean isA2345(List<Integer> list){

        Integer last = list.get(0) / 4;
        //判断是不是12345
        if (last == 0){
            if (list.get(1) / 4 == 9 && list.get(2) /4 == 10 && list.get(3) / 4 == 11 && list.get(4) / 4 == 12){
                return true;
            }
        }
        return false;
    }

    protected static Integer niu_x(List<Integer> list){

        List<Integer> aList = new ArrayList<Integer>();
//        Collections.copy(aList, list);
        aList.addAll(list);
        boolean isFind = false;
        for (Integer i = 0; i < list.size() - 2; i++){

            if (isFind == true) break;

            for (Integer j = i + 1; j < list.size() - 1; j++){

                if (isFind == true) break;

                for (Integer k = j + 1; k < list.size(); k++){

                   Integer sum = calculateDianShu(list.get(i)) + calculateDianShu(list.get(j)) + calculateDianShu(list.get(k));

                   if (sum % 10 == 0){
                       isFind = true;
                       aList.remove(list.get(i));
                       aList.remove(list.get(j));
                       aList.remove(list.get(k));
                       break;
                   }
                }
            }
        }

        if (isFind == false) return WU_NIU;

        Integer sum = 0;
        for (Integer i = 0; i < aList.size(); i++){
            sum += calculateDianShu(aList.get(i));
        }

        if (sum / 10 == 0){
            return NIU_NIUI;
        }
        return NIU_NIUI - sum % 10 + 10;
    }

    public static CowPlayer findWinner(CowPlayer...player){

        List<CowPlayer> list = Arrays.asList(player);

        return findWinner(list);
    }

    public static CowPlayer findWinner(List<CowPlayer> list){
        //对数组进行排序
        List<CowPlayer> aList = new ArrayList<CowPlayer>();
        aList.addAll(list);
        for (Integer i = 0; i < aList.size() - 1; i++){

            for (Integer j = i + 1; j < aList.size(); j++){

                CowPlayer playerI = aList.get(i);
                CowPlayer playerJ = aList.get(j);

                if (playerI.compareWithOtherPlayer(playerJ) == 1){
                    Collections.swap(aList, i, j);
                }
            }
        }
        return aList.get(0);
    }


    public static Map<Integer, Integer> getMultipleMap() {
        return multipleMap;
    }

    public static void setMultipleMap(Map<Integer, Integer> multipleMap) {
        CardUtils.multipleMap = multipleMap;
    }
}
