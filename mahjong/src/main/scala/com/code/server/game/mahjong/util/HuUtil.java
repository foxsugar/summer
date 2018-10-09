package com.code.server.game.mahjong.util;


import com.code.server.game.mahjong.logic.CardTypeUtil;
import com.code.server.game.mahjong.logic.PlayerCardsInfoMj;

import java.util.*;

public class HuUtil implements HuType {


    static final int WAN = 0;
    static final int WAN1 = 0;
    static final int WAN2 = 1;
    static final int WAN3 = 2;
    static final int WAN4 = 3;
    static final int WAN5 = 4;
    static final int WAN6 = 5;
    static final int WAN7 = 6;
    static final int WAN8 = 7;
    static final int WAN9 = 8;
    static final int TIAO = 9;
    static final int TIAO1 = 9;
    static final int TIAO2 = 10;
    static final int TIAO3 = 11;
    static final int TIAO4 = 12;
    static final int TIAO5 = 13;
    static final int TIAO6 = 14;
    static final int TIAO7 = 15;
    static final int TIAO8 = 16;
    static final int TIAO9 = 17;
    static final int TONG = 18;
    static final int TONG1 = 18;
    static final int TONG2 = 19;
    static final int TONG3 = 20;
    static final int TONG4 = 21;
    static final int TONG5 = 22;
    static final int TONG6 = 23;
    static final int TONG7 = 24;
    static final int TONG8 = 25;
    static final int TONG9 = 26;
    static final int dong = 27;
    static final int nan = 28;
    static final int xi = 29;
    static final int bei = 30;
    static final int zhong = 31;
    static final int fa = 32;
    static final int bai = 33;

    //风组成顺的四种排列方式
    static Integer[][] feng_shun_array = new Integer[][]{
            {27, 28, 29},
            {27, 28, 30},
            {28, 29, 30},
            {27, 29, 30}
    };

    static Integer[] zi_shun_array = new Integer[]{
            31, 32, 33
    };

    static List<List<Integer>> feng = new ArrayList<>();
    static List<Integer> feng1 = new ArrayList<>();
    static List<Integer> feng2 = new ArrayList<>();
    static List<Integer> feng3 = new ArrayList<>();
    static List<Integer> feng0 = new ArrayList<>();


    static int[] yao = new int[]{0, 8, 9, 17, 18, 26, 27, 28, 29, 30, 31, 32, 33};

    static final int[] n_zero;

    static {
        n_zero = new int[34];
        Arrays.fill(n_zero, 0);

        feng0.addAll(Arrays.asList(feng_shun_array[0]));
        feng1.addAll(Arrays.asList(feng_shun_array[1]));
        feng2.addAll(Arrays.asList(feng_shun_array[2]));
        feng3.addAll(Arrays.asList(feng_shun_array[3]));
        feng.add(feng0);
        feng.add(feng1);
        feng.add(feng2);
        feng.add(feng3);
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
        for (int i = 0; i < size; i++) {
            String card = list.get(i);
            int cardType = CardTypeUtil.cardType.get(card);
            temp[i] = cardType;
        }
        return temp;
    }


