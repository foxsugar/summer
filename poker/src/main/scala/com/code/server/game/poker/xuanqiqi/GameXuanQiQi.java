package com.code.server.game.poker.xuanqiqi;

import com.code.server.constant.response.ResponseVo;
import com.code.server.game.room.Game;
import com.code.server.game.room.Room;
import com.code.server.game.room.kafka.MsgSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

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
public class GameXuanQiQi extends Game {

    protected static final Logger logger = LoggerFactory.getLogger(GameXuanQiQi.class);

    /*
        bankerId 可选双倍
        turnNumber
     */

    protected int bankerMultiple = 1;

    protected long chuPaiId;
    protected long operatId;

    protected List<Integer> cards = new ArrayList<>();//牌
    protected List<Integer> randamCards = new ArrayList<>();//搬牌

    //无状态    0
    //宣       1
    //过       2
    protected Map<Long,Integer> xuanOrGuo = new HashMap<>();


    //出过 1-3，未出 0
    protected Map<Long,Integer> ifChuPai = new HashMap<>();

    //赢为1，输为0，未比过为-1
    protected Map<Long,Integer> compareCard = new HashMap<>();




    protected RoomXuanQiQi room;

    public Map<Long, PlayerCardInfoXuanQiQi> playerCardInfos = new HashMap<>();

    public void init(List<Long> users) {
        //初始化玩家
        for (Long uid : users) {
            PlayerCardInfoXuanQiQi playerCardInfo = getGameTypePlayerCardInfo();
            playerCardInfo.userId = uid;
            playerCardInfos.put(uid, playerCardInfo);
            xuanOrGuo.put(uid,0);
            ifChuPai.put(uid,0);
            compareCard.put(uid,-1);
        }
        this.users.addAll(users);
        chuPaiId = room.getBankerId();
        operatId = room.getBankerId();

        shuffle();
        deal();
        tellBanker();
        updateLastOperateTime();
    }

    public void startGame(List<Long> users, Room room) {
        this.room = (RoomXuanQiQi) room;
        init(users);
        updateLastOperateTime();
        //通知其他人游戏已经开始
        MsgSender.sendMsg2Player(new ResponseVo("gameService", "gameBegin", "ok"), this.getUsers());
    }


    /**
     * 洗牌
     */
    protected void shuffle() {
        for (int i = 25; i < 43; i++) {
            cards.add(i);
            randamCards.add(i);
        }
        cards.add(45);
        cards.add(46);
        cards.add(49);
        cards.add(50);
        cards.add(53);
        cards.add(54);
        randamCards.add(45);
        randamCards.add(46);
        randamCards.add(49);
        randamCards.add(50);
        randamCards.add(53);
        randamCards.add(54);
        Collections.shuffle(cards);
    }

    /**
     * 发牌
     */
    protected void deal() {
        for (PlayerCardInfoXuanQiQi playerCardInfo : playerCardInfos.values()) {
            for (int i = 0; i < 8; i++) {
                playerCardInfo.handCards.add(cards.remove(0));
            }
            //通知发牌
            MsgSender.sendMsg2Player(new ResponseVo("gameService", "deal", playerCardInfo.handCards), playerCardInfo.userId);
        }
    }

