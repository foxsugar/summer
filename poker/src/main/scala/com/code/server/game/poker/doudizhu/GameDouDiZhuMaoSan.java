package com.code.server.game.poker.doudizhu;

/**
 * Created by sunxianping on 2018/5/24.
 */
public class GameDouDiZhuMaoSan extends GameDouDiZhu {


    protected void compute(boolean isDizhuWin) {

        double subScore = 0;
        int s = isDizhuWin ? -1 : 1;
        multiple *= tableScore;
        //地主
        PlayerCardInfoDouDiZhu playerCardInfoDizhu = playerCardInfos.get(dizhu);
//        if (playerCardInfoDizhu.isQiang()) {
//            multiple *= 2;
//        }
        for (PlayerCardInfoDouDiZhu playerCardInfo : playerCardInfos.values()) {
            //不是地主 扣分
            if (dizhu != playerCardInfo.getUserId()) {
                double score = multiple * s;
                if (playerCardInfo.isQiang()) {
                    score *= 2;
                    //地主抢了 再乘2
                    if(playerCardInfoDizhu.isQiang()){
                        score *= 2;
                    }
                }
                subScore += score;
                playerCardInfo.setScore(score);
                room.addUserSocre(playerCardInfo.getUserId(), score);
            }
        }

        playerCardInfoDizhu.setScore(-subScore);
        room.addUserSocre(dizhu, -subScore);

    }
}
