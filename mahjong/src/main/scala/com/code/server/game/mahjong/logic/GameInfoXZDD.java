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
    /**
     * 发牌
     */
    public void fapai() {
        //打乱顺序
        Collections.shuffle(remainCards);
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
        }else{

            this.state = STATE_DINGQUE;
        }
        //是否有换牌

    }

    private boolean isHasHuanpai(){
        return true;
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

            MsgSender.sendMsg2Player("gameService", "allHuanpai", 0, users);

            //开始换牌
            for (PlayerCardsInfoMj player : this.playerCardsInfos.values()) {
                List<String> cs = new ArrayList<>(player.changeCards);
                //把牌给下一个人
                long nextUser = nextTurnId(player.getUserId());
                PlayerCardsInfoMj nextPlayer = this.playerCardsInfos.get(nextUser);
                nextPlayer.cards.addAll(cs);
                player.cards.removeAll(player.changeCards);
                Map<String, Object> newCards = new HashMap<>();
                newCards.put("userId", nextUser);
                newCards.put("newCards", nextPlayer.getCards());
                MsgSender.sendMsg2Player("gameService", "changePushCards", newCards, nextUser);
            }

            this.state = STATE_DINGQUE;

            mopai(firstTurn, "发牌");
        }
        return 0;
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
            sendResult(true, playerCardsInfo.getUserId(), null);
            noticeDissolutionResult();
            room.clearReadyStatus(true);
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
        setBanker(yipaoduoxiang.get(0));

        //回放
        OperateReqResp operateReqResp = new OperateReqResp();
        operateReqResp.setYipaoduoxiangUser(yipaoduoxiang);
        operateReqResp.setOperateType(OperateReqResp.type_yipaoduoxiang);
        operateReqResp.setIsMing(true);
        replay.getOperate().add(operateReqResp);



        boolean onlyOneNoHu = getHuPlayerNum() == this.users.size() - 1;
        if (this.remainCards.size() == 0 || onlyOneNoHu) {
            isAlreadyHu = true;
            sendResult(true, -1L, yipaoduoxiang);
            noticeDissolutionResult();
            room.clearReadyStatus(true);
        }else{
            //摸牌

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