    /**
     * 搬牌
     */
    protected void tellBanker(){
        Collections.shuffle(randamCards);
        Map<Long,Integer> randamCardOfPlayer = new HashMap<>();
        int temp = 0;
        for (PlayerCardInfoXuanQiQi playerCardInfo : playerCardInfos.values()) {
            randamCardOfPlayer.put(playerCardInfo.getUserId(),randamCards.get(temp));
            temp++;
        }
        MsgSender.sendMsg2Player(new ResponseVo("gameService", "tellRandomCard", randamCardOfPlayer), users);

        //0和1比，0大
        if(UtilXuanQiQi.getOneCardWin(randamCardOfPlayer.get(playerCardInfos.get(0).getUserId()),randamCardOfPlayer.get(playerCardInfos.get(1).getUserId()))){
            if(UtilXuanQiQi.getOneCardWin(randamCardOfPlayer.get(playerCardInfos.get(0).getUserId()),randamCardOfPlayer.get(playerCardInfos.get(2).getUserId()))){
                room.setBankerId(playerCardInfos.get(0).getUserId());
            }else{
                room.setBankerId(playerCardInfos.get(2).getUserId());
            }
        }else{//0和1比,1大
            if(UtilXuanQiQi.getOneCardWin(randamCardOfPlayer.get(playerCardInfos.get(1).getUserId()),randamCardOfPlayer.get(playerCardInfos.get(2).getUserId()))){
                room.setBankerId(playerCardInfos.get(1).getUserId());
            }else{
                room.setBankerId(playerCardInfos.get(2).getUserId());
            }
        }
        chuPaiId = room.getBankerId();
        operatId = room.getBankerId();
        playerCardInfos.get(room.getBankerId()).setCanSetMultiple("1");
        MsgSender.sendMsg2Player(new ResponseVo("gameService", "tellBankerId", room.getBankerId()), users);
        MsgSender.sendMsg2Player(new ResponseVo("gameService", "canDouble", room.getBankerId()), room.getBankerId());
    }

    /**
     * 游戏结束
     */
    protected void gameOver(){

    }

    public PlayerCardInfoXuanQiQi getGameTypePlayerCardInfo() {
        switch (room.getGameType()) {
            case "1":
                return new PlayerCardInfoXuanQiQi();
            default:
                return new PlayerCardInfoXuanQiQi();
        }
    }


    //==============================操作协议========================================

    //庄设置双倍
    public int setMultiple(long userId,int multiple){
        bankerMultiple = multiple;
        playerCardInfos.get(room.getBankerId()).setCanSetMultiple("0");
        MsgSender.sendMsg2Player(new ResponseVo("gameService", "canChoose", room.getBankerId()), room.getBankerId());
        playerCardInfos.get(room.getBankerId()).setCanSetMultiple("1");
        return 0;
    }

    //宣牌
    public int xuan(long userId) {
        //TODO XuanParam参数未设置
        if(xuanOrGuo.get(userId)==0){
            xuanOrGuo.put(userId,1);
            MsgSender.sendMsg2Player(new ResponseVo("gameService", "canChuPai", chuPaiId), users);
        }
        return 0;
    }

    //扣牌
    public int kou(long userId) {
        xuanOrGuo.put(userId,2);
        boolean isAllKou = allKou();
        if(isAllKou){
            gameOver();
        }
        if(chuPaiId==userId){//出牌的就是他，通知下一个人是否宣
            xuanOrGuo.put(userId,2);
            long canXuanUserId = nextTurnId(userId);
            operatId = canXuanUserId;
            MsgSender.sendMsg2Player(new ResponseVo("gameService", "canXuan", canXuanUserId), users);
        }
        return 0;
    }

    //查看是否全扣
    private boolean allKou(){
        boolean b = true;
        a:for (long l:xuanOrGuo.keySet()) {
            if(xuanOrGuo.get(l)!=2){
                b=false;
                break a;
            }
        }
        return b;
    }

