package com.code.server.game.mahjong.logic;

import com.code.server.game.mahjong.util.HuCardType;
import com.code.server.game.mahjong.util.HuLimit;
import com.code.server.game.mahjong.util.HuUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by dajuejinxian on 2018/7/16.
 */
public class PlayerCardsInfoGSJ_New extends PlayerCardsInfoDonghu{

    //验证牌花色是否大于8张
    private static final int CHECK_CARD_POINT = 7;
    //第0位是不是1
    public static final int mode_硬八张 = 0;
    @Override
    public void init(List<String> cards) {

        this.cards = cards;
        isHasTing = true;

        specialHuScore.put(hu_清一色,15);
        specialHuScore.put(hu_一条龙,15);
        specialHuScore.put(hu_七小对,15);
        specialHuScore.put(hu_豪华七小对,30);
        specialHuScore.put(hu_双豪七小对,30);
        specialHuScore.put(hu_十三幺,30);
        specialHuScore.put(hu_清龙,30);
        //坎胡
        specialHuScore.put(hu_边张,10);
        specialHuScore.put(hu_夹张,10);
    }

    //不带吃
    public boolean isHasChi(String card){
        return false;
    }

    //剩余8墩牌时，无人胡牌即荒庄，荒庄不算杠分
    public boolean isHuangzhuang(GameInfo gameInfo) {
        return gameInfo.getRemainCards().size() <= 16;
    }

    @Override
    public boolean isCanHu_dianpao(String card) {

        if (!isTing) return false;

        List<String> tempList = new ArrayList<>();
        tempList.addAll(cards);
        tempList.add(card);
        List<String> temp = getCardsAddThisCard(card);
        List<String> noPengAndGang = getCardsNoChiPengGang(temp);
        System.out.println("检测是否可胡点炮= " + noPengAndGang);
        return HuUtil.isHu(noPengAndGang, this, CardTypeUtil.cardType.get(card), null).size()>0 && checkCard(tempList);
    }

    /**
     * 是否可胡 自摸
     * @param card
     * @return
     */
    @Override
    public boolean isCanHu_zimo(String card) {

        if (!isTing) return false;

        List<String> cs = getCardsNoChiPengGang(cards);
        System.out.println("检测是否可胡自摸= " + cs );
        return HuUtil.isHu(cs, this, CardTypeUtil.cardType.get(card), null).size()>0 && checkCard(cards);

    }

    public boolean checkCard(List<String> cards){

        boolean isHas = this.roomInfo.isHasMode(mode_硬八张);
        if (!isHas) return true;

        int wanNum = 0;
        int tiaoNum = 0;
        int tongNum = 0;
        for (String string : cards) {
            if(CardTypeUtil.getCardGroup(string)==CardTypeUtil.GROUP_WAN){
                wanNum++;
            }else if(CardTypeUtil.getCardGroup(string)==CardTypeUtil.GROUP_TONG){
                tiaoNum++;
            }else if(CardTypeUtil.getCardGroup(string)==CardTypeUtil.GROUP_TIAO){
                tongNum++;
            }
        }
        return wanNum > CHECK_CARD_POINT || tiaoNum > CHECK_CARD_POINT || tongNum > CHECK_CARD_POINT;
    }

