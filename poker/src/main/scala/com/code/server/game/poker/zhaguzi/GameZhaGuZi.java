package com.code.server.game.poker.zhaguzi;
import com.code.server.constant.response.*;
import com.code.server.game.poker.doudizhu.CardUtil;
import com.code.server.game.poker.pullmice.IfCard;
import com.code.server.game.room.Game;
import com.code.server.game.room.PlayerCardInfo;
import com.code.server.game.room.Room;
import com.code.server.game.room.kafka.IfaceMsgSender;
import com.code.server.game.room.kafka.MsgSender;
import com.code.server.game.room.service.RoomManager;
import com.code.server.util.IdWorker;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import scala.Int;
import java.util.*;

import static com.code.server.game.poker.zhaguzi.CardUtilsError.MUST_JIE_FENG;

public class GameZhaGuZi extends Game {

    protected static final Logger logger = LoggerFactory.getLogger(GameZhaGuZi.class);

    protected static final String serviceName = "gameZhaGuZiService";

    protected Map<Long, PlayerZhaGuZi> playerCardInfos = new HashMap<>();

    protected RoomZhaGuZi room;

    protected List<Integer> cards = new ArrayList<Integer>();

    protected List<Map<String, Object>> leaveCards = new ArrayList<>();

    protected Integer status = ZhaGuZiConstant.START_GAME;

    public IfaceGameVo toVo(long watchUser) {

        GameZhaGuZiVo vo = new GameZhaGuZiVo();
        BeanUtils.copyProperties(this, vo);
        vo.cards.clear();
        for (int i = 0; i < this.cards.size(); i++){

            Integer card = this.cards.get(i);
            Integer ret = CardUtils.local2Client(card, new IfCard() {
                @Override
                public Map<Integer, Integer> cardDict() {
                    return CardUtils.getCardDict();
                }
            });
            vo.cards.add(ret);
        }
        return vo;
    }

    public void startGame(List<Long> users, Room room){

        this.room = (RoomZhaGuZi) room;
        this.users = users;
        initPlayer();
        initCards();
        deal();

        //第一局 红桃5 先说话
        if (this.room.curGameNumber == 1){

            PlayerZhaGuZi player = null;

            for (PlayerZhaGuZi playerZhaGuZi : playerCardInfos.values()){

                for (Integer card : playerZhaGuZi.cards){
                    //红桃五
                    if (card == 50){
                        player = playerZhaGuZi;
                        break;
                    }
                }
            }
            this.room.lastWinnderId = player.userId;
        }

        talkStart(this.room.lastWinnderId);
    }

    //发牌
    public void deal(){

        int count = cards.size() / users.size();

        //发牌：第一局随机选择给一位玩家先发牌；第二局发牌是上一局第一个出完牌的人的下一家先发第一张牌。
        long lastId = 0;
        if (this.room.curGameNumber == 1){

            Random random = new Random();
            int index = random.nextInt(this.room.getPersonNumber());
            lastId = users.get(index);

        }else {
            lastId = nextTurnId(this.room.lastWinnderId);
        }

        long currentId = lastId;

        Map<Object, Object> result = new HashMap<>();
        List<PlayerZhaGuZiVo> list = new ArrayList<>();

        while (true){
            PlayerZhaGuZi playerZhaGuZi = playerCardInfos.get(currentId);
            while (true){
                playerZhaGuZi.cards.add(this.cards.get(0));
                this.cards.remove(0);
                if (playerZhaGuZi.cards.size() == count){
                    currentId = nextTurnId(currentId);
                    break;
                }
            }

            list.add((PlayerZhaGuZiVo) playerZhaGuZi.toVo());
            //轮圈完毕
            if (currentId == lastId){
                break;
            }
        }
        result.put("players", list);
        MsgSender.sendMsg2Player(serviceName, "deal", result, users);
    }

