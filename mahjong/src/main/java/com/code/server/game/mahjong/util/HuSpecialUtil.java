package com.code.server.game.mahjong.util;


import com.code.server.game.mahjong.logic.CardTypeUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HuSpecialUtil {
    static final int MAN = 0;
    static final int MAN1 = 0;
    static final int MAN2 = 1;
    static final int MAN3 = 2;
    static final int MAN4 = 3;
    static final int MAN5 = 4;
    static final int MAN6 = 5;
    static final int MAN7 = 6;
    static final int MAN8 = 7;
    static final int MAN9 = 8;
    static final int PIN = 9;
    static final int PIN1 = 9;
    static final int PIN2 = 10;
    static final int PIN3 = 11;
    static final int PIN4 = 12;
    static final int PIN5 = 13;
    static final int PIN6 = 14;
    static final int PIN7 = 15;
    static final int PIN8 = 16;
    static final int PIN9 = 17;
    static final int SOU = 18;
    static final int SOU1 = 18;
    static final int SOU2 = 19;
    static final int SOU3 = 20;
    static final int SOU4 = 21;
    static final int SOU5 = 22;
    static final int SOU6 = 23;
    static final int SOU7 = 24;
    static final int SOU8 = 25;
    static final int SOU9 = 26;
    static final int TON = 27;
    static final int NAN = 28;
    static final int SHA = 29;
    static final int PEI = 30;
    static final int HAK = 31;
    static final int HAT = 32;
    static final int CHU = 33;

    //风组成顺的四种排列方式
    static int[][] feng_shun_array = new int[][]{
            {27,28,29},
            {27,28,30},
            {28,29,30},
            {27,29,30}
    };

    static final int[] n_zero;

    static {
        n_zero = new int[34];
        Arrays.fill(n_zero, 0);
    }

    static int[] analyse(int[] hai) {
        int[] n = n_zero.clone();

        for (int i : hai) {
            n[i]++;
        }
        return n;
    }

    private static int[] convert(List<String> list) {
        int size = list.size();
        int[] temp = new int[size];
        for (int i=0;i<size;i++) {
            String card = list.get(i);
            int cardType = CardTypeUtil.cardType.get(card);
            temp[i] = cardType;
        }
        return  temp;
    }
    public static List<HuCardType> isHu(List<String> list) {
        return agari(analyse(convert(list)));
    }



    public static List<Integer> isTing(List<String> list,List<Integer> exclude) {
        List<Integer> ting = new ArrayList<>();
        boolean isCan = false;
        for (int i = 0; i < n_zero.length; i++) {
            //不包含
            if (!exclude.contains(i)) {
                //加入数组
                int[] a = convert(list);
                int[] b = Arrays.copyOf(a, a.length + 1);
                b[a.length] = i;
                if (agari(analyse(b)).size() > 0) {

                    ting.add(i);
                    isCan = true;
//                    return true;
                }
            }
        }
        return ting;


    }

    //胡牌
    static List<HuCardType> agari(int[] n) {
//        List<Integer[][]> ret = new ArrayList<Integer[][]>();
        List<HuCardType> ret = new ArrayList<>();

        for (int i = 0; i < 34; i++) {
            for (int ke_first = 0; ke_first < 2; ke_first++) {
                Integer[] jiang = new Integer[1];
                ArrayList<Integer> ke = new ArrayList<Integer>();
                ArrayList<Integer> shun = new ArrayList<Integer>();
                ArrayList<List<Integer>> feng = new ArrayList<>();
                int zi_num = 0;

                int[] t = n.clone();
                if (t[i] >= 2) {
                    // 循环可以做将的牌
                    t[i] -= 2;
                    jiang[0] = i;

                    if (ke_first == 0) {//先找刻
                        // 寻找刻
                        for (int j = 0; j < 34; j++) {
                            if (t[j] >= 3) {
                                t[j] -= 3;
                                ke.add(j);
                            }
                        }
                        // 顺
                        for (int a = 0; a < 3; a++) {
                            for (int b = 0; b < 7; ) {//8万以后不能成顺
                                if (t[9 * a + b] >= 1 && t[9 * a + b + 1] >= 1 && t[9 * a + b + 2] >= 1) {
                                    t[9 * a + b]--;
                                    t[9 * a + b + 1]--;
                                    t[9 * a + b + 2]--;
                                    shun.add(9 * a + b);
                                } else {
                                    b++;
                                }
                            }
                        }
                        //处理风
                        disposeFENG(t,feng);
                        //处理字
                        zi_num = disposeZFB(t);
                    } else {//先找顺
                        //处理风
                        disposeFENG(t,feng);
                        //处理字
                        zi_num = disposeZFB(t);

                        // 找顺
                        for (int a = 0; a < 3; a++) {
                            for (int b = 0; b < 7; ) {
                                if (t[9 * a + b] >= 1 &&
                                        t[9 * a + b + 1] >= 1 &&
                                        t[9 * a + b + 2] >= 1) {
                                    t[9 * a + b]--;
                                    t[9 * a + b + 1]--;
                                    t[9 * a + b + 2]--;
                                    shun.add(9 * a + b);
                                } else {
                                    b++;
                                }
                            }
                        }
                        // 找刻
                        for (int j = 0; j < 34; j++) {
                            if (t[j] >= 3) {
                                t[j] -= 3;
                                ke.add(j);
                            }
                        }
                    }

                    //
                    if (Arrays.equals(t, n_zero)) {
                        HuCardType huCardType = new HuCardType();
                        huCardType.jiang = jiang[0];
                        huCardType.shun = shun;
                        huCardType.ke = ke;
                        huCardType.feng_shun = feng;
                        huCardType.zi_shun = zi_num;

                        ret.add(huCardType);
//                        System.out.println(huCardType);
                    }
                }
            }
        }
        return ret;
    }



    private static int disposeZFB(int[] t) {
        int min = t[31];
        for (int i = 31; i < 34; i++) {
            if (t[i] < min) {
                min = t[i];
            }
        }
        for (int i = 31; i < 34; i++) {
            t[i] -= min;
        }

        return min;
    }

    private static void disposeFENG(int[] t,List<List<Integer>> list) {


        for (int i = 0; i < feng_shun_array.length;) {
            int one = t[feng_shun_array[i][0]];
            int two = t[feng_shun_array[i][1]];
            int three = t[feng_shun_array[i][2]];

            if (one > 0 && two > 0 && three > 0) {
                //凑成
                List<Integer> inner = new ArrayList<>();
                inner.add(feng_shun_array[i][0]);
                inner.add(feng_shun_array[i][1]);
                inner.add(feng_shun_array[i][2]);
                list.add(inner);
                t[feng_shun_array[i][0]]--;
                t[feng_shun_array[i][1]]--;
                t[feng_shun_array[i][2]]--;
            } else {
                i++;
            }
        }

    }

    public static void main(String[] args) {
        int[] hai = {
                MAN1, MAN2, MAN3,
                MAN1, MAN2, MAN3,
                TON, NAN, SHA,
                HAK, HAT, CHU,
                MAN4, MAN4};

        int[] n = null;
        List<HuCardType> ret = null;

        long time = System.currentTimeMillis();
//        for (int i = 0; i < 100000; i++) {
            n = analyse(hai);
            ret = agari(n);
//        }
//        System.out.println("耗时 = "+(System.currentTimeMillis() - time));

        System.out.println("ret size = "+ret.size());
        for (HuCardType r : ret) {

            System.out.println(r);
            System.out.println();
        }
//
//        String[] t = new String[]{"000","001","002","003","004","005","006","007","008","009","010","011","012"};
//        List<String> list = Arrays.asList(t);
//
//        List<Integer> exclude = new ArrayList<>();
//        exclude.add(1);
//        exclude.add(2);
//        exclude.add(0);
////        exclude.add(3);
//        System.out.println(isTing(list,exclude).size()>0);
    }


//    static final int dong = 27;
//    static final int nan = 28;
//    static final int xi = 29;
//    static final int bei = 30;
//    static final int zhong = 31;
//    static final int fa = 32;
//    static final int bai = 33;
}
