package com.code.server.db.model;

import com.code.server.db.utils.BaseEntity;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by win7 on 2017/3/8.
 */
@DynamicUpdate
@Entity
@Table(name = "users",
        indexes = {@Index(name = "userId", columnList = "userId"),
                    @Index(name="openId",columnList = "openId")})
public class User extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long userId;

    @Column(nullable = false)
    private String account;

    @Column(nullable = false)
    private String password;

    @Column(columnDefinition = "varchar(4000)")
    private String username;

    private String ipConfig;

    private double money;//虚拟货币

    private double cash;//货币

    @Column(columnDefinition = "varchar(4000)")
    private String image;//头像

    private double rebate;//返利

    @Column(columnDefinition = "int default 0")
    private int referee;//返利人id

    private double gold;//金币



    private int vip;//vip

    private String fatherId;//代理id

    private String uuid;//uuid

    private String openId;//openId

    private int sex;//

    private String aliId;

    @Column(columnDefinition = "varchar(2000)")
    private String email;

    private Date registDate;

    private Date lastLoginDate;


    @Type(type = "json")
    @Lob
    @Column(columnDefinition = "longtext")
    private UserInfo userInfo = new UserInfo();


    @Type(type = "json")
    @Lob
    @Column(columnDefinition = "longtext")
    private Record record = new Record();


    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getUsername() {
        return username;
    }

    public User setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIpConfig() {
        return ipConfig;
    }

    public void setIpConfig(String ipConfig) {
        this.ipConfig = ipConfig;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public double getCash() {
        return cash;
    }

    public void setCash(double cash) {
        this.cash = cash;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }



    public String getFatherId() {
        return fatherId;
    }

    public void setFatherId(String fatherId) {
        this.fatherId = fatherId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public int getVip() {
        return vip;
    }

    public User setVip(int vip) {
        this.vip = vip;
        return this;
    }

    public int getSex() {
        return sex;
    }

    public User setSex(int sex) {
        this.sex = sex;
        return this;
    }

    public String getAliId() {
        return aliId;
    }

    public void setAliId(String aliId) {
        this.aliId = aliId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getRegistDate() {
        return registDate;
    }

    public void setRegistDate(Date registDate) {
        this.registDate = registDate;
    }

    public Date getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }



    public Record getRecord() {
        return record;
    }

    public User setRecord(Record record) {
        this.record = record;
        return this;
    }

    public double getRebate() {
        return rebate;
    }

    public User setRebate(double rebate) {
        this.rebate = rebate;
        return this;
    }

    public int getReferee() {
        return referee;
    }

    public User setReferee(int referee) {
        this.referee = referee;
        return this;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public User setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
        return this;
    }

    public double getGold() {
        return gold;
    }

    public User setGold(double gold) {
        this.gold = gold;
        return this;
    }
} // class User