    @Override
    public boolean isCanTing(List<String> cards) {
        if (isTing) {
            return false;
        }
        List<HuCardType> list = getTingHuCardType(getCardsNoChiPengGang(cards), new HuLimit(0));

        boolean isHas = this.roomInfo.isHasMode(mode_硬八张);

        if (isHas){

            List<String> noPengGang = getCardsNoChiPengGang(cards);

            long wanCount = noPengGang.stream().filter(x ->{
                return (Integer.valueOf(x) / 4 <= 8);
                 }).count();

            long tongCount = noPengGang.stream().filter(x ->{
                return (Integer.valueOf(x) / 4 >= 18) && (Integer.valueOf(x) / 4 <= 26);
            }).count();

            long tiaoCount = noPengGang.stream().filter(x ->{
                return (Integer.valueOf(x) / 4 >= 9) && (Integer.valueOf(x) / 4 <= 17);
            }).count();

            for (Map.Entry<Integer,Long> entry : getPengType().entrySet()){
                if (CardTypeUtil.getCardGroupByCardType(entry.getKey()) == CardTypeUtil.GROUP_WAN){
                    wanCount += 3;
                }
                if (CardTypeUtil.getCardGroupByCardType(entry.getKey()) == CardTypeUtil.GROUP_TIAO){
                    tiaoCount += 3;
                }
                if (CardTypeUtil.getCardGroupByCardType(entry.getKey()) == CardTypeUtil.GROUP_TONG){
                    tongCount += 3;
                }
            }

            for (Map.Entry<Integer,Long> entry : getMingGangType().entrySet()){
                if (CardTypeUtil.getCardGroupByCardType(entry.getKey()) == CardTypeUtil.GROUP_WAN){
                    wanCount += 3;
                }
                if (CardTypeUtil.getCardGroupByCardType(entry.getKey()) == CardTypeUtil.GROUP_TIAO){
                    tiaoCount += 3;
                }
                if (CardTypeUtil.getCardGroupByCardType(entry.getKey()) == CardTypeUtil.GROUP_TONG){
                    tongCount += 3;
                }
            }

            for (Integer i : getAnGangType()){
                if (CardTypeUtil.getCardGroupByCardType(i) == CardTypeUtil.GROUP_WAN){
                    wanCount += 3;
                }
                if (CardTypeUtil.getCardGroupByCardType(i) == CardTypeUtil.GROUP_TIAO){
                    tiaoCount += 3;
                }
                if (CardTypeUtil.getCardGroupByCardType(i) == CardTypeUtil.GROUP_TONG){
                    tongCount += 3;
                }
            }

            List<HuCardType> huList = getTingHuCardType(getCardsNoChiPengGang(cards), null);

            for (HuCardType huCardType : huList) {

                long wanCountTemp = wanCount;
                long tiaoCountTemp = tiaoCount;
                long tongCountTemp = tongCount;

                if (huCardType.tingRemoveCard == null){
                    if (wanCountTemp >= 7 || tiaoCountTemp >= 7 || tongCountTemp >= 7){
                        return true;
                    }
                }else {
                    int removeGroup = CardTypeUtil.getCardGroup(huCardType.tingRemoveCard);
//                int rs = CardTypeUtil.getCardGroupByCardType(removeGroup / 4);
                    switch (removeGroup){
                        case CardTypeUtil.GROUP_WAN:
                            wanCountTemp--;
                            break;
                        case CardTypeUtil.GROUP_TIAO:
                            tiaoCountTemp--;
                            break;
                        case CardTypeUtil.GROUP_TONG:
                            tongCountTemp--;
                            break;
                    }
                    if (wanCountTemp >= 7 || tiaoCountTemp >= 7 || tongCountTemp >= 7){
                        return true;
                    }
                }
            }

            return false;

        }else {

            return  getTingHuCardType(getCardsNoChiPengGang(cards), null).size() > 0;
        }




    }

    @Override
    protected boolean isCanTingAfterGang(List<String> cards, int cardType) {
        // 先删除这次杠的
        removeCardByType(cards, cardType, 4);
        for (int pt : pengType.keySet()) {// 如果杠的是之前碰过的牌
            //去掉碰
            if (pt != cardType) {
                removeCardByType(cards, pt, 3);
            }
        }
        // 去掉杠的牌
        cards = getCardsNoGang(cards);
        List<HuCardType> list = getTingHuCardType(cards, null);
//        return list.stream().filter(hct -> hct.specialHuList.contains(hu_缺一门) || hct.specialHuList.contains(hu_缺两门)).count() > 0;
        return list.size() > 0;
    }

