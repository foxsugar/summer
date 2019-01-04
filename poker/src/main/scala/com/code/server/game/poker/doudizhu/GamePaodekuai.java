package com.code.server.game.poker.doudizhu;

import com.code.server.constant.data.DataManager;
import com.code.server.constant.data.StaticDataProto;
import com.code.server.constant.game.CardStruct;
import com.code.server.constant.response.ErrorCode;
import com.code.server.constant.response.ResponseVo;
import com.code.server.game.room.Room;
import com.code.server.game.room.kafka.MsgSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by sunxianping on 2018-12-21.
 */
public class GamePaodekuai extends GameDouDiZhu {

    protected static final int mode_底分05 = 1;
    protected static final int mode_底分1 = 2;
    protected static final int mode_底分2 = 3;
    protected static final int mode_底分3 = 4;
    protected static final int mode_单圈最大炸弹算分 = 5;
    protected static final int mode_单圈每个炸弹算分 = 6;
    protected static final int mode_红桃3先出 = 7;
    protected static final int mode_赢家先出 = 8;
    protected static final int mode_打完洗牌 = 9;
    protected static final int mode_不洗牌 = 10;
    protected static final int mode_连对2对起 = 11;
    protected static final int mode_连对3对起 = 12;
    protected static final int mode_炸弹底分5 = 13;
    protected static final int mode_炸弹底分10 = 14;
    protected static final int mode_炸弹底分20 = 15;
    protected static final int mode_炸弹底分30 = 16;

    List<Long> playList = new ArrayList<>();
    int curCircleZhaCount = 0;


    public void startGame(List<Long> users, Room room) {
        this.room = (RoomDouDiZhu)room;
        init(users, room.getBankerId());
        updateLastOperateTime();
        //通知其他人游戏已经开始
//        MsgSender.sendMsg2Player(new ResponseVo("gameService", "gameBegin", "ok"), this.getUsers());
    }

    public void init(List<Long> users, long dizhuUser) {
        //没人16张牌
        initCardNum = 16;
        //初始化玩家
        for (Long uid : users) {
            PlayerCardInfoDouDiZhu playerCardInfo = getGameTypePlayerCardInfo();
            playerCardInfo.userId = uid;
            playerCardInfos.put(uid, playerCardInfo);
        }
        this.users.addAll(users);



        shuffle();
        handleNoShuffle();
        deal();
        //第一局 第一个玩家做地主
        dizhuUser = dizhuUser != 0 ? dizhuUser : users.get(0);
//        chooseDizhu(dizhuUser);

        if (isHasModel(mode_红桃3先出)) {

            dizhuUser = getHas3User();
        }

        startPlay(dizhuUser);
    }


    protected void handleBomb(CardStruct cardStruct) {


        long userId = cardStruct.getUserId();
        if (cardStruct.getType() == CardStruct.type_炸) {

            if (isHasModel(mode_单圈最大炸弹算分)) {
                //判断这圈是否结束
                curCircleZhaCount++;

            }else{
                PlayerCardPaodekuai playerCardPaodekuai = (PlayerCardPaodekuai) playerCardInfos.get(userId);
                playerCardPaodekuai.setZhaCount(playerCardPaodekuai.getZhaCount() + 1);
                this.room.getRoomStatisticsMap().get(userId).zhaCount += 1;
            }
        }

        if (zhaCount < room.getMultiple() || room.getMultiple() == -1) {
            if (cardStruct.getType() == CardStruct.type_炸) {
                List<Integer> cards = cardStruct.getCards();
                zhaCount += 1;//记录炸的数量
                multiple *= 2;//记录倍数
            } else if (cardStruct.getType() == CardStruct.type_火箭) {
                zhaCount += 1;//记录炸的数量
                multiple *= 2;//记录倍数
            }
        }
    }


