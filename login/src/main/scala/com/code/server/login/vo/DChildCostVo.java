package com.code.server.login.vo;

/**
 * Created by dajuejinxian on 2018/7/27.
 */
public class DChildCostVo {

    public double firstLevel;
    public double secondLevel;
    public double thirdLevel;
    public double partner;
    public String dateStr;

    public double getFirstLevel() {
        return firstLevel;
    }

    public void setFirstLevel(double firstLevel) {
        this.firstLevel = firstLevel;
    }

    public double getSecondLevel() {
        return secondLevel;
    }

    public void setSecondLevel(double secondLevel) {
        this.secondLevel = secondLevel;
    }

    public double getThirdLevel() {
        return thirdLevel;
    }

    public void setThirdLevel(double thirdLevel) {
        this.thirdLevel = thirdLevel;
    }

    public double getPartner() {
        return partner;
    }

    public void setPartner(double partner) {
        this.partner = partner;
    }

    public String getDateStr() {
        return dateStr;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }
}