    @Override
    public void huCompute(RoomInfo room, GameInfo gameInfo, boolean isZimo, long dianpaoUser, String card) {

        int gangScoreTemp = 5;
        int anGangScoreTemp = 10;

        //this的杠分
        int gangScore = 0;
        //算因为明杠造成的分
        for (Map.Entry<Integer,Long> entry : mingGangType.entrySet()){
            //有人点杠
            if (entry.getValue() != -1){
                PlayerCardsInfoMj playerCardsInfo = this.gameInfo.playerCardsInfos.get(entry.getValue());
                playerCardsInfo.addGangScore(-gangScoreTemp);
                playerCardsInfo.addScore(-gangScoreTemp);
                gangScore += gangScoreTemp;
                this.roomInfo.addUserSocre(playerCardsInfo.getUserId(), -gangScoreTemp);

            }else {
                //自摸明杠
                for (PlayerCardsInfoMj playerCardsInfo : this.gameInfo.playerCardsInfos.values()){
                    if (playerCardsInfo.getUserId() == this.userId) continue;
                    playerCardsInfo.addGangScore(-gangScoreTemp);
                    playerCardsInfo.addScore(-gangScoreTemp);
                    gangScore += gangScoreTemp;
                    this.roomInfo.addUserSocre(playerCardsInfo.getUserId(), -gangScoreTemp);
                }
            }
        }

        //暗杠
        for (Integer i : anGangType){
            for (PlayerCardsInfoMj playerCardsInfo : this.gameInfo.playerCardsInfos.values()){
                if (playerCardsInfo.getUserId() == this.userId) continue;
                this.roomInfo.addUserSocre(playerCardsInfo.getUserId(), -anGangScoreTemp);
                playerCardsInfo.addGangScore(-anGangScoreTemp);
                playerCardsInfo.addScore(-anGangScoreTemp);
                gangScore += anGangScoreTemp;
            }
        }

        List<String> cs = getCardsNoChiPengGang(cards);
        List<HuCardType> huList = HuUtil.isHu(cs, this, CardTypeUtil.cardType.get(card), new HuLimit(0));
        // 设置胡牌类型
        HuCardType huCardType = getMaxScoreHuCardType(huList);
        this.winType.addAll(huCardType.specialHuList);
        //这个分数是胡牌之后应该赢的分数
//        todo
        int score = huCardType.fan == 0 ? 5 : huCardType.fan;
        int count = bankerNumber(room.getBankerMap());
        score += count;
        //加轮数
        int selfScore = 0;

        if (isZimo){

            for (PlayerCardsInfoMj playerCardsInfoMj : this.gameInfo.playerCardsInfos.values()){
                if (playerCardsInfoMj.getUserId() == this.userId) continue;
                int tempScore = score;
                selfScore += tempScore;
                playerCardsInfoMj.addScore(-tempScore);
                this.roomInfo.addUserSocre(playerCardsInfoMj.getUserId(), -tempScore);
            }

        }else {

            PlayerCardsInfoMj dianPao = this.gameInfo.playerCardsInfos.get(dianpaoUser);
            int temp = score;
            selfScore += temp;
            dianPao.addScore(-temp);
            this.roomInfo.addUserSocre(dianPao.getUserId(), -temp);

        }

        this.addScore(selfScore);
        this.addScore(gangScore);
        this.roomInfo.addUserSocre(this.userId, selfScore);
        this.roomInfo.addUserSocre(this.userId, gangScore);
    }

    /**
     * @Title: 查询连庄次数
     * @Creater: Clark
     * @Description:
     * @param @param bankerMap
     * @param @return    设定文件
     * @return int    返回类型
     * @throws
     */
    public static int bankerNumber(Map<Integer,Long> bankerMap){
        int continueNum = 0;
        int maxGameNum = 0;
        Set<Integer> number = bankerMap.keySet();
        for (Integer i : number) {
            if(i>maxGameNum){
                maxGameNum = i;
            }
        }
        for (int i = maxGameNum; i > 1; i--) {
            if(bankerMap.get(i).longValue()== bankerMap.get(i-1)){
                continueNum++;
            }else{
                return continueNum;
            }
        }
        return continueNum;
    }

}
