package com.code.server.game.mahjong.logic;

import com.code.server.game.mahjong.util.HuCardType;
import com.code.server.game.mahjong.util.HuUtil;

import java.util.List;

/**
 * Created by sunxianping on 2018-09-30.
 */
public class PlayerCardsInfoZhuohaoziKX extends PlayerCardsInfoZhuohaozi {


    @Override
    public void init(List<String> cards) {
        super.init(cards);

//        specialHuScore.put(hu_七小对,1);
//        specialHuScore.put(hu_十三幺,4);
//        specialHuScore.put(hu_豪华七小对,1);
//        specialHuScore.put(hu_双豪七小对_山西,1);
//        specialHuScore.remove(hu_七小对);
//        specialHuScore.remove(hu_豪华七小对);
//        specialHuScore.remove(hu_双豪七小对_山西);
    }

    @Override
    public boolean isCanHu_dianpao(String card) {

        if (!isTing ) return false;
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
            if (getMaxPoint(huCardType, true) >= DIANPAO_MIN_SCORE) {
                return true;
            }
        }
        return false;
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

            int temp = getMaxPoint(huCardType, !isZimo);
            //点炮
            if (!isZimo && huCardType.specialHuList.contains(hu_吊将) && CardTypeUtil.cardTingScore.get(lastCard)>=6) {
                temp = 20;
            }
            if (temp > maxPoint) {
                maxPoint = temp;
            }

        }
        boolean bankerIsZhuang = this.userId == this.gameInfo.getFirstTurn();

        //显庄 并且 赢得人是庄家
        boolean isBankerWinMore = bankerIsZhuang && isHasMode(this.roomInfo.mode, GameInfoZhuohaozi.mode_显庄);
        if (isBankerWinMore) maxPoint += 10;

        if (isZimo) maxPoint *= 2;

        boolean isBaoAll = !isZimo && !this.gameInfo.getPlayerCardsInfos().get(dianpaoUser).isTing;

        int allScore = 0;

        for (PlayerCardsInfoMj playerCardsInfoMj : this.gameInfo.playerCardsInfos.values()) {
            if (playerCardsInfoMj.getUserId() != this.userId) {

                int tempScore = maxPoint;
                //庄家多输
                if (playerCardsInfoMj.getUserId() == this.gameInfo.getFirstTurn() && isHasMode(this.roomInfo.mode, GameInfoZhuohaozi.mode_显庄)) {
                    if (isZimo) {
                        tempScore += 20;
                    } else {
                        tempScore += 10;
                    }
                }
                allScore += tempScore;

                if (!isBaoAll) {
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
}