    /**
     * 能胡的牌
     *
     * @param
     * @param tingCardType
     * @return
     */
    public static List<HuCardType> isHu(List<String> cards, PlayerCardsInfoMj playerCardsInfo, int tingCardType, HuLimit limit) {
        List<HuCardType> huList = new ArrayList<>();
        List<HuCardType> result = new ArrayList<>();
        //带特殊胡法
        if (playerCardsInfo.isHasSpecialHu()) {
            if (playerCardsInfo.isHasSpecialHu(hu_十三幺) && is13Yao(cards, playerCardsInfo)) {
                huList.add(HuCardType.getSpecialHuInstance(hu_十三幺).setFan(playerCardsInfo.getSpecialHuScore(hu_十三幺)));
            }

            if (playerCardsInfo.isHasSpecialHu(hu_七小对) && isQixiaodui(cards, playerCardsInfo)) {
                huList.add(HuCardType.getSpecialHuInstance(hu_七小对).setFan(playerCardsInfo.getSpecialHuScore(hu_七小对)));
            }

            if (playerCardsInfo.isHasSpecialHu(hu_豪华七小对) && isHaoHuaQixiaodui(cards, playerCardsInfo, 1)) {
                huList.add(HuCardType.getSpecialHuInstance(hu_豪华七小对).setFan(playerCardsInfo.getSpecialHuScore(hu_豪华七小对)));
            }
            if (playerCardsInfo.isHasSpecialHu(hu_双豪七小对) && isHaoHuaQixiaodui(cards, playerCardsInfo, 2)) {
                huList.add(HuCardType.getSpecialHuInstance(hu_双豪七小对).setFan(playerCardsInfo.getSpecialHuScore(hu_双豪七小对)));
            }
            if (playerCardsInfo.isHasSpecialHu(hu_三豪七小对) && isHaoHuaQixiaodui(cards, playerCardsInfo, 3)) {
                huList.add(HuCardType.getSpecialHuInstance(hu_三豪七小对).setFan(playerCardsInfo.getSpecialHuScore(hu_三豪七小对)));
            }

            if (playerCardsInfo.isHasSpecialHu(hu_双豪七小对_山西) && isShuangHaoQixiaodui(cards, playerCardsInfo)) {
                huList.add(HuCardType.getSpecialHuInstance(hu_双豪七小对_山西).setFan(playerCardsInfo.getSpecialHuScore(hu_双豪七小对_山西)));
            }
            if (playerCardsInfo.isHasSpecialHu(hu_清七对) && isQingqidui(cards, playerCardsInfo)) {
                huList.add(HuCardType.getSpecialHuInstance(hu_清七对).setFan(playerCardsInfo.getSpecialHuScore(hu_清七对)));
            }
            if (playerCardsInfo.isHasSpecialHu(HuType.hu_普通七小对) && isQixiaodui_common(cards, playerCardsInfo)) {
                huList.add(HuCardType.getSpecialHuInstance(hu_普通七小对).setFan(playerCardsInfo.getSpecialHuScore(hu_普通七小对)));
            }
            if (playerCardsInfo.isHasSpecialHu(hu_双豪七小对) && isHaoHuaQixiaodui(cards, playerCardsInfo, 2)) {
                huList.add(HuCardType.getSpecialHuInstance(hu_双豪七小对).setFan(playerCardsInfo.getSpecialHuScore(hu_双豪七小对)));
            }
            if (playerCardsInfo.isHasSpecialHu(hu_三豪七小对) && isHaoHuaQixiaodui(cards, playerCardsInfo, 3)) {
                huList.add(HuCardType.getSpecialHuInstance(hu_三豪七小对).setFan(playerCardsInfo.getSpecialHuScore(hu_三豪七小对)));
            }
            if (playerCardsInfo.isHasSpecialHu(hu_十三不靠) && is13BuKao(cards, playerCardsInfo)) {
                huList.add(HuCardType.getSpecialHuInstance(hu_十三不靠).setFan(playerCardsInfo.getSpecialHuScore(hu_十三不靠)));
            }

            if (playerCardsInfo.isHasSpecialHu(hu_清一色七小对) && isQixiaodui(cards, playerCardsInfo) && isYiSe_wtt(cards)) {
                huList.add(HuCardType.getSpecialHuInstance(hu_清一色七小对).setFan(playerCardsInfo.getSpecialHuScore(hu_清一色七小对)));
            }

            if (playerCardsInfo.isHasSpecialHu(hu_清一色豪华七小对) && isHaoHuaQixiaodui(cards, playerCardsInfo,1) && isYiSe_wtt(cards)) {
                huList.add(HuCardType.getSpecialHuInstance(hu_清一色豪华七小对).setFan(playerCardsInfo.getSpecialHuScore(hu_清一色豪华七小对)));
            }
            if (playerCardsInfo.isHasSpecialHu(hu_清一色双豪华七小对) && isShuangHaoQixiaodui(cards, playerCardsInfo) && isYiSe_wtt(cards)) {
                huList.add(HuCardType.getSpecialHuInstance(hu_清一色双豪华七小对).setFan(playerCardsInfo.getSpecialHuScore(hu_清一色双豪华七小对)));
            }
        }


        //普通胡法
        List<HuCardType> huCardTypes = agari(analyse(convert(cards)), playerCardsInfo.isHasFengShun());
        huList.addAll(huCardTypes);

        for (HuCardType huCardType : huList) {
            //设置胡的类型
            HuCardType.setHuCardType(huCardType, playerCardsInfo);

            huCardType.tingCardType = tingCardType;
            //不限制最低番数
            if (limit == null || !limit.isLimitFan) {
                result.add(huCardType);
            } else {
                huCardType.fan = FanUtil.compute(cards, huCardType, tingCardType, playerCardsInfo);
                if (huCardType.fan >= limit.fan) {
                    result.add(huCardType);
                }
            }
        }
        return result;
    }


