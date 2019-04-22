package com.code.server.game.mahjong.logic;

import com.code.server.constant.response.ResponseVo;
import com.code.server.game.mahjong.response.ErrorCode;
import com.code.server.game.mahjong.response.HandCardsResp;
import com.code.server.game.mahjong.response.OperateReqResp;
import com.code.server.game.mahjong.response.ResponseType;
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
    /**
     * 发牌
     */
    public void fapai() {
        //打乱顺序
        Collections.shuffle(remainCards);
        //没有风 字
        remainCards.removeAll(CardTypeUtil.FENG_CARD);
        remainCards.removeAll(CardTypeUtil.ZI_CARD);
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
        playerCardsInfoMj.dingqueGroupType = groupType;

        if (playerCardsInfoMj.dingqueGroupType != 0) {
            return ErrorCode.CAN_NOT_DINGQUE;
        }
        Map<String, Object> result = new HashMap<>();
        ResponseVo vo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, "dingqueResp", result);

        MsgSender.sendMsg2Player(vo, this.users);


        result.put("groupType", groupType);
        result.put("userId", userId);
        MsgSender.sendMsg2Player("gameService", "dingque", result, users);

        boolean isAllDingque = this.playerCardsInfos.values().stream().noneMatch(playerCardsInfoMj1 -> playerCardsInfoMj.dingqueGroupType == 0);
        if (isAllDingque) {
            MsgSender.sendMsg2Player("gameService", "allDingque", 0, users);
            this.state = STATE_PLAY;
            //

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

        boolean isAllChange = this.playerCardsInfos.values().stream().noneMatch(playerCardsInfoMj1 -> playerCardsInfoMj1.changeCards.size() == 0);
        if (isAllChange) {



            int changeType = 0;
            //开始换牌
            for (PlayerCardsInfoMj player : this.playerCardsInfos.values()) {
                List<String> cs = new ArrayList<>();
                //把牌给下一个人
                long nextUser = nextTurnId(player.getUserId());
                PlayerCardsInfoMj nextPlayer = this.playerCardsInfos.get(nextUser);
                //正转
                if (this.room.curGameNumber % 2 == 0) {
                    cs.addAll(nextPlayer.getChangeCards());
                    player.cards.addAll(cs);
                    nextPlayer.cards.removeAll(nextPlayer.changeCards);
                }else{//反转
                    changeType = 1;
                    cs.addAll(player.getChangeCards());
                    nextPlayer.cards.addAll(cs);
                    player.cards.removeAll(player.changeCards);
                }
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
                this.state = STATE_PLAY;
                mopai(firstTurn, "发牌");
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
        List<PlayerCardsInfoMj> loseList = new ArrayList<>();
        for (PlayerCardsInfoMj player : this.playerCardsInfos.values()) {
            if(!player.isAlreadyHu){
                if (!player.chaHuazhu() && player.chaDajiao()) {
                    winList.add(player);
                }
                if (player.chaHuazhu() || !player.chaDajiao()) {
                    loseList.add(player);
                }
            }
        }

        //把杠分退掉
        loseList.forEach(losePlayer->{
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

        //赔付
        if (loseList.size() > 0 && winList.size() > 0) {



            //按最大牌型输分
            winList.forEach(winPlayer->{
                int score = winPlayer.getMaxTingScore();
                loseList.forEach(losePlayer->{
                    losePlayer.addScore(-score);
                    this.room.addUserSocre(losePlayer.getUserId(), -score);

                    winPlayer.addScore(score);
                    this.room.addUserSocre(winPlayer.getUserId(), score);
                });
            });
        }

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
                long nextUser = 0;
                setBanker(userId);
                playerCardsInfo.hu_zm(room, this, catchCard);
                //回放
                replay.getOperate().add(operateReqResp);
                handleHu(playerCardsInfo,nextUser);

            } else {
                return ErrorCode.CAN_NOT_HU;
            }
        } else {

            if (this.disCard == null && jieGangHuCard == null) {
                return ErrorCode.CAN_NOT_HU;
            }
            String card = this.disCard;
            //
            if (jieGangHuCard != null) {
                card = jieGangHuCard;
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

        long nextUser = nextTurnId(userId);

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
            playerCardsInfo.hu_dianpao(room, this, lastPlayUserId, disCard);

            //回放
            operateReqResp.setFromUserId(lastOperateUserId);
            operateReqResp.setCard(disCard);

            this.disCard = null;
        }

        //回放
        operateReqResp.setIsMing(true);
        replay.getOperate().add(operateReqResp);

        handleHu(playerCardsInfo,nextUser);
    }


    protected void handleHu(PlayerCardsInfoMj playerCardsInfo,long nextMopaiUser) {
//        isAlreadyHu = true;
        boolean onlyOneNoHu = getHuPlayerNum() == this.users.size() - 1;
        if (this.remainCards.size() == 0 || onlyOneNoHu) {
            if (onlyOneNoHu) {

                sendResult(true, playerCardsInfo.getUserId(), null);
                noticeDissolutionResult();
                room.clearReadyStatus(true);
                return;
            }
           handleHuangzhuang(nextMopaiUser);
        }else{
            //下个人摸牌
            mopai(nextMopaiUser);
        }



    }




    /**
     * 胡牌的人数
     * @return
     */
    private int getHuPlayerNum() {
        return (int)playerCardsInfos.values().stream().filter(playerCardsInfoMj -> !playerCardsInfoMj.isAlreadyHu).count();
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



        boolean onlyOneNoHu = getHuPlayerNum() == this.users.size() - 1;
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
            //摸牌
            mopai(nextMopaiUser);
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
