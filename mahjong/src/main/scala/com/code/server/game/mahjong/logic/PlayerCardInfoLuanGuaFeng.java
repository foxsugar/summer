package com.code.server.game.mahjong.logic;

import java.util.List;

/**
 * Created by sunxianping on 2018/7/25.
 */
public class PlayerCardInfoLuanGuaFeng extends  PlayerCardsInfoMj{


    @Override
    public boolean isHasXuanfengDan(List<String> cards,String card) {
        return isCanLiang();
    }


    private boolean isCanLiang(){
        int opSize = this.operateList.size();

        if(opSize == 1 && this.operateList.get(opSize - 1) == type_mopai){
            return this.cards.stream().filter(this::isFeng).count() >= 3;
        }
        return false;
    }


    private boolean isFeng(String card) {
        int type = CardTypeUtil.getCardGroup(card);
        return type == CardTypeUtil.GROUP_FENG || type == CardTypeUtil.GROUP_ZI;
    }

}