    public static List<HuCardType> isHu(PlayerCardsInfoMj playerCardsInfo, List<String> cards, int chiPengGangNum, List<Integer> hun, int lastCard) {
        List<HuCardType> huList = new ArrayList<>();
        if (playerCardsInfo.isHasSpecialHu(hu_七小对)) {
            HuCardType huCardType = isQixiaodui_hun(cards, playerCardsInfo, hun, lastCard);
            if (huCardType != null) {
                huCardType.setFan(playerCardsInfo.getSpecialHuScore(hu_七小对)).specialHuList.add(hu_七小对);
                huList.add(huCardType);
            }
        }

        if (playerCardsInfo.isHasSpecialHu(hu_豪华七小对)) {
            HuCardType huCardType = isHaoHuaQixiaodui_hun(cards, playerCardsInfo, hun, lastCard);
            if (huCardType != null) {
                huCardType.setFan(playerCardsInfo.getSpecialHuScore(hu_豪华七小对)).specialHuList.add(hu_豪华七小对);
                huList.add(huCardType);
            }
        }

        if (playerCardsInfo.isHasSpecialHu(HuType.hu_清七对)) {
            Boolean isQingqidui = isQingQiDui_hun(cards, playerCardsInfo, hun, lastCard);
            if (isQingqidui) {
                HuCardType huCardType = new HuCardType();
                huCardType.setFan(playerCardsInfo.getSpecialHuScore(hu_清七对)).specialHuList.add(hu_清七对);
                huList.add(huCardType);
            }
        }




        huList.addAll(HuWithHun.getHuCardType(playerCardsInfo, cards,analyse(convert(cards)), chiPengGangNum, hun, lastCard));

        for (HuCardType huCardType : huList) {
            //设置胡的类型
            HuCardType.setHuCardType(huCardType, playerCardsInfo);
            huCardType.tingCardType = lastCard;
//            for (Integer huType : huCardType.specialHuList) {
//                if (playerCardsInfo.getSpecialHuScore().containsKey(huType)) {
//                    huCardType.fan += playerCardsInfo.getSpecialHuScore().get(huType);
//                }
//            }
        }
        if (huList.size() > 0) {
//            System.out.println("hello");
        }

        return huList;
    }


    public static List<Integer> isTing(List<String> cards, PlayerCardsInfoMj playerCardsInfo, HuLimit limit) {
        List<Integer> exclude = new ArrayList<>();
        //TODO 杠过的牌也可以听 去掉下面两行
//        exclude.addAll(playerCardsInfo.getAnGangType());
//        exclude.addAll(playerCardsInfo.getMingGangType().keySet());

        List<Integer> ting = new ArrayList<>();
        for (int i = 0; i < n_zero.length; i++) {
            //不包含
            if (!exclude.contains(i)) {
                //加入数组
                int[] a = convert(cards);
                int[] b = Arrays.copyOf(a, a.length + 1);
                b[a.length] = i;

                String addCard = CardTypeUtil.getCardStrByType(i);
                List<String> newCards = new ArrayList<>();
                newCards.addAll(cards);
                newCards.add(addCard);

                if (isHu(newCards, playerCardsInfo, i, limit).size() > 0) {
                    ting.add(i);
                }
            }
        }
        return ting;
    }

    /**
     * @param cards
     * @param playerCardsInfo
     * @param limit
     * @param removeCard      听删掉的牌
     * @return
     */
    public static List<HuCardType> getTingHuList(List<String> cards, PlayerCardsInfoMj playerCardsInfo, HuLimit limit, String removeCard) {
        List<HuCardType> result = new ArrayList<>();
        Set<Integer> tingSet = new HashSet<>();
        for (int i = 0; i < n_zero.length; i++) {
            String addCard = CardTypeUtil.getCardStrByType(i);
            List<String> newCards = new ArrayList<>();
            newCards.addAll(cards);
            newCards.add(addCard);

            List<HuCardType> huCardTypes = isHu(newCards, playerCardsInfo, i, limit);
            for (HuCardType huCardType : huCardTypes) {
                tingSet.add(i);
                huCardType.tingCardType = i;
                huCardType.cards.clear();
                huCardType.cards.addAll(newCards);
                huCardType.tingRemoveCard = removeCard;
            }
            result.addAll(huCardTypes);
        }
        if (tingSet.size() > 1) {
            for (HuCardType huCardType : result) {
                huCardType.isCheckYiZhangying = false;
            }
        }
        return result;
    }


    /**
     * @param cards
     * @param playerCardsInfo
     * @param removeCard      听删掉的牌
     * @return
     */
    public static List<HuCardType> getTingHuListWithHun(List<String> cards, PlayerCardsInfoMj playerCardsInfo, List<Integer> hun, String removeCard,int chiPengGangNum) {
        List<HuCardType> result = new ArrayList<>();
        Set<Integer> tingSet = new HashSet<>();
//        int chiPengGangNum = playerCardsInfo.getChiPengGangNum();
        for (int i = 0; i < n_zero.length; i++) {
            String addCard = CardTypeUtil.getCardStrByType(i);
            List<String> newCards = new ArrayList<>();
            newCards.addAll(cards);
            newCards.add(addCard);

            List<HuCardType> huCardTypes = isHu(playerCardsInfo, newCards, chiPengGangNum, hun, i);
            for (HuCardType huCardType : huCardTypes) {
                tingSet.add(i);
                huCardType.tingCardType = i;
                huCardType.cards.clear();
                huCardType.cards.addAll(newCards);
                huCardType.tingRemoveCard = removeCard;
            }
            result.addAll(huCardTypes);
        }
        if (tingSet.size() > 1) {
            for (HuCardType huCardType : result) {
                huCardType.isCheckYiZhangying = false;
            }
        }
        return result;
    }