    /**
     * 出牌
     * @param userId
     * @param card1
     * @param card2
     * @param card3
     * @return
     */
    public int play(long userId,int cardNumber,int card1,int card2,int card3){

        //存到player
        List<Integer> tempList = new ArrayList<>();
        if(cardNumber==1){
            tempList.add(card1);
            playerCardInfos.get(userId).handCards.remove(card1);
        }else if(cardNumber==2){
            tempList.add(card1);
            tempList.add(card2);
            playerCardInfos.get(userId).handCards.remove(card1);
            playerCardInfos.get(userId).handCards.remove(card2);
        }else if(cardNumber==3){
            tempList.add(card1);
            tempList.add(card2);
            tempList.add(card3);
            playerCardInfos.get(userId).handCards.remove(card1);
            playerCardInfos.get(userId).handCards.remove(card2);
            playerCardInfos.get(userId).handCards.remove(card3);
        }
        playerCardInfos.get(userId).setPlayCards(tempList);
        //手牌删除牌


        if(ifChuPai.values().contains(1) && !ifChuPai.values().contains(2) && !ifChuPai.values().contains(3)){
            ifChuPai.put(userId,2);
        }else if (ifChuPai.values().contains(2) && !ifChuPai.values().contains(3)){
            ifChuPai.put(userId,3);
        }else if (!ifChuPai.values().contains(1) && !ifChuPai.values().contains(2) && !ifChuPai.values().contains(3)){
            ifChuPai.put(userId,1);
        }

        int chuPaiCount = chuPaiIndex();
        if(chuPaiCount ==1){//第一个出牌，通知下家出
            MsgSender.sendMsg2Player(new ResponseVo("gameService", "canChuPai", chuPaiId), users);
        }else if(chuPaiCount ==2){//第二个人出牌，比牌，通知下家出
            PlayerCardInfoXuanQiQi p1 = null;
            PlayerCardInfoXuanQiQi p2 = null;
            for (Long l:ifChuPai.keySet()) {
                if(ifChuPai.get(l)==1){
                    p1 =playerCardInfos.get(l);
                }else if(ifChuPai.get(l)==2){
                    p2 =playerCardInfos.get(l);
                }
            }
            compareCard(cardNumber,p1,p2);
        }else if(chuPaiCount ==3){//第三个人出牌，存记录，分罗
            PlayerCardInfoXuanQiQi p1 = null;
            PlayerCardInfoXuanQiQi p2 = null;
            long winnerId = 0l;
            for (long l:compareCard.keySet()) {//取前两个人比牌赢的
                if(compareCard.get(l)==1){
                    winnerId = l;
                }
            }
            p1 =playerCardInfos.get(winnerId);
            for (Long l:ifChuPai.keySet()) {
                if(ifChuPai.get(l)==3){
                    p2 =playerCardInfos.get(l);
                }
            }
            compareCard(cardNumber,p1,p2);

            setWinCardAndCardType();//存记录分罗

            //TODO 清记录，进入下一宣牌环节
            cleanRecord();
        }

        return 0;
    }

    //查看是否全出过牌
    private boolean allPlayCard(){
        boolean b = true;
        a:for (long l:ifChuPai.keySet()) {
            if(ifChuPai.get(l)!=1){
                b=false;
                break a;
            }
        }
        return b;
    }

    //查看第几个人出过牌
    private int chuPaiIndex(){
        int temp = 0;
        for (long l:ifChuPai.keySet()) {
            if(ifChuPai.get(l)>0){
                temp++;
            }
        }
        return temp;
    }

    /**
     * 比牌
     * @param cardNumber
     * @param p1
     * @param p2
     */
    private void compareCard(int cardNumber,PlayerCardInfoXuanQiQi p1,PlayerCardInfoXuanQiQi p2){
        if(cardNumber==1){//比1张
            boolean p1Win = UtilXuanQiQi.getOneCardWin(p1.playCards.get(0),p2.playCards.get(0));
            if(p1Win){
                compareCard.put(p1.getUserId(),1);
                compareCard.put(p2.getUserId(),0);
            }else{
                compareCard.put(p1.getUserId(),0);
                compareCard.put(p2.getUserId(),1);
            }
        }else if(cardNumber==2){
            boolean p1Win = UtilXuanQiQi.getTwoCardWin(p1.playCards.get(0),p1.playCards.get(1),p2.playCards.get(0),p2.playCards.get(1));
            if(p1Win){
                compareCard.put(p1.getUserId(),1);
                compareCard.put(p2.getUserId(),0);
            }else{
                compareCard.put(p1.getUserId(),0);
                compareCard.put(p2.getUserId(),1);
            }
        }else if (cardNumber==3){
            boolean p1Win = UtilXuanQiQi.getThreeCardWin(p1.playCards.get(0),p1.playCards.get(1),p1.playCards.get(2),p2.playCards.get(0),p2.playCards.get(1),p2.playCards.get(2));
            if(p1Win){
                compareCard.put(p1.getUserId(),1);
                compareCard.put(p2.getUserId(),0);
            }else{
                compareCard.put(p1.getUserId(),0);
                compareCard.put(p2.getUserId(),1);
            }
        }
    }


