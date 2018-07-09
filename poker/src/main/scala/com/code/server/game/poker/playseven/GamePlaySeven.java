package com.code.server.game.poker.playseven;

import com.code.server.constant.response.GameOfResult;
import com.code.server.constant.response.ResponseVo;
import com.code.server.constant.response.UserOfResult;
import com.code.server.game.room.Game;
import com.code.server.game.room.Room;
import com.code.server.game.room.kafka.MsgSender;
import com.code.server.game.room.service.RoomManager;
import com.code.server.util.IdWorker;

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
public class GamePlaySeven extends Game{

    public static final long STOP_TIME = 500;

    public boolean shouQiDouble = false;//首七
    public boolean shuangLiangDouble = false;//双亮
    public boolean seeTableCard = false;
    public boolean fanzhu = false;

    protected List<Integer> cards = new ArrayList<>();//牌
    protected List<Integer> tableCards = new ArrayList<>();//底牌

    protected long chuPaiId;
    protected long zhuId;//第一个叫主的

    public int tableCardFen = 0;
    //TODO 跳反的话要减分
    public int jianFen = 0;

    public int kouDiBeiShu = 1;//扣底翻倍的倍数

    public Integer liangCard;//亮的牌
    public long secondBanker;//另一个队友Id

    //出过 1，未出 0
    protected Map<Long, Integer> ifChuPai = new HashMap<>();

    //赢为1，输为0，未比过为-1
    protected Map<Long, Integer> compareCard = new HashMap<>();

    protected Map<Long, Integer> userGetFen = new HashMap<>();//玩家的分

    protected RoomPlaySeven room;

    public Map<Long, PlayerCardInfoPlaySeven> playerCardInfos = new HashMap<>();

    public void startGame(List<Long> users, Room room) {
        this.room = (RoomPlaySeven) room;
        init(users);
        updateLastOperateTime();
        //通知其他人游戏已经开始
        MsgSender.sendMsg2Player(new ResponseVo("gameService", "gameBegin", "ok"), this.getUsers());
    }

    public void init(List<Long> users) {
        //初始化玩家
        for (Long uid : users) {
            PlayerCardInfoPlaySeven playerCardInfo = getGameTypePlayerCardInfo();
            playerCardInfos.put(uid, playerCardInfo);
            ifChuPai.put(uid,0);
            compareCard.put(uid,-1);
            userGetFen.put(uid,0);
        }
        this.users.addAll(users);
        shuffle();
        dealOne();

        updateLastOperateTime();
    }

    public PlayerCardInfoPlaySeven getGameTypePlayerCardInfo() {
        switch (room.getGameType()) {
            case "1":
                return new PlayerCardInfoPlaySeven();
            default:
                return new PlayerCardInfoPlaySeven();
        }
    }


    //==============操作=====================
    public int shouQi(long userId,Integer card){
        this.liangCard = card;
        this.zhuId = userId;
        this.shouQiDouble = true;
        this.seeTableCard =true;
        playerCardInfos.get(userId).setShouQi("2");
        for (Long l:playerCardInfos.keySet()) {
            if(l!=userId){
                playerCardInfos.get(l).setShouQi("3");
                playerCardInfos.get(l).setDanLiang("3");
                playerCardInfos.get(l).setShuangLiang("3");
                playerCardInfos.get(l).setShouQi("3");
            }
        }
        Map<String, Object> msg = new HashMap<>();
        msg.put("shouQiUserId", userId);
        MsgSender.sendMsg2Player(new ResponseVo("gameService", "tellShouQiUserId", msg), users);

        Map<String, Object> tableCardMsg = new HashMap<>();
        msg.put("tableCards", tableCards);
        MsgSender.sendMsg2Player(new ResponseVo("gameService", "tellTableCards", tableCardMsg), userId);

        MsgSender.sendMsg2Player("gameService", "shouQi", 0, userId);
        return 0;
    }

