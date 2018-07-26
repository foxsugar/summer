package com.code.server.game.mahjong.logic;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by T420 on 2016/11/30.
 */
public class CardTypeUtil {
    public static final int GROUP_WAN = 1;
    public static final int GROUP_TIAO = 2;
    public static final int GROUP_TONG = 3;
    public static final int GROUP_FENG = 4;
    public static final int GROUP_ZI = 5;
    public static Map<String, Integer> cardType = new HashMap<>();
    public static Map<Integer, Integer> cardTingScore = new HashMap<>();

    public static List<String> ALL_CARD = new ArrayList<>();
    public static List<String> WAN_CARD = new ArrayList<>();
    public static List<String> TONG_CARD = new ArrayList<>();
    public static List<String> TIAO_CARD = new ArrayList<>();
    public static List<String> FENG_CARD = new ArrayList<>();
    public static List<String> ZI_CARD = new ArrayList<>();


    static {
        init_card_type();
        init_wan_card();
        init_tong_card();
        init_tiao_card();
        init_feng_card();
        init_zi_card();
        init_ting_score();

        //最后
        init_all_cards();
    }

    public static int getCardTingScore(String card) {
        if (cardType.containsKey(card)) {
            int type = cardType.get(card);
            if (cardTingScore.containsKey(type)) {
                return cardTingScore.get(type);
            }
        }
        return 0;
    }

    public static int getTypeByCard(String card) {
        return cardType.get(card);
    }

    /**
     * 通过card得到牌的组
     * @param card
     * @return
     */
    public static int getCardGroup(String card) {
        int type = cardType.get(card);
        return getCardGroupByCardType(type);
    }

    public static int getCardGroupByCardType(int type){
        if (type >= 0 && type <= 8) {
            return GROUP_WAN;
        }
        if (type >= 9 && type <= 17) {
            return GROUP_TIAO;
        }
        if (type >= 18 && type <= 26) {
            return GROUP_TONG;
        }
        if (type >= 27 && type <= 30) {
            return GROUP_FENG;
        }
        if (type >= 31 && type <= 33) {
            return GROUP_ZI;
        }
        return 0;
    }

    public static boolean isFeng(String card) {
        int type = getCardGroup(card);
        return type == GROUP_FENG || type == GROUP_ZI;
    }
    private static void init_all_cards() {
        ALL_CARD.addAll(WAN_CARD);
        ALL_CARD.addAll(TONG_CARD);
        ALL_CARD.addAll(TIAO_CARD);
        ALL_CARD.addAll(FENG_CARD);
        ALL_CARD.addAll(ZI_CARD);

    }

    private static void init_wan_card(){
        WAN_CARD.add("000");
        WAN_CARD.add("001");
        WAN_CARD.add("002");
        WAN_CARD.add("003");
        WAN_CARD.add("004");
        WAN_CARD.add("005");
        WAN_CARD.add("006");
        WAN_CARD.add("007");
        WAN_CARD.add("008");
        WAN_CARD.add("009");
        WAN_CARD.add("010");
        WAN_CARD.add("011");
        WAN_CARD.add("012");
        WAN_CARD.add("013");
        WAN_CARD.add("014");
        WAN_CARD.add("015");
        WAN_CARD.add("016");
        WAN_CARD.add("017");
        WAN_CARD.add("018");
        WAN_CARD.add("019");
        WAN_CARD.add("020");
        WAN_CARD.add("021");
        WAN_CARD.add("022");
        WAN_CARD.add("023");
        WAN_CARD.add("024");
        WAN_CARD.add("025");
        WAN_CARD.add("026");
        WAN_CARD.add("027");
        WAN_CARD.add("028");
        WAN_CARD.add("029");
        WAN_CARD.add("030");
        WAN_CARD.add("031");
        WAN_CARD.add("032");
        WAN_CARD.add("033");
        WAN_CARD.add("034");
        WAN_CARD.add("035");
    }

