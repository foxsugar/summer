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
}
