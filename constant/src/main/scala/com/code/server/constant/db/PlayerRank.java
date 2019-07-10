package com.code.server.constant.db;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sunxianping on 2019-07-01.
 */
public class PlayerRank {
    private Map<Long,PlayerScore> players = new HashMap<>();

    public Map<Long, PlayerScore> getPlayers() {
        return players;
    }

    public PlayerRank setPlayers(Map<Long, PlayerScore> players) {
        this.players = players;
        return this;
    }
}
