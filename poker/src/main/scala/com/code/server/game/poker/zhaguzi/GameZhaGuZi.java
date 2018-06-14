package com.code.server.game.poker.zhaguzi;

import com.code.server.constant.response.*;
import com.code.server.game.poker.pullmice.IfCard;
import com.code.server.game.room.Game;
import com.code.server.game.room.Room;
import com.code.server.game.room.kafka.MsgSender;
import com.code.server.game.room.service.RoomManager;
import com.code.server.util.IdWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.util.*;

import static com.code.server.game.poker.zhaguzi.CardUtilsError.MUST_HONGTAO_FIVE;
import static com.code.server.game.poker.zhaguzi.CardUtilsError.MUST_JIE_FENG;

public class GameZhaGuZi extends Game {

    protected static final Logger logger = LoggerFactory.getLogger(GameZhaGuZi.class);

    protected static final String serviceName = "gameZhaGuZiService";

    protected Map<Long, PlayerZhaGuZi> playerCardInfos = new HashMap<>();

    protected RoomZhaGuZi room;

    protected List<Integer> cards = new ArrayList<Integer>();

    protected List<Map<String, Object>> leaveCards = new ArrayList<>();

    public List<Map<String, Object>> getLeaveCards() {
        return leaveCards;
    }

    public void setLeaveCards(List<Map<String, Object>> leaveCards) {
        this.leaveCards = leaveCards;
    }

    protected Integer status = ZhaGuZiConstant.START_GAME;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    protected long currentTalkId;

    public long getCurrentTalkId() {
        return currentTalkId;
    }

    public void setCurrentTalkId(long currentTalkId) {
        this.currentTalkId = currentTalkId;
    }

    protected  PlayerZhaGuZi lastOverPlayer = null;

    //第一个出牌人的Id
    protected  long firstDiscardId;

    public IfCard ifCard = new IfCard() {
        @Override
        public Map<Integer, Integer> cardDict() {
            return CardUtils.getCardDict();
        }
    };

    public IfaceGameVo toVo(long watchUser) {

        GameZhaGuZiVo vo = new GameZhaGuZiVo();
        BeanUtils.copyProperties(this, vo);
//        vo.cards.clear();

        List<Integer> aList = new ArrayList<>();
        for (int i = 0; i < this.cards.size(); i++) {

            Integer card = this.cards.get(i);
            Integer ret = CardUtils.local2Client(card, new IfCard() {
                @Override
                public Map<Integer, Integer> cardDict() {
                    return CardUtils.getCardDict();
                }
            });

            aList.add(ret);
        }

        vo.cards = aList;

        for (PlayerZhaGuZi playerZhaGuZi : playerCardInfos.values()) {
            vo.playerCardInfos.put(playerZhaGuZi.getUserId(), (PlayerZhaGuZiVo) playerZhaGuZi.toVo());
        }

        vo.leaveCards = new ArrayList<>();

        for (Map<String, Object> oo : this.leaveCards) {

            List<Integer> localCards = (List<Integer>) oo.get("cards");
            List<Integer> clientCards = new ArrayList<>();

            for (Integer l : localCards) {
                Integer c = CardUtils.local2Client(l, ifCard);
                clientCards.add(c);
            }

            Map<String, Object> copyOO = new HashMap<>();
            copyOO.putAll(oo);

            copyOO.put("cards", clientCards);

            vo.leaveCards.add(copyOO);

        }

        return vo;
    }

