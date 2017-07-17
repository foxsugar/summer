package com.code.server.game.mahjong.logic;




import com.code.server.game.mahjong.util.FanUtil;
import com.code.server.game.mahjong.util.HuCardType;
import com.code.server.game.mahjong.util.HuLimit;
import com.code.server.game.mahjong.util.HuUtil;

import java.util.*;


public class PlayerCardsInfoSZ extends PlayerCardsInfoMj {

    protected boolean isHasYimenpai = false;//一门牌
    protected boolean isHasShuye = false;//数页

    protected static final int TING_MIN_FAN = 3;
    protected static final int MING_GANG_FAN = 1;
    protected static final int AN_GANG_FAN = 2;

    protected static final int MODE_YIMENPAI = 10;
    protected static final int MODE_SHUYE = 11;
    @Override
    public void init(List<String> cards) {
        super.init(cards);

        specialHuScore.put(hu_缺一门,1);
        specialHuScore.put(hu_缺两门,3);
        specialHuScore.put(hu_孤将,1);
        specialHuScore.put(hu_一张赢,1);
        specialHuScore.put(hu_三风一副,1);
        specialHuScore.put(hu_三风两副,3);
        specialHuScore.put(hu_三风三副,10);
        specialHuScore.put(hu_三元一副,1);
        specialHuScore.put(hu_三元两副,7);
        specialHuScore.put(hu_三元三副,50);

        specialHuScore.put(hu_门清,1);
        specialHuScore.put(hu_清一色,10);
        specialHuScore.put(hu_一条龙,10);
        specialHuScore.put(hu_七小对,10);
        specialHuScore.put(hu_豪华七小对,25);
        specialHuScore.put(hu_双豪七小对_山西,50);
        specialHuScore.put(hu_字一色,50);
        specialHuScore.put(hu_清七对,50);
        specialHuScore.put(hu_清龙,50);


        isHasYimenpai = isHasMode(roomInfo.getMode(),MODE_YIMENPAI);
        isHasShuye = isHasMode(roomInfo.getMode(), MODE_SHUYE);

    }
    public boolean isHasChi(String card){
        return false;
    }

    /**
     * 得到杠的番数
     * @return
     */
    protected int getGangFan(){
        return MING_GANG_FAN*mingGangType.size() + AN_GANG_FAN*anGangType.size();
    }
    protected int getNeedFan(int addFan) {

        int hasFan = getGangFan() + addFan;
//        int hasFan = getGangFan();
        int need = TING_MIN_FAN - hasFan;
        return need < 0 ? 0 : need;
    }

    private boolean isCanHuYimenpai(HuCardType huCardType){
        if(huCardType.specialHuList.contains(hu_缺两门) || huCardType.specialHuList.contains(hu_字一色)){
            return true;
        }
        return false;
    }
    @Override
    public boolean isCanTing(List<String> cards) {
        if (isTing) {
            return false;
        }
        System.out.println("---------------三嘴是否可听--------------start userId : "+userId);

//        boolean isYizhangying = getYiZhangYingSet(getCardsNoChiPengGang(cards), null);
//        System.out.println("是否是一张赢 :  "+isYizhangying);
//        int addFan = isYizhangying?1:0;
//        int fan = getNeedFan(addFan);
//        HuLimit limit = new HuLimit(fan);
        List<String> temp = getCardsNoChiPengGang(cards);
        System.out.println("听的牌 : "+cards);
        System.out.println("听的牌 没有碰杠: "+temp);
//        Set<Integer> set = getTingCardType(getCardsNoChiPengGang(cards), limit);
//        boolean result = set.size()>0;
//        System.out.println("结果: "+result);
//        return set.size() > 0;


        List<String> tempCards = getCardsNoChiPengGang(cards);
        Set<Integer> yzyTingSet = getYiZhangYingSet(tempCards, null);
        System.out.println("=================一张赢的所有类型 : "+yzyTingSet);
        //胡牌类型加上杠
        int needFan = getNeedFan(0);
        List<HuCardType> list = getTingHuCardType(tempCards,null);
        for (HuCardType huCardType : list) {
            if (isHasYimenpai) {
                FanUtil.compute(huCardType.cards, huCardType,huCardType.tingCardType , this);
                if(isCanHuYimenpai(huCardType)){
                    return true;
                }
            } else {
                System.out.println("");
                System.out.println("============= 听的类型: "+huCardType.tingCardType);
                HuCardType.setHuCardType(huCardType, this);
                //听后牌有新添加的
                int fanResult = FanUtil.compute(huCardType.cards, huCardType,huCardType.tingCardType , this);
                //是一张赢
                if (yzyTingSet.contains(huCardType.tingCardType) && huCardType.isCheckYiZhangying) {
                    System.out.println("是一张赢加一番");
                    fanResult++;
                }
                System.out.println("算番的结果== : " + fanResult);
                System.out.println("是否可听: "+(fanResult >= needFan));
                if (fanResult >= needFan) {
                    return true;
                }
            }
        }
        System.out.println("---------------三嘴是否可听--------------end userId : "+userId);
        return false;

    }