    public int danLiang(long userId,Integer card){
        this.liangCard = card;
        this.zhuId = userId;
        this.seeTableCard =true;
        playerCardInfos.get(userId).setDanLiang("2");
        for (Long l:playerCardInfos.keySet()) {
            if(l!=userId){
                playerCardInfos.get(l).setShouQi("3");
                playerCardInfos.get(l).setDanLiang("3");
                playerCardInfos.get(l).setShuangLiang("3");
                playerCardInfos.get(l).setShouQi("3");
            }
        }
        Map<String, Object> msg = new HashMap<>();
        msg.put("danLiangUserId", userId);
        MsgSender.sendMsg2Player(new ResponseVo("gameService", "tellDanLiangUserId", msg), users);

        Map<String, Object> tableCardMsg = new HashMap<>();
        msg.put("tableCards", tableCards);
        MsgSender.sendMsg2Player(new ResponseVo("gameService", "tellTableCards", tableCardMsg), userId);

        MsgSender.sendMsg2Player("gameService", "danLiang", 0, userId);
        return 0;
    }

    public int shuangLiang(long userId,Integer card){
        this.liangCard = card;
        this.zhuId = userId;
        this.shouQiDouble = true;
        this.seeTableCard =true;
        playerCardInfos.get(userId).setShuangLiang("2");
        for (Long l:playerCardInfos.keySet()) {
            if(l!=userId){
                playerCardInfos.get(l).setShouQi("3");
                playerCardInfos.get(l).setDanLiang("3");
                playerCardInfos.get(l).setShuangLiang("3");
                playerCardInfos.get(l).setShouQi("3");
            }
        }
        Map<String, Object> msg = new HashMap<>();
        msg.put("danLiangUserId", userId);
        MsgSender.sendMsg2Player(new ResponseVo("gameService", "tellShuangLiangUserId", msg), users);

        Map<String, Object> tableCardMsg = new HashMap<>();
        msg.put("tableCards", tableCards);
        MsgSender.sendMsg2Player(new ResponseVo("gameService", "tellTableCards", tableCardMsg), userId);

        MsgSender.sendMsg2Player("gameService", "shuangLiang", 0, userId);
        return 0;
    }

    public int fanZhu(long userId,boolean fan,Integer card){
        //TODO 定时器，10s自动反主
        if(fan){//反主
            this.liangCard = card;
            this.shouQiDouble = false;
            this.fanzhu = true;
            playerCardInfos.get(userId).setFanZhu("2");
            chuPaiId = userId;
            zhuId = userId;
            MsgSender.sendMsg2Player(new ResponseVo("gameService", "canChuPai", 0), zhuId);
        }else{
            if("4".equals(playerCardInfos.get(zhuId).renShu)){
                playerCardInfos.get(zhuId).setRenShu("1");
                MsgSender.sendMsg2Player(new ResponseVo("gameService", "canRenShu", 0), zhuId);
            }
            for (long l:playerCardInfos.keySet()) {
                if("1".equals(playerCardInfos.get(l).fanZhu)){
                    playerCardInfos.get(l).setFanZhu("3");
                }
            }
        }
        MsgSender.sendMsg2Player("gameService", "fanZhu", 0, userId);
        return 0;
    }

    public int renShu(long userId,boolean renshu){
        if(renshu){
            computeRenshu();
            sendResult();
            genRecord();
            room.clearReadyStatus(true);
            sendFinalResult();
        }else {
            MsgSender.sendMsg2Player(new ResponseVo("gameService", "canChuPai", 0), zhuId);
        }
        MsgSender.sendMsg2Player("gameService", "renShu", 0, userId);
        return 0;
    }

    public int changeTableCards(long userId,String tableDelete,String tableAdd){

        //delete和add均为底牌的操作
        List<Integer> delete  = CardsUtil.transfromStringToCards(tableDelete);
        List<Integer> add  = CardsUtil.transfromStringToCards(tableAdd);

        playerCardInfos.get(userId).handCards.addAll(delete);
        playerCardInfos.get(userId).handCards.removeAll(add);

        tableCards.removeAll(delete);
        tableCards.addAll(add);

        for (Long l:playerCardInfos.keySet()) {
            if("3".equals(playerCardInfos.get(l).getFanZhu()) && l!=userId){
                playerCardInfos.get(l).setFanZhu("1");
                MsgSender.sendMsg2Player(new ResponseVo("gameService", "canFanZhu", 0), l);
            }
        }
        MsgSender.sendMsg2Player("gameService", "changeTableCards", 0, userId);
        return 0;
    }

