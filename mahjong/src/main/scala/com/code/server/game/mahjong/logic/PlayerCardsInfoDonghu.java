package com.code.server.game.mahjong.logic;

import com.code.server.game.mahjong.util.HuCardType;
import com.code.server.game.mahjong.util.HuLimit;
import com.code.server.game.mahjong.util.HuUtil;

import java.util.List;

/**
 * Created by sunxianping on 2017/10/10.
 */
public class PlayerCardsInfoDonghu extends PlayerCardsInfoSS {


    @Override
    public void init(List<String> cards) {

        this.cards = cards;
        isHasTing = true;

        specialHuScore.put(hu_七小对, 2);
        specialHuScore.put(hu_十三不靠, 2);

//        specialHuScore.put(hu_清一色, 1);
//        specialHuScore.put(hu_一条龙, 1);
//        specialHuScore.put(hu_十三幺, 1);
//        specialHuScore.put(hu_豪华七小对, 1);
//        specialHuScore.put(hu_双豪七小对, 1);
//        specialHuScore.put(hu_三豪七小对, 1);
//        specialHuScore.put(hu_清龙, 1);

        specialHuScore.put(hu_缺一门, 0);
        specialHuScore.put(hu_缺两门, 0);

        if (isHasMode(this.roomInfo.getMode(), GameInfoDonghu.mode_大胡)) {
            specialHuScore.put(hu_清一色, 3);
            specialHuScore.put(hu_一条龙, 3);
            specialHuScore.put(hu_七小对, 3);

            specialHuScore.put(hu_豪华七小对, 6);
            specialHuScore.put(hu_双豪七小对, 6);
            specialHuScore.put(hu_三豪七小对, 6);
            specialHuScore.put(hu_清龙, 6);

            specialHuScore.put(hu_缺一门, 0);
            specialHuScore.put(hu_缺两门, 0);
        }

    }

    public boolean isCanTing(List<String> cards) {
        List<HuCardType> list = getTingHuCardType(getCardsNoChiPengGang(cards), new HuLimit(0));
        return !isTing && list.stream().filter(hct -> hct.specialHuList.contains(hu_缺一门) || hct.specialHuList.contains(hu_缺两门)).count() > 0;

    }

    /**
     * 杠之后是否能听
     *
     * @param cards
     * @param cardType
     * @return
     */
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
        return list.stream().filter(hct -> hct.specialHuList.contains(hu_缺一门) || hct.specialHuList.contains(hu_缺两门)).count() > 0;
    }


    @Override
    public boolean isCanHu_dianpao(String card) {
        if (!isTing) return false;

        List<String> temp = getCardsAddThisCard(card);
        int groupCount = getCardGroup(temp);
        return groupCount != 3 && super.isCanHu_dianpao(card);
    }

    @Override
    public boolean isCanHu_zimo(String card) {
        if (!isTing) return false;

        List<String> temp = getCardsAddThisCard(card);
        int groupCount = getCardGroup(temp);
        return groupCount != 3 && super.isCanHu_zimo(card);

    }

    @Override
    public void gangCompute(RoomInfo room, GameInfo gameInfo, boolean isMing, long diangangUser, String card) {
        this.lastOperate = type_gang;
        operateList.add(type_gang);
        this.gameInfo.addUserOperate(this.userId, type_gang);
    }

    @Override
    public void huCompute(RoomInfo room, GameInfo gameInfo, boolean isZimo, long dianpaoUser, String card) {

        //算杠
        int gangScore = (this.mingGangType.size() + this.anGangType.size() * 2) * this.roomInfo.getMultiple();
        int subGang = 0;
        //自摸三个人赔
        if (isZimo) {
            for (PlayerCardsInfoMj playerCardInfo : this.gameInfo.playerCardsInfos.values()) {
                if (playerCardInfo.getUserId() != this.userId) {
                    int gangScoreTemp = gangScore * this.roomInfo.getMultiple();
                    playerCardInfo.addScore(-gangScoreTemp);
                    playerCardInfo.addGangScore(-gangScoreTemp);
                    this.roomInfo.addUserSocre(playerCardInfo.getUserId(), -gangScoreTemp);
                    subGang += gangScoreTemp;
                }
            }

        } else {//点炮的人赔
            PlayerCardsInfoMj dianpaoPlayer = this.gameInfo.playerCardsInfos.get(dianpaoUser);
            dianpaoPlayer.addScore(-gangScore * this.roomInfo.getMultiple());
            dianpaoPlayer.addGangScore(-gangScore * this.roomInfo.getMultiple());
            this.roomInfo.addUserSocre(dianpaoPlayer.getUserId(), -gangScore * this.roomInfo.getMultiple());
            subGang += gangScore * this.roomInfo.getMultiple();
        }
        this.addGangScore(subGang);
        this.addScore(subGang);
        this.roomInfo.addUserSocre(this.userId, subGang);


        List<String> cs = getCardsNoChiPengGang(cards);
        List<HuCardType> huList = HuUtil.isHu(cs, this, CardTypeUtil.cardType.get(card), new HuLimit(0));
        // 设置胡牌类型
        HuCardType huCardType = getMaxScoreHuCardType(huList);
        this.winType.addAll(huCardType.specialHuList);
        int score = huCardType.fan == 0 ? 1 : huCardType.fan;
        int subScore = 0;
        boolean isBanker = this.userId == gameInfo.getFirstTurn();
        if(isBanker) score += 1;

        //其他人扣分
        for (PlayerCardsInfoMj playerCardsInfoMj : this.gameInfo.playerCardsInfos.values()) {
            if(playerCardsInfoMj.getUserId() != userId){
                int scoreTemp = score;
                //如果是庄家多输一分
                if(playerCardsInfoMj.getUserId() == this.gameInfo.getFirstTurn()){
                    scoreTemp += 1;
                }
                scoreTemp = scoreTemp * this.roomInfo.getMultiple();
                playerCardsInfoMj.addScore(-scoreTemp);
                subScore += scoreTemp;
                this.roomInfo.addUserSocre(playerCardsInfoMj.getUserId(), -scoreTemp);
            }
        }

        //

        this.addScore(subScore);
        this.roomInfo.addUserSocre(this.userId, subScore);

    }
}