    private static void init_tong_card(){
        TIAO_CARD.add("036");
        TIAO_CARD.add("037");
        TIAO_CARD.add("038");
        TIAO_CARD.add("039");
        TIAO_CARD.add("040");
        TIAO_CARD.add("041");
        TIAO_CARD.add("042");
        TIAO_CARD.add("043");
        TIAO_CARD.add("044");
        TIAO_CARD.add("045");
        TIAO_CARD.add("046");
        TIAO_CARD.add("047");
        TIAO_CARD.add("048");
        TIAO_CARD.add("049");
        TIAO_CARD.add("050");
        TIAO_CARD.add("051");
        TIAO_CARD.add("052");
        TIAO_CARD.add("053");
        TIAO_CARD.add("054");
        TIAO_CARD.add("055");
        TIAO_CARD.add("056");
        TIAO_CARD.add("057");
        TIAO_CARD.add("058");
        TIAO_CARD.add("059");
        TIAO_CARD.add("060");
        TIAO_CARD.add("061");
        TIAO_CARD.add("062");
        TIAO_CARD.add("063");
        TIAO_CARD.add("064");
        TIAO_CARD.add("065");
        TIAO_CARD.add("066");
        TIAO_CARD.add("067");
        TIAO_CARD.add("068");
        TIAO_CARD.add("069");
        TIAO_CARD.add("070");
        TIAO_CARD.add("071");
    }

    private static void init_tiao_card(){
        TONG_CARD.add("072");
        TONG_CARD.add("073");
        TONG_CARD.add("074");
        TONG_CARD.add("075");
        TONG_CARD.add("076");
        TONG_CARD.add("077");
        TONG_CARD.add("078");
        TONG_CARD.add("079");
        TONG_CARD.add("080");
        TONG_CARD.add("081");
        TONG_CARD.add("082");
        TONG_CARD.add("083");
        TONG_CARD.add("084");
        TONG_CARD.add("085");
        TONG_CARD.add("086");
        TONG_CARD.add("087");
        TONG_CARD.add("088");
        TONG_CARD.add("089");
        TONG_CARD.add("090");
        TONG_CARD.add("091");
        TONG_CARD.add("092");
        TONG_CARD.add("093");
        TONG_CARD.add("094");
        TONG_CARD.add("095");
        TONG_CARD.add("096");
        TONG_CARD.add("097");
        TONG_CARD.add("098");
        TONG_CARD.add("099");
        TONG_CARD.add("100");
        TONG_CARD.add("101");
        TONG_CARD.add("102");
        TONG_CARD.add("103");
        TONG_CARD.add("104");
        TONG_CARD.add("105");
        TONG_CARD.add("106");
        TONG_CARD.add("107");
    }

    private static void init_feng_card() {
        FENG_CARD.add("108");//东
        FENG_CARD.add("109");
        FENG_CARD.add("110");
        FENG_CARD.add("111");
        FENG_CARD.add("112");
        FENG_CARD.add("113");
        FENG_CARD.add("114");
        FENG_CARD.add("115");
        FENG_CARD.add("116");
        FENG_CARD.add("117");
        FENG_CARD.add("118");
        FENG_CARD.add("119");
        FENG_CARD.add("120");
        FENG_CARD.add("121");
        FENG_CARD.add("122");
        FENG_CARD.add("123");

    }

    private static void init_zi_card(){
        ZI_CARD.add("124");
        ZI_CARD.add("125");
        ZI_CARD.add("126");
        ZI_CARD.add("127");
        ZI_CARD.add("128");
        ZI_CARD.add("129");
        ZI_CARD.add("130");
        ZI_CARD.add("131");
        ZI_CARD.add("132");
        ZI_CARD.add("133");
        ZI_CARD.add("134");
        ZI_CARD.add("135");
    }

