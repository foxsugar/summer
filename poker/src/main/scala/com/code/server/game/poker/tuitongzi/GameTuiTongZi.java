package com.code.server.game.poker.tuitongzi;
import com.code.server.constant.response.*;
import com.code.server.game.room.Game;
import com.code.server.game.room.Room;
import com.code.server.game.room.kafka.MsgSender;
import com.code.server.game.room.service.RoomManager;
import com.code.server.util.IdWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class GameTuiTongZi extends Game{

    protected static final Logger logger = LoggerFactory.getLogger(GameTuiTongZi.class);

    protected static final String serviceName = "gameTTZService";

    public Map<Long, PlayerTuiTongZi> playerCardInfos = new HashMap<Long, PlayerTuiTongZi>();

    protected RoomTuiTongZi room;

    protected long bankerId = -1L;

    protected Integer state = TuiTongZiConstant.STATE_START;

    protected long firstBankerId = -1;

    protected long firstBanerCount = 0;

    //第几局开始提示是否继续坐庄
    public static final int REQUIRE_COUNT_1 = 4;
    public static final int REQUIRE_COUNT_2 = 5;
    public static final int REQUIRE_COUNT_3 = 8;

    protected long lastOperateTime;

    public IfaceGameVo toVo(long watchUser) {

        GameTuiTongZiVo vo = new GameTuiTongZiVo();
        vo.zhuangCount = this.room.getZhuangCount();
        if (nextTurnId(this.bankerId) == firstBankerId){
            vo.firstBanerCount = this.firstBanerCount - 1;
        }else {
            vo.firstBanerCount = this.firstBanerCount;
        }
        vo.bankerId = this.bankerId;
        vo.state = this.state;
        vo.potBottom = room.getPotBottom();
        for (Long l:playerCardInfos.keySet()) {
            vo.playerCardInfos.put(l, playerCardInfos.get(l).toVo());
        }
        vo.cards.clear();
        vo.cards.addAll(this.room.cards);
        return vo;
    }


    protected boolean isBaWangZhuang(){
        return false;
    }
    public void startGame(List<Long> users, Room room){
        this.room = (RoomTuiTongZi) room;
        this.users = users;

        this.firstBankerId = ((RoomTuiTongZi) room).firstBankerId;
        this.firstBanerCount = ((RoomTuiTongZi) room).firstBanerCount;

        initPlayer();
        initCards();
        this.bankerId = this.room.getBankerId();


        //霸王庄
        if (isBaWangZhuang()){

            this.room.setZhuangCount(this.room.getZhuangCount() + 1);
            //第一次的时候设置锅底分数
            if (room.getCurGameNumber() == 1){
                this.bankerId = users.get(0);
                PlayerTuiTongZi playerTuiTongZi = playerCardInfos.get(this.bankerId);
                //设置锅底分数
                ((RoomTuiTongZi) room).setPotBottom(20);
                room.setBankerId(users.get(0));
                this.state = TuiTongZiConstant.STATE_SELECT;

                bankerBreakStart();


            }else{

                //是否继续坐庄
                if (this.room.getZhuangCount() == REQUIRE_COUNT_1 || this.room.getZhuangCount() == REQUIRE_COUNT_2 || this.room.getZhuangCount() == REQUIRE_COUNT_3){
                    continueBankerStart();
                }else {
                    this.state = TuiTongZiConstant.STATE_SELECT;
                    betStart();
                }

            }

        }else {

            //连续坐庄的次数
            this.room.setZhuangCount(this.room.getZhuangCount() + 1);
            //如果现在是第一局
            if (room.getCurGameNumber() == 1){
                this.bankerId = users.get(0);
                PlayerTuiTongZi playerTuiTongZi = playerCardInfos.get(this.bankerId);
                //设置锅底分数
                ((RoomTuiTongZi) room).setPotBottom(20);
                room.setBankerId(users.get(0));
            }
            //强制下庄
            if (this.room.getZhuangCount() == 9){
                long nextBanker = nextTurnId(room.getBankerId());
                room.setBankerId(nextBanker);
                ((RoomTuiTongZi) room).setPotBottom(20);
                this.bankerId = room.getBankerId();
                createNewCards();
                this.room.setZhuangCount(1);
            }else if(room.getCurGameNumber() != 1){
                //强制下装
                if (!(((RoomTuiTongZi) room).getPotBottom() < 400 && ((RoomTuiTongZi) room).getPotBottom() >= 5)){
                    long nextBanker = nextTurnId(room.getBankerId());
                    room.setBankerId(nextBanker);
                    ((RoomTuiTongZi) room).setPotBottom(20);
                    this.bankerId = room.getBankerId();
                    createNewCards();
                    this.room.setZhuangCount(1);
                }
            }

            System.out.println("==============zhuangCount" + this.room.getZhuangCount());
            //是否继续坐庄
            if (this.room.getZhuangCount() == REQUIRE_COUNT_1 || this.room.getZhuangCount() == REQUIRE_COUNT_2 || this.room.getZhuangCount() == REQUIRE_COUNT_3){
                continueBankerStart();
            }else {
                this.state = TuiTongZiConstant.STATE_SELECT;
                conti();
            }
        }
        updateLastOperateTime();
    }

    /**
     * 抢庄状态
     */
    public int bankerBreakStart(){
        state = TuiTongZiConstant.STATE_FIGHT_FOR_BANKER;
        MsgSender.sendMsg2Player("gameTTZService", "fightForBankerStart", this.bankerId, users);
        return 0;
    }

    public int fightForBanker(Long userId, Boolean flag){

        PlayerTuiTongZi playerTuiTongZi = playerCardInfos.get(userId);
        playerTuiTongZi.setGrab(flag ? 1 : 2);

        Map<String , Object> map = new HashMap<>();
        map.put("flag", flag);
        map.put("userId", userId);
        MsgSender.sendMsg2Player("gameTTZService", "fightForBankerResult", map, users);
        MsgSender.sendMsg2Player("gameTTZService", "fightForBanker", "0", userId);

        int count = 0;
        for (PlayerTuiTongZi playerTuiTongZi1 : playerCardInfos.values()){
            if (playerTuiTongZi1.getGrab() != 0){
                count++;
            }

        }

        //产生随机数随机给一个庄
        if (count == users.size()){

            List<PlayerTuiTongZi> aList = new ArrayList<>();
            for (PlayerTuiTongZi playerTuiTongZi1 : playerCardInfos.values()){
                if (playerTuiTongZi.getGrab() == 1){
                    aList.add(playerTuiTongZi1);
                }
            }

            int bound = aList.size();

            PlayerTuiTongZi randomPlayer = null;
            if (bound != 0){
                randomPlayer = aList.get(new Random().nextInt(bound));
            }else {
                randomPlayer = playerCardInfos.get(this.bankerId);
            }

            room.setBankerId(randomPlayer.getUserId());
            ((RoomTuiTongZi) room).setPotBottom(20);
            this.bankerId = room.getBankerId();
            MsgSender.sendMsg2Player("gameTTZService", "endFightForBanker", this.bankerId, users);

            betStart();
        }

        return 0;

    }

    public void conti(){
        if (firstBankerId < 0){
            firstBankerId = bankerId;
            this.room.firstBankerId = firstBankerId;
        }

        long id = nextTurnId(bankerId);
        if (id == firstBankerId && this.room.getZhuangCount() == 1){
            firstBanerCount++;
            this.room.firstBanerCount = firstBanerCount;
        }

        betStart();
    }
    //询问是否继续坐庄
    public void continueBankerStart(){

        Map<String, Object> param = new HashMap<>();
        param.put("bankerId", this.bankerId);
        this.state = TuiTongZiConstant.STATE_WILL_SELECT;
        //推送开始下注
        MsgSender.sendMsg2Player(serviceName, "continueBankerStart", param, users);

    }
    //是否继续坐庄
    public int continueBanker(boolean isZhuang, long userId){
        if (isBaWangZhuang()){

            if (isZhuang == false){
                sendFightFinalResult();
            }else {
                this.state = TuiTongZiConstant.STATE_SELECT;
                MsgSender.sendMsg2Player(serviceName, "continueBanker","0", userId);
                betStart();
            }

        }else {

            if (isZhuang == false){
                if (this.room.firstBanerCount == 2){
                    this.room.setZhuangCount(0);
                    sendFinalResult();
                    return 0;
                }

                this.room.addUserSocre(this.room.getBankerId(), this.room.getPotBottom() - 20);

                this.room.setZhuangCount(1);
                long nextBanker = nextTurnId(room.getBankerId());
                room.setBankerId(nextBanker);
                ((RoomTuiTongZi) room).setPotBottom(20);
                this.bankerId = room.getBankerId();
                createNewCards();
                this.state = TuiTongZiConstant.STATE_SELECT;
                MsgSender.sendMsg2Player(serviceName, "continueBanker","0", userId);
                conti();
            }else {
                this.state = TuiTongZiConstant.STATE_SELECT;
                MsgSender.sendMsg2Player(serviceName, "continueBanker","0", userId);
                conti();
            }
        }

        updateLastOperateTime();
        return 0;
    }

    public void initPlayer(){

        playerCardInfos.clear();
        for (Long uid : users){
            PlayerTuiTongZi playerTuiTongZi = getGameTypePlayerCardInfo();
            playerTuiTongZi.setUserId(uid);
            playerCardInfos.put(uid, playerTuiTongZi);
        }
    }
    /**
     * 重拿一副新牌
     * */
    public void createNewCards(){
        room.cards.clear();
        room.cardsCount++;
        for (int i = 0; i < 36; i++){
            room.cards.add(i);
        }
        //洗牌
        shuffle(room.cards);
    }

    public void initCards(){
        // 如果打完4局还剩4张牌
        if (room.cards.size() <= 4){
            room.cards.clear();
            room.cardsCount++;
            for (int i = 0; i < 36; i++){
                room.cards.add(i);
            }
        }
        //洗牌
        shuffle(room.cards);
    }
     /*
     转为下注状态
     * */
    public void betStart(){

        state = TuiTongZiConstant.STATE_BET;

        Map<String, Object> param = new HashMap<>();

        param.put("bankerId", this.bankerId);

        param.put("curGameNumber", this.room.getGameNumber());

        param.put("panBottom", this.room.getPotBottom());

        if (firstBankerId == nextTurnId(this.bankerId)){
            param.put("firstBanerCount", this.firstBanerCount - 1);
        }else {
            param.put("firstBanerCount", this.firstBanerCount);
        }

        param.put("zhuangCount", this.room.getZhuangCount());

        this.pushScoreChange();
        //推送开始下注
        MsgSender.sendMsg2Player(serviceName, "betStart", param, users);
    }

    public void pushScoreChange() {

//        public Map<Long, Double> userScores = new HashMap<>();

        Map<Long, Double> userScores = new HashMap<>();
        userScores.putAll(this.room.userScores);

        Double zhuangScore = this.room.userScores.get(this.bankerId);
        zhuangScore -= 20;
        userScores.put(this.bankerId, zhuangScore);

        MsgSender.sendMsg2Player(new ResponseVo("gameService", "scoreChangeTTZ", userScores), this.getUsers());
    }

    /*
    *  发牌
    * */
    public void deal(){

        for (PlayerTuiTongZi player : playerCardInfos.values()) {
            for (int i = 0; i < 2; i++) {
                player.getPlayerCards().add(room.cards.remove(0));
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

        //丢弃一些牌
        if (playerCardInfos.size() < 4){
            int ret = 4 - playerCardInfos.size();
            while (ret > 0){
                room.cards.remove(0);
                room.cards.remove(0);
                ret--;
            }
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
        MsgSender.sendMsg2Player(serviceName, "openStart", this.bankerId, users);
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

        if (state != TuiTongZiConstant.START_CRAP) return ErrorCode.CRAP_PARAM_ERROR;
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
        updateLastOperateTime();
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
//        result.put("bet", bet);

        long ret = 0;
        if (zhu == Bet.Wu){
            ret = 5;
        }else if(zhu == Bet.SHI){
            ret = 10;
        }else if(zhu == Bet.SHI_WU){
            ret = 15;
        }else if(zhu == Bet.ER_SHI){
            ret = 20;
        }else if(zhu == Bet.GUO_BAN){
            ret = room.getPotBottom() / 2;
        }else if(zhu == Bet.MAN_ZHU){
            ret = room.getPotBottom();
        }

        Map<String, Long> res = new HashMap<>();
        res.put("userId", userId);
        res.put("ret", ret);

        MsgSender.sendMsg2Player(serviceName, "betResult", res, users);
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
        updateLastOperateTime();
        return 0;
    }

    public int open(Long userId, Long firstId){
        logger.info(userId +"  开牌: ");

        PlayerTuiTongZi playerTuiTongZi = playerCardInfos.get(userId);
        if (playerTuiTongZi == null) return ErrorCode.NO_USER;
        playerTuiTongZi.setOpen(true);

        Map<String, Long> result = new HashMap<>();
        result.put("userId", userId);

        result.put("cardsPatterns", playerTuiTongZi.getPattern());

        String lastMax = room.getRoomStatisticsMap().get(userId).maxCardGroup;
        if (lastMax == null){
            lastMax = "0";
        }
        String current = playerTuiTongZi.getPattern() + "";

        if (playerTuiTongZi.getPattern() > Integer.parseInt(lastMax)){
            lastMax = current;
        }
        room.getRoomStatisticsMap().get(userId).maxCardGroup = lastMax;

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
        updateLastOperateTime();
        return 0;
    }
    /*
    * 游戏结束
    * */
    public void gameOver(Long firstId){

        try {
            compute(firstId);
            sendResult();
            genRecord();
            updateLastOperateTime();
            updateRoomLastTime();
            this.room.clearReadyStatus(true);

            if (isBaWangZhuang()){
                //强制下装
                if (!(((RoomTuiTongZi) room).getPotBottom() < 400 && ((RoomTuiTongZi) room).getPotBottom() >= 5) || room.getZhuangCount() == 8){
                    //退出游戏
                    sendFightFinalResult();
                }
            }else {
                sendFinalResult();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //生成战绩
    public void genRecord(){
        long id = IdWorker.getDefaultInstance().nextId();
        Map<Long, Double> map = new HashMap<>();
        for (Map.Entry<Long, PlayerTuiTongZi> entry : playerCardInfos.entrySet()){
            PlayerTuiTongZi p = entry.getValue();
            map.put(p.getUserId(), p.getScore() + 0.0);
        }
        genRecord(map, this.room, id);
    }

    public void sendFightFinalResult(){

        if (this.room.getPotBottom() != 0){
            room.addUserSocre(this.room.getBankerId(), this.room.getPotBottom() - 20);
        }

        List<UserOfResult>  userOfResult =  this.room.getUserOfResult();
        GameOfResult gameOfResult = new GameOfResult();
        gameOfResult.setUserList(userOfResult);
        MsgSender.sendMsg2Player("gameService", "gameTTZFinalResult", gameOfResult, users);
        RoomManager.removeRoom(room.getRoomId());
        this.room.genRoomRecord();
    }

    public void sendFinalResult(){

        //因为是两圈，并且要求换zhu
        if (firstBanerCount == 2 && this.room.getZhuangCount() == 0){

            if (this.room.getPotBottom() != 0){
                room.addUserSocre(this.room.getBankerId(), this.room.getPotBottom() - 20);
            }

            List<UserOfResult>  userOfResult =  this.room.getUserOfResult();
            GameOfResult gameOfResult = new GameOfResult();
            gameOfResult.setUserList(userOfResult);
            MsgSender.sendMsg2Player("gameService", "gameTTZFinalResult", gameOfResult, users);
            RoomManager.removeRoom(room.getRoomId());

            this.room.genRoomRecord();
        }
    }

    public void compute(Long firstId) throws Exception {

        Integer i = users.indexOf(bankerId);
        PlayerTuiTongZi playerZhuang = playerCardInfos.get(bankerId);

        List<PlayerTuiTongZi> winnerList = new ArrayList<>();
        List<PlayerTuiTongZi> loserList = new ArrayList<>();
        List<PlayerTuiTongZi> players = new ArrayList<>();
        Integer k = users.indexOf(firstId);

        //所有赢的玩家按照发牌顺序排序用来计算得分
        for (int j = 0; j < users.size(); j++){
            PlayerTuiTongZi player = playerCardInfos.get(users.get(j));
            if (player.getUserId() != bankerId){
                if (!TuiTongZiCardUtils.zhuangIsBiggerThanXian(playerZhuang, player)){
                    player.setWinner(true);
                }
            }

            if (j == k){
                player.setPxId(0);
            }else if(j > k){
                player.setPxId(j - k);
            }else {
                player.setPxId(j + k);
            }

            players.add(player);
        }

        for (int j = 0; j < players.size() - 1; j++){

            for (int w = j + 1; w < players.size(); w++){

                if (TuiTongZiCardUtils.mAIsBiggerThanB(players.get(j), players.get(w)) == 2){

                    Collections.swap(players, j, w);
                }else if(TuiTongZiCardUtils.mAIsBiggerThanB(players.get(j), players.get(w)) == 1){

                    if (players.get(j).getPxId() > players.get(w).getPxId()){
                        Collections.swap(players, j, w);
                    }
                }
            }
        }

        for (int j = 0; j < players.size(); j++){
            if (players.get(j).getUserId() != bankerId){
                if (players.get(j).isWinner()){
                    winnerList.add(players.get(j));
                }else {
                    loserList.add(players.get(j));
                }
            }
        }

        long lastGuoDi = this.room.getPotBottom();
        long currentGuoDi = lastGuoDi;
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
            loser.setScore(loser.getScore() - delta);
            currentGuoDi += delta;
            // 把分数加到room里
            room.addUserSocre(loser.getUserId(), loser.getScore());
        }


        for (PlayerTuiTongZi winner : winnerList){

            if (currentGuoDi == 0){
                winner.setScore(0);
                room.addUserSocre(winner.getUserId(), winner.getScore());
                continue;
            }

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
//            playerZhuang.setScore(playerZhuang.getScore() + delta);
            winner.setScore(winner.getScore() + delta);
            room.addUserSocre(winner.getUserId(), winner.getScore());
            //假如锅里没钱就跳出别的玩家喝水
        }
        this.room.setPotBottom(currentGuoDi);
        playerZhuang.setScore(currentGuoDi - lastGuoDi);

        for (PlayerTuiTongZi p : playerCardInfos.values()){
            p.setPotBottom(currentGuoDi);
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

        MsgSender.sendMsg2Player(serviceName, "gameResult", aList, this.users);

        if (isBaWangZhuang()){

            boolean updateZhuang = false;
            //强制下装
            if (!(((RoomTuiTongZi) room).getPotBottom() < 400 && ((RoomTuiTongZi) room).getPotBottom() >= 5)){
                updateZhuang = true;
            }

            if (updateZhuang){
                this.room.addUserSocre(this.bankerId, - 20 + this.room.getPotBottom());
                this.room.setPotBottom(0);
                this.room.setZhuangCount(0);
                //强制下装
            }

        }else {
            //发送
            //  假如需要下装
            //一轮有几次

            boolean updateZhuang = false;
            //强制下装
            if (this.room.getZhuangCount() == 8){
                updateZhuang = true;
            }

            if(this.room.getZhuangCount() != 8){
                //强制下装
                if (!(((RoomTuiTongZi) room).getPotBottom() < 400 && ((RoomTuiTongZi) room).getPotBottom() >= 5)){
                    updateZhuang = true;
                }
            }

            if (updateZhuang){
                this.room.addUserSocre(this.bankerId, - 20 + this.room.getPotBottom());
                this.room.setPotBottom(0);
                this.room.setZhuangCount(0);
            }
        }

        this.pushScoreChange();
    }
    /*
     * 轮庄
     * */
    protected long nextTurnId(long curId) {

        int index = users.indexOf(curId);

        int nextId = index + 1;
        if (nextId >= users.size()) {
            nextId = 0;
        }
        return users.get(nextId);
    }

    public int exchange(Long userId, int cardPattern){

        List<Integer> list = null;
        try {
            list = TuiTongZiCardUtils.cheat(this.room.cards, cardPattern);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (list != null){
            PlayerTuiTongZi playerTuiTongZi = playerCardInfos.get(userId);

            //把自己手里的牌和牌堆里的牌进行交换
            this.room.cards.removeAll(list);
            this.room.cards.addAll(playerTuiTongZi.getPlayerCards());

            //进行换牌操作
            playerTuiTongZi.getPlayerCards().clear();
            playerTuiTongZi.getPlayerCards().addAll(list);
            try {
                playerTuiTongZi.setPattern(TuiTongZiCardUtils.cardsPatterns(list));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Map result = new HashMap();
        result.put("userId", userId);
        result.put("cardPattern", cardPattern);
        result.put("cards", list);
        result.put("isFind", list != null? true : false);

        MsgSender.sendMsg2Player(serviceName, "exchangeResult", result, users);
        MsgSender.sendMsg2Player(serviceName, "exchange", "0", userId);

        return 0;
    }

    //推筒子作弊算法
    public void cheat(){
        if (this.room.cheatId != -1){

            boolean canExchange = false;
            PlayerTuiTongZi playerCheat = null;
            for (PlayerTuiTongZi player : playerCardInfos.values()){
                if (player.getUserId() == this.room.cheatId){
                    playerCheat = player;
                    break;
                }
            }

            List<List<Integer>> playerCards = new ArrayList<>();
            for (int i = 0; i < 8; i = i + 2){

                List<Integer> list = new ArrayList<>();
                list.add(i);
                list.add(i + 1);
                playerCards.add(list);
            }

            //把将要发的牌按照大小排序
            for (int i = 0; i < playerCards.size() -1; i++){
                for (int j = i + 1; j < playerCards.size(); j++){
                    List<Integer> listI = playerCards.get(i);
                    List<Integer> listJ = playerCards.get(j);
                    try {
                        if (TuiTongZiCardUtils.mAIsBiggerThanB(listI, listJ) == 2){
                            Collections.swap(playerCards, i, j);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public int setTestUser(Long userId){
        return 1;
    }

    public int bankerBreak(Long userId, Long flag){
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

    public RoomTuiTongZi getRoom() {
        return room;
    }

    public void setRoom(RoomTuiTongZi room) {
        this.room = room;
    }

    //更新操作时间
    protected void updateLastOperateTime() {
        this.lastOperateTime = System.currentTimeMillis();
    }

    //更新操作时间
    protected void updateRoomLastTime() {
        room.setRoomLastTime(System.currentTimeMillis());
    }

    public long getLastOperateTime() {
        return lastOperateTime;
    }

    public void setLastOperateTime(long lastOperateTime) {
        this.lastOperateTime = lastOperateTime;
    }

    public Map<Long, PlayerTuiTongZi> getPlayerCardInfos() {
        return playerCardInfos;
    }

    public void setPlayerCardInfos(Map<Long, PlayerTuiTongZi> playerCardInfos) {
        this.playerCardInfos = playerCardInfos;
    }


}