    public int play(long userId,String playCard){
        //TODO 出特殊7的话 会有提示
        List<Integer> playCardList  = CardsUtil.transfromStringToCards(playCard);

        PlayerCardInfoPlaySeven chuPaiPlayer = playerCardInfos.get(userId);
        chuPaiPlayer.setPlayCards(playCardList);
        chuPaiPlayer.getHandCards().removeAll(playCardList);

        ifChuPai.put(userId,1);
        if(1==getChuPaiNum()){//第一个人出牌
            compareCard.put(userId,1);
            long nextUser = nextTurnId(userId);
            MsgSender.sendMsg2Player(new ResponseVo("gameService", "canChuPai", 0), nextUser);
        }else if(room.getPersonNumber()==getChuPaiNum()){//最后一个人出牌
            boolean compareResult = CardsUtil.compareCards(getWinCards(),playCardList);
            int fen = 0;
            long winnerId = getWinnerId();
            if(compareResult){//原来的人赢
                compareCard.put(userId,0);
            }else{
                compareCard.put(winnerId,0);
                compareCard.put(userId,1);
            }
            for (Long l :users) {
                for (Integer integer:playerCardInfos.get(l).getHandCards()) {
                    fen+=CardsUtil.cardsOfScore.get(integer);
                }
            }
            playerCardInfos.get(winnerId).setFen(fen);
            userGetFen.put(winnerId,userGetFen.get(winnerId)+fen);

            Map<String, Object> msg = new HashMap<>();
            msg.put("userGetFen", userGetFen);
            MsgSender.sendMsg2Player(new ResponseVo("gameService", "allFen", msg), users);


            if(playerCardInfos.get(userId).handCards.size()>0){
                MsgSender.sendMsg2Player(new ResponseVo("gameService", "canChuPai", 0), winnerId);
            }else{
                if(winnerId!=room.getBankerId()||winnerId!=secondBanker){
                    int size = playCardList.size();
                    if(1==size){
                        this.kouDiBeiShu = 2;
                    }else{
                        this.kouDiBeiShu = Integer.parseInt(Math.scalb(1.0,size/2+1)+"");
                    }
                }
                for (long l:users) {
                    if(l!=room.getBankerId()||l!=secondBanker){
                        this.jianFen+=playerCardInfos.get(l).getFen();
                    }
                }
                for (Integer integer:tableCards) {
                    this.tableCardFen+=CardsUtil.cardsOfScore.get(integer);
                }
                compute();
                sendResult();
                genRecord();
                room.clearReadyStatus(true);
                sendFinalResult();
            }

        }else{//第二个人出牌
            boolean compareResult = CardsUtil.compareCards(getWinCards(),playCardList);
            long winnerId = getWinnerId();
            if(compareResult){//原来的人赢
                compareCard.put(userId,0);
            }else{
                compareCard.put(winnerId,0);
                compareCard.put(userId,1);
            }
            long nextUser = nextTurnId(userId);
            MsgSender.sendMsg2Player(new ResponseVo("gameService", "canChuPai", 0), nextUser);
        }

        Map<String, Object> msg = new HashMap<>();
        msg.put("userId", userId);
        msg.put("playCard", playCardList);
        MsgSender.sendMsg2Player(new ResponseVo("gameService", "playCard", msg), users);

        MsgSender.sendMsg2Player(new ResponseVo("gameService", "play", 0), userId);
        return 0;
    }





    /**
     * 洗牌
     */
    protected void shuffle() {
        cards.addAll(CardsUtil.cardsOf108.keySet());
        Collections.shuffle(cards);
    }

