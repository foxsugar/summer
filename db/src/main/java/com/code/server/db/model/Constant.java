package com.code.server.db.model;

import javax.persistence.*;

/**
 * Created by win7 on 2017/3/10.
 */
@Entity
@Table(name = "constant")
public class Constant {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String marquee;
    private String download;
    private String download2;
    private int initMoney;
    private String versionOfAndroid;//安卓版本
    private String versionOfIos;//IOS版本


    private int appleCheck;


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

    @Override
    public String toString() {
        return "Constant{" +
                "id=" + id +
                ", marquee='" + marquee + '\'' +
                ", download='" + download + '\'' +
                ", download2='" + download2 + '\'' +
                ", initMoney=" + initMoney +
                ", versionOfAndroid='" + versionOfAndroid + '\'' +
                ", versionOfIos='" + versionOfIos + '\'' +
                ", appleCheck=" + appleCheck +
                '}';
    }
}
