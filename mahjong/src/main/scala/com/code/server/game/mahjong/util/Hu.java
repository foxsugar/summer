package com.code.server.game.mahjong.util;


import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by SunXianping on 2016/11/28 0028.
 */
public class Hu {

    private static final Integer[] ALL_CARD = new Integer[]{
            //万
            1, 2, 3, 4, 5, 6, 7, 8, 9,
            1, 2, 3, 4, 5, 6, 7, 8, 9,
            1, 2, 3, 4, 5, 6, 7, 8, 9,
            1, 2, 3, 4, 5, 6, 7, 8, 9,
            //饼
            11, 12, 13, 14, 15, 16, 17, 18, 19,
            11, 12, 13, 14, 15, 16, 17, 18, 19,
            11, 12, 13, 14, 15, 16, 17, 18, 19,
            11, 12, 13, 14, 15, 16, 17, 18, 19,
            //条
            21, 22, 23, 24, 25, 26, 27, 28, 29,
            21, 22, 23, 24, 25, 26, 27, 28, 29,
            21, 22, 23, 24, 25, 26, 27, 28, 29,
            21, 22, 23, 24, 25, 26, 27, 28, 29,
            //字
            31, 32, 33, 34, 35, 36, 37,
            31, 32, 33, 34, 35, 36, 37,
            31, 32, 33, 34, 35, 36, 37,
            31, 32, 33, 34, 35, 36, 37
    };
    public static final int TYPE_WAN = 0;//万 1~9
    public static final int TYPE_BING = 1;//饼 11~19
    public static final int TYPE_TIAO = 2;//条 21~29
    public static final int TYPE_ZI = 3;//字 31~37 东南西北中发白

    public static final int CARD_GROUP_TYPE_JIANG = 0;//将
    public static final int CARD_GROUP_TYPE_SHUN = 1;//顺
    public static final int CARD_GROUP_TYPE_KE = 2;//刻
    public static final int CARD_GROUP_TYPE_GANG = 3;//杠
    public static final int CARD_GROUP_TYPE_FENG_SHUN = 4;//风顺
    public static final int CARD_GROUP_TYPE_ZFB = 5;//中发白
    public static final int CARD_GROUP_TYPE_TWO_HUN = 6;//两个混
    public static final int CARD_GROUP_TYPE_THREE_HUN = 7;//三个混
    public static final int CARD_GROUP_TYPE_TWO_HUN_JIANG = 8;//两个 将
    public static final int CARD_GROUP_TYPE_ONE_HUN_JIANG = 9;//带一个混的将
    public static final int CARD_GROUP_TYPE_SHUN_ONE_HUN = 10;//带一个混的顺
    public static final int CARD_GROUP_TYPE_YAO_JIU_SHUN = 11;//幺九顺


