package com.code.server.game.mahjong.logic;

import java.util.List;

/**
 * Created by sunxianping on 2017/8/11.
 */
public class PlayerCardsInfoTJ extends PlayerCardsInfoMj {

    @Override
    public void init(List<String> cards) {
        super.init(cards);
    }

    @Override
    public boolean isCanHu_zimo(String card) {
        return super.isCanHu_zimo(card);
    }


    @Override
    public boolean isCanChiThisCard(String card, String one, String two) {
        return false;
    }

    @Override
    public boolean isHasChi(String card) {
        return false;
    }

    @Override
    public boolean isCanChiTing(String card) {
        return false;
    }

    @Override
    public boolean isCanPengTing(String card) {
        return false;
    }

    @Override
    public boolean isCanHu_dianpao(String card) {
        return false;
    }



    @Override
    public boolean isCanTing(List<String> cards) {
        return false;
    }
}
