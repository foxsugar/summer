package com.code.server.constant.db;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sunxianping on 2018/6/7.
 */
public class AgentInfo {

    private double allRebate;
    private Map<String, Double> everyDayRebate = new HashMap<>();
    private Map<String, ChildCost> everyDayCost = new HashMap<>();
    private Map<String, PartnerRebate> everyPartnerRebate = new HashMap<>();

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

    public Map<String, ChildCost> getEveryDayCost() {
        return everyDayCost;
    }

    public AgentInfo setEveryDayCost(Map<String, ChildCost> everyDayCost) {
        this.everyDayCost = everyDayCost;
        return this;
    }

    public Map<String, PartnerRebate> getEveryPartnerRebate() {
        return everyPartnerRebate;
    }

    public AgentInfo setEveryPartnerRebate(Map<String, PartnerRebate> everyPartnerRebate) {
        this.everyPartnerRebate = everyPartnerRebate;
        return this;
    }
}
