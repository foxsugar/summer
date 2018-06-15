package com.code.server.game.mahjong.logic;

import com.code.server.game.mahjong.util.HuCardType;
import com.code.server.game.mahjong.util.HuUtil;

import java.util.*;

/**
 * Created by sunxianping on 2018/5/18.
 */
public class PlayerCardsInfoZhuohaozi extends PlayerCardsInfoKD {

    @Override
    public void init(List<String> cards) {
        super.init(cards);
//        specialHuScore.put(hu_清一色,2);
        specialHuScore.put(hu_吊将, 1);
        specialHuScore.put(hu_七小对, 1);
        specialHuScore.put(hu_豪华七小对, 1);
    }


    @Override
    public boolean isCanTing(List<String> cards) {
        if (isTing) {
            return false;
        }

        List<HuCardType> huCardTypes = getTingHuCardTypeWithHun(getCardsNoChiPengGang(cards), this.gameInfo.hun, this.getChiPengGangNum());
        for (HuCardType huCardType : huCardTypes) {
            int point = getMaxPoint(huCardType);
            if (point >= TING_MIN_SCORE) {
                return true;
            }

        }


        return false;
    }



    @Override
    public boolean isCanPengAddThisCard(String card) {
        int cardType = CardTypeUtil.getTypeByCard(card);
        if (this.gameInfo.hun.contains(cardType)) {
            return false;
        }
        return super.isCanPengAddThisCard(card);
    }


    /**
     * 杠之后是否能听
     *
     * @param cards
     * @param cardType
     * @return
     */
    protected boolean isCanTingAfterGang(List<String> cards, int cardType) {
        //先删除这次杠的
        removeCardByType(cards, cardType, 4);
        int chipenggangNum = this.getChiPengGangNum();
        //去掉碰
        for (int pt : pengType.keySet()) {//如果杠的是之前碰过的牌
            if (pt != cardType) {
                removeCardByType(cards, pt, 3);
                chipenggangNum--;
            }
        }
        //去掉杠的牌
        cards = getCardsNoGang(cards);

        chipenggangNum++;

        List<HuCardType> huCardTypes = getTingHuCardTypeWithHun(cards, this.gameInfo.hun, chipenggangNum);

        for (HuCardType huCardType : huCardTypes) {
            int point = getMaxPoint(huCardType);
            if (point >= TING_MIN_SCORE) {
                return true;
            }

        }
        return false;
    }


    @Override
    public boolean isCanHu_dianpao(String card) {
        if (!isTing) return false;
        //混牌 不能点炮
        int cardType = CardTypeUtil.getTypeByCard(card);
        if (this.gameInfo.hun.contains(cardType)) {
            return false;
        }
        List<String> temp = getCardsAddThisCard(card);
        List<String> noPengAndGang = getCardsNoChiPengGang(temp);
        int lastCard = CardTypeUtil.getTypeByCard(card);
        List<HuCardType> huList = HuUtil.isHu(this, noPengAndGang, getChiPengGangNum(), this.gameInfo.hun, lastCard);
        for (HuCardType huCardType : huList) {
            if (getMaxPoint(huCardType) >= DIANPAO_MIN_SCORE) {
                return true;
            }
        }
        return false;
    }


    @Override
    public boolean isCanHu_zimo(String card) {
        if (!isTing) return false;
        int lastCard = CardTypeUtil.getTypeByCard(card);

        List<HuCardType> huList = HuUtil.isHu(this, getCardsNoChiPengGang(this.cards), getChiPengGangNum(), this.gameInfo.hun, lastCard);
        for (HuCardType huCardType : huList) {
            if (getMaxPoint(huCardType) >= ZIMO_MIN_SCORE) {
                return true;
            }
        }
        return false;
    }

    public boolean isHasChi(String card) {
        return false;
    }


