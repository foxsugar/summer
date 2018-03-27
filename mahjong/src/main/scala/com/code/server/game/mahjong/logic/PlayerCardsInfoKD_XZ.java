package com.code.server.game.mahjong.logic;

/**
 * Created by sunxianping on 2018/3/26.
 */
public class PlayerCardsInfoKD_XZ extends PlayerCardsInfoKD {



    @Override
    public void huCompute(RoomInfo room, GameInfo gameInfo, boolean isZimo, long dianpaoUser, String card) {
        super.huCompute(room, gameInfo, isZimo, dianpaoUser, card);

        boolean isBankerWin = this.userId == gameInfo.getFirstTurn();
        int subScore = 0;
        for(PlayerCardsInfoMj playerCardsInfoMj : gameInfo.getPlayerCardsInfos().values()){
            if (playerCardsInfoMj.getUserId() != this.userId) {
                int tempScore = 0;
                if (isZimo) {
                    tempScore = 10;
                }else{
                    tempScore = 5;
                }
                if(isBankerWin){
                    tempScore *= -1;
                }
                playerCardsInfoMj.addScore(tempScore);
                roomInfo.addUserSocre(playerCardsInfoMj.getUserId(), tempScore);
                subScore += subScore;
            }
        }


        this.addScore(-subScore);
        roomInfo.addUserSocre(this.userId, -subScore);

    }
}
