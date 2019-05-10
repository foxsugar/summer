package com.code.server.game.mahjong.logic;

import com.code.server.game.mahjong.response.OperateReqResp;

/**
 * Created by sunxianping on 2019-02-20.
 */
public class GameInfoZhanglebao extends GameInfoHeleKD {

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
