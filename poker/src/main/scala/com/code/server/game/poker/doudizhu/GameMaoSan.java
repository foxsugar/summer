package com.code.server.game.poker.doudizhu;

import com.code.server.constant.data.DataManager;
import com.code.server.constant.data.StaticDataProto;
import com.code.server.constant.game.CardStruct;
import com.code.server.constant.response.ErrorCode;
import com.code.server.constant.response.ResponseVo;
import com.code.server.game.room.kafka.MsgSender;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sunxianping on 2018/4/23.
 */
public class GameMaoSan extends GameDouDiZhuMaoSan {

    public static final Integer HONGTAO4 = 14;

    public void init(List<Long> users, long dizhuUser) {
        initCardNum = 13;
        super.init(users, dizhuUser);
    }


    /**
     * 发牌
     */
    protected void deal() {
        cards.remove(Integer.valueOf(10));
        cards.remove(Integer.valueOf(54));




        RoomDouDiZhu roomDouDiZhu = (RoomDouDiZhu) room;
        if (roomDouDiZhu.testNextCards != null) {
            testDeal();
        } else {
            for (PlayerCardInfoDouDiZhu playerCardInfo : playerCardInfos.values()) {
                for (int i = 0; i < this.initCardNum; i++) {
                    playerCardInfo.cards.add(cards.remove(0));
                }
                //通知发牌
                MsgSender.sendMsg2Player(new ResponseVo("gameService", "deal", playerCardInfo.cards), playerCardInfo.userId);
            }
        }
        //底牌
        tableCards.add(10);
        tableCards.add(54);

    }




    /**
     * 叫地主
     *
     * @param userId
     * @param isJiao
     * @return
     */
    public int jiaoDizhu(long userId, boolean isJiao, int score) {

        logger.info(userId + "  叫地主 " + isJiao);
        if (canJiaoUser != userId) {
            return ErrorCode.CAN_NOT_JIAO_TURN;
        }
        if (isJiao && score <= tableScore) {
            return ErrorCode.CAN_NOT_JIAO_SCORE;
        }
        //叫地主列表
        chooseJiaoSet.add(userId);

        //不叫 下个人能叫
        if (!isJiao) {
            bujiaoSet.add(userId);
            if (chooseJiaoSet.size() >= users.size()) {
                //曾经有人叫过
                if (dizhu != -1) {
                    //推送选定地主
//                    qiangStepStart();
                    chooseDizhu();
                    startPlay(dizhu);


                } else {
                    handleLiuju();
                }
            } else {
                long nextJiao = nextTurnId(userId);
                canJiaoUser = nextJiao;
                noticeCanJiao(nextJiao);
            }
        } else {//叫了 开始抢
            jiaoUser = userId;
            dizhu = userId;
            tableScore = score;
            //第三个人叫的 直接开始游戏
            RoomDouDiZhu r = (RoomDouDiZhu) this.room;
            //已经叫到最大分(默认三分,毛三有选项)
            if (score == r.jiaoScoreMax) {
                //推送选定地主
                qiangStepStart();
            } else {
                long nextJiao = nextTurnId(userId);
                canJiaoUser = nextJiao;
                noticeCanJiao(nextJiao);
            }

        }

        Map<String, Object> rs = new HashMap<>();
        rs.put("userId", userId);
        rs.put("isJiao", isJiao);
        rs.put("score", score);
        MsgSender.sendMsg2Player("gameService", "jiaoResponse", rs, users);

        MsgSender.sendMsg2Player("gameService", "jiaoDizhu", 0, userId);

        updateLastOperateTime();


        //回放
        replay.getOperate().add(Operate.getOperate_JDZ(userId, score, !isJiao));

        return 0;
    }


    /**
     * 抢地主
     *
     * @param userId
     * @param isQiang
     * @return
     */
    public int qiangDizhu(long userId, boolean isQiang) {
        logger.info(userId + "  抢地主 " + isQiang);

        if (userId != canQiangUser) {
            return ErrorCode.CAN_NOT_QIANG_TURN;
        }
        this.chooseQiangSet.add(userId);
        if (!isQiang) {
            this.buqiangSet.add(userId);
        }

        PlayerCardInfoDouDiZhu playerCardInfo = playerCardInfos.get(userId);
        playerCardInfo.setQiang(isQiang);

        //回执
        Map<String, Object> rs = new HashMap<>();
        rs.put("userId", userId);
        rs.put("isQiang", isQiang);
        MsgSender.sendMsg2Player("gameService", "qiangResponse", rs, users);
        MsgSender.sendMsg2Player("gameService", "qiangDizhu", 0, userId);

        //两个农民都没抢
        boolean allNoQiang = buqiangSet.size() == 3 && !isQiang;
        //开始游戏
        if (chooseQiangSet.size() == 4 || allNoQiang) {
            startPlay(dizhu);
        } else {
            canQiangUser = nextTurnId(userId);
            noticeCanQiang(canQiangUser);
        }


        updateLastOperateTime();
        //回放
        replay.getOperate().add(Operate.getOperate_QDZ(userId, !isQiang));
        return 0;
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


        //回放
        replay.getOperate().add(Operate.getOperate_PLAY(userId, cardStruct, false));

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

            //下局是否是赢得人做地主
            boolean isWinner2Dizhu = false;
            StaticDataProto.RoomData roomData = DataManager.data.getRoomDataMap().get(this.room.getGameType());
            if (roomData != null) {
                isWinner2Dizhu = DataManager.data.getRoomDataMap().get(this.room.getGameType()).getIsWinner2Dizhu() == 1;
            }
            if (isWinner2Dizhu) {
                this.room.setBankerId(userId);
            }

            room.clearReadyStatus(true);

            sendFinalResult();

        }
        MsgSender.sendMsg2Player("gameService", "play", 0, userId);
        updateLastOperateTime();
        return 0;
    }

    public static void main(String[] args) {
        System.out.println(1%0);
    }
}