    //胡牌
    static List<HuCardType> agari(int[] n, boolean... isHasFengShun) {
        List<HuCardType> ret = new ArrayList<>();

        List<List<CardGroup>> list = new ArrayList();
        List<CardGroup> groups = new ArrayList<>();
        boolean isHasF = isHasFengShun.length != 0 && isHasFengShun[0];
        Hu.isHu(n, list, groups, isHasF);
        return Hu.convert(list);
//        for (int i = 0; i < 34; i++) {
//
//            for (int ke_first = 0; ke_first < 2; ke_first++) {
//                Integer[] jiang = new Integer[1];
//                ArrayList<Integer> ke = new ArrayList<Integer>();
//                ArrayList<Integer> shun = new ArrayList<Integer>();
//                ArrayList<List<Integer>> feng = new ArrayList<>();
//                int zi_num = 0;
//
//                int[] t = n.clone();
//                if (t[i] >= 2) {
//                    // 循环可以做将的牌
//                    t[i] -= 2;
//                    jiang[0] = i;
//
//                    if (ke_first == 0) {//先找刻
//                        // 寻找刻
//                        for (int j = 0; j < 34; j++) {
//                            if (t[j] >= 3) {
//                                t[j] -= 3;
//                                ke.add(j);
//                            }
//                        }
//                        // 顺
//                        for (int a = 0; a < 3; a++) {
//                            for (int b = 0; b < 7; ) {//8万以后不能成顺
//                                if (t[9 * a + b] >= 1 && t[9 * a + b + 1] >= 1 && t[9 * a + b + 2] >= 1) {
//                                    int one = 9 * a + b;
//                                    int two = 9 * a + b + 1;
//                                    int three = 9 * a + b + 2;
//                                    t[one]--;
//                                    t[two]--;
//                                    t[three]--;
//                                    shun.add(9 * a + b);
//                                } else {
//                                    b++;
//                                }
//                            }
//                        }
//                        if (isHasFengShun.length > 0 && isHasFengShun[0]) {
//
//                            //处理风
//                            disposeFENG(t, feng);
//                            //处理字
//                            zi_num = disposeZFB(t);
//                        }
//                    } else {//先找顺
//                        if (isHasFengShun.length > 0 && isHasFengShun[0]) {
//                            //处理风
//                            disposeFENG(t, feng);
//                            //处理字
//                            zi_num = disposeZFB(t);
//                        }
//
//                        // 找顺
//                        for (int a = 0; a < 3; a++) {
//                            for (int b = 0; b < 7; ) {
//                                if (t[9 * a + b] >= 1 &&
//                                        t[9 * a + b + 1] >= 1 &&
//                                        t[9 * a + b + 2] >= 1) {
//                                    t[9 * a + b]--;
//                                    t[9 * a + b + 1]--;
//                                    t[9 * a + b + 2]--;
//                                    shun.add(9 * a + b);
//                                } else {
//                                    b++;
//                                }
//                            }
//                        }
//                        // 找刻
//                        for (int j = 0; j < 34; j++) {
//                            if (t[j] >= 3) {
//                                t[j] -= 3;
//                                ke.add(j);
//                            }
//                        }
//                    }
//
//                    //
//                    if (Arrays.equals(t, n_zero)) {
//                        HuCardType huCardType = new HuCardType();
//                        huCardType.jiang = jiang[0];
//                        huCardType.shun = shun;
//                        huCardType.ke = ke;
//                        huCardType.feng_shun = feng;
//                        huCardType.zi_shun = zi_num;
//
//                        add(ret, huCardType);
//                    }
//                }
//            }
//        }
//        return ret;
    }

