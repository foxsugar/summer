package com.code.server.game.mahjong.logic;

import com.code.server.constant.response.ResponseVo;
import com.code.server.game.mahjong.response.ErrorCode;
import com.code.server.game.mahjong.response.OperateReqResp;
import com.code.server.game.mahjong.response.OperateResp;
import com.code.server.game.mahjong.response.ResponseType;
import com.code.server.game.room.kafka.MsgSender;

/**
 * Created by sunxianping on 2018-10-08.
 */
public class GameInfoFanshi extends GameInfoNew {


    @Override
    protected void mopai(long userId, String... wz) {
        //如果剩最后一张 询问是否摸牌
        if (remainCards.size() == 1) {
            ResponseVo responseVo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, "lastCardIsCatch", 0);
            MsgSender.sendMsg2Player(responseVo, userId);
            this.setLastCatchCardUser(userId);
        }else{
            super.mopai(userId, wz);
        }
    }


    public int fanshiGetCard(long userId,boolean isGet) {

        if (userId != this.getLastCatchCardUser()) {
            return ErrorCode.NOT_TURN;
        }
        if (isGet) {
            super.mopai(userId,null);
        } else {
            handleHuangzhuang(userId);
        }
        this.setLastCatchCardUser(0);
        ResponseVo responseVo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, "fanshiGetCard", 0);
        MsgSender.sendMsg2Player(responseVo, userId);
        return 0;
    }





    protected void doPeng(PlayerCardsInfoMj playerCardsInfo, long userId) {
        playerCardsInfo.peng(disCard, lastPlayUserId);
        lastOperateUserId = userId;

        //删除弃牌
        deleteDisCard(lastPlayUserId, disCard);


        //通知其他玩家

        OperateReqResp operateReqResp = new OperateReqResp();
        operateReqResp.setOperateType(OperateReqResp.type_peng);
        operateReqResp.setCard(disCard);
        operateReqResp.setFromUserId(lastPlayUserId);
        operateReqResp.setUserId(userId);

        //回放
        replay.getOperate().add(operateReqResp);

        //通知其他人
        ResponseVo vo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_OTHER_OPERATE, operateReqResp);
        MsgSender.sendMsg2Player(vo, users);


        //碰完能听,杠,不能胡
        boolean isCanTing = playerCardsInfo.isCanTing(playerCardsInfo.cards);//多一张牌
        boolean isCanGang = false;
        turnId = userId;
        // 通知客户端 操作
        OperateResp operateResp = new OperateResp();
        operateResp.setIsCanTing(isCanTing);
        operateResp.setIsCanGang(isCanGang);
        ResponseVo operateVo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_OPERATE, operateResp);
        MsgSender.sendMsg2Player(operateVo, userId);
        this.disCard = null;
        //自己将能做的处理
        playerCardsInfo.canBePeng = false;
        playerCardsInfo.canBeTing = isCanTing;
        playerCardsInfo.canBeGang = isCanGang;
        playerCardsInfo.canBeHu = false;
        resetOtherOperate(userId);
    }



}
