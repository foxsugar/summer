package com.code.server.game.mahjong.logic;

import java.util.List;

/**
 * Created by sunxianping on 2019-04-15.
 *
 * 血战到底 麻将
 */
public class PlayerCardsInfoXZDD extends PlayerCardsInfoMj{
    @Override
    public void init(List<String> cards) {
        super.init(cards);

//        specialHuScore.put(hu_清一色,2);
        specialHuScore.put(hu_吊将, 0);

    }


    @Override
    public boolean isHasChi(String card) {
        return false;
    }

    @Override
    public boolean isHasGang() {
        if(isAlreadyHu) return false;
        return super.isHasGang();
    }


    @Override
    public boolean isCanPengAddThisCard(String card) {
        if(isAlreadyHu) return false;
        return super.isCanPengAddThisCard(card);
    }

    @Override
    public boolean isCanGangAddThisCard(String card) {
        if(isAlreadyHu) return false;
        return super.isCanGangAddThisCard(card);
    }

    @Override
    public boolean isCanGangThisCard(String card) {
        if(isAlreadyHu) return false;
        return super.isCanGangThisCard(card);
    }

    @Override
    public boolean isCanHu_dianpao(String card) {
        if(isAlreadyHu) return false;
        return super.isCanHu_dianpao(card);
    }

    @Override
    public boolean isCanHu_zimo(String card) {
        if(isAlreadyHu) return false;
        return super.isCanHu_zimo(card);
    }

    @Override
    public boolean isCanTing(List<String> cards) {
        if(isAlreadyHu) return false;
        return super.isCanTing(cards);
    }


}