    private static void add(List<HuCardType> ret, HuCardType huCardType) {
        boolean isIn = false;
        for (HuCardType hct : ret) {
            if (HuCardType.isEquil(hct, huCardType)) {
                isIn = true;
            }
        }
        if (!isIn) {
            ret.add(huCardType);
        }
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

    private static void disposeFENG(int[] t, List<List<Integer>> list) {
        List<Integer> fengList = new ArrayList<>();
        for (int i = 27; i < 31; i++) {
            for (int j = 0; j < t[i]; j++) {
                fengList.add(i);
            }
        }
        int size = fengList.size();
        if (size == 0 || size % 3 != 0) {
            return;
        }
        int disposeNum = size / 3;
        switch (disposeNum) {
            case 1:
                list.addAll(disposeFENG1(fengList));
                break;
            case 2:
                list.addAll(disposeFENG2(fengList));
                break;
            case 3:
                list.addAll(disposeFENG3(fengList));
                break;
            case 4:
                list.addAll(disposeFENG4(fengList));
                break;
        }
        for (List<Integer> l : list) {
            for (Integer i : l) {
                t[i]--;
            }
        }
    }


    public static List<List<Integer>> disposeFENG1(List<Integer> list) {
        int size = feng.size();
        List<List<Integer>> result = new ArrayList<>();
        for (int one = 0; one < size; one++) {
            List<Integer> temp = new ArrayList<>(list);
            removeList(temp, feng.get(one));
            if (temp.size() == 0) {
                result.add(new ArrayList<>(feng.get(one)));
                return result;
            }

        }
        return result;
    }

    public static List<List<Integer>> disposeFENG2(List<Integer> list) {
        int size = feng.size();
        List<List<Integer>> result = new ArrayList<>();
        for (int one = 0; one < size; one++) {
            for (int two = 0; two < size; two++) {
                List<Integer> temp = new ArrayList<>(list);
                removeList(temp, feng.get(one));
                removeList(temp, feng.get(two));
                if (temp.size() == 0) {
                    result.add(new ArrayList<>(feng.get(one)));
                    result.add(new ArrayList<>(feng.get(two)));
                    return result;
                }

            }
        }
        return result;
    }

    public static List<List<Integer>> disposeFENG3(List<Integer> list) {
        int size = feng.size();
        List<List<Integer>> result = new ArrayList<>();
        for (int one = 0; one < size; one++) {
            for (int two = 0; two < size; two++) {
                for (int three = 0; three < size; three++) {
                    List<Integer> temp = new ArrayList<>(list);
                    removeList(temp, feng.get(one));
                    removeList(temp, feng.get(two));
                    removeList(temp, feng.get(three));

                    if (temp.size() == 0) {
                        result.add(new ArrayList<>(feng.get(one)));
                        result.add(new ArrayList<>(feng.get(two)));
                        result.add(new ArrayList<>(feng.get(three)));
                        return result;
                    }
                }

            }
        }
        return result;
    }

    public static List<List<Integer>> disposeFENG4(List<Integer> list) {
        List<List<Integer>> result = new ArrayList<>();
        result.add(feng.get(0));
        result.add(feng.get(1));
        result.add(feng.get(2));
        result.add(feng.get(3));
        return result;
    }


    public static void removeList(List<Integer> list, List<Integer> list1) {
        for (Integer i : list1) {
            list.remove(i);
        }
    }

    /**
     * 是否是清七对
     *
     * @param cards
     * @param playerCardsInfo
     * @return
     */
    public static boolean isQingqidui(List<String> cards, PlayerCardsInfoMj playerCardsInfo) {
        return isYiSe_wtt(cards) && (isQixiaodui(cards, playerCardsInfo) || isHaoHuaQixiaodui(cards, playerCardsInfo, 1) || isShuangHaoQixiaodui(cards, playerCardsInfo));
    }


    public static boolean isYiSe_wtt(List<String> cards) {
        List<String> temp = new ArrayList<>();
        temp.addAll(cards);
        Set<Integer> set = new HashSet<>();
        for (String card : temp) {
            int group = CardTypeUtil.getCardGroup(card);
            set.add(group);
            if (!(group == CardTypeUtil.GROUP_TIAO || group == CardTypeUtil.GROUP_TONG || group == CardTypeUtil.GROUP_WAN)) {
                return false;
            }
        }
        return set.size() == 1;
    }

    public static boolean isQixiaodui_common(List<String> cards, PlayerCardsInfoMj playerCardsInfo) {
        List<String> temp = new ArrayList<>();
        temp.addAll(cards);
        int size = playerCardsInfo.getMingGangType().size() + playerCardsInfo.getAnGangType().size() + playerCardsInfo.getPengType().size() + playerCardsInfo.getChiType().size();
        ;
        if (size > 0) {
            return false;
        }
        Map<Integer, Integer> cardMap = PlayerCardsInfoMj.getCardNum(temp);
        if (cardMap.size() > 7) {
            return false;
        }
        for (Integer num : cardMap.values()) {
            if (num != 2 && num != 4) {
                return false;
            }
        }
        return true;

    }

    public static boolean isQixiaodui(List<String> cards, PlayerCardsInfoMj playerCardsInfo) {
        List<String> temp = new ArrayList<>();
        temp.addAll(cards);
        int size = playerCardsInfo.getMingGangType().size() + playerCardsInfo.getAnGangType().size() + playerCardsInfo.getPengType().size() + playerCardsInfo.getChiType().size();
        if (size > 0) {
            return false;
        }
        Map<Integer, Integer> cardMap = PlayerCardsInfoMj.getCardNum(temp);
        if (cardMap.size() > 7) {
            return false;
        }
        for (Integer num : cardMap.values()) {
            if (num != 2) {
                return false;
            }
        }
        return true;

    }


    public static boolean isHaoHuaQixiaodui(List<String> cards, PlayerCardsInfoMj playerCardsInfo, int haohuaNum) {
        List<String> temp = new ArrayList<>();
        temp.addAll(cards);
        int size = playerCardsInfo.getMingGangType().size() + playerCardsInfo.getAnGangType().size() + playerCardsInfo.getPengType().size() + playerCardsInfo.getChiType().size();
        if (size > 0) {
            return false;
        }
        Map<Integer, Integer> cardMap = PlayerCardsInfoMj.getCardNum(playerCardsInfo.getCardsNoGang(temp));
        int isHas4Num = 0;
        for (Integer num : cardMap.values()) {
            if (num != 2 && num != 4) {
                return false;
            }
            if (num == 4) {
                isHas4Num++;
            }
        }
        return isHas4Num == haohuaNum;

    }

    public static boolean isShuangHaoQixiaodui(List<String> cards, PlayerCardsInfoMj playerCardsInfo) {
        List<String> temp = new ArrayList<>();
        temp.addAll(cards);

        int size = playerCardsInfo.getMingGangType().size() + playerCardsInfo.getAnGangType().size() + playerCardsInfo.getPengType().size() + playerCardsInfo.getChiType().size();
        if (size > 0) {
            return false;
        }
        Map<Integer, Integer> cardMap = PlayerCardsInfoMj.getCardNum(temp);
        int isHas4Num = 0;
        for (Integer num : cardMap.values()) {
            if (num != 2 && num != 4) {
                return false;
            }
            if (num == 4) {
                isHas4Num++;
            }
        }

        return isHas4Num >= 2;

    }

    public static boolean cardIsHun(List<Integer> hun, int cardType) {
        return hun.contains(cardType);
    }

    public static HuCardType isQixiaodui_hun(List<String> cards, PlayerCardsInfoMj playerCardsInfo, List<Integer> hun, int lastCard) {

        HuCardType huCardType = new HuCardType();

        List<String> temp = new ArrayList<>();
        temp.addAll(cards);
        int size = playerCardsInfo.getMingGangType().size() + playerCardsInfo.getAnGangType().size() + playerCardsInfo.getPengType().size() + playerCardsInfo.getChiType().size();
        if (size > 0) {
            return null;
        }
        int hunSize = playerCardsInfo.getHunNum();
        Map<Integer, Integer> cardMap = PlayerCardsInfoMj.getCardNum(temp);
        //去掉混的牌型种类
        for (Integer hunType : playerCardsInfo.getGameInfo().getHun()) {
            cardMap.remove(hunType);
        }
        if (cardMap.size() > 7) {
            return null;
        }
        List<Integer> needHun = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : cardMap.entrySet()) {
            //多于两张牌 可能是豪七
            if (entry.getValue() != 2 && entry.getValue() != 1) {
                return null;
            }
            if (entry.getValue() == 1) {
                needHun.add(entry.getKey());
                huCardType.hunReplaceCard.add(entry.getKey());
            }
        }

        //混不够用
        if (needHun.size() > hunSize) {
            return null;
        }

        //判断混替代什么
        //最后抓的牌是混
        if (hunSize - needHun.size() >= 2) {
            //-1 什么都能代替
            huCardType.hunReplaceCard.add(-1);
        }


        return huCardType;
    }


