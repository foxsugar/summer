package com.code.server.game.poker.cow;

import com.code.server.constant.response.ResponseVo;
import com.code.server.game.room.Game;
import com.code.server.game.room.Room;
import com.code.server.game.room.kafka.MsgSender;
import com.code.server.util.IdWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 项目名称：${project_name}
 * 类名称：${type_name}
 * 类描述：
 * 创建人：Clark
 * 创建时间：${date} ${time}
 * 修改人：Clark
 * 修改时间：${date} ${time}
 * 修改备注：
 *
 * @version 1.0
 */
public class GameCow extends Game {

    protected static final Logger logger = LoggerFactory.getLogger(GameCow.class);

    protected List<Integer> cards = new ArrayList<>();//牌
    public Map<Long, PlayerCow> playerCardInfos = new HashMap<>();
    protected Random rand = new Random();
    protected List<Long> loseUser = new ArrayList<>();//输牌的人
    protected RoomCow room;
    protected long lastOperateTime;


    public void init(List<Long> users) {
        //初始化玩家
        for (Long uid : users) {
            PlayerCow playerCardInfo = getGameTypePlayerCardInfo();
            playerCardInfo.userId = uid;
            playerCardInfos.put(uid, playerCardInfo);
        }
        this.users.addAll(users);
        this.room.setBankerId(users.get(0));
        shuffle();//洗牌
        deal();//发牌
        noticePlayerBet();
        updateLastOperateTime();
    }

    public void startGame(List<Long> users, Room room) {
        this.room = (RoomCow) room;
        init(users);
        updateLastOperateTime();
        //通知其他人游戏已经开始
        MsgSender.sendMsg2Player(new ResponseVo("gameService", "gameBegin", "ok"), this.getUsers());
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
        for (PlayerCow playerCardInfo : playerCardInfos.values()) {
            for (int i = 0; i < 4; i++) {
                playerCardInfo.handcards.add(cards.remove(0));
            }
            //通知发牌
            MsgSender.sendMsg2Player(new ResponseVo("gameService", "deal", playerCardInfo.handcards), playerCardInfo.userId);
        }
    }



    /**
     * 发第五张牌
     */
    protected void dealFiveCard(){
        for (PlayerCow playerCardInfo : playerCardInfos.values()) {
            playerCardInfo.handcards.add(cards.remove(0));
            CowPlayer c =  new CowPlayer(playerCardInfo.getUserId(),playerCardInfo.handcards.get(0),playerCardInfo.handcards.get(1),playerCardInfo.handcards.get(2),playerCardInfo.handcards.get(3),playerCardInfo.handcards.get(4));
            playerCardInfo.setPlayer(c);
            //通知发牌
            Map<String, Object> result = new HashMap<>();
            result.put("userId",playerCardInfo.getUserId());
            result.put("fiveCard",playerCardInfo.handcards.get(4));
            result.put("grade",playerCardInfo.getPlayer().getGrade());
            ResponseVo vo = new ResponseVo("gameService", "dealFiveCard", result);
            MsgSender.sendMsg2Player(vo, playerCardInfo.userId);
        }


        noticePlayerCompare();
    }

    /**
     * 通知闲家下注
     */
    protected void noticePlayerBet() {
        List<Long> list = new ArrayList<>();
        list.addAll(this.getUsers());
        list.remove(room.getBankerId());
        MsgSender.sendMsg2Player(new ResponseVo("gameService", "noticePlayerBet", "canBet"),list);
    }

    /**
     * 通知闲家可以开牌
     */
    protected void noticePlayerCompare() {
        List<Long> list = new ArrayList<>();
        list.addAll(this.getUsers());
        list.remove(room.getBankerId());
        MsgSender.sendMsg2Player(new ResponseVo("gameService", "noticePlayerCompare", "canCompare"),list);
    }

    /**
     * 加注
     * @return
     */
    public int raise(long userId,double addChip) throws Exception{
        logger.info(userId +"  下注: "+ addChip);

        Map<String, Object> result = new HashMap<>();
        result.put("userId",userId);
        result.put("addChip",addChip);
        ResponseVo vo = new ResponseVo("gameService", "raiseResponse", result);
        MsgSender.sendMsg2Player(vo, users);

        MsgSender.sendMsg2Player("gameService", "raise", 0, userId);
        PlayerCow playerCardInfo = playerCardInfos.get(userId);
        playerCardInfo.setRaise(1);
        playerCardInfo.setScore(addChip);

        boolean b = true;
        for (PlayerCow p:playerCardInfos.values()) {
            if(room.getBankerId()!=p.userId && 0==p.getRaise()){
                b=false;
            }
        }
        if(b){
            dealFiveCard();
            noticePlayerCompare();
        }

        updateLastOperateTime();

        return 0;
    }

    /**
     * 开牌
     * @return
     */
    public int compare(long userId){
        logger.info(userId +"  开牌: ");

        Map<String, Object> result = new HashMap<>();
        result.put("userId",userId);
        ResponseVo vo = new ResponseVo("gameService", "compareResponse", result);
        MsgSender.sendMsg2Player(vo, users);

        MsgSender.sendMsg2Player("gameService", "compare", 0, userId);
        PlayerCow playerCardInfo = playerCardInfos.get(userId);
        playerCardInfo.setKill(1);

        boolean b = true;
        for (PlayerCow p:playerCardInfos.values()) {
            if(room.getBankerId()!=p.userId && 0==p.getKill()){
                b=false;
            }
        }
        if(b){
            compute();
            sendResult();
            genRecord();
        }

        updateLastOperateTime();

        return 0;
    }