    @Override
    public void ting(String card) {
        super.ting(card);
        yiZhangyingSet = getYiZhangYingSet(getCardsNoChiPengGang(cards), null);

    }

    //
//    听的牌 : [119, 109, 117, 021, 017, 093, 000, 111, 002, 108, 027, 003, 102, 110, 076]
//    听的牌 没有碰杠: [119, 117, 021, 017, 093, 027, 102, 076]
//            =================一张赢的所有类型 : [24]
//
//            ============= 听的类型: 24
//    是一张赢加一番
//    算番的结果== : 2
//    是否可听: true


    public Set<Integer> getYiZhangYingSet(List<String> cards, HuLimit limit) {
        //获得没有碰和杠的牌
        List<String> handCards = new ArrayList<>();
        handCards.addAll(cards);

        //是否多一张牌
        int size = handCards.size();
        boolean isMore = (size - 2) % 3 == 0;//去掉将如果能整除说明手牌多一张
        Set<Integer> yzySet = new HashSet<>();
        if (isMore) {//多一张
            //循环去掉一张看能否听
            for (String card : handCards) {
                List<String> tempCards = new ArrayList<>();
                tempCards.addAll(handCards);
                tempCards.remove(card);
                List<Integer> tingList = HuUtil.isTing(tempCards, this, limit);
                if (tingList.size() == 1) {
                    yzySet.addAll(tingList);
                }
            }
        } else {
            List<Integer> yzyList = HuUtil.isTing(handCards, this,limit);
            if (yzyList.size() == 1) {
                yzySet.addAll(yzyList);
            }
        }
        return yzySet;
    }




    @Override
    public boolean isCanPengAddThisCard(String card) {
        //听之后不能碰牌
        if (isTing) {
            return false;
        }
        return super.isCanPengAddThisCard(card);
    }
    @Override
    public boolean isCanGangThisCard(String card) {
        if (isTing && super.isCanGangThisCard(card)) {
            List<String> temp = new ArrayList<>();
            temp.addAll(cards);
            //去掉 这张杠牌
            int cardType = CardTypeUtil.cardType.get(card);

            return isCanTingAfterGang(temp, cardType,false);

        } else return super.isCanGangThisCard(card);

    }

    @Override
    public boolean isCanGangAddThisCard(String card) {
        //听之后 杠后的牌还能听
        if (isTing && super.isCanGangAddThisCard(card)) {
            List<String> temp = getCardsAddThisCard(card);
            //去掉 这张杠牌
            int ct = CardTypeUtil.cardType.get(card);
            return isCanTingAfterGang(temp, ct,true);

        } else return super.isCanGangAddThisCard(card);

    }

    @Override
    public boolean isHasGang() {
        if (isTing) {
            Set<Integer> canGangType = getHasGangList(cards);
            for (int gt : canGangType) {
                List<String> temp = new ArrayList<>();
                temp.addAll(cards);
                if (isCanTingAfterGang(temp, gt,false)) {
                    return true;
                }
            }
            return false;

        }else return super.isHasGang();
    }

