package com.code.server.game.mahjong.logic;

import com.code.server.constant.data.DataManager;
import com.code.server.constant.game.RoomStatistics;
import com.code.server.constant.kafka.IKafaTopic;
import com.code.server.constant.kafka.KafkaMsgKey;
import com.code.server.kafka.MsgProducer;
import com.code.server.redis.service.RedisManager;
import com.code.server.util.SpringUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sunxianping on 2018/6/23.
 */
public class RoomInfoGoldHeLe extends RoomInfo {




    public void init(String roomId, long userId, String modeTotal, String mode, int multiple, int gameNumber, int personNumber, long createUser, long bankerId, int mustZimo) {
        this.roomId = roomId;
        this.modeTotal = modeTotal;
        this.mode = mode;
        this.multiple = multiple;
        this.gameNumber = gameNumber;
        this.personNumber = personNumber;
        this.createUser = createUser;
        this.bankerId = bankerId;
        this.isInGame = false;
        this.bankerMap.put(1, bankerId);
        this.maxCircle = gameNumber;
        this.circleNumber.put(1, 1);
        this.mustZimo = mustZimo;

        this.createNeedMoney = 0;
        this.goldRoomType = 100;
        this.multiple = goldRoomType;
        this.isAddGold = DataManager.data.getRoomDataMap().get(this.gameType).getIsAddGold() == 1;
        clubRoomSetId();
    }

    @Override
    public void addUserSocre(long userId, double score) {
        //todo 金币改变
//        if (isGoldRoom()) {
        //钱不够的话 能扣多少是多少
        if (score < 0) {
            double temp = -score;
            double nowGold = RedisManager.getUserRedisService().getUserGold(userId);
            if (nowGold < temp) {
                score = -nowGold;
            }


        }


        double s = userScores.get(userId);
        userScores.put(userId, s + score);


        RedisManager.getUserRedisService().addUserGold(userId, score);

//        }

    }


    protected void roomAddUser(long userId) {

        this.users.add(userId);
        this.userStatus.put(userId, 0);
        this.userScores.put(userId, 0D);

        this.userScores.put(userId, RedisManager.getUserRedisService().getUserGold(userId));

        this.roomStatisticsMap.put(userId, new RoomStatistics(userId));
        this.canStartUserId = users.get(0);

        addUser2RoomRedis(userId);
    }

    @Override
    protected boolean isCanJoinCheckMoney(long userId) {
        //todo 检验金币

        double gold = RedisManager.getUserRedisService().getUserGold(userId);
        if (gold < getEnterGold()) {
            return false;
        }

        return true;
    }


    @Override
    public void clearReadyStatus(boolean isAddGameNum) {

        //抽水
        GameInfo gameInfo = (GameInfo) this.game;
        for (PlayerCardsInfoMj playerCardsInfoMj : gameInfo.getPlayerCardsInfos().values()) {
            if (playerCardsInfoMj.getScore() > 0) {
                double g = 3 * playerCardsInfoMj.getScore() / 100;
                RedisManager.getUserRedisService().addUserGold(playerCardsInfoMj.getUserId(), -g);
                //返利

                Map<String, Object> addGold = new HashMap<>();
                addGold.put("userId", playerCardsInfoMj.getUserId());
                addGold.put("gold",g);
                KafkaMsgKey kafkaMsgKey = new KafkaMsgKey().setMsgId(KAFKA_MSG_ID_GUESS_ADD_GOLD);
                MsgProducer msgProducer = SpringUtil.getBean(MsgProducer.class);
                msgProducer.send(IKafaTopic.CENTER_TOPIC, kafkaMsgKey, addGold);

            }
        }
        super.clearReadyStatus(isAddGameNum);
        //todo 如果 金币不够 退出
        int minGold = getOutGold();
        for (long userId : this.users) {
            double gold = RedisManager.getUserRedisService().getUserGold(userId);
            if (gold < minGold) {
                dissolutionRoom();
                return;
            }
        }
    }
}
