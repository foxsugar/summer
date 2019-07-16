package com.code.server.game.mahjong.logic;

import com.code.server.constant.response.ResponseVo;
import com.code.server.game.mahjong.response.*;
import com.code.server.game.room.kafka.MsgSender;

import java.util.*;

/**
 * Created by sunxianping on 2019-04-15.
 *
 * 血战到底 麻将
 */
public class GameInfoXZDD extends GameInfoNew {


    private int STATE_HUANPAI = 1;
    private int STATE_DINGQUE = 2;
    private int STATE_PLAY = 3;


    public static final int MODE_CHANGE_0 = 1;
    public static final int MODE_CHANGE_3 = 2;
    public static final int MODE_CHANGE_4 = 3;
    public static final int MODE_HAS_GANGKAI = 4;
    public static final int MODE_HAS_GANGPAO = 5;
    public static final int MODE_NO_WAN = 6;
    public static final int MODE_ZIMO_JIADI = 7;
    public static final int MODE_ZIMO_JIAFAN = 8;
    public static final int MODE_FAN_3 = 9;
    public static final int MODE_FAN_4 = 10;
    public static final int MODE_FAN_5 = 11;
    public static final int MODE_FAN_6 = 12;
    public static final int MODE_DINGQUE = 13;
    /**
     * 发牌
     */
    public void fapai() {
        //打乱顺序
        Collections.shuffle(remainCards);
        //没有风 字
        remainCards.removeAll(CardTypeUtil.FENG_CARD);
        remainCards.removeAll(CardTypeUtil.ZI_CARD);
        if (this.room.isHasMode(MODE_NO_WAN)) {
            remainCards.removeAll(CardTypeUtil.WAN_CARD);
        }
        for (int i = 0; i < playerSize; i++) {
            PlayerCardsInfoMj playerCardsInfo = PlayerCardsInfoFactory.getInstance(room);
            playerCardsInfo.setGameInfo(this);
            long userId = users.get(i);
            //设置id
            playerCardsInfo.setUserId(userId);
            List<String> playerCards = new ArrayList<>();
            //发牌
            for (int j = 0; j < cardSize; j++) {
                playerCards.add(remainCards.remove(0));
            }
            //初始化
            playerCardsInfo.init(playerCards);
            //放进map
            playerCardsInfos.put(userId, playerCardsInfo);

            //发牌状态通知
            HandCardsResp resp = new HandCardsResp();
            resp.setCards(playerCards);
            ResponseVo vo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_GET_HAND_CARDS, resp);

            MsgSender.sendMsg2Player(vo, userId);
        }



        doAfterFapai();
        //回放的牌信息
        for (PlayerCardsInfoMj playerCardsInfoMj : playerCardsInfos.values()) {
            List<String> cs = new ArrayList<>();
            cs.addAll(playerCardsInfoMj.getCards());
            replay.getCards().put(playerCardsInfoMj.getUserId(), cs);
        }


        //第一个人抓牌
      //  mopai(firstTurn, "发牌");

