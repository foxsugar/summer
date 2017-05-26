package com.code.server.constant.response;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunxianping on 2017/3/27.
 */
public class GameResultDouDizhu {
    private List<PlayerCardInfoVo> playerCardInfos = new ArrayList<>();
    private boolean isDizhuWin;
    private boolean isSpring;
    private int multiple;
    private boolean isReopen;

    public List<PlayerCardInfoVo> getPlayerCardInfos() {
        return playerCardInfos;
    }

    public GameResultDouDizhu setPlayerCardInfos(List<PlayerCardInfoVo> playerCardInfos) {
        this.playerCardInfos = playerCardInfos;
        return this;
    }



    public boolean isSpring() {
        return isSpring;
    }

    public GameResultDouDizhu setSpring(boolean spring) {
        isSpring = spring;
        return this;
    }

    public int getMultiple() {
        return multiple;
    }

    public GameResultDouDizhu setMultiple(int multiple) {
        this.multiple = multiple;
        return this;
    }

    public boolean isDizhuWin() {
        return isDizhuWin;
    }

    public GameResultDouDizhu setDizhuWin(boolean dizhuWin) {
        isDizhuWin = dizhuWin;
        return this;
    }

    public boolean isReopen() {
        return isReopen;
    }

    public GameResultDouDizhu setReopen(boolean reopen) {
        isReopen = reopen;
        return this;
    }
}
