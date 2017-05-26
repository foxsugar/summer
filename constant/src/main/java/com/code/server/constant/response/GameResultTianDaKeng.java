package com.code.server.constant.response;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ClarkKent on 2017/4/17.
 */
public class GameResultTianDaKeng {
    private List<PlayerCardInfoTianDaKengVo> playerCardInfos = new ArrayList<>();



    public List<PlayerCardInfoTianDaKengVo> getPlayerCardInfos() {
        return playerCardInfos;
    }

    public void setPlayerCardInfos(List<PlayerCardInfoTianDaKengVo> playerCardInfos) {
        this.playerCardInfos = playerCardInfos;
    }
}