    public void startGame(List<Long> users, Room room) {

        this.room = (RoomZhaGuZi) room;
        this.users = users;

        initPlayer();
        initCards();

        //通知其他人游戏已经开始
        MsgSender.sendMsg2Player(new ResponseVo("gameService", "gameZhaGuZiBegin", "ok"), this.getUsers());

        deal();

        //第一局 红桃5 先说话
        if (this.room.curGameNumber == 1) {

            PlayerZhaGuZi player = null;

            for (PlayerZhaGuZi playerZhaGuZi : playerCardInfos.values()) {

                for (Integer card : playerZhaGuZi.cards) {
                    //红桃五
                    if (card == 50) {
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
    public void deal() {

        int count = cards.size() / users.size();

        //发牌：第一局随机选择给一位玩家先发牌；第二局发牌是上一局第一个出完牌的人的下一家先发第一张牌。
        long lastId = 0;
        if (this.room.curGameNumber == 1) {

            Random random = new Random();
            int index = random.nextInt(this.room.getPersonNumber());
            lastId = users.get(index);

        } else {
            lastId = nextTurnId(this.room.lastWinnderId);
        }

        long currentId = lastId;

        Map<Object, Object> result = new HashMap<>();
        List<PlayerZhaGuZiVo> list = new ArrayList<>();

        while (true) {
            PlayerZhaGuZi playerZhaGuZi = playerCardInfos.get(currentId);
            while (true) {
                playerZhaGuZi.cards.add(this.cards.get(0));
                this.cards.remove(0);
                if (playerZhaGuZi.cards.size() == count) {
                    int isSanJia = CardUtils.computeIsSanJia(this.room.getPersonNumber(), playerZhaGuZi);
                    playerZhaGuZi.setSanJia(isSanJia);

                    for (Integer card : playerZhaGuZi.cards) {

                        if (card > 5 && card < 10) {
                            playerZhaGuZi.getRetain3List().add(card);
                        }
                    }
                    list.add((PlayerZhaGuZiVo) playerZhaGuZi.toVo());
                    currentId = nextTurnId(currentId);
                    break;
                }
            }

            //轮圈完毕
            if (currentId == lastId) {
                break;
            }
        }

        result.put("players", list);
        MsgSender.sendMsg2Player(serviceName, "deal", result, users);

    }

    //通知发话
    public void talkStart(long userId) {

        this.currentTalkId = userId;

        PlayerZhaGuZi player = playerCardInfos.get(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("uid", player.getUserId());
        result.put("sanJia", player.getSanJia());

//        Integer hongsan = CardUtils.string2Local("红桃-3", ifCard);
//        Integer fangpian = CardUtils.string2Local("方片-3", ifCard);

//        boolean giveup = false;
//        if (player.getRetain3List().contains(hongsan) && player.getRetain3List().contains(fangpian)) {
//            giveup = true;
//        }

        boolean giveup = CardUtils.canGiveUp(player);

        result.put("giveup", giveup);
        MsgSender.sendMsg2Player(serviceName, "talkStart", result, users);
    }

    //说话
    public int talk(long userId, int op, String cards) {

        List<Integer> aList = CardUtils.transfromStringToCards(cards);

        //本地牌编号
        List<Integer> bList = new ArrayList<>();

        for (Integer v : aList) {

            if (v == -1) {
                bList.add(v);
                continue;
            }

            Integer va = CardUtils.client2Local(v, ifCard);

            bList.add(va);

        }

        PlayerZhaGuZi playerZhaGuZi = this.playerCardInfos.get(userId);

        if (op != Operator.LIANG_SAN && op != Operator.ZHA_GU && op != Operator.BU_LIANG) {
            return ErrorCode.OPERATOR_ERROR;
        }

        if (playerZhaGuZi.getOp() != Operator.MEI_LIANG) {
            //已经说话完毕了
            return ErrorCode.ALREADY_TALK;
        }

        //判断玩家说话是否正常

        int isSanJia = playerZhaGuZi.getSanJia();
        //如果是3家

        if (op == Operator.BU_LIANG) {

            if (bList.get(0) != -1) {
                return ErrorCode.OPERATOR_ERROR;
            }

        } else if (op == Operator.LIANG_SAN) {

            //不是三家 操作了 亮3
            if (isSanJia != PlayerZhaGuZi.SAN_JIA) {
                return ErrorCode.OPERATOR_ERROR;
            }

            if (this.room.getPersonNumber() == 5){

                if (bList.contains(7) == false && bList.contains(9) == false) {
                    return ErrorCode.OPERATOR_ERROR;
                }


            }else {

                if (bList.contains(7) == false && bList.contains(9) == false && bList.contains(6) == false) {
                    return ErrorCode.OPERATOR_ERROR;
                }
            }


            for (Integer card : bList) {
                // 说明牌是应该是3
//                Integer localCardValue = CardUtils.client2Local(card, ifCard);
                if ((card - 2) / 4 != 1) {
                    return ErrorCode.OPERATOR_ERROR;
                }
            }


        } else if (op == Operator.ZHA_GU) {

            //不是股家操作了扎股
            if (isSanJia != PlayerZhaGuZi.GU_JIA) {
                return ErrorCode.OPERATOR_ERROR;
            }

            for (Integer card : bList) {

                if (this.room.getPersonNumber() == 5){
                    if (card != 6 && card != 8 && card != -1) {
                        return ErrorCode.OPERATOR_ERROR;
                    }
                }else {

                    if (card != 8 && card != -1) {
                        return ErrorCode.OPERATOR_ERROR;
                    }
                }

            }
        }

        playerZhaGuZi.setOp(op);
        playerZhaGuZi.getLiangList().addAll(bList);

        Map<String, Object> result = new HashMap<>();
        result.put("uid", userId);
        result.put("op", op);
        result.put("cards", aList);
        MsgSender.sendMsg2Player(serviceName, "talkResult", result, users);
        MsgSender.sendMsg2Player(serviceName, "talk", "0", userId);

        long nextId = nextTurnId(userId);
        //全部说话完毕
        if (nextId == this.room.lastWinnderId) {
            status = ZhaGuZiConstant.TALK;
            playStart();
        } else {
            talkStart(nextId);
        }

        return 0;
    }

    //开始打牌
    public void playStart() {

        int count = 0;
        for (PlayerZhaGuZi playerZhaGuZi : playerCardInfos.values()) {
            if (playerZhaGuZi.getOp() == Operator.LIANG_SAN || playerZhaGuZi.getOp() == Operator.ZHA_GU) {
                count++;
            }
        }

        //没人亮牌
        if (count == 0) {

            computeShuffle();

        } else {

            continuePlay();
        }
    }

    //出牌
    public void continuePlay() {

        status = ZhaGuZiConstant.BEING_DISCARD;

        PlayerZhaGuZi player = null;
        //有红桃5的先出
        if (this.room.curGameNumber == 1) {

            for (PlayerZhaGuZi playerZhaGuZi : playerCardInfos.values()) {

                for (Integer card : playerZhaGuZi.cards) {
                    //红桃五
                    if (card == 51) {
                        player = playerZhaGuZi;
                        break;
                    }
                }
            }

        } else {

            player = playerCardInfos.get(this.room.lastWinnderId);
        }


        for (PlayerZhaGuZi playerZhaGuZi : playerCardInfos.values()) {

            if (playerZhaGuZi == player) {
                playerZhaGuZi.setSelfTurn(true);
            } else {
                playerZhaGuZi.setSelfTurn(false);
            }
        }

        this.firstDiscardId = player.getUserId();

//        //提示出牌
        MsgSender.sendMsg2Player(serviceName, "discardStart", assembleDiscardResult(player), users);

    }

    //提示出牌
    public void noticeDiscardStart(long uid) {

        //三家是否出完牌
        boolean isOver1 = true;
        for (PlayerZhaGuZi p : playerCardInfos.values()) {

            if (p.getSanJia() == PlayerZhaGuZi.SAN_JIA) {
                if (p.isOver() == false) {
                    isOver1 = false;
                    break;
                }
            }
        }

        boolean isOver2 = true;
        for (PlayerZhaGuZi p : playerCardInfos.values()) {

            if (p.getSanJia() == PlayerZhaGuZi.GU_JIA) {
                if (p.isOver() == false) {
                    isOver2 = false;
                    break;
                }
            }
        }

        //是否有一家出完牌
        if (isOver1 == true || isOver2 == true) {
            compute(isOver1, isOver2);
            return;
        }

        long nextID = uid;

        while (true) {
            nextID = nextTurnId(nextID);
            if (playerCardInfos.get(nextID).isOver() == false) {
                break;
            }
        }

        for (PlayerZhaGuZi playerZhaGuZi : playerCardInfos.values()) {
            playerZhaGuZi.setCanJieFeng(false);
        }

        PlayerZhaGuZi playerZhaGuZi = playerCardInfos.get(nextID);

        boolean ret = isCanJieFeng(playerZhaGuZi);

        playerZhaGuZi.setCanJieFeng(ret);

        //是否轮到自己出牌
        for (PlayerZhaGuZi ppp : playerCardInfos.values()) {

            if (ppp == playerZhaGuZi) {
                ppp.setSelfTurn(true);
            } else {
                ppp.setSelfTurn(false);
            }
        }

        MsgSender.sendMsg2Player(serviceName, "discardStart", assembleDiscardResult(playerZhaGuZi), users);

    }

    //出牌协议
    public int discard(long uid, int op, String li) {
        List<Integer> clientList = CardUtils.transfromStringToCards(li);

        Map<String, Object> result = new HashMap<>();
        result.put("cards", clientList);
        result.put("op", op);
        result.put("uid", uid);

        List<Integer> list = new ArrayList<>();
        for (Integer value : clientList) {
            if (value == -1) {
                continue;
            }
            Integer local = CardUtils.client2Local(value, ifCard);
            list.add(local);
        }

        PlayerZhaGuZi playerZhaGuZi = playerCardInfos.get(uid);
        //如果过的话 提示下一个人出牌
        if (op == Operator.PASS) {

            if (playerZhaGuZi.isCanJieFeng() == true) {
                return MUST_JIE_FENG;
            }
            playerZhaGuZi.opList.add(op);

            result.put("player", assembleDiscardResult(playerZhaGuZi));
            MsgSender.sendMsg2Player(serviceName, "discardResult", result, users);
            MsgSender.sendMsg2Player(serviceName, "discard", 0, uid);
            //
            Map<String, Object> map = new HashMap();
            map.put("uid", uid);
            map.put("op", op);
            map.put("cards", list);
            leaveCards.add(map);
            noticeDiscardStart(uid);
        }
        //管上
        else if (op == Operator.GUAN_SHANG) {

            Map<String, Object> last = null;
            if (leaveCards.size() != 0) {
//                last = leaveCards.get(leaveCards.size() - 1);
                int lastIndex = leaveCards.size() - 1;

                while (true) {


                    if (lastIndex >= 0) {

                        last = leaveCards.get(lastIndex);
                        Integer lastOp = (Integer) last.get("op");

                        if (lastOp != Operator.PASS) {
                            break;
                        }

                    } else {

                        System.out.println("没有找到 报异常 -------");
                        break;
                    }

                    lastIndex--;
                }
            }

            //第一次出牌
            if (last == null) {

                Integer local8 = CardUtils.string2Local("红桃-5", ifCard);
                //第一把第一个人必须出红桃5
                if (!list.contains(local8) && this.room.curGameNumber == 1) {
                    return MUST_HONGTAO_FIVE;
                }

                //然后判断 list

                int type = CardUtils.computeCardType(playerZhaGuZi, list);
                if (type == CardUtils.ERROR){
                    return ErrorCode.CARDS_ERROR;
                }

                result.put("player", assembleDiscardResult(playerZhaGuZi));

                playerZhaGuZi.opList.add(op);
                MsgSender.sendMsg2Player(serviceName, "discardResult", result, users);
                MsgSender.sendMsg2Player(serviceName, "discard", 0, uid);
                for (Integer a : list) {
                    playerZhaGuZi.cards.remove(a);
                }

                Map<String, Object> map = new HashMap();
                map.put("uid", uid);
                map.put("op", op);
                map.put("cards", list);
                leaveCards.add(map);
                noticeDiscardStart(uid);
            } else {

                PlayerZhaGuZi playerZhaGuZi1 = playerCardInfos.get(uid);
                PlayerZhaGuZi playerZhaGuZi2 = playerCardInfos.get(last.get("uid"));

                int type = CardUtils.computeCardType(playerZhaGuZi, list);
                if (type == CardUtils.ERROR){
                    return ErrorCode.CARDS_ERROR;
                }

                int res = 0;
                boolean isFeng = playerZhaGuZi.isCanJieFeng();

                //如果不是同一个人在判断
                if (playerZhaGuZi1.userId != playerZhaGuZi2.userId){
                    if (isFeng == false) {

                         res = CardUtils.compare(playerZhaGuZi1, list, playerZhaGuZi2, (List<Integer>) last.get("cards"));
                        //说明报错了
                        if (res != 0 && res != 1 && res != 2) {
                            return ErrorCode.CARDS_ERROR;
                        }

                        if (res == 1 || res == 2) {
                            return ErrorCode.CAN_NOT_DISCARD;
                        }
                    }
                }

                playerZhaGuZi.opList.add(op);

                //减去玩家手中的牌
                for (Integer a : list) {
                    playerZhaGuZi.cards.remove(a);
                }

                //计算头和尾游
                if (playerZhaGuZi.cards.size() == 0) {

                    int max = -1;
                    for (PlayerZhaGuZi player : playerCardInfos.values()) {
                        if (max < player.rank) {
                            max = player.rank;
                        }
                    }

                    playerZhaGuZi.rank = max + 1;

                    this.lastOverPlayer = playerZhaGuZi;
                }

                List<PlayerZhaGuZiVo> bList = new ArrayList<>();
                for (PlayerZhaGuZi player : playerCardInfos.values()) {
                    PlayerZhaGuZiVo vo = (PlayerZhaGuZiVo) player.toVo();
                    bList.add(vo);
                }

                result.put("player", assembleDiscardResult(playerZhaGuZi));
                MsgSender.sendMsg2Player(serviceName, "discardResult", result, users);
                MsgSender.sendMsg2Player(serviceName, "discard", 0, uid);

                Map<String, Object> map = new HashMap();
                map.put("uid", uid);
                map.put("op", op);
                map.put("cards", list);
                leaveCards.add(map);
                noticeDiscardStart(uid);
            }
        }

        return 0;
    }

    //算分
    public void compute(boolean isOver1, boolean isOver2) {

        //这时候出牌序号不一定有

        status = ZhaGuZiConstant.COMPUTE;

        //保存头游
        for (PlayerZhaGuZi playerZhaGuZi : playerCardInfos.values()) {

            if (playerZhaGuZi.rank == 1) {
                this.room.lastWinnderId = playerZhaGuZi.userId;
                break;
            }
        }

        List<PlayerZhaGuZi> aList = new ArrayList<>();
        for (PlayerZhaGuZi playerZhaGuZi : playerCardInfos.values()) {
            aList.add(playerZhaGuZi);
        }

        int ret = CardUtils.findWinnerList(aList);

        if (ret == 0) {
            logger.info("三家赢");
        } else if (ret == 1) {
            logger.info("平局");
        } else {
            logger.info("股家赢");
        }

        double base = 0;

        Integer hongtaosan = CardUtils.string2Local("红桃-3", ifCard);
        Integer fangpiansan = CardUtils.string2Local("方片-3", ifCard);
        Integer heitaosan = CardUtils.string2Local("黑桃-3", ifCard);
        Integer meihuasan = CardUtils.string2Local("梅花-3", ifCard);

        if (ret != 1) {

            //先算两三数和扎股数之和

            //具体算分
            for (PlayerZhaGuZi playerZhaGuZi : aList) {

                if (playerZhaGuZi.getSanJia() == PlayerZhaGuZi.SAN_JIA) {

                    if (playerZhaGuZi.getOp() == Operator.LIANG_SAN) {
                        //如果是三家
                        List<Integer> liangList = playerZhaGuZi.getLiangList();

                        if (!liangList.contains(hongtaosan) && !liangList.contains(fangpiansan)) {
                            logger.warn("亮三错误，检查talk");
                        }

                        if (liangList.contains(hongtaosan)) {
                            base += 2;
                        }

                        if (liangList.contains(fangpiansan)) {
                            base += 1;
                        }

                        if (liangList.contains(heitaosan)) {
                            base += 1;
                        }

                        if (liangList.contains(meihuasan)) {
                            base += 1;
                        }
                    }

                } else if (playerZhaGuZi.getSanJia() == PlayerZhaGuZi.GU_JIA) {

                    if (playerZhaGuZi.getOp() == Operator.ZHA_GU) {
                        base++;
                        //如果是股家
                        List<Integer> liangList = playerZhaGuZi.getLiangList();

                        if (liangList.contains(meihuasan)) {
                            base++;
                        }

                        if (liangList.contains(heitaosan)) {
                            base++;
                        }
                    }

                }
            }

            if (ret == 2){
                //算红桃3是不是憋手里
                for (PlayerZhaGuZi player : playerCardInfos.values()){
                    if (player.getRetain3List().contains(hongtaosan)){
                        if (player.cards.contains(hongtaosan)){
                            base++;
                        }
                        break;
                    }
                }
            }

            int count = 0;

            if (ret == 0) {

                for (PlayerZhaGuZi playerZhaGuZi : aList) {
                    if ((playerZhaGuZi.getSanJia() == PlayerZhaGuZi.GU_JIA) && (!playerZhaGuZi.isOver())) {
                        count++;
                    }
                }
            } else {
                for (PlayerZhaGuZi playerZhaGuZi : aList) {
                    if ((playerZhaGuZi.getSanJia() == PlayerZhaGuZi.SAN_JIA) && (!playerZhaGuZi.isOver())) {
                        count++;
                    }
                }
            }

            logger.warn("{}家没出完, base是{}" , count, base);

            base += count;

            int sanCount = 0;
            for (PlayerZhaGuZi playerZhaGuZi1 : playerCardInfos.values()){
                if (playerZhaGuZi1.getSanJia() == PlayerZhaGuZi.SAN_JIA){
                    sanCount++;
                }
            }

            if (ret == 0) {
                //三家赢
                for (PlayerZhaGuZi playerZhaGuZi : aList) {
                    double score = 0;
                    if (playerZhaGuZi.getSanJia() == PlayerZhaGuZi.SAN_JIA) {

                        if (sanCount == 1){
                            score = 4 * base;
                        }else if(sanCount == 2){
                            if (playerZhaGuZi.getRetain3List().contains(hongtaosan)){
                                score = (2 * base);
                            }else {
                                score = base;
                            }
                        }

                    } else if (playerZhaGuZi.getSanJia() == PlayerZhaGuZi.GU_JIA) {
                        score = -base;
                    }
                    playerZhaGuZi.setScore(score);
                }

            } else if (ret == 2) {
                //股家赢
                for (PlayerZhaGuZi playerZhaGuZi : aList) {

                    double score = 0;

                    if (playerZhaGuZi.getSanJia() == PlayerZhaGuZi.SAN_JIA) {

                        if (sanCount == 1){
                            score = - 4 * base;
                        }else if(sanCount == 2){
                            if (playerZhaGuZi.getRetain3List().contains(hongtaosan)){
                                score = ( -2 * base);
                            }else {
                                score = -base;
                            }
                        }

                    } else if (playerZhaGuZi.getSanJia() == PlayerZhaGuZi.GU_JIA) {
                        score = base;
                    }
                    playerZhaGuZi.setScore(score);
                }
            }

        }else {
            logger.info("平局 不计算输赢");
        }

        sendGameResult(ret);
    }

    public void sendGameResult(int winCode){
        List<PlayerZhaGuZiVo> list = new ArrayList<>();
        //算分
        for (PlayerZhaGuZi playerZhaGuZi : playerCardInfos.values()) {
            this.room.addUserSocre(playerZhaGuZi.getUserId(), playerZhaGuZi.getScore());
            list.add((PlayerZhaGuZiVo) playerZhaGuZi.toVo());
        }

        Map<String, Object> result = new HashMap<>();

        result.put("winCode", winCode);
        result.put("players", list);

        //发送结算结果
        MsgSender.sendMsg2Player(serviceName, "gameResult", result, users);

        this.pushScoreChange();

        this.genRecord();

        room.clearReadyStatus(true);

        this.sendFinalResult();
    }

    //重新洗牌算分
    public void computeShuffle() {

        status = ZhaGuZiConstant.COMPUTE;

        sendGameResult(1);

    }

    //认输了
    public void computeGiveUp(long uid) {

        status = ZhaGuZiConstant.COMPUTE;

        PlayerZhaGuZi playerSanJia = playerCardInfos.get(uid);

        double score = 0;

        for (PlayerZhaGuZi playerZhaGuZi : playerCardInfos.values()) {

            if (playerSanJia != playerZhaGuZi) {
                playerZhaGuZi.setScore(1d);
                score += 1d;
            }
        }

        playerSanJia.setScore(playerSanJia.getScore() - score);

        sendGameResult(2);
    }

    //生成战绩
    public void genRecord() {
        long id = IdWorker.getDefaultInstance().nextId();
        Map<Long, Double> map = new HashMap<>();
        for (Map.Entry<Long, PlayerZhaGuZi> entry : playerCardInfos.entrySet()) {
            PlayerZhaGuZi p = entry.getValue();
            map.put(p.userId, p.getScore() + 0.0);
        }
        genRecord(map, this.room, id);
    }

    public void sendFinalResult() {

        if (this.room.curGameNumber >  this.room.getGameNumber()) {
            //因为是两圈，并且要求换zhu
            List<UserOfResult> userOfResult = this.room.getUserOfResult();
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

    //能不能接风
    public boolean isCanJieFeng(PlayerZhaGuZi playerZhaGuZi) {

        if (this.lastOverPlayer == null) return false;

        List<Long> uidList = new ArrayList<>();
        long nextId = this.firstDiscardId;
        uidList.add(nextId);
        while (true){

            nextId = nextTurnId(nextId);
            if (nextId == this.firstDiscardId){
                break;
            }
            uidList.add(nextId);
        }

        //计算应该接风的人的
        long shuoldId = nextTurnId(this.lastOverPlayer.getUserId());
        while (true){
            PlayerZhaGuZi p = playerCardInfos.get(shuoldId);
            if (p.isOver()){
                shuoldId = nextTurnId(shuoldId);
            }else {
                break;
            }
        }

        if (playerZhaGuZi.getUserId() != shuoldId){
            return false;
        }

        if (uidList.get(0) - this.lastOverPlayer.getUserId() == 0){

            for (PlayerZhaGuZi playerZhaGuZi1 : playerCardInfos.values()){

                if (playerZhaGuZi1.isOver()){
                    continue;
                }
                if (playerZhaGuZi1 == lastOverPlayer) continue;

                if (playerZhaGuZi1.opList.size() != this.lastOverPlayer.opList.size()){
                    return false;
                }
            }

        }else if (uidList.get(uidList.size() - 1) - this.lastOverPlayer.getUserId() == 0){

            for (PlayerZhaGuZi playerZhaGuZi1 : playerCardInfos.values()){

                if (playerZhaGuZi1.isOver()){
                    continue;
                }
                if (playerZhaGuZi1 == lastOverPlayer) continue;

                if (playerZhaGuZi1.opList.size() != (this.lastOverPlayer.opList.size() + 1)){
                    return false;
                }
            }

        }else {

            long index = uidList.indexOf(this.lastOverPlayer.userId);

            for (int i = 0; i < uidList.size(); i++){

                long uid = uidList.get(i);
                PlayerZhaGuZi p = playerCardInfos.get(uid);

                if (p.isOver()){
                    continue;
                }
                if (p == lastOverPlayer) continue;

                if (i < index){

                    if (p.opList.size() != this.lastOverPlayer.opList.size() + 1){
                        return false;
                    }

                }else {

                    if (p.opList.size() != this.lastOverPlayer.opList.size()){
                        return false;
                    }
                }

            }

        }

        int count = 0;

        for (PlayerZhaGuZi playerZhaGuZi1 : playerCardInfos.values()){
            if (playerZhaGuZi1.isOver()){
                count++;
            }else {
                int op = playerZhaGuZi1.opList.get(playerZhaGuZi1.opList.size() - 1);
                if (op == Operator.PASS){
                    count++;
                }
            }
        }

        if (count != playerCardInfos.size()){
            return false;
        }

        return true;
    }

    //组装发牌的时候客户端需要的数据
    public HashMap<String, Object> assembleDiscardResult(PlayerZhaGuZi playerZhaGuZi){
        HashMap<String,Object> rs = new HashMap<>();
        rs.put("rank", playerZhaGuZi.getRank());
        rs.put("userId", playerZhaGuZi.getUserId());
        rs.put("canJieFeng", playerZhaGuZi.isCanJieFeng());
        rs.put("isWinner", playerZhaGuZi.getIsWinner());
        rs.put("score", playerZhaGuZi.getScore());
        return rs;
    }

    //上一个人
    public long lastTurn(long uid) {
        int index = this.users.indexOf(uid);
        int last = index - 1;
        if (last < 0) {
            last = this.users.size() - 1;
        }
        return this.users.get(last);
    }

    public int isGiveUp(long uid, boolean isGiveUp) {

        MsgSender.sendMsg2Player(serviceName, "isGiveUpResult", "0", users);
        MsgSender.sendMsg2Player(serviceName, "isGiveUp", "0", uid);

        if (isGiveUp) {
            computeGiveUp(uid);
        } else {
            continuePlay();
        }

        return 0;
    }

    public void initPlayer() {

        playerCardInfos.clear();
        for (Long uid : users) {
            PlayerZhaGuZi playerZhaGuZiz = new PlayerZhaGuZi();
            playerZhaGuZiz.userId = uid;
            playerZhaGuZiz.setRoomPersonNum(this.room.getPersonNumber());
            playerCardInfos.put(uid, playerZhaGuZiz);
        }
    }

    public void initCards() {

        for (int i = 0; i < 54; i++) {

            if (i > 45 && i < 50) {
                continue;
            }

            this.cards.add(i);
        }
        //洗牌
        Collections.shuffle(this.cards);
    }

}
