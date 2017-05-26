package com.code.server.game.mahjong.response;

import com.byz.mj.serviceNew.GameInfo;
import com.byz.mj.serviceNew.PlayerCardsInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by win7 on 2016/12/5.
 */
public class ReconnectResp {

    private String disCard;
    private boolean isHasBao;
    private String baoCard;
    private int changeBaoCount;
    private List<PlayerCardsResp> playerCards = new ArrayList<>();
    private boolean isAllPass;



    public ReconnectResp(){}

    public ReconnectResp(GameInfo gameInfo,int userId) {
        disCard = gameInfo.getDisCard();
        isHasBao = gameInfo.getBaoCard() != null;
        baoCard = gameInfo.getBaoCard();
        changeBaoCount = gameInfo.getChangeBaoSize();
        isAllPass = gameInfo.getWaitingforList().size() ==0;

        for (PlayerCardsInfo playerCardsInfo : gameInfo.getPlayerCardsInfos().values()) {
            int uid = playerCardsInfo.getUserId();
            boolean isMine = userId == uid;
            playerCards.add(new PlayerCardsResp(playerCardsInfo,isMine));
        }
    }

}
