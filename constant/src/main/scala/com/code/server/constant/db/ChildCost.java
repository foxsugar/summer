package com.code.server.constant.db;

/**
 * Created by sunxianping on 2018/7/23.
 */
public class ChildCost {
    public double firstLevel;
    public double secondLevel;
    public double thirdLevel;
    public double partner;


    public double getFirstLevel() {
        return firstLevel;
    }

    public ChildCost setFirstLevel(double firstLevel) {
        this.firstLevel = firstLevel;
        return this;
    }

    public double getSecondLevel() {
        return secondLevel;
    }

    public ChildCost setSecondLevel(double secondLevel) {
        this.secondLevel = secondLevel;
        return this;
    }

    public double getThirdLevel() {
        return thirdLevel;
    }

    public ChildCost setThirdLevel(double thirdLevel) {
        this.thirdLevel = thirdLevel;
        return this;
    }

    public double getPartner() {
        return partner;
    }

    public ChildCost setPartner(double partner) {
        this.partner = partner;
        return this;
    }
}
