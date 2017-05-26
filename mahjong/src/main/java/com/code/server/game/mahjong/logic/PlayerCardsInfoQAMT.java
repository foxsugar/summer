package com.code.server.game.mahjong.logic;




import com.code.server.game.mahjong.util.HuCardType;
import com.code.server.game.mahjong.util.HuLimit;

import java.util.*;


public class PlayerCardsInfoQAMT extends PlayerCardsInfoQAKT {



    protected Set<Integer> getTingListNoCPG(List<String> allCards,List<String> cards,boolean isCheckMenqing,boolean isChi,boolean isCanRemoveYao,boolean isGang) {
        Set<Integer> result = new HashSet<>();
        List<HuCardType> list = getTingHuCardType(cards,new HuLimit(0));
        for (HuCardType huCardType : list) {
            //如果听删除的牌是幺 && 手牌只有一张幺
            if (!isCanRemoveYao && huCardType.tingRemoveCard!=null) {
                if (cardIsYao(huCardType.tingRemoveCard)) {
                    continue;
                }
            }

            List<String> temp = new ArrayList<>();
            temp.addAll(allCards);
            if (huCardType.tingRemoveCard != null) {
                temp.remove(huCardType.tingRemoveCard);
            }
            int groupSize = getCardGroup(temp);
            //缺一门
            if (groupSize <= 2) {
                continue;
            }
            if (huCardType.specialHuList.contains(hu_普通七小对)) {//有七小对
                result.add(huCardType.tingCardType);
            }else if(huCardType.specialHuList.contains(hu_飘胡)){
                if (isChi) {
                    continue;
                }
                if (isCheckMenqing && huCardType.specialHuList.contains(hu_门清)) {
                    continue;
                }
                result.add(huCardType.tingCardType);
            } else {
                if ((!huCardType.specialHuList.contains(hu_夹张)&&!huCardType.specialHuList.contains(hu_边张_乾安))) {
                    continue;
                }
                if (isCheckMenqing && huCardType.specialHuList.contains(hu_门清)) {
                    continue;
                }
                //必须有碰等
                if (!isGang && (huCardType.peng.size() == 0 && huCardType.mingGang.size()==0 && huCardType.anGang.size()==0 && huCardType.ke.size()==0)) {
                    continue;
                }

                result.add(huCardType.tingCardType);
            }

        }
        return result;
    }


    @Override
    public boolean isCanChiTing(String card) {
        return false;
    }

    @Override
    public boolean isCanPengTing(String card) {
        return false;
    }

    public static void main(String[] args) {

        PlayerCardsInfoQAMT playerCardsInfo = new PlayerCardsInfoQAMT();
        String[] s = new String[]{"044","045","046",   "000","004","008",    "072","076","080",   "084","092","096","097",   "124"};
        RoomInfo roomInfo = new RoomInfo();
        roomInfo.setMode("63");

        playerCardsInfo.setRoomInfo(roomInfo);
        playerCardsInfo.cards = new ArrayList<>();
        playerCardsInfo.cards.addAll(Arrays.asList(s));

        playerCardsInfo.init(playerCardsInfo.cards);
        List<List<String>> chis=new ArrayList<>();
        List<String> chi = new ArrayList<>();
        chi.add("044");
        chi.add("045");
        chi.add("046");

        List<String> chi1 = new ArrayList<>();
        chi1.add("008");
        chi1.add("012");
        chi1.add("016");

        //List<String> chi2 = new ArrayList<>();
        //chi1.add("024");
        //chi1.add("020");
        //chi1.add("028");

//        chis.add(chi);
        //chis.add(chi1);
        //chis.add(chi2);
//        chis.add(chi1);
//        playerCardsInfo.mingGangType.put(13,1);
//        playerCardsInfo.chiType.add(11);
//        playerCardsInfo.chiType.add(2);
        //playerCardsInfo.chiType.add(5);
//        playerCardsInfo.chiType.add(2);

//        playerCardsInfo.chiCards = chis;
        playerCardsInfo.pengType.put(11,1);
//        playerCardsInfo.pengType.put(31,1);

        System.out.println(playerCardsInfo.isCanTing(playerCardsInfo.cards));
//        System.out.println(playerCardsInfo.isCanChiTing("021"));


    }

}
