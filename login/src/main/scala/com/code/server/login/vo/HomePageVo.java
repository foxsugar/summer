package com.code.server.login.vo;

import com.code.server.constant.game.AgentBean;

/**
 * Created by dajuejinxian on 2018/5/31.
 */
public class HomePageVo {

    private String InvitationCode;
    private String totalMoney;
    private String rebate;

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
}
