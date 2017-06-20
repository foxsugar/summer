package com.code.server.game.mahjong.response;



import com.code.server.game.mahjong.logic.GameInfo;
import com.code.server.game.mahjong.logic.PlayerCardsInfoMj;

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

    public ReconnectResp(GameInfo gameInfo, long userId) {
        disCard = gameInfo.getDisCard();
        isHasBao = gameInfo.getBaoCard() != null;
        baoCard = gameInfo.getBaoCard();
        changeBaoCount = gameInfo.getChangeBaoSize();
        isAllPass = gameInfo.getWaitingforList().size() ==0;

        for (PlayerCardsInfoMj playerCardsInfo : gameInfo.getPlayerCardsInfos().values()) {
            long uid = playerCardsInfo.getUserId();
            boolean isMine = userId == uid;
            playerCards.add(new PlayerCardsResp(playerCardsInfo,isMine));
        }
    }

}