    protected void dealOne() {
        int cardSize = 0;
        if(4==room.getPersonNumber()){
            while (cardSize<26){
                sendSingleCard();
                try {
                    Thread.sleep(STOP_TIME);
                }catch (Exception e){
                    e.printStackTrace();
                }
                cardSize++;
            }
        }else if(5==room.getPersonNumber()){
            while (cardSize<21){
                sendSingleCard();
                try {
                    Thread.sleep(STOP_TIME);
                }catch (Exception e){
                    e.printStackTrace();
                }
                cardSize++;
            }
        }
    }

    private void sendSingleCard(){
        for (PlayerCardInfoPlaySeven playerCardInfo : playerCardInfos.values()) {
            playerCardInfo.handCards.add(cards.remove(0));
            Map<String, Object> msg = new HashMap<>();
            msg.put("userId", playerCardInfo.getUserId());
            int size = playerCardInfo.handCards.size();
            int tempcardNum = playerCardInfo.handCards.get(size-1);
            msg.put("card",tempcardNum);
            if(size==0){
                if(tempcardNum==49||tempcardNum==50||tempcardNum==51||tempcardNum==52||tempcardNum==53||tempcardNum==54||
                        tempcardNum==-49||tempcardNum==-50||tempcardNum==-51||tempcardNum==-52||tempcardNum==-53||tempcardNum==-54){
                    playerCardInfo.setShouQi("1");
                    playerCardInfo.setDanLiang("1");
                }
            }else{
                if(tempcardNum==49||tempcardNum==50||tempcardNum==51||tempcardNum==52||tempcardNum==53||tempcardNum==54||
                        tempcardNum==-49||tempcardNum==-50||tempcardNum==-51||tempcardNum==-52||tempcardNum==-53||tempcardNum==-54){
                    playerCardInfo.setDanLiang("1");
                }
            }
            if("1".equals(playerCardInfo.getDanLiang())){
                for (int j = 0; j < size; j++) {
                    if(tempcardNum+playerCardInfo.handCards.get(j)==0){
                        playerCardInfo.setShuangLiang("1");
                        playerCardInfo.setFanZhu("4");
                        playerCardInfo.setRenShu("4");
                    }
                }
            }
            MsgSender.sendMsg2Player(new ResponseVo("gameService", "cardAndCanOperate", msg), playerCardInfo.getUserId());
        }
    }



    /**
     * 发牌，定时发
     */
    @Deprecated
    protected void deal(){
        //发所有
        for (PlayerCardInfoPlaySeven playerCardInfo : playerCardInfos.values()) {
            if(4==room.getPersonNumber()){
                for (int i = 0; i < 25; i++) {
                    playerCardInfo.handCards.add(cards.remove(0));
                }
            }else if(5==room.getPersonNumber()){
                for (int i = 0; i < 20; i++) {
                    playerCardInfo.handCards.add(cards.remove(0));
                }
            }
        }
        this.tableCards.addAll(cards);
        //定时
        int timer = 0;
        if(4==room.getPersonNumber()){
            while(timer<25){
                for (int i = 0; i < 25; i++) {
                    sendSingleCardAndWait500s(i);
                }
                timer++;
            }
        }else if(5==room.getPersonNumber()){
            while(timer<20){
                for (int i = 0; i < 20; i++) {
                    sendSingleCardAndWait500s(i);
                }
                timer++;
            }
        }


    }

