package com.code.server.game.poker.hitgoldflower;


import com.code.server.constant.response.*;
import com.code.server.game.room.Game;
import com.code.server.game.room.Room;
import com.code.server.game.room.kafka.MsgSender;
import com.code.server.game.room.service.RoomManager;
import com.code.server.util.IdWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by sunxianping on 2017/3/13.
 */
public class GameHitGoldFlower extends Game {
    protected static final Logger logger = LoggerFactory.getLogger(GameHitGoldFlower.class);

    private static final Double INIT_BOTTOM_CHIP = 1.0;//底注
    private static final int INIT_CARD_NUM = 3;//玩家牌数3张
    private static final Double MAX_BET_NUM = 1000.0;//最大投注数

    protected List<Integer> cards = new ArrayList<>();//牌
    protected Map<Long, PlayerCardInfoHitGoldFlower> playerCardInfos = new HashMap<>();
    protected Random rand = new Random();

    private int curRoundNumber=1;//当前轮数
    protected Double chip = INIT_BOTTOM_CHIP;

    protected List<Integer> leaveCards = new ArrayList<>();//剩余的牌，暂时无用
    protected List<Long> aliveUser = new ArrayList<>();//存活的人
    protected List<Long> seeUser = new ArrayList<>();//看牌的人
    protected Long curUserId;


    protected Room room;

    protected long lastOperateTime;


    public void init(List<Long> users) {
        //初始化玩家
        for (Long uid : users) {
            PlayerCardInfoHitGoldFlower playerCardInfo = getGameTypePlayerCardInfo();
            playerCardInfo.userId = uid;
            playerCardInfos.put(uid, playerCardInfo);
        }
        this.users.addAll(users);


        shuffle();//洗牌
        deal();//发牌
        mustBet();
        curUserId = room.getBankerId();

        noticeAction(curUserId);
        updateLastOperateTime();
    }

