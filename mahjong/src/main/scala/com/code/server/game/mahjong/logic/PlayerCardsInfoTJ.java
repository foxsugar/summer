package com.code.server.game.mahjong.logic;

import com.code.server.game.mahjong.util.HuCardType;
import com.code.server.game.mahjong.util.HuUtil;

import java.util.List;

/**
 * Created by sunxianping on 2017/8/11.
 */
public class PlayerCardsInfoTJ extends PlayerCardsInfoMj {

    @Override
    public void init(List<String> cards) {
        super.init(cards);


        specialHuScore.put(hu_混吊,1);

        specialHuScore.put(hu_捉五,3);
        specialHuScore.put(hu_混儿吊捉五,3);

        specialHuScore.put(hu_龙,1);
        specialHuScore.put(hu_本混龙,1);

        specialHuScore.put(hu_捉五龙,1);
        specialHuScore.put(hu_本混捉五龙,3);

        specialHuScore.put(hu_混儿吊龙,10);
        specialHuScore.put(hu_混儿吊本混龙,1);

        specialHuScore.put(hu_混儿吊捉五龙,7);
        specialHuScore.put(hu_混儿吊捉五本混龙,50);




    }

    @Override
    public boolean isCanHu_zimo(String card) {
        //小相公
        if(this.isPlayHun){
            return false;
        }
        List<String> cs = getCardsNoChiPengGang(cards);
        System.out.println("检测是否可胡自摸= " + cs);
        int cardType = CardTypeUtil.cardType.get(card);

        int lastCard = CardTypeUtil.getTypeByCard(card);
        int chiPengGangNum = getChiPengGangNum();
        return HuUtil.isHu(this,this.cards,chiPengGangNum,this.gameInfo.hun,lastCard).size() > 0;

    }


    @Override
    public void huCompute(RoomInfo room, GameInfo gameInfo, boolean isZimo, long dianpaoUser, String card) {
        System.out.println("===========房间倍数============ "+room.getMultiple());
        List<String> cs = getCardsNoChiPengGang(cards);
        System.out.println("检测是否可胡自摸= " + cs);
        int cardType = CardTypeUtil.cardType.get(card);

        int lastCard = CardTypeUtil.getTypeByCard(card);
        int chiPengGangNum = getChiPengGangNum();
        List<HuCardType> huList = HuUtil.isHu(this,this.cards,chiPengGangNum,this.gameInfo.hun,lastCard);

        //是否是杠开
        boolean isGangKai = isGangKai();

        //是否是素胡
        boolean isSuHu = isSuHu();

        HuCardType maxHuType = getMaxScoreHuCardType(huList);


    }


    /**
     * 是否是素和
     * @return
     */
    protected boolean isSuHu(){
        boolean isHasHun = false;
        for (String card : this.cards) {
            int cardType = CardTypeUtil.getTypeByCard(card);
            if (this.gameInfo.hun.contains(cardType)) {
                isHasHun = true;
            }
        }
        return !isHasHun;
    }

    /**
     * 是否是杠开
     * @return
     */
    protected boolean isGangKai(){

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
