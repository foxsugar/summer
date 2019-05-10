package com.code.server.game.mahjong.logic;

/**
 * Created by sunxianping on 2019-05-05.
 */
public class GameInfoHongzhongZLB extends GameInfoHongZhong {


    public int getNeedRemainCardNum(){
        if (!this.room.isHasMode(GameInfoZhuohaozi.mode_留牌)) {
            return 0;
        }
        int gangCount = 0;
        for (PlayerCardsInfoMj playerCardsInfoMj : this.playerCardsInfos.values()) {
            gangCount += playerCardsInfoMj.getGangNum();
        }
        int add =  (gangCount % 2 == 0) ? 0:1;

        return 16 + add;
    }

    /**
     * 是否荒庄
     *
     * @param playerCardsInfo
     * @return
     */
    protected boolean isHuangzhuang(PlayerCardsInfoMj playerCardsInfo) {
        return this.remainCards.size() <= getNeedRemainCardNum();
    }


}