    //单张发并且等待500s
    @Deprecated
    private void sendSingleCardAndWait500s(int i) {
        for (Long l:playerCardInfos.keySet()) {
            Map<String, Object> msg = new HashMap<>();
            msg.put("userId", l);
            int tempcardNum = playerCardInfos.get(l).handCards.get(i);
            msg.put("card",tempcardNum);
            if(i==0){
                if(tempcardNum==49||tempcardNum==50||tempcardNum==51||tempcardNum==52||tempcardNum==53||tempcardNum==54||
                        tempcardNum==-49||tempcardNum==-50||tempcardNum==-51||tempcardNum==-52||tempcardNum==-53||tempcardNum==-54){
                    playerCardInfos.get(l).setShouQi("1");
                    playerCardInfos.get(l).setDanLiang("1");
                }
            }else{
                if(tempcardNum==49||tempcardNum==50||tempcardNum==51||tempcardNum==52||tempcardNum==53||tempcardNum==54||
                        tempcardNum==-49||tempcardNum==-50||tempcardNum==-51||tempcardNum==-52||tempcardNum==-53||tempcardNum==-54){
                    playerCardInfos.get(l).setDanLiang("1");
                }
            }
            if("1".equals(playerCardInfos.get(l).getDanLiang())){
                for (int j = 0; j < i; j++) {
                    if(tempcardNum+playerCardInfos.get(l).handCards.get(j)==0){
                        playerCardInfos.get(l).setShuangLiang("1");
                        playerCardInfos.get(l).setFanZhu("4");
                        playerCardInfos.get(l).setRenShu("4");
                    }
                }
            }
            MsgSender.sendMsg2Player(new ResponseVo("gameService", "cardAndCanOperate", msg), l);
        }
        try {
            Thread.sleep(500);//发牌停顿
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    protected void compute() {
        RoomPlaySeven roomPlaySeven = null;
        if (room instanceof RoomPlaySeven) {
            roomPlaySeven = (RoomPlaySeven) room;
        }
        for (Long l:users) {
            roomPlaySeven.addUserSocre(l, 2);
            playerCardInfos.get(l).addScore(2);
        }
        roomPlaySeven.addUserSocre(zhuId, -2*(roomPlaySeven.getPersonNumber()));
        playerCardInfos.get(zhuId).addScore(-2*(roomPlaySeven.getPersonNumber()));
    }

    //认输输两分
    protected void computeRenshu() {
        RoomPlaySeven roomPlaySeven = null;
        if (room instanceof RoomPlaySeven) {
            roomPlaySeven = (RoomPlaySeven) room;
        }
        for (Long l:users) {
            roomPlaySeven.addUserSocre(l, 2);
            playerCardInfos.get(l).addScore(2);
        }
        roomPlaySeven.addUserSocre(zhuId, -2*(roomPlaySeven.getPersonNumber()));
        playerCardInfos.get(zhuId).addScore(-2*(roomPlaySeven.getPersonNumber()));
    }

    protected void sendResult() {
        Map<String, Object> gameResult = new HashMap<>();
        gameResult.put("tableCardFen",tableCardFen);
        gameResult.put("jianFen",jianFen);
        gameResult.put("kouDiBeiShu",kouDiBeiShu);
        //TODO 考虑封顶
        MsgSender.sendMsg2Player("gameService", "gameResult",gameResult, users);
    }

    protected void genRecord() {
        long id = IdWorker.getDefaultInstance().nextId();
        genRecord(playerCardInfos.values().stream().collect
                (Collectors.toMap(PlayerCardInfoPlaySeven::getUserId, PlayerCardInfoPlaySeven::getScore)), room, id);
    }

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

    protected long nextTurnId(long curId) {
        int index = users.indexOf(curId);

        int nextId = index + 1;
        if (nextId >= users.size()) {
            nextId = 0;
        }
        return users.get(nextId);
    }

    //获取已经出牌的人数
    public int getChuPaiNum(){
        int chuPaiNum = 0;
        for (long l:ifChuPai.keySet()) {
            if(1==ifChuPai.get(l)){
                chuPaiNum++;
            }
        }
        return chuPaiNum;
    }

    //获取赢的人的牌
    public List<Integer> getWinCards(){
        long winnerId = 0l;
        for (long l:compareCard.keySet()) {
            if(1==compareCard.get(l)){
                winnerId = l;
            }
        }
        return playerCardInfos.get(winnerId).playCards;
    }

    //获取赢的人
    public long getWinnerId(){
        long winnerId = 0l;
        for (long l:compareCard.keySet()) {
            if(1==compareCard.get(l)){
                winnerId = l;
            }
        }
        return winnerId;
    }
}
