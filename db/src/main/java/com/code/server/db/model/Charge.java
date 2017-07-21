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

    public Charge setOrderId(String orderId) {
        this.orderId = orderId;
        return this;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public Charge setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
        return this;
    }

    public long getUserid() {
        return userid;
    }

    public Charge setUserid(long userid) {
        this.userid = userid;
        return this;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public Charge setCreatetime(Date createtime) {
        this.createtime = createtime;
        return this;
    }

    public Date getCallbacktime() {
        return callbacktime;
    }

    public Charge setCallbacktime(Date callbacktime) {
        this.callbacktime = callbacktime;
        return this;
    }

    public double getMoney() {
        return money;
    }

    public Charge setMoney(double money) {
        this.money = money;
        return this;
    }

    public int getOrigin() {
        return origin;
    }

    public Charge setOrigin(int origin) {
        this.origin = origin;
        return this;
    }

    public int getStatus() {
        return status;
    }

    public Charge setStatus(int status) {
        this.status = status;
        return this;
    }

    public String getSign() {
        return sign;
    }

    public Charge setSign(String sign) {
        this.sign = sign;
        return this;
    }

    public String getSp_ip() {
        return sp_ip;
    }

    public Charge setSp_ip(String sp_ip) {
        this.sp_ip = sp_ip;
        return this;
    }

    public int getShareid() {
        return shareid;
    }

    public Charge setShareid(int shareid) {
        this.shareid = shareid;
        return this;
    }

    public String getShare_content() {
        return share_content;
    }

    public Charge setShare_content(String share_content) {
        this.share_content = share_content;
        return this;
    }

    public String getShare_area() {
        return share_area;
    }

    public Charge setShare_area(String share_area) {
        this.share_area = share_area;
        return this;
    }

    public double getMoney_point() {
        return money_point;
    }

    public Charge setMoney_point(double money_point) {
        this.money_point = money_point;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public Charge setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getRecharge_source() {
        return recharge_source;
    }

    public Charge setRecharge_source(String recharge_source) {
        this.recharge_source = recharge_source;
        return this;
    }
}
