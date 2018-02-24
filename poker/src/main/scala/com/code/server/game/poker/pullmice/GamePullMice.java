package com.code.server.game.poker.pullmice;
import com.code.server.constant.response.*;
import com.code.server.game.room.Game;
import com.code.server.game.room.Room;
import com.code.server.game.room.kafka.MsgSender;
import com.code.server.game.room.service.RoomManager;
import com.code.server.util.IdWorker;
import groovy.util.logging.Slf4j;
import java.util.*;

@Slf4j
public class GamePullMice extends Game{

    protected static final String serviceName = "gamePullMiceService";

    protected RoomPullMice room;

    protected Integer state = PullMiceConstant.STATE_START;

    protected Map<Long, PlayerPullMice> playerCardInfos = new HashMap<Long, PlayerPullMice>();

    protected List<PlayerPullMice> pxList;

    @Override
    public IfaceGameVo toVo(long watchUser) {
        GamePullMiceVo vo = new GamePullMiceVo();
        vo.pxList = this.pxList;
        vo.state = this.state;
        vo.playerCardInfos = this.playerCardInfos;
        return vo;
    }

    public void startGame(List<Long> users, Room room){
        this.room = (RoomPullMice) room;
        this.users = users;
        isNoticeClientShuffle();
        initPlayer();
        //先推送一下分数
        this.pushScoreChange();
        deal();
    }

    public void initPlayer(){
        playerCardInfos.clear();
        for (Long uid : users){
            PlayerPullMice playerPullMice = new PlayerPullMice();
            playerPullMice.setUserId(uid);
            playerCardInfos.put(uid, playerPullMice);
        }
    }

    public void initCards(){
        room.cards.clear();
        for (int i = 0; i < 54; i++){
            room.cards.add(i);
        }
        for (int i = 0; i < 54; i++){
            room.cards.add(i);
        }
        Collections.shuffle(room.cards);
    }

    //第一次下注
    public void betStart(){
        state = PullMiceConstant.STATE_BET;
        Map<Object, Object> result = new HashMap<>();
        List<PlayerPullMiceVo> aList = new ArrayList<>();
        for (PlayerPullMice playerPullMice : this.pxList){
            aList.add((PlayerPullMiceVo) playerPullMice.toVo());
        }
        result.put("result", aList);
        result.put("state", this.state);
        //此时pxId为1的先下注
        MsgSender.sendMsg2Player(serviceName, "betStart", result, pxList.get(0).getUserId());
    }

    public int bet(Long userId, Integer zhu){

        PlayerPullMice playerPullMice = playerCardInfos.get(userId);
        if (playerPullMice == null) return ErrorCode.NO_USER;

        if (playerPullMice.getBetList().size() != state - 2){
            return ErrorCode.ALREADY_BET;
        }

        //第一个发牌的人
        PlayerPullMice playerOne = null;
        for (PlayerPullMice p : playerCardInfos.values()){
            if (p.getPxId() == 1){
                playerOne = p;
                break;
            }
        }

        //跟注
        if (playerPullMice == playerOne) return ErrorCode.BET_PARAM_ERROR;

        Bet bet = new Bet();
        bet.setZhu(zhu);
        //取出player最后一次下的注
        if (zhu == Bet.FOLLOW){
            Bet  bet_ = playerOne.getBetList().get(playerOne.getBetList().size() - 1);
            bet.setZhu(bet_.getZhu());
            zhu = bet_.getZhu();
        }

        playerPullMice.getBetList().add(bet);

        long ret = 0;
        if (zhu == Bet.YI){
            ret = 1;
        }else if(zhu == Bet.ER){
            ret = 2;
        }else if(zhu == Bet.SAN){
            ret = 3;
        }else if(zhu == Bet.SI){
            ret = 4;
        }else if(zhu == Bet.WU){
            ret = 5;
        }else if(zhu == Bet.ESCAPE){
            playerPullMice.setEscape(true);
        } else if(zhu == Bet.FENG){
            ret = 10;
        }

        playerPullMice.setScore(playerPullMice.getScore() - ret);
        this.room.potBottom += ret;

        Map<String, Long> res = new HashMap<>();
        res.put("userId", userId);
        res.put("ret", ret);
        res.put("potBottom", this.room.potBottom);
        MsgSender.sendMsg2Player(serviceName, "betResult", res, users);
        MsgSender.sendMsg2Player(serviceName, "bet", "0", userId);

        boolean isDeal = true;
        for (PlayerPullMice player : playerCardInfos.values()){

            if (player.isEscape()){
                continue;
            }

            //第一次下注
            if (state == PullMiceConstant.STATE_BET){
                if (player.getBetList().size() != 1){
                    isDeal = false;
                    break;
                }

            } else if (state == PullMiceConstant.BET_FIRST){
                if (player.getBetList().size() != 2){
                    isDeal = false;
                    break;
                }
            }else if(state == PullMiceConstant.BET_SECOND){
                if (player.getBetList().size() != 3){
                    isDeal = false;
                    break;
                }
            }else if(state == PullMiceConstant.BET_THIRD){
                //发第五张牌之后，第一个人如果没有选封
                if (player.getPxId() == 1 && zhu != Bet.FENG){
                    if (player.getBetList().size() != 4){
                        isDeal = false;
                        break;
                    }
                }else {
                    if (player.getPxId() == 1 && player.isAlreadyFeng() != true){
                        isDeal = false;
                        break;
                    }
                }
            }
        }

        if (isDeal){

            if (state == PullMiceConstant.BET_FOURTH){

                state = PullMiceConstant.STATE_OPEN;

                this.gameOver();
                //算开牌

            }else {

                state++;
                //继续发牌
                List<PlayerPullMice> list = new ArrayList<>();
                list.addAll(playerCardInfos.values());
                //确定发牌顺序
                CardUtils.calListPxId(list, users);
                this.pxList = list;

                //发一张牌
                for (int i = 0; i < list.size(); i++){
                    PlayerPullMice player = playerCardInfos.get(users.get(i));
                    //一张一张的发牌
                    isNoticeClientShuffle();
                    player.getCards().add(room.cards.remove(0));
                    MsgSender.sendMsg2Player(new ResponseVo(serviceName, "deal", player.getCards()), player.getUserId());
                }

                Map<Object, Object> result = new HashMap<>();
                List<PlayerPullMiceVo> aList = new ArrayList<>();
                for (PlayerPullMice playerPullMice_ : this.pxList){
                    aList.add((PlayerPullMiceVo) playerPullMice_.toVo());
                }

                result.put("result", aList);
                result.put("state", this.state);
                //此时pxId为1的先下注
                MsgSender.sendMsg2Player(serviceName, "betStart", result, pxList.get(0).getUserId());

            }
        }else {

            PlayerPullMice playerCurrent = playerCardInfos.get(userId);

            PlayerPullMice playerNext = null;

            for (PlayerPullMice p : pxList){
                if (p.getPxId() == playerCurrent.getPxId() + 1){
                    playerNext = p;
                    break;
                }
            }

            List<PlayerPullMiceVo> aList = new ArrayList<>();
            for (PlayerPullMice playerPullMice_ : this.pxList){
                aList.add((PlayerPullMiceVo) playerPullMice_.toVo());
            }

            Map<Object, Object> result = new HashMap<>();
            result.put("result", aList);
            result.put("state", this.state);

            MsgSender.sendMsg2Player(serviceName, "betStart", result, playerNext.getUserId());

        }

        return 0;
    }

