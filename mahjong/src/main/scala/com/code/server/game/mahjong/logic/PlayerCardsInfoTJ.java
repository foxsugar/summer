package com.code.server.game.mahjong.logic;

import com.code.server.game.mahjong.util.HuUtil;

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

        List<String> cs = getCardsNoChiPengGang(cards);
        System.out.println("检测是否可胡自摸= " + cs);
        int cardType = CardTypeUtil.cardType.get(card);

        return HuUtil.isHu(cs, this,cardType , null).size()>0;

    }

    private boolean isGangKai(){

        int size = this.operateList.size();

        return size != 0 && this.operateList.get(size - 1) == type_gang;
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
