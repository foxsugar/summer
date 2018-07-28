package com.code.server.constant.db;

/**
 * Created by sunxianping on 2018/7/27.
 */
public class PartnerRebate {
    public double cost;
    public double goldRebate;
    public double moneyRebate;
    public double allRebate;

    public double getCost() {
        return cost;
    }

    public PartnerRebate setCost(double cost) {
        this.cost = cost;
        return this;
    }

    public double getMoneyRebate() {
        return moneyRebate;
    }

    public PartnerRebate setMoneyRebate(double moneyRebate) {
        this.moneyRebate = moneyRebate;
        return this;
    }

    public double getAllRebate() {
        return allRebate;
    }

    public PartnerRebate setAllRebate(double allRebate) {
        this.allRebate = allRebate;
        return this;
    }

    public double getGoldRebate() {
        return goldRebate;
    }

    public PartnerRebate setGoldRebate(double goldRebate) {
        this.goldRebate = goldRebate;
        return this;
    }
}
