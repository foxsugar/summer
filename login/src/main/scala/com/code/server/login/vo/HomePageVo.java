package com.code.server.login.vo;

import com.code.server.constant.game.AgentBean;

/**
 * Created by dajuejinxian on 2018/5/31.
 */
public class HomePageVo {

    private String InvitationCode;
    private String totalMoney;
    private String rebate;
    //收益
    private double firstLevel;
    private double secondLevel;
    private double thirdLevel;
    private double allCost;

    public double getAllCost() {
        return allCost;
    }

    public void setAllCost(double allCost) {
        this.allCost = allCost;
    }

    public String getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(String totalMoney) {
        this.totalMoney = totalMoney;
    }

    public String getInvitationCode() {
        return InvitationCode;
    }

    public void setInvitationCode(String invitationCode) {
        InvitationCode = invitationCode;
    }

    public String getRebate() {
        return rebate;
    }

    public void setRebate(String rebate) {
        this.rebate = rebate;
    }

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

    @Override
    public String toString() {
        return "HomePageVo{" +
                "InvitationCode='" + InvitationCode + '\'' +
                ", totalMoney='" + totalMoney + '\'' +
                ", rebate='" + rebate + '\'' +
                '}';
    }
}
