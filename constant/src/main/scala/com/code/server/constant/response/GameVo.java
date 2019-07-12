package com.code.server.constant.response;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sunxianping on 2017/3/15.
 */
public class GameVo implements IfaceGameVo{


    public Map<Long, Integer> autoStatus = new HashMap<>();
    public Map<Long, Integer> autoTimes = new HashMap<>();

    public GameVo(){}


    public Map<Long, Integer> getAutoStatus() {
        return autoStatus;
    }

    public GameVo setAutoStatus(Map<Long, Integer> autoStatus) {
        this.autoStatus = autoStatus;
        return this;
    }

    public Map<Long, Integer> getAutoTimes() {
        return autoTimes;
    }

    public GameVo setAutoTimes(Map<Long, Integer> autoTimes) {
        this.autoTimes = autoTimes;
        return this;
    }
}
