package com.code.server.game.poker.tuitongzi;
import com.code.server.constant.response.ErrorCode;
import com.code.server.constant.response.ResponseVo;
import com.code.server.game.room.Game;
import com.code.server.game.room.Room;
import com.code.server.game.room.kafka.MsgSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;

public class GameTuiTongZi extends Game{

    protected static final Logger logger = LoggerFactory.getLogger(GameTuiTongZi.class);

    protected static final String serviceName = "gameTuiTongZiService";

    protected List<Integer> cards = new ArrayList<Integer>();

    public Map<Long, PlayerTuiTongZi> playerCardInfos = new HashMap<>();

    protected RoomTuiTongZi room;

    protected long bankerId = -1L;

    protected Integer state = TuiTongZiConstant.STATE_START;

    public void startGame(List<Long> users, Room room){
        this.room = (RoomTuiTongZi) room;
        this.users = users;
        initPlayer();
        initCards();
        //暂时把庄写死
        this.bankerId = users.get(0);

        PlayerTuiTongZi playerTuiTongZi = playerCardInfos.get(this.bankerId);
        playerTuiTongZi.setScore(playerTuiTongZi.getScore() - 20);
        /*
        庄家分数
        * */
        playerTuiTongZi.setBankerScore(20);
        //开始下注
        betStart();
    }

    public void initPlayer(){

        for (Long uid : users){
            PlayerTuiTongZi playerTuiTongZi = getGameTypePlayerCardInfo();
            playerTuiTongZi.setUserId(uid);
            playerCardInfos.put(uid, playerTuiTongZi);
        }
    }

    public void initCards(){
        for (int i = 0; i < 36; i++){
            cards.add(i);
        }
        //洗牌
        shuffle(cards);
    }
     /*
     转为下注状态
     * */
    public void betStart(){

        state = TuiTongZiConstant.STATE_BET;

        Map<String, Object> param = new HashMap<>();

        param.put("bankerId", this.bankerId);

        param.put("curGameNumber", this.room.getGameNumber());

        //推送开始下注
        MsgSender.sendMsg2Player(serviceName, "betStart", param, users);
    }

    /*
    *  发牌
    * */
    public void deal(){

        for (PlayerTuiTongZi player : playerCardInfos.values()) {
            for (int i = 0; i < 2; i++) {
                player.getPlayerCards().add(cards.remove(0));
            }
            //发完牌之后，确定牌型
            int ret = -1;
            try {
                ret = TuiTongZiCardUtils.cardsPatterns(player.getPlayerCards());
            } catch (Exception e) {
                e.printStackTrace();
            }
            player.setPattern(ret);
            //通知发牌
            MsgSender.sendMsg2Player(new ResponseVo(serviceName, "deal", player.getPlayerCards()), player.getUserId());
        }
    }
    /*
    * 洗牌
    * */
    public void shuffle(List<Integer> list){
        Collections.shuffle(list);
    }

    protected void openStart(){
        state = TuiTongZiConstant.STATE_OPEN;
        deal();
        //推送开始下注
        MsgSender.sendMsg2Player("gamePaijiuService", "openStart", this.bankerId, users);
    }

    public PlayerTuiTongZi getGameTypePlayerCardInfo() {

        switch (room.getGameType()) {
            case "38":
                return new PlayerTuiTongZi();
            default:
                return new PlayerTuiTongZi();
        }
    }
    /**
     * 摇骰子阶段
     */
    protected void crapStart(){
        MsgSender.sendMsg2Player(serviceName, "crapStart", 0, bankerId);
        this.state = TuiTongZiConstant.START_CRAP;
    }
    /*
     *掷骰子
     */
    public int crap(Long userId){

        if (state == TuiTongZiConstant.START_CRAP) return ErrorCode.CRAP_PARAM_ERROR;
        if (userId != bankerId) return ErrorCode.NOT_BANKER;

        Random random = new Random();
        Integer num1 = random.nextInt(6) + 1;
        Integer num2 = random.nextInt(6) + 1;
        Map<String, Integer> result = new HashMap<>();
        result.put("num1", num1);
        result.put("num2", num2);
        MsgSender.sendMsg2Player(serviceName, "randSZ", result, users);
        MsgSender.sendMsg2Player(serviceName, "crap", "0", userId);
        openStart();
        return 0;
    }

