package com.code.server.game.mahjong.logic;



import java.util.List;

/**
 * Created by win7 on 2017/2/24.
 */
public class GameInfoJinChengSS extends GameInfo {

    protected static final String SS_Ping_HAVEFENG_CANTING = "13051314";//13表示盛世，03表示无风（13表示有风），14表示需要听（04表示不需要听）平胡05  大胡 15
    protected static final String SS_Ping_HAVEFENG_NOTING = "13051304";
    protected static final String SS_Ping_NOFENG_CANTING = "13050314";
    protected static final String SS_Ping_NOFENG_NOTING = "13050304";
    protected static final String SS_DA_HAVEFENG_CANTING = "13151314";
    protected static final String SS_DA_HAVEFENG_NOTING = "13151304";
    protected static final String SS_DA_NOFENG_CANTING = "13150314";
    protected static final String SS_DA_NOFENG_NOTING = "13150304";
	

    /**
     * 初始化方法
     *
     * @param firstTurn
     * @param users
     */
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
        //不带风
        if ("3".equals(room.getMode()) || "4".equals(room.getMode()) || SS_Ping_NOFENG_CANTING.equals(room.getMode()) || SS_Ping_NOFENG_NOTING.equals(room.getMode())|| SS_DA_NOFENG_CANTING.equals(room.getMode())|| SS_DA_NOFENG_NOTING.equals(room.getMode())) {
            remainCards.removeAll(CardTypeUtil.FENG_CARD);
            remainCards.removeAll(CardTypeUtil.ZI_CARD);
        }
    }
    
}
