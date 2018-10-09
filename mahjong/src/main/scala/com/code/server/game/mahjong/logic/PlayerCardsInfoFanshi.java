package com.code.server.game.mahjong.logic;

import com.code.server.game.mahjong.util.HuCardType;
import com.code.server.game.mahjong.util.HuUtil;

import java.util.List;
import java.util.Map;

/**
 * Created by sunxianping on 2018-10-09.
 */
public class PlayerCardsInfoFanshi extends PlayerCardsInfoMj {

    @Override
    public void init(List<String> cards) {
        super.init(cards);
        specialHuScore.put(hu_缺一门,0);

        specialHuScore.put(hu_夹张,0);
        specialHuScore.put(hu_边张,0);
        specialHuScore.put(hu_吊张,0);


        specialHuScore.put(hu_清一色,9);
        specialHuScore.put(hu_一条龙,4);
        specialHuScore.put(hu_清龙,12);
        specialHuScore.put(hu_架龙,9);
        specialHuScore.put(hu_清一色架龙,18);


        specialHuScore.put(hu_七小对,6);
        specialHuScore.put(hu_豪华七小对,10);
        specialHuScore.put(hu_双豪七小对_山西,20);

        specialHuScore.put(hu_清一色七小对,19);
        specialHuScore.put(hu_清一色豪华七小对,19);
        specialHuScore.put(hu_清一色双豪华七小对,29);


        specialHuScore.put(hu_碰碰胡,4);
        specialHuScore.put(hu_清一色碰碰胡,10);









    }

    public boolean isCanTing(List<String> cards) {
        return false;
    }

    public boolean isHasChi(String card) {
        return false;
    }


    @Override
    public boolean isCanHu_dianpao(String card) {
        //混牌 不能点炮
        int cardType = CardTypeUtil.getTypeByCard(card);
        if (this.gameInfo.hun.contains(cardType)) {
            return false;
        }
        List<String> temp = getCardsAddThisCard(card);
        List<String> noPengAndGang = getCardsNoChiPengGang(temp);
        int lastCard = CardTypeUtil.getTypeByCard(card);
        List<HuCardType> huList = HuUtil.isHu(this, noPengAndGang, getChiPengGangNum(), this.gameInfo.hun, lastCard);
        return huList.size() > 0;
    }

    @Override
    public boolean isCanHu_zimo(String card) {
        int lastCard = CardTypeUtil.getTypeByCard(card);

        List<HuCardType> huList = HuUtil.isHu(this, getCardsNoChiPengGang(this.cards), getChiPengGangNum(), this.gameInfo.hun, lastCard);
        return huList.size() > 0;
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
                int temp = 3 * this.roomInfo.getMultiple();
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


}