    /*
    * 下注
    * */
    public int bet(Long userId, Integer zhu){
        PlayerTuiTongZi playerTuiTongZi1 = playerCardInfos.get(userId);
        //玩家不存在
        if (playerTuiTongZi1 == null) return ErrorCode.NO_USER;
        //已经下过注
        if (playerTuiTongZi1.getBet() != null) return ErrorCode.ALREADY_BET;

        Bet bet = new Bet();
        bet.setZhu(zhu);
        playerTuiTongZi1.setBet(bet);

        Map result = new HashMap();
        result.put("userId", userId);
        result.put("bet", bet);

        MsgSender.sendMsg2Player(serviceName, "betResult", result, users);
        MsgSender.sendMsg2Player(serviceName, "bet", "0" , userId);

        int count = 0;
        for (Long l : users){
            if (l != this.bankerId){
                PlayerTuiTongZi p = playerCardInfos.get(l);
                if (p.getBet() != null){
                    count++;
                }
            }
        }

        if (count == (users.size() - 1)){
            crapStart();
        }

        return 0;
    }

    public int open(Long userId, Long firstId){
        logger.info(userId +"  开牌: ");

        PlayerTuiTongZi playerTuiTongZi = playerCardInfos.get(userId);
        if (playerTuiTongZi == null) return ErrorCode.NO_USER;
        playerTuiTongZi.setOpen(true);

        Map<String, Long> result = new HashMap<>();
        result.put("userId", userId);
        /**
         * 告知服务器牌型
         * */
        result.put("cardsPatterns", playerTuiTongZi.getPattern());
        MsgSender.sendMsg2Player(serviceName, "openResult", result, users);
        MsgSender.sendMsg2Player(serviceName, "open", "0", userId);

        boolean isFind = true;
        for (long uid : users){
            PlayerTuiTongZi p = playerCardInfos.get(uid);
            if (p.isOpen() == false){
                isFind = false;
            }
        }

        if (isFind == true){
            gameOver(firstId);
        }

        return 0;
    }
    /*
    * 游戏结束
    * */
    public void gameOver(Long firstId){

        try {
            compute(firstId);
            sendResult();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void compute(Long firstId) throws Exception {

        Integer i = users.indexOf(bankerId);
        PlayerTuiTongZi playerZhuang = playerCardInfos.get(bankerId);

        List<PlayerTuiTongZi> winnerList = new ArrayList<>();
        List<PlayerTuiTongZi> loserList = new ArrayList<>();

        Integer k = users.indexOf(firstId);
        //所有赢的玩家按照发牌顺序排序用来计算得分
        for (int j = 0; j < users.size(); j++){
            PlayerTuiTongZi player = playerCardInfos.get(users.get(j));
            if (player.getUserId() != bankerId){
                if (TuiTongZiCardUtils.zhuangIsBiggerThanXian(playerZhuang, player)){
                    //如果庄家比较大
                    if (j < k){
                        winnerList.add(player);
                    }else if(j == k){
                        winnerList.set(0, player);
                    }else {
                        winnerList.set(1, player);
                    }
                }else {
                    loserList.add(player);
                }
            }
        }

        long lastGuoDi = this.room.getBankerScore();

        for (PlayerTuiTongZi loser : loserList){
            long delta = 0;
            if (loser.getBet().getZhu() == Bet.Wu){
                delta = 5;
            }else if(loser.getBet().getZhu() == Bet.SHI){
                delta = 10;
            }else if(loser.getBet().getZhu() == Bet.SHI_WU){
                delta = 15;
            }else if(loser.getBet().getZhu() == Bet.ER_SHI){
                delta = 20;
            }else if(loser.getBet().getZhu() == Bet.GUO_BAN){
                delta = lastGuoDi / 2;
            }else if(loser.getBet().getZhu() == Bet.MAN_ZHU){
                delta = lastGuoDi;
            }
            playerZhuang.setScore(playerZhuang.getScore() + delta);
            loser.setScore(loser.getScore() - delta);
        }

        //先把losers的分数放在锅里
        long currentGuoDi = lastGuoDi + playerZhuang.getScore();
        for (PlayerTuiTongZi winner : winnerList){
            long delta = 0;
            if (winner.getBet().getZhu() == Bet.Wu){
                delta = 5;
            }else if(winner.getBet().getZhu() == Bet.SHI){
                delta = 10;
            }else if(winner.getBet().getZhu() == Bet.SHI_WU){
                delta = 15;
            }else if(winner.getBet().getZhu() == Bet.ER_SHI){
                delta = 20;
            }else if(winner.getBet().getZhu() == Bet.GUO_BAN){
                delta = lastGuoDi / 2;
            }else if(winner.getBet().getZhu() == Bet.MAN_ZHU){
                delta = lastGuoDi;
            }

            // 是不是起对周锅
            boolean isQiDui = TuiTongZiCardUtils.isDuiZi(winner.getPlayerCards());
            if (isQiDui){
                delta = delta * 2;
            }
            if (delta > currentGuoDi){
                delta = currentGuoDi;
            }
            currentGuoDi = currentGuoDi - delta;
            playerZhuang.setScore(playerZhuang.getScore() + delta);
            winner.setScore(winner.getScore() + delta);
            //假如锅里没钱就跳出别的玩家喝水
            if (currentGuoDi == 0){
                break;
            }
        }
        this.room.setBankerScore(currentGuoDi);
        if (currentGuoDi <= 5 || currentGuoDi >= 400){
            System.out.println("庄家下装|庄家结束游戏");
        }
    }
    /**
     * 牌局结果
     */
    public void sendResult(){

        List<PlayerTuiTongZi> aList = new ArrayList<>();
        for (long id : users){
            aList.add(playerCardInfos.get(id));
        }

        MsgSender.sendMsg2Player("gamePaijiuService", "gameResult", aList, this.users);
    }


//    protected def sendResult(): Unit = {
//        var gameResult = new GameResultPaijiu
//        this.playerCardInfos.values.foreach(playerInfo => gameResult.getPlayerCardInfos.add(playerInfo.toVo))
//        MsgSender.sendMsg2Player("gamePaijiuService", "gameResult", gameResult, this.users)
//    }

//    /**
//     * 结算
//     */
//    protected def compute(): Unit = {
//        val banker = playerCardInfos(bankerId)
//        var resultSet: Set[Int] = Set()
//        playerCardInfos.foreach { case (uid, playerInfo) =>
//            if (uid != bankerId) {
//                val winResult: Int = compareAndSetScore(banker, playerInfo)
//                resultSet = resultSet.+(winResult)
//            }
//        }
//        //全赢或全输
//        if (resultSet.size == 1) {
//            val bankerStatiseics = this.roomPaijiu.getRoomStatisticsMap.get(bankerId)
//            if (resultSet.contains(WIN)) bankerStatiseics.winAllTime += 1
//            if (resultSet.contains(LOSE)) bankerStatiseics.loseAllTime += 1
//        }
//    }

    public int exchange(Long userId){
        return 1;
    }

    public int setTestUser(Long userId){
        return 1;
    }

    public int bankerBreakStart(){
        return 1;
    }

    public int bankerBreak(Long userId, Long flag){
        return 1;
    }

    public int fightForBanker(Long userId, Boolean flag){
        return 1;
    }

    public int bankerSetScore(Long userId, Long score){
        return 1;
    }

    public int bankerBreak(Long userId, Boolean flag){
        return 1;
    }

    public int bankerSetScore(Long userId, int score){
        return 1;
    }

}
