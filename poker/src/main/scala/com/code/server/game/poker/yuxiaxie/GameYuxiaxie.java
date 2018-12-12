package com.code.server.game.poker.yuxiaxie;

import com.code.server.constant.response.IfaceGameVo;
import com.code.server.game.room.Game;
import com.code.server.game.room.Room;
import com.code.server.game.room.kafka.MsgSender;
import org.springframework.beans.BeanUtils;

import java.util.*;

/**
 * Created by sunxianping on 2018-11-30.
 */
public class GameYuxiaxie extends Game {

    static final int STATE_START_GAME = 0;
    static final int STATE_BET = 1;
    static final int STATE_CRAP = 2;
    static final int STATE_OPEN = 3;

    private RoomYuxiaxie room;
    protected Map<Long, PlayerInfoYuxiaxie> playerCardInfos = new HashMap<>();
    private int state;
    private List<Integer> dice = new ArrayList<>();


    /**
     * 开始游戏
     *
     * @param users
     * @param room
     */
    public void startGame(List<Long> users, Room room) {
        this.room = (RoomYuxiaxie) room;
        init(users, room.getBankerId());
        updateLastOperateTime();
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


    protected void betStart() {
        this.state = STATE_BET;
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

        PlayerInfoYuxiaxie playerInfoYuxiaxie = playerCardInfos.get(userId);

        playerInfoYuxiaxie.bet(type, index1, index2, num);

        //下注历史记录
        Map<Integer,Bet> betInfo = this.room.getBetHistory().getOrDefault(userId, new HashMap<>());
        betInfo.put(this.room.curGameNumber, playerInfoYuxiaxie.getBet());
        this.room.getBetHistory().put(userId, betInfo);


        MsgSender.sendMsg2Player("gameService", "betResp", playerInfoYuxiaxie.getBet(),this.users);
        MsgSender.sendMsg2Player("gameService", "bet", "ok",userId);
       return 0;
    }


    public void crapStart(){
        this.state = STATE_CRAP;
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
        int num1 = random.nextInt(6);
        int num2 = random.nextInt(6);
        dice.add(num1);
        dice.add(num2);

        MsgSender.sendMsg2Player("gameService", "crapResp", dice,this.users);
        MsgSender.sendMsg2Player("gameService", "crap", 0,userId);

        //开始下注
        betStart();

        return 0;
    }

    /**
     * 开局
     */
    public void open() {
        //开局
        this.state = STATE_OPEN;




    }

    public void compute(){
        int allScore = 0;
        for (PlayerInfoYuxiaxie playerInfoYuxiaxie : this.playerCardInfos.values()) {
            //
            if (this.room.getBankerId() == playerInfoYuxiaxie.getUserId()) {
                continue;
            }

            int score = playerInfoYuxiaxie.settle(this.dice.get(0), this.dice.get(1));

            this.room.addUserSocre(playerInfoYuxiaxie.getUserId(), score);

            allScore += score;

        }
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




    @Override
    public IfaceGameVo toVo(long watchUser) {
        GameYuxiaxieVo game = new GameYuxiaxieVo();
        BeanUtils.copyProperties(this, game);

        for (PlayerInfoYuxiaxie playerInfo : this.playerCardInfos.values()) {
            game.getPlayerCardInfos().put(playerInfo.getUserId(), (PlayerInfoYuxiaxieVo) playerInfo.toVo(watchUser));
        }
        return game;
    }
}
