package com.code.server.game.poker.yuxiaxie;

import com.code.server.constant.response.ErrorCode;
import com.code.server.constant.response.GameOfResult;
import com.code.server.constant.response.IfaceGameVo;
import com.code.server.constant.response.UserOfResult;
import com.code.server.game.room.Game;
import com.code.server.game.room.Room;
import com.code.server.game.room.kafka.MsgSender;
import com.code.server.game.room.service.RoomManager;
import com.code.server.redis.service.RedisManager;
import org.springframework.beans.BeanUtils;

import java.util.*;

import static com.code.server.game.poker.yuxiaxie.Bet.TYPE_DANYA;
import static com.code.server.game.poker.yuxiaxie.Bet.TYPE_NUO;

/**
 * Created by sunxianping on 2018-11-30.
 */
public class GameYuxiaxie extends Game {

    static final int STATE_START_GAME = 0;
    static final int STATE_CRAP = 1;
    static final int STATE_BET = 2;
    static final int STATE_OPEN = 3;

    private RoomYuxiaxie room;
    protected Map<Long, PlayerInfoYuxiaxie> playerCardInfos = new HashMap<>();
    private int state;
    private List<Integer> dice = new ArrayList<>();
    List<Bet> allBets = new ArrayList<>();
    private int nuoCount;


    /**
     * 开始游戏
     *
     * @param users
     * @param room
     */
    public void startGame(List<Long> users, Room room) {
        this.room = (RoomYuxiaxie) room;
        init(users, room.getBankerId());
//        updateLastOperateTime();
        //通知其他人游戏已经开始
//        MsgSender.sendMsg2Player(new ResponseVo(SERVICE_NAME, "gameBegin", "ok"), this.getUsers());
        MsgSender.sendMsg2Player("gameService", "gameBegin", "ok",this.users);
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
            PlayerInfoYuxiaxie playerCardInfo = new PlayerInfoYuxiaxie();
            playerCardInfo.userId = uid;
            playerCardInfos.put(uid, playerCardInfo);
        }
        this.users.addAll(users);


//        betStart();

