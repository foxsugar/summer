package com.code.server.game.mahjong.logic;

import com.code.server.game.mahjong.util.HuCardType;
import com.code.server.game.mahjong.util.HuUtil;

import java.util.List;

/**
 * Created by sunxianping on 2018/7/25.
 */
public class PlayerCardInfoLuanGuaFeng extends  PlayerCardsInfoMj{


    @Override
    public boolean isHasXuanfengDan(List<String> cards,String card) {
        return isCanLiang();
    }

    @Override
    public boolean isCanBufeng(String card) {
        //必须亮过
        if (this.xuanfengDan.size() == 0) {
            return false;
        }
        if(!CardTypeUtil.isFeng(card)){
            return false;
        }


        return true;
    }

    private boolean isCanLiang(){
        int opSize = this.operateList.size();

        if(opSize == 1 && this.operateList.get(opSize - 1) == type_mopai){
            return this.cards.stream().filter(card->isFeng(card) || isHun(card)).count() >= 3;
        }
        return false;
    }


    private boolean isFeng(String card) {
        int type = CardTypeUtil.getCardGroup(card);
        return type == CardTypeUtil.GROUP_FENG || type == CardTypeUtil.GROUP_ZI;
    }

    private boolean isHun(String card) {
        int type = CardTypeUtil.getCardGroup(card);
        return this.gameInfo.hun.contains(type);
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


        boolean bankerIsZhuang = this.userId == this.gameInfo.getFirstTurn();

        //显庄 并且 赢得人是庄家
        boolean isBankerWinMore = bankerIsZhuang && isHasMode(this.roomInfo.mode, GameInfoZhuohaozi.mode_显庄);
        if (isBankerWinMore) maxPoint += 10;

        if (isZimo) maxPoint *= 2;


        int allScore = 0;

        for (PlayerCardsInfoMj playerCardsInfoMj : this.gameInfo.playerCardsInfos.values()) {
            if (playerCardsInfoMj.getUserId() != this.userId) {

                int tempScore = maxPoint;
                //庄家多输

                allScore += tempScore;



            }
        }


        this.addScore(allScore);
        this.roomInfo.addUserSocre(this.userId, allScore);


    }
}
