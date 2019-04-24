package com.code.server.db.model;

import com.code.server.constant.db.OtherConstant;
import com.code.server.db.utils.BaseEntity;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by win7 on 2017/3/10.
 */
@Entity
@Table(name = "constant")
public class Constant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String marquee;
    private String marquee1;
    private String marquee2;
    private String download;
    private String download1;
    private String download2;
    private int initMoney;
    private int initGold;
    private String versionOfAndroid;//安卓版本
    private String versionOfIos;//IOS版本
    private int appleCheck;
    private int shareMoney;//分享获得的钱
    private String AccessCode;
    private int income1;
    private int income2;
    @Type(type = "json")
    @Lob
    @Column(columnDefinition = "json")
    private Set<String> blackList = new HashSet<>();

    private String payUid;//paysa uid
    private String payToken;//paysa token

    @Type(type = "json")
    @Lob
    @Column(columnDefinition = "json")
    private OtherConstant other;



    public long getId() {
        return id;
    }

    public Constant setId(long id) {
        this.id = id;
        return this;
    }

    public String getMarquee() {
        return marquee;
    }

    public Constant setMarquee(String marquee) {
        this.marquee = marquee;
        return this;
    }

    public String getDownload() {
        return download;
    }

    public Constant setDownload(String download) {
        this.download = download;
        return this;
    }

    public int getInitMoney() {
        return initMoney;
    }

    public Constant setInitMoney(int initMoney) {
        this.initMoney = initMoney;
        return this;
    }

    public String getDownload2() {
        return download2;
    }

    public Constant setDownload2(String download2) {
        this.download2 = download2;
        return this;
    }

    public String getVersionOfAndroid() {
        return versionOfAndroid;
    }

    public Constant setVersionOfAndroid(String versionOfAndroid) {
        this.versionOfAndroid = versionOfAndroid;
        return this;
    }

    public String getVersionOfIos() {
        return versionOfIos;
    }

    public Constant setVersionOfIos(String versionOfIos) {
        this.versionOfIos = versionOfIos;
        return this;
    }

    public int getAppleCheck() {
        return appleCheck;
    }

    public Constant setAppleCheck(int appleCheck) {
        this.appleCheck = appleCheck;
        return this;
    }


    public String getMarquee1() {
        return marquee1;
    }

    public Constant setMarquee1(String marquee1) {
        this.marquee1 = marquee1;
        return this;
    }

    public String getMarquee2() {
        return marquee2;
    }

    public Constant setMarquee2(String marquee2) {
        this.marquee2 = marquee2;
        return this;
    }

    public int getShareMoney() {
        return shareMoney;
    }

    public Constant setShareMoney(int shareMoney) {
        this.shareMoney = shareMoney;
        return this;
    }

    public Set<String> getBlackList() {
        return blackList;
    }

    public Constant setBlackList(Set<String> blackList) {
        this.blackList = blackList;
        return this;
    }

    public String getAccessCode() {
        return AccessCode;
    }

    public Constant setAccessCode(String accessCode) {
        AccessCode = accessCode;
        return this;
    }

    public String getDownload1() {
        return download1;
    }

    public Constant setDownload1(String download1) {
        this.download1 = download1;
        return this;
    }

    public int getIncome1() {
        return income1;
    }

    public void setIncome1(int income1) {
        this.income1 = income1;
    }

    public int getIncome2() {
        return income2;
    }

    public void setIncome2(int income2) {
        this.income2 = income2;
    }

    public String getPayUid() {
        return payUid;
    }

    public Constant setPayUid(String payUid) {
        this.payUid = payUid;
        return this;
    }

    public String getPayToken() {
        return payToken;
    }

    public Constant setPayToken(String payToken) {
        this.payToken = payToken;
        return this;
    }

    public OtherConstant getOther() {
        return other;
    }

    public Constant setOther(OtherConstant other) {
        this.other = other;
        return this;
    }

    public int getInitGold() {
        return initGold;
    }

    public Constant setInitGold(int initGold) {
        this.initGold = initGold;
        return this;
    }

    @Override
    public String toString() {
        return "Constant{" +
                "id=" + id +
                ", marquee='" + marquee + '\'' +
                ", marquee1='" + marquee1 + '\'' +
                ", marquee2='" + marquee2 + '\'' +
                ", download='" + download + '\'' +
                ", download1='" + download1 + '\'' +
                ", download2='" + download2 + '\'' +
                ", initMoney=" + initMoney +
                ", versionOfAndroid='" + versionOfAndroid + '\'' +
                ", versionOfIos='" + versionOfIos + '\'' +
                ", appleCheck=" + appleCheck +
                ", shareMoney=" + shareMoney +
                ", AccessCode='" + AccessCode + '\'' +
                ", income1=" + income1 +
                ", income2=" + income2 +
                ", blackList=" + blackList +
                ", payUid='" + payUid + '\'' +
                ", payToken='" + payToken + '\'' +
                ", other=" + other +
                '}';
    }
}
