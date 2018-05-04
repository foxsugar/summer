package com.code.server.game.poker.pullmice;

import scala.Int;
import sun.swing.StringUIClientPropertyKey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dajuejinxian on 2018/4/7.
 */
public class BaseCardUtils {

    private static Map<Integer, Integer> cardDict = new HashMap<>();

    public static String[] colors = {"♠", "♥", "♣", "♦"};

    public static String[] values = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "小王", "大王"};

    public static Map<Integer, Integer> getCardDict() {
        return cardDict;
    }

    public  static Integer local2Client(Integer card, IfCard ifCard){

        if (ifCard == null){

            ifCard= new IfCard() {
                @Override
                public Map<Integer, Integer> cardDict() {
                    return BaseCardUtils.getCardDict();
                }
            };
        }

        Map<Integer, Integer> dict = ifCard.cardDict();

        return dict.get(card);

    }

    public static Integer client2Local(Integer card, IfCard ifCard){

        if (ifCard == null){

            ifCard= new IfCard() {
                @Override
                public Map<Integer, Integer> cardDict() {
                    return cardDict;
                }
            };
        }

        Map<Integer, Integer> dict = ifCard.cardDict();

        Integer key = null;
        for (Map.Entry<Integer, Integer> entry : dict.entrySet()){

            if (entry.getValue() == card){
                key = entry.getKey();
                break;
            }
        }

        return key;
    }

    public static String client2String(Integer card){
        int chushu = (card - 1) / 4;
        int yushu = (card - 1) % 4;
        if (card == 53){
            return values[13];
        }else if(card == 54){
            return values[14];
        }
        return colors[yushu] + "-" + values[chushu];
    }

    public static Integer string2Client(String str){

        if (str.equals("大王")){
            return 54;
        }

        if (str.equals("小王")){
            return 53;
        }

        String[] sourceStrArray  = str.split("-");
        int idx = 0;
        for (int i = 0; i < colors.length; i++){
            if (colors[i].equals(sourceStrArray[0])){
                idx = i;
                break;
            }
        }

        int yushu = idx;

        idx = 0;
        for (int i = 0; i < values.length; i++){
            if( values[i].equals(sourceStrArray[1])){
                idx = i;
                break;
            }
        }

        int chushu = idx;

        int ret = chushu * 4 + yushu;

        return ret + 1;
    }

    public static Integer string2Local(String str, IfCard card){

        if (card == null){

            card = new IfCard() {
                @Override
                public Map<Integer, Integer> cardDict() {
                    return cardDict;
                }
            };
        }

        Integer client = string2Client(str);

        return client2Local(client, card);
    }

    public static String local2String(Integer local, IfCard card){

        if (card == null){

            card = new IfCard() {
                @Override
                public Map<Integer, Integer> cardDict() {
                    return cardDict;
                }
            };
        }

        int client = local2Client(local, card);
        String str = client2String(client);

        return str;
    }

    public static List<String> localsToString(List<Integer> list, IfCard card){
        List<String> aList = new ArrayList<>();
        for (Integer i : list){
           String str = local2String(i, card);
           aList.add(str);
        }
        return aList;
    }

    public static List<Integer> strings2Local(List<String> list, IfCard card){
        List<Integer> aList = new ArrayList<>();
        for (String i : list){
            Integer vI = string2Local(i, card);
            aList.add(vI);
        }
        return aList;
    }


}
