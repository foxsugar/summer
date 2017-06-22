package com.code.server.game.mahjong.util;



import com.code.server.game.mahjong.logic.CardTypeUtil;
import com.code.server.game.mahjong.logic.PlayerCardsInfoMj;
import org.apache.commons.collections4.CollectionUtils;

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
     * @param list
     * @param isHasFengShun
     * @return
     */
    private static List<HuCardType> isHuCommon(List<String> list, boolean... isHasFengShun) {
        return agari(analyse(convert(list)), isHasFengShun);
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

            if (playerCardsInfo.isHasSpecialHu(hu_豪华七小对) && isHaoHuaQixiaodui(cards, playerCardsInfo,1)) {
                huList.add(HuCardType.getSpecialHuInstance(hu_豪华七小对).setFan(playerCardsInfo.getSpecialHuScore(hu_豪华七小对)));
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
            if (playerCardsInfo.isHasSpecialHu(hu_双豪七小对) && isHaoHuaQixiaodui(cards, playerCardsInfo,2)) {
                huList.add(HuCardType.getSpecialHuInstance(hu_双豪七小对).setFan(playerCardsInfo.getSpecialHuScore(hu_双豪七小对)));
            }
            if (playerCardsInfo.isHasSpecialHu(hu_三豪七小对) && isHaoHuaQixiaodui(cards, playerCardsInfo,3)) {
                huList.add(HuCardType.getSpecialHuInstance(hu_三豪七小对).setFan(playerCardsInfo.getSpecialHuScore(hu_三豪七小对)));
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
     *
     * @param cards
     * @param playerCardsInfo
     * @param limit
     * @param removeCard 听删掉的牌
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
     * 能听的牌
     *
     * @param list
     * @param exclude
     * @param isHasFengShun
     * @return
     */
    private static List<Integer> isTing(List<String> list, List<Integer> exclude, boolean... isHasFengShun) {
        List<Integer> ting = new ArrayList<>();
        for (int i = 0; i < n_zero.length; i++) {
            //不包含
            if (!exclude.contains(i)) {
                //加入数组
                int[] a = convert(list);
                int[] b = Arrays.copyOf(a, a.length + 1);
                b[a.length] = i;
                if (agari(analyse(b), isHasFengShun).size() > 0) {
                    ting.add(i);
                }
            }
        }
        return ting;
    }


    //胡牌
    static List<HuCardType> agari(int[] n, boolean... isHasFengShun) {
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
                                    int one = 9 * a + b;
                                    int two = 9 * a + b + 1;
                                    int three = 9 * a + b + 2;
                                    t[one]--;
                                    t[two]--;
                                    t[three]--;
                                    shun.add(9 * a + b);
                                } else {
                                    b++;
                                }
                            }
                        }
                        if (isHasFengShun.length > 0 && isHasFengShun[0]) {

                            //处理风
                            disposeFENG(t, feng);
                            //处理字
                            zi_num = disposeZFB(t);
                        }
                    } else {//先找顺
                        if (isHasFengShun.length > 0 && isHasFengShun[0]) {
                            //处理风
                            disposeFENG(t, feng);
                            //处理字
                            zi_num = disposeZFB(t);
                        }

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

                        add(ret, huCardType);
                    }
                }
            }
        }
        return ret;
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
        for(int i=27;i<31;i++) {
            for(int j=0;j<t[i];j++) {
                fengList.add(i);
            }
        }
        int size = fengList.size();
        if (size == 0 || size % 3 != 0) {
            return;
        }
        int disposeNum = size/3;
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
        for(List<Integer> l : list){
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
        return isYiSe_wtt(cards) && (isQixiaodui(cards, playerCardsInfo)||isHaoHuaQixiaodui(cards,playerCardsInfo,1)||isShuangHaoQixiaodui(cards,playerCardsInfo));
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
        int size = playerCardsInfo.getMingGangType().size() + playerCardsInfo.getAnGangType().size() + playerCardsInfo.getPengType().size()+playerCardsInfo.getChiType().size();;
        if (size > 0) {
            return false;
        }
        Map<Integer, Integer> cardMap = PlayerCardsInfoMj.getCardNum(temp);
        if (cardMap.size() > 7) {
            return false;
        }
        for (Integer num : cardMap.values()) {
            if (num != 2 && num!=4) {
                return false;
            }
        }
        return true;

    }

    public static boolean isQixiaodui(List<String> cards, PlayerCardsInfoMj playerCardsInfo) {
        List<String> temp = new ArrayList<>();
        temp.addAll(cards);
        int size = playerCardsInfo.getMingGangType().size() + playerCardsInfo.getAnGangType().size() + playerCardsInfo.getPengType().size() +playerCardsInfo.getChiType().size();
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
        int size = playerCardsInfo.getMingGangType().size() + playerCardsInfo.getAnGangType().size() + playerCardsInfo.getPengType().size() +playerCardsInfo.getChiType().size();
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
        return isHas4Num==haohuaNum;

    }

    public static boolean isShuangHaoQixiaodui(List<String> cards, PlayerCardsInfoMj playerCardsInfo) {
        List<String> temp = new ArrayList<>();
        temp.addAll(cards);

        int size = playerCardsInfo.getMingGangType().size() + playerCardsInfo.getAnGangType().size() + playerCardsInfo.getPengType().size() +playerCardsInfo.getChiType().size();
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

    public static boolean isYitiaolong(List<String> cards, PlayerCardsInfoMj playerCardsInfo) {
        List<String> temp = new ArrayList<>();
        temp.addAll(cards);
        Map<Integer, Integer> cardMap = PlayerCardsInfoMj.getCardNum(temp);

        boolean isHas = false;
        int ytlType = 0;
        for (int i = 0; i < 3; i++) {
            boolean isLx = true;

            for (int j = 0; j < 9; j++) {
                if (!cardMap.containsKey(i * 9 + j)) {
                    isLx = false;
                    break;
                }
            }
            if (isLx) {
                isHas = true;
                ytlType = i;
            }
        }

        if (isHas) {
            for (int i = 0; i < 9; i++) {
                int type = ytlType * 9 + i;
                playerCardsInfo.removeCardByType(temp, type, 1);
            }
            return isHuCommon(temp).size() > 0;
        } else {
            return false;
        }
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
        ret = agari(n,true);
        System.out.println(ret);
//        }
//        System.out.println("耗时 = "+(System.currentTimeMillis() - time));

        for (HuCardType r : ret) {

            System.out.println(r);
            System.out.println();
        }


    }

    private static void test_equil() {
        List list = new ArrayList();
        List list1 = new ArrayList();
        System.out.println(CollectionUtils.isEqualCollection(list, list1));
    }

    private static void test_yitiaolong() {

        String[] t = new String[]{"112", "123", "110", "062", "050", "126", "044", "121", "108", "067", "056", "125", "119", "024"};
        List<String> list = Arrays.asList(t);
        list.addAll(Arrays.asList(t));
        isHuCommon(list, true);

    }
    
    /**
     * 测试是否0和的数据
    * @Title: getUserAndScore
    * @Creater: Clark  
    * @Description: 
    * @param @param map
    * @param @return    设定文件
    * @return String    返回类型
    * @throws
     */
    public static String getUserAndScore(Map<Integer,Integer> map){
    	StringBuffer sb = new StringBuffer();
    	int temp = 0;
    	for (Integer i : map.keySet()) {
			sb.append("UserId:"+i+"得分为："+map.get(i));
			sb.append(";");
			temp+=map.get(i);
		}
    	sb.append("是不是0和："+ (temp==0?"是":"否"));
    	return sb.toString();
    }

//    static final int dong = 27;
//    static final int nan = 28;
//    static final int xi = 29;
//    static final int bei = 30;
//    static final int zhong = 31;
//    static final int fa = 32;
//    static final int bai = 33;
}
