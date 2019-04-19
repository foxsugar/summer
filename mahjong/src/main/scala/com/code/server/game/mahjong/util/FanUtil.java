package com.code.server.game.mahjong.util;


import com.code.server.game.mahjong.logic.CardTypeUtil;
import com.code.server.game.mahjong.logic.PlayerCardsInfoMj;

import java.util.*;

import static com.code.server.game.mahjong.logic.CardTypeUtil.getCardGroup;


/**
 * Created by win7 on 2016/12/6.
 */
public class FanUtil implements HuType {


    static List<Integer> card3 = new ArrayList<Integer>();
    static List<Integer> card7 = new ArrayList<Integer>();
    static List<Integer> card5 = new ArrayList<Integer>();
    static List<Integer> ytl1 = new ArrayList<>();
    static List<Integer> ytl2 = new ArrayList<>();
    static List<Integer> ytl3 = new ArrayList<>();
    static List<Integer> dy = new ArrayList<>();


    static {
        //3
        card3.add(2);
        card3.add(11);
        card3.add(20);

        //7
        card7.add(6);
        card7.add(15);
        card7.add(24);

        //5
        card5.add(4);
        card5.add(13);
        card5.add(22);

        //
        ytl1.add(0);
        ytl1.add(3);
        ytl1.add(6);

        ytl2.add(9);
        ytl2.add(12);
        ytl2.add(15);

        ytl3.add(18);
        ytl3.add(21);
        ytl3.add(24);

        dy.add(0);
        dy.add(8);
        dy.add(9);
        dy.add(17);
        dy.add(18);
        dy.add(26);

        dy.add(27);
        dy.add(28);
        dy.add(29);
        dy.add(30);
        dy.add(31);
        dy.add(32);
        dy.add(33);
    }

