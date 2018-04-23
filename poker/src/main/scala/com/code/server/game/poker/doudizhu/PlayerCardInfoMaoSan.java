package com.code.server.game.poker.doudizhu;

import com.code.server.constant.game.CardStruct;

/**
 * Created by sunxianping on 2018/4/23.
 */
public class PlayerCardInfoMaoSan extends PlayerCardInfoDouDiZhu {


    public boolean checkPlayCard(CardStruct lastcardStruct , CardStruct currentCardStruct , int lasttype){
        if(!cards.containsAll(currentCardStruct.cards)){
            return false;
        }
        if (lastcardStruct == null || lastcardStruct.getUserId()==0) {
            return true;
        }
        if (lastcardStruct.getUserId() == userId) {
            return true;
        }
        boolean results = false;


        return true;
    }
}