    /**
     * 存记录，分罗
     */
    private void setWinCardAndCardType(){
        long finalWinnerId = 0l;
        a:for (long l:compareCard.keySet()) {//取最后赢的人
            if(compareCard.get(l)==1){
                finalWinnerId = l;
                break a;
            }
        }
        List<Integer> tempCards = new ArrayList<>();
        for (long l:playerCardInfos.keySet()) {
            tempCards.addAll(playerCardInfos.get(l).getPlayCards());
        }
        playerCardInfos.get(finalWinnerId).setWinCards(tempCards);//设置赢的

        Map<Integer,Boolean> tempCardsType= new HashMap<>();//罗上牌明或扣的状态
        for (long l:ifChuPai.keySet()) {
            if(ifChuPai.get(l)==1 && finalWinnerId ==l){//第一个出牌的赢
                for (long ll:playerCardInfos.keySet()) {
                    if(ll==finalWinnerId){
                        for (Integer i:playerCardInfos.get(ll).playCards) {
                            tempCardsType.put(i,true);
                        }
                    }else{
                        for (Integer i:playerCardInfos.get(ll).playCards) {
                            tempCardsType.put(i,false);
                        }
                    }
                }
            }else if(ifChuPai.get(l)==2 && finalWinnerId ==l){//第2个出牌的赢
                for (long ll:playerCardInfos.keySet()) {
                    if(ifChuPai.get(ll)==3){
                        for (Integer i:playerCardInfos.get(ll).playCards) {
                            tempCardsType.put(i,false);
                        }
                    }else {
                        for (Integer i:playerCardInfos.get(ll).playCards) {
                            tempCardsType.put(i,true);
                        }
                    }
                }
            }else if(ifChuPai.get(l)==3 && finalWinnerId ==l){//第3个出牌的赢
                for (long ll:playerCardInfos.keySet()) {
                    for (Integer i:playerCardInfos.get(ll).playCards) {
                        tempCardsType.put(i,true);
                    }
                }
            }
        }
        playerCardInfos.get(finalWinnerId).getCardsType().putAll(tempCardsType);//设置罗的明/扣

        Map<String, Object> result = new HashMap<>();
        result.put("finalWinnerId",finalWinnerId);
        result.put("cardsType",tempCardsType);
        ResponseVo vo = new ResponseVo("gameService", "winResult", result);
        MsgSender.sendMsg2Player(vo, users);
    }

    private void cleanRecord(){

    }






















    //==========================getter and setter============================

    public Map<Long, PlayerCardInfoXuanQiQi> getPlayerCardInfos() {
        return playerCardInfos;
    }

    public void setPlayerCardInfos(Map<Long, PlayerCardInfoXuanQiQi> playerCardInfos) {
        this.playerCardInfos = playerCardInfos;
    }

    public long getChuPaiId() {
        return chuPaiId;
    }

    public void setChuPaiId(long chuPaiId) {
        this.chuPaiId = chuPaiId;
    }

    public long getOperatId() {
        return operatId;
    }

    public void setOperatId(long operatId) {
        this.operatId = operatId;
    }

    public List<Integer> getCards() {
        return cards;
    }

    public void setCards(List<Integer> cards) {
        this.cards = cards;
    }

    public List<Integer> getRandamCards() {
        return randamCards;
    }

    public void setRandamCards(List<Integer> randamCards) {
        this.randamCards = randamCards;
    }

    public RoomXuanQiQi getRoom() {
        return room;
    }

    public void setRoom(RoomXuanQiQi room) {
        this.room = room;
    }

    public int getBankerMultiple() {
        return bankerMultiple;
    }

    public void setBankerMultiple(int bankerMultiple) {
        this.bankerMultiple = bankerMultiple;
    }

    public Map<Long, Integer> getXuanOrGuo() {
        return xuanOrGuo;
    }

    public void setXuanOrGuo(Map<Long, Integer> xuanOrGuo) {
        this.xuanOrGuo = xuanOrGuo;
    }

    public Map<Long, Integer> getIfChuPai() {
        return ifChuPai;
    }

    public void setIfChuPai(Map<Long, Integer> ifChuPai) {
        this.ifChuPai = ifChuPai;
    }

    public Map<Long, Integer> getCompareCard() {
        return compareCard;
    }

    public void setCompareCard(Map<Long, Integer> compareCard) {
        this.compareCard = compareCard;
    }
}
