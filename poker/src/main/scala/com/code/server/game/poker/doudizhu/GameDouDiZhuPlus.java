package com.code.server.game.poker.doudizhu;


import com.code.server.constant.game.CardStruct;
import com.code.server.constant.kafka.IKafaTopic;
import com.code.server.constant.kafka.KafkaMsgKey;
import com.code.server.constant.response.GameOfResult;
import com.code.server.constant.response.GameResultDouDizhu;
import com.code.server.constant.response.ResponseVo;
import com.code.server.constant.response.UserOfResult;
import com.code.server.game.room.Room;
import com.code.server.game.room.kafka.MsgSender;
import com.code.server.game.room.service.RoomManager;
import com.code.server.kafka.MsgProducer;
import com.code.server.redis.service.RedisManager;
import com.code.server.util.IdWorker;
import com.code.server.util.SpringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by sunxianping on 2017/3/13.
 */
public class GameDouDiZhuPlus extends GameDouDiZhu {


    public void startGame(List<Long> users, Room room) {

        RoomDouDiZhuPlus roomDouDiZhuPlus = (RoomDouDiZhuPlus)room;

        this.users.forEach(userId -> {
            RedisManager.getUserRedisService().addUserMoney(userId, - roomDouDiZhuPlus.getUsesMoney().get(room.getGoldRoomType()));
        });

        Map<Long, Double> userScores = new HashMap<>();
        for (Long l:room.getUsers()) {
            userScores.put(l,0.0);
        }
        room.setUserScores(userScores);
        this.room = room;
        init(users, room.getBankerId());
        updateLastOperateTime();
        //通知其他人游戏已经开始
        MsgSender.sendMsg2Player(new ResponseVo("gameService", "gameBegin", "ok"), this.getUsers());
        RoomManager.getInstance().getRobotRoom().add(room);
    }

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
/*        for (PlayerCardInfoDouDiZhu playerCardInfo : playerCardInfos.values()) {
            gameResultDouDizhu.getPlayerCardInfos().add(playerCardInfo.toVo());
        }
        //分数结算到房卡

        this.users.forEach(userId -> {
            RedisManager.getUserRedisService().addUserMoney(userId, this.room.getUserScores().get(userId));
        });*/

        ArrayList<Long> tempList = new ArrayList<>();
        tempList.addAll(this.users);
        tempList.remove(dizhu);

        double dizhuOfHave = RedisManager.getUserRedisService().getUserMoney(dizhu);
        double person1OfHave = RedisManager.getUserRedisService().getUserMoney(tempList.get(0));
        double person2OfHave = RedisManager.getUserRedisService().getUserMoney(tempList.get(1));
        double dizhuOfGame =this.room.getUserScores().get(dizhu);
        double person1OfGame =this.room.getUserScores().get(tempList.get(0));
        double person2OfGame =this.room.getUserScores().get(tempList.get(1));
        if(dizhuOfGame>0){//地主赢
            if(person1OfGame+person1OfHave<=0){//豆不够
                RedisManager.getUserRedisService().addUserMoney(tempList.get(0), -person1OfHave);
                RedisManager.getUserRedisService().addUserMoney(dizhu, person1OfHave);
            }else{
                RedisManager.getUserRedisService().addUserMoney(tempList.get(0), person1OfGame);
                RedisManager.getUserRedisService().addUserMoney(dizhu, -person1OfGame);
            }

            if(person2OfGame+person2OfHave<=0){
                RedisManager.getUserRedisService().addUserMoney(tempList.get(1), -person2OfHave);
                RedisManager.getUserRedisService().addUserMoney(dizhu, person2OfHave);
            }else{
                RedisManager.getUserRedisService().addUserMoney(tempList.get(1), person2OfGame);
                RedisManager.getUserRedisService().addUserMoney(dizhu, -person2OfGame);
            }

        }else{//地主输
            if(dizhuOfGame+dizhuOfHave>=0){//豆充足
                RedisManager.getUserRedisService().addUserMoney(dizhu, dizhuOfGame);
                RedisManager.getUserRedisService().addUserMoney(tempList.get(0), person1OfGame);
                RedisManager.getUserRedisService().addUserMoney(tempList.get(1), person2OfGame);
            }else{
                RedisManager.getUserRedisService().addUserMoney(dizhu, -dizhuOfHave);
                RedisManager.getUserRedisService().addUserMoney(tempList.get(0), person1OfGame/(person1OfGame+person2OfGame) * dizhuOfHave);
                RedisManager.getUserRedisService().addUserMoney(tempList.get(1), person2OfGame/(person1OfGame+person2OfGame) * dizhuOfHave);
            }
        }


