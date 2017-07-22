package com.code.server.db.model;

import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by Administrator on 2017/6/15.
 */

@DynamicUpdate
@Entity
@Table(name = "charge",
        indexes = {@Index(name = "userId", columnList = "userId")})
public class Charge {

    @Id
    private String orderId;
    private String transaction_id;
    private long userid;
    private Date createtime;
    private Date callbacktime;
    private double money;//人民币
    private int origin;
    private int status;
    private String sign;
    private String sp_ip;
    private int shareid;//分享ID
    private String share_content;//分享内容
    private String share_area;//分享区域
    private double money_point;//房卡
    private String username;//玩家名称
    private String recharge_source;//1 微信  2 支付宝  3 分享赠送  4 充值卡  5绑定赠送

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public long getUserid() {
        return userid;
    }

    public void setUserid(long userid) {
        this.userid = userid;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Date getCallbacktime() {
        return callbacktime;
    }

    public void setCallbacktime(Date callbacktime) {
        this.callbacktime = callbacktime;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public int getOrigin() {
        return origin;
    }

    public void setOrigin(int origin) {
        this.origin = origin;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getSp_ip() {
        return sp_ip;
    }

    public void setSp_ip(String sp_ip) {
        this.sp_ip = sp_ip;
    }

    public int getShareid() {
        return shareid;
    }

    public void setShareid(int shareid) {
        this.shareid = shareid;
    }

    public String getShare_content() {
        return share_content;
    }

    public void setShare_content(String share_content) {
        this.share_content = share_content;
    }

    public String getShare_area() {
        return share_area;
    }

    public void setShare_area(String share_area) {
        this.share_area = share_area;
    }

    public double getMoney_point() {
        return money_point;
    }

    public void setMoney_point(double money_point) {
        this.money_point = money_point;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRecharge_source() {
        return recharge_source;
    }

    public void setRecharge_source(String recharge_source) {
        this.recharge_source = recharge_source;
    }
}