    private static void init_card_type() {
        cardType.put("000", 0);
        cardType.put("001", 0);
        cardType.put("002", 0);
        cardType.put("003", 0);
        cardType.put("004", 1);
        cardType.put("005", 1);
        cardType.put("006", 1);
        cardType.put("007", 1);
        cardType.put("008", 2);
        cardType.put("009", 2);
        cardType.put("010", 2);
        cardType.put("011", 2);
        cardType.put("012", 3);
        cardType.put("013", 3);
        cardType.put("014", 3);
        cardType.put("015", 3);
        cardType.put("016", 4);
        cardType.put("017", 4);
        cardType.put("018", 4);
        cardType.put("019", 4);
        cardType.put("020", 5);
        cardType.put("021", 5);
        cardType.put("022", 5);
        cardType.put("023", 5);
        cardType.put("024", 6);
        cardType.put("025", 6);
        cardType.put("026", 6);
        cardType.put("027", 6);
        cardType.put("028", 7);
        cardType.put("029", 7);
        cardType.put("030", 7);
        cardType.put("031", 7);
        cardType.put("032", 8);
        cardType.put("033", 8);
        cardType.put("034", 8);
        cardType.put("035", 8);
        cardType.put("036", 9);
        cardType.put("037", 9);
        cardType.put("038", 9);
        cardType.put("039", 9);
        cardType.put("040", 10);
        cardType.put("041", 10);
        cardType.put("042", 10);
        cardType.put("043", 10);
        cardType.put("044", 11);
        cardType.put("045", 11);
        cardType.put("046", 11);
        cardType.put("047", 11);
        cardType.put("048", 12);
        cardType.put("049", 12);
        cardType.put("050", 12);
        cardType.put("051", 12);
        cardType.put("052", 13);
        cardType.put("053", 13);
        cardType.put("054", 13);
        cardType.put("055", 13);
        cardType.put("056", 14);
        cardType.put("057", 14);
        cardType.put("058", 14);
        cardType.put("059", 14);
        cardType.put("060", 15);
        cardType.put("061", 15);
        cardType.put("062", 15);
        cardType.put("063", 15);
        cardType.put("064", 16);
        cardType.put("065", 16);
        cardType.put("066", 16);
        cardType.put("067", 16);
        cardType.put("068", 17);
        cardType.put("069", 17);
        cardType.put("070", 17);
        cardType.put("071", 17);
        cardType.put("072", 18);
        cardType.put("073", 18);
        cardType.put("074", 18);
        cardType.put("075", 18);
        cardType.put("076", 19);
        cardType.put("077", 19);
        cardType.put("078", 19);
        cardType.put("079", 19);
        cardType.put("080", 20);
        cardType.put("081", 20);
        cardType.put("082", 20);
        cardType.put("083", 20);
        cardType.put("084", 21);
        cardType.put("085", 21);
        cardType.put("086", 21);
        cardType.put("087", 21);
        cardType.put("088", 22);
        cardType.put("089", 22);
        cardType.put("090", 22);
        cardType.put("091", 22);
        cardType.put("092", 23);
        cardType.put("093", 23);
        cardType.put("094", 23);
        cardType.put("095", 23);
        cardType.put("096", 24);
        cardType.put("097", 24);
        cardType.put("098", 24);
        cardType.put("099", 24);
        cardType.put("100", 25);
        cardType.put("101", 25);
        cardType.put("102", 25);
        cardType.put("103", 25);
        cardType.put("104", 26);
        cardType.put("105", 26);
        cardType.put("106", 26);
        cardType.put("107", 26);
        cardType.put("108", 27);
        cardType.put("109", 27);
        cardType.put("110", 27);
        cardType.put("111", 27);
        cardType.put("112", 28);
        cardType.put("113", 28);
        cardType.put("114", 28);
        cardType.put("115", 28);
        cardType.put("116", 29);
        cardType.put("117", 29);
        cardType.put("118", 29);
        cardType.put("119", 29);
        cardType.put("120", 30);
        cardType.put("121", 30);
        cardType.put("122", 30);
        cardType.put("123", 30);
        cardType.put("124", 31);
        cardType.put("125", 31);
        cardType.put("126", 31);
        cardType.put("127", 31);
        cardType.put("128", 32);
        cardType.put("129", 32);
        cardType.put("130", 32);
        cardType.put("131", 32);
        cardType.put("132", 33);
        cardType.put("133", 33);
        cardType.put("134", 33);
        cardType.put("135", 33);
    }