    @Override
    public void gangCompute(RoomInfo room, GameInfo gameInfo, boolean isMing, long diangangUser, String card) {
        this.lastOperate = type_gang;
        operateList.add(type_gang);
        this.gameInfo.addUserOperate(this.userId, type_gang);

        if (isMing) {
            this.roomInfo.addMingGangNum(this.getUserId());
        }else{
            this.roomInfo.addAnGangNum(this.getUserId());
        }

        int gangType = CardTypeUtil.getTypeByCard(card);
        boolean isJinGang = this.gameInfo.hun.contains(gangType);
        int score = CardTypeUtil.cardTingScore.get(gangType);
        if (isJinGang) score = 100;
        int allScore = 0;

        if (isMing && diangangUser != -1) {

            boolean isDianpaoTing = this.gameInfo.getPlayerCardsInfos().get(diangangUser).isTing;

            for (PlayerCardsInfoMj playerCardsInfoMj : this.gameInfo.playerCardsInfos.values()) {
                if (playerCardsInfoMj.getUserId() != this.userId) {
                    allScore += score;
                    if (isDianpaoTing) {
                        playerCardsInfoMj.addGangScore(-score);
                        playerCardsInfoMj.addScore(-score);
                        this.roomInfo.addUserSocre(playerCardsInfoMj.getUserId(), -score);
                    }

                }

            }

            if (!isDianpaoTing) {
                PlayerCardsInfoMj dianGangUser = this.gameInfo.getPlayerCardsInfos().get(diangangUser);
                dianGangUser.addGangScore(-allScore);
                dianGangUser.addScore(-allScore);
                this.roomInfo.addUserSocre(dianGangUser.getUserId(), -allScore);
            }
        } else {
            if (!isMing) score *= 2;
            for (PlayerCardsInfoMj playerCardsInfoMj : this.gameInfo.playerCardsInfos.values()) {
                if (playerCardsInfoMj.getUserId() != this.userId) {
                    allScore += score;
                    playerCardsInfoMj.addGangScore(-score);
                    playerCardsInfoMj.addScore(-score);
                    this.roomInfo.addUserSocre(playerCardsInfoMj.getUserId(), -score);
                }
            }
        }

        this.addGangScore(allScore);
        this.addScore(allScore);
        this.roomInfo.addUserSocre(this.getUserId(), allScore);



        room.pushScoreChange();
    }




    @Override
    public void huCompute(RoomInfo room, GameInfo gameInfo, boolean isZimo, long dianpaoUser, String card) {


        //显庄 庄家输赢每家多10分

        System.out.println("===========房间倍数============ " + room.getMultiple());
        List<String> cs = getCardsNoChiPengGang(cards);
        System.out.println("检测是否可胡自摸= " + cs);
        int cardType = CardTypeUtil.cardType.get(card);

        int lastCard = CardTypeUtil.getTypeByCard(card);
        int chiPengGangNum = getChiPengGangNum();
        List<HuCardType> huList = HuUtil.isHu(this, getCardsNoChiPengGang(this.cards), chiPengGangNum, this.gameInfo.hun, lastCard);
        int maxPoint = 0;
        for (HuCardType huCardType : huList) {

            int temp = getMaxPoint(huCardType);
            if(temp > maxPoint){
                maxPoint = temp;
            }

        }
        boolean bankerIsZhuang = this.userId == this.gameInfo.getFirstTurn();

        //显庄 并且 赢得人是庄家
        boolean isBankerWinMore = bankerIsZhuang && isHasMode(this.roomInfo.mode, GameInfoZhuohaozi.mode_显庄);
        if(isBankerWinMore) maxPoint += 10;

        if(isZimo) maxPoint *= 2;

        boolean isBaoAll = !isZimo && !this.gameInfo.getPlayerCardsInfos().get(dianpaoUser).isTing;

        int allScore = 0;

        for (PlayerCardsInfoMj playerCardsInfoMj : this.gameInfo.playerCardsInfos.values()) {
            if (playerCardsInfoMj.getUserId() != this.userId) {

                int tempScore = maxPoint;
                //庄家多输
                if (playerCardsInfoMj.getUserId() == this.gameInfo.getFirstTurn()) {
                    if(isZimo) {
                        tempScore += 20;
                    }else{
                        tempScore += 10;
                    }
                }
                allScore += tempScore;

                if(!isBaoAll){
                    playerCardsInfoMj.addScore(-tempScore);
                    this.roomInfo.addUserSocre(playerCardsInfoMj.getUserId(), -tempScore);
                }

            }
        }

        if (isBaoAll) {
            PlayerCardsInfoMj dpUser = this.gameInfo.getPlayerCardsInfos().get(dianpaoUser);
            dpUser.addScore(-allScore);
            this.roomInfo.addUserSocre(dpUser.getUserId(), -allScore);
        }

        this.addScore(allScore);
        this.roomInfo.addUserSocre(this.userId, allScore);


    }