    //通知发话
    public void talkStart(long userId){
        MsgSender.sendMsg2Player(serviceName, "talkStart","pleaseTalk",userId);
    }

    //说话
    public int talk(long userId, int op, int card){
        PlayerZhaGuZi playerZhaGuZi = this.playerCardInfos.get(userId);

        if (op != Operator.LIANG_SAN && op != Operator.ZHA_GU && op != Operator.BU_LIANG){
            return ErrorCode.OPERATOR_ERROR;
        }

        if (playerZhaGuZi.getOp() != Operator.NOT_OPERATION){
            //已经说话完毕了
            return ErrorCode.ALREADY_TALK;
        }

        //判断玩家说话是否正常
        int isSanJia = playerZhaGuZi.getIsSanJia();
        //如果是3家
        if (isSanJia == playerZhaGuZi.SAN_JIA){

            if (op != Operator.BU_LIANG && op != Operator.LIANG_SAN){
                return ErrorCode.OPERATOR_ERROR;
            }

        }else {

            if (op != Operator.BU_LIANG && op != Operator.ZHA_GU){
                return ErrorCode.OPERATOR_ERROR;
            }

        }

        if (op == Operator.BU_LIANG && card != -1){
            return ErrorCode.OPERATOR_ERROR;
        }

        // 说明牌是应该是3
        if (card != -1){

            Integer localCardValue = CardUtils.client2Local(card, new IfCard() {
                @Override
                public Map<Integer, Integer> cardDict() {
                    return CardUtils.getCardDict();
                }
            });

            //如果不是3
            if (localCardValue != 4 && localCardValue != 5 && localCardValue != 6 && localCardValue != 7){
                return ErrorCode.OPERATOR_ERROR;
            }

            //暂时只考虑五人的情况
            if (op == Operator.LIANG_SAN){
                if (card != 7 && card != 9){
                    return ErrorCode.OPERATOR_ERROR;
                }
            }

            if (op == Operator.ZHA_GU){
                if (card != 6 && card != 8){
                    return ErrorCode.OPERATOR_ERROR;
                }
            }
        }

        playerZhaGuZi.setOp(op);
        playerZhaGuZi.getLiangList().add(card);

        MsgSender.sendMsg2Player(serviceName, "talkResult","0",userId);

        long nextId = nextTurnId(userId);
        //全部说话完毕
        if (nextId == this.room.lastWinnderId){
            status = ZhaGuZiConstant.TALK;
            playStart();
        }else {
            talkStart(nextId);
        }
        return 0;
    }

    //开始打牌
    public void playStart(){

        int count = 0;
        for (PlayerZhaGuZi playerZhaGuZi : playerCardInfos.values()){
            if (playerZhaGuZi.getOp() == Operator.LIANG_SAN || playerZhaGuZi.getOp() == Operator.ZHA_GU){
                count++;
            }
        }

        //没人亮牌
        if (count == 0){

            computeShuffle();

        }else {

            PlayerZhaGuZi hongTao = null;
            PlayerZhaGuZi fangPian = null;
            PlayerZhaGuZi heiTao = null;

            for (PlayerZhaGuZi playerZhaGuZi : playerCardInfos.values()){

                for (Integer card : playerZhaGuZi.cards){
                    //红桃三
                    if (card == 7){
                        hongTao = playerZhaGuZi;
                    }else if(card == 9){
                        fangPian = playerZhaGuZi;
                    }else if(card == 6){
                        heiTao = playerZhaGuZi;
                    }
                }
            }

             if (this.room.getPersonNumber() == 5){
                //红桃三方片三是一个人
                if (hongTao == fangPian){
                    status = ZhaGuZiConstant.GIVE_UP;
                    MsgSender.sendMsg2Player(serviceName, "isGiveUpStart", "0", hongTao.userId);

                }else {
                    continuePlay();
                }

             }else {
                 //红桃三方片三黑桃三是一个人
                 if (hongTao == fangPian && hongTao == heiTao){
                     status = ZhaGuZiConstant.GIVE_UP;
                     MsgSender.sendMsg2Player(serviceName, "isGiveUpStart", "0", hongTao.userId);



                 }else {
                     continuePlay();
                 }
             }
        }
    }