    /**
     * 杠之后是否能听
     * @param cards
     * @param cardType
     * @return
     */
    protected boolean isCanTingAfterGang(List<String> cards,int cardType,boolean isDianGang){
        //先删除这次杠的
        removeCardByType(cards,cardType,4);
        boolean isMing = false;
        //去除碰
        for(int pt : pengType.keySet()){//如果杠的是之前碰过的牌
            if (pt != cardType) {
                removeCardByType(cards, pt, 3);
            } else {
                isMing = true;
            }
        }
        //去掉杠的牌
        cards = getCardsNoGang(cards);
        isMing = isMing||isDianGang;
        //加上新的杠的番数
        int addFan = isMing?MING_GANG_FAN:AN_GANG_FAN;
        int needFan = getNeedFan(addFan);


        //胡牌类型加上杠
        List<HuCardType> list = getTingHuCardType(cards,null);
        for (HuCardType huCardType : list) {
            HuCardType.setHuCardType(huCardType, this);
            if (isMing) {
                huCardType.mingGang.add(cardType);
            } else {
                huCardType.anGang.add(cardType);
            }
            if (isHasYimenpai) {
                FanUtil.compute(huCardType.cards, huCardType,huCardType.tingCardType , this);
                if(isCanHuYimenpai(huCardType)){
                    return true;
                }
            } else {
                int fanResult = FanUtil.compute(huCardType.cards, huCardType, huCardType.tingCardType, this);
                if (fanResult >= needFan) {
                    return true;
                }
            }
        }

        return false;
    }



    @Override
    public boolean isCanHu_dianpao(String card) {
        return false;
    }

    @Override
    public boolean isCanHu_zimo(String card) {
        if (!isTing) {
            return false;
        }
        List<String> cs = getCardsNoChiPengGang(cards);
        System.out.println("检测是否可胡自摸= " + cs);
        int needFan = getNeedFan(0);

        return HuUtil.isHu(cs, this, CardTypeUtil.cardType.get(card), new HuLimit(needFan)).size() > 0;

    }


    @Override
    public boolean checkPlayCard(String card) {
        if (isTing) {
            return super.checkPlayCard(card) && card.equals(catchCard);
        } else {
            return super.checkPlayCard(card);
        }
    }


    /**
     * 三元是否是自摸来的
     * @param huCardType
     * @param card
     * @return
     */
    private boolean sanyuanIsZimo(HuCardType huCardType,String card){
        return CardTypeUtil.getCardGroup(card)==CardTypeUtil.GROUP_ZI;
    }

    /**
     * 七小对是否是豪华的
     * @param card
     * @return
     */
    private boolean qxdIsHaohua(String card){
        int cardType = CardTypeUtil.cardType.get(card);
        Map<Integer, Integer> map = getCardNum(cards);
        return map.containsKey(cardType) && map.get(cardType)==4;
    }

    protected Map<Integer,Integer> getNumByGroup(List<String> cards){
        Map<Integer, Integer> result = new HashMap<>();
        result.put(CardTypeUtil.GROUP_WAN, 0);
        result.put(CardTypeUtil.GROUP_TIAO, 0);
        result.put(CardTypeUtil.GROUP_TONG, 0);
        result.put(CardTypeUtil.GROUP_FENG, 0);
        for(String card : cards){
            int group = CardTypeUtil.getCardGroup(card);
            if (group == CardTypeUtil.GROUP_FENG || group == CardTypeUtil.GROUP_ZI) {
                result.put(CardTypeUtil.GROUP_FENG, result.get(CardTypeUtil.GROUP_FENG) + 1);
            } else {
                result.put(group, result.get(group) + 1);
            }
        }
        return result;
    }

    protected int getShuyeFan(List<String> cards){
        Map<Integer, Integer> shuye = getNumByGroup(cards);
        int result = 0;
        for(Integer num : shuye.values()){
            if(num > 7){
                result += num;
            }
        }
        return result;
    }

