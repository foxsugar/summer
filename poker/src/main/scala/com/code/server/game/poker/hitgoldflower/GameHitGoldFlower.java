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
    public Map<Long, PlayerCardInfoHitGoldFlower> playerCardInfos = new HashMap<>();
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
        this.aliveUser.addAll(users);

        shuffle();//洗牌
        deal();//发牌
        mustBet();
        curUserId = room.getBankerId();

        noticeAction(curUserId);
        updateLastOperateTime();
    }

    public void startGame(List<Long> users, Room room) {
        this.room = room;
        init(users);
        updateLastOperateTime();
        //通知其他人游戏已经开始
        MsgSender.sendMsg2Player(new ResponseVo("gameService", "gameBegin", "ok"), this.getUsers());
    }

    /**
     * 必须下底注
     */
    private void mustBet(){
        for (Long l : playerCardInfos.keySet()) {
            playerCardInfos.get(l).setAllScore(INIT_BOTTOM_CHIP);
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
            if(addChip!=chip*2+2 && addChip!=chip*2*2 && addChip!=chip*2*4 && addChip!=1000.0){
                return ErrorCode.BET_WRONG;
            }
            chip = addChip/2;
        }else{
            if(addChip!=chip+2 && addChip!=chip*2 && addChip!=chip*4 && addChip!=500.0){
                return ErrorCode.BET_WRONG;
            }
            chip = addChip;
        }

        playerCardInfos.get(userId).setAllScore(playerCardInfos.get(userId).getAllScore()+addChip);
        Map<String, Object> result = new HashMap<>();
        result.put("userId",userId);
        result.put("addChip",addChip);
        ResponseVo vo = new ResponseVo("gameService", "raiseResponse", result);
        MsgSender.sendMsg2Player(vo, users);

        noticeAction(curUserId);

        MsgSender.sendMsg2Player("gameService", "raise", 0, userId);
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
            playerCardInfos.get(userId).setAllScore(playerCardInfos.get(userId).getAllScore()+chip*2);
        }else{
            playerCardInfos.get(userId).setAllScore(playerCardInfos.get(userId).getAllScore()+chip);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("userId",userId);
        if(seeUser.contains(userId)){
            result.put("addChip",chip*2);
        }else{
            result.put("addChip",chip);
        }

        ResponseVo vo = new ResponseVo("gameService", "callResponse", result);
        MsgSender.sendMsg2Player(vo, users);

        noticeAction(curUserId);

        MsgSender.sendMsg2Player("gameService", "call", 0, userId);
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

        Map<String, Object> result = new HashMap<>();
        result.put("userId",userId);
        result.put("result","fold success!!");
        ResponseVo vo = new ResponseVo("gameService", "foldResponse", result);
        MsgSender.sendMsg2Player(vo, users);

        if(aliveUser.size()==1){
            //处理结果
            compute(aliveUser);
            sendResult();
            genRecord();

            room.setBankerId(aliveUser.get(0));
            room.clearReadyStatus(true);
            sendFinalResult();
        }else{
            noticeAction(userId);
        }

        MsgSender.sendMsg2Player("gameService", "fold", 0, userId);
        updateLastOperateTime();

        return 0;
    }

    /**
     * 看牌
     * @return
     */
    public int see(long userId){

        logger.info(userId +"  看牌"+playerCardInfos.get(userId).getHandcards());

        if (playerCardInfos.get(userId).getCurRoundNumber()<=room.getMenPai()) {
            return ErrorCode.NOT_GET_MEMPAI;
        }
        seeUser.add(userId);
        playerCardInfos.get(userId).setSee("0");

        Map<String, Object> result = new HashMap<>();
        result.put("userId",userId);
        ResponseVo vo = new ResponseVo("gameService", "seeResponse", result);
        MsgSender.sendMsg2Player(vo, users);

        Map<String, Object> seeResult = new HashMap<>();
        result.put("userId",userId);
        result.put("cards",playerCardInfos.get(userId).getHandcards());
        ResponseVo seeVo = new ResponseVo("gameService", "seeResponse", result);
        MsgSender.sendMsg2Player(vo, userId);

        noticeActionSelf(userId);

        MsgSender.sendMsg2Player("gameService", "see", 0, userId);
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

        Map<String, Object> resultR = new HashMap<>();
        resultR.put("userId",askerId);

        if(seeUser.contains(askerId)){
            playerCardInfos.get(askerId).setAllScore(playerCardInfos.get(askerId).getAllScore()+chip*2);
            resultR.put("addChip",chip*2);
        }else{
            playerCardInfos.get(askerId).setAllScore(playerCardInfos.get(askerId).getAllScore()+chip);
            resultR.put("addChip",chip);
        }
        ResponseVo voR = new ResponseVo("gameService", "raiseResponse", resultR);
        MsgSender.sendMsg2Player(voR, users);

        Player asker = new Player(askerId, ListUtils.cardCode.get(playerCardInfos.get(askerId).getHandcards().get(0)), ListUtils.cardCode.get(playerCardInfos.get(askerId).getHandcards().get(1)), ListUtils.cardCode.get(playerCardInfos.get(askerId).getHandcards().get(2)));
        Player accepter = new Player(accepterId, ListUtils.cardCode.get(playerCardInfos.get(accepterId).getHandcards().get(0)), ListUtils.cardCode.get(playerCardInfos.get(accepterId).getHandcards().get(1)), ListUtils.cardCode.get(playerCardInfos.get(accepterId).getHandcards().get(2)));

        ArrayList<Player> winnerList = Player.findWinners(asker,accepter);

        Long winnerId = winnerList.size()==1?winnerList.get(0).getUid():winnerList.get(1).getUid();

        Map<String, Long> result = new HashMap<>();
        result.put("askerId",winnerId);
        result.put("winnerId",winnerId);
        result.put("loserId",winnerId==askerId?accepterId:askerId);
        ResponseVo vo = new ResponseVo("gameService", "killResponse", result);
        MsgSender.sendMsg2Player(vo, users);

        if(aliveUser.size()>2){
            if(winnerId==askerId){
                aliveUser.remove(winnerId==askerId?accepterId:askerId);
            }

            if(aliveUser.size()>1){
                noticeAction(curUserId);
            }else{
                //处理结果
                List<Long> list = new ArrayList<>();
                list.add(winnerId);
                compute(list);
                sendResult();
                genRecord();

                room.setBankerId(winnerId);
                room.clearReadyStatus(true);
                sendFinalResult();
            }

            if(winnerId!=askerId){
                aliveUser.remove(winnerId==askerId?accepterId:askerId);
            }
        }else{
            aliveUser.remove(winnerId==askerId?accepterId:askerId);
            List<Long> list = new ArrayList<>();
            list.add(winnerId);
            compute(list);
            sendResult();
            genRecord();

            room.setBankerId(winnerId);
            room.clearReadyStatus(true);
            sendFinalResult();
        }

        MsgSender.sendMsg2Player("gameService", "kill", 0, askerId);

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
    protected void compute(List<Long> winList) {
        RoomHitGoldFlower roomHitGoldFlower = null;
        if(room instanceof RoomHitGoldFlower){
            roomHitGoldFlower = (RoomHitGoldFlower)room;
        }
        //设置每个人的牌类型
        for (PlayerCardInfoHitGoldFlower playerCardInfo : playerCardInfos.values()) {
            Player p = new Player(playerCardInfo.getUserId(), ListUtils.cardCode.get(playerCardInfo.getHandcards().get(0)), ListUtils.cardCode.get(playerCardInfo.getHandcards().get(1)), ListUtils.cardCode.get(playerCardInfo.getHandcards().get(2)));
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
            totalChip+=playerCardInfo.getAllScore();
        }
        for (PlayerCardInfoHitGoldFlower playerCardInfo : playerCardInfos.values()) {
            if(winList.contains(playerCardInfo.getUserId())){
                playerCardInfo.setScore(room.getMultiple()*totalChip/winList.size());
            }else{
                playerCardInfo.setScore(-room.getMultiple()*playerCardInfo.getAllScore());
            }
        }
        for (PlayerCardInfoHitGoldFlower playerCardInfo : playerCardInfos.values()) {
            if(winList.contains(playerCardInfo.getUserId())){
                room.addUserSocre(playerCardInfo.getUserId(),playerCardInfo.getScore()-room.getMultiple()*playerCardInfo.getAllScore());
                room.addUserSocre(playerCardInfo.getUserId(),playerCardInfo.getCaifen());
                playerCardInfo.setFinalScore(playerCardInfo.getScore()-room.getMultiple()*playerCardInfo.getAllScore()+playerCardInfo.getCaifen());
            }else{
                room.addUserSocre(playerCardInfo.getUserId(),-room.getMultiple()*playerCardInfo.getAllScore());
                room.addUserSocre(playerCardInfo.getUserId(),-playerCardInfo.getCaifen());
                playerCardInfo.setFinalScore(-room.getMultiple()*playerCardInfo.getAllScore()-playerCardInfo.getCaifen());
            }
        }
    }

    /**
     * 发送战绩
     */
    protected void sendResult() {
        GameResultHitGoldFlower gameResultHitGoldFlower = new GameResultHitGoldFlower();

        //将room保存到playerCardInfos
        double personNumberTemp = 0.0;
        double nagetiveTotal = 0.0;
        for (Long l :this.playerCardInfos.keySet()) {
            if(this.playerCardInfos.get(l).getScore()<0){
                personNumberTemp+=1;
                nagetiveTotal+=this.playerCardInfos.get(l).getScore();
            }
        }
        for (Long l :this.playerCardInfos.keySet()) {
            if(this.playerCardInfos.get(l).getScore()>0){
                this.playerCardInfos.get(l).setScore(-nagetiveTotal/(playerCardInfos.size()-personNumberTemp));
            }
        }

        for (PlayerCardInfoHitGoldFlower playerCardInfo : playerCardInfos.values()) {
            gameResultHitGoldFlower.getPlayerCardInfos().add(playerCardInfo.toVoHaveHandcards());
        }

        gameResultHitGoldFlower.getUserScores().putAll(this.room.getUserScores());
        List<Long> winnerList = new ArrayList<>();
        for (PlayerCardInfoHitGoldFlower playerCardInfo : playerCardInfos.values()) {
            if(playerCardInfo.getFinalScore()>0){
                winnerList.add(playerCardInfo.getUserId());
            }
        }
        gameResultHitGoldFlower.setWinnerList(winnerList);
        gameResultHitGoldFlower.setBankerId(winnerList.get(0));
        MsgSender.sendMsg2Player("gameService", "gameResult", gameResultHitGoldFlower, users);
    }

    /**
     * 战绩
     */
    protected void genRecord() {
        long id = IdWorker.getDefaultInstance().nextId();
        genRecord(playerCardInfos.values().stream().collect
                (Collectors.toMap(PlayerCardInfoHitGoldFlower::getUserId, PlayerCardInfoHitGoldFlower::getScore)), room, id);
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
        playerCardInfo.setRaise("1");
        playerCardInfo.setFold("1");
        playerCardInfo.setCall("1");
        playerCardInfo.setKill("1");
        playerCardInfo.setSee("1");

        if(seeUser.contains(userId) || getMaxRoundNumber() <= room.getMenPai()){
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
        if(getMaxRoundNumber() <= room.getMenPai()){
            playerCardInfo.setKill("0");
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
        playerCardInfo.setRaise("1");
        playerCardInfo.setFold("1");
        playerCardInfo.setCall("1");
        playerCardInfo.setKill("1");
        playerCardInfo.setSee("1");
        playerCardInfo.setCurRoundNumber(playerCardInfo.getCurRoundNumber()+1);
        if(seeUser.contains(curUserId) || getMaxRoundNumber() <= room.getMenPai()){
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
        for (int i = 1; i < 53; i++) {
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
        if(getMaxRoundNumberB()||aliveUser.size()==1){
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
            Player p = new Player(l, ListUtils.cardCode.get(playerCardInfos.get(l).getHandcards().get(0)), ListUtils.cardCode.get(playerCardInfos.get(l).getHandcards().get(1)), ListUtils.cardCode.get(playerCardInfos.get(l).getHandcards().get(2)));
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

        room.setBankerId(winList.get(0));
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

    @Override
    public IfaceGameVo toVo(long userId) {
        GameHitGoldFlowerVo vo = new GameHitGoldFlowerVo();
        //vo.cards = this.getCards();
        vo.chip = this.getChip();
        //vo.leaveCards = this.getLeaveCards();
        vo.aliveUser = this.getAliveUser();
        vo.seeUser = this.getSeeUser();
        vo.curUserId = this.getCurUserId();
        vo.curRoundNumber = getMaxRoundNumber();

        Double temp = 0.0;
        //玩家牌信息
        for (PlayerCardInfoHitGoldFlower playerCardInfo : this.getPlayerCardInfos().values()) {
            if(seeUser.contains(playerCardInfo.getUserId())){
                vo.playerCardInfos.put(playerCardInfo.userId, playerCardInfo.toVo(userId));
            }else{
                vo.playerCardInfos.put(playerCardInfo.userId, playerCardInfo.toVo());
            }
            temp+=playerCardInfo.getAllScore();
        }
        vo.allTableChip = temp;
        return vo;
    }

    public int getMaxRoundNumber(){
        int max = 1;
        for (Long l :playerCardInfos.keySet()) {
            if(playerCardInfos.get(l).getCurRoundNumber()>max){
                max = playerCardInfos.get(l).getCurRoundNumber();
            }else{
                max = max;
            }
        }
        return max;
    }

    /**
     * 判断是否大于最大轮数
     * @return
     */
    public boolean getMaxRoundNumberB(){
        boolean maxRound = false;
        int tempCount = 0;
        for (Long l :playerCardInfos.keySet()) {
            if(aliveUser.contains(l) && playerCardInfos.get(l).getCurRoundNumber()==room.getCricleNumber()){
                tempCount +=1;
            }
        }
        if(tempCount==aliveUser.size()){
            maxRound = true;
        }
        return maxRound;
    }
}