        crapStart();
    }


    /**
     * 下注开始
     */
    protected void betStart() {
        this.state = STATE_BET;
        updateLastOperateTime();
        MsgSender.sendMsg2Player("gameService", "betStart", "ok",this.users);
    }


    /**
     * 下注
     * @param userId
     * @param index1
     * @param index2
     * @param num
     * @return
     */
    public int bet(long userId,int type, int index1,  int index2, int num) {

        //判断有没有到上限

        //记录总下注
        Bet bet = getBetAndSet(type, index1, index2);

        if (bet.getNum() + num > getBetLimit(type)) {
            return ErrorCode.CANNOT_BET_LIMIT;
        }

        PlayerInfoYuxiaxie playerInfoYuxiaxie = playerCardInfos.get(userId);

        playerInfoYuxiaxie.bet(this.room, type, index1, index2, num);


        bet.addNum(num);

        //下注历史记录
        Map<Integer,List<Bet>> betInfo = this.room.getBetHistory().getOrDefault(userId, new HashMap<>());
        betInfo.put(this.room.curGameNumber, playerInfoYuxiaxie.getBets());
        this.room.getBetHistory().put(userId, betInfo);


        MsgSender.sendMsg2Player("gameService", "betResp", new Bet(type, index1, index2, num),this.users);
        MsgSender.sendMsg2Player("gameService", "bet", "ok",userId);
       return 0;
    }


    /**
     * 挪
     * @param userId
     * @param index1
     * @param index2
     * @param num
     * @return
     */
    public int nuo(long userId, int index1, int index2, int num) {
        Bet bet1 = getBetAndSet(TYPE_DANYA, index1, 0);
        Bet bet2 = getBetAndSet(TYPE_DANYA, index2, 0);

        if (num > bet1.getNum()) {
            return ErrorCode.CANNOT_NUO;
        }
        if (bet2.num + num > getBetLimit(TYPE_DANYA)) {
            return ErrorCode.CANNOT_NUO;
        }
        //挪次数限制
        if (this.nuoCount >= this.room.getNuo()) {
            return ErrorCode.CANNOT_NUO;
        }
        this.nuoCount ++;

        PlayerInfoYuxiaxie playerInfoYuxiaxie = playerCardInfos.get(userId);

        playerInfoYuxiaxie.bet(this.room, TYPE_NUO, index1, index2, num);


        bet1.addNum(-num);
        bet2.addNum(num);


        MsgSender.sendMsg2Player("gameService", "nuoResp", new Bet(3, index1, index2, num),this.users);
        MsgSender.sendMsg2Player("gameService", "nuo", "ok",userId);

        return 0;
    }

    /**
     * 获得下注限制
     * @param type
     * @return
     */
    private int getBetLimit(int type) {
        if (type == 0) {
            return this.room.getDanya();
        }
        if (type == 1) {
            return this.room.getBaozi();
        }
        if (type == 2) {
            return this.room.getChuanlian();
        }
        return 0;
    }

    /**
     * 通过type获得下注
     * @param type
     * @param index1
     * @param index2
     * @return
     */
    private Bet getBetByType(int type, int index1, int index2) {
        for (Bet bet : allBets) {
            if (bet.type == type && bet.index1 == index1 && bet.index2 == index2) {
                return bet;
            }
        }

        return null;
    }

    /**
     * 通过type获得下注,没有的话设置
     * @param type
     * @param index1
     * @param index2
     * @return
     */
    private Bet getBetAndSet(int type, int index1, int index2) {
        Bet bet = getBetByType(type, index1, index2);
        if (bet == null) {
            bet = new Bet(type, index1, index2, 0);
            this.allBets.add(bet);
        }
        return bet;
    }

    /**
     * 摇色子阶段
     */
    public void crapStart(){
        this.state = STATE_CRAP;
        this.room.setLastOperateTime(System.currentTimeMillis());
        MsgSender.sendMsg2Player("gameService", "crapStart", "ok",this.users);
    }




    /**
     * 摇色子
     * @param userId
     * @return
     */
    public int crap(long userId) {
        this.state = STATE_CRAP;
        Random random = new Random();
        int num1 = random.nextInt(6) + 1;
        int num2 = random.nextInt(6) + 1;
        dice.add(num1);
        dice.add(num2);
        List<Integer> diceHis = new ArrayList<>();
        diceHis.addAll(dice);
        this.room.getDiceHistory().add(diceHis);

        MsgSender.sendMsg2Player("gameService", "crapResp", dice,this.users);
        MsgSender.sendMsg2Player("gameService", "crap", 0,userId);

        //开始下注
        betStart();

        return 0;
    }


    /**
     * 结束
     * @return
     */
    public int gameOver() {
        this.state = STATE_OPEN;
        compute();


        sendResult();
        room.clearReadyStatus(true);
        sendFinalResult();




        return 0;
    }


    private void sendResult() {

        Map<String, Object> result = new HashMap<>();
        result.put("dice", this.dice);
        List<PlayerInfoYuxiaxieVo> players = new ArrayList<>();
        for (PlayerInfoYuxiaxie playerInfoYuxiaxie : playerCardInfos.values()) {
            players.add((PlayerInfoYuxiaxieVo) playerInfoYuxiaxie.toVo());
        }
        result.put("players", players);


        MsgSender.sendMsg2Player("gameService", "gameResult", result, users);

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

    /**
     * 结算
     */
    public void compute(){
        int allScore = 0;
        int allBetNum = 0;
        for (PlayerInfoYuxiaxie playerInfoYuxiaxie : this.playerCardInfos.values()) {
            //
            if (this.room.getBankerId() == playerInfoYuxiaxie.getUserId()) {
                continue;
            }

            int score = playerInfoYuxiaxie.settle(this.room,this.dice.get(0), this.dice.get(1));

            this.room.userScoreHistory.putIfAbsent(playerInfoYuxiaxie.getUserId(), new HashMap<>());
            this.room.userScoreHistory.get(playerInfoYuxiaxie.getUserId()).put(this.room.curGameNumber, score);


//            this.room.addUserSocre(playerInfoYuxiaxie.getUserId(), score);
            allScore += score;
            allBetNum += playerInfoYuxiaxie.getAllBetNum();

        }
        //
        int rs = allBetNum - allScore;
        PlayerInfoYuxiaxie banker = playerCardInfos.get(this.room.getBankerId());
//        banker.setScore(banker.getScore() - allScore);
        banker.setScore(banker.getScore() + rs);
//        this.room.addUserSocre(banker.getUserId(), -allScore);
        this.room.addUserSocre(banker.getUserId(), rs);
        if (this.room.isClubRoom()) {
            RedisManager.getClubRedisService().addClubUserMoney(this.room.getClubId(), banker.getUserId(), rs);
        }

    }





    @Override
    public IfaceGameVo toVo(long watchUser) {
        GameYuxiaxieVo game = new GameYuxiaxieVo();
        BeanUtils.copyProperties(this, game);

        for (PlayerInfoYuxiaxie playerInfo : this.playerCardInfos.values()) {
            game.getPlayerCardInfos().put(playerInfo.getUserId(), (PlayerInfoYuxiaxieVo) playerInfo.toVo(watchUser));
        }
        return game;
    }

    public int getState() {
        return state;
    }

    public GameYuxiaxie setState(int state) {
        this.state = state;
        return this;
    }

    public List<Bet> getAllBets() {
        return allBets;
    }

    public GameYuxiaxie setAllBets(List<Bet> allBets) {
        this.allBets = allBets;
        return this;
    }
}