    /**
     * 牌面分数
     */
    private static void init_ting_score(){
        cardTingScore.put(0, 1);
        cardTingScore.put(1, 2);
        cardTingScore.put(2, 3);
        cardTingScore.put(3, 4);
        cardTingScore.put(4, 5);
        cardTingScore.put(5, 6);
        cardTingScore.put(6, 7);
        cardTingScore.put(7, 8);
        cardTingScore.put(8, 9);
        cardTingScore.put(9, 1);
        cardTingScore.put(10, 2);
        cardTingScore.put(11, 3);
        cardTingScore.put(12, 4);
        cardTingScore.put(13, 5);
        cardTingScore.put(14, 6);
        cardTingScore.put(15, 7);
        cardTingScore.put(16, 8);
        cardTingScore.put(17, 9);
        cardTingScore.put(18, 1);
        cardTingScore.put(19, 2);
        cardTingScore.put(20, 3);
        cardTingScore.put(21, 4);
        cardTingScore.put(22, 5);
        cardTingScore.put(23, 6);
        cardTingScore.put(24, 7);
        cardTingScore.put(25, 8);
        cardTingScore.put(26, 9);
        cardTingScore.put(27, 10);
        cardTingScore.put(28, 10);
        cardTingScore.put(29, 10);
        cardTingScore.put(30, 10);
        cardTingScore.put(31, 10);
        cardTingScore.put(32, 10);
        cardTingScore.put(33, 10);
    }


    private static void test() {
        String[] zi = new String[]{"东", "南", "西", "北", "中", "发", "白"};
        for (int i = 0; i < 34; i++) {
            int type = i / 9;
            int index = i % 9 + 1;
            if (type == 0) {
                System.out.println(i + ": " + index + "万");
            }
            if (type == 1) {
                System.out.println(i + ": " + index + "条");
            }
            if (type == 2) {
                System.out.println(i + ": " + index + "桶");
            }
            if (type == 3) {
                System.out.println(i + ": " + zi[index - 1]);
            }
        }

    }

    public static String getCardStrByType(int type) {
        int result = type * 4;
        if (result >=0 && result<10) {
            return "00"+result;
        }
        else if (result >= 10 && result < 100) {
            return "0"+result;
        }
        else if (result >= 100) {
            return ""+result;
        } else {
            return ""+result;
        }
    }

    /**
     * 生成ting的分数
     */
    private static void cardTingScore() {
        for (int i = 0; i < 34; i++) {
            int score = i%9 + 1;
            //字是10分
            if (i >= 27) {
                score = 10;
            }
            System.out.println(MessageFormat.format("cardTingScore.put({0}, {1});",i,score));
        }
    }


    private static void gen_str() {
        for (int i = 0; i < 136; i++) {
            String key = "00" + i;
            if (i < 10) {
                key = "00" + i;
            } else if (i >= 10 && i < 100) {
                key = "0" + i;
            } else if (i >= 100) {
                key = "" + i;
            }
            int value = i / 4;

//            System.out.println(MessageFormat.format("huType.put(\"{0}\",{1});", key, value));
            System.out.println(MessageFormat.format("ALL_CARD.add(\"{0}\");", key));
        }
    }

    public static void main(String[] args) {
        boolean isTing = true;


        System.out.println(isTing && 12==getTypeByCard("045"));


//        cardTingScore();
//        gen_str();
    }
}
