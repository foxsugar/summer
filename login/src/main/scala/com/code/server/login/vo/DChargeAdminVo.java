package com.code.server.login.vo;

/**
 * Created by dajuejinxian on 2018/6/28.
 */
public class DChargeAdminVo {

    private String orderId;
    private String createtime;
    private String money;
    private String money_point;
    private long status;
    private String username;
    private long chargeType;
    private long recharge_source;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getMoney_point() {
        return money_point;
    }

    public void setMoney_point(String money_point) {
        this.money_point = money_point;
    }

    public long getStatus() {
        return status;
    }

    public void setStatus(long status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getChargeType() {
        return chargeType;
    }

    public void setChargeType(long chargeType) {
        this.chargeType = chargeType;
    }

    public long getRecharge_source() {
        return recharge_source;
    }

    public void setRecharge_source(long recharge_source) {
        this.recharge_source = recharge_source;
    }
}