        if(isHasHuanpai()){
            this.state = STATE_HUANPAI;
            MsgSender.sendMsg2Player("gameService", "startHuanpai", 0, users);
        }else{
            this.state = STATE_DINGQUE;
            MsgSender.sendMsg2Player("gameService", "startDingque", 0, users);

            if (this.room.isHasMode(MODE_NO_WAN)) {
                this.playerCardsInfos.values().forEach(player->dingque(player.getUserId(),1));
//                this.state = STATE_PLAY;
//                mopai(firstTurn, "发牌");
            }

        }

    }

    private boolean isHasHuanpai(){
        return !this.room.isHasMode(MODE_CHANGE_0);
    }

    /**
     * 定缺
     * @param userId
     * @param groupType
     * @return
     */
    public int dingque(long userId, int groupType){
        PlayerCardsInfoMj playerCardsInfoMj = this.playerCardsInfos.get(userId);

        if (playerCardsInfoMj.dingqueGroupType != 0) {
            return ErrorCode.CAN_NOT_DINGQUE;
        }
        playerCardsInfoMj.dingqueGroupType = groupType;
        Map<String, Object> result = new HashMap<>();
        ResponseVo vo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, "dingqueResp", result);
        MsgSender.sendMsg2Player(vo, this.users);
        result.put("groupType", groupType);
        result.put("userId", userId);
        MsgSender.sendMsg2Player("gameService", "dingque", result, users);

        OperateReqResp operateReqResp = new OperateReqResp();
        operateReqResp.setUserId(userId);
        operateReqResp.setDingqueGroup(groupType);
        operateReqResp.setOperateType(OperateReqResp.type_dingque);
        replay.getOperate().add(operateReqResp);

        boolean isAllDingque = this.playerCardsInfos.values().stream().noneMatch(playerCardsInfoMj1 -> playerCardsInfoMj1.dingqueGroupType == 0);
        if (isAllDingque) {
            MsgSender.sendMsg2Player("gameService", "allDingque", 0, users);
            this.state = STATE_PLAY;
            //
            mopai(this.room.getBankerId());

        }
        return 0;
    }


    /**
     * 换牌
     * @param userId
     * @param cards
     * @return
     */
    public int huanpai(long userId, List<String> cards) {

        PlayerCardsInfoMj playerCardsInfoMj = this.playerCardsInfos.get(userId);
        if (playerCardsInfoMj.getChangeCards().size() != 0) {
            return ErrorCode.CAN_NOT_HUANPAI;
        }
        playerCardsInfoMj.getChangeCards().addAll(cards);
        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);

        MsgSender.sendMsg2Player("gameService", "huanpai", result, users);


        OperateReqResp operateReqResp = new OperateReqResp();
        operateReqResp.setUserId(userId);
        operateReqResp.setHuanpaiCards(cards);
        operateReqResp.setOperateType(OperateReqResp.type_huanpai);
        replay.getOperate().add(operateReqResp);

        boolean isAllChange = this.playerCardsInfos.values().stream().noneMatch(playerCardsInfoMj1 -> playerCardsInfoMj1.changeCards.size() == 0);
        if (isAllChange) {



            int changeType = 0;
            //开始换牌
            for (PlayerCardsInfoMj player : this.playerCardsInfos.values()) {

                OperateReqResp operateHuanpaiResp = new OperateReqResp();
                operateHuanpaiResp.setOperateType(OperateReqResp.type_huanpaiNew);

                List<String> cs = new ArrayList<>();
                //把牌给下一个人
                long nextUser = nextTurnId(player.getUserId());
                PlayerCardsInfoMj nextPlayer = this.playerCardsInfos.get(nextUser);
                //正转
                if (this.room.curGameNumber % 2 == 0) {
                    cs.addAll(nextPlayer.getChangeCards());
                    player.cards.addAll(cs);
                    player.cards.removeAll(player.changeCards);

                    operateHuanpaiResp.setUserId(player.userId);
                }else{//反转
                    changeType = 1;
                    cs.addAll(player.getChangeCards());
                    nextPlayer.cards.addAll(cs);
                    nextPlayer.cards.removeAll(nextPlayer.changeCards);

                    operateHuanpaiResp.setUserId(nextPlayer.userId);
                }
                operateHuanpaiResp.setHuanpaiNewCards(cs);
                operateHuanpaiResp.setChangeType(changeType);


                replay.getOperate().add(operateHuanpaiResp);
            }


            Map<String, Object> huanpaiResult = new HashMap<>();
            huanpaiResult.put("changeType", changeType);
            MsgSender.sendMsg2Player("gameService", "allHuanpai", huanpaiResult, users);

            this.playerCardsInfos.forEach((uid,playerInfo)->{
                Map<String, Object> newCards = new HashMap<>();
                newCards.put("userId", uid);
                newCards.put("newCards", playerInfo.getCards());
                MsgSender.sendMsg2Player("gameService", "changePushCards", newCards, uid);
            });
            if (this.room.isHasMode(MODE_NO_WAN)) {
                this.playerCardsInfos.values().forEach(player->dingque(player.getUserId(),1));
//                this.state = STATE_PLAY;
//                mopai(firstTurn, "发牌");
            }else{
                this.state = STATE_DINGQUE;
                MsgSender.sendMsg2Player("gameService", "startDingque", 0, users);
            }



        }
        return 0;
    }



    /**
     * 荒庄的处理
     *
     * @param userId
     */
    protected void handleHuangzhuang(long userId) {

        List<PlayerCardsInfoMj> winList = new ArrayList<>();
        List<PlayerCardsInfoMj> huazhuList = new ArrayList<>();
        List<PlayerCardsInfoMj> noHuazhuList = new ArrayList<>();
        List<PlayerCardsInfoMj> dajiaoList = new ArrayList<>();
        List<PlayerCardsInfoMj> noDajiaoList = new ArrayList<>();
        List<PlayerCardsInfoMj> loseGangList = new ArrayList<>();
        for (PlayerCardsInfoMj player : this.playerCardsInfos.values()) {
            if(!player.isAlreadyHu){
                if (player.chaHuazhu()) {
                    huazhuList.add(player);
                }else{
                    noHuazhuList.add(player);
                }

                if (player.chaDajiao()) {
                    dajiaoList.add(player);
                }else{
                    if (!player.chaHuazhu()) {
                        noDajiaoList.add(player);
                    }
                }
                if (!player.chaHuazhu() && player.chaDajiao()) {
                    winList.add(player);
                }
                if (player.chaHuazhu() || !player.chaDajiao()) {
                    loseGangList.add(player);
                }
            }
        }

        //把杠分退掉
        loseGangList.forEach(losePlayer->{
            losePlayer.getOtherGangScore().forEach((otherId,score)->{
                PlayerCardsInfoMj otherPlayer = this.playerCardsInfos.get(otherId);
                otherPlayer.addGangScore(score.intValue());
                otherPlayer.addScore(score);
                this.room.addUserSocre(otherId, score);

                losePlayer.addGangScore(-score.intValue());
                losePlayer.addScore(-score);
                this.room.addUserSocre(losePlayer.getUserId(), -score);
            });
        });



        //赔花猪
        int huazhuScore = 16;
        huazhuList.forEach(huazhuPlayer ->{
            noHuazhuList.forEach(noHuazhuPlayer->{

                noHuazhuPlayer.addScore(huazhuScore);
                this.room.addUserSocre(noHuazhuPlayer.getUserId(), huazhuScore);

                huazhuPlayer.addScore(-huazhuScore);
                this.room.addUserSocre(huazhuPlayer.getUserId(), -huazhuScore);

            });
        });

        //赔付
//        if (loseList.size() > 0 && winList.size() > 0) {



            //按最大牌型输分
            dajiaoList.forEach(winPlayer->{
                int score = winPlayer.getMaxTingScore();
                noDajiaoList.forEach(losePlayer->{
                    losePlayer.addScore(-score);
                    this.room.addUserSocre(losePlayer.getUserId(), -score);

                    winPlayer.addScore(score);
                    this.room.addUserSocre(winPlayer.getUserId(), score);
                });
            });
//        }

        sendResult(false, userId, null);
        noticeDissolutionResult();
        //通知所有玩家结束
        room.clearReadyStatus(true);
    }



    /**
     * 胡牌
     *
     * @param userId
     * @return
     */
    public int hu(long userId) {
        if (isAlreadyHu) {
            return ErrorCode.CAN_NOT_HU_ALREADY;
        }
        PlayerCardsInfoMj playerCardsInfo = playerCardsInfos.get(userId);
        if (playerCardsInfo == null) {
            return ErrorCode.USER_ERROR;
        }

        //回放
        OperateReqResp operateReqResp = new OperateReqResp();
        operateReqResp.setUserId(userId);
        operateReqResp.setOperateType(OperateReqResp.type_hu);

        if (lastOperateUserId == userId) {//自摸
            if (playerCardsInfo.isCanHu_zimo(catchCard)) {
                long nextUser = nextTurnId(userId);
                setBanker(userId);
                playerCardsInfo.hu_zm(room, this, catchCard);
                //回放
                replay.getOperate().add(operateReqResp);
                handleHu(playerCardsInfo,nextUser);

            } else {
                return ErrorCode.CAN_NOT_HU;
            }
        } else {

            if (this.yipaoduoxiangCard == null && this.disCard == null && jieGangHuCard == null) {
                return ErrorCode.CAN_NOT_HU;
            }
            String card = this.disCard;
            //
            if (jieGangHuCard != null) {
                card = jieGangHuCard;
            }
            if (this.yipaoduoxiangCard != null) {
                card = yipaoduoxiangCard;
            }

            if (playerCardsInfo.isCanHu_dianpao(card)) {

                handleWait(userId, WaitDetail.huPoint);
            } else {
                return ErrorCode.CAN_NOT_HU;
            }
        }


        return 0;

    }




    protected void doHu(PlayerCardsInfoMj playerCardsInfo, long userId) {
        OperateReqResp operateReqResp = new OperateReqResp();
        operateReqResp.setUserId(userId);
        operateReqResp.setOperateType(OperateReqResp.type_hu);

        //todo 谁坐庄
        setBanker(userId);


        if (jieGangHuCard != null) {
            //截杠胡
            playerCardsInfo.setJieGangHu(true);
            playerCardsInfo.hu_dianpao(room, this, beJieGangUser, jieGangHuCard);
            //回放
            operateReqResp.setFromUserId(beJieGangUser);
            operateReqResp.setCard(jieGangHuCard);

            PlayerCardsInfoMj playerCardsInfoBeJie = playerCardsInfos.get(beJieGangUser);
            //删除杠
            if (playerCardsInfoBeJie != null) {
                playerCardsInfoBeJie.cards.remove(jieGangHuCard);
                playerCardsInfoBeJie.removeGang2Peng(jieGangHuCard);
            }

            beJieGangUser = -1;
            jieGangHuCard = null;
        } else {
            //删除弃牌
            deleteDisCard(lastPlayUserId, disCard);
            if (nowYipaoduoxiang) {

                playerCardsInfo.hu_dianpao(room, this, lastPlayUserId, yipaoduoxiangCard);

                //回放
                operateReqResp.setFromUserId(lastOperateUserId);
                operateReqResp.setCard(yipaoduoxiangCard);

                this.disCard = null;


            }else{

                playerCardsInfo.hu_dianpao(room, this, lastPlayUserId, disCard);

                //回放
                operateReqResp.setFromUserId(lastOperateUserId);
                operateReqResp.setCard(disCard);

                this.disCard = null;
            }
        }

        long nextUser = nextTurnId(userId);
        //回放
        operateReqResp.setIsMing(true);
        replay.getOperate().add(operateReqResp);

        handleHu(playerCardsInfo,nextUser);
    }


    protected void handleHu(PlayerCardsInfoMj playerCardsInfo,long nextMopaiUser) {
//        isAlreadyHu = true;
        boolean onlyOneNoHu = getNoHuPlayerNum() == 1;
        if (this.remainCards.size() == 0 || onlyOneNoHu) {
            if (onlyOneNoHu) {

                sendResult(true, playerCardsInfo.getUserId(), null);
                noticeDissolutionResult();
                room.clearReadyStatus(true);
                return;
            }
           handleHuangzhuang(nextMopaiUser);
        }else{
            Map<String, Object> huInfo = new HashMap<>();
            List<Long> huList = new ArrayList<>();
            huList.add(playerCardsInfo.userId);
            huInfo.put("huList", huList);
            MsgSender.sendMsg2Player("gameService", "noticeHu", huInfo, users);
            this.room.pushScoreChange();
            //下个人摸牌
            if (nowYipaoduoxiang) {
                if (waitingforList.stream().noneMatch(waitDetail1 -> waitDetail1.isHu && !waitDetail1.isFire)) {
                    mopai(nextMopaiUser);
                }
            }else{

                mopai(nextMopaiUser);
            }
        }



    }

    /**
     * 出牌
     *
     * @param userId
     * @param card
     */
    public int chupai(long userId, String card) {
        if (isAlreadyHu) {
            return ErrorCode.CAN_PLAYCARD_IS_HU;
        }
        System.out.println("出牌的人 = " + userId);
        //出牌的玩家
        PlayerCardsInfoMj chupaiPlayerCardsInfo = playerCardsInfos.get(userId);
        if (this.turnId != userId) {
            return ErrorCode.CAN_NOT_PLAYCARD;
        }
        if (!chupaiPlayerCardsInfo.checkPlayCard(card)) {
            return ErrorCode.CAN_NOT_PLAYCARD;
        }

        //重置一炮多响状态
        this.nowYipaoduoxiang = false;
        this.yipaoduoxiangCard = null;

        this.lastPlayUserId = userId;//上个出牌的人
        lastOperateUserId = userId;//上个操作的人
        //出的牌
        this.disCard = card;
        chupaiPlayerCardsInfo.chupai(card);


        //通知其他玩家出牌信息
        PlayCardResp playCardResp = new PlayCardResp();
        playCardResp.setUserId(userId);
        playCardResp.setCard(this.disCard);
        playCardResp.setAuto(this.autoPlay);
        ResponseVo vo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_PLAY_CARD, playCardResp);
        MsgSender.sendMsg2Player(vo, users);
        this.autoPlay = false;

        //回放 出牌
        OperateReqResp operateReqResp = new OperateReqResp();
        operateReqResp.setCard(card);
        operateReqResp.setUserId(userId);
        operateReqResp.setOperateType(OperateReqResp.type_play);
        replay.getOperate().add(operateReqResp);


        //其他人能做的操作
        for (Map.Entry<Long, PlayerCardsInfoMj> entry : playerCardsInfos.entrySet()) {
            OperateResp operateResp = new OperateResp();

            //其他玩家的处理 碰杠等 如果有加入等待列表(要等待这些玩家"过")
            if (userId != entry.getKey()) {
                //通知其他玩家出了什么牌 自己能有什么操作
                PlayerCardsInfoMj playerCardsInfo = entry.getValue();
                boolean isCanGang = playerCardsInfo.isCanGangAddThisCard(card);
                boolean isCanPeng = playerCardsInfo.isCanPengAddThisCard(card);
                if (isHasGuoPeng()) {
                    isCanPeng = isCanPeng && !playerCardsInfo.isGuoPeng(card);
                }
                boolean isCanHu;
                if (isHasGuoHu() && playerCardsInfo.isGuoHu()) {
                    isCanHu = false;
                } else {
                    isCanHu = playerCardsInfo.isCanHu_dianpao(card);
                }
                boolean isNext = nextTurnId(chupaiPlayerCardsInfo.getUserId()) == playerCardsInfo.getUserId();
                boolean isCanChi = isNext && playerCardsInfo.isHasChi(card);
                boolean isCanChiTing = playerCardsInfo.isCanChiTing(card);
                boolean isCanPengTing = playerCardsInfo.isCanPengTing(card);
                //设置返回结果
                operateResp.setCanBeOperate(isCanChi, isCanPeng, isCanGang, false, isCanHu, isCanChiTing, isCanPengTing);

                //设置自己能做的操作
                playerCardsInfo.setCanBeOperate(isCanChi, isCanPeng, isCanGang, false, isCanHu, isCanChiTing, isCanPengTing);

                boolean isWait = isCanGang || isCanPeng || isCanHu || isCanChi || isCanChiTing || isCanPengTing;
                if (isWait) {
                    add2WaitingList(entry.getKey(), isCanHu, isCanGang, isCanPeng, isCanChi, isCanChiTing, isCanPengTing);
                }
            }

            //可能的操作
            ResponseVo OperateVo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_OPERATE, operateResp);
            MsgSender.sendMsg2Player(OperateVo, entry.getKey());
        }

        resetCanBeOperate(chupaiPlayerCardsInfo);


        System.out.println("======= waiting list : " + waitingforList.size());
        //如果等待列表为空 就轮到下个人摸牌
        if (this.waitingforList.size() == 0) {
            pushAllPass();
            long nextId = nextTurnId(turnId);
            mopai(nextId, "出牌");
        } else {
            compare(waitingforList);
            //todo 一炮多响
            if ( waitingforList.stream().filter(waitDetail -> waitDetail.isHu).count() >= 2) {
//                handleYiPaoDuoXiang();
                this.nowYipaoduoxiang = true;
                this.yipaoduoxiangCard = card;
            } else {
                //比较
//                compare(waitingforList);
            }
        }
        return 0;

    }


    /**
     * 胡牌的人数
     * @return
     */
    private int getHuPlayerNum() {
        return (int)playerCardsInfos.values().stream().filter(playerCardsInfoMj -> playerCardsInfoMj.isAlreadyHu).count();
    }

    private int getNoHuPlayerNum(){
        return (int) playerCardsInfos.values().stream().filter(player -> !player.isAlreadyHu).count();
    }


    protected void handleYiPaoDuoXiang() {

        List<Long> yipaoduoxiang = new ArrayList<>();

        //删除弃牌
        deleteDisCard(lastPlayUserId, disCard);
        this.waitingforList.forEach(waitDetail -> {
            if (waitDetail.isHu) {
                long uid = waitDetail.myUserId;
                yipaoduoxiang.add(uid);
                PlayerCardsInfoMj playerCardsInfoMj = playerCardsInfos.get(uid);
                playerCardsInfoMj.hu_dianpao(room, this, lastPlayUserId, disCard);
            }
        });

        //todo 下次的庄家  点炮的做庄
        if (getHuPlayerNum() == 0) {
            setBanker(lastPlayUserId);
        }

        long nextMopaiUser = nextTurnId(lastPlayUserId);
        //回放
        OperateReqResp operateReqResp = new OperateReqResp();
        operateReqResp.setYipaoduoxiangUser(yipaoduoxiang);
        operateReqResp.setOperateType(OperateReqResp.type_yipaoduoxiang);
        operateReqResp.setIsMing(true);
        replay.getOperate().add(operateReqResp);



        boolean onlyOneNoHu = getNoHuPlayerNum() == 1;
        if (this.remainCards.size() == 0 || onlyOneNoHu) {
            if (onlyOneNoHu) {

                isAlreadyHu = true;
                sendResult(true, -1L, yipaoduoxiang);
                noticeDissolutionResult();
                room.clearReadyStatus(true);
                return;
            }
            handleHuangzhuang(nextMopaiUser);
        }else{
            Map<String, Object> huInfo = new HashMap<>();
            huInfo.put("huList", yipaoduoxiang);
            MsgSender.sendMsg2Player("gameService", "noticeHu", huInfo, users);
            //摸牌
            mopai(nextMopaiUser);
        }


    }


    private WaitDetail getHuWait(long userId) {
        for (WaitDetail waitDetail : this.waitingforList) {
            if (waitDetail.myUserId == userId && waitDetail.isHu) {
                return waitDetail;
            }
        }
        return null;
    }



    /**
     * 过
     *
     * @param userId
     */
    public int guo(long userId) {
        if (isAlreadyHu) {
            return ErrorCode.CAN_NOT_GUO;
        }
        String disCard = getDisCard();
        if (nowYipaoduoxiang) {
            disCard = yipaoduoxiangCard;



        }else{


            //过胡逻辑
            if (playerCardsInfos.get(userId).isCanHu_dianpao(disCard)) {
                playerCardsInfos.get(userId).setGuoHu(true);
            }
            if (isHasGuoPeng() && playerCardsInfos.get(userId).isCanPengAddThisCard(disCard)) {
                playerCardsInfos.get(userId).addGuoPeng(disCard);
            }
            if (waitingforList.size() > 0) {

                List<WaitDetail> removeList = new ArrayList<>();
                for (WaitDetail waitDetail : waitingforList) {
                    if (waitDetail.myUserId == userId) {
                        removeList.add(waitDetail);
                    }
                }
                //todo 去掉可以操作集合
                resetCanBeOperate(playerCardsInfos.get(userId));
                waitingforList.removeAll(removeList);

                if (this.waitingforList.size() == 0) {
                    //截杠胡
                    if (jieGangHuCard != null) {
                        //截的 都是碰的明杠
                        doGang_hand(playerCardsInfos.get(beJieGangUser), -1, jieGangHuCard);
                        beJieGangUser = -1;
                        jieGangHuCard = null;
                    } else {
                        //告诉全部点过
                        pushAllPass();
                        long nextId = nextTurnId(turnId);
                        //下个人摸牌
                        mopai(nextId, "过后摸牌");
                    }
                } else {
                    WaitDetail waitDetail = this.waitingforList.get(0);
                    if (waitDetail.operate != -1) {
                        waitDetail.fire();
                        waitingforList.clear();
                    }
                }
            }
        }
        return 0;
    }



    protected void handleWait(long userId, int operateType, String... params) {
        List<WaitDetail> removeList = new ArrayList<>();
        for (WaitDetail waitDetail1 : waitingforList) {
            if (waitDetail1.myUserId == userId) {
                if (waitDetail1.getPoint() == operateType) {
                    waitDetail1.operate(this, operateType, params);
                } else {
                    removeList.add(waitDetail1);
                }
            }
        }
        waitingforList.removeAll(removeList);
        if (nowYipaoduoxiang) {
            if (waitingforList.size() > 0) {

                WaitDetail waitDetailFirst = waitingforList.get(0);
//                if (waitDetail != null && waitDetail.operate != -1 ) {
                //第一位不是胡
                if (waitDetailFirst != null && waitDetailFirst.operate != -1 &&!waitDetailFirst.isHu) {
                    waitDetailFirst.fire();
                    waitingforList.clear();
                }


                if(operateType == WaitDetail.huPoint){
                    WaitDetail waitDetail = getHuWait(userId);

                    if (waitDetail != null && !waitDetail.isFire) {
                        waitDetail.fire();
                        if (waitingforList.stream().noneMatch(waitDetail1 -> waitDetail1.isHu && !waitDetail1.isFire)) {
                            waitingforList.clear();
                        }
                    }
                }


//

//                    }
//                }

            }

        }else{

            if (waitingforList.size() > 0) {
                WaitDetail waitDetail = waitingforList.get(0);
                if (waitDetail.operate != -1) {
                    waitDetail.fire();
                    waitingforList.clear();
                }

            }
        }

    }



    /**
     * 下一个出牌人id
     *
     * @param curId
     * @return
     */
    public long nextTurnId(long curId) {
        List<Long> us = new ArrayList<>(users);
        for (PlayerCardsInfoMj playerCardsInfoMj : this.playerCardsInfos.values()) {
            if (playerCardsInfoMj.isAlreadyHu) {
                us.remove(playerCardsInfoMj.getUserId());
            }
        }
        int index = us.indexOf(curId);

        int nextId = index + 1;
        if (nextId >= us.size()) {
            nextId = 0;
        }
        return us.get(nextId);
    }


}