    @Override
    public void huCompute(RoomInfo room, GameInfo gameInfo, boolean isZimo, long dianpaoUser, String card) {
        System.out.println("===========房间倍数============ "+room.getMultiple());
        int needFan = getNeedFan(0);
        System.out.println("胡的牌 : "+this.cards);
        List<String> cs = getCardsNoChiPengGang(cards);
        List<HuCardType> huList = HuUtil.isHu(cs, this, CardTypeUtil.cardType.get(card), new HuLimit(needFan));
        int maxFan = 1;//基础番
        for (HuCardType huCardType : huList) {
            System.out.println("胡牌拥有的类型: " + huCardType.specialHuList);
            int s = huCardType.fan+1;
            if (huCardType.specialHuList.contains(hu_三元一副) && sanyuanIsZimo(huCardType,card)){//三元一副 自摸加1番
                huCardType.specialHuList.remove(hu_三元一副);
                huCardType.specialHuList.add(hu_三元一副自摸);
                s+=1;
            }
            if (isZimo && huCardType.specialHuList.contains(hu_三元两副) && sanyuanIsZimo(huCardType,card)) {//三元一副 自摸加2番
                huCardType.specialHuList.remove(hu_三元两副);
                huCardType.specialHuList.add(hu_三元两副自摸);
                s+=3;
            }
            if (huCardType.specialHuList.contains(hu_双豪七小对_山西) && !qxdIsHaohua(card)) {//不是双豪华七小对 减去40
                s -= (specialHuScore.get(hu_双豪七小对_山西)-specialHuScore.get(hu_七小对));
                huCardType.specialHuList.remove(hu_双豪七小对_山西);
                huCardType.specialHuList.add(hu_七小对);
            }
            if (huCardType.specialHuList.contains(hu_豪华七小对) && !qxdIsHaohua(card)) {//不是豪华七小对
                s -= (specialHuScore.get(hu_豪华七小对)-specialHuScore.get(hu_七小对));
                huCardType.specialHuList.remove(hu_豪华七小对);
                huCardType.specialHuList.add(hu_七小对);
            }
            huCardType.fan = s;
            if (s >= maxFan) {
                maxFan = s;
            }
        }
        System.out.println("牌型的番数 : "+maxFan);
        //加上杠的番数
        System.out.println("杠加的番数 : "+getGangFan());

        //设置胡牌类型
        setWinTypeResult(getMaxScoreHuCardType(huList));

        //加上杠的番数
        maxFan += getGangFan();


        //数页
        int shuye = getShuyeFan(this.cards);
        System.out.println("页数 = " + shuye);


        //庄家赢
        int subScore = 0;
        if (gameInfo.getFirstTurn() == userId) {
            maxFan +=1;
            maxFan += getYmpFan(shuye);
            maxFan = maxFan>=50?50:maxFan;

            for(PlayerCardsInfoMj playerCardsInfo : gameInfo.getPlayerCardsInfos().values()){
                if (playerCardsInfo.getUserId() != userId) {//三家减分
                    System.out.println("userId : "+playerCardsInfo.getUserId() +" 偏家输变化分数"+maxFan*room.getMultiple());
                    playerCardsInfo.addScore(-maxFan*room.getMultiple());
                    room.setUserSocre(playerCardsInfo.getUserId(),-maxFan*room.getMultiple());
                    subScore += maxFan*room.getMultiple();
                }
            }

        } else {//偏家赢
            maxFan = maxFan>=50?50:maxFan;
            for(PlayerCardsInfoMj playerCardsInfo : gameInfo.getPlayerCardsInfos().values()){
                if(playerCardsInfo.getUserId()!=userId){//输的人
                    if(playerCardsInfo.getUserId() == gameInfo.getFirstTurn()){//庄家输
                        int temp = maxFan + 1;
                        //一门牌 庄家输 多10番
                        temp += getYmpFan(shuye);
                        temp = temp>=50?50:temp;
                        playerCardsInfo.addScore(-temp*room.getMultiple());
                        System.out.println("userId : "+playerCardsInfo.getUserId() +" 庄家输 变化分数"+temp*room.getMultiple());
                        room.setUserSocre(playerCardsInfo.getUserId(),-temp*room.getMultiple());
                        subScore += temp*room.getMultiple();
                    }else{
                        playerCardsInfo.addScore(-maxFan*room.getMultiple());
                        room.setUserSocre(playerCardsInfo.getUserId(),-maxFan*room.getMultiple());
                        subScore += maxFan*room.getMultiple();
                        System.out.println("userId : "+playerCardsInfo.getUserId() +" 偏家输变化分数"+maxFan*room.getMultiple());
                    }
                }
            }
        }
        this.fan = maxFan;
        //赢得人加分
        addScore(subScore);
        System.out.println("赢得分数 : "+subScore);
        room.setUserSocre(userId,subScore);
    }

