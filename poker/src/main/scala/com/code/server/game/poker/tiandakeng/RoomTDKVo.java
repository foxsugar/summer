package com.code.server.game.poker.tiandakeng;

import com.code.server.constant.response.RoomVo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunxianping on 2018-11-06.
 */
public class RoomTDKVo extends RoomVo {

    private boolean isLanGuo = false;//是否烂锅

    private List<Integer> languoBets = new ArrayList<>();

    public boolean isLanGuo() {
        return isLanGuo;
    }

    public RoomTDKVo setLanGuo(boolean lanGuo) {
        isLanGuo = lanGuo;
        return this;
    }

    public List<Integer> getLanguoBets() {
        return languoBets;
    }

    public RoomTDKVo setLanguoBets(List<Integer> languoBets) {
        this.languoBets = languoBets;
        return this;
    }
}
