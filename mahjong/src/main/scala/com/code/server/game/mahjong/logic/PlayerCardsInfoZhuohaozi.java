package com.code.server.game.mahjong.logic;

import com.code.server.game.mahjong.util.HuCardType;
import com.code.server.game.mahjong.util.HuUtil;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        int lastCard = CardTypeUtil.getTypeByCard(card);
        List<HuCardType> huList = HuUtil.isHu(this, getCardsNoChiPengGang(this.cards), getChiPengGangNum(), this.gameInfo.hun, lastCard);
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
        super.gangCompute(room, gameInfo, isMing, diangangUser, card);
        int gangType = CardTypeUtil.getTypeByCard(card);
        boolean isJinGang = this.gameInfo.hun.contains(gangType);
        int score = CardTypeUtil.cardTingScore.get(gangType) * 2;
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

                playerCardsInfoMj.addScore(-tempScore);
                this.roomInfo.addUserSocre(playerCardsInfoMj.getUserId(), -tempScore);

            }
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

        if (huCardType.specialHuList.contains(hu_吊将)) {
            return 10;
        }
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

}
