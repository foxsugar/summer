package com.code.server.game.mahjong.logic;

import com.code.server.constant.response.ResponseVo;
import com.code.server.game.mahjong.response.HandCardsResp;
import com.code.server.game.mahjong.response.OperateReqResp;
import com.code.server.game.mahjong.response.ResponseType;
import com.code.server.game.mahjong.util.HuWithHun;
import com.code.server.game.room.kafka.MsgSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static com.code.server.game.mahjong.logic.GameInfoZhuohaozi.*;

/**
 * Created by sunxianping on 2019-02-20.
 */
public class GameInfoZhanglebao extends GameInfoHeleKD {


    @Override
    public void initHun() {

    }

    /**
     * 发牌
     */
    public void fapai() {
        //打乱顺序
        Collections.shuffle(remainCards);
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
        mopai(firstTurn, "发牌");


        //确定耗子

        if (!room.isHasMode(mode_不带耗子)) {

            //随机混
            Random rand = new Random();
            int hunIndex = 0;
            if (PlayerCardsInfoMj.isHasMode(this.room.mode, mode_风耗子)) {
                hunIndex = 27 + rand.nextInt(7);
            }else{
                String card = this.remainCards.remove(0);
                hunIndex = CardTypeUtil.getTypeByCard(card);
            }

            if (PlayerCardsInfoMj.isHasMode(this.room.mode, mode_双耗子)) {
                this.hun = HuWithHun.getHunType(hunIndex);
            }else{
                this.hun.add(hunIndex);
            }
//            else {
//                String card = this.remainCards.remove(0);
//                hunIndex = CardTypeUtil.getTypeByCard(card);
//                this.hun.add(hunIndex);
//            }

            //通知混
            MsgSender.sendMsg2Player("gameService", "noticeHun", this.hun, users);

            replay.getHun().addAll(this.hun);
        }
    }

    /**
     * 荒庄的处理
     *
     * @param userId
     */
    protected void handleHuangzhuang(long userId) {
        if (this.isTurnZeroAfterHuangZhuang()) {
            turnResultToZeroOnHuangZhuang();
        }else{
            computeAllGang();
        }
        sendResult(false, userId, null);
        noticeDissolutionResult();
        //通知所有玩家结束
        room.clearReadyStatus(true);

        //庄家换下个人
//        if (room instanceof RoomInfo) {
//            RoomInfo roomInfo = (RoomInfo) room;
//            if (roomInfo.isChangeBankerAfterHuangZhuang()) {
//                room.setBankerId(nextTurnId(room.getBankerId()));
//            }
//
//        }
    }


    public int getNeedRemainCardNum(){
        if (!this.room.isHasMode(GameInfoZhuohaozi.mode_留牌)) {
            return 0;
        }
        int gangCount = 0;
        for (PlayerCardsInfoMj playerCardsInfoMj : this.playerCardsInfos.values()) {
            gangCount += playerCardsInfoMj.getGangNum();
        }
        int add =  (gangCount % 2 == 0) ? 0:1;

        return 16 + add;
    }

    /**
     * 是否荒庄
     *
     * @param playerCardsInfo
     * @return
     */
    protected boolean isHuangzhuang(PlayerCardsInfoMj playerCardsInfo) {
        return this.remainCards.size() <= getNeedRemainCardNum();
    }


    protected void doHu(PlayerCardsInfoMj playerCardsInfo, long userId) {
        OperateReqResp operateReqResp = new OperateReqResp();
        operateReqResp.setUserId(userId);
        operateReqResp.setOperateType(OperateReqResp.type_hu);

        if (room.isHasMode(GameInfoZhuohaozi.mode_显庄)) {
            setBanker(nextTurnId(this.room.getBankerId()));
        }else{
            setBanker(userId);
        }

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

        handleHu(playerCardsInfo);
    }


}
