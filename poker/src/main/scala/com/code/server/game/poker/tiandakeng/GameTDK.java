package com.code.server.game.poker.tiandakeng;

import com.code.server.constant.response.*;
import com.code.server.game.room.Game;
import com.code.server.game.room.Room;
import com.code.server.game.room.kafka.MsgSender;
import com.code.server.game.room.service.RoomManager;
import com.code.server.util.IdWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by sunxianping on 2018-10-18.
 */
public class GameTDK extends Game {

    private static final Logger logger = LoggerFactory.getLogger(GameTDK.class);

    private static final String SERVICE_NAME = "gameTDKService";
    static final int model_带王 = 1;
    static final int model_王中炮 = 2;
    static final int model_抓A必泡 = 3;
    static final int model_公张随豹 = 4;
    static final int model_公张随点 = 5;
    static final int model_烂锅翻倍 = 6;
    static final int model_末脚踢服 = 7;
    static final int model_亮底 = 8;
    static final int model_把踢 = 9;
    static final int model_末踢 = 10;

    static final int model_无托管 = 11;
    static final int model_90秒托管 = 12;
    static final int model_180秒托管 = 13;
    static final int model_允许观战 = 14;
    static final int model_开启GPS = 15;
    static final int model_禁言 = 16;

    static final int model_半坑_8 = 17;
    static final int model_半坑_9 = 18;
    static final int model_半坑_10 = 19;
    static final int model_半坑_J = 20;
    static final int model_全坑_2 = 21;

    static final int model_莆田半坑_5 = 22;
    static final int model_莆田全坑_2 = 23;


    static final int STATE_DEAL = 0;
    static final int STATE_BET = 1;
    static final int STATE_KICK = 2;
    static final int STATE_KICK_BET = 3;
    static final int STATE_TWO_KICK = 4;
    static final int STATE_TWO_KICK_BET = 5;
    static final int STATE_OPEN = 6;

    //room
    protected RoomTDK room;
    //玩家
    protected Map<Long, PlayerInfoTDK> playerCardInfos = new HashMap<>();
    //所有的牌
    protected List<Integer> cards = new ArrayList<>();
    //还存活的玩家
    protected List<Long> aliveUserList = new ArrayList<>();
    //弃牌的人
    protected List<Long> giveUpList = new ArrayList<>();
    //下的注
    protected List<Integer> bets = new ArrayList<>();
    //下注信息
    protected BetInfo betInfo;
    //踢牌信息
    protected KickInfo kickInfo;
    //状态
    protected int state = 0;
    //手牌数
    protected int handCardNum = 0;
    //开牌的玩家
    protected long openUser = 0;
    //操作描述(客户端显示需要)
    protected Map<Long, String> operateDesc = new HashMap<>();