    public static int compute(List<String> cards, HuCardType huCardType, int tingCardType, PlayerCardsInfoMj playerCardsInfo) {
        int fan = huCardType.fan;
        if (playerCardsInfo.isHasSpecialHu(hu_缺一门) && no_group_num(cards, huCardType) == 1) {//缺一门
            huCardType.specialHuList.add(hu_缺一门);
            fan += playerCardsInfo.getSpecialHuScore(hu_缺一门)  * playerCardsInfo.getPuMutiple();
        }
        if (playerCardsInfo.isHasSpecialHu(hu_缺两门) && no_group_num(cards, huCardType) >= 2) {//缺两门
            huCardType.specialHuList.add(hu_缺两门);
            fan += playerCardsInfo.getSpecialHuScore(hu_缺两门);
        }
        if (playerCardsInfo.isHasSpecialHu(hu_缺两门严格) && no_group_num(cards, huCardType) == 2) {//缺两门
            huCardType.specialHuList.add(hu_缺两门严格);
            fan += playerCardsInfo.getSpecialHuScore(hu_缺两门严格) * playerCardsInfo.getPuMutiple();
        }
        if (playerCardsInfo.isHasSpecialHu(hu_缺三门) && no_group_num(cards, huCardType) == 3) {//缺两门
            huCardType.specialHuList.add(hu_缺三门);
            fan += playerCardsInfo.getSpecialHuScore(hu_缺三门)* playerCardsInfo.getPuMutiple();
        }
        if (playerCardsInfo.isHasSpecialHu(hu_孤将) && is_gujiang(cards, huCardType)) {//孤将
            huCardType.specialHuList.add(hu_孤将);
            fan += playerCardsInfo.getSpecialHuScore(hu_孤将);
        }
        if (playerCardsInfo.isHasSpecialHu(hu_一张赢) && playerCardsInfo.getYiZhangyingSet().contains(tingCardType)) {//hu_一张赢
            huCardType.specialHuList.add(hu_一张赢);
            fan += playerCardsInfo.getSpecialHuScore(hu_一张赢);
        }
        if (playerCardsInfo.isHasSpecialHu(hu_门清) && is_mengqing(cards, huCardType)) {//门清
            huCardType.specialHuList.add(hu_门清);
            fan += playerCardsInfo.getSpecialHuScore(hu_门清);
        }
        if (playerCardsInfo.isHasSpecialHu(hu_三风一副) && getFengNum(cards, huCardType) == 1) {//三风一副
            huCardType.specialHuList.add(hu_三风一副);
            fan += playerCardsInfo.getSpecialHuScore(hu_三风一副);
        }
        if (playerCardsInfo.isHasSpecialHu(hu_三风两副) && getFengNum(cards, huCardType) == 2) {//三风两副
            huCardType.specialHuList.add(hu_三风两副);
            fan += playerCardsInfo.getSpecialHuScore(hu_三风两副);
        }
        if (playerCardsInfo.isHasSpecialHu(hu_三风三副) && getFengNum(cards, huCardType) == 3) {//三风三副
            huCardType.specialHuList.add(hu_三风三副);
            fan += playerCardsInfo.getSpecialHuScore(hu_三风三副);
        }
        if (playerCardsInfo.isHasSpecialHu(hu_三元一副) && getYuanNum(cards, huCardType) == 1) {//三元一副
            huCardType.specialHuList.add(hu_三元一副);
            fan += playerCardsInfo.getSpecialHuScore(hu_三元一副);
        }
        if (playerCardsInfo.isHasSpecialHu(hu_三元两副) && getYuanNum(cards, huCardType) == 2) {//三元两副
            huCardType.specialHuList.add(hu_三元两副);
            fan += playerCardsInfo.getSpecialHuScore(hu_三元两副);
        }
        if (playerCardsInfo.isHasSpecialHu(hu_三元三副) && getYuanNum(cards, huCardType) == 3) {//三元三副
            huCardType.specialHuList.add(hu_三元三副);
            fan += playerCardsInfo.getSpecialHuScore(hu_三元三副);
        }
        if (playerCardsInfo.isHasSpecialHu(hu_字一色) && isZiyise(cards, huCardType)) {//字一色
            huCardType.specialHuList.add(hu_字一色);
            fan += playerCardsInfo.getSpecialHuScore(hu_字一色);
        }

        if (playerCardsInfo.isHasSpecialHu(hu_一条龙) && isYitiaolong(cards, huCardType)) {
            huCardType.specialHuList.add(hu_一条龙);
            fan += playerCardsInfo.getSpecialHuScore(hu_一条龙);
        }

        if (playerCardsInfo.isHasSpecialHu(hu_架龙) && isYitiaolong_jialong(cards, huCardType)) {
            huCardType.specialHuList.add(hu_架龙);
            fan += playerCardsInfo.getSpecialHuScore(hu_架龙);
        }

        if (playerCardsInfo.isHasSpecialHu(hu_清一色架龙) && isYitiaolong_jialong(cards, huCardType)
                && isQingyise(playerCardsInfo.getCards(), huCardType)) {
            huCardType.specialHuList.add(hu_清一色架龙);
            fan += playerCardsInfo.getSpecialHuScore(hu_清一色架龙);
        }

        if (playerCardsInfo.isHasSpecialHu(hu_夹张) && jiazhang(cards, huCardType, tingCardType)) {//hu_夹张
            huCardType.specialHuList.add(hu_夹张);
            fan += playerCardsInfo.getSpecialHuScore(hu_夹张);
        }
        if (playerCardsInfo.isHasSpecialHu(hu_边张) && bianzhang(cards, huCardType, tingCardType)) {//hu_边张
            huCardType.specialHuList.add(hu_边张);
            fan += playerCardsInfo.getSpecialHuScore(hu_边张);
        }
        if (playerCardsInfo.isHasSpecialHu(hu_边张_乾安) && bianzhangQA(cards, huCardType, tingCardType)) {//hu_边张_乾安
            huCardType.specialHuList.add(hu_边张_乾安);
            fan += playerCardsInfo.getSpecialHuScore(hu_边张_乾安);
        }
        if (playerCardsInfo.isHasSpecialHu(hu_吊张) && diaozhang(cards, huCardType, tingCardType)) {//hu_吊张
            huCardType.specialHuList.add(hu_吊张);
            fan += playerCardsInfo.getSpecialHuScore(hu_吊张);
        }
        if (playerCardsInfo.isHasSpecialHu(hu_飘胡) && isPiaohu(cards, huCardType)) {//hu_飘胡
            huCardType.specialHuList.add(hu_飘胡);
            fan += playerCardsInfo.getSpecialHuScore(hu_飘胡);
        }

        if (playerCardsInfo.isHasSpecialHu(hu_清龙) && isQinglong(cards, huCardType)) {//清龙
            huCardType.specialHuList.add(hu_清龙);
            fan += playerCardsInfo.getSpecialHuScore(hu_清龙);
        }

        if (playerCardsInfo.isHasSpecialHu(hu_清一色) && isQingyise(cards, huCardType)) {
            huCardType.specialHuList.add(hu_清一色);
            fan += playerCardsInfo.getSpecialHuScore(hu_清一色);
        }

        if (playerCardsInfo.isHasSpecialHu(hu_吊将) && isDiaojiang(tingCardType, huCardType)) {
            huCardType.specialHuList.add(hu_吊将);
            fan += playerCardsInfo.getSpecialHuScore(hu_吊将);
        }

        if (playerCardsInfo.isHasSpecialHu(hu_断幺) && duanyao(cards, tingCardType, huCardType)) {
            huCardType.specialHuList.add(hu_断幺);
            fan += playerCardsInfo.getSpecialHuScore(hu_断幺);
        }

        if (playerCardsInfo.isHasSpecialHu(HuType.hu_混一色) && isHunyise(playerCardsInfo.getCards(), huCardType)) {
            huCardType.specialHuList.add(hu_混一色);
            fan += playerCardsInfo.getSpecialHuScore(hu_混一色);
        }

        if (playerCardsInfo.isHasSpecialHu(HuType.hu_碰碰胡) && isPengpenghu(playerCardsInfo.getCards(), huCardType)) {
            huCardType.specialHuList.add(hu_碰碰胡);
            fan += playerCardsInfo.getSpecialHuScore(hu_碰碰胡);
        }

        if (playerCardsInfo.isHasSpecialHu(HuType.hu_清一色碰碰胡) && isPengpenghu(playerCardsInfo.getCards(), huCardType)
                && isQingyise(playerCardsInfo.getCards(), huCardType)) {
            huCardType.specialHuList.add(hu_清一色碰碰胡);
            fan += playerCardsInfo.getSpecialHuScore(hu_清一色碰碰胡);
        }

        if (playerCardsInfo.isHasSpecialHu(HuType.hu_中张) && isHasZhongZhang(cards, playerCardsInfo, huCardType)) {
            huCardType.specialHuList.add(hu_中张);
            fan += playerCardsInfo.getSpecialHuScore(hu_中张);
        }


        if (playerCardsInfo.isHasSpecialHu(HuType.hu_幺九) && isHuYaoJiu( huCardType)) {
            huCardType.specialHuList.add(hu_幺九);
            fan += playerCardsInfo.getSpecialHuScore(hu_幺九);
        }

        if (playerCardsInfo.isHasSpecialHu(HuType.hu_将对) && isHu258( cards, playerCardsInfo) && isPengpenghu(cards, huCardType)) {
            huCardType.specialHuList.add(hu_将对);
            fan += playerCardsInfo.getSpecialHuScore(hu_将对);
        }

        if (playerCardsInfo.isHasSpecialHu(HuType.hu_四碰) && huCardType.peng.size()==4) {
            huCardType.specialHuList.add(hu_四碰);
            fan += playerCardsInfo.getSpecialHuScore(hu_四碰);
        }

        if (playerCardsInfo.isHasSpecialHu(hu_风谱1)) {
            int size = huCardType.feng_shun.size() - 1;
            huCardType.specialHuList.add(hu_风谱1 + size);
            fan += playerCardsInfo.getSpecialHuScore(hu_风谱1 + size) * playerCardsInfo.getPuMutiple();
        }

        if (playerCardsInfo.isHasSpecialHu(hu_将谱1) && huCardType.zi_shun!=0) {
            int size = huCardType.zi_shun - 1;
            huCardType.specialHuList.add(hu_将谱1 + size);
            fan += playerCardsInfo.getSpecialHuScore(hu_将谱1 + size) * playerCardsInfo.getPuMutiple();
        }

        if (playerCardsInfo.isHasSpecialHu(hu_幺九谱1) && huCardType.yao_jiu_shun.size()>0) {

            int size = huCardType.yao_jiu_shun.size() - 1;
            huCardType.specialHuList.add(hu_幺九谱1 + size);
            fan += playerCardsInfo.getSpecialHuScore(hu_幺九谱1 + size) * playerCardsInfo.getPuMutiple();
        }


        fan = huCardType.clearRepeat(playerCardsInfo, fan);
        return fan;
    }

