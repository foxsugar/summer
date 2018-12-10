package com.code.server.game.poker.yuxiaxie;

import com.code.server.constant.response.GameVo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sunxianping on 2018-12-10.
 */
public class GameYuxiaxieVo extends GameVo {


    private RoomYuxiaxie room;
    protected Map<Long, PlayerInfoYuxiaxieVo> playerCardInfos = new HashMap<>();
    private int state;
    private List<Integer> dice = new ArrayList<>();


    public RoomYuxiaxie getRoom() {
        return room;
    }

    public GameYuxiaxieVo setRoom(RoomYuxiaxie room) {
        this.room = room;
        return this;
    }

    public Map<Long, PlayerInfoYuxiaxieVo> getPlayerCardInfos() {
        return playerCardInfos;
    }

    public GameYuxiaxieVo setPlayerCardInfos(Map<Long, PlayerInfoYuxiaxieVo> playerCardInfos) {
        this.playerCardInfos = playerCardInfos;
        return this;
    }

    public int getState() {
        return state;
    }

    public GameYuxiaxieVo setState(int state) {
        this.state = state;
        return this;
    }

    public List<Integer> getDice() {
        return dice;
    }

    public GameYuxiaxieVo setDice(List<Integer> dice) {
        this.dice = dice;
        return this;
    }
}
