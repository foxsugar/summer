package com.code.server.game.mahjong.logic;

import com.code.server.game.mahjong.util.HuCardType;
import com.code.server.game.mahjong.util.HuLimit;
import com.code.server.game.mahjong.util.HuType;
import com.code.server.game.mahjong.util.HuUtil;

import java.util.Arrays;
import java.util.List;

/**
 * Created by sunxianping on 2019-11-20.
 */
public class PlayerCardsInfoLingchuan extends PlayerCardsInfoMj {

    @Override
    public void init(List<String> cards) {

        this.cards = cards;

        specialHuScore.put(hu_七小对, 2);
        specialHuScore.put(hu_十三幺, 2);

//        specialHuScore.put(hu_清一色, 1);
//        specialHuScore.put(hu_一条龙, 1);
//        specialHuScore.put(hu_十三幺, 1);
//        specialHuScore.put(hu_豪华七小对, 1);
//        specialHuScore.put(hu_双豪七小对, 1);
//        specialHuScore.put(hu_三豪七小对, 1);
//        specialHuScore.put(hu_清龙, 1);

//        specialHuScore.put(hu_缺一门, 0);
//        specialHuScore.put(hu_缺两门, 0);


    }

    @Override
    public boolean isHasChi(String card) {
        return false;
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
    public boolean isCanTing(List<String> cards) {
        if (!this.roomInfo.haveTing) {
            return false;
        }
        if (isTing) {
            return false;
        }

        List<HuCardType> list = getTingHuCardType(getCardsNoChiPengGang(cards), new HuLimit(0));


        for (HuCardType huCardType : list) {
            if (kaobazhangNum(huCardType) >= 8) {
                return true;
            }
        }
        return false;
    }

    /**
     * 靠八张数量
     *
     * @return
     */
    private int kaobazhangNum(HuCardType huCardType) {
        if (huCardType.specialHuList.contains(HuType.hu_十三幺)) {
            return 14;
        }
        int[] cardNum = {0, 0, 0, 0};
        for (String card : huCardType.cards) {
            int group = CardTypeUtil.getCardGroup(card);
            cardGroupAddCard(cardNum, group, 1);
        }
        for (int peng : huCardType.peng) {
            int group = CardTypeUtil.getCardGroupByCardType(peng);
            cardGroupAddCard(cardNum, group, 3);
        }
        for (int gang : huCardType.mingGang) {
            int group = CardTypeUtil.getCardGroupByCardType(gang);
            cardGroupAddCard(cardNum, group, 4);
        }
        for (int gang : huCardType.anGang) {
            int group = CardTypeUtil.getCardGroupByCardType(gang);
            cardGroupAddCard(cardNum, group, 4);
        }

        Arrays.sort(cardNum);
        return cardNum[3];
    }


    private void cardGroupAddCard(int[] arr, int group, int num) {
        if (group == 4 || group == 5) {
            arr[3] += num;
        } else {
            arr[group - 1] += num;
        }
    }

    @Override
    public void gangCompute(RoomInfo room, GameInfo gameInfo, boolean isMing, long diangangUser, String card) {

    }

    @Override
    public void huCompute(RoomInfo room, GameInfo gameInfo, boolean isZimo, long dianpaoUser, String card) {


        List<HuCardType> huList = HuUtil.isHu(getCardsNoChiPengGang(cards), this, CardTypeUtil.cardType.get(card), new HuLimit(0));

        HuCardType huCardType = null;
        int max = 0;
        for (HuCardType hct : huList) {
            int bz = kaobazhangNum(hct);
            if (bz > max) {
                huCardType = hct;
            }
        }

        this.winType.addAll(huCardType.specialHuList);
        int score = max;
        score += mingGangType.size();
        score += anGangType.size() * 2;
        //庄家 * 2
        boolean isBankerWin = this.userId == this.roomInfo.getBankerId();
        if(isBankerWin) score *= 2;
        if(isZimo) score *= 2;

        int allScore = 0;

        PlayerCardsInfoMj dianpao = this.gameInfo.playerCardsInfos.get(dianpaoUser);
        boolean dianpaoBaoAll= false;
        if (dianpao != null && !dianpao.isTing) {
            dianpaoBaoAll = true;
        }

        for (PlayerCardsInfoMj playerCardsInfoMj : this.gameInfo.playerCardsInfos.values()) {

            int scoreSelf = score;
            if (playerCardsInfoMj.userId == dianpaoUser) {
                scoreSelf += 2;
            }
            if (!dianpaoBaoAll) {
                playerCardsInfoMj.addScore(-scoreSelf);
                this.roomInfo.addUserSocre(playerCardsInfoMj.userId, -scoreSelf);
            }
            allScore += scoreSelf;
        }

        if (dianpaoBaoAll) {
            dianpao.addScore(-allScore);
            this.roomInfo.addUserSocre(dianpao.userId, -allScore);
        }

        this.addScore(allScore);
        this.roomInfo.addUserSocre(this.userId, allScore);

    }

}
