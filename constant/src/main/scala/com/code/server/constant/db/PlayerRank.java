package com.code.server.constant.db;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunxianping on 2019-07-01.
 */
public class PlayerRank {
    private List<PlayerScore> players = new ArrayList<>();

    public List<PlayerScore> getPlayers() {
        return players;
    }

    public PlayerRank setPlayers(List<PlayerScore> players) {
        this.players = players;
        return this;
    }
}
