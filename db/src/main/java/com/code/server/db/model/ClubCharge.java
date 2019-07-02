package com.code.server.db.model;

import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/**
 * Created by sunxianping on 2018/2/27.
 */

@DynamicUpdate
@Entity
@Table(indexes = {@Index(name = "clubId", columnList = "clubId")})
public class ClubCharge {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;//id

    private String clubId;//俱乐部id

    private long num;//数量

    private long nowMoney;//现在的钱数

    private String chargeTime;//时间


    public int getId() {
        return id;
    }

    public ClubCharge setId(int id) {
        this.id = id;
        return this;
    }

    public String getClubId() {
        return clubId;
    }

    public ClubCharge setClubId(String clubId) {
        this.clubId = clubId;
        return this;
    }

    public long getNum() {
        return num;
    }

    public ClubCharge setNum(long num) {
        this.num = num;
        return this;
    }

    public long getNowMoney() {
        return nowMoney;
    }

    public ClubCharge setNowMoney(long nowMoney) {
        this.nowMoney = nowMoney;
        return this;
    }

    public String getChargeTime() {
        return chargeTime;
    }

    public ClubCharge setChargeTime(String chargeTime) {
        this.chargeTime = chargeTime;
        return this;
    }
}
