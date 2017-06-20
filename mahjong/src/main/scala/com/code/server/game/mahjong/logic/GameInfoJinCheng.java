package com.code.server.game.mahjong.logic;



import java.util.List;

/**
 * Created by win7 on 2017/2/24.
 */
public class GameInfoJinCheng extends GameInfo {

    protected static final String JC_Ping_HAVEFENG_CANTING = "12051314";//12表示进城，03表示无风（13表示有风），14表示需要听（04表示不需要听）平胡05  大胡 15
    protected static final String JC_Ping_HAVEFENG_NOTING = "12051304";
    protected static final String JC_Ping_NOFENG_CANTING = "12050314";
    protected static final String JC_Ping_NOFENG_NOTING = "12050304";
    protected static final String JC_DA_HAVEFENG_CANTING = "12151314";
    protected static final String JC_DA_HAVEFENG_NOTING = "12151304";
    protected static final String JC_DA_NOFENG_CANTING = "12150314";
    protected static final String JC_DA_NOFENG_NOTING = "12150304";
	
    
    /**
     * 初始化方法
     *  @param firstTurn
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
        if ("3".equals(room.getMode()) || "4".equals(room.getMode()) || JC_Ping_NOFENG_CANTING.equals(room.getMode()) || JC_Ping_NOFENG_NOTING.equals(room.getMode())|| JC_DA_NOFENG_CANTING.equals(room.getMode())|| JC_DA_NOFENG_NOTING.equals(room.getMode())) {
            remainCards.removeAll(CardTypeUtil.FENG_CARD);
            remainCards.removeAll(CardTypeUtil.ZI_CARD);
        }
    }
    
    /**
     * 荒庄的处理
     *
     * @param userId
     */
    protected void handleHuangzhuang(long userId) {

        turnResultToZeroOnHuangZhuang();
        //平胡

        handleHuangzhuangScore();

        sendResult(false, userId);
        /*room.addOneToCircleNumber();
        int nextId = nextTurnId(this.getFirstTurn());
        room.setBankerId(nextId);*/
        noticeDissolutionResult();
        //通知所有玩家结束
        room.clearReadyStatus();

    }

    private void handleHuangzhuangScore(){
        for (long i : room.getUserScores().keySet()) {

            PlayerCardsInfoMj playerCardsInfo = this.getPlayerCardsInfos().get(i);
            if (playerCardsInfo == null) {
                return;
            }
            if (playerCardsInfo.getUserId() == this.firstTurn) {
                playerCardsInfo.setScore(-6);
                room.setUserSocre(i, -6);
            } else {
                playerCardsInfo.setScore(2);
                room.setUserSocre(i, 2);
            }
        }
    }

    protected void handleHu(PlayerCardsInfoMj playerCardsInfo) {
        isAlreadyHu = true;
        sendResult(true, playerCardsInfo.userId);
        //圈
        if (this.getFirstTurn() != playerCardsInfo.getUserId()) {
            //换庄
            room.addOneToCircleNumber();
            long nextId = nextTurnId(this.getFirstTurn());
            room.setBankerId(nextId);
        }
        noticeDissolutionResult();
        room.clearReadyStatus();
    }

    protected boolean isRoomOver() {
        return room.getCurCircle() > room.maxCircle;
    }
}
