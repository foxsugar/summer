package com.code.server.game.mahjong.logic;

import com.code.server.constant.response.ResponseVo;
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
        }else{
            super.mopai(userId, wz);
        }
    }


    public int fanshiGetCard(long userId,boolean isGet) {

        if (isGet) {
            super.mopai(userId,null);
        } else {
            handleHuangzhuang(userId);
        }
        ResponseVo responseVo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, "fanshiGetCard", 0);
        MsgSender.sendMsg2Player(responseVo, userId);
        return 0;
    }




}
