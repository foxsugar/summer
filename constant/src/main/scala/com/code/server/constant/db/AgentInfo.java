package com.code.server.constant.db;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sunxianping on 2018/6/7.
 */
public class AgentInfo {

    private double allRebate;
    private Map<String, Double> everyDayRebate = new HashMap<>();

    public double getAllRebate() {
        return allRebate;
    }

    public AgentInfo setAllRebate(double allRebate) {
        this.allRebate = allRebate;
        return this;
    }

    public Map<String, Double> getEveryDayRebate() {
        return everyDayRebate;
    }

    public AgentInfo setEveryDayRebate(Map<String, Double> everyDayRebate) {
        this.everyDayRebate = everyDayRebate;
        return this;
    }
}
