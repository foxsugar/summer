package com.code.server.game.poker.xuanqiqi;

import com.code.server.constant.response.*;
import com.code.server.game.room.Game;
import com.code.server.game.room.Room;
import com.code.server.game.room.kafka.MsgSender;
import com.code.server.game.room.service.RoomManager;
import com.code.server.util.IdWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

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
    //扣       2
    //
    protected Map<Long, Integer> xuanOrGuo = new HashMap<>();


    //出过 1-3，未出 0
    protected Map<Long, Integer> ifChuPai = new HashMap<>();

    //赢为1，输为0，未比过为-1
    protected Map<Long, Integer> compareCard = new HashMap<>();

    //宣起记录
    protected List<XuanParam> xuanList = new ArrayList<>();


    protected RoomXuanQiQi room;

    public Map<Long, PlayerCardInfoXuanQiQi> playerCardInfos = new HashMap<>();

    public void init(List<Long> users) {
        //初始化玩家
        for (Long uid : users) {
            PlayerCardInfoXuanQiQi playerCardInfo = getGameTypePlayerCardInfo();
            playerCardInfo.userId = uid;
            playerCardInfo.canSetMultiple = "-1";
            playerCardInfo.canSendCard = "0";
            playerCardInfo.canXuan = "0";
            playerCardInfo.canKou = "0";
            playerCardInfo.canGuo = "0";
            playerCardInfos.put(uid, playerCardInfo);
            xuanOrGuo.put(uid, 0);
            ifChuPai.put(uid, 0);
            compareCard.put(uid, -1);
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

        while (true) {
            List<Integer> list1 = new ArrayList<>();
            List<Integer> list2 = new ArrayList<>();
            List<Integer> list3 = new ArrayList<>();
            for (int i = 0; i < 8; i++) {
                list1.add(cards.get(i));
            }
            for (int i = 8; i < 16; i++) {
                list2.add(cards.get(i));
            }
            for (int i = 16; i < 24; i++) {
                list3.add(cards.get(i));
            }
            if (dealAgain(list1) && dealAgain(list2) && dealAgain(list3)) {
                return;
            } else {
                Collections.shuffle(cards);
            }
        }
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
    protected void tellBanker() {
        Collections.shuffle(randamCards);
        Map<Long, Integer> randamCardOfPlayer = new HashMap<>();
        List<Long> tempList = new ArrayList<>();
        int temp = 0;
        for (PlayerCardInfoXuanQiQi playerCardInfo : playerCardInfos.values()) {
            randamCardOfPlayer.put(playerCardInfo.getUserId(), randamCards.get(temp));
            tempList.add(playerCardInfo.getUserId());
            temp++;
        }
        MsgSender.sendMsg2Player(new ResponseVo("gameService", "tellRandomCard", randamCardOfPlayer), users);


        //0和1比，0大
        if (UtilXuanQiQi.getOneCardWin(randamCardOfPlayer.get(tempList.get(0)), randamCardOfPlayer.get(tempList.get(1)))) {
            if (UtilXuanQiQi.getOneCardWin(randamCardOfPlayer.get(tempList.get(0)), randamCardOfPlayer.get(tempList.get(2)))) {
                room.setBankerId(tempList.get(0));
            } else {
                room.setBankerId(tempList.get(2));
            }
        } else {//0和1比,1大
            if (UtilXuanQiQi.getOneCardWin(randamCardOfPlayer.get(tempList.get(1)), randamCardOfPlayer.get(tempList.get(2)))) {
                room.setBankerId(tempList.get(1));
            } else {
                room.setBankerId(tempList.get(2));
            }
        }
        chuPaiId = room.getBankerId();
        operatId = room.getBankerId();
        playerCardInfos.get(room.getBankerId()).setCanSetMultiple("1");

        MsgSender.sendMsg2Player(new ResponseVo("gameService", "tellBankerId", room.getBankerId()), users);

        MsgSender.sendMsg2Player(new ResponseVo("gameService", "canDouble", room.getBankerId()), room.getBankerId());
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
    public int setMultiple(long userId, int multiple) {
        bankerMultiple = multiple;
        playerCardInfos.get(room.getBankerId()).setCanSetMultiple("0");
        playerCardInfos.get(userId).setCanKou("1");
        playerCardInfos.get(userId).setCanGuo("1");
        Map<String, Object> msg = new HashMap<>();
        msg.put("userId", room.getBankerId());
        msg.put("canKou", true);
        msg.put("canSendCard", true);
        MsgSender.sendMsg2Player(new ResponseVo("gameService", "canChoose", msg), users);

        Map<String, Object> multipleMsg = new HashMap<>();
        multipleMsg.put("bankerId", room.getBankerId());
        multipleMsg.put("multiple", multiple);
        MsgSender.sendMsg2Player(new ResponseVo("gameService", "multipleMsg", multipleMsg), users);
        MsgSender.sendMsg2Player("gameService", "setMultiple", 0, userId);
        return 0;
    }

    //过牌
    public int guo(long userId) {

        if ("1".equals(playerCardInfos.get(userId).canGuo) && chuPaiId == userId) {//庄过，直接通知出牌
            //xuanOrGuo.put(userId,1);
            playerCardInfos.get(userId).setCanGuo("0");
            playerCardInfos.get(userId).setCanKou("0");
            playerCardInfos.get(userId).setCanSendCard("1");
            operatId = chuPaiId;
            Map<String, Object> msg = new HashMap<>();
            msg.put("userId", operatId);
            msg.put("canSendCard", true);
            MsgSender.sendMsg2Player(new ResponseVo("gameService", "canChuPai", msg), users);
        } else {
            xuanOrGuo.put(userId, 2);
            boolean isAllKou = allKou();
            //判断是不是全扣了
            if (isAllKou) {
                if (playerCardInfos.get(room.getBankerId()).getHandCards().size() == 8) {
                    MsgSender.sendMsg2Player(new ResponseVo("gameService", "dealAgain", null), users);
                    room.clearReadyStatus(true);
                } else {
                    compute();
                    sendResult();
                    genRecord();
                    room.clearReadyStatus(true);
                    sendFinalResult();
                }
            } else {
                long nextUserId = nextTurnId(userId);
                playerCardInfos.get(nextUserId).setCanXuan("1");
                playerCardInfos.get(nextUserId).setCanGuo("1");
                playerCardInfos.get(userId).setCanXuan("0");
                playerCardInfos.get(userId).setCanGuo("0");
                operatId = nextUserId;
                Map<String, Object> msg = new HashMap<>();
                msg.put("userId", operatId);
                msg.put("canXuan", true);
                msg.put("canGuo", true);
                MsgSender.sendMsg2Player(new ResponseVo("gameService", "canChoose", msg), users);

                MsgSender.sendMsg2Player(new ResponseVo("gameService", "noXuan", userId), users);
            }

        }
        MsgSender.sendMsg2Player("gameService", "guo", 0, userId);
        return 0;
    }

    //宣牌
    public int xuan(long userId) {

        if ("1".equals(playerCardInfos.get(userId).getCanXuan()) && operatId == userId) {
            //XuanParam参数设置
            XuanParam xuanParam = new XuanParam();
            xuanParam.setXuan_UserId(userId);
            xuanParam.setXuaned_UserId(chuPaiId);
            xuanParam.setXuan_LuoNum(playerCardInfos.get(userId).winCards.size() / 3);
            xuanParam.setXuaned_LuoNum(playerCardInfos.get(chuPaiId).winCards.size() / 3);
            xuanParam.setGotLuo(false);
            xuanList.add(xuanParam);

            operatId = chuPaiId;
            playerCardInfos.get(userId).setCanXuan("0");
            playerCardInfos.get(operatId).setCanSendCard("1");

            Map<String, Object> msg = new HashMap<>();
            msg.put("userId", operatId);
            msg.put("canSendCard", true);
            msg.put("xuanParam", xuanParam);
            MsgSender.sendMsg2Player(new ResponseVo("gameService", "canChuPai", msg), users);
        }
        MsgSender.sendMsg2Player("gameService", "xuan", 0, userId);
        return 0;
    }

    //扣牌
    public int kou(long userId) {
        xuanOrGuo.put(userId, 2);

        playerCardInfos.get(userId).setCanKou("0");
        playerCardInfos.get(userId).setCanSendCard("0");
        playerCardInfos.get(userId).setCanChoose("0");

        if (playerCardInfos.get(userId).winCards.size() / 18 >= 1) {
            playerCardInfos.get(userId).setCatchSix(true);
        } else if (playerCardInfos.get(userId).winCards.size() / 15 >= 1) {
            playerCardInfos.get(userId).setCatchFive(true);
        } else if (playerCardInfos.get(userId).winCards.size() / 9 >= 1) {
            playerCardInfos.get(userId).setCatchThree(true);
        }

        MsgSender.sendMsg2Player(new ResponseVo("gameService", "tellKouUserId", userId), users);

        //判断下一个人
        if (chuPaiId == userId) {//出牌的就是他，通知下一个人是否宣
            playerCardInfos.get(userId).setSafeNum(playerCardInfos.get(userId).getWinCards().size() / 3);
            long canXuanUserId = nextTurnId(userId);
            operatId = canXuanUserId;
            playerCardInfos.get(canXuanUserId).setCanXuan("1");
            playerCardInfos.get(canXuanUserId).setCanGuo("1");
            Map<String, Object> msg = new HashMap<>();
            msg.put("userId", operatId);
            msg.put("canXuan", true);
            msg.put("canGuo", true);
            MsgSender.sendMsg2Player(new ResponseVo("gameService", "canChoose", msg), users);
        }

        MsgSender.sendMsg2Player("gameService", "kou", 0, userId);

        return 0;
    }

    //查看是否全扣
    private boolean allKou() {
        boolean b = true;
        a:
        for (long l : xuanOrGuo.keySet()) {
            if (xuanOrGuo.get(l) != 2) {
                b = false;
                break a;
            }
        }
        return b;
    }

    /**
     * 出牌
     *
     * @param userId
     * @param card1
     * @param card2
     * @param card3
     * @return
     */
    public int play(long userId, int cardNumber, Integer card1, Integer card2, Integer card3) {


        //TODO 牌型验证
        boolean checkCard = false;//和出的牌比
        boolean checkAllCard = false;//和所有牌比
        if (ifChuPai.values().contains(1) && !ifChuPai.values().contains(2) && !ifChuPai.values().contains(3)) {
            //第二个人出牌
            long temp = 0l;
            for (long l : ifChuPai.keySet()) {
                if (1 == ifChuPai.get(l)) {
                    temp = l;
                }
            }
            PlayerCardInfoXuanQiQi playerCardInfoXuanQiQi = new PlayerCardInfoXuanQiQi();
            playerCardInfoXuanQiQi.setUserId(userId);
            if (card1 != null) {
                playerCardInfoXuanQiQi.getPlayCards().add(card1);
            }
            if (card2 != null) {
                playerCardInfoXuanQiQi.getPlayCards().add(card2);
            }
            if (card3 != null) {
                playerCardInfoXuanQiQi.getPlayCards().add(card3);
            }
            checkCard = compareCardForCheck(playerCardInfos.get(temp).playCards.size(), playerCardInfos.get(temp), playerCardInfoXuanQiQi);
            if (checkCard) {//出的牌小，查找有没有大牌
                if (1 == cardNumber) {
                    a:
                    for (Integer i : playerCardInfos.get(userId).handCards) {
                        if (UtilXuanQiQi.getOneCardWin(i, playerCardInfos.get(temp).playCards.get(0))) {
                            if (UtilXuanQiQi.singleCard.get(i) != UtilXuanQiQi.singleCard.get(playerCardInfos.get(temp).playCards.get(0))) {
                                checkAllCard = true;
                                break a;
                            }
                        }
                    }
                } else if (2 == cardNumber) {
                    int tempNum = playerCardInfos.get(userId).handCards.size();
                    if (tempNum >= cardNumber) {
                        b:
                        for (int i = 0; i < tempNum - 1; i++) {
                            for (int j = i + 1; j < tempNum; j++) {
                                if (UtilXuanQiQi.getTwoCardWin(
                                        playerCardInfos.get(userId).handCards.get(i),
                                        playerCardInfos.get(userId).handCards.get(j),
                                        playerCardInfos.get(temp).playCards.get(0),
                                        playerCardInfos.get(temp).playCards.get(1))) {
                                    boolean b1 = UtilXuanQiQi.doubleWangList.contains(playerCardInfos.get(userId).handCards.get(i))
                                            && UtilXuanQiQi.doubleWangList.contains(playerCardInfos.get(userId).handCards.get(j))
                                            && UtilXuanQiQi.redTenlist.contains(playerCardInfos.get(temp).playCards.get(0))
                                            && UtilXuanQiQi.redTenlist.contains(playerCardInfos.get(temp).playCards.get(1));
                                    boolean b2 = UtilXuanQiQi.doubleWangList.contains(playerCardInfos.get(userId).handCards.get(0))
                                            && UtilXuanQiQi.doubleWangList.contains(playerCardInfos.get(userId).handCards.get(1))
                                            && UtilXuanQiQi.redTenlist.contains(playerCardInfos.get(temp).playCards.get(i))
                                            && UtilXuanQiQi.redTenlist.contains(playerCardInfos.get(temp).playCards.get(j));
                                    if (b1 || b2) {
                                        checkAllCard = false;
                                        break b;
                                    }
                                }
                            }
                        }
                    }
                } else if (3 == cardNumber) {
                    int tempNum = playerCardInfos.get(userId).handCards.size();
                    if (tempNum >= cardNumber) {
                        c:
                        for (int i = 0; i < tempNum - 2; i++) {
                            for (int j = i + 1; j < tempNum - 1; j++) {
                                for (int k = j + 1; k < tempNum; k++) {
                                    if (UtilXuanQiQi.getThreeCardWin(
                                            playerCardInfos.get(userId).handCards.get(i),
                                            playerCardInfos.get(userId).handCards.get(j),
                                            playerCardInfos.get(userId).handCards.get(k),
                                            playerCardInfos.get(temp).playCards.get(0),
                                            playerCardInfos.get(temp).playCards.get(1),
                                            playerCardInfos.get(temp).playCards.get(2))) {
                                        checkAllCard = true;
                                        break c;
                                    }
                                }
                            }
                        }
                    }
                }

            }
        } else if (ifChuPai.values().contains(2) && !ifChuPai.values().contains(3)) {
            //第三个人出牌
            long temp = 0l;
            for (long l : compareCard.keySet()) {
                if (1 == compareCard.get(l)) {
                    temp = l;
                }
            }
            PlayerCardInfoXuanQiQi playerCardInfoXuanQiQi = new PlayerCardInfoXuanQiQi();
            playerCardInfoXuanQiQi.setUserId(userId);
            if (card1 != null) {
                playerCardInfoXuanQiQi.getPlayCards().add(card1);
            }
            if (card2 != null) {
                playerCardInfoXuanQiQi.getPlayCards().add(card2);
            }
            if (card3 != null) {
                playerCardInfoXuanQiQi.getPlayCards().add(card3);
            }
            checkCard = compareCardForCheck(playerCardInfos.get(temp).playCards.size(), playerCardInfos.get(temp), playerCardInfoXuanQiQi);
            if (checkCard) {//出的牌小，查找有没有大牌
                if (1 == cardNumber) {
                    a:
                    for (Integer i : playerCardInfos.get(userId).handCards) {
                        if (UtilXuanQiQi.getOneCardWin(i, playerCardInfos.get(temp).playCards.get(0))) {
                            if (UtilXuanQiQi.singleCard.get(i) != UtilXuanQiQi.singleCard.get(playerCardInfos.get(temp).playCards.get(0))) {
                                checkAllCard = true;
                                break a;
                            }
                        }
                    }
                } else if (2 == cardNumber) {
                    int tempNum = playerCardInfos.get(userId).handCards.size();
                    if (tempNum >= cardNumber) {
                        b:
                        for (int i = 0; i < tempNum - 1; i++) {
                            for (int j = i + 1; j < tempNum; j++) {
                                if (UtilXuanQiQi.getTwoCardWin(
                                        playerCardInfos.get(userId).handCards.get(i),
                                        playerCardInfos.get(userId).handCards.get(j),
                                        playerCardInfos.get(temp).playCards.get(0),
                                        playerCardInfos.get(temp).playCards.get(1))) {
                                    boolean b1 = UtilXuanQiQi.doubleWangList.contains(playerCardInfos.get(userId).handCards.get(i))
                                            && UtilXuanQiQi.doubleWangList.contains(playerCardInfos.get(userId).handCards.get(j))
                                            && UtilXuanQiQi.redTenlist.contains(playerCardInfos.get(temp).playCards.get(0))
                                            && UtilXuanQiQi.redTenlist.contains(playerCardInfos.get(temp).playCards.get(1));
                                    boolean b2 = UtilXuanQiQi.doubleWangList.contains(playerCardInfos.get(userId).handCards.get(0))
                                            && UtilXuanQiQi.doubleWangList.contains(playerCardInfos.get(userId).handCards.get(1))
                                            && UtilXuanQiQi.redTenlist.contains(playerCardInfos.get(temp).playCards.get(i))
                                            && UtilXuanQiQi.redTenlist.contains(playerCardInfos.get(temp).playCards.get(j));
                                    if (b1 || b2) {
                                        checkAllCard = false;
                                        break b;
                                    }
                                }
                            }
                        }
                    }
                } else if (3 == cardNumber) {
                    int tempNum = playerCardInfos.get(userId).handCards.size();
                    if (tempNum >= cardNumber) {
                        c:
                        for (int i = 0; i < tempNum - 2; i++) {
                            for (int j = i + 1; j < tempNum - 1; j++) {
                                for (int k = j + 1; k < tempNum; k++) {
                                    if (UtilXuanQiQi.getThreeCardWin(
                                            playerCardInfos.get(userId).handCards.get(i),
                                            playerCardInfos.get(userId).handCards.get(j),
                                            playerCardInfos.get(userId).handCards.get(k),
                                            playerCardInfos.get(temp).playCards.get(0),
                                            playerCardInfos.get(temp).playCards.get(1),
                                            playerCardInfos.get(temp).playCards.get(2))) {
                                        checkAllCard = true;
                                        break c;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else if (!ifChuPai.values().contains(1) && !ifChuPai.values().contains(2) && !ifChuPai.values().contains(3)) {
            //第一个人出牌，不用判断
        }
        if (checkAllCard) {
            return ErrorCode.MUST_PLAY_MaxCard;
        }

        //存到player
        List<Integer> tempList = new ArrayList<>();
        if (cardNumber == 1) {
            tempList.add(0, card1);
            playerCardInfos.get(userId).handCards.remove(card1);
        } else if (cardNumber == 2) {
            tempList.add(0, card1);
            tempList.add(1, card2);
            playerCardInfos.get(userId).handCards.remove(card1);
            playerCardInfos.get(userId).handCards.remove(card2);
        } else if (cardNumber == 3) {
            tempList.add(0, card1);
            tempList.add(1, card2);
            tempList.add(2, card3);
            playerCardInfos.get(userId).handCards.remove(card1);
            playerCardInfos.get(userId).handCards.remove(card2);
            playerCardInfos.get(userId).handCards.remove(card3);
        }
        playerCardInfos.get(userId).setPlayCards(tempList);
        //手牌删除牌

        Map<Long, List<Integer>> chuPaiList = new HashMap<>();
        chuPaiList.put(userId, tempList);

        if (ifChuPai.values().contains(1) && !ifChuPai.values().contains(2) && !ifChuPai.values().contains(3)) {
            ifChuPai.put(userId, 2);
        } else if (ifChuPai.values().contains(2) && !ifChuPai.values().contains(3)) {
            ifChuPai.put(userId, 3);
        } else if (!ifChuPai.values().contains(1) && !ifChuPai.values().contains(2) && !ifChuPai.values().contains(3)) {
            ifChuPai.put(userId, 1);
        }

        int chuPaiCount = chuPaiIndex();
        if (chuPaiCount == 1) {//第一个出牌，通知下家出
            chuPaiId = nextTurnId(userId);
            operatId = nextTurnId(userId);
            playerCardInfos.get(userId).setCanSendCard("0");
            playerCardInfos.get(userId).setCanKou("0");
            playerCardInfos.get(userId).setCanGuo("0");
            playerCardInfos.get(chuPaiId).setCanSendCard("1");
            playerCardInfos.get(userId).setDisplay(true);
            Map<String, Object> msg = new HashMap<>();
            msg.put("userId", operatId);
            msg.put("canSendCard", true);
            msg.put("chuPaiList", chuPaiList);
            msg.put("display", true);
            MsgSender.sendMsg2Player(new ResponseVo("gameService", "canChuPai", msg), users);
        } else if (chuPaiCount == 2) {//第二个人出牌，比牌，通知下家出
            PlayerCardInfoXuanQiQi p1 = null;
            PlayerCardInfoXuanQiQi p2 = null;
            for (Long l : ifChuPai.keySet()) {
                if (ifChuPai.get(l) == 1) {
                    p1 = playerCardInfos.get(l);
                } else if (ifChuPai.get(l) == 2) {
                    p2 = playerCardInfos.get(l);
                }
            }
            compareCard(cardNumber, p1, p2);
            chuPaiId = nextTurnId(userId);
            operatId = nextTurnId(userId);
            playerCardInfos.get(userId).setCanSendCard("0");
            playerCardInfos.get(chuPaiId).setCanSendCard("1");
            Map<String, Object> msg = new HashMap<>();
            msg.put("userId", operatId);
            msg.put("canSendCard", true);
            msg.put("chuPaiList", chuPaiList);
            msg.put("display", compareCard.get(userId) == 1);
            playerCardInfos.get(userId).setDisplay(compareCard.get(userId) == 1);
            MsgSender.sendMsg2Player(new ResponseVo("gameService", "canChuPai", msg), users);
        } else if (chuPaiCount == 3) {//第三个人出牌，存记录，分罗
            PlayerCardInfoXuanQiQi p2 = null;
            long winnerId = 0l;
            for (long l : compareCard.keySet()) {//取前两个人比牌赢的
                if (compareCard.get(l) == 1) {
                    winnerId = l;
                }
            }
            PlayerCardInfoXuanQiQi p1 = playerCardInfos.get(winnerId);
            for (Long l : ifChuPai.keySet()) {
                if (ifChuPai.get(l) == 3) {
                    p2 = playerCardInfos.get(l);
                }
            }
            compareCard(cardNumber, p1, p2);

            long lastWinner = 0l;
            for (long l : compareCard.keySet()) {//取三个人最后赢的
                if (compareCard.get(l) == 1) {
                    lastWinner = l;
                }
            }

            operatId = lastWinner;
            chuPaiId = lastWinner;

            playerCardInfos.get(userId).setCanSendCard("0");
            if (playerCardInfos.get(userId).getHandCards().size() > 0) {
                playerCardInfos.get(lastWinner).setCanSendCard("1");
                playerCardInfos.get(lastWinner).setCanKou("1");
                Map<String, Object> msg = new HashMap<>();
                msg.put("userId", operatId);
                msg.put("canSendCard", true);
                msg.put("canKou", true);
                msg.put("chuPaiList", chuPaiList);
                msg.put("display", compareCard.get(userId) == 1);
                playerCardInfos.get(userId).setDisplay(compareCard.get(userId) == 1);
                MsgSender.sendMsg2Player(new ResponseVo("gameService", "canChuPai", msg), users);
                setWinCardAndCardType();//存记录分罗
                //清除所有状态,游戏继续
                cleanRecord();
            } else {
                setWinCardAndCardTypeNoCard();//存记录分罗

                if(playerCardInfos.get(lastWinner).winCards.size()==9 && !playerCardInfos.get(lastWinner).gotThree){
                    playerCardInfos.get(lastWinner).setCatchThree(true);
                }else if(playerCardInfos.get(lastWinner).winCards.size()==15  && !playerCardInfos.get(lastWinner).gotFive){
                    playerCardInfos.get(lastWinner).setCatchFive(true);
                }else if(playerCardInfos.get(lastWinner).winCards.size()>=18  && !playerCardInfos.get(lastWinner).gotSix){
                    playerCardInfos.get(lastWinner).setCatchSix(true);
                }else if(playerCardInfos.get(lastWinner).winCards.size()==12  && !playerCardInfos.get(lastWinner).gotThree && playerCardInfos.get(lastWinner).playCards.size()==2){
                    playerCardInfos.get(lastWinner).setCatchThree(true);
                }
                playerCardInfos.get(lastWinner).setCanSendCard("1");
                playerCardInfos.get(lastWinner).setCanKou("1");
                Map<String, Object> msg = new HashMap<>();
                msg.put("userId", operatId);
                msg.put("canSendCard", true);
                msg.put("canKou", true);
                msg.put("chuPaiList", chuPaiList);
                msg.put("display", compareCard.get(userId) == 1);
                playerCardInfos.get(userId).setDisplay(compareCard.get(userId) == 1);
                MsgSender.sendMsg2Player(new ResponseVo("gameService", "canChuPai", msg), users);

                long finalWinnerId = 0l;
                a:
                for (long l : compareCard.keySet()) {//取最后赢的人
                    if (compareCard.get(l) == 1) {
                        finalWinnerId = l;
                        break a;
                    }
                }

                Map<String, Object> result = new HashMap<>();
                result.put("finalWinnerId", finalWinnerId);
                result.put("winCards", playerCardInfos.get(finalWinnerId).winCards);
                result.put("cardsType", playerCardInfos.get(finalWinnerId).getCardsType());
                result.put("gotThree", playerCardInfos.get(finalWinnerId).winCards.size() / 9 >= 1);
                result.put("gotFive", playerCardInfos.get(finalWinnerId).winCards.size() / 15 >= 1);
                result.put("gotSix", playerCardInfos.get(finalWinnerId).winCards.size() / 18 >= 1);
                ResponseVo vo = new ResponseVo("gameService", "winResult", result);
                MsgSender.sendMsg2Player(vo, users);

                for (long l : playerCardInfos.keySet()) {
                    if (finalWinnerId == l) {
                        playerCardInfos.get(l).setCanChoose("1");
                        playerCardInfos.get(l).setCanKou("1");
                        playerCardInfos.get(l).setCanSendCard("1");
                        Map<String, Object> msgs = new HashMap<>();
                        msgs.put("userId", operatId);
                        msgs.put("canSendCard", true);
                        msgs.put("canKou", true);
                        MsgSender.sendMsg2Player(new ResponseVo("gameService", "canChoose", msgs), users);
                    } else {
                        playerCardInfos.get(l).setCanChoose("0");
                    }
                }

                //游戏结算
                compute();
                sendResult();
                genRecord();
                room.clearReadyStatus(true);
                sendFinalResult();
            }
        }

        MsgSender.sendMsg2Player("gameService", "play", 0, userId);

        return 0;
    }

    //查看是否全出过牌
    private boolean allPlayCard() {
        boolean b = true;
        a:
        for (long l : ifChuPai.keySet()) {
            if (ifChuPai.get(l) != 1) {
                b = false;
                break a;
            }
        }
        return b;
    }

    //查看第几个人出过牌
    private int chuPaiIndex() {
        int temp = 0;
        for (long l : ifChuPai.keySet()) {
            if (ifChuPai.get(l) > 0) {
                temp++;
            }
        }
        return temp;
    }

    //是不是所有牌都在一个人身上99 1010
    private boolean dealAgain(List<Integer> checkList) {
        boolean b = false;
        List<Integer> list = new ArrayList<>();
        for (int i = 33; i < 41; i++) {
            list.add(i);
        }
        for (Integer i : checkList) {
            if (list.contains(i)) {
                b = true;
            }
        }
        return b;
    }


    /**
     * 比牌
     *
     * @param cardNumber
     * @param p1
     * @param p2
     */
    private void compareCard(int cardNumber, PlayerCardInfoXuanQiQi p1, PlayerCardInfoXuanQiQi p2) {
        if (cardNumber == 1) {//比1张
            boolean p1Win = UtilXuanQiQi.getOneCardWin(p1.playCards.get(0), p2.playCards.get(0));
            if (p1Win) {
                compareCard.put(p1.getUserId(), 1);
                compareCard.put(p2.getUserId(), 0);
            } else {
                compareCard.put(p1.getUserId(), 0);
                compareCard.put(p2.getUserId(), 1);
            }
        } else if (cardNumber == 2) {
            boolean p1Win = UtilXuanQiQi.getTwoCardWin(p1.playCards.get(0), p1.playCards.get(1), p2.playCards.get(0), p2.playCards.get(1));
            if (p1Win) {
                compareCard.put(p1.getUserId(), 1);
                compareCard.put(p2.getUserId(), 0);
            } else {
                compareCard.put(p1.getUserId(), 0);
                compareCard.put(p2.getUserId(), 1);
            }
        } else if (cardNumber == 3) {
            boolean p1Win = UtilXuanQiQi.getThreeCardWin(p1.playCards.get(0), p1.playCards.get(1), p1.playCards.get(2), p2.playCards.get(0), p2.playCards.get(1), p2.playCards.get(2));
            if (p1Win) {
                compareCard.put(p1.getUserId(), 1);
                compareCard.put(p2.getUserId(), 0);
            } else {
                compareCard.put(p1.getUserId(), 0);
                compareCard.put(p2.getUserId(), 1);
            }
        }
    }

    /**
     * 比牌(判断必须出最大用的),比手牌
     *
     * @param cardNumber
     * @param p1
     * @param p2
     */
    private boolean compareCardForCheck(int cardNumber, PlayerCardInfoXuanQiQi p1, PlayerCardInfoXuanQiQi p2) {
        if (cardNumber == 1) {//比1张
            return UtilXuanQiQi.getOneCardWin(p1.playCards.get(0), p2.playCards.get(0));
        } else if (cardNumber == 2) {
            return UtilXuanQiQi.getTwoCardWin(p1.playCards.get(0), p1.playCards.get(1), p2.playCards.get(0), p2.playCards.get(1));
        } else if (cardNumber == 3) {
            return UtilXuanQiQi.getThreeCardWin(p1.playCards.get(0), p1.playCards.get(1), p1.playCards.get(2), p2.playCards.get(0), p2.playCards.get(1), p2.playCards.get(2));
        }
        return true;
    }

    /**
     * 存记录，分罗
     */
    private void setWinCardAndCardType() {
        long finalWinnerId = 0l;
        a:
        for (long l : compareCard.keySet()) {//取最后赢的人
            if (compareCard.get(l) == 1) {
                finalWinnerId = l;
                break a;
            }
        }
        List<Integer> tempCards = new ArrayList<>();
        tempCards.addAll(playerCardInfos.get(finalWinnerId).getWinCards());
        List<Long> list = new ArrayList<>();
        for (long l : ifChuPai.keySet()) {
            if (ifChuPai.get(l) == 1) {
                list.add(l);
            }
        }
        for (long l : ifChuPai.keySet()) {
            if (ifChuPai.get(l) == 2) {
                list.add(l);
            }
        }
        for (long l : ifChuPai.keySet()) {
            if (ifChuPai.get(l) == 3) {
                list.add(l);
            }
        }

        for (long l : list) {
            tempCards.addAll(playerCardInfos.get(l).getPlayCards());
        }
        playerCardInfos.get(finalWinnerId).setWinCards(tempCards);//设置赢的


        Map<Integer, Boolean> tempCardsType = new HashMap<>();//罗上牌明或扣的状态
        for (long l : ifChuPai.keySet()) {
            if (ifChuPai.get(l) == 1 && finalWinnerId == l) {//第一个出牌的赢
                for (long ll : playerCardInfos.keySet()) {
                    if (ll == finalWinnerId) {
                        for (Integer i : playerCardInfos.get(ll).playCards) {
                            tempCardsType.put(i, true);
                        }
                    } else {
                        for (Integer i : playerCardInfos.get(ll).playCards) {
                            tempCardsType.put(i, false);
                        }
                    }
                }
            } else if (ifChuPai.get(l) == 2 && finalWinnerId == l) {//第2个出牌的赢
                for (long ll : playerCardInfos.keySet()) {
                    if (ifChuPai.get(ll) == 3) {
                        for (Integer i : playerCardInfos.get(ll).playCards) {
                            tempCardsType.put(i, false);
                        }
                    } else {
                        for (Integer i : playerCardInfos.get(ll).playCards) {
                            tempCardsType.put(i, true);
                        }
                    }
                }
            } else if (ifChuPai.get(l) == 3 && finalWinnerId == l) {//第3个出牌的赢

                long one = 0l;
                long two = 0l;
                for (long ll : ifChuPai.keySet()) {
                    if (1 == ifChuPai.get(ll)) {
                        one = ll;
                    } else if (2 == ifChuPai.get(ll)) {
                        two = ll;
                    }
                }


                for (long ll : playerCardInfos.keySet()) {
                    if (!compareCardForCheck(playerCardInfos.get(one).playCards.size(), playerCardInfos.get(one), playerCardInfos.get(two))) {
                        for (Integer i : playerCardInfos.get(ll).playCards) {
                            tempCardsType.put(i, true);
                        }
                    } else {
                        if (ifChuPai.get(ll) == 2) {
                            for (Integer i : playerCardInfos.get(ll).playCards) {
                                tempCardsType.put(i, false);
                            }
                        } else {
                            for (Integer i : playerCardInfos.get(ll).playCards) {
                                tempCardsType.put(i, true);
                            }
                        }
                    }
                }
            }
        }
        playerCardInfos.get(finalWinnerId).getCardsType().putAll(tempCardsType);//设置罗的明/扣

        if (playerCardInfos.get(finalWinnerId).winCards.size() / 18 >= 1) {
            playerCardInfos.get(finalWinnerId).setGotSix(true);
        } else if (playerCardInfos.get(finalWinnerId).winCards.size() / 15 >= 1) {
            playerCardInfos.get(finalWinnerId).setGotFive(true);
        } else if (playerCardInfos.get(finalWinnerId).winCards.size() / 9 >= 1) {
            playerCardInfos.get(finalWinnerId).setGotThree(true);
        }

        chuPaiId = finalWinnerId;//设置下一个出牌的人
        operatId = finalWinnerId;

        Map<String, Object> result = new HashMap<>();
        result.put("finalWinnerId", finalWinnerId);
        result.put("winCards", playerCardInfos.get(finalWinnerId).winCards);
        result.put("cardsType", tempCardsType);
        result.put("gotThree", playerCardInfos.get(finalWinnerId).winCards.size() / 9 >= 1);
        result.put("gotFive", playerCardInfos.get(finalWinnerId).winCards.size() / 15 >= 1);
        result.put("gotSix", playerCardInfos.get(finalWinnerId).winCards.size() / 18 >= 1);
        ResponseVo vo = new ResponseVo("gameService", "winResult", result);
        MsgSender.sendMsg2Player(vo, users);

        for (long l : playerCardInfos.keySet()) {
            if (finalWinnerId == l) {
                playerCardInfos.get(l).setCanChoose("1");
                playerCardInfos.get(l).setCanKou("1");
                playerCardInfos.get(l).setCanSendCard("1");
                Map<String, Object> msg = new HashMap<>();
                msg.put("userId", operatId);
                msg.put("canSendCard", true);
                msg.put("canKou", true);
                MsgSender.sendMsg2Player(new ResponseVo("gameService", "canChoose", msg), users);
            } else {
                playerCardInfos.get(l).setCanChoose("0");
            }
        }
    }

    //最后一次出牌
    private void setWinCardAndCardTypeNoCard() {
        long finalWinnerId = 0l;
        a:
        for (long l : compareCard.keySet()) {//取最后赢的人
            if (compareCard.get(l) == 1) {
                finalWinnerId = l;
                break a;
            }
        }
        List<Integer> tempCards = new ArrayList<>();
        tempCards.addAll(playerCardInfos.get(finalWinnerId).getWinCards());
        List<Long> list = new ArrayList<>();
        for (long l : ifChuPai.keySet()) {
            if (ifChuPai.get(l) == 1) {
                list.add(l);
            }
        }
        for (long l : ifChuPai.keySet()) {
            if (ifChuPai.get(l) == 2) {
                list.add(l);
            }
        }
        for (long l : ifChuPai.keySet()) {
            if (ifChuPai.get(l) == 3) {
                list.add(l);
            }
        }

        for (long l : list) {
            tempCards.addAll(playerCardInfos.get(l).getPlayCards());
        }
        playerCardInfos.get(finalWinnerId).setWinCards(tempCards);//设置赢的


        Map<Integer, Boolean> tempCardsType = new HashMap<>();//罗上牌明或扣的状态
        for (long l : ifChuPai.keySet()) {
            if (ifChuPai.get(l) == 1 && finalWinnerId == l) {//第一个出牌的赢
                for (long ll : playerCardInfos.keySet()) {
                    if (ll == finalWinnerId) {
                        for (Integer i : playerCardInfos.get(ll).playCards) {
                            tempCardsType.put(i, true);
                        }
                    } else {
                        for (Integer i : playerCardInfos.get(ll).playCards) {
                            tempCardsType.put(i, false);
                        }
                    }
                }
            } else if (ifChuPai.get(l) == 2 && finalWinnerId == l) {//第2个出牌的赢
                for (long ll : playerCardInfos.keySet()) {
                    if (ifChuPai.get(ll) == 3) {
                        for (Integer i : playerCardInfos.get(ll).playCards) {
                            tempCardsType.put(i, false);
                        }
                    } else {
                        for (Integer i : playerCardInfos.get(ll).playCards) {
                            tempCardsType.put(i, true);
                        }
                    }
                }
            } else if (ifChuPai.get(l) == 3 && finalWinnerId == l) {//第3个出牌的赢

                long one = 0l;
                long two = 0l;
                for (long ll : ifChuPai.keySet()) {
                    if (1 == ifChuPai.get(ll)) {
                        one = l;
                    } else if (2 == ifChuPai.get(ll)) {
                        two = l;
                    }
                }


                for (long ll : playerCardInfos.keySet()) {
                    if (!compareCardForCheck(playerCardInfos.get(one).playCards.size(), playerCardInfos.get(one), playerCardInfos.get(two))) {
                        for (Integer i : playerCardInfos.get(ll).playCards) {
                            tempCardsType.put(i, true);
                        }
                    } else {
                        if (ifChuPai.get(ll) == 2) {
                            for (Integer i : playerCardInfos.get(ll).playCards) {
                                tempCardsType.put(i, false);
                            }
                        } else {
                            for (Integer i : playerCardInfos.get(ll).playCards) {
                                tempCardsType.put(i, true);
                            }
                        }
                    }
                }
            }
        }
        playerCardInfos.get(finalWinnerId).getCardsType().putAll(tempCardsType);//设置罗的明/扣

        /*if (playerCardInfos.get(finalWinnerId).winCards.size() / 18 >= 1) {
            playerCardInfos.get(finalWinnerId).setGotSix(true);
        } else if (playerCardInfos.get(finalWinnerId).winCards.size() / 15 >= 1) {
            playerCardInfos.get(finalWinnerId).setGotFive(true);
        } else if (playerCardInfos.get(finalWinnerId).winCards.size() / 9 >= 1) {
            playerCardInfos.get(finalWinnerId).setGotThree(true);
        }*/

        chuPaiId = finalWinnerId;//设置下一个出牌的人
        operatId = finalWinnerId;
    }

    private void sendMessage(){

    }

    /**
     * 清除状态
     */
    private void cleanRecord() {
        for (Long uid : users) {
            xuanOrGuo.put(uid, 0);
            ifChuPai.put(uid, 0);
            compareCard.put(uid, -1);
            playerCardInfos.get(uid).setPlayCards(null);
            if (uid != chuPaiId) {
                playerCardInfos.get(uid).setCanKou("0");
                playerCardInfos.get(uid).setCanGuo("0");
            }
        }
    }

    /**
     * 算分
     */
    protected void compute() {
        RoomXuanQiQi roomXuanQiQi = null;
        if (room instanceof RoomXuanQiQi) {
            roomXuanQiQi = (RoomXuanQiQi) room;
        }
        //设置每个人的罗数
        for (PlayerCardInfoXuanQiQi playerCardInfo : playerCardInfos.values()) {
            if (3 == playerCardInfo.getSafeNum() || 4 == playerCardInfo.getSafeNum()) {
                roomXuanQiQi.addNumThree(playerCardInfo.getUserId());
            } else if (5 == playerCardInfo.getSafeNum()) {
                roomXuanQiQi.addNumFive(playerCardInfo.getUserId());
            } else if (playerCardInfo.getSafeNum() > 5) {
                roomXuanQiQi.addNumSix(playerCardInfo.getUserId());
            }
        }

        int score0 = 0;
        int score1 = 0;
        int score2 = 0;

        boolean bao0 = false;
        boolean bao1 = false;
        boolean bao2 = false;


        PlayerCardInfoXuanQiQi p0 = playerCardInfos.get(users.get(0));
        PlayerCardInfoXuanQiQi p1 = playerCardInfos.get(users.get(1));
        PlayerCardInfoXuanQiQi p2 = playerCardInfos.get(users.get(2));

        a:
        for (XuanParam xuanParam : xuanList) {
            //bao0
            if (xuanParam.xuan_UserId == p0.userId && xuanParam.xuaned_UserId == p1.getUserId() && xuanParam.getXuaned_LuoNum() < 5 && p1.winCards.size() >= 15) {
                bao0 = true;
            }
            if (xuanParam.xuan_UserId == p0.userId && xuanParam.xuaned_UserId == p1.getUserId() && xuanParam.getXuaned_LuoNum() < 6 && p1.winCards.size() >= 18) {
                bao0 = true;
            }
            if (xuanParam.xuan_UserId == p0.userId && xuanParam.xuaned_UserId == p2.getUserId() && xuanParam.getXuaned_LuoNum() < 5 && p2.winCards.size() >= 15) {
                bao0 = true;
            }
            if (xuanParam.xuan_UserId == p0.userId && xuanParam.xuaned_UserId == p2.getUserId() && xuanParam.getXuaned_LuoNum() < 6 && p2.winCards.size() >= 18) {
                bao0 = true;
            }
            //bao1
            if (xuanParam.xuan_UserId == p1.userId && xuanParam.xuaned_UserId == p0.getUserId() && xuanParam.getXuaned_LuoNum() < 5 && p0.winCards.size() >= 15) {
                bao1 = true;
            }
            if (xuanParam.xuan_UserId == p1.userId && xuanParam.xuaned_UserId == p0.getUserId() && xuanParam.getXuaned_LuoNum() < 6 && p0.winCards.size() >= 18) {
                bao1 = true;
            }
            if (xuanParam.xuan_UserId == p1.userId && xuanParam.xuaned_UserId == p2.getUserId() && xuanParam.getXuaned_LuoNum() < 5 && p2.winCards.size() >= 15) {
                bao1 = true;
            }
            if (xuanParam.xuan_UserId == p1.userId && xuanParam.xuaned_UserId == p2.getUserId() && xuanParam.getXuaned_LuoNum() < 6 && p2.winCards.size() >= 18) {
                bao1 = true;
            }
            //bao2
            if (xuanParam.xuan_UserId == p2.userId && xuanParam.xuaned_UserId == p0.getUserId() && xuanParam.getXuaned_LuoNum() < 5 && p0.winCards.size() >= 15) {
                bao2 = true;
            }
            if (xuanParam.xuan_UserId == p2.userId && xuanParam.xuaned_UserId == p0.getUserId() && xuanParam.getXuaned_LuoNum() < 6 && p0.winCards.size() >= 18) {
                bao2 = true;
            }
            if (xuanParam.xuan_UserId == p2.userId && xuanParam.xuaned_UserId == p1.getUserId() && xuanParam.getXuaned_LuoNum() < 5 && p1.winCards.size() >= 15) {
                bao2 = true;
            }
            if (xuanParam.xuan_UserId == p2.userId && xuanParam.xuaned_UserId == p1.getUserId() && xuanParam.getXuaned_LuoNum() < 6 && p1.winCards.size() >= 18) {
                bao2 = true;
            }
        }

        //=======================score0分数计算=============================
        //第二个人不够
        if (p1.winCards.size() < 9) {
            if (p0.getUserId() == room.getBankerId()) {
                if (p0.catchSix) {
                    score0 += (3 * bankerMultiple);
                } else if (p0.catchFive) {
                    score0 += (2 * bankerMultiple);
                } else if (p0.catchThree) {
                    score0 += (1 * bankerMultiple);
                }
            } else {
                if (p0.catchSix) {
                    score0 += 3;
                } else if (p0.catchFive) {
                    score0 += 2;
                } else if (p0.catchThree) {
                    score0 += 1;
                }
            }
        }
        //第三个人不够
        if (p2.winCards.size() < 9) {
            if (p0.getUserId() == room.getBankerId()) {
                if (p0.catchSix) {
                    score0 += (3 * bankerMultiple);
                } else if (p0.catchFive) {
                    score0 += (2 * bankerMultiple);
                } else if (p0.catchThree) {
                    score0 += (1 * bankerMultiple);
                }
            } else {
                if (p0.catchSix) {
                    score0 += 3;
                } else if (p0.catchFive) {
                    score0 += 2;
                } else if (p0.catchThree) {
                    score0 += 1;
                }
            }
        }

        //第一个人不够，减分
        if (p0.winCards.size() < 9) {
            if (bao0) {
                if (p1.getUserId() == room.getBankerId()) {
                    if (p1.catchSix) {
                        score0 -= 12;
                    } else if (p1.catchFive) {
                        score0 -= 8;
                    } else if (p1.catchThree) {
                        score0 -= 4;
                    }
                } else {
                    if (p1.catchSix) {
                        score0 -= 9;
                    } else if (p1.catchFive) {
                        score0 -= 6;
                    } else if (p1.catchThree) {
                        score0 -= 3;
                    }
                }
                if (p2.getUserId() == room.getBankerId()) {
                    if (p2.catchSix) {
                        score0 -= 12;
                    } else if (p2.catchFive) {
                        score0 -= 8;
                    } else if (p2.catchThree) {
                        score0 -= 4;
                    }
                } else {
                    if (p2.catchSix) {
                        score0 -= 9;
                    } else if (p2.catchFive) {
                        score0 -= 6;
                    } else if (p2.catchThree) {
                        score0 -= 3;
                    }
                }
            } else {
                if (p1.getUserId() == room.getBankerId()) {
                    if (p1.catchSix) {
                        score0 -= 6;
                    } else if (p1.catchFive) {
                        score0 -= 4;
                    } else if (p1.catchThree) {
                        score0 -= 2;
                    }
                } else {
                    if(p0.getUserId() == room.getBankerId()){//算分的庄
                        if (p1.catchSix) {
                            score0 -= 6;
                        } else if (p1.catchFive) {
                            score0 -= 4;
                        } else if (p1.catchThree) {
                            score0 -= 2;
                        }
                    }else{
                        if (p1.catchSix) {
                            score0 -= 3;
                        } else if (p1.catchFive) {
                            score0 -= 2;
                        } else if (p1.catchThree) {
                            score0 -= 1;
                        }
                    }
                }
                if (p2.getUserId() == room.getBankerId()) {
                    if (p2.catchSix) {
                        score0 -= 6;
                    } else if (p2.catchFive) {
                        score0 -= 4;
                    } else if (p2.catchThree) {
                        score0 -= 2;
                    }
                } else {
                    if(p0.getUserId() == room.getBankerId()){//算分的庄
                        if (p2.catchSix) {
                            score0 -= 6;
                        } else if (p2.catchFive) {
                            score0 -= 4;
                        } else if (p2.catchThree) {
                            score0 -= 2;
                        }
                    }else{
                        if (p2.catchSix) {
                            score0 -= 3;
                        } else if (p2.catchFive) {
                            score0 -= 2;
                        } else if (p2.catchThree) {
                            score0 -= 1;
                        }
                    }
                }
            }
        }

        //=======================score1分数计算=============================
        //第一个人不够
        if (p0.winCards.size() < 9) {
            if (p1.getUserId() == room.getBankerId()) {
                if (p1.catchSix) {
                    score1 += (3 * bankerMultiple);
                } else if (p1.catchFive) {
                    score1 += (2 * bankerMultiple);
                } else if (p1.catchThree) {
                    score1 += (1 * bankerMultiple);
                }
            } else {
                if (p1.catchSix) {
                    score1 += 3;
                } else if (p1.catchFive) {
                    score1 += 2;
                } else if (p1.catchThree) {
                    score1 += 1;
                }
            }
        }
        //第三个人不够
        if (p2.winCards.size() < 9) {
            if (p1.getUserId() == room.getBankerId()) {
                if (p1.catchSix) {
                    score1 += (3 * bankerMultiple);
                } else if (p1.catchFive) {
                    score1 += (2 * bankerMultiple);
                } else if (p1.catchThree) {
                    score1 += (1 * bankerMultiple);
                }
            } else {
                if (p1.catchSix) {
                    score1 += 3;
                } else if (p1.catchFive) {
                    score1 += 2;
                } else if (p1.catchThree) {
                    score1 += 1;
                }
            }
        }

        //第二个人不够罗，减分
        if (p1.winCards.size() < 9) {
            if (bao0) {
                if (p0.getUserId() == room.getBankerId()) {
                    if (p0.catchSix) {
                        score1 -= 12;
                    } else if (p0.catchFive) {
                        score1 -= 8;
                    } else if (p0.catchThree) {
                        score1 -= 4;
                    }
                } else {
                    if (p0.catchSix) {
                        score1 -= 9;
                    } else if (p0.catchFive) {
                        score1 -= 6;
                    } else if (p0.catchThree) {
                        score1 -= 3;
                    }
                }
                if (p2.getUserId() == room.getBankerId()) {
                    if (p2.catchSix) {
                        score1 -= 12;
                    } else if (p2.catchFive) {
                        score1 -= 8;
                    } else if (p2.catchThree) {
                        score1 -= 4;
                    }
                } else {
                    if (p2.catchSix) {
                        score1 -= 9;
                    } else if (p2.catchFive) {
                        score1 -= 6;
                    } else if (p2.catchThree) {
                        score1 -= 3;
                    }
                }
            } else {
                if (p0.getUserId() == room.getBankerId()) {
                    if (p0.catchSix) {
                        score1 -= 6;
                    } else if (p0.catchFive) {
                        score1 -= 4;
                    } else if (p0.catchThree) {
                        score1 -= 2;
                    }
                } else {
                    if(p1.getUserId() == room.getBankerId()){//算分的庄
                        if (p0.catchSix) {
                            score1 -= 6;
                        } else if (p0.catchFive) {
                            score1 -= 4;
                        } else if (p0.catchThree) {
                            score1 -= 2;
                        }
                    }else{
                        if (p0.catchSix) {
                            score1 -= 3;
                        } else if (p0.catchFive) {
                            score1 -= 2;
                        } else if (p0.catchThree) {
                            score1 -= 1;
                        }
                    }
                }
                if (p2.getUserId() == room.getBankerId()) {
                    if (p2.catchSix) {
                        score1 -= 6;
                    } else if (p2.catchFive) {
                        score1 -= 4;
                    } else if (p2.catchThree) {
                        score1 -= 2;
                    }
                } else {
                    if(p1.getUserId() == room.getBankerId()){//算分的庄
                        if (p2.catchSix) {
                            score1 -= 6;
                        } else if (p2.catchFive) {
                            score1 -= 4;
                        } else if (p2.catchThree) {
                            score1 -= 2;
                        }
                    }else{
                        if (p2.catchSix) {
                            score1 -= 3;
                        } else if (p2.catchFive) {
                            score1 -= 2;
                        } else if (p2.catchThree) {
                            score1 -= 1;
                        }
                    }
                }
            }
        }

        score2 = -(score0 + score1);

        roomXuanQiQi.addUserSocre(playerCardInfos.get(p0.getUserId()).getUserId(), score0);
        roomXuanQiQi.addUserSocre(playerCardInfos.get(p1.getUserId()).getUserId(), score1);
        roomXuanQiQi.addUserSocre(playerCardInfos.get(p2.getUserId()).getUserId(), score2);
        playerCardInfos.get(p0.getUserId()).addScore(score0);
        playerCardInfos.get(p1.getUserId()).addScore(score1);
        playerCardInfos.get(p2.getUserId()).addScore(score2);

        //算分：宣起
        for (XuanParam x : xuanList) {
            if (!x.isGotLuo()) {//宣之后未达到，扣分
                roomXuanQiQi.addUserSocre(playerCardInfos.get(x.getXuan_UserId()).getUserId(), -2);
                roomXuanQiQi.addUserSocre(x.xuaned_UserId, 2);
                playerCardInfos.get(x.getXuan_UserId()).addScore(-2);
                playerCardInfos.get(x.xuaned_UserId).addScore(2);
            }
        }
        for (Long l:playerCardInfos.keySet()) {
            if(playerCardInfos.get(l).score>0){
                roomXuanQiQi.addWinNumXQQ(l);
            }
        }

    }




    /**
     * 发送结算版
     */
    protected void sendResult() {
        //计算下一轮庄
        Map<Long, Double> userScores = new HashMap<>();
        for (PlayerCardInfoXuanQiQi p:playerCardInfos.values()) {
            userScores.put(p.getUserId(),p.getScore());
        }
        List<Long> userList = new ArrayList<>();
        for (Long l :userScores.keySet()){
            userList.add(l);
        }
        long bankerId = 0l;
        long u0 = userList.get(0);
        long u1 = userList.get(1);
        long u2 = userList.get(2);
        if(userScores.get(u0)==0&&userScores.get(u1)==0&&userScores.get(u2)==0){
            bankerId = room.getBankerId();
        }else{
            if(userScores.get(u0)*userScores.get(u1)>0){
                bankerId = u2;
            }else if(userScores.get(u0)*userScores.get(u2)>0){
                bankerId = u1;
            }else if(userScores.get(u1)*userScores.get(u2)>0){
                bankerId = u0;
            }
        }

        Map<String, Object> gameResult = new HashMap<>();
        gameResult.put("bankerId",bankerId);
        gameResult.put("userScores",userScores);
        MsgSender.sendMsg2Player("gameService", "gameResult",gameResult, users);

        room.setBankerId(bankerId);
    }

    /**
     * 战绩
     */
    protected void genRecord() {
        long id = IdWorker.getDefaultInstance().nextId();
        genRecord(playerCardInfos.values().stream().collect
                (Collectors.toMap(PlayerCardInfoXuanQiQi::getUserId, PlayerCardInfoXuanQiQi::getScore)), room, id);
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


    @Override
    public IfaceGameVo toVo(long userId) {
        GameXuanQiQiVO vo = new GameXuanQiQiVO();
        vo.bankerMultiple = this.bankerMultiple;
        vo.chuPaiId = this.chuPaiId;
        vo.operatId = this.operatId;
        vo.cards = this.cards;
        vo.randamCards = this.randamCards;
        vo.xuanOrGuo = this.xuanOrGuo;
        vo.ifChuPai = this.ifChuPai;
        vo.compareCard = this.compareCard;
        vo.xuanList = this.xuanList;
        vo.bankerId = room.getBankerId();

        for (PlayerCardInfoXuanQiQi playerCardInfo : this.getPlayerCardInfos().values()) {
            if(userId == playerCardInfo.getUserId()){
                vo.playerCardInfos.put(playerCardInfo.userId, playerCardInfo.toVo(userId));
            }else{
                vo.playerCardInfos.put(playerCardInfo.userId, playerCardInfo.toVo());
            }
        }

        return vo;
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

    public List<XuanParam> getXuanList() {
        return xuanList;
    }

    public void setXuanList(List<XuanParam> xuanList) {
        this.xuanList = xuanList;
    }
}
