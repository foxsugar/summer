package com.code.server.constant.response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sunxianping on 2017/7/31.
 */
public class GameResultPaijiu {
    public List<IfacePlayerInfoVo> playerCardInfos = new ArrayList<>();

    public double bankerScore;
    public List<IfacePlayerInfoVo> getPlayerCardInfos() {
        return playerCardInfos;
    }

    public Map<Integer, Object> cardMap = new HashMap<>();

    public GameResultPaijiu setPlayerCardInfos(List<IfacePlayerInfoVo> playerCardInfos) {
        this.playerCardInfos = playerCardInfos;
        return this;
    }

    public double getBankerScore() {
        return bankerScore;
    }

    public void setBankerScore(double bankerScore) {
        this.bankerScore = bankerScore;
    }

    public Map<Integer, Object> getCardMap() {
        return cardMap;
    }

    public void setCardMap(Map<Integer, Object> cardMap) {
        this.cardMap = cardMap;
    }
}
