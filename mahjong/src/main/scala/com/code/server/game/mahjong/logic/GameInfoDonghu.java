package com.code.server.game.mahjong.logic;

import com.code.server.game.room.kafka.MsgSender;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sunxianping on 2017/10/10.
 */
public class GameInfoDonghu extends GameInfo {

    public static final int mode_大胡 = 0;
    public static final int mode_平胡 = 1;
    public static final int mode_抬庄 = 2;
    public static final int mode_后合 = 3;
    public static final int mode_四杠荒庄 = 4;

    public static final Map<Integer, Integer> gang_take_out_card = new HashMap<>();

    static {
        gang_take_out_card.put(0, 12);
        gang_take_out_card.put(1, 15);
        gang_take_out_card.put(2, 17);
        gang_take_out_card.put(3, 19);
        gang_take_out_card.put(4, 136);

    }

    @Override
    public void init(int gameId, long firstTurn, List<Long> users, RoomInfo room) {
        this.gameId = gameId;
        this.firstTurn = firstTurn;
        this.turnId = firstTurn;
        remainCards.addAll(CardTypeUtil.ALL_CARD);
        this.users.addAll(users);
        this.room = room;
        this.cardSize = 13;
        this.playerSize = room.getPersonNumber();
        fapai();
    }

    @Override
    protected void handleHuangzhuang(long userId) {
        sendResult(false, userId);
        noticeDissolutionResult();
        //通知所有玩家结束
        room.clearReadyStatus();
        //庄家换下个人
//        if (room instanceof RoomInfo) {
//            RoomInfo roomInfo = (RoomInfo) room;
//            room.setBankerId(nextTurnId(room.getBankerId()));


//        }
    }

    /**
     * 是否荒庄
     *
     * @param playerCardsInfo
     * @return
     */
    protected boolean isHuangzhuang(PlayerCardsInfoMj playerCardsInfo) {
        int remain = this.remainCards.size();
        int maxRemainCard = 0;
        if (PlayerCardsInfoMj.isHasMode(this.room.mode, mode_四杠荒庄)) {
            int gangCount = this.playerCardsInfos.values().stream().mapToInt(p ->
                    p.mingGangType.size() + p.anGangType.size()
            ).sum();
            maxRemainCard = gang_take_out_card.get(gangCount);
            //小于牌剁的数量
        }
        return remain <= maxRemainCard;
    }


    @Override
    public int chupai(long userId, String card) {
        int rtn = super.chupai(userId, card);

        boolean isBanker = this.getFirstTurn() == userId;
        //铲
        int chanSize = chanCards.size();
        if ((isBanker && chanSize == 0) || (chanSize > 0 && chanSize < 4)) {

            chanCards.add(card);
            //达成台庄
            if (chanCards.size() >= 4 && isCardSame(chanCards.subList(0, 4))) {
                //换庄家
                if (PlayerCardsInfoMj.isHasMode(this.room.mode, mode_抬庄)) {
                    long newBanker = nextTurnId(this.firstTurn);
                    this.setFirstTurn(newBanker);
                    this.room.setBankerId(newBanker);
                    Map<String, Object> r = new HashMap<>();
                    r.put("banker", this.getFirstTurn());
                    MsgSender.sendMsg2Player("gameService", "noticeTaizhuang", r, users);
                }

                //如果 后合模式 直接荒庄
                if (PlayerCardsInfoMj.isHasMode(this.room.mode, mode_后合)) {
                    //输给每人三分
//                    handle_houhe(this.firstTurn);
                    //荒庄
                    handleHuangzhuang(userId);
                }

            }
        }

        return rtn;
    }

    private void handle_houhe(long userId) {
        int sub = 0;
        int s = 3;
        for (PlayerCardsInfoMj playerCardsInfo : this.playerCardsInfos.values()) {
            if (playerCardsInfo.getUserId() != userId) {
                playerCardsInfo.addScore(s);
                this.room.addUserSocre(playerCardsInfo.getUserId(), s);
                sub += s;
            }
        }
        this.playerCardsInfos.get(userId).addScore(-sub);
        this.room.addUserSocre(userId, -sub);
    }
}