    /**
     * 找将
     *
     * @param cards
     * @return
     */
    private List<CardGroup> findJiang(List<Integer> cards) {
        List<CardGroup> result = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : getAllCardNum(cards).entrySet()) {
            if (entry.getValue() >= 2) {
                result.add(new CardGroup(CARD_GROUP_TYPE_JIANG, entry.getKey()));
            }
        }
//        System.out.println("将 = " + result.toString());
        return result;

    }


    /**
     * 找到顺的所有可能
     *
     * @param cards
     */
    private List<CardGroup> findShun(List<Integer> cards) {
        List<CardGroup> result = new ArrayList<>();
        //牌的数量
        int size = cards.size();
        //小于3 不能凑成顺子
        if (size < 3) {
            return result;
        }

        Set<Integer> temp = new HashSet<>();
        for (int i = 0; i < size; i++) {
            int card = cards.get(i);//牌
            if (i <= size - 3 && getCardType(card) != TYPE_ZI) {//有空间排顺并且不是字牌
                if (cards.contains(card + 1) && cards.contains(card + 2)) {
//                    System.out.println("顺 = " + card + "  " + (card + 1) + "  " + (card + 2));
                    temp.add(card);
                }
            }
        }
        for (int c : temp) {
            result.add(new CardGroup(CARD_GROUP_TYPE_SHUN, c));
        }
        //Todo 去重
        return result;
    }

    /**
     * 找刻
     *
     * @param cards
     * @return
     */
    private List<CardGroup> findKe(List<Integer> cards) {
        List<CardGroup> result = new ArrayList<>();
        //牌的数量
        int size = cards.size();
        //小于3 不能凑成刻
        if (size >= 3) {
            for (Map.Entry<Integer, Integer> entry : getAllCardNum(cards).entrySet()) {
                if (entry.getValue() >= 3) {
                    result.add(new CardGroup(CARD_GROUP_TYPE_KE, entry.getKey()));
                }
            }
        }

//        System.out.println("刻 = " + result.toString());
        return result;

    }


    /**
     * 找到各种牌的数量
     *
     * @param cards
     * @returnget
     */
    private Map<Integer, Integer> getAllCardNum(List<Integer> cards) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int card : cards) {
            if (map.containsKey(card)) {
                map.put(card, map.get(card) + 1);
            } else {
                map.put(card, 1);
            }
        }
        return map;

    }

    private static final Comparator<CardGroup> comparator = (o1, o2) -> {
        if (o1.huType < o2.huType) {
            return -1;
        } else if (o1.huType == o2.huType) {
            if (o1.card <= o2.card) {
                return -1;
            } else {
                return 1;
            }
        } else {
            return 1;
        }
    };
    private static int count = 0;


    public static void main(String[] args) {
        int[] a = new int[]{
//                3, 2, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 2, 3, 3, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0
        };
        List<List<CardGroup>> list = new ArrayList<>();
        List<List<CardGroup>> allList = new ArrayList<>();
        List<CardGroup> groups = new ArrayList<>();
        testHun(true, a, list, allList, groups);

        System.out.println("配好的 : " + list);
        allList = removeRepeat(allList);
//        for(int ll : a){
//            System.out.println(ll);
//        }
        System.out.println("部分: " + allList);
    }

    static void findGroup(int[] cards, List all) {

    }

    private static void getRemainCard(int[] cards, List<List<CardGroup>> list) {

    }

    static void testHun(boolean isOut, int[] cards, List<List<CardGroup>> complete, List<List<CardGroup>> noComplete, List<CardGroup> temp) {

        for (int i = 0; i < 34; i++) {
            if (cards[i] == 0) {
                continue;
            }

            //将
            if (cards[i] >= 2 && !isHasJiang(temp)) {
                int[] newCards = Arrays.copyOf(cards, cards.length);
                newCards[i] -= 2;
                List<CardGroup> newList = new ArrayList(temp);
                newList.add(new CardGroup(CARD_GROUP_TYPE_JIANG, i));
                if (isEmpty(newCards)) {
                    add2List(complete, newList);
                } else {
                    testHun(false, newCards, complete, noComplete, newList);

                }
            }

            //碰
            if (cards[i] >= 3) {
                int[] newCards = Arrays.copyOf(cards, cards.length);
                newCards[i] -= 3;
                List<CardGroup> newList = new ArrayList(temp);
                newList.add(new CardGroup(CARD_GROUP_TYPE_KE, i));
                if (isEmpty(newCards)) {
                    add2List(complete, newList);
                } else {
                    testHun(false, newCards, complete, noComplete, newList);
                }
            }
            //顺
            if (isShun(cards, i)) {
                int[] newCards = Arrays.copyOf(cards, cards.length);
                newCards[i] -= 1;
                newCards[i + 1] -= 1;
                newCards[i + 2] -= 1;
                List<CardGroup> newList = new ArrayList(temp);
                newList.add(new CardGroup(CARD_GROUP_TYPE_SHUN, i));
                if (isEmpty(newCards)) {
                    add2List(complete, newList);
                } else {
                    testHun(false, newCards, complete, noComplete, newList);
                }
            }

            add2List(noComplete, temp);


        }
    }


    public static List<List<CardGroup>> removeRepeat(List<List<CardGroup>> lists) {
        //按多少排序
        lists.sort((list1, list2) -> {
            if (list1.size() > list2.size()) {
                return -1;
            }else if(list1.size() == list2.size()){
                return 0;
            } else {
                return 1;
            }
        });
        List<List<CardGroup>> newList = new ArrayList<>();
        for (List<CardGroup> desList : lists) {
            isAllInclude(newList, desList);
        }

        lists = newList;

//        System.out.println("newlist" + newList);

        return newList;
    }


    private static boolean isAllInclude(List<List<CardGroup>> srcList, List<CardGroup> des) {
        for (CardGroup cardGroup : des) {
            if (srcList.size() == 0) {
                srcList.add(des);
            } else {
                boolean isIn = false;
                for (List<CardGroup> src : srcList) {
                    boolean isInList = isInList(src, cardGroup);
                    if (isInList) {
//                        System.out.println("buzai");
                        isIn = true;

                    }
                }

                if (!isIn) {
//                    System.out.println("不在要加入    srclist = "+srcList +"  des = "+des);
                    srcList.add(des);
                } else {
//                    System.out.println("已存在");
                }
            }
        }
        return true;
    }


    private static boolean isInList(List<CardGroup> list, CardGroup cardGroup) {
//        System.out.println("list = "+ list+ " cardGroup : " +cardGroup);
        for (CardGroup lc : list) {
            if (lc.huType == cardGroup.huType && lc.card == cardGroup.card) {
                return true;
            }
        }
        return false;
    }


    static void isHu(int[] cards, List all, List<CardGroup> list, boolean isHasFengShun,boolean isHasZiShun, boolean isHasYaojiuShun) {
//        count++;
//           System.out.println("====: " + count);

        for (int i = 0; i < 34; i++) {
            if (cards[i] == 0) {
                continue;
            }

            //将
            if (cards[i] >= 2 && !isHasJiang(list)) {
                int[] newCards = Arrays.copyOf(cards, cards.length);
                newCards[i] -= 2;
                List<CardGroup> newList = new ArrayList<>(list);
                newList.add(new CardGroup(CARD_GROUP_TYPE_JIANG, i));
                if (isEmpty(newCards)) {
                    add2List(all, newList);
                } else {
                    isHu(newCards, all, newList, isHasFengShun,isHasZiShun,isHasYaojiuShun);

                }
            }

            //碰
            if (cards[i] >= 3) {
                int[] newCards = Arrays.copyOf(cards, cards.length);
                newCards[i] -= 3;
                List<CardGroup> newList = new ArrayList<>(list);
                newList.add(new CardGroup(CARD_GROUP_TYPE_KE, i));
                if (isEmpty(newCards)) {
                    add2List(all, newList);
                } else {
                    isHu(newCards, all, newList, isHasFengShun,isHasZiShun,isHasYaojiuShun);
                }
            }
            //顺
            if (isShun(cards, i)) {
                int[] newCards = Arrays.copyOf(cards, cards.length);
                newCards[i] -= 1;
                newCards[i + 1] -= 1;
                newCards[i + 2] -= 1;
                List<CardGroup> newList = new ArrayList<>(list);
                newList.add(new CardGroup(CARD_GROUP_TYPE_SHUN, i));
                if (isEmpty(newCards)) {
                    add2List(all, newList);
                } else {
                    isHu(newCards, all, newList, isHasFengShun,isHasZiShun,isHasYaojiuShun);
                }
            }

            if (isHasFengShun) {
                //风组成的顺
                if (isHasFengShun(cards, i, feng_shun_array[0])) {
                    int[] newCards = Arrays.copyOf(cards, cards.length);
                    removeFengShun(newCards, feng_shun_array[0]);
                    List<CardGroup> newList = new ArrayList<>(list);
                    newList.add(new CardGroup(CARD_GROUP_TYPE_FENG_SHUN, 0));
                    if (isEmpty(newCards)) {
                        add2List(all, newList);
                    } else {
                        isHu(newCards, all, newList, isHasFengShun,isHasZiShun,isHasYaojiuShun);
                    }
                }
                if (isHasFengShun(cards, i, feng_shun_array[1])) {
                    int[] newCards = Arrays.copyOf(cards, cards.length);
                    removeFengShun(newCards, feng_shun_array[1]);
                    List<CardGroup> newList = new ArrayList<>(list);
                    newList.add(new CardGroup(CARD_GROUP_TYPE_FENG_SHUN, 1));
                    if (isEmpty(newCards)) {
                        add2List(all, newList);
                    } else {
                        isHu(newCards, all, newList, isHasFengShun,isHasZiShun,isHasYaojiuShun);
                    }
                }
                if (isHasFengShun(cards, i, feng_shun_array[2])) {
                    int[] newCards = Arrays.copyOf(cards, cards.length);
                    removeFengShun(newCards, feng_shun_array[2]);
                    List<CardGroup> newList = new ArrayList<>(list);
                    newList.add(new CardGroup(CARD_GROUP_TYPE_FENG_SHUN, 2));
                    if (isEmpty(newCards)) {
                        add2List(all, newList);
                    } else {
                        isHu(newCards, all, newList, isHasFengShun,isHasZiShun,isHasYaojiuShun);
                    }
                }
                if (isHasFengShun(cards, i, feng_shun_array[3])) {
                    int[] newCards = Arrays.copyOf(cards, cards.length);
                    removeFengShun(newCards, feng_shun_array[3]);
                    List<CardGroup> newList = new ArrayList<>(list);
                    newList.add(new CardGroup(CARD_GROUP_TYPE_FENG_SHUN, 3));
                    if (isEmpty(newCards)) {
                        add2List(all, newList);
                    } else {
                        isHu(newCards, all, newList, isHasFengShun,isHasZiShun,isHasYaojiuShun);
                    }
                }
            }
            //是中发白
            if (isHasZiShun && isHasZFB(cards, i)) {
                int[] newCards = Arrays.copyOf(cards, cards.length);
                removeZFB(newCards);
                List<CardGroup> newList = new ArrayList<>(list);
                newList.add(new CardGroup(CARD_GROUP_TYPE_ZFB, 31));
                if (isEmpty(newCards)) {
                    add2List(all, newList);
                } else {
                    isHu(newCards, all, newList, isHasFengShun,isHasZiShun,isHasYaojiuShun);
                }
            }

            if (isHasYaojiuShun) {
                for(int[] yaojiu : HuUtil.yao_jiu_shun){
                    if (isHasYJShun(cards, i, yaojiu)) {
                        int[] newCards = Arrays.copyOf(cards, cards.length);
                        removeFengShun(newCards, yaojiu);
                        List<CardGroup> newList = new ArrayList<>(list);
                        List<Integer> temp = new ArrayList<>();
                        temp.add(yaojiu[0]);
                        temp.add(yaojiu[1]);
                        temp.add(yaojiu[2]);
                        newList.add(new CardGroup(CARD_GROUP_TYPE_YAO_JIU_SHUN, temp));
                        if (isEmpty(newCards)) {
                            add2List(all, newList);
                        } else {
                            isHu(newCards, all, newList, isHasFengShun,isHasZiShun,isHasYaojiuShun);
                        }
                    }
                }
            }


        }
    }

    public static List<HuCardType> convert(List<List<CardGroup>> all) {
        List<HuCardType> result = new ArrayList<>();
        for (List<CardGroup> cardGroups : all) {
            result.add(convert2HuCardType(cardGroups));
        }
        return result;
    }

    public static HuCardType convert2HuCardType(List<CardGroup> list) {
        HuCardType huCardType = new HuCardType();
        for (CardGroup cardGroup : list) {
            if (cardGroup.hunNum != 0) {
                if(cardGroup.huType == CARD_GROUP_TYPE_KE
                        || cardGroup.huType == CARD_GROUP_TYPE_ONE_HUN_JIANG
                        || cardGroup.huType == CARD_GROUP_TYPE_SHUN_ONE_HUN){
                    huCardType.hunReplaceCard.add(cardGroup.getHunReplaceCard());
                }
            }
            switch (cardGroup.huType) {
                case CARD_GROUP_TYPE_JIANG:
                    huCardType.jiang = cardGroup.card;
                    break;
                case CARD_GROUP_TYPE_SHUN:
                    huCardType.shun.add(cardGroup.card);
                    break;
                case CARD_GROUP_TYPE_KE:
                    huCardType.ke.add(cardGroup.card);
                    break;
                case CARD_GROUP_TYPE_FENG_SHUN:
                    huCardType.feng_shun.add(Arrays.stream(feng_shun_array[cardGroup.card]).boxed().collect(Collectors.toList()));
                    break;
                case CARD_GROUP_TYPE_ZFB:
                    huCardType.zi_shun++;
                    break;
                case CARD_GROUP_TYPE_TWO_HUN:
                    huCardType.hun2.add(cardGroup.card);
                    break;
                case CARD_GROUP_TYPE_THREE_HUN:
                    huCardType.hun3.add(cardGroup.card);
                    break;
                case CARD_GROUP_TYPE_TWO_HUN_JIANG:
                    huCardType.hunJiang = true;
                    break;
                case CARD_GROUP_TYPE_ONE_HUN_JIANG:
                    huCardType.jiangOneHun = cardGroup.card;
                    break;
                case CARD_GROUP_TYPE_SHUN_ONE_HUN:
                    huCardType.shunHaveHuns.add(cardGroup.shunHaveHun);
                    break;
                case CARD_GROUP_TYPE_YAO_JIU_SHUN:
                    huCardType.yao_jiu_shun.add(cardGroup.yaojiuCards);
                    break;



            }
        }
        return huCardType;
    }


    public static boolean isHasZFB(int[] cards, int index) {
        if (index != 31) {
            return false;
        }
        return cards[index] >= 1 && cards[index + 1] >= 1 && cards[index + 2] >= 1;
    }

    /**
     * 是否有幺九顺
     * @param cards
     * @param index
     * @param yaojiu
     * @return
     */
    public static boolean isHasYJShun(int[] cards, int index,int[] yaojiu) {
        //以index开头的才可以
        if (index != yaojiu[0]) {
            return false;
        }

        for (int fengIndex : yaojiu) {
            if (cards[fengIndex] == 0) {
                return false;
            }
        }
        return true;
    }

    public static boolean isHasJiang(List<CardGroup> list) {
        for (CardGroup cardGroup : list) {
            if (cardGroup.huType == CARD_GROUP_TYPE_JIANG || cardGroup.huType == CARD_GROUP_TYPE_TWO_HUN_JIANG || cardGroup.huType == CARD_GROUP_TYPE_ONE_HUN_JIANG) {
                return true;
            }
        }
        return false;
    }

    public static void add2List(List<List<CardGroup>> list, List<CardGroup> cardGroups) {
        cardGroups.sort(comparator);
        boolean equal = false;
        for (List<CardGroup> l : list) {
            if (isEqual(l, cardGroups)) {
                equal = true;
            }
        }
        if (!equal) {
            list.add(cardGroups);
        }
    }

    public static boolean isEqual(List<CardGroup> list1, List<CardGroup> list2) {
        if (list1.size() != list2.size()) {
            return false;
        }
        for (int i = 0; i < list1.size(); i++) {
            if (list1.get(i).huType != list2.get(i).huType || list1.get(i).card != list2.get(i).card) {
                return false;
            }
        }
        return true;
    }


    public static boolean isShun(int[] cards, int index) {
        if (index >= 25 || index == 7 || index == 8 || index == 16 || index == 17) {
            return false;
        }
        return cards[index] >= 1 && cards[index + 1] >= 1 && cards[index + 2] >= 1;
    }

    //风组成顺的四种排列方式
    static int[][] feng_shun_array = new int[][]{
            {27, 28, 29},
            {27, 28, 30},
            {27, 29, 30},
            {28, 29, 30},
    };

    static int[] zfb = new int[]{31, 32, 33};

    private static boolean isHasFengShun(int[] cards, int index, int[] fengShun) {
        if (index != 27 && index != 28) {
            return false;
        }
        //以index开头的才可以
        if (index != fengShun[0]) {
            return false;
        }
        for (int fengIndex : fengShun) {
            if (cards[fengIndex] == 0) {
                return false;
            }
        }
        return true;

    }

    private static void removeFengShun(int[] cards, int[] fengShun) {
        for (int index : fengShun) {
            cards[index] -= 1;
        }
    }

    private static void removeZFB(int[] cards) {
        for (int index : zfb) {
            cards[index] -= 1;
        }
    }

    private static boolean isEmpty(int[] cards) {
        for (int i : cards) {
            if (i != 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获得麻将类型
     *
     * @param card
     * @return
     */
    private int getCardType(int card) {
        return card / 10;
    }

    /**
     * 检查是刻还是顺
     *
     * @param cards
     * @param hupaiInfo
     * @return
     */
    private boolean checkIsKeOrShun(List<Integer> cards, HupaiInfo hupaiInfo) {
//        System.out.println("------------------");
        //找到顺的牌
        List<CardGroup> allShun = findShun(cards);
        //找到刻的牌
        List<CardGroup> allKe = findKe(cards);
        List<CardGroup> allGroup = new ArrayList<>();
        allGroup.addAll(allShun);
        allGroup.addAll(allKe);

        if (allGroup.size() == 0) {
            return false;
        }

        for (CardGroup group : allGroup) {
            hupaiInfo.all.add(group);
            //复制一个副本
            List<Integer> remainCard = new ArrayList<>(cards);
            cardRemoveGroup(remainCard, group);
            if (remainCard.size() == 0) {//全部移除 胡牌
                return true;
            } else {
                return checkIsKeOrShun(remainCard, hupaiInfo);
            }

        }
        return false;
    }


    /**
     * 手牌移除麻将组
     *
     * @param list
     * @param group
     */
    private void cardRemoveGroup(List<Integer> list, CardGroup group) {
        switch (group.huType) {
            case CARD_GROUP_TYPE_JIANG:
                removeCard(list, group.card);
                removeCard(list, group.card);
                break;
            case CARD_GROUP_TYPE_SHUN:
                removeCard(list, group.card);
                removeCard(list, group.card + 1);
                removeCard(list, group.card + 2);

                break;
            case CARD_GROUP_TYPE_KE:
                removeCard(list, group.card);
                removeCard(list, group.card);
                removeCard(list, group.card);
                break;
            case CARD_GROUP_TYPE_GANG:
                removeCard(list, group.card);
                removeCard(list, group.card);
                removeCard(list, group.card);
                removeCard(list, group.card);
                break;
            default:
                //todo 错误处理
                break;
        }
    }


    /**
     * 移除麻将  将int转成包装类
     *
     * @param list
     * @param card
     */
    private void removeCard(List<Integer> list, int card) {
        removeCard(list, (Integer) card);
    }

    /**
     * 移除麻将
     *
     * @param list
     * @param card
     */
    private void removeCard(List<Integer> list, Integer card) {
        list.remove(card);
    }


    /**
     * 是否胡牌
     *
     * @param cards
     * @return
     */
    private boolean isHu(List<Integer> cards) {
        boolean result = false;
        if (cards == null) {
            return false;
        }
        List<HupaiInfo> hupaiList = new ArrayList<>();
        for (CardGroup groupJiang : findJiang(cards)) {
            List<Integer> remainCards = new ArrayList<>(cards);
            //移除将牌 两个
            cardRemoveGroup(remainCards, groupJiang);
            HupaiInfo hupaiInfo = new HupaiInfo();

            boolean isHu = checkIsKeOrShun(remainCards, hupaiInfo);
            if (isHu) {
                hupaiInfo.all.add(groupJiang);
//                System.out.println("group size = " + hupaiInfo.all.toString());
                hupaiList.add(hupaiInfo);
                result = true;
            }


        }

        System.out.println("胡牌的种类= " + hupaiList.size());

        return result;
    }

    public static class HupaiInfo {
        List<CardGroup> jiang = new ArrayList<>();
        List<CardGroup> shun = new ArrayList<>();
        List<CardGroup> ke = new ArrayList<>();
        List<CardGroup> gang = new ArrayList<>();
        List<CardGroup> all = new ArrayList<>();
    }

//    /**
//     * 胡牌分组
//     */
//    public static class CardGroup {
//        public CardGroup(int huType, int card) {
//            this.card = card;
//            this.huType = huType;
//        }
//
//        int huType;
//        int card;
//
//        @Override
//        public String toString() {
//            return
////                    "huType=" + huType +
//                    huType + "," + card
//                    ;
//        }
//    }


}
