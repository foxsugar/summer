package com.code.server.constant.response;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunxianping on 2017/7/31.
 */
public class GameResultPaijiu {
    public List<IfacePlayerInfoVo> playerCardInfos = new ArrayList<>();

    public double bankerScore;
    public List<IfacePlayerInfoVo> getPlayerCardInfos() {
        return playerCardInfos;
    }

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
}
