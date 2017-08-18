package com.code.server.game.poker.doudizhu;


import com.code.server.constant.response.GameResultDouDizhu;
import com.code.server.game.room.kafka.MsgSender;
import com.code.server.redis.service.RedisManager;

/**
 * Created by sunxianping on 2017/3/13.
 */
public class GameDouDiZhuGold extends GameDouDiZhu {

    protected void compute(boolean isDizhuWin) {

        double subScore = 0;
        int s = isDizhuWin ? -1 : 1;
        //multiple *= (tableScore);
        //multiple *= room.getGoldRoomType();
        //地主
        PlayerCardInfoDouDiZhu playerCardInfoDizhu = playerCardInfos.get(dizhu);
        if (playerCardInfoDizhu.isQiang()) {
            multiple *= 2;
        }
        for (PlayerCardInfoDouDiZhu playerCardInfo : playerCardInfos.values()) {
            //不是地主 扣分
            if (dizhu != playerCardInfo.getUserId()) {
                double score = multiple * s;
                score *=room.getGoldRoomType();
                score *= (tableScore);
                if (playerCardInfo.isQiang()) {
                    score *= 2;
                }
                subScore += score;
                playerCardInfo.setScore(score);
                room.addUserSocre(playerCardInfo.getUserId(), score);
            }
        }

        playerCardInfoDizhu.setScore(-subScore);
        room.addUserSocre(dizhu, -subScore);

    }

    protected void sendResult(boolean isReopen, boolean isDizhuWin) {
        GameResultDouDizhu gameResultDouDizhu = new GameResultDouDizhu();
        gameResultDouDizhu.setGoldRoomType(room.getGoldRoomType()*tableScore);
        gameResultDouDizhu.setMultiple(multiple);
        gameResultDouDizhu.setSpring(isSpring);
        gameResultDouDizhu.setDizhuWin(isDizhuWin);
        gameResultDouDizhu.setReopen(isReopen);
        gameResultDouDizhu.setTableCards(tableCards);
        for (PlayerCardInfoDouDiZhu playerCardInfo : playerCardInfos.values()) {
            gameResultDouDizhu.getPlayerCardInfos().add(playerCardInfo.toVo());
        }
        //分数结算到房卡

        this.users.forEach(userId -> {
            RedisManager.getUserRedisService().addUserMoney(userId, this.room.getUserScores().get(userId));
        });
        for (Long l:this.room.getUserScores().keySet()) {
            this.room.getUserScores().put(l,this.room.getUserScores().get(l));
        }
        gameResultDouDizhu.getUserScores().putAll(this.room.getUserScores());
        MsgSender.sendMsg2Player("gameService", "gameResult", gameResultDouDizhu, users);

        replay.setResult(gameResultDouDizhu);
    }
}