    /**
     * 得到最大听的点数
     *
     * @param huCardType
     * @return
     */
    private int getMaxPoint(HuCardType huCardType) {
        if (huCardType.specialHuList.contains(hu_吊将)) {
            return 10;
        }
        boolean isHun = HuUtil.cardIsHun(this.gameInfo.hun, huCardType.tingCardType);
        if (!isHun) return CardTypeUtil.cardTingScore.get(huCardType.tingCardType);
        Set<Integer> cards = new HashSet<>();
        Set<Integer> result = new HashSet<>();
        if (huCardType.hunReplaceCard.size() != 0) {
            if (huCardType.hunReplaceCard.contains(-1)) {
                return 10;
            } else {
                cards.addAll(huCardType.hunReplaceCard);
            }
        }

//        if (huCardType.specialHuList.contains(hu_吊将)) {
//            return 10;
//        }
        if (huCardType.hun3.size() > 0) {
            return 10;
        }
        //有两个混的情况
        if (huCardType.hun2.size() > 0) {
            for (int cardType : huCardType.hun2) {
                int point = CardTypeUtil.cardTingScore.get(cardType);
                if (point == 10) {
                    return 10;
                }
                if (point == 7 || point == 8 || point == 9) {
                    result.add(9);
                }
            }
        }


        for (int cardType : cards) {
            result.add(CardTypeUtil.cardTingScore.get(cardType));
        }

        return Collections.max(result);
    }


    public static void main(String[] args) {
        PlayerCardsInfoZhuohaozi playerCardsInfo = new PlayerCardsInfoZhuohaozi();

//        change();



        playerCardsInfo.isHasFengShun = true;


        String[] s = new String[]{"032", "033", "034", "036","040",  "044", "056",  "060",  "064",   "092", "093", "104", "105","135"};//092
//        String[] s = new String[]{"112", "113", "114",   "024",   "028", "032",  "088", "092", "096",  "097",    "132", "133", "124", "120"};

        List<Integer> hun = new ArrayList<>();
        hun.add(29);
//        hun.add(1);
//        hun.add(8);


        RoomInfo roomInfo = new RoomInfo();
        roomInfo.setMode("1023");
        GameInfoTJ gameInfoTJ = new GameInfoTJ();
        gameInfoTJ.hun = hun;
        playerCardsInfo.setRoomInfo(roomInfo);
        playerCardsInfo.setGameInfo(gameInfoTJ);
        playerCardsInfo.cards = new ArrayList<>();
        playerCardsInfo.init(playerCardsInfo.cards);


//        playerCardsInfo.pengType.put(2,0L);
//        playerCardsInfo.pengType.put(6,0L);
//        playerCardsInfo.anGangType.add(32);

        playerCardsInfo.isTing = true;
//        playerCardsInfo.pengType.put(30,0L);

        List<String> list = Arrays.asList(s);
        playerCardsInfo.cards.addAll(list);

//        List<HuCardType> huList = HuUtil.isHu(playerCardsInfo,
//                playerCardsInfo.getCardsNoChiPengGang(playerCardsInfo.cards),
//                playerCardsInfo.getChiPengGangNum(), hun, 23);
//        boolean isCanHu = playerCardsInfo.isCanHu_dianpao("024");
//        boolean isCanHu = playerCardsInfo.isCanHu_zimo("116");
        boolean isGang = playerCardsInfo.isCanGangThisCard("035");


//        boolean isCanTing = playerCardsInfo.isCanTing(playerCardsInfo.cards);
//        System.out.println(isCanTing);
//        System.out.println("是否可以胡: " + isCanHu);
//        huList.forEach(h -> System.out.println(h.specialHuList));
//        System.out.println(huList);
    }
}