    protected int getYmpFan(int shuye){
        int result = 0;
        if(isHasYimenpai){
            result += 0;
        }

        if (isHasYimenpai && isHasShuye) {
            result += shuye;
        }
        return result;
    }
private static void change(){
        String s = "104, 091, 130, 088, 077, 078, 058, 002, 070, 049, 047, 081, 090, 068";
    String result = "";
    for (String ss : s.split(",")) {
        result = result+"\""+ss.trim()+"\",";
    }
    System.out.println(result);
}

    public static void main(String[] args) {
        System.out.println(isHasMode("0",MODE_YIMENPAI));
        change();
        PlayerCardsInfoSZ_LQ playerCardsInfo = new PlayerCardsInfoSZ_LQ();
        playerCardsInfo.isHasFengShun = true;

        playerCardsInfo.isHasYimenpai = true;
        playerCardsInfo.isHasShuye = true;
//        String[] s = new String[]{"064","051","097","132","045","067","101","133","092","065","042","124","135"};
//        String[] s = new String[]{"064","051","097","132","045","067","101","133","092","065","134","042","124","135"};
        String[] s = new String[]{"044","045","048",     "049",  "108","109",    "116", "117","120",    "121","128","129",    "036","072"};

//        String[] s = new String[]{"076","077","078",     "080",  "084","085",    "088", "089","090",    "092","093","096",    "100","101"};


        playerCardsInfo.cards = new ArrayList<>();
        playerCardsInfo.init(playerCardsInfo.cards);
        List<String> list = Arrays.asList(s);
        playerCardsInfo.cards.addAll(list);
//        playerCardsInfo.anGangType.add(24);
//        playerCardsInfo.mingGangType.put(0,1);
//        playerCardsInfo.pengType.put(0,1);
//        playerCardsInfo.pengType.put(13,1);
//        playerCardsInfo.isTing = true;
//        playerCardsInfo.pengType.put(0,1);
//        playerCardsInfo.pengType.put(19,1);



//        playerCardsInfo.pengType.put(25,1);

//        playerCardsInfo.isTing = true;
        List<String> temp = new ArrayList<>();
        temp.addAll(playerCardsInfo.cards);
//        playerCardsInfo.isTing = true;
        playerCardsInfo.tingSet = new HashSet<>();
//        playerCardsInfo.tingSet.add(15);
//        System.out.println(playerCardsInfo.isCanTing());
//        System.out.println("==="+playerCardsInfo.getYiZhangYingSet(playerCardsInfo.getCardsNoChiPengGang(temp),null));
//        System.out.println(playerCardsInfo.isCanHu_zimo("088"));
        System.out.println(playerCardsInfo.isCanTing(playerCardsInfo.cards));
//        List<String> cs = playerCardsInfo.getCardsNoChiPengGang(playerCardsInfo.getCards());
//        int type = CardTypeUtil.getTypeByCard("060");
//        HuUtil.isHu(cs, playerCardsInfo, CardTypeUtil.cardType.get("060"), new HuLimit(3));
//        System.out.println(HuUtil.isHu(playerCardsInfo.getCards(), playerCardsInfo, null));
//        System.out.println(HuUtil.isHu(playerCardsInfo.cards,playerCardsInfo,new HuLimit(6)));
//        playerCardsInfo.huCompute();
    }




}
