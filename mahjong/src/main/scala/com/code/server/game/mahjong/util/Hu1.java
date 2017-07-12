package com.code.server.game.mahjong.util;


import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by SunXianping on 2016/11/28 0028.
 */
public class Hu1 {

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
    private static final int TYPE_WAN = 0;//万 1~9
    private static final int TYPE_BING = 1;//饼 11~19
    private static final int TYPE_TIAO = 2;//条 21~29
    private static final int TYPE_ZI = 3;//字 31~37 东南西北中发白

    private static final int CARD_GROUP_TYPE_JIANG = 0;//将
    private static final int CARD_GROUP_TYPE_SHUN = 1;//顺
    private static final int CARD_GROUP_TYPE_KE = 2;//刻
    private static final int CARD_GROUP_TYPE_GANG = 3;//杠
    private static final int CARD_GROUP_TYPE_FENG_SHUN = 4;//风顺
    private static final int CARD_GROUP_TYPE_ZFB = 5;//中发白


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
        if (o1.type < o2.type) {
            return -1;
        } else if (o1.type == o2.type) {
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


    static void test1(int[] cards, List all, List<CardGroup> list, boolean isHasFengShun) {
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
                List<CardGroup> newList = new ArrayList(list);
                newList.add(new CardGroup(CARD_GROUP_TYPE_JIANG, i));
                if (isEmpty(newCards)) {
                    add2List(all, newList);
                } else {
                    test1(newCards, all, newList, isHasFengShun);

                }
            }

            //碰
            if (cards[i] >= 3) {
                int[] newCards = Arrays.copyOf(cards, cards.length);
                newCards[i] -= 3;
                List<CardGroup> newList = new ArrayList(list);
                newList.add(new CardGroup(CARD_GROUP_TYPE_KE, i));
                if (isEmpty(newCards)) {
                    add2List(all, newList);
                } else {
                    test1(newCards, all, newList, isHasFengShun);
                }
            }
            //顺
            if (isShun(cards, i)) {
                int[] newCards = Arrays.copyOf(cards, cards.length);
                newCards[i] -= 1;
                newCards[i + 1] -= 1;
                newCards[i + 2] -= 1;
                List<CardGroup> newList = new ArrayList(list);
                newList.add(new CardGroup(CARD_GROUP_TYPE_SHUN, i));
                if (isEmpty(newCards)) {
                    add2List(all, newList);
                } else {
                    test1(newCards, all, newList, isHasFengShun);
                }
            }

            if (isHasFengShun) {
                //风组成的顺
                if (isHasFengShun(cards, i, feng_shun_array[0])) {
                    int[] newCards = Arrays.copyOf(cards, cards.length);
                    removeFengShun(newCards, feng_shun_array[0]);
                    List<CardGroup> newList = new ArrayList(list);
                    newList.add(new CardGroup(CARD_GROUP_TYPE_FENG_SHUN, 0));
                    if (isEmpty(newCards)) {
                        add2List(all, newList);
                    } else {
                        test1(newCards, all, newList, isHasFengShun);
                    }
                }
                if (isHasFengShun(cards, i, feng_shun_array[1])) {
                    int[] newCards = Arrays.copyOf(cards, cards.length);
                    removeFengShun(newCards, feng_shun_array[1]);
                    List<CardGroup> newList = new ArrayList(list);
                    newList.add(new CardGroup(CARD_GROUP_TYPE_FENG_SHUN, 1));
                    if (isEmpty(newCards)) {
                        add2List(all, newList);
                    } else {
                        test1(newCards, all, newList, isHasFengShun);
                    }
                }
                if (isHasFengShun(cards, i, feng_shun_array[2])) {
                    int[] newCards = Arrays.copyOf(cards, cards.length);
                    removeFengShun(newCards, feng_shun_array[2]);
                    List<CardGroup> newList = new ArrayList(list);
                    newList.add(new CardGroup(CARD_GROUP_TYPE_FENG_SHUN, 2));
                    if (isEmpty(newCards)) {
                        add2List(all, newList);
                    } else {
                        test1(newCards, all, newList, isHasFengShun);
                    }
                }
                if (isHasFengShun(cards, i, feng_shun_array[3])) {
                    int[] newCards = Arrays.copyOf(cards, cards.length);
                    removeFengShun(newCards, feng_shun_array[3]);
                    List<CardGroup> newList = new ArrayList(list);
                    newList.add(new CardGroup(CARD_GROUP_TYPE_FENG_SHUN, 3));
                    if (isEmpty(newCards)) {
                        add2List(all, newList);
                    } else {
                        test1(newCards, all, newList, isHasFengShun);
                    }
                }
                //是中发白
                if (isHasZFB(cards, i)) {
                    int[] newCards = Arrays.copyOf(cards, cards.length);
                    removeZFB(newCards);
                    List<CardGroup> newList = new ArrayList(list);
                    newList.add(new CardGroup(CARD_GROUP_TYPE_ZFB, 31));
                    if (isEmpty(newCards)) {
                        add2List(all, newList);
                    } else {
                        test1(newCards, all, newList, isHasFengShun);
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
            switch (cardGroup.type) {
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
            }
        }
        return huCardType;
    }


    private static boolean isHasZFB(int[] cards, int index) {
        if (index != 31) {
            return false;
        }
        return cards[index] >= 1 && cards[index + 1] >= 1 && cards[index + 2] >= 1;
    }

    private static boolean isHasJiang(List<CardGroup> list) {
        for (CardGroup cardGroup : list) {
            if (cardGroup.type == CARD_GROUP_TYPE_JIANG) {
                return true;
            }
        }
        return false;
    }

    private static void add2List(List<List<CardGroup>> list, List<CardGroup> cardGroups) {
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

    private static boolean isEqual(List<CardGroup> list1, List<CardGroup> list2) {
        if (list1.size() != list2.size()) {
            return false;
        }
        for (int i = 0; i < list1.size(); i++) {
            if (list1.get(i).type != list2.get(i).type || list1.get(i).card != list2.get(i).card) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        int[] a = new int[]{
//                3, 2, 0, 0, 0, 0, 0, 0, 0,
                2, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0,
                3, 3, 3, 3,
                0, 0, 0
        };
        List list = new ArrayList();
        List<CardGroup> groups = new ArrayList<>();
        test1(a, list, groups, true);
        System.out.println(list);
    }

    private static boolean isShun(int[] cards, int index) {
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
        switch (group.type) {
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

    /**
     * 胡牌分组
     */
    public static class CardGroup {
        public CardGroup(int type, int card) {
            this.card = card;
            this.type = type;
        }

        int type;
        int card;

        @Override
        public String toString() {
            return
//                    "type=" + type +
                    type + "," + card
                    ;
        }
    }


//    private void findAllHu(List<Integer> list,List<Integer> pai) {
//
//        for (int i=0;i<list.size();i++) {
//            List<Integer> remianList = new ArrayList<>(list);
//            remianList.remove(i);
//            findAllHu(remianList,count);
//        }
//
//    }

//    public static void main(String[] args) {
//        Hu hu = new Hu();
//        Integer[] l1 = new Integer[]{1, 2, 3, 1, 2, 3, 1, 2, 3, 1, 2, 3, 4, 4};
//        Integer[] l2 = new Integer[]{1, 2, 4, 11, 12, 14, 21, 22, 24, 5, 5, 4, 4, 4};
//        List<Integer> list = new ArrayList<>();
//        Collections.addAll(list, l1);
//        //排序
//        Collections.sort(list);
//        System.out.println("sort = " + list.toString());
////        hu.findJiang(list);
////        hu.findShun(list);
////        hu.findKe(list);
//        System.out.println(hu.isHu(list));
//
//        long start = System.currentTimeMillis();
////        for (int i = 0; i < 100000; i++) {
//
//        hu.isHu(list);
////        }
//        long end = System.currentTimeMillis();
//        System.out.println("time = " + (end - start));
//
//
//        List<Integer> cardlist = Arrays.asList(ALL_CARD);
//        System.out.println(cardlist.size());
//
//
//    }

}
