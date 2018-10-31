package com.code.server.game.poker.tiandakeng;

import com.code.server.constant.response.ResponseVo;
import com.code.server.game.room.Game;
import com.code.server.game.room.Room;
import com.code.server.game.room.kafka.MsgSender;

import java.util.*;

/**
 * Created by sunxianping on 2018-10-18.
 */
public class GameTDK extends Game {

    private static final String SERVICE_NAME = "gameTDKService";
    private static final int model_带王 = 1;
    private static final int model_王中炮 = 2;
    private static final int model_抓A必泡 = 3;
    private static final int model_公张随豹 = 4;
    private static final int model_公张随点 = 5;
    private static final int model_烂锅翻倍 = 6;
    private static final int model_末脚踢服 = 7;
    private static final int model_亮底 = 8;
    private static final int model_把踢 = 9;
    private static final int model_末踢 = 10;

    private static final int model_无托管 = 11;
    private static final int model_90秒托管 = 12;
    private static final int model_180秒托管 = 13;
    private static final int model_允许观战 = 14;
    private static final int model_开启GPS = 15;
    private static final int model_禁言 = 16;

    private static final int model_半坑_8 = 17;
    private static final int model_半坑_9 = 18;
    private static final int model_半坑_10 = 19;
    private static final int model_半坑_J = 20;
    private static final int model_全坑_2 = 21;

    private static final int model_莆田半坑_5 = 22;
    private static final int model_莆田全坑_2 = 23;


    private static final int STATE_DEAL = 0;
    private static final int STATE_BET = 1;
    private static final int STATE_KICK = 2;

    protected RoomTDK room;
    protected Map<Long, PlayerInfoTDK> playerCardInfos = new HashMap<>();
    protected List<Integer> cards = new ArrayList<>();//牌
    protected List<Long> aliveUserList = new ArrayList<>();
    //弃牌的人
    protected List<Long> giveUpList = new ArrayList<>();
    //下的注
    protected List<Integer> bets = new ArrayList<>();
    //下注信息
    protected BetInfo betInfo;

    protected KickInfo kickInfo;

    protected int state = 0;

    protected int handCardNum = 0;

    /**
     * 开始游戏
     *
     * @param users
     * @param room
     */
    public void startGame(List<Long> users, RoomTDK room) {
        this.room = room;
        init(users, room.getBankerId());
        updateLastOperateTime();
        //通知其他人游戏已经开始
        MsgSender.sendMsg2Player(new ResponseVo(SERVICE_NAME, "gameBegin", "ok"), this.getUsers());
    }

    /**
     * 初始化
     *
     * @param users
     * @param bankerId
     */
    public void init(List<Long> users, long bankerId) {
        //初始化玩家
        for (Long uid : users) {
            PlayerInfoTDK playerCardInfo = new PlayerInfoTDK();
            playerCardInfo.userId = uid;
            playerCardInfos.put(uid, playerCardInfo);
        }
        this.users.addAll(users);
        this.aliveUserList.addAll(users);

        //洗牌
        shuffle();

        //如果有烂锅的情况 不下底注
        if (!this.room.isLanGuo()) {
            //todo 筹码是否带过来
            //下底注
            bottomBet(1);
        }
        //发牌
        deal();

    }


    /**
     * 洗牌
     */
    protected void shuffle() {
        //必有A
        cards.add(1);
        cards.add(2);
        cards.add(3);
        cards.add(4);

        //必有J Q K
        for (int i = 0; i < 12; i++) {
            cards.add(41 + i);
        }

        //从10起
        if (isHasMode(model_半坑_10)) {
            for (int i = 0; i < 4; i++) {
                cards.add(37 + i);
            }
        }

        //从9起
        if (isHasMode(model_半坑_9)) {
            for (int i = 0; i < 8; i++) {
                cards.add(33 + i);
            }
        }

        //从8起
        if (isHasMode(model_半坑_8)) {
            for (int i = 0; i < 12; i++) {
                cards.add(29 + i);
            }
        }

        //从5起
        if (isHasMode(model_莆田半坑_5)) {
            for (int i = 0; i < 24; i++) {
                cards.add(17 + i);
            }
        }

        //从2起
        if (isHasMode(model_莆田全坑_2 )|| isHasMode(model_全坑_2)) {
            for (int i = 0; i < 36; i++) {
                cards.add(5 + i);
            }
        }

        //带王
        if (isHasMode(model_带王)) {
            cards.add(53);
            cards.add(54);
        }

        Collections.shuffle(cards);
    }

    /**
     * 下底注
     *
     * @param num
     */
    protected void bottomBet(int num) {
        for (PlayerInfoTDK playerInfoTDK : playerCardInfos.values()) {
            playerInfoTDK.addBet(num);
            //通知下注
            pushBet(playerInfoTDK.getUserId(), num);
        }
    }

    /**
     * 推送下注
     *
     * @param userId
     * @param num
     */
    protected void pushBet(long userId, int num) {
        Map<String, Object> r = new HashMap<>();
        r.put("userId", userId);
        r.put("num", num);
        pushToAll(new ResponseVo(SERVICE_NAME, "bet", r));
    }

    /**
     * 给所有人推送 包括观战的人
     *
     * @param responseVo
     */
    protected void pushToAll(ResponseVo responseVo) {
        List<Long> allUser = new ArrayList<>();
        allUser.addAll(users);
        allUser.addAll(this.room.watchUser);
        MsgSender.sendMsg2Player(responseVo, allUser);
    }

