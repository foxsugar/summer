package com.code.server.game.mahjong.logic;

/**
 * Created by sunxianping on 2018/3/26.
 */
public class PlayerCardsInfoKD_XZ extends PlayerCardsInfoKD {



    //        点炮情况：庄平胡，点炮玩家除正常算分外额外输分+5分
//        闲家平胡，庄家额外输分+5分（包胡情况点炮玩家出这额外5分）
//        自摸情况：庄家自摸每名玩家在分数*2基础上额外输分加10
//        闲家自摸，其他两名闲家分数*2，庄家分数*2+10
    @Override
    public void huCompute(RoomInfo room, GameInfo gameInfo, boolean isZimo, long dianpaoUser, String card) {
        super.huCompute(room, gameInfo, isZimo, dianpaoUser, card);

        long bankerUserId = this.gameInfo.getFirstTurn();
        boolean isBankerWin = this.userId == bankerUserId;
        PlayerCardsInfoMj bankerUser = this.gameInfo.getPlayerCardsInfos().get(bankerUserId);

        if (isZimo) {

            //闲家自摸
            if (!isBankerWin) {
                bankerUser.addScore(-10);
                room.addUserSocre(bankerUserId, -10);
                this.addScore(10);
                room.addUserSocre(this.userId, 10);

            } else {//庄家自摸
                for (PlayerCardsInfoMj playerCardsInfoMj : this.gameInfo.getPlayerCardsInfos().values()) {
                    if (playerCardsInfoMj.getUserId() != this.userId) {
                        playerCardsInfoMj.addScore(-10);
                        room.addUserSocre(playerCardsInfoMj.getUserId(), -10);
                    }
                }
                this.addScore(30);
                room.addUserSocre(this.userId, 30);
            }


        } else {//点炮

            PlayerCardsInfoMj dianpaoPlayer = this.getGameInfo().getPlayerCardsInfos().get(dianpaoUser);
            //庄平胡
            if (isBankerWin) {
                dianpaoPlayer.addScore(-5);
                room.addUserSocre(dianpaoUser, -5);
            } else {//闲家胡
                boolean isBaoHu = !dianpaoPlayer.isTing;
                //包胡
                if (isBaoHu) {
                    dianpaoPlayer.addScore(-5);
                    room.addUserSocre(dianpaoUser, -5);
                } else {
                    bankerUser.addScore(-5);
                    room.addUserSocre(bankerUserId, -5);
                }
            }

            this.addScore(5);
            room.addUserSocre(this.userId, 5);
        }


    }
}