    protected void handleBomb2People(long userId){
        if(isHasModel(mode_单圈每个炸弹算分)) return;
        //判断是不是一圈了
        if (playList.size() >= 1 && curCircleZhaCount>0) {
            //
            if (userId == playList.get(playList.size() - 1)) {
                PlayerCardInfoDouDiZhu playerCardPaodekuai =  playerCardInfos.get(userId);
                playerCardPaodekuai.setZhaCount(playerCardPaodekuai.getZhaCount() + 1);
                this.room.getRoomStatisticsMap().get(userId).zhaCount += 1;

                this.curCircleZhaCount = 0;
            }
        }
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

        //记录这局的出牌
        putCard2LastGameCards(cardStruct.cards);

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

        //打牌列表 加上玩家
        this.playList.add(userId);
        //炸是否加到个人身上
        handleBomb2People(userId);
        //处理炸
        handleBomb(cardStruct);



        //回放
        replay.getOperate().add(Operate.getOperate_PLAY(userId, cardStruct, false));

        //牌打完
        if (playerCardInfo.cards.size() == 0) {
            PlayerCardInfoDouDiZhu playerCardInfoDizhu = playerCardInfos.get(dizhu);
            //是否是春天

            if (userPlayCount.size() == 1 || playerCardInfoDizhu.getPlayCount() == 1) {
                isSpring = true;
                //炸 封顶的情况
//                if (zhaCount < room.getMultiple() || room.getMultiple() == -1) {
//                    multiple *= 2;
//                }
            }

            compute(userId);

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
//        userId.sendMsg("gameService", "play", 0);
        updateLastOperateTime();
        return 0;
    }



    protected void compute(long winnerId) {

        double subScore = 0;

        for (PlayerCardInfoDouDiZhu playerCardInfo : playerCardInfos.values()) {
            //不是地主 扣分
            if (winnerId != playerCardInfo.getUserId()) {

                double score = getDifen() * playerCardInfo.getCards().size() ;
                if (isSpring) {
                    score *= 2;
                }
                score += playerCardInfo.getZhaCount() * getZhaDifen();

                if (playerCardInfo.getCards().size() == 1) {
                    score = 0;
                }

                subScore += score;
                playerCardInfo.setScore(-score);
                room.addUserSocre(playerCardInfo.getUserId(), -score);

                //手里的牌 加到不洗牌中
                //记录这局的出牌
                putCard2LastGameCards(playerCardInfo.cards);
            }
        }

        PlayerCardInfoDouDiZhu winner = playerCardInfos.get(winnerId);
        winner.setScore(subScore);
        room.addUserSocre(winnerId, subScore);

    }


    private double getDifen() {
        if (isHasModel(mode_底分05)) {
            return 0.5;
        }
        if (isHasModel(mode_底分1)) {
            return 1;
        }
        if (isHasModel(mode_底分2)) {
            return 2;
        }
        if (isHasModel(mode_底分3)) {
            return 3;
        }
        return 1;
    }

    private int getZhaDifen() {
        if (isHasModel(mode_炸弹底分5)) {
            return 5;
        }
        if (isHasModel(mode_炸弹底分10)) {
            return 10;
        }
        if (isHasModel(mode_炸弹底分20)) {
            return 20;
        }
        if (isHasModel(mode_炸弹底分30)) {
            return 30;
        }
        return 5;
    }
    /**
     * 得到有红桃3的玩家
     * @return
     */
    private long getHas3User() {
        for (PlayerCardInfoDouDiZhu playerCardInfoDouDiZhu : playerCardInfos.values()) {
            //红桃3
            if (playerCardInfoDouDiZhu.cards.contains(10)) {
                return playerCardInfoDouDiZhu.userId;
            }
        }
        return 0;
    }



    protected boolean isNoShuffle(){
        return isHasModel(mode_不洗牌);
    }

    public static void main(String[] args) {
        System.out.println(Room.isHasMode(1,10914));
    }
    /**
     * 开始打牌
     *
     * @param dizhu
     */
    protected void startPlay(long dizhu) {
        playStepStart(dizhu);


        doAfterStart();
    }

    /**
     * 洗牌
     */
    protected void shuffle() {
        for (int i = 1; i <= 54; i++) {
            cards.add(i);
        }
        cards.remove((Integer) 53);
        cards.remove((Integer) 54);
        cards.remove((Integer) 6);
        cards.remove((Integer) 7);
        cards.remove((Integer) 8);
        cards.remove((Integer) 4);

        Collections.shuffle(cards);

    }
}