    //出牌
    public void continuePlay(){

        status = ZhaGuZiConstant.BEING_DISCARD;

        PlayerZhaGuZi player = null;
        //有红桃5的先出
        if (this.room.curGameNumber == 1){

            for (PlayerZhaGuZi playerZhaGuZi : playerCardInfos.values()){

                for (Integer card : playerZhaGuZi.cards){
                    //红桃五
                    if (card == 50){
                        player = playerZhaGuZi;
                        break;
                    }
                }
            }

        }else {

            player = playerCardInfos.get(this.room.lastWinnderId);
        }

        //提示出牌
        MsgSender.sendMsg2Player(serviceName, "discardStart", "0", player.userId);

    }

    //算分
    public void compute(boolean isOver1, boolean isOver2){

        //这时候出牌序号不一定有

        status = ZhaGuZiConstant.COMPUTE;

        //保存头游
        for (PlayerZhaGuZi playerZhaGuZi : playerCardInfos.values()){

            if (playerZhaGuZi.rank == 1){
                this.room.lastWinnderId = playerZhaGuZi.userId;
                break;
            }
        }

        List<PlayerZhaGuZi> aList = new ArrayList<>();
        for (PlayerZhaGuZi playerZhaGuZi : playerCardInfos.values()){
            aList.add(playerZhaGuZi);
        }

        int ret = CardUtils.findWinnerList(aList);

        if (ret == 0){
            logger.info("三家赢");
        }else if (ret == 1){
            logger.info("平局");
        }else {
            logger.info("股家赢");
        }

        //具体算分
        for (PlayerZhaGuZi playerZhaGuZi : aList){

        }

        this.pushScoreChange();

        this.genRecord();

        this.sendFinalResult();
    }

    //重新洗牌算分
    public void computeShuffle(){

        status = ZhaGuZiConstant.COMPUTE;

        this.pushScoreChange();

        this.genRecord();

        this.sendFinalResult();

    }

    //认输了
    public void computeGiveUp(long uid){

        status = ZhaGuZiConstant.COMPUTE;

        PlayerZhaGuZi playerSanJia = playerCardInfos.get(uid);

        double score = 0;

        for (PlayerZhaGuZi playerZhaGuZi : playerCardInfos.values()){

            if (playerSanJia != playerZhaGuZi){
                playerSanJia.setScore(1d);
                score += 1d;
            }
        }

        playerSanJia.setScore(playerSanJia.getScore() - score);

        for (PlayerZhaGuZi playerZhaGuZi : playerCardInfos.values()){
            this.room.addUserSocre(playerZhaGuZi.userId, playerZhaGuZi.getScore());
        }

        this.pushScoreChange();

        this.genRecord();

        this.sendFinalResult();
    }

    //生成战绩
    public void genRecord(){
        long id = IdWorker.getDefaultInstance().nextId();
        Map<Long, Double> map = new HashMap<>();
        for (Map.Entry<Long, PlayerZhaGuZi> entry : playerCardInfos.entrySet()){
            PlayerZhaGuZi p = entry.getValue();
            map.put(p.userId, p.getScore() + 0.0);
        }
        genRecord(map, this.room, id);
    }

    public void sendFinalResult(){

        if (this.room.curGameNumber == 6){
            //因为是两圈，并且要求换zhu
            List<UserOfResult>  userOfResult =  this.room.getUserOfResult();
            GameOfResult gameOfResult = new GameOfResult();
            gameOfResult.setUserList(userOfResult);
            MsgSender.sendMsg2Player("gameService", "gameZhaGuZiFinalResult", gameOfResult, users);
            RoomManager.removeRoom(room.getRoomId());

            this.room.genRoomRecord();
        }

    }

