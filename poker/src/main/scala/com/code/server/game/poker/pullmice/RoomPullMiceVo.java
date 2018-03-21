package com.code.server.game.poker.pullmice;

import com.code.server.constant.response.RoomVo;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

public class RoomPullMiceVo extends RoomVo {

    @JsonIgnore
    protected List<Integer> cards = new ArrayList<>();

    protected long potBottom;

    protected long cardsTotal;

    protected long maxGameCount;

    protected boolean canWuBuFeng;

    public boolean isCanWuBuFeng() {
        return canWuBuFeng;
    }

    public void setCanWuBuFeng(boolean canWuBuFeng) {
        this.canWuBuFeng = canWuBuFeng;
    }

    public List<Integer> getCards() {
        return cards;
    }

    public void setCards(List<Integer> cards) {
        this.cards = cards;
    }


    public long getCardsTotal() {
        return cardsTotal;
    }

    public void setCardsTotal(long cardsTotal) {
        this.cardsTotal = cardsTotal;
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