    public static HuCardType isHaoHuaQixiaodui_hun(List<String> cards, PlayerCardsInfoMj playerCardsInfo, List<Integer> hun, int lastCard) {
        HuCardType huCardType = new HuCardType();
        List<String> temp = new ArrayList<>();
        temp.addAll(cards);

        int size = playerCardsInfo.getMingGangType().size() + playerCardsInfo.getAnGangType().size() + playerCardsInfo.getPengType().size() + playerCardsInfo.getChiType().size();
        if (size > 0) {
            return null;
        }


        int hunSize = playerCardsInfo.getHunNum();
        Map<Integer, Integer> cardMap = PlayerCardsInfoMj.getCardNum(temp);
        //去掉混的牌型种类
        for (Integer hunType : playerCardsInfo.getGameInfo().getHun()) {
            cardMap.remove(hunType);
        }

        if (cardMap.size() > 7) {
            return null;
        }

        int isHas4Num = 0;
        List<Integer> needHunList = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : cardMap.entrySet()) {
            if (entry.getValue() != 2 && entry.getValue() != 4) {
                needHunList.add(entry.getKey());
            }
            if (entry.getValue() == 4) {
                isHas4Num++;
            }
        }
        if (needHunList.size() > hunSize) {
            return null;
        }
        //没有四张牌的
        boolean lastCardIsHun = cardIsHun(hun, lastCard);
        if (isHas4Num == 0) {

            List<Integer> has3key = mapHasValue(cardMap, 3);
            if (has3key.size() > 0) {
                if (lastCardIsHun) {
                    huCardType.hunReplaceCard.addAll(has3key);
                }
            } else {
                //需要 混数 - 需要混数 > 2 才能凑够豪华
                if (hunSize - needHunList.size() < 2) {
                    return null;
                }
                //混能替换的牌是
                if (lastCardIsHun) {
                    huCardType.hunReplaceCard.addAll(mapHasValue(cardMap, 1));
                    huCardType.hunReplaceCard.addAll(mapHasValue(cardMap, 2));
                }
            }
        }


