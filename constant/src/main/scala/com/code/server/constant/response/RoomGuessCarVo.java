package com.code.server.constant.response;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunxianping on 2017/12/12.
 */
public class RoomGuessCarVo extends RoomVo {

    public List<Integer> record = new ArrayList<>();
    public int state = 0;
    public double bankerScore = 0;

    public int getState() {
        return state;
    }

    public RoomGuessCarVo setState(int state) {
        this.state = state;
        return this;
    }

    public List<Integer> getRecord() {
        return record;
    }

    public RoomGuessCarVo setRecord(List<Integer> record) {
        this.record = record;
        return this;
    }

    public double getBankerScore() {
        return bankerScore;
    }

    public RoomGuessCarVo setBankerScore(double bankerScore) {
        this.bankerScore = bankerScore;
        return this;
    }
}
