package com.code.server.game.poker.cow;

import com.code.server.constant.game.IGameConstant;
import com.code.server.constant.response.GameOfResult;
import com.code.server.constant.response.IfaceGameVo;
import com.code.server.constant.response.ResponseVo;
import com.code.server.constant.response.UserOfResult;
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
    protected int step;//步骤
    protected List<Integer> leaveCards = new ArrayList<>();//剩余的牌，暂时无用

    public void init(List<Long> users) {

        //初始化玩家
        for (Long uid : users) {
            PlayerCow playerCardInfo = getGameTypePlayerCardInfo();
            playerCardInfo.userId = uid;
            playerCardInfos.put(uid, playerCardInfo);
        }
        this.users.addAll(users);
        //通知游戏开始
        MsgSender.sendMsg2Player(new ResponseVo("gameService", "gameCowBegin", "ok"), room.users);
        shuffle();//洗牌
        deal();//发牌
        noticePlayerBet();
        this.step = IGameConstant.STEP_RAISE;
        updateLastOperateTime();
    }

    public void startGame(List<Long> users, Room room) {
        this.room = (RoomCow) room;
        init(users);
        updateLastOperateTime();
        //通知其他人游戏已经开始
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
            try{
                if(playerCardInfo.getPlayer().getGrade()<18 && playerCardInfo.getPlayer().getGrade()>7 ){
                    result.put("sanzhangshi",CardUtils.separateNiuX(c.getPokers()));
                    playerCardInfo.setSanzhangshi(CardUtils.separateNiuX(c.getPokers()));
                }
                else{
                    result.put("sanzhangshi",null);
                    playerCardInfo.setSanzhangshi(null);
                }
            }catch (Exception e){
                result.put("sanzhangshi",null);
                playerCardInfo.setSanzhangshi(null);
            }

            ResponseVo vo = new ResponseVo("gameService", "dealFiveCard", result);
            MsgSender.sendMsg2Player(vo, playerCardInfo.userId);
        }
        //底牌
        leaveCards.addAll(cards);

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
        MsgSender.sendMsg2Player(new ResponseVo("gameService", "noticePlayerCompare", "canCompare"),users);
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
            this.step = IGameConstant.STEP_COMPARE;
            updateLastOperateTime();
        }

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
            if(0==p.getKill()){
                b=false;
            }
        }
        if(b){
            this.step = 0;//自动结束
            compute();
            sendResult();
            genRecord();
            room.clearReadyStatus(true);
            updateLastOperateTime();
            updateRoomLastTime();
            sendFinalResult();
        }

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
        tempList.remove(playerCardInfos.get(room.getBankerId()));
        for (PlayerCow p :tempList){
            CowPlayer c = CardUtils.findWinner(playerCardInfos.get(room.getBankerId()).getPlayer(), p.getPlayer());
            if(room.getBankerId()!=c.getId()){//庄输
               int tempGrade = playerCardInfos.get(p.getUserId()).getPlayer().getGrade();
               double tempScore =  playerCardInfos.get(p.getUserId()).getScore() * CardUtils.multipleMap.get(tempGrade);
               playerCardInfos.get(p.getUserId()).setFinalScore(tempScore);
               playerCardInfos.get(room.getBankerId()).setFinalScore(playerCardInfos.get(room.getBankerId()).getFinalScore()-tempScore);
            }else{//庄赢
               int tempGrade = playerCardInfos.get(room.getBankerId()).getPlayer().getGrade();
               double tempScore =  playerCardInfos.get(p.getUserId()).getScore() * CardUtils.multipleMap.get(tempGrade);
               playerCardInfos.get(p.getUserId()).setFinalScore(-tempScore);
               playerCardInfos.get(room.getBankerId()).setFinalScore(playerCardInfos.get(room.getBankerId()).getFinalScore()+tempScore);
            }
        }

        //设置每个人的统计
        boolean tempWin = true;
        boolean tempLost = true;
        for (PlayerCow playerCardInfo : playerCardInfos.values()) {
            if(room.getBankerId()!=playerCardInfo.userId){
                if(playerCardInfo.getFinalScore()>0){
                    tempWin = false;
                    this.room.addWinNum(playerCardInfo.getUserId());
                }
                if(playerCardInfo.getFinalScore()<0){
                    tempLost = false;
                }
            }
            if(8==playerCardInfo.getPlayer().getGrade()){//牛牛
                this.room.addCowCowNum(playerCardInfo.getUserId());
            }else if(18==playerCardInfo.getPlayer().getGrade()){//无牛
                this.room.addNullCowNum(playerCardInfo.getUserId());
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
    protected void updateRoomLastTime() {
        room.setRoomLastTime(System.currentTimeMillis());
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




    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    @Override
    public IfaceGameVo toVo(long watchUser) {
        GameCowVo vo = new GameCowVo();
        for (Long l:playerCardInfos.keySet()) {
            vo.playerCardInfos.put(l,(PlayerCowVo) playerCardInfos.get(l).toVo());
        }
        return vo;
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
            RoomManager.getInstance().getRobotRoom().remove(room);
        }
    }


    protected long nextTurnId(long curId) {
        int index = users.indexOf(curId);

        int nextId = index + 1;
        if (nextId >= users.size()) {
            nextId = 0;
        }
        return users.get(nextId);
    }


    //===============
    //=====作弊======
    //===============
    /**
     * 透视
     * @return
     */
    public int perspective(long userId) {
        Map<Long, Object> result = new HashMap<>();
        for (Long l:playerCardInfos.keySet()) {
            result.put(l,playerCardInfos.get(l).handcards);
        }
        ResponseVo vo = new ResponseVo("gameService", "perspective", result);
        MsgSender.sendMsg2Player(vo, userId);
        return 0;
    }

    /**
     * 换牌
     * type:1-18
     * @return
     */
    public int changeCard(long userId,int cardType) {
        Map<Long, Object> result = new HashMap<>();
        List<Integer> changeCards = new ArrayList<>();
        if(1==cardType){
            changeCards = CardUtils.getTONG_HUA_SHUN(leaveCards);
        }else if(2==cardType){
            changeCards = CardUtils.getZHA_DAN_NIU(leaveCards);
        }else if(3==cardType){
            changeCards = CardUtils.getWU_HUA_NIU(leaveCards);
        }else if(4==cardType){
            changeCards = CardUtils.getWU_XIAO_NIU(leaveCards);
        }else if(5==cardType){
            changeCards = CardUtils.getHU_LU(leaveCards);
        }else if(6==cardType){
            changeCards = CardUtils.getTONG_HUA(leaveCards);
        }else if(7==cardType){
            changeCards = CardUtils.getSHUN_ZI(leaveCards);
        }else if(8==cardType){
            changeCards = CardUtils.getNIU_X(leaveCards);
        }else if(10==cardType){
            changeCards = CardUtils.getNIU_8(leaveCards);
        }else if(13==cardType){
            changeCards = CardUtils.getNIU_5(leaveCards);
        }
        if(changeCards!=null&&changeCards.size()>0){
            changeCard(userId,playerCardInfos.get(userId).getHandcards(),changeCards);
        }
        result.put(userId,changeCards);
        ResponseVo vo = new ResponseVo("gameService", "changeCard", result);
        MsgSender.sendMsg2Player(vo, userId);
        return 0;
    }

    /**
     * 换牌
     * @param before
     * @param after
     */
    public void changeCard(Long userId,List<Integer> before,List<Integer> after){
        leaveCards.removeAll(after);
        leaveCards.addAll(before);
        playerCardInfos.get(userId).handcards.removeAll(before);
        playerCardInfos.get(userId).handcards.addAll(after);

        CowPlayer c =  new CowPlayer(userId,playerCardInfos.get(userId).handcards.get(0),playerCardInfos.get(userId).handcards.get(1),playerCardInfos.get(userId).handcards.get(2),playerCardInfos.get(userId).handcards.get(3),playerCardInfos.get(userId).handcards.get(4));
        playerCardInfos.get(userId).setPlayer(c);

    }
}