    //算分=============================================
    protected void compute() {
        RoomCow roomCom = null;
        if(room instanceof RoomCow){
            roomCom = (RoomCow)room;
        }

        //算分
        List<PlayerCow> tempList = new ArrayList<>();
        tempList.addAll(playerCardInfos.values());
        tempList.remove(0);
        for (PlayerCow p :tempList){
            CowPlayer c = CowCardUtils.findWinner(playerCardInfos.get(room.getBankerId()).getPlayer(), p.getPlayer());
            if(room.getBankerId()!=c.getId()){//庄输
                playerCardInfos.get(p.getUserId()).setFinalScore(playerCardInfos.get(p.getUserId()).getScore()* CowCardUtils.multipleMap.get(playerCardInfos.get(p.getUserId()).getPlayer().getGrade()));
            }else{//庄赢
                playerCardInfos.get(p.getUserId()).setFinalScore(-playerCardInfos.get(room.getBankerId()).getScore()* CowCardUtils.multipleMap.get(playerCardInfos.get(room.getBankerId()).getPlayer().getGrade()));
            }
        }

        //设置每个人的统计
        boolean tempWin = true;
        boolean tempLost = true;
        for (PlayerCow playerCardInfo : playerCardInfos.values()) {
            if(8==playerCardInfo.getPlayer().getGrade()){//牛牛
                this.room.addCowCowNum(playerCardInfo.getUserId());
            }else if(18==playerCardInfo.getPlayer().getGrade()){//无牛
                this.room.addNullCowNum(playerCardInfo.getUserId());
            }
            if(playerCardInfo.getFinalScore()<0){
                tempLost = false;
                this.room.addWinNum(playerCardInfo.getUserId());
            }
            if(playerCardInfo.getFinalScore()>0){
                tempWin = false;
            }
            if(playerCardInfo.userId!=room.getBankerId()){
                playerCardInfos.get(room.getBankerId()).setFinalScore(playerCardInfos.get(room.getBankerId()).getFinalScore()-playerCardInfo.getFinalScore());
            }
        }
        if(tempWin){
            this.room.addAllWinNum(room.getBankerId());
        }
        if(tempLost){
            this.room.addAllLoseNum(room.getBankerId());
        }
        if(playerCardInfos.get(room.getBankerId()).getFinalScore()>0){
            this.room.addWinNum(room.getBankerId());
        }

        for (PlayerCow playerCardInfo : playerCardInfos.values()) {
            room.addUserSocre(playerCardInfo.getUserId(),playerCardInfo.getFinalScore());
        }
    }

    /**
     * 发送战绩
     */
    protected void sendResult() {
        GameResultCow gameResultCow  = new GameResultCow();
        gameResultCow.setBankerId(room.getBankerId());
        List<Long> winnerList = new ArrayList<>();
        Map<Long, Double> userScores = new HashMap<>();
        for (PlayerCow p : playerCardInfos.values()) {
            if(p.getFinalScore()>0){
                winnerList.add(p.getUserId());
            }
            gameResultCow.getPlayerCardInfos().add( p.toVo());
            userScores.put(p.getUserId(),p.getFinalScore());
        }
        gameResultCow.setWinnerList(winnerList);
        MsgSender.sendMsg2Player("gameService", "gameResult", gameResultCow, users);
    }

    /**
     * 战绩
     */
    protected void genRecord() {
        long id = IdWorker.getDefaultInstance().nextId();
        genRecord(playerCardInfos.values().stream().collect
                (Collectors.toMap(PlayerCow::getUserId, PlayerCow::getScore)), room, id);
    }

    public PlayerCow getGameTypePlayerCardInfo() {
        switch (room.getGameType()) {
            case "38":
                return new PlayerCow();
            default:
                return new PlayerCow();
        }
    }

    //更新操作时间
    protected void updateLastOperateTime() {
        this.lastOperateTime = System.currentTimeMillis();
    }


    public List<Integer> getCards() {
        return cards;
    }

    public void setCards(List<Integer> cards) {
        this.cards = cards;
    }

    public Map<Long, PlayerCow> getPlayerCardInfos() {
        return playerCardInfos;
    }

    public void setPlayerCardInfos(Map<Long, PlayerCow> playerCardInfos) {
        this.playerCardInfos = playerCardInfos;
    }

    public Random getRand() {
        return rand;
    }

    public void setRand(Random rand) {
        this.rand = rand;
    }

    public List<Long> getLoseUser() {
        return loseUser;
    }

    public void setLoseUser(List<Long> loseUser) {
        this.loseUser = loseUser;
    }

    public RoomCow getRoom() {
        return room;
    }

    public void setRoom(RoomCow room) {
        this.room = room;
    }

    public long getLastOperateTime() {
        return lastOperateTime;
    }

    public void setLastOperateTime(long lastOperateTime) {
        this.lastOperateTime = lastOperateTime;
    }
}