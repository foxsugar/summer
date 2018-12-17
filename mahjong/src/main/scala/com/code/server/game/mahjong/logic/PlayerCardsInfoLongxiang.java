package com.code.server.game.mahjong.logic;

import com.code.server.game.mahjong.util.HuCardType;
import com.code.server.game.mahjong.util.HuUtil;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.code.server.game.mahjong.logic.GameInfoLongxiang.mode_幺九谱;
import static com.code.server.game.mahjong.logic.GameInfoLongxiang.mode_扣听;

/**
 * Created by sunxianping on 2018-12-14.
 */
public class PlayerCardsInfoLongxiang extends PlayerCardsInfoMj {

    @Override
    public void init(List<String> cards) {
        super.init(cards);

        this.setIsHasFengShun(true);
        this.setHasZiShun(true);
        if(isHasMode(this.roomInfo.mode,mode_幺九谱)){
            this.setHasYaojiuShun(true);
        }

        specialHuScore.put(hu_十三幺, 1);

        specialHuScore.put(hu_缺一门, 1);
        specialHuScore.put(hu_缺两门, 1);

        specialHuScore.put(hu_中张, 1);
        specialHuScore.put(hu_将对, 1);
        specialHuScore.put(hu_四碰, 1);
        specialHuScore.put(hu_幺九, 1);


        specialHuScore.put(hu_七小对, 1);
        specialHuScore.put(hu_豪华七小对, 1);
        specialHuScore.put(hu_双豪七小对, 1);
        specialHuScore.put(hu_三豪七小对, 1);

        specialHuScore.put(hu_清一色, 1);

        specialHuScore.put(hu_清一色七小对, 1);
        specialHuScore.put(hu_清一色豪华七小对, 1);
        specialHuScore.put(hu_清一色双豪华七小对, 1);

        specialHuScore.put(hu_碰碰胡, 1);
        specialHuScore.put(hu_清一色碰碰胡, 1);


        specialHuScore.put(hu_门清, 1);

    }


    public boolean isCanTing(List<String> cards) {
        if (isTing || !isHasMode(this.roomInfo.mode, mode_扣听)) {
            return false;
        }
        String lastCard = this.cards.get(this.cards.size() - 1);
        int lastCardType = CardTypeUtil.getTypeByCard(lastCard);
        Set<Integer> set = getTingCardType(getCardsNoChiPengGang(cards), null);
        for (int type : set) {
            if (type != lastCardType) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否可以胡这张牌
     *
     * @param card
     * @return
     */
    public boolean isCanHu_dianpao(String card) {
        if (roomInfo.mustZimo == 1) {
            return false;
        }
        List<String> temp = getCardsAddThisCard(card);
        List<String> noPengAndGang = getCardsNoChiPengGang(temp);
        int cardType = CardTypeUtil.cardType.get(card);
        List<HuCardType> huList =  HuUtil.isHu(noPengAndGang, this, cardType, null);
        if (huList.size() == 0) {
            return false;
        }
        for (HuCardType huCardType : huList) {

        }
        return true;

    }

    @Override
    public void computeALLGang() {
        //明杠1分
        int sub = 0;
        int gangFan = 0;
        gangFan += this.anGangType.size() * 2;






        for (Map.Entry<Integer, Long> entry : this.mingGangType.entrySet()) {
            long dianGangUser = entry.getValue();
            //点杠
            if (dianGangUser != -1) {
                PlayerCardsInfoMj dianGangPlayer = this.gameInfo.getPlayerCardsInfos().get(dianGangUser);
                int temp = 1 * this.roomInfo.getMultiple();
                dianGangPlayer.addScore(-temp);
                dianGangPlayer.addGangScore(-temp);
                roomInfo.setUserSocre(dianGangUser, -temp);

                //自己加分
                this.addScore(temp);
                this.addGangScore(temp);
                roomInfo.setUserSocre(this.getUserId(), temp);

            } else {
                gangFan += 1;
            }
        }

        //除了点杠
        int score = gangFan * roomInfo.getMultiple();

        for (PlayerCardsInfoMj playerCardsInfo : gameInfo.getPlayerCardsInfos().values()) {
            if (playerCardsInfo.getUserId() != this.userId) {
                playerCardsInfo.addScore(-score);
                playerCardsInfo.addGangScore(-score);
                roomInfo.setUserSocre(playerCardsInfo.getUserId(), -score);
                sub += score;
            }
        }
        this.addGangScore(sub);
        this.addScore(sub);
        roomInfo.setUserSocre(this.getUserId(), sub);

    }

    @Override
    public void huCompute(RoomInfo room, GameInfo gameInfo, boolean isZimo, long dianpaoUser, String card) {
        super.huCompute(room, gameInfo, isZimo, dianpaoUser, card);
    }
}