    /**
     * 夹张
     *
     * @param cards
     * @param huCardType
     * @return
     */
    private static boolean jiazhang(List<String> cards, HuCardType huCardType, int hucard) {
        boolean results = false;
        List<Integer> shun = huCardType.shun;
        Collections.sort(shun);
        for (int shunhu : shun) {
            boolean isSame = CardTypeUtil.getCardGroupByCardType(hucard) == CardTypeUtil.getCardGroupByCardType(shunhu);
            if (isSame && hucard - shunhu == 1) {
                results = true;
                break;
            }
        }
        return results;
    }

    /**
     * 边张
     *
     * @param cards
     * @param huCardType
     * @return
     */
    private static boolean bianzhang(List<String> cards, HuCardType huCardType, int hucard) {
        List<Integer> shun = huCardType.shun;
        Collections.sort(shun);

        if (card3.contains(hucard)) {
            //3
            for (int shunhu : shun) {
                //不包括3开头的顺子
                boolean isSame = CardTypeUtil.getCardGroupByCardType(hucard) == CardTypeUtil.getCardGroupByCardType(shunhu);
                if (isSame && hucard - shunhu == 2) {
                    for (int card : card3) {
                        if (CardTypeUtil.getCardGroupByCardType(card) == CardTypeUtil.getCardGroupByCardType(hucard)) {
                            if (shunhu != card) {
                                return true;
                            }
                        }
                    }
                }
            }
        } else if (card7.contains(hucard)) {
            //7
            for (int shunhu : shun) {
                //不包括5开头的顺子
                boolean isSame = CardTypeUtil.getCardGroupByCardType(hucard) == CardTypeUtil.getCardGroupByCardType(shunhu);
                if (isSame && hucard == shunhu) {
                    for (int card : card5) {
                        if (CardTypeUtil.getCardGroupByCardType(card) == CardTypeUtil.getCardGroupByCardType(hucard)) {
                            if (shunhu != card) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }


    /**
     * 边张
     *
     * @param cards
     * @param huCardType
     * @return
     */
    private static boolean bianzhangQA(List<String> cards, HuCardType huCardType, int hucard) {
        List<Integer> shun = huCardType.shun;
        Collections.sort(shun);

        if (card3.contains(hucard)) {
            //3
            for (int shunhu : shun) {
                boolean isSame = CardTypeUtil.getCardGroupByCardType(hucard) == CardTypeUtil.getCardGroupByCardType(shunhu);
                if (isSame && hucard - shunhu == 2) {
                    return true;
                }
            }
        } else if (card7.contains(hucard)) {
            //7
            for (int shunhu : shun) {
                boolean isSame = CardTypeUtil.getCardGroupByCardType(hucard) == CardTypeUtil.getCardGroupByCardType(shunhu);
                if (isSame && hucard == shunhu) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 吊张  (单吊将)
     *
     * @param cards
     * @param huCardType
     * @return
     */
    private static boolean diaozhang(List<String> cards, HuCardType huCardType, int hutype) {
        //将这张牌的类型
        int jiangtype = huCardType.jiang;
        return hutype == jiangtype;
    }

    /**
     * 缺几门 (万桶条 不包含风牌字牌)
     *
     * @param cards
     * @return
     */
    private static int no_group_num(List<String> cards, HuCardType huCardType) {
        Set<Integer> set = new HashSet<>();
        for (int group : getAllGroup(cards, huCardType)) {
            if (group == CardTypeUtil.GROUP_TONG || group == CardTypeUtil.GROUP_TIAO || group == CardTypeUtil.GROUP_WAN) {
                set.add(group);
            }
        }
        return 3 - set.size();
    }

    /**
     * 断幺
     *
     * @param cards
     * @param huCardType
     * @return
     */
    private static boolean duanyao(List<String> cards, int tingCardType, HuCardType huCardType) {
        boolean results = true;
        boolean resultc = true;

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

        List<String> list = new ArrayList<>();
        list.addAll(cards);
        aa:
        for (String s : list) {
            if (dy.contains(CardTypeUtil.getTypeByCard(s))) {
                resultc = false;
                break aa;
            }
        }

        b:
        for (Integer s : huCardType.mingGang) {
            if (tempList.contains(s)) {
                results = false;
                break b;
            }
        }
        c:
        for (Integer s : huCardType.anGang) {
            if (tempList.contains(s)) {
                results = false;
                break c;
            }
        }
        d:
        for (Integer s : huCardType.peng) {
            if (tempList.contains(s)) {
                results = false;
                break d;
            }
        }
        e:
        for (Integer s : huCardType.chi) {
            if (s == 6 || s == 15 || s == 24 || s == 0 || s == 9 || s == 18) {
                results = false;
                break e;
            }
        }

        return results && resultc;
    }

    public static void main(String[] args) {
        ArrayList<String> list = new ArrayList();
        list.add("044");
        list.add("045");
        list.add("046");
        list.add("048");
        list.add("052");
        list.add("056");
        list.add("084");
        list.add("085");
        list.add("096");
        list.add("100");
        list.add("104");
        list.add("004");
        list.add("008");
        list.add("012");
        list.add("106");

        HuCardType h = new HuCardType();
        h.specialHuList.add(hu_断幺);
        System.out.print(duanyao(list, -1, h));
    }


    /**
     * 获得所有组
     *
     * @param cards
     * @param huCardType
     * @return
     */
    public static Set<Integer> getAllGroup(List<String> cards, HuCardType huCardType) {
        Set<Integer> result = new HashSet<>();
        for (String card : cards) {
            int group = getCardGroup(card);
            result.add(group);
        }
        for (int chiType : huCardType.chi) {
            int group = CardTypeUtil.getCardGroupByCardType(chiType);
            result.add(group);
        }
        for (int pengType : huCardType.peng) {
            int group = CardTypeUtil.getCardGroupByCardType(pengType);
            result.add(group);
        }
        for (int type : huCardType.anGang) {
            int group = CardTypeUtil.getCardGroupByCardType(type);
            result.add(group);
        }
        for (int type : huCardType.mingGang) {
            int group = CardTypeUtil.getCardGroupByCardType(type);
            result.add(group);
        }

        return result;
    }

    private static boolean is_mengqing(List<String> cards, HuCardType huCardType) {
        return huCardType.chi.size() == 0 && huCardType.peng.size() == 0 && huCardType.mingGang.size() == 0;
    }

    private static boolean is_gujiang(List<String> cards, HuCardType huCardType) {
        int jiangType = huCardType.jiang;
        //七小对 没有将
        if (jiangType == -1) {
            return false;
        }

        int group = CardTypeUtil.getCardGroupByCardType(jiangType);
        if (group != CardTypeUtil.GROUP_WAN && group != CardTypeUtil.GROUP_TONG && group != CardTypeUtil.GROUP_TIAO) {
            return false;
        }
        boolean chiHasGroup = isHasThisGroup(huCardType.chi, group);
        boolean pengHasGroup = isHasThisGroup(huCardType.peng, group);
        boolean anGangHasGroup = isHasThisGroup(huCardType.anGang, group);
        boolean mingGangHasGroup = isHasThisGroup(huCardType.mingGang, group);

        return getGroupNum(cards, group) == 2 && !pengHasGroup && !mingGangHasGroup && !anGangHasGroup && !chiHasGroup;
    }

    private static int getFengNum(List<String> cards, HuCardType huCardType) {
        return huCardType.feng_shun.size();
    }

    private static int getYuanNum(List<String> cards, HuCardType huCardType) {
        return huCardType.zi_shun;
    }

    private static boolean isZiyise(List<String> cards, HuCardType huCardType) {
        List<String> temp = new ArrayList<>();
        temp.addAll(cards);
        Set<Integer> set = new HashSet<>();
        for (int group : getAllGroup(temp, huCardType)) {
            if (!(group == CardTypeUtil.GROUP_ZI || group == CardTypeUtil.GROUP_FENG)) {
                return false;
            }
            set.add(group);
        }

        return set.size() <= 2;

    }

    /**
     * 混一色
     *
     * @param cards
     * @param huCardType
     * @return
     */
    private static boolean isHunyise(List<String> cards, HuCardType huCardType) {
        boolean result = false;
        List<String> list = new ArrayList<>();
        list.addAll(cards);
        for (int i = 0; i < list.size(); i++) {
            list.remove(i);
            Set<Integer> huaSet = new HashSet<>();
            Set<Integer> ziSet = new HashSet<>();
            for (int group : getAllGroup(list, huCardType)) {
                if (group == CardTypeUtil.GROUP_TONG || group == CardTypeUtil.GROUP_TIAO || group == CardTypeUtil.GROUP_WAN) {
                    huaSet.add(group);
                } else {
                    ziSet.add(group);
                }
            }
            if (huaSet.size() == 1 && ziSet.size() > 0) {
                result = true;
            }
            list.clear();
            list.addAll(cards);
        }

        return result;
    }


    private static boolean isPengpenghu(List<String> cards, HuCardType huCardType) {
        if (huCardType.peng.size() + huCardType.anGang.size() + huCardType.mingGang.size() + huCardType.ke.size() == 4) {
            return true;
        }
        return false;
    }

    /**
     * 是否是中张
     * @param cards
     * @param playerCardsInfoMj
     * @param huCardType
     * @return
     */
    private static boolean isHasZhongZhang(List<String> cards, PlayerCardsInfoMj playerCardsInfoMj, HuCardType huCardType) {
        for (String card : cards) {
            if (isYaoJiu(card)) {
                return false;
            }
        }

        for (String card : playerCardsInfoMj.getCards()) {
            if (isYaoJiu(card)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 是否胡1 9
     * @param huCardType
     * @return
     */
    private static boolean isHuYaoJiu(HuCardType huCardType) {
        //将 不是1 9
        if (!isYaoJiu(huCardType.jiang)) {
            return false;
        }
        Set<Integer> ke = new HashSet<>();
        ke.addAll(huCardType.ke);
        ke.addAll(huCardType.peng);
        ke.addAll(huCardType.anGang);
        ke.addAll(huCardType.mingGang);

        //刻不是1 9
        for (int keType : ke) {
            if (!isYaoJiu(keType)) {
                return false;
            }
        }

        //顺没有1 9
        for (int shunType : huCardType.shun) {
            if (shunType != 0 && shunType != 6 && shunType != 9 && shunType != 15 && shunType != 18 && shunType != 24) {
                return false;
            }
        }

        if (huCardType.feng_shun.size() > 0) {
            return false;
        }
        if (huCardType.zi_shun > 0) {
            return false;
        }

        return true;
    }


    public static boolean isHu258(List<String> cards, PlayerCardsInfoMj playerCardsInfoMj){
        Set<String> allCards = new HashSet<>();
        allCards.addAll(cards);
        allCards.addAll(playerCardsInfoMj.getCards());
        for (String card : allCards) {
            int cardType = CardTypeUtil.getTypeByCard(card);
            if (cardType != 1 && cardType != 4 && cardType != 7 &&
                    cardType != 10 && cardType != 13 && cardType != 16 &&
                    cardType != 18 && cardType != 22 && cardType != 25) {
                return false;
            }
        }
        return true;

    }


    private static boolean isYaoJiu(String card) {
        int cardType = CardTypeUtil.getTypeByCard(card);
        return isYaoJiu(cardType);
    }

    public static boolean isYaoJiu(int cardType) {
        for (int yaojiu : HuUtil.yaojiu) {
            if (cardType == yaojiu) {
                return true;
            }
        }
        return false;
    }

    public static boolean isYiSe_wtt(List<String> cards, HuCardType huCardType) {
        Set<Integer> set = new HashSet<>();
        for (int group : getAllGroup(cards, huCardType)) {
            set.add(group);
            if (!(group == CardTypeUtil.GROUP_TIAO || group == CardTypeUtil.GROUP_TONG || group == CardTypeUtil.GROUP_WAN)) {
                return false;
            }
        }
        return set.size() == 1;
    }

    private static boolean isQingyise(List<String> cards, HuCardType huCardType) {
        return isYiSe_wtt(cards, huCardType);
    }

    private static boolean isQinglong(List<String> cards, HuCardType huCardType) {
        return isYitiaolong(cards, huCardType) && isYiSe_wtt(cards, huCardType);
    }

    private static boolean isPiaohu(List<String> cards, HuCardType huCardType) {
        return huCardType.shun.size() == 0 && huCardType.chi.size() == 0 && (huCardType.peng.size() > 0 || huCardType.mingGang.size() > 0 || huCardType.anGang.size() > 0 || huCardType.ke.size() > 0);
    }

    private static boolean isDiaojiang(int tingCardType, HuCardType huCardType) {
        return huCardType.jiang == tingCardType;
    }

    private static int getGroupNum(List<String> cards, int group) {
        int result = 0;
        for (String card : cards) {
            int groupType = getCardGroup(card);
            if (groupType == group) {
                result++;
            }

        }
        return result;
    }


    public static boolean isYitiaolong(List<String> cards, HuCardType huCardType) {
        List<String> temp = new ArrayList<>();
        temp.addAll(cards);
        Map<Integer, Integer> cardMap = PlayerCardsInfoMj.getCardNum(temp);

        List<Integer> list1 = new ArrayList<>();
        list1.addAll(huCardType.shun);
        list1.addAll(huCardType.chi);

        boolean isHasLong1 = list1.containsAll(ytl1);
        boolean isHasLong2 = list1.containsAll(ytl2);
        boolean isHasLong3 = list1.containsAll(ytl3);

        boolean isHas = isHasLong1 || isHasLong2 || isHasLong3;
        return isHas;
    }


    /**
     * 是否是架龙
     *
     * @param cards
     * @param huCardType
     * @return
     */
    public static boolean isYitiaolong_jialong(List<String> cards, HuCardType huCardType) {
        List<String> temp = new ArrayList<>();
        temp.addAll(cards);
        Map<Integer, Integer> cardMap = PlayerCardsInfoMj.getCardNum(temp);

        List<Integer> list1 = new ArrayList<>();
        list1.addAll(huCardType.shun);
        list1.addAll(huCardType.chi);

        boolean isHasLong1 = list1.containsAll(ytl1);
        boolean isHasLong2 = list1.containsAll(ytl2);
        boolean isHasLong3 = list1.containsAll(ytl3);

        int jiangGroup = CardTypeUtil.getCardGroupByCardType(huCardType.jiang);

        return isHasLong1 && jiangGroup == CardTypeUtil.GROUP_WAN ||
                isHasLong2 && jiangGroup == CardTypeUtil.GROUP_TIAO ||
                isHasLong3 && jiangGroup == CardTypeUtil.GROUP_TONG;
    }

    private static boolean isHasThisGroup(Set<Integer> set, int group) {
        for (int s : set) {
            int s_group = CardTypeUtil.getCardGroupByCardType(s);
            if (s_group == group) {
                return true;
            }
        }
        return false;
    }


}
