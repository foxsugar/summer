package com.code.server.game.mahjong.logic;

/**
 * Created by sunxianping on 2019-05-05.
 */
public class GameInfoHongzhongZLB extends GameInfoHongZhong {


    public int getNeedRemainCardNum(){
        if (!this.room.isHasMode(PlayerCardsInfoHongZhong.LIUPAI)) {
            return 0;
        }
        int gangCount = 0;
        for (PlayerCardsInfoMj playerCardsInfoMj : this.playerCardsInfos.values()) {
            gangCount += playerCardsInfoMj.getGangNum();
        }
        return 16 + gangCount;
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