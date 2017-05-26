
package com.code.server.game.mahjong.util;

import java.util.*;

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
    private static final int TYPE_WAN = 0;//万 1~9
    private static final int TYPE_BING = 1;//饼 11~19
    private static final int TYPE_TIAO = 2;//条 21~29
    private static final int TYPE_ZI = 3;//字 31~37 东南西北中发白

    private static final int CARD_GROUP_TYPE_JIANG = 0;//将
    private static final int CARD_GROUP_TYPE_SHUN = 1;//顺
    private static final int CARD_GROUP_TYPE_KE = 2;//刻
    private static final int CARD_GROUP_TYPE_GANG = 3;//杠


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
                removeCard(list, group.firstCard);
                removeCard(list, group.firstCard);
                break;
            case CARD_GROUP_TYPE_SHUN:
                removeCard(list, group.firstCard);
                removeCard(list, group.firstCard + 1);
                removeCard(list, group.firstCard + 2);

                break;
            case CARD_GROUP_TYPE_KE:
                removeCard(list, group.firstCard);
                removeCard(list, group.firstCard);
                removeCard(list, group.firstCard);
                break;
            case CARD_GROUP_TYPE_GANG:
                removeCard(list, group.firstCard);
                removeCard(list, group.firstCard);
                removeCard(list, group.firstCard);
                removeCard(list, group.firstCard);
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
        public CardGroup(int type, int firstCard) {
            this.firstCard = firstCard;
            this.type = type;
        }

        int type;
        Integer firstCard;

        @Override
        public String toString() {
            return "" + firstCard;
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

    public static void main(String[] args) {
        Hu hu = new Hu();
        Integer[] l1 = new Integer[]{1, 2, 3, 1, 2, 3, 1, 2, 3,1,2,3, 4, 4};
        Integer[] l2 = new Integer[]{1, 2, 4, 11, 12, 14, 21, 22, 24, 5, 5, 4, 4, 4};
        List<Integer> list = new ArrayList<>();
        Collections.addAll(list, l1);
        //排序
        Collections.sort(list);
        System.out.println("sort = " + list.toString());
//        hu.findJiang(list);
//        hu.findShun(list);
//        hu.findKe(list);
        System.out.println(hu.isHu(list));

        long start = System.currentTimeMillis();
//        for (int i = 0; i < 100000; i++) {

            hu.isHu(list);
//        }
        long end = System.currentTimeMillis();
        System.out.println("time = " + (end - start));


        List<Integer> cardlist = Arrays.asList(ALL_CARD);
        System.out.println(cardlist.size());




    }

}