        return huCardType;

    }


    private static boolean isQingQiDui_hun(List<String> cards, PlayerCardsInfoMj playerCardsInfo, List<Integer> hun, int lastCard) {
        List<String> noHunCards = new ArrayList<>();
        for (String card : cards) {

            if (!hun.contains(CardTypeUtil.getTypeByCard(card))) {
                noHunCards.add(card);
            }
        }
        return isYiSe_wtt(cards) &&
                (isQixiaodui_hun(cards, playerCardsInfo, hun, lastCard) != null || isHaoHuaQixiaodui_hun(cards, playerCardsInfo, hun, lastCard) != null);
    }

    private static List<Integer> mapHasValue(Map<Integer, Integer> map, int size) {
        List<Integer> result = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            if (entry.getValue() == size) {
                result.add(entry.getKey());
            }
        }
        return result;
    }


    public static boolean is13BuKao(List<String> cards, PlayerCardsInfoMj playerCardsInfo) {

        if (cards.size() != 14) {
            return false;
        }

        Collections.sort(cards);
        //判断是不是有且仅有东南西北中发白加上将
        List<Integer> list = new ArrayList<>();

        List<Integer> list2 = new ArrayList<>();

        for (int i = 0; i < cards.size(); i++) {

            int index = Integer.parseInt(cards.get(i));
            if (index >= 108 && index <= 135) {
                list.add((index - 108) / 4);
            } else {
                list2.add(index);
            }
        }

        if (list.size() != 8) {
            return false;
        }

        HashSet h = new HashSet(list);
        list.clear();
        list.addAll(h);
        Collections.sort(list);
        if (list.size() != 7) {
            return false;
        }
        System.out.println("含有东南西北中发白带将");

        //判断六张牌是不是都是万或都是筒或都是条
        int last = 0;
        for (int i = 0; i < list2.size(); i++) {
            int a = list2.get(i);
            int current = isWanTongTiao(a);
            if (last != current && i != 0) {
                last = 0;
                break;
            }
            last = current;
        }

        //如果种类相同
        if (last != 0) {
            return false;
        } else {

            int result1 = m13buKaoType(list2.get(0), list2.get(1), list2.get(2));
            int result2 = m13buKaoType(list2.get(3), list2.get(4), list2.get(5));

            if (result1 == 0 || result2 == 0) {
                return false;
            }
        }

        return true;
    }

    public static int isWanTongTiao(int d) {

        int ret = 0;

        if ((d / 4 < 9) && (d / 4 >= 0)) {
            //万
            ret = 1;
        } else if (d / 4 < 18) {
            ret = 2;
            //条
        } else if (d / 4 < 27) {
            ret = 3;
            //筒
        } else {
            ret = 0;
        }
        return ret;
    }

    public static int m147_258_369_158(int shang1, int shang2, int shang3) {

        if (shang1 == 0 && shang2 == 3 && shang3 == 6) {
            return 1;
        } else if (shang1 == 9 && shang2 == 12 && shang3 == 15) {
            return 1;
        } else if (shang1 == 18 && shang2 == 21 && shang3 == 24) {
            return 1;
        } else if (shang1 == 1 && shang2 == 4 && shang3 == 7) {
            return 2;
        } else if (shang1 == 10 && shang2 == 13 && shang3 == 16) {
            return 2;
        } else if (shang1 == 19 && shang2 == 22 && shang3 == 25) {
            return 2;
        } else if (shang1 == 2 && shang2 == 5 && shang3 == 8) {
            return 3;
        } else if (shang1 == 11 && shang2 == 14 && shang3 == 17) {
            return 3;
        } else if (shang1 == 20 && shang2 == 23 && shang3 == 26) {
            return 3;
        } else if (shang1 == 0 && shang2 == 4 && shang3 == 7) {
            return 4;
        } else if (shang1 == 9 && shang2 == 13 && shang3 == 16) {
            return 4;
        } else if (shang1 == 18 && shang2 == 22 && shang3 == 25) {
            return 4;
        }
        return 0;
    }

    public static int m13buKaoType(int a, int b, int c) {

        int ret1 = isWanTongTiao(a);
        int ret2 = isWanTongTiao(b);
        int ret3 = isWanTongTiao(c);

        int ret = 0;
        if ((ret1 == ret2) && (ret1 == ret3) && (ret1 != 0)) {
            ret = ret1;
        } else {
            return 0;
        }

        int shang1 = a / 4;
        int yu1 = a % 4;

        int shang2 = b / 4;
        int yu2 = b % 4;

        int shang3 = c / 4;
        int yu3 = c % 4;

        //花色是否相同
//        if(!((yu1 == yu2) && (yu1 == yu3))){
//            return 0;
//        }

        int res = m147_258_369_158(shang1, shang2, shang3);
        if (res == 0) {
            return 0;
        }

        //百位代表万筒条， 十位代表花色， 个位代表 那种牌型
        int x = res + yu1 * 10 + ret * 100;

        return x;
    }

    public static boolean is13Yao(List<String> cards, PlayerCardsInfoMj playerCardsInfo) {
        List<String> temp = new ArrayList<>();
        temp.addAll(cards);
        Map<Integer, Integer> cardMap = PlayerCardsInfoMj.getCardNum(temp);
        boolean isHas2Num = false;
        for (int y : yao) {
            if (!cardMap.containsKey(y)) {
                return false;
            } else {
                if (cardMap.get(y) == 2) {
                    isHas2Num = true;
                }
            }

        }
        return isHas2Num;
    }


    public static void main(String[] args) {


        int[] hai = {
                TIAO2, TIAO3, TIAO4,
                TIAO6, TIAO7, TIAO8,
                dong, nan, bei,
                dong, xi, bei,
                zhong, zhong};

        int[] n = null;
        List<HuCardType> ret = null;

        n = analyse(hai);
        ret = agari(n, true);
        System.out.println(ret);
//        }
//        System.out.println("耗时 = "+(System.currentTimeMillis() - time));

        for (HuCardType r : ret) {

            System.out.println(r);
            System.out.println();
        }

        List<String> list = new ArrayList<String>();

//        list.add("108");
//        list.add("112");
//        list.add("116");
//        list.add("120");
//        list.add("124");
//        list.add("128");
//        list.add("132");
//        list.add("133");


//        list.add("000");
//        list.add("024");
//        list.add("012");

//        list.add("036");
//        list.add("048");
//        list.add("060");

//        list.add("040");
//        list.add("052");
//        list.add("064");

//        list.add("044");
//        list.add("056");
//        list.add("068");

//            boolean res =  is13BuKao(list, null);
//
//            System.out.println(res);

//        Scanner sc = new Scanner(System.in);
//
//
//        while (true){
//
//            System.out.println("1请输入第一张牌");
//            String a = sc.next();
//            System.out.println("1请输入第二张牌");
//            String b = sc.next();
//            System.out.println("1请输入第三张牌");
//            String c = sc.next();
//
//            System.out.println("2请输入第一张牌");
//            String d = sc.next();
//            System.out.println("2请输入第二张牌");
//            String e = sc.next();
//            System.out.println("2请输入第三张牌");
//            String f = sc.next();
//
//            list.clear();
//            list.add("108");
//            list.add("109");
//            list.add("112");
//            list.add("116");
//            list.add("120");
//            list.add("124");
//            list.add("128");
//            list.add("132");
////            list.add("133");
//
//            list.add(a);
//            list.add(b);
//            list.add(c);
//
//            list.add(d);
//            list.add(e);
//            list.add(f);
//
//            boolean res =  is13BuKao(list, null);
//            System.out.println("==================");
//            System.out.println(res);
//        }


        list.clear();
        list.add("081");
        list.add("121");
        list.add("115");
        list.add("116");
        list.add("110");
        list.add("126");
        list.add("130");
        list.add("134");

        list.add("003");
        list.add("014");
        list.add("024");
        list.add("093");
        list.add("107");
        list.add("119");

        boolean res = is13BuKao(list, null);
        System.out.println("==================");
        System.out.println(res);

    }


    /**
     * 测试是否0和的数据
     *
     * @param @param  map
     * @param @return 设定文件
     * @return String    返回类型
     * @throws
     * @Title: getUserAndScore
     * @Creater: Clark
     * @Description:
     */
    public static String getUserAndScore(Map<Integer, Integer> map) {
        StringBuffer sb = new StringBuffer();
        int temp = 0;
        for (Integer i : map.keySet()) {
            sb.append("UserId:" + i + "得分为：" + map.get(i));
            sb.append(";");
            temp += map.get(i);
        }
        sb.append("是不是0和：" + (temp == 0 ? "是" : "否"));
        return sb.toString();
    }

//    static final int dong = 27;
//    static final int nan = 28;
//    static final int xi = 29;
//    static final int bei = 30;
//    static final int zhong = 31;
//    static final int fa = 32;
//    static final int bai = 33;

    /**
     * 断幺
     *
     * @param cards
     * @return
     */
    private static boolean duanyao(List<String> cards, PlayerCardsInfoMj playerCardsInfo) {

        List<Integer> tempList = new ArrayList<>();
        tempList.add(0);
        tempList.add(8);
        tempList.add(9);
        tempList.add(17);
        tempList.add(18);
        tempList.add(26);
        tempList.add(27);
        tempList.add(28);
        tempList.add(29);
        tempList.add(30);
        tempList.add(31);
        tempList.add(32);
        tempList.add(33);

        boolean results = true;
        a:
        for (String s : playerCardsInfo.getCards()) {
            if (tempList.contains(CardTypeUtil.getTypeByCard(s))) {
                results = false;
                break a;
            }
        }

        return results;
    }
}