    public void pushScoreChange() {
        MsgSender.sendMsg2Player(new ResponseVo("gameService", "scoreChangeZhaGuZi", this.room.userScores), this.getUsers());
    }

    //提示出牌
    public void noticeDiscardStart(long uid){

        //三家是否出完牌
        boolean isOver1 = true;
        for (PlayerZhaGuZi p : playerCardInfos.values()){

            if (p.getIsSanJia() == PlayerZhaGuZi.SAN_JIA){
                if (p.isOver() == false){
                    isOver1 = false;
                    break;
                }
            }
        }

        boolean isOver2 = true;
        for (PlayerZhaGuZi p : playerCardInfos.values()){

            if (p.getIsSanJia() == PlayerZhaGuZi.GU_JIA){
                if (p.isOver() == false){
                    isOver2 = false;
                    break;
                }
            }
        }

        //是否有一家出完牌
        if (isOver1 != true && isOver2 != true){
            compute(isOver1, isOver2);
            return;
        }

        long nextID = uid;

        while (true){
            nextID = nextTurnId(nextID);
            if (playerCardInfos.get(nextID).isOver() == false){
                break;
            }
        }

        for (PlayerZhaGuZi playerZhaGuZi : playerCardInfos.values()){
            playerZhaGuZi.setCanJieFeng(false);
        }

        PlayerZhaGuZi playerZhaGuZi = playerCardInfos.get(nextID);

        boolean ret = isCanJieFeng(playerZhaGuZi);

        playerZhaGuZi.setCanJieFeng(ret);

                MsgSender.sendMsg2Player(serviceName, "discardStart", playerZhaGuZi.toVo(), nextID, new IfaceMsgSender() {
            @Override
            public void callback(Object object, long userId) {

            }
        });

    }

    //能不能接风
    public boolean isCanJieFeng(PlayerZhaGuZi playerZhaGuZi){

        boolean isCanJieFeng = true;

        long lastId = playerZhaGuZi.userId;
        while (true){

            //上一个人的id
            lastId = lastTurn(lastId);

            if (lastId == playerZhaGuZi.userId){
                break;
            }

            PlayerZhaGuZi shangJia = playerCardInfos.get(lastTurn(playerZhaGuZi.userId));
            if (shangJia.isOver() == false){
                isCanJieFeng = false;
            }

            PlayerZhaGuZi playerCurrent = playerCardInfos.get(lastId);
            int operator = playerCurrent.opList.get(playerCurrent.opList.size() - 1);

            if (playerCurrent.isOver()) continue;

            if (operator != Operator.PASS){
                isCanJieFeng = false;
            }
        }

        return isCanJieFeng;

    }

