package com.code.server.game.poker.pullmice;

import com.code.server.constant.response.RoomVo;

import java.util.ArrayList;
import java.util.List;

public class RoomPullMiceVo extends RoomVo {

    protected List<Integer> cards = new ArrayList<>();

    protected long potBottom;

    protected long maxGameCount;

    public List<Integer> getCards() {
        return cards;
    }

    public void setCards(List<Integer> cards) {
        this.cards = cards;
    }

    public long getPotBottom() {
        return potBottom;
    }

    public void setPotBottom(long potBottom) {
        this.potBottom = potBottom;
    }

    public long getMaxGameCount() {
        return maxGameCount;
    }

    public void setMaxGameCount(long maxGameCount) {
        this.maxGameCount = maxGameCount;
    }
}