    /**
     * 开始游戏
     *
     * @param users
     * @param room
     */
    public void startGame(List<Long> users, Room room) {
        this.room = (RoomTDK) room;
        init(users, room.getBankerId());
        updateLastOperateTime();
        //通知其他人游戏已经开始
//        MsgSender.sendMsg2Player(new ResponseVo(SERVICE_NAME, "gameBegin", "ok"), this.getUsers());
        pushToAll(new ResponseVo(SERVICE_NAME, "gameBegin", "ok"));
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
        } else {
            this.bets.addAll(this.room.getLanguoBets());
        }
        //发牌
        deal();


    }


    /**
     * 洗牌
     */
    protected void shuffle() {
        CardUtil.shuffleCard(this, cards);
    }

    /**
     * 下底注
     *
     * @param num
     */
    protected void bottomBet(int num) {
        for (PlayerInfoTDK playerInfoTDK : playerCardInfos.values()) {
            this.bets.add(num);
            playerInfoTDK.addBet(num, handCardNum);
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
        pushToAll(new ResponseVo(SERVICE_NAME, "pushBet", r));
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
            playerCards.put(playerInfoTDK.getUserId(), playerInfoTDK.getHandCardsInfo(true));
        }

        //推送发牌信息
        pushToAll(new ResponseVo(SERVICE_NAME, "deal", playerCards));

        handCardNum = 3;

        //开始下注
        betStart();
    }


    /**
     * 发牌 每轮
     */
    protected void deal_round() {
        this.state = STATE_DEAL;
        this.betInfo = null;
        this.kickInfo = null;

        int remainUser = users.size() - giveUpList.size();
        int remianCards = cards.size();
        boolean isEnough = remianCards >= remainUser;

        //牌足够 每人一张
        if (isEnough) {
            for (long userId : getUserListBeginWithMaxCardScoreUser()) {
                PlayerInfoTDK playerInfoTDK = playerCardInfos.get(userId);
                int card = cards.remove(0);
                playerInfoTDK.deal(card, false);
            }

        } else {//有公张
            //需要公张的人数
            int needGZNum = remainUser - remianCards;
            List<Long> users = getUserListBeginWithMaxCardScoreUser();
            //正常发牌的人数  牌数-1
            int haveCardUserNum = remianCards - 1;
            //需要公张的user
            List<Long> needCommonUser = new ArrayList<>();
            //正常发牌
            for (int i = 0; i < users.size(); i++) {
                PlayerInfoTDK playerInfoTDK = playerCardInfos.get(users.get(i));
                if (i < haveCardUserNum) {
                    int card = cards.remove(0);
                    playerInfoTDK.deal(card, false);
                } else {
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
        handCardNum++;
        //通知发牌情况
        Map<Long, Map<String, Object>> playerCards = new HashMap<>();
        for (PlayerInfoTDK playerInfoTDK : this.playerCardInfos.values()) {
            playerCards.put(playerInfoTDK.getUserId(), playerInfoTDK.getHandCardsInfo(true));
        }

        //推送发牌信息
        pushToAll(new ResponseVo(SERVICE_NAME, "deal_round", playerCards));

        //发玩牌 通知下注
        betStart();

    }

    /**
     * 开始下注
     */
    protected void betStart() {
        this.state = STATE_BET;
        this.kickInfo = null;

        //找到名牌点数最大的玩家 下注
        long betUser = findMaxCardScoreUser();
        //生成下注信息
        List<Long> needBetUser = getAliveUserBeginWithBanker();
        this.betInfo = new BetInfo(betUser, needBetUser);
        //通知他下注
        Map<String, Object> result = new HashMap<>();
        result.put("userId", betUser);
        pushToAll(new ResponseVo(SERVICE_NAME, "betStart", result));
    }


    /**
     * 下注
     * @param userId
     * @param num
     * @param isGiveUp
     * @return
     */
    public int bet(long userId, int num, boolean isGiveUp,String desc) {
        int rtn = 0;
        switch (state) {
            case STATE_BET:
                rtn = bet_common(userId, num, isGiveUp);
                break;
            case STATE_KICK_BET:
                rtn = bet_kick(userId, isGiveUp);
                break;
            case STATE_TWO_KICK_BET:
                rtn = bet_kick_two(userId, isGiveUp);
                break;
        }
        if (rtn != 0) {
            return rtn;
        }
        operateDesc.put(userId, desc);
        MsgSender.sendMsg2Player(SERVICE_NAME, "bet", 0,userId);
        return 0;
    }

    /**
     * 踢
     *
     * @param userId
     * @param num
     * @param isKick
     * @return
     */
    public int kick(long userId, int num, boolean isKick,String desc) {
        int rtn = 0;
        switch (state) {
            case STATE_KICK:
                rtn = kick_common(userId, num, isKick);
                break;
            case STATE_TWO_KICK:
                rtn = kick_two(userId, num, isKick);
                break;
        }
        if (rtn != 0) {
            return rtn;
        }
        operateDesc.put(userId, desc);
        MsgSender.sendMsg2Player(SERVICE_NAME, "kick", 0,userId);
        return 0;
    }


    /**
     * 看牌
     * @param userId
     * @return
     */
    public int lookCard(long userId) {
        PlayerInfoTDK playerInfoTDK = playerCardInfos.get(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        List<Integer> cards = new ArrayList<>();
        if (playerInfoTDK.getCards().size() >= 2) {
            cards.add(playerInfoTDK.getCards().get(0));
            cards.add(playerInfoTDK.getCards().get(1));
        }
        result.put("cards", cards);

        MsgSender.sendMsg2Player(SERVICE_NAME, "lookCard", result, userId);
        Map<String, Object> r = new HashMap<>();
        r.put("userId", userId);
        pushToAll(new ResponseVo(SERVICE_NAME, "someoneLook", r));
        return 0;
    }

    /**
     * 开牌
     *
     * @param userId
     * @return
     */
    public int openCard(long userId) {
        //todo 验证开牌
        if (this.state != STATE_OPEN) {
            return ErrorCode.CANNOT_OPEN;
        }
        Map<String, Object> r = new HashMap<>();
        r.put("userId", userId);
        pushToAll(new ResponseVo(SERVICE_NAME, "openCardNotify",r));

        boolean isGongZhangSuiBao = isHasMode(model_公张随豹);
        boolean isABiPao = isHasMode(model_抓A必泡);
        boolean isWangZhongPao = isHasMode(model_王中炮);
        //算出所有人的牌面分数
        int maxCardScore = 0;
        long maxUser = 0;
        for (PlayerInfoTDK playerInfoTDK : playerCardInfos.values()) {
            if (aliveUserList.contains(playerInfoTDK.getUserId())) {
                int score = playerInfoTDK.computeScore(isGongZhangSuiBao, isABiPao, isWangZhongPao);
                if (score >= maxCardScore) {
                    maxCardScore = score;
                    maxUser = playerInfoTDK.getUserId();
                }
            }
        }
        long winnerId = 0;
        //莆田玩法没有烂锅
        if (isHasMode(model_莆田半坑_5) || isHasMode(model_莆田全坑_2)) {
            winnerId = findMaxCardScoreUser(true);
        } else {
            int maxScoreCount = 0;
            //如果找不到最大分数玩家 烂锅
            for (PlayerInfoTDK playerInfoTDK : playerCardInfos.values()) {
                if (aliveUserList.contains(playerInfoTDK.getUserId())) {
                    if (maxCardScore == playerInfoTDK.getCardScore()) {
                        maxScoreCount++;
                    }
                }
            }
            //是否烂锅
            if (maxScoreCount >= 2) {
                this.room.setLanGuo(true);
                this.room.getLanguoBets().addAll(this.bets);

            } else {
                winnerId = maxUser;
            }
        }

        gameOver(winnerId);

        MsgSender.sendMsg2Player(SERVICE_NAME, "openCard", 0, userId);
        return 0;
    }


    /**
     * 玩家下注
     *
     * @param userId
     * @param num
     * @param isGiveUp
     * @return
     */
    private int bet_common(long userId, int num, boolean isGiveUp) {
        //检测是否能下注
        PlayerInfoTDK playerInfoTDK = this.playerCardInfos.get(userId);

        if (this.betInfo == null || this.betInfo.curBetUser != userId) {
            return ErrorCode.CANNOT_BET;
        }

        //下注推送
        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("num", num);
        result.put("isGiveUp", isGiveUp);
        pushToAll(new ResponseVo(SERVICE_NAME, "betResp", result));

        //下注
        this.betInfo.bet(userId, isGiveUp);

        //弃牌
        if (isGiveUp) {
            playerInfoTDK.setGiveUp(isGiveUp);
            this.giveUpList.add(userId);
            this.aliveUserList.remove(userId);

            //只剩最后一个人
            if (aliveUserList.size() == 1) {
                gameOver(aliveUserList.get(0));
                return 0;
            }
        } else {
            //设置改下多少注
            if (this.betInfo.betNum == 0) {
                this.betInfo.betNum = num;
            }
            num = this.betInfo.betNum;
            //下注
            this.bets.add(num);
            playerInfoTDK.addBet(num, handCardNum);
        }
        //都下过注 进入踢阶段
        if (this.betInfo.isBetOver()) {
            //把踢进入踢牌阶段
            //把踢或者到了最后一轮或者没牌了
            if (isHasMode(model_把踢) || cards.size() == 0 || handCardNum == 5) {
                //踢牌阶段
                kickStart(findMaxCardScoreUser());

            } else {//发牌
                deal_round();
            }

        } else {
            //通知下一个人下注
            long nextUser = nextTurnId(userId);
            this.betInfo.curBetUser = nextUser;
            Map<String, Object> pleaseBetResult = new HashMap<>();

            pleaseBetResult.put("userId", nextUser);
            pushToAll(new ResponseVo(SERVICE_NAME, "followBet", pleaseBetResult));
        }

        return 0;
    }


    /**
     * 踢牌的下注
     *
     * @param userId
     * @param isGiveUp
     * @return
     */
    private int bet_kick(long userId, boolean isGiveUp) {
        //
        if (this.kickInfo == null || this.kickInfo.kickBetInfo == null || this.kickInfo.kickBetInfo.curBetUser != userId) {
            return ErrorCode.CANNOT_BET;
        }

        PlayerInfoTDK playerInfoTDK = playerCardInfos.get(userId);
        BetInfo betInfo = this.kickInfo.kickBetInfo;
        int num = betInfo.betNum;

        //下注推送
        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("num", num);
        result.put("isGiveUp", isGiveUp);
        pushToAll(new ResponseVo(SERVICE_NAME, "betResp", result));

        betInfo.bet(userId, isGiveUp);
        //弃牌
        if (isGiveUp) {
            playerInfoTDK.setGiveUp(true);
            this.giveUpList.add(userId);
            this.aliveUserList.remove(userId);
            //只剩一个人 结束
            if (aliveUserList.size() == 1) {
                gameOver(aliveUserList.get(0));
                return 0;
            }
        } else {//下注
            this.bets.add(num);
            playerInfoTDK.addBet(num, handCardNum);
        }

        //都下过注 进入踢阶段
        if (this.kickInfo.kickBetInfo.isBetOver()) {
            //踢牌阶段结束
            //是否发牌 或者 开牌
            if (this.kickInfo.isOver(aliveUserList)) {

                //发牌
                if (cards.size() > 0 && handCardNum != 5) {
                    deal_round();
                } else {
                    //是否是二人无限踢
                    if (isHasMode(model_末脚踢服) && aliveUserList.size() == 2) {
                        //开始无限踢
                        kickTwoStart(findMaxCardScoreUser());
                    } else {
                        //开牌
                        openStart();
                    }
                }

            } else {//通知下一轮踢牌
                long nextUser = nextTurnId(this.kickInfo.curKickUser);
                notifyNextUserKick(nextUser);
            }

        } else {
            //通知下一个人下注
            long nextUser = nextTurnId(userId);
            betInfo.curBetUser = nextUser;
            Map<String, Object> pleaseBetResult = new HashMap<>();
            pleaseBetResult.put("userId", nextUser);
            pushToAll(new ResponseVo(SERVICE_NAME, "followBet", pleaseBetResult));
        }

        return 0;
    }


    /**
     * 无限踢的下注
     *
     * @param userId
     * @param isGiveUp
     * @return
     */
    private int bet_kick_two(long userId, boolean isGiveUp) {
        if (this.kickInfo == null || this.kickInfo.kickBetInfo == null || this.kickInfo.kickBetInfo.curBetUser != userId) {
            return ErrorCode.CANNOT_BET;
        }
        PlayerInfoTDK playerInfoTDK = playerCardInfos.get(userId);
        BetInfo betInfo = this.kickInfo.kickBetInfo;
        int num = betInfo.betNum;

        //下注推送
        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("num", num);
        result.put("isGiveUp", isGiveUp);
        pushToAll(new ResponseVo(SERVICE_NAME, "betResp", result));

        betInfo.bet(userId, isGiveUp);
        //弃牌
        if (isGiveUp) {
            playerInfoTDK.setGiveUp(true);
            this.giveUpList.add(userId);
            this.aliveUserList.remove(userId);
            //结束
            gameOver(aliveUserList.get(0));

        } else {//下注
            this.bets.add(num);
            playerInfoTDK.addBet(num, handCardNum);
            this.kickInfo.kickBetInfo = null;
            //是否已经10轮了
            if (this.kickInfo.count >= 10) {
                openStart();
            } else {
                //通知自己是否再踢牌(二人无限踢中是依次询问,自己跟注后再问自己)
                notifyNextUserKickTwo(userId);
            }
        }
        return 0;
    }


    /**
     * 踢牌
     * @param userId
     * @param num
     * @param isKick
     * @return
     */
    private int kick_common(long userId, int num, boolean isKick) {
        //是否可以踢
        if (this.kickInfo == null || this.kickInfo.curKickUser != userId) {
            return ErrorCode.CANNOT_KICK;
        }

        //踢牌返回
        Map<String, Object> kickResp = new HashMap<>();
        kickResp.put("userId", userId);
        kickResp.put("num", num);
        kickResp.put("isKick", isKick);
        pushToAll(new ResponseVo(SERVICE_NAME, "kickResp", kickResp));

        //踢牌状态改变
        this.kickInfo.kick(userId, isKick);

        if (isKick) {
            //踢的话 进行踢牌下注
            kickBetStart();
            //处理
            kickInfo.initBetInfo(num, userId, aliveUserList);
            this.bets.add(num);
            //玩家下注
            PlayerInfoTDK playerInfoTDK = playerCardInfos.get(userId);
            playerInfoTDK.addBet(num, handCardNum);

            //通知下个人 下注
            long nextUser = nextTurnId(userId);
            kickInfo.kickBetInfo.curBetUser = nextUser;
            Map<String, Object> pleaseBetResult = new HashMap<>();
            pleaseBetResult.put("userId", nextUser);
            pushToAll(new ResponseVo(SERVICE_NAME, "followBet", pleaseBetResult));

        } else {
            //不踢的话 问下个人踢不踢
            long nextUser = nextTurnId(userId);
//            kickStart(nextUser);
            notifyNextUserKick(nextUser);
            //所有人都选过了
            if (this.kickInfo.isOver(aliveUserList)) {
                //发牌或者开牌
                //发牌
                if (cards.size() > 0 && handCardNum != 5) {
                    deal_round();
                } else {
                    openStart();
                }
            }
        }
        return 0;
    }

    /**
     * 无限踢中的踢牌
     *
     * @param userId
     * @param num
     * @param isKick
     * @return
     */
    private int kick_two(long userId, int num, boolean isKick) {
        if (this.kickInfo == null || this.kickInfo.curKickUser != userId) {
            return ErrorCode.CANNOT_KICK;
        }

        //踢牌返回
        Map<String, Object> kickResp = new HashMap<>();
        kickResp.put("userId", userId);
        kickResp.put("num", num);
        kickResp.put("isKick", isKick);
        pushToAll(new ResponseVo(SERVICE_NAME, "kickResp", kickResp));

        //踢牌次数+1
        kickInfo.addCount();

        if (isKick) {
            kickTwoBetStart();
            kickInfo.initBetInfo(num, userId, aliveUserList);
            this.bets.add(num);
            //玩家下注
            PlayerInfoTDK playerInfoTDK = playerCardInfos.get(userId);
            playerInfoTDK.addBet(num, handCardNum);

            //通知下个人 下注
            this.state = STATE_TWO_KICK_BET;
            long nextUser = nextTurnId(userId);
//            this.kickInfo.curKickUser = nextUser;
            this.kickInfo.kickBetInfo.curBetUser = nextUser;
            Map<String, Object> pleaseBetResult = new HashMap<>();
            pleaseBetResult.put("userId", nextUser);
            pushToAll(new ResponseVo(SERVICE_NAME, "followBet", pleaseBetResult));
        } else {
            //不踢的话 开牌
            openStart();
        }
        return 0;
    }


    /**
     * 开牌阶段
     */
    private void openStart() {
        this.state = STATE_OPEN;
        this.kickInfo = null;
        //通知开牌
        long openUser = findMaxCardScoreUser();
        this.openUser = openUser;
        Map<String, Object> map = new HashMap<>();
        map.put("userId", openUser);
        pushToAll(new ResponseVo(SERVICE_NAME, "pleaseOpen", map));
    }

    /**
     * 踢牌阶段开始
     */
    private void kickStart(long userId) {
        this.state = STATE_KICK;
        this.betInfo = null;
        this.kickInfo = new KickInfo(userId, aliveUserList);
        pushIsKick(userId);
    }


    /**
     * 通知下个人踢牌
     * @param userId
     */
    private void notifyNextUserKick(long userId) {
        this.state = STATE_KICK;
        this.betInfo = null;
        this.kickInfo.kickBetInfo = null;
        this.kickInfo.alreadyKickUser.add(userId);
        this.kickInfo.curKickUser = userId;
        pushIsKick(userId);

    }


    /**
     * 通知下个人踢牌(二人无限踢中)
     * @param userId
     */
    private void notifyNextUserKickTwo(long userId) {
        this.state = STATE_TWO_KICK;
        this.betInfo = null;
        this.kickInfo.kickBetInfo = null;
        this.kickInfo.alreadyKickUser.add(userId);
        this.kickInfo.curKickUser = userId;
        pushIsKick(userId);
    }
    /**
     * 二人无限踢 开始
     */
    private void kickTwoStart(long userId) {
        this.state = STATE_TWO_KICK;
        this.betInfo = null;
        this.kickInfo = new KickInfo(userId, aliveUserList);
        pushIsKick(userId);
    }

    /**
     * 推送谁踢
     *
     * @param userId
     */
    private void pushIsKick(long userId) {
        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        pushToAll(new ResponseVo(SERVICE_NAME, "isKick", result));
    }

    /**
     * 踢牌下注阶段
     */
    protected void kickBetStart() {
        this.state = STATE_KICK_BET;
    }

    /**
     * 无限踢下注阶段
     */
    protected void kickTwoBetStart() {
        this.state = STATE_TWO_KICK_BET;
    }


    /**
     * 算分
     */
    protected void gameOver(long winnerId) {
        //烂锅
        if (winnerId == 0) {
            this.room.setLanGuo(false);
            this.room.getLanguoBets().clear();
        }

        //结算
        compute(winnerId);
        sendResult(winnerId);
        genRecord();
        this.room.clearReadyStatus(true);
        sendFinalResult();
    }

    /**
     * 发结算
     * @param winnerId
     */
    private void sendResult(long winnerId) {
        Map<String, Object> result = new HashMap<>();
        result.put("winner", winnerId);
        Map<Long, PlayerCardInfoTDKVo> users = new HashMap<>();
        for (PlayerInfoTDK playerInfoTDK : playerCardInfos.values()) {
            users.put(playerInfoTDK.getUserId(), playerInfoTDK.toVoShowAllCard());
        }
        result.put("users", users);
        pushToAll(new ResponseVo(SERVICE_NAME, "gameResult", result));
    }


    /**
     * 算分
     * @param winnerId
     */
    private void compute(long winnerId) {
        if (winnerId == 0) return;
        int allScore = 0;
        for (PlayerInfoTDK playerInfoTDK : playerCardInfos.values()) {
            if (playerInfoTDK.getUserId() == winnerId) continue;
            playerInfoTDK.setScore(-playerInfoTDK.getAllBet());
            this.room.addUserSocre(playerInfoTDK.getUserId(), -playerInfoTDK.getAllBet());
            allScore += playerInfoTDK.getAllBet();
        }
        //赢得人加分
        PlayerInfoTDK winnerUser = playerCardInfos.get(winnerId);
        winnerUser.setScore(allScore);
        this.room.addUserSocre(winnerId, allScore);
    }

    /**
     * 是否有模式
     *
     * @param mode
     * @return
     */
    boolean isHasMode(int mode) {
        return Room.isHasMode(mode, this.room.getOtherMode());
    }


    /**
     * 从庄家开始拿到玩家列表
     *
     * @return
     */
    private List<Long> getAliveUserBeginWithBanker() {
        long banker = this.room.getBankerId();
        List<Long> users = new ArrayList<>();

        long nextUser = banker;
        for (int i = 0; i < this.aliveUserList.size(); i++) {
            users.add(nextUser);
            nextUser = nextTurnId(nextUser);
        }
        return users;
    }


    /**
     * 从最大牌分数玩家 拿到玩家列表
     *
     * @return
     */
    private List<Long> getUserListBeginWithMaxCardScoreUser() {
        long maxScoreUser = findMaxCardScoreUser();
        List<Long> users = new ArrayList<>();
        long nextUser = maxScoreUser;
        for (int i = 0; i < this.aliveUserList.size(); i++) {
            users.add(nextUser);
            nextUser = nextTurnId(nextUser);
        }
        return users;
    }

    /**
     * 下一个
     *
     * @param curId
     * @return
     */
    protected long nextTurnId(long curId) {
        int index = aliveUserList.indexOf(curId);
        int nextId = index + 1;
        if (nextId >= aliveUserList.size()) {
            nextId = 0;
        }
        return aliveUserList.get(nextId);
    }


    /**
     * 找到第一个下注的人
     *
     * @return
     */
    private long findMaxCardScoreUser(boolean... isAddFirstTwoCard) {
        boolean isAdd = false;
        if (isAddFirstTwoCard.length == 1 && isAddFirstTwoCard[0]) {
            isAdd = true;
        }
        List<Long> users = getAliveUserBeginWithBanker();
        //按顺序比较
        int score = 0;
        long maxUser = 0;
        boolean isGongZhangSuiBao = isHasMode(model_公张随豹);
        boolean isABiPao = isHasMode(model_抓A必泡);
        boolean isWangZhongPao = isHasMode(model_王中炮);
        for (long userId : users) {
            int s = playerCardInfos.get(userId).getCardScore(isGongZhangSuiBao, isABiPao, isWangZhongPao, isAdd);
            //分数相同 离banker近的赢
            if (s > score) {
                score = s;
                maxUser = userId;
            }
        }
        return maxUser;
    }


    @Override
    public IfaceGameVo toVo(long watchUser) {
        GameTDKVo gameTDKVo = new GameTDKVo();
        BeanUtils.copyProperties(this, gameTDKVo);
        gameTDKVo.setRemainCardSize(this.cards.size());
        for (PlayerInfoTDK playerInfoTDK : this.playerCardInfos.values()) {
            gameTDKVo.playerVo.put(playerInfoTDK.getUserId(), (PlayerCardInfoTDKVo) playerInfoTDK.toVo(watchUser));
        }
        return gameTDKVo;
    }


    /**
     * 生成战绩
     */
    protected void genRecord() {
        long id = IdWorker.getDefaultInstance().nextId();
        genRecord(playerCardInfos.values().stream().collect
                (Collectors.toMap(PlayerInfoTDK::getUserId, PlayerInfoTDK::getScore)), room, id);
    }

    /**
     * 发送最终结算版
     */
    protected void sendFinalResult() {
        //所有牌局都结束
        if (room.getCurGameNumber() > room.getGameNumber()) {
            List<UserOfResult> userOfResultList = this.room.getUserOfResult();
            // 存储返回
            GameOfResult gameOfResult = new GameOfResult();
            gameOfResult.setUserList(userOfResultList);
            MsgSender.sendMsg2Player(SERVICE_NAME, "gameFinalResult", gameOfResult, users);

            RoomManager.removeRoom(room.getRoomId());

            //战绩
            this.room.genRoomRecord();
        }
    }

    public RoomTDK getRoom() {
        return room;
    }

    public GameTDK setRoom(RoomTDK room) {
        this.room = room;
        return this;
    }

    public Map<Long, PlayerInfoTDK> getPlayerCardInfos() {
        return playerCardInfos;
    }

    public GameTDK setPlayerCardInfos(Map<Long, PlayerInfoTDK> playerCardInfos) {
        this.playerCardInfos = playerCardInfos;
        return this;
    }

    public List<Integer> getCards() {
        return cards;
    }

    public GameTDK setCards(List<Integer> cards) {
        this.cards = cards;
        return this;
    }

    public List<Long> getAliveUserList() {
        return aliveUserList;
    }

    public GameTDK setAliveUserList(List<Long> aliveUserList) {
        this.aliveUserList = aliveUserList;
        return this;
    }

    public List<Long> getGiveUpList() {
        return giveUpList;
    }

    public GameTDK setGiveUpList(List<Long> giveUpList) {
        this.giveUpList = giveUpList;
        return this;
    }

    public List<Integer> getBets() {
        return bets;
    }

    public GameTDK setBets(List<Integer> bets) {
        this.bets = bets;
        return this;
    }

    public BetInfo getBetInfo() {
        return betInfo;
    }

    public GameTDK setBetInfo(BetInfo betInfo) {
        this.betInfo = betInfo;
        return this;
    }

    public KickInfo getKickInfo() {
        return kickInfo;
    }

    public GameTDK setKickInfo(KickInfo kickInfo) {
        this.kickInfo = kickInfo;
        return this;
    }

    public int getState() {
        return state;
    }

    public GameTDK setState(int state) {
        this.state = state;
        return this;
    }

    public int getHandCardNum() {
        return handCardNum;
    }

    public GameTDK setHandCardNum(int handCardNum) {
        this.handCardNum = handCardNum;
        return this;
    }

    public long getOpenUser() {
        return openUser;
    }

    public GameTDK setOpenUser(long openUser) {
        this.openUser = openUser;
        return this;
    }

    public Map<Long, String> getOperateDesc() {
        return operateDesc;
    }

    public GameTDK setOperateDesc(Map<Long, String> operateDesc) {
        this.operateDesc = operateDesc;
        return this;
    }
}
