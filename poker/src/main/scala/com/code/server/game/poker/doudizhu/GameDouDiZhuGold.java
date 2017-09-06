package com.code.server.game.poker.doudizhu;


import com.code.server.constant.game.CardStruct;
import com.code.server.constant.response.ErrorCode;
import com.code.server.constant.response.GameResultDouDizhu;
import com.code.server.constant.response.ResponseVo;
import com.code.server.game.room.kafka.MsgSender;
import com.code.server.game.room.service.RoomManager;
import com.code.server.redis.service.RedisManager;

import java.util.*;

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
                this.room.getUserScores().put(tempList.get(0),-person1OfHave);
                this.room.getUserScores().put(dizhu,person1OfHave);
            }else{
                RedisManager.getUserRedisService().addUserMoney(tempList.get(0), person1OfGame);
                RedisManager.getUserRedisService().addUserMoney(dizhu, -person1OfGame);
                this.room.getUserScores().put(tempList.get(0),person1OfGame);
                this.room.getUserScores().put(dizhu,-person1OfGame);
            }

            if(person2OfGame+person2OfHave<=0){
                RedisManager.getUserRedisService().addUserMoney(tempList.get(1), -person2OfHave);
                RedisManager.getUserRedisService().addUserMoney(dizhu, person2OfHave);
                this.room.getUserScores().put(tempList.get(1),-person2OfHave);
                this.room.getUserScores().put(dizhu,this.room.getUserScores().get(dizhu)+person2OfHave);
            }else{
                RedisManager.getUserRedisService().addUserMoney(tempList.get(1), person2OfGame);
                RedisManager.getUserRedisService().addUserMoney(dizhu, -person2OfGame);
                this.room.getUserScores().put(tempList.get(1),person2OfGame);
                this.room.getUserScores().put(dizhu,this.room.getUserScores().get(dizhu)-person2OfGame);
            }

        }else{//地主输
            if(dizhuOfGame+dizhuOfHave>=0){//豆充足
                RedisManager.getUserRedisService().addUserMoney(dizhu, dizhuOfGame);
                RedisManager.getUserRedisService().addUserMoney(tempList.get(0), person1OfGame);
                RedisManager.getUserRedisService().addUserMoney(tempList.get(1), person2OfGame);
                this.room.getUserScores().put(dizhu,dizhuOfGame);
                this.room.getUserScores().put(tempList.get(0),person1OfGame);
                this.room.getUserScores().put(tempList.get(1),person2OfGame);
            }else{
                RedisManager.getUserRedisService().addUserMoney(dizhu, -dizhuOfHave);
                RedisManager.getUserRedisService().addUserMoney(tempList.get(0), person1OfGame/(person1OfGame+person2OfGame) * dizhuOfHave);
                RedisManager.getUserRedisService().addUserMoney(tempList.get(1), person2OfGame/(person1OfGame+person2OfGame) * dizhuOfHave);
                this.room.getUserScores().put(dizhu,-dizhuOfHave);
                this.room.getUserScores().put(tempList.get(0),person1OfGame/(person1OfGame+person2OfGame) * dizhuOfHave);
                this.room.getUserScores().put(tempList.get(1),person2OfGame/(person1OfGame+person2OfGame) * dizhuOfHave);
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


    /**
     * 流局针对金币场
     */
    protected void handleLiuju(){
        this.room = room;

        initCardNum = 17;//每人17张
        cards = new ArrayList<>();//牌
        disCards = new ArrayList<>();//丢弃的牌
        tableCards = new ArrayList<>();//底牌
        playerCardInfos = new HashMap<>();
        dizhu = -1;//地主
        chooseJiaoSet = new HashSet<>();//叫过地主的人
        chooseQiangSet = new HashSet<>();//抢过地主的人
        bujiaoSet = new HashSet<>();//不叫的集合
        buqiangSet = new HashSet<>();//不抢的集合

        lasttype = 0;//上一个人出牌的类型
        zhaCount = 0;//炸的个数
        multiple = 1;
        isSpring =false;//是否春天
        userPlayCount = new HashSet<>();
        isNMQiang = false;//农民是否抢过

        for (Long uid : users) {
            PlayerCardInfoDouDiZhu playerCardInfo = getGameTypePlayerCardInfo();
            playerCardInfo.userId = uid;
            playerCardInfos.put(uid, playerCardInfo);
        }

        shuffle();
        deal();
        //第一局 第一个玩家做地主
        long dizhuUser = room.getBankerId();
        dizhuUser = dizhuUser != 0 ? dizhuUser : users.get(0);
        chooseDizhu(dizhuUser);

        updateLastOperateTime();
        //通知其他人游戏已经开始
        MsgSender.sendMsg2Player(new ResponseVo("gameService", "gameBegin", "ok"), this.getUsers());
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


    /*
    ============================
    ========== Test ============
    ============================
    */
    public int sayHello() {

        logger.info("Just say hello!!!" );
        return 0;
    }
}
