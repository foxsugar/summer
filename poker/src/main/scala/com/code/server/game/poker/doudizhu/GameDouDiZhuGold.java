package com.code.server.game.poker.doudizhu;


import com.code.server.constant.game.CardStruct;
import com.code.server.constant.response.ErrorCode;
import com.code.server.constant.response.GameResultDouDizhu;
import com.code.server.constant.response.ResponseVo;
import com.code.server.game.room.kafka.MsgSender;
import com.code.server.game.room.service.RoomManager;
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


    /**
     * 出牌
     *
     * @param userId
     */
    public int play(long userId, CardStruct cardStruct) {
        PlayerCardInfoDouDiZhu playerCardInfo = playerCardInfos.get(userId);
        //不可出牌
        if (!playerCardInfo.checkPlayCard(lastCardStruct, cardStruct, lasttype)) {
            return ErrorCode.CAN_NOT_PLAY;
        }

        userPlayCount.add(userId);
        playerCardInfo.setPlayCount(playerCardInfo.getPlayCount() + 1);

        long nextUserCard = nextTurnId(cardStruct.getUserId()); //下一个出牌的人

        cardStruct.setNextUserId(nextUserCard);
        cardStruct.setUserId(userId);

        playTurn = nextUserCard;

        MsgSender.sendMsg2Player(new ResponseVo("gameService", "playResponse", cardStruct), this.users);
        lasttype = cardStruct.getType();//保存这次出牌的类型
        lastCardStruct = cardStruct;//保存这次出牌的牌型


        //删除牌
        playerCardInfo.cards.removeAll(cardStruct.getCards());

        //处理炸
        handleBomb(cardStruct);

        //回放
        replay.getOperate().add(Operate.getOperate_PLAY(userId,cardStruct,false));

        //牌打完
        if (playerCardInfo.cards.size() == 0) {
            PlayerCardInfoDouDiZhu playerCardInfoDizhu = playerCardInfos.get(dizhu);
            //是否是春天
            if (userPlayCount.size() == 1 || playerCardInfoDizhu.getPlayCount() == 1) {
                isSpring = true;
                multiple *= 2;
            }

            compute(playerCardInfo.getUserId() == dizhu);

            sendResult(false, playerCardInfo.getUserId() == dizhu);


            //生成记录
            genRecord();

            room.clearReadyStatus(true);
            RoomManager.removeRoom(room.getRoomId());
            //sendFinalResult();

        }
        MsgSender.sendMsg2Player("gameService", "play", 0, userId);
//        userId.sendMsg("gameService", "play", 0);
        updateLastOperateTime();
        return 0;
    }
}