    /**
     * 发牌
     */
    protected void deal() {
        //切换状态
        this.state = STATE_DEAL;

        //每人发三张牌
        for (PlayerInfoTDK playerInfoTDK : playerCardInfos.values()) {
            playerInfoTDK.deal(cards.remove(0), false);
            playerInfoTDK.deal(cards.remove(0), false);
            playerInfoTDK.deal(cards.remove(0), false);
        }

        Map<Long, Map<String, Object>> playerCards = new HashMap<>();
        for (PlayerInfoTDK playerInfoTDK : this.playerCardInfos.values()) {
            playerCards.put(playerInfoTDK.getUserId(), playerInfoTDK.getHandCardsInfo());
        }

        //推送发牌信息
        pushToAll(new ResponseVo(SERVICE_NAME, "deal", playerCards));

        handCardNum = 3;

        //开始下注
        betStart();
    }


    /**
     * 发来 每轮
     */
    protected void deal_round(){
        int remainUser = users.size() - giveUpList.size();
        int remianCards = cards.size();
        boolean isEnough = remianCards >= remainUser;

        //牌足够 每人一张
        if (isEnough) {
            for(long userId : getAliveUserListOrder()){
                PlayerInfoTDK playerInfoTDK = playerCardInfos.get(userId);
                int card = cards.remove(0);
                playerInfoTDK.deal(card, false);
            }

        }else{//有公张
            //需要公张的人数
            int needGZNum =  remainUser - remianCards;
            List<Long> users = getAliveUserListOrder();
            //正常发牌的人数  牌数-1
            int haveCardUserNum = remianCards - 1;
            //需要公张的user
            List<Long> needCommonUser = new ArrayList<>();
            //正常发牌
            for(int i=0;i<users.size();i++) {
                PlayerInfoTDK playerInfoTDK = playerCardInfos.get(users.get(i));
                if (i < haveCardUserNum) {
                    int card = cards.remove(0);
                    playerInfoTDK.deal(card, false);
                }else{
                    needCommonUser.add(users.get(i));
                }
            }
            //发公张
            int commonCard = cards.remove(0);
            for (long userId : needCommonUser) {
                PlayerInfoTDK playerInfoTDK = playerCardInfos.get(userId);
                playerInfoTDK.deal(commonCard, true);
            }
        }

        long firstUser = findFirstBetUser();

        for (PlayerInfoTDK playerInfoTDK : playerCardInfos.values()) {

        }


        handCardNum ++;
    }

    /**
     * 开始下注
     */
    protected void betStart() {
        this.state = STATE_BET;

        //找到名牌点数最大的玩家 下注

        long betUser = findFirstBetUser();
        //生成下注信息
        List<Long> needBetUser = getAliveUserListOrder();
        this.betInfo = new BetInfo(betUser, needBetUser);
        //通知他下注
        Map<String, Object> result = new HashMap<>();
        result.put("userId", betUser);
        pushToAll(new ResponseVo(SERVICE_NAME, "betStart", result));
    }


    /**
     * 玩家下注
     *
     * @param userId
     * @param num
     * @param isGiveUp
     * @return
     */
    public int bet(long userId, int num, boolean isGiveUp) {

        PlayerInfoTDK playerInfoTDK = this.playerCardInfos.get(userId);
        this.betInfo.bet(userId, isGiveUp);
        if (isGiveUp) {
            playerInfoTDK.setGiveUp(isGiveUp);
            this.giveUpList.add(userId);
            this.aliveUserList.remove(userId);

            //只剩最后一个人
            if (users.size() - this.giveUpList.size() == 1) {
                gameOver();
            }
        } else {
            this.bets.add(num);
            playerInfoTDK.addBet(num);
            //都下过注 进入踢阶段
            if (this.betInfo.isBetOver()) {

                //把踢进入踢牌阶段
                if (isHasMode(model_把踢)) {
                    //踢牌阶段
                    kickStart();

                }else{//发牌

                }

            }
        }

        //下注推送
        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("num", num);
        result.put("isGiveUp", isGiveUp);
        pushToAll(new ResponseVo(SERVICE_NAME, "betResp", result));
        return 0;
    }


    /**
     * 踢牌阶段开始
     */
    private void kickStart() {
        this.state = STATE_KICK;

    }

    /**
     * 是否有模式
     * @param mode
     * @return
     */
    private boolean isHasMode(int mode){
        return Room.isHasMode(mode, this.room.getOtherMode());
    }

    /**
     * 算分
     */
    protected void gameOver() {

    }


    private List<Long> getAliveUserListOrder() {
        long banker = this.room.getBankerId();
        List<Long> users = new ArrayList<>();

        long nextUser = banker;
        for (int i = 0; i < users.size(); i++) {
            users.add(nextUser);
            nextUser = nextTurnId(nextUser);
        }

        //删除已经弃牌的人
        users.removeAll(giveUpList);
        return users;

    }


    protected long nextTurnId(long curId) {
        int index = users.indexOf(curId);

        int nextId = index + 1;
        if (nextId >= users.size()) {
            nextId = 0;
        }
        return users.get(nextId);
    }

    protected List<Long> getNewList(long first, List<Long> users) {

        return null;
    }

    /**
     * 找到第一个下注的人
     *
     * @return
     */
    private long findFirstBetUser() {

        List<Long> users = getAliveUserListOrder();
        //按顺序比较
        int score = 0;
        long maxUser = 0;
        boolean isGongZhangSuiBao = Room.isHasMode(model_公张随豹, this.room.getOtherMode());
        boolean isABiPao = Room.isHasMode(model_抓A必泡, this.room.getOtherMode());
        boolean isWangZhongPao = Room.isHasMode(model_王中炮, this.room.getOtherMode());
        for (long userId : users) {
            int s = playerCardInfos.get(userId).getCardScore(isGongZhangSuiBao, isABiPao, isWangZhongPao, false);
            //分数相同 离banker近的赢
            if (s > score) {
                score = s;
                maxUser = userId;
            }
        }
        return maxUser;
    }
}