    //算分
    public void compute(){

        List<PlayerPullMice> list = new ArrayList<>();
        for (PlayerPullMice p : playerCardInfos.values()){
            list.add(p);
        }
        PlayerPullMice winner = CardUtils.findWinner(list);
        winner.setWinner(true);
        winner.setScore(winner.getScore() + this.room.potBottom);
        this.room.potBottom = 0;

        for (PlayerPullMice p : list){
            room.addUserSocre(p.getUserId(), p.getScore());
        }
    }

    /**
     * 牌局结果
     */
    public void sendResult(){

        List<PlayerPullMiceVo> list = new ArrayList<>();
        for (PlayerPullMice p : playerCardInfos.values()){
            list.add((PlayerPullMiceVo) p.toVo());
        }
        MsgSender.sendMsg2Player(serviceName, "gameResult", list, this.users);

        //推送一下分数
        this.pushScoreChange();
    }

    //生成战绩
    public void genRecord(){
        long id = IdWorker.getDefaultInstance().nextId();
        Map<Long, Double> map = new HashMap<>();
        for (Map.Entry<Long, PlayerPullMice> entry : playerCardInfos.entrySet()){
           PlayerPullMice p = entry.getValue();
            map.put(p.getUserId(), p.getScore() + 0.0);
        }
        genRecord(map, this.room, id);
    }

    public void gameOver(){
        compute();
        sendResult();
        genRecord();
        this.room.clearReadyStatus(true);
        if (this.room.curGameNumber == this.room.maxGameCount){
            sendFinalResult();
        }
    }

    public void sendFinalResult(){

        List<UserOfResult>  userOfResult =  this.room.getUserOfResult();
        GameOfResult gameOfResult = new GameOfResult();
        gameOfResult.setUserList(userOfResult);
        MsgSender.sendMsg2Player("gameService", "gamePullMiceFinalResult", gameOfResult, users);
        RoomManager.removeRoom(room.getRoomId());

        this.room.genRoomRecord();
    }

    //通知客户端洗牌
    public void isNoticeClientShuffle(){
        if (room.cards.size() == 0){
            initCards();
            MsgSender.sendMsg2Player(new ResponseVo(serviceName, "shuffle", this.room.cards.size()), users);
        }
    }

    //第一次发牌
    public void deal(){
        List<PlayerPullMice> list = new ArrayList<>();
        list.addAll(playerCardInfos.values());
        //确定发牌顺序
        CardUtils.calListPxId(list, users);
        //获取下注顺序的数组
        this.pxList = list;
        //发第一张牌
        for (int i = 0; i < list.size(); i++){
            PlayerPullMice player = playerCardInfos.get(users.get(i));
            //一张一张的发牌
            //如果没牌了，重新洗牌洗牌
            isNoticeClientShuffle();
            player.getCards().add(room.cards.remove(0));
            MsgSender.sendMsg2Player(new ResponseVo(serviceName, "deal", player.getCards()), player.getUserId());
        }
        //发第二张明牌
        for (int i = 0; i < list.size(); i++){
            PlayerPullMice player = playerCardInfos.get(users.get(i));
            //一张一张的发牌
            isNoticeClientShuffle();
            player.getCards().add(room.cards.remove(0));
            MsgSender.sendMsg2Player(new ResponseVo(serviceName, "deal", player.getCards()), player.getUserId());
        }
        //推送下注
        betStart();
    }

    /*
    * 推送玩家分数
    * */
    public void pushScoreChange() {
        Map<Long, Double> userScores = new HashMap<>();
        userScores.putAll(this.room.userScores);
        MsgSender.sendMsg2Player(new ResponseVo("gameService", "scoreChangePullMice", userScores), this.getUsers());
    }
}