    //出牌协议
    public int beingDiscard(long uid, int op, String li){

        List<Integer> list = CardUtils.transfromStringToCards(li);
        PlayerZhaGuZi playerZhaGuZi = playerCardInfos.get(uid);
        //如果过的话 提示下一个人出牌
        if (op == Operator.PASS){

            if (playerZhaGuZi.isCanJieFeng() == true){
                return MUST_JIE_FENG;
            }

            playerZhaGuZi.opList.add(op);
            MsgSender.sendMsg2Player(serviceName, "discardResult", "0", uid);
            noticeDiscardStart(uid);
        }
        //管上
        else if(op == Operator.GUAN_SHANG){

            boolean ret = false;

            Map<String, Object> last = null;
            if (leaveCards.size() != 0){
                last = leaveCards.get(leaveCards.size() - 1);
            }

            //第一次出牌
            if (last == null){
                playerZhaGuZi.opList.add(op);
                MsgSender.sendMsg2Player(serviceName, "discardResult", "0", uid);

                for (Integer a : list){
                    playerZhaGuZi.cards.remove(a);
                }

                Map<String, Object> map = new HashMap();
                map.put("uid", uid);
                map.put("cards", list);
                leaveCards.add(map);
                noticeDiscardStart(uid);
            }else {

                PlayerZhaGuZi playerZhaGuZi1 = playerCardInfos.get(uid);
                PlayerZhaGuZi playerZhaGuZi2 = playerCardInfos.get(last.get("uid"));

                int res = CardUtils.compare(playerZhaGuZi1, list, playerZhaGuZi2, (List<Integer>) last.get("cards"));
                //说明报错了
                if (res != 0 && res != 1 && res != 2){
                    return res;
                }

                playerZhaGuZi.opList.add(op);
                //是不是接风
                PlayerZhaGuZi lastPlayer = playerCardInfos.get(lastTurn(uid));

//                boolean isFeng = false;
//                //假如上一个人出牌完毕
//                if (lastPlayer.isOver()){
//                    boolean ret1 = playerZhaGuZi.opList.size() - lastPlayer.opList.size() == 1 ? true : false;
//                    boolean ret2 = true;
//
//                    for (PlayerZhaGuZi p : playerCardInfos.values()){
//
//                        if (p.isOver() == true){
//                            continue;
//                        }
//                        if ((p.opList.get(p.opList.size() - 1) != Operator.PASS)){
//                            ret2 = false;
//                            break;
//                        }
//                    }
//
//                    //轮到自己接风
//                    if (ret1 && ret2){
//                        isFeng = true;
//                    }
//                }

                boolean isFeng = playerZhaGuZi.isCanJieFeng();

                if (isFeng == false){
                    if (res == 0 || res == 1){
                        return ErrorCode.CAN_NOT_DISCARD;
                    }
                }

                //减去玩家手中的牌
                for (Integer a : list){
                    playerZhaGuZi.cards.remove(a);
                }

                //计算头和尾游
                if (playerZhaGuZi.cards.size() == 0){

                    int max = -1;
                    for (PlayerZhaGuZi player : playerCardInfos.values()){
                        if (max  < player.rank){
                            max = player.rank;
                        }
                    }

                    playerZhaGuZi.rank += 1;
                }

                List<Object> resList = new ArrayList<>();
                for (PlayerZhaGuZi player : playerCardInfos.values()){
                    Map<Object, Object> result = new HashMap<>();
                    result.put("uid", player.userId);
                    result.put("player", player.toVo());
                    resList.add(resList);
                }

                Map resultMap = new HashMap();
                resultMap.put("resList", resList);
                MsgSender.sendMsg2Player(serviceName, "discardResult", resultMap, uid);

                Map<String, Object> map = new HashMap();
                map.put("uid", uid);
                map.put("cards", list);
                leaveCards.add(map);
                noticeDiscardStart(uid);
            }
        }

        return 0;
    }

    //上一个人
    public long lastTurn(long uid){
        int index = this.users.indexOf(uid);
        int last = index - 1;
        if (last < 0){
            last = this.users.size() - 1;
        }
        return this.users.get(last);
    }

    public int isGiveUp(long uid, boolean isGiveUp){

        MsgSender.sendMsg2Player(serviceName, "isGiveUpResult", "0", uid);

        if (isGiveUp){
            computeGiveUp(uid);
        }else {
            continuePlay();
        }

        return 0;
    }

    public void initPlayer(){

        playerCardInfos.clear();
        for (Long uid : users){
            PlayerZhaGuZi playerZhaGuZiz = new PlayerZhaGuZi();
            playerZhaGuZiz.userId = uid;
            playerZhaGuZiz.setRoomPersonNum(this.room.getPersonNumber());
            playerCardInfos.put(uid, playerZhaGuZiz);
        }
    }

    public void initCards(){

        for (int i = 0; i < 54; i++){

            if (i > 45 && i < 50){
                continue;
            }

            this.cards.add(i);
        }
        //洗牌
        Collections.shuffle(this.cards);
    }

}