    /**
     * 必须下底注
     */
    private void mustBet(){
        for (Long l : playerCardInfos.keySet()) {
            playerCardInfos.get(l).setScore(-1);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("mustBet",chip);
        ResponseVo vo = new ResponseVo("gameService", "mustBet", result);
        MsgSender.sendMsg2Player(vo, users);
    }



    /**
     * 加注
     * @return
     */
    public int raise(long userId,double addChip){
        logger.info(userId +"  下注: "+ addChip);

        if (userId!=curUserId) {//判断是否到顺序
            return ErrorCode.NOT_YOU_TURN;
        }

        if(seeUser.contains(userId)){
            if(addChip!=chip*2+2 && addChip!=chip*2*2 && addChip!=chip*2*4){
                return ErrorCode.BET_WRONG;
            }
        }else{
            if(addChip!=chip+2 && addChip!=chip*2 && addChip!=chip*4){
                return ErrorCode.BET_WRONG;
            }
        }

        playerCardInfos.get(userId).setScore(playerCardInfos.get(userId).getScore()-addChip);
        Map<String, Object> result = new HashMap<>();
        result.put("userId",userId);
        result.put("addChip",playerCardInfos.get(userId).getScore());
        ResponseVo vo = new ResponseVo("gameService", "raise", result);
        MsgSender.sendMsg2Player(vo, users);

        noticeAction(curUserId);
        updateLastOperateTime();

        return 0;
    }

    /**
     * 跟注
     * @return
     */
    public int call(long userId){

        logger.info(userId +"  跟注: "+ chip);

        if (userId!=curUserId) {//判断是否到顺序
            return ErrorCode.NOT_YOU_TURN;
        }

        if(seeUser.contains(userId)){
            playerCardInfos.get(userId).setScore(playerCardInfos.get(userId).getScore()-chip*2);
        }else{
            playerCardInfos.get(userId).setScore(playerCardInfos.get(userId).getScore()-chip);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("userId",userId);
        result.put("chip",playerCardInfos.get(userId).getScore());
        ResponseVo vo = new ResponseVo("gameService", "call", result);
        MsgSender.sendMsg2Player(vo, users);

        noticeAction(curUserId);
        updateLastOperateTime();

        return 0;
    }


    /**
     * 弃牌
     * @return
     */
    public int fold(long userId){

        logger.info(userId +"  弃牌!!!");

        aliveUser.remove(userId);

        Map<String, String> result = new HashMap<>();
        result.put("result","fold success!!");
        ResponseVo vo = new ResponseVo("gameService", "fold", result);
        MsgSender.sendMsg2Player(vo, users);

        noticeAction(userId);
        updateLastOperateTime();

        return 0;
    }

    /**
     * 看牌
     * @return
     */
    public int see(long userId){

        logger.info(userId +"  看牌"+playerCardInfos.get(userId).getHandcards());

        if (curRoundNumber<room.getMenPai()) {
            return ErrorCode.NOT_GET_MEMPAI;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("cards",playerCardInfos.get(userId).getHandcards());
        ResponseVo vo = new ResponseVo("gameService", "see", result);
        MsgSender.sendMsg2Player(vo, users);

        noticeActionSelf(userId);
        updateLastOperateTime();

        return 0;
    }

    /**
     * 比牌
     * @return
     */
    public int kill(long askerId,long accepterId){

        logger.info(askerId +"  比牌: "+ chip);

        if (!aliveUser.contains(askerId)||!aliveUser.contains(accepterId)) {
            return ErrorCode.NOT_KILL;
        }
        //Todo 拒绝了，不能看

        if(seeUser.contains(askerId)){
            playerCardInfos.get(askerId).setScore(playerCardInfos.get(askerId).getScore()+chip*2);
        }else{
            playerCardInfos.get(askerId).setScore(playerCardInfos.get(askerId).getScore()+chip);
        }

        Player asker = new Player(askerId, playerCardInfos.get(askerId).getHandcards().get(0), playerCardInfos.get(askerId).getHandcards().get(1), playerCardInfos.get(askerId).getHandcards().get(2));
        Player accepter = new Player(accepterId, playerCardInfos.get(accepterId).getHandcards().get(0), playerCardInfos.get(accepterId).getHandcards().get(1), playerCardInfos.get(accepterId).getHandcards().get(2));

        ArrayList<Player> winnerList = Player.findWinners(asker,accepter);

        Long winnerId = winnerList.size()==1?winnerList.get(0).getUid():winnerList.get(1).getUid();

        Map<String, Long> result = new HashMap<>();
        result.put("winnerId",winnerId);
        result.put("loserId",winnerId==askerId?accepterId:askerId);
        ResponseVo vo = new ResponseVo("gameService", "kill", result);
        MsgSender.sendMsg2Player(vo, users);

        noticeAction(curUserId);
        updateLastOperateTime();

        return 0;
    }


    //=====================================
    //==============结束操作================
    //=====================================

    /**
     * 算分
     * @param winList
     */
    protected void compute(ArrayList<Long> winList) {
        RoomHitGoldFlower roomHitGoldFlower = null;
        if(room instanceof RoomHitGoldFlower){
            roomHitGoldFlower = (RoomHitGoldFlower)room;
        }
        //设置每个人的牌类型
        for (PlayerCardInfoHitGoldFlower playerCardInfo : playerCardInfos.values()) {
            Player p = new Player(playerCardInfo.getUserId(), playerCardInfo.getHandcards().get(0), playerCardInfo.getHandcards().get(1), playerCardInfo.getHandcards().get(2));
            playerCardInfo.setCardType(p.getCategory().toString());
            playerCardInfo.setCardType(PokerItem.is235(p.getPokers())?"BaoZiShaShou":"DanZi");
            //添加次数
            if("BaoZi".equals(playerCardInfo.getCardType())){
                roomHitGoldFlower.addBaoziNum(playerCardInfo.getUserId());
            }else if("ShunJin".equals(playerCardInfo.getCardType())){
                roomHitGoldFlower.addTonghuashunNum(playerCardInfo.getUserId());
            }else if("JinHua".equals(playerCardInfo.getCardType())){
                roomHitGoldFlower.addTonghuaNum(playerCardInfo.getUserId());
            }else if("ShunZi".equals(playerCardInfo.getCardType())){
                roomHitGoldFlower.addShunziNum(playerCardInfo.getUserId());
            }else if("DuiZi".equals(playerCardInfo.getCardType())){
                roomHitGoldFlower.addDuiziNum(playerCardInfo.getUserId());
            }else if("DanZi".equals(playerCardInfo.getCardType())){
                roomHitGoldFlower.addSanpaiNum(playerCardInfo.getUserId());
            }
        }
        //添加彩分
        for (PlayerCardInfoHitGoldFlower playerCardInfo : playerCardInfos.values()) {
            if("BaoZi".equals(playerCardInfo.getCardType())){
                double tempCaifen = 0.0;
                for (PlayerCardInfoHitGoldFlower p : playerCardInfos.values()) {
                    if(playerCardInfo.getUserId()!=p.getUserId()){
                        tempCaifen+=room.getCaiFen();
                        p.setCaifen(p.getCaifen()-room.getCaiFen());
                    }
                }
                playerCardInfo.setCaifen(playerCardInfo.getCaifen()+tempCaifen);
            }
        }

        //算分
        double totalChip = 0.0;
        for (PlayerCardInfoHitGoldFlower playerCardInfo : playerCardInfos.values()) {
            if(!winList.contains(playerCardInfo.getUserId())){
                totalChip+=playerCardInfo.getScore();
            }
        }
        for (PlayerCardInfoHitGoldFlower playerCardInfo : playerCardInfos.values()) {
            if(winList.contains(playerCardInfo.getUserId())){
                playerCardInfo.setScore(-totalChip/winList.size());
            }
        }
        for (PlayerCardInfoHitGoldFlower playerCardInfo : playerCardInfos.values()) {
            room.addUserSocre(playerCardInfo.getUserId(),playerCardInfo.getScore());
            room.addUserSocre(playerCardInfo.getUserId(),playerCardInfo.getCaifen());
        }
    }

    /**
     * 发送战绩
     */
    protected void sendResult() {
        GameResultHitGoldFlower gameResultHitGoldFlower = new GameResultHitGoldFlower();
        for (PlayerCardInfoHitGoldFlower playerCardInfo : playerCardInfos.values()) {
            gameResultHitGoldFlower.getPlayerCardInfos().add(playerCardInfo.toVo());
        }

        gameResultHitGoldFlower.getUserScores().putAll(this.room.getUserScores());
        MsgSender.sendMsg2Player("gameService", "gameResult", gameResultHitGoldFlower, users);
    }

    /**
     * 战绩
     */
    protected void genRecord() {
        long id = IdWorker.getDefaultInstance().nextId();
        genRecord(playerCardInfos.values().stream().collect
                (Collectors.toMap(PlayerCardInfoHitGoldFlower::getUserId, PlayerCardInfoHitGoldFlower::getAllScore)), room, id);
    }

    /**
     * 最后结算
     */
    protected void sendFinalResult() {
        //所有牌局都结束
        if (room.getCurGameNumber() > room.getGameNumber()) {
            List<UserOfResult> userOfResultList = this.room.getUserOfResult();
            // 存储返回
            GameOfResult gameOfResult = new GameOfResult();
            gameOfResult.setUserList(userOfResultList);
            MsgSender.sendMsg2Player("gameService", "gameFinalResult", gameOfResult, users);

            RoomManager.removeRoom(room.getRoomId());

            //战绩
            this.room.genRoomRecord();

        }
    }

    //===========================================
    //==============以下为通知代码================
    //===========================================

    /**
     * 通知操作按钮(本轮的人)
     *
     * @param userId
     */
    protected void noticeActionSelf(long userId) {

        /**
         protected String call = "1";//跟注
         protected String raise = "0";//加注  ===
         protected String fold = "1";//弃牌
         protected String kill = "1";//比牌
         protected String see = "0";//看牌    ===
         */
        PlayerCardInfoHitGoldFlower playerCardInfo = playerCardInfos.get(userId);
        if(seeUser.contains(userId)){
            playerCardInfo.setSee("0");
        }
        if(seeUser.contains(userId)){
            if(chip>=MAX_BET_NUM){
                playerCardInfo.setRaise("0");
            }
        }else{
            if(chip>=MAX_BET_NUM/2){
                playerCardInfo.setRaise("0");
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("playerCardInfo", playerCardInfo);
        result.put("chip", chip);
        ResponseVo vo = new ResponseVo("gameService", "noticeActionSelf", result);
        MsgSender.sendMsg2Player(vo, users);
    }

    /**
     * 通知操作按钮(下一个)
     *
     * @param userId
     */
    protected void noticeAction(long userId) {
        curUserId = nextActioner(userId);
        /**
         protected String call = "1";//跟注
         protected String raise = "0";//加注  ===
         protected String fold = "1";//弃牌
         protected String kill = "1";//比牌
         protected String see = "0";//看牌    ===
         */
        PlayerCardInfoHitGoldFlower playerCardInfo = playerCardInfos.get(curUserId);
        if(seeUser.contains(curUserId)){
            playerCardInfo.setSee("0");
        }
        if(seeUser.contains(curUserId)){
            if(chip>=MAX_BET_NUM){
                playerCardInfo.setRaise("0");
            }
        }else{
            if(chip>=MAX_BET_NUM/2){
                playerCardInfo.setRaise("0");
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("playerCardInfo", playerCardInfo);
        result.put("chip", chip);
        ResponseVo vo = new ResponseVo("gameService", "noticeAction", result);
        MsgSender.sendMsg2Player(vo, users);
    }





    //===========================================
    //==============以下为准备代码================
    //===========================================

    public PlayerCardInfoHitGoldFlower getGameTypePlayerCardInfo() {
        switch (room.getGameType()) {
            case "1":
                return new PlayerCardInfoHitGoldFlower();
            default:
                return new PlayerCardInfoHitGoldFlower();
        }
    }


    /**
     * 洗牌
     */
    protected void shuffle() {
        for (int i = 2; i <= 54; i++) {
            cards.add(i);
        }
        Collections.shuffle(cards);
    }

    /**
     * 发牌
     */
    protected void deal() {
        for (PlayerCardInfoHitGoldFlower playerCardInfo : playerCardInfos.values()) {
            for (int i = 0; i < INIT_CARD_NUM; i++) {
                playerCardInfo.handcards.add(cards.remove(0));
            }
            //通知发牌
            MsgSender.sendMsg2Player(new ResponseVo("gameService", "deal", playerCardInfo.handcards), playerCardInfo.userId);
        }

        //底牌
        leaveCards.addAll(cards);
    }

    /**
     * 下一个操作人
     * @param curId
     * @return
     */
    protected Long nextActioner(long curId){
        int index = aliveUser.indexOf(curId);

        int nextId = index + 1;
        if (nextId >= aliveUser.size()) {
            nextId = 0;
        }
        //加圈数
        if(aliveUser.get(nextId).equals(room.getBankerId())){
            curRoundNumber+=1;
        }
        if(curRoundNumber>room.getCricleNumber()){
            campareAllCards();
        }
        return aliveUser.get(nextId);
    }


    /**
     * 比较所有人牌型
     * @param
     * @return
     */
    protected void campareAllCards() {
        ArrayList<Player> list = new ArrayList<>();
        ArrayList<Long> winList = new ArrayList<>();
        ArrayList<Player> winnerList = null;
        for (Long l : aliveUser) {
            Player p = new Player(l, playerCardInfos.get(l).getHandcards().get(0), playerCardInfos.get(l).getHandcards().get(1), playerCardInfos.get(l).getHandcards().get(2));
            list.add(p);
        }
        if (list.size() == 5) {
            winnerList = Player.findWinners(list.get(0), list.get(1), list.get(2), list.get(3), list.get(4));
        } else if (list.size() == 4) {
            winnerList = Player.findWinners(list.get(0), list.get(1), list.get(2), list.get(3));
        } else if (list.size() == 3) {
            winnerList = Player.findWinners(list.get(0), list.get(1), list.get(2));
        } else if (list.size() == 2) {
            winnerList = Player.findWinners(list.get(0), list.get(1));
        } else {
            winList.add(list.get(0).getUid());
        }
        for (Player p : winnerList) {
            winList.add(p.getUid());
        }
        Map<String, Object> result = new HashMap<>();
        result.put("winList", winList);
        ResponseVo vo = new ResponseVo("gameService", "campareAllCards", result);
        MsgSender.sendMsg2Player(vo, users);

        //处理结果
        compute(winList);
        sendResult();
        genRecord();

        room.clearReadyStatus(true);
        sendFinalResult();
    }

    //更新操作时间
    protected void updateLastOperateTime() {
        this.lastOperateTime = System.currentTimeMillis();
    }


    //===========================================
    //==============get，set================
    //===========================================


    public Random getRand() {
        return rand;
    }

    public void setRand(Random rand) {
        this.rand = rand;
    }

    public int getCurRoundNumber() {
        return curRoundNumber;
    }

    public void setCurRoundNumber(int curRoundNumber) {
        this.curRoundNumber = curRoundNumber;
    }

    public Double getChip() {
        return chip;
    }

    public void setChip(Double chip) {
        this.chip = chip;
    }

    public List<Integer> getLeaveCards() {
        return leaveCards;
    }

    public void setLeaveCards(List<Integer> leaveCards) {
        this.leaveCards = leaveCards;
    }

    public List<Long> getAliveUser() {
        return aliveUser;
    }

    public void setAliveUser(List<Long> aliveUser) {
        this.aliveUser = aliveUser;
    }

    public List<Long> getSeeUser() {
        return seeUser;
    }

    public void setSeeUser(List<Long> seeUser) {
        this.seeUser = seeUser;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public List<Integer> getCards() {
        return cards;
    }

    public void setCards(List<Integer> cards) {
        this.cards = cards;
    }

    public Map<Long, PlayerCardInfoHitGoldFlower> getPlayerCardInfos() {
        return playerCardInfos;
    }

    public void setPlayerCardInfos(Map<Long, PlayerCardInfoHitGoldFlower> playerCardInfos) {
        this.playerCardInfos = playerCardInfos;
    }

    public Long getCurUserId() {
        return curUserId;
    }

    public void setCurUserId(Long curUserId) {
        this.curUserId = curUserId;
    }

    public long getLastOperateTime() {
        return lastOperateTime;
    }

    public void setLastOperateTime(long lastOperateTime) {
        this.lastOperateTime = lastOperateTime;
    }
}