        for (Long l:this.room.getUserScores().keySet()) {
            this.room.getUserScores().put(l,this.room.getUserScores().get(l));
        }

        //将room保存到playerCardInfos
        for (PlayerCardInfoDouDiZhu playerCardInfo : playerCardInfos.values()) {
            playerCardInfo.setScore(this.room.getUserScores().get(playerCardInfo.getUserId()));
            gameResultDouDizhu.getPlayerCardInfos().add(playerCardInfo.toVo());
        }

        gameResultDouDizhu.getUserScores().putAll(this.room.getUserScores());
        MsgSender.sendMsg2Player("gameService", "gameResult", gameResultDouDizhu, users);

        replay.setResult(gameResultDouDizhu);

        pushScoreChange();

        a: for (Long l:room.getUsers()) {
            if (RedisManager.getUserRedisService().getUserMoney(l) < RoomDouDiZhuPlus.needsMoney.get(room.getGoldRoomType())){
                room.clearReadyStatus(true);
                List<UserOfResult> userOfResultList = this.room.getUserOfResult();
                // 存储返回
                GameOfResult gameOfResult = new GameOfResult();
                gameOfResult.setUserList(userOfResultList);
                MsgSender.sendMsg2Player("gameService", "gameFinalResult", gameOfResult, users);

                RoomManager.removeRoom(room.getRoomId());

                //战绩
                this.room.genRoomRecord();
                break a;
            }
        }
    }


    @Override
    protected void handleBomb(CardStruct cardStruct) {

        if (cardStruct.getType() == CardStruct.type_炸) {
            List<Integer> cards = cardStruct.getCards();
            zhaCount += 1;//记录炸的数量
            multiple *= 2;//记录倍数
        } else if (cardStruct.getType() == CardStruct.type_火箭) {
            zhaCount += 1;//记录炸的数量
            multiple *= 2;//记录倍数
        }
    }


    public void pushScoreChange() {
        Map<Long, Double> userMoneys = new HashMap<>();
        for (Long l: users) {
            userMoneys.put(l,RedisManager.getUserRedisService().getUserMoney(l));
        }
        MsgSender.sendMsg2Player(new ResponseVo("gameService", "scoreChange", userMoneys), this.getUsers());
    }

    public void init(List<Long> users, long dizhuUser) {
        //初始化玩家
        for (Long uid : users) {
            PlayerCardInfoDouDiZhu playerCardInfo = getGameTypePlayerCardInfo();
            playerCardInfo.userId = uid;
            playerCardInfos.put(uid, playerCardInfo);
        }
        this.users.addAll(users);


        shuffle();
        deal();
        chooseDizhu(room.getUsers().get(rand.nextInt(3)));
    }

    protected void genRecord() {

        room.setRoomType("3");
        long id = IdWorker.getDefaultInstance().nextId();
        genRecord(playerCardInfos.values().stream().collect
                (Collectors.toMap(PlayerCardInfoDouDiZhu::getUserId, PlayerCardInfoDouDiZhu::getScore)), room, id);

        //回放
        replay.setId(id);
        replay.setCount(playerCardInfos.size());
        replay.setRoom_uuid(this.room.getUuid());

        replay.setRoomInfo(this.room.toVo(0));

        KafkaMsgKey kafkaMsgKey = new KafkaMsgKey().setMsgId(KAFKA_MSG_ID_REPLAY);
        MsgProducer msgProducer = SpringUtil.getBean(MsgProducer.class);
        msgProducer.send(IKafaTopic.CENTER_TOPIC, kafkaMsgKey, replay);
    }
}
