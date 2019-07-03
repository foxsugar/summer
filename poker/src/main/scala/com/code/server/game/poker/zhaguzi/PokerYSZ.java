package com.code.server.game.poker.zhaguzi;

import java.util.ArrayList;

/**
 * Created by dajuejinxian on 2018/5/19.
 */
public class PokerYSZ {

    //比较点数
    public static int compareResult(int a, int b){
        if (a > b){
            return 0;
        }else if(a == b){
            return 1;
        }else {
            return 2;
        }
    }

    //比较金花
    public static int JinHua(Player p1, Player p2){

        ArrayList<PokerItem> list1 = p1.getPokers();
        ArrayList<PokerItem> list2 = p2.getPokers();

        for(int i = 0; i < list1.size(); i++){
            PokerItem item1 = list1.get(i);
            PokerItem item2 = list2.get(i);
            int chushu1 = (item1.getIndex() - 2) / 4;
            int chushu2 = (item2.getIndex() - 2) / 4;
            if(chushu1 < chushu2){
                return 0;
            }else if(chushu1 > chushu2){
                return 2;
            }
        }
        return 1;
    }

    //单子 和金花算法一样
    public static int DanZi(Player p1, Player p2){
        return  JinHua(p1, p2);
    }

    //对子
    public static int DuiZi(Player p1, Player p2){

        ArrayList<PokerItem> list1 = p1.getPokers();
        ArrayList<PokerItem> list2 = p2.getPokers();

        //因为是对子 所以中间的牌必定是对子中的一个
        //先比较对
        PokerItem item1 = list1.get(1);
        PokerItem item2 = list2.get(1);

        int dui1 = (item1.index - 2) / 4;
        int dui2 = (item2.index - 2) / 4;
        if(dui1 < dui2){
            return 0;
        }else if(dui1 > dui2){
            return 2;
        }else{
            //对子相等 比较单
            PokerItem signle1 = list1.get(0).value.equals(item1.value) ? list1.get(2) : list1.get(0);
            PokerItem signle2 = list2.get(0).value.equals(item2.value) ? list2.get(2) : list2.get(0);

            int chushu1 = (signle1.index - 2) / 4;
            int chushu2 = (signle2.index - 2) / 4;

            if(chushu1 < chushu2){
                return 0;
            }else if(chushu1 == chushu2){
                return 1;
            }else{
                return 2;
            }
        }
    }




    //比较两个豹子
    public static int baoZiCompare(Player p1, Player p2){

        PokerItem aItem = p1.getPokers().get(0);
        PokerItem bItem = p2.getPokers().get(0);

        int a = (aItem.getIndex() - 2) / 4;
        int b = (bItem.getIndex() - 2) / 4;

        return compareResult(a, b);
    }

    //比较顺金
    public static int ShunJin(Player p1, Player p2){
        return shunZi(p1, p2);
    }

    //比较顺金
    public static int ShunJinCompare(Player p1, Player p2){
        return shunZiCompare(p1, p2);
    }

    public static int shunZi(Player p1, Player p2){

        ArrayList<PokerItem> list1 = p1.getPokers();
        ArrayList<PokerItem> list2 = p2.getPokers();
        PokerItem item1 = list1.get(0);
        PokerItem item2 = list2.get(0);
        int chushu1 = (item1.getIndex() - 2) / 4;
        int chushu2 = (item2.getIndex() - 2) / 4;

        if(chushu1 == chushu2){

            //再比较第二张牌
            PokerItem itemA = list1.get(1);
            PokerItem itemB = list2.get(1);

            //如果第一张是A
            if (chushu1 == 0){

                if ((itemA.getIndex() - 2) / 4 == (itemB.getIndex() - 2) / 4){
                    //第二张相等
                    return 1;
                }else if((itemA.getIndex() - 2) / 4 > (itemB.getIndex() - 2) / 4){
                    //第二张
                    if ((itemB.getIndex() - 2) / 4 == 1){
                        return 2;
                    }
                    return 0;

                }else {

                    //K
                    if ((itemA.getIndex() - 2) / 4 == 1){
                        return 0;
                    }
                    return 2;
                }
            }

            return 1;


        }else if(chushu1 > chushu2){
            return 2;
        }else{
            return 0;
        }
    }

    //比较顺子
    public static int shunZiCompare(Player p1, Player p2){

        ArrayList<PokerItem> list1 = p1.getPokers();
        ArrayList<PokerItem> list2 = p2.getPokers();

        //第一张牌
        PokerItem item1 = list1.get(0);
        PokerItem item2 = list2.get(0);
        int chushu1 = (item1.getIndex() - 2) / 4;
        int chushu2 = (item2.getIndex() - 2) / 4;

        if(chushu1 == chushu2){

            PokerItem itemA = list1.get(1);
            PokerItem itemB = list2.get(1);

            //如果第一张是A
            if (chushu1 == 0){

                if ((itemA.getIndex() - 2) / 4 == (itemB.getIndex() - 2) / 4){
                    return 1;
                }else if((itemA.getIndex() - 2) / 4 > (itemB.getIndex() - 2) / 4){
                    return 2;
                }else {
                    return 0;
                }
            }

            return 1;


        }else if(chushu1 > chushu2){

            //
            PokerItem itemA = list2.get(1);
            PokerItem itemB = list2.get(2);

            //如果是23
            if ((chushu2 == 0) && (itemA.getIndex() / 4 == 11) && (itemB.getIndex() / 4 == 12)){
                return 0;
            }

            return 2;
        }else{

            //如果是23
            PokerItem itemA = list1.get(1);
            PokerItem itemB = list1.get(2);
            if (chushu1 == 0 && (itemA.getIndex() / 4 == 11) && (itemB.getIndex() / 4 == 12)){
                return 2;
            }
            return 0;
        }
    }


}


