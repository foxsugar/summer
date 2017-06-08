package com.code.server.game.mahjong.response;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by win7 on 2016/12/2.
 */
public class HandCardsResp {
    List<String> cards = new ArrayList<>();

    public List<String> getCards() {
        return cards;
    }

    public void setCards(List<String> cards) {
        this.cards = cards;
    }
}
