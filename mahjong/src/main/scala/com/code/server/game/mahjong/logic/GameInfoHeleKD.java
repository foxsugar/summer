package com.code.server.game.mahjong.logic;

import com.code.server.game.mahjong.util.HuWithHun;
import com.code.server.game.room.kafka.MsgSender;
import com.code.server.redis.service.RedisManager;

import java.util.List;
import java.util.Random;

import static com.code.server.game.mahjong.logic.GameInfoZhuohaozi.*;

/**
 * Created by sunxianping on 2018/6/25.
 */
public class GameInfoHeleKD extends GameInfoXYKD {




    public void init(int gameId, long firstTurn, List<Long> users, RoomInfo room) {
        this.gameId = gameId;

        this.firstTurn = firstTurn;
        this.turnId = firstTurn;
        remainCards.addAll(CardTypeUtil.ALL_CARD);
        this.users.addAll(users);
        this.room = room;
        this.cardSize = 13;
        this.playerSize = room.getPersonNumber();

        initHun();
        //不带风
        fapai();
    }


    protected void handleHuangzhuang(long userId) {

        turnResultToZeroOnHuangZhuang();
        sendResult(false, userId, null);
        noticeDissolutionResult();
        //通知所有玩家结束
        room.clearReadyStatus(true);
        //庄家换下个人
        if (room instanceof RoomInfo) {
            RoomInfo roomInfo = (RoomInfo) room;
            if (roomInfo.isChangeBankerAfterHuangZhuang()) {
                room.setBankerId(nextTurnId(room.getBankerId()));
            }

        }
//        if(HuUtil.getUserAndScore(this.room.getUserScores()).contains("否")){
//        	logger.info("<<<牌局得分>>>"+HuUtil.getUserAndScore(this.room.getUserScores()));
//        }
    }


    protected void turnResultToZeroOnHuangZhuang() {
        for (long i : room.getUserScores().keySet()) {
            if (room instanceof RoomInfoGoldHeLe) {
                RedisManager.getUserRedisService().addUserGold(i, -getPlayerCardsInfos().get(i).getScore());
            }
            room.setUserSocre(i, -getPlayerCardsInfos().get(i).getScore());
            if (this.getPlayerCardsInfos().get(i) != null) {
                this.getPlayerCardsInfos().get(i).setScore(0);
            }
        }
    }



    /**
     * 初始化混
     */
    public void initHun() {

        if (room.isHasMode(mode_不带耗子)) {
            return;
        }
        //随机混
        Random rand = new Random();
        int hunIndex = 0;
        if (PlayerCardsInfoMj.isHasMode(this.room.mode, mode_风耗子)) {
            hunIndex = 27 + rand.nextInt(7);
        }else{
            hunIndex = rand.nextInt(34);
        }

        if (PlayerCardsInfoMj.isHasMode(this.room.mode, mode_双耗子)) {
            this.hun = HuWithHun.getHunType(hunIndex);
        } else {
            this.hun.add(hunIndex);
        }

        //通知混
        MsgSender.sendMsg2Player("gameService", "noticeHun", this.hun, users);

    }


    @Override
    public int ting(long userId, String card) {

//        room.isHasMode(mode_明听);
//        String ifAnKou = room.getMode();
        if(!room.isHasMode(mode_明听)){
            tingAT(userId,card);
        }else {
            tingMT(userId,card);
        }
        return 0;
    }
}
