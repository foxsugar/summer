package com.code.server.db.model;

import com.code.server.db.utils.BaseEntity;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

/**
 * Created by sunxianping on 2019-06-21.
 */
@DynamicUpdate
@Entity
@Table(name = "users",
        indexes = {@Index(name = "id", columnList = "id"),
                @Index(name="userId",columnList = "userId"),
                @Index(name="agentId",columnList = "agentId"),
        })
public class RebateDetail extends BaseEntity {

    private long id;
    private long userId;
    private long agentId;
    private double num;
    private String date;

    private double beforeNum;
    private double afterNum;



    public long getId() {
        return id;
    }

    public RebateDetail setId(long id) {
        this.id = id;
        return this;
    }

    public long getUserId() {
        return userId;
    }

    public RebateDetail setUserId(long userId) {
        this.userId = userId;
        return this;
    }

    public long getAgentId() {
        return agentId;
    }

    public RebateDetail setAgentId(long agentId) {
        this.agentId = agentId;
        return this;
    }

    public double getNum() {
        return num;
    }

    public RebateDetail setNum(double num) {
        this.num = num;
        return this;
    }

    public String getDate() {
        return date;
    }

    public RebateDetail setDate(String date) {
        this.date = date;
        return this;
    }

    public double getBeforeNum() {
        return beforeNum;
    }

    public RebateDetail setBeforeNum(double beforeNum) {
        this.beforeNum = beforeNum;
        return this;
    }

    public double getAfterNum() {
        return afterNum;
    }

    public RebateDetail setAfterNum(double afterNum) {
        this.afterNum = afterNum;
        return this;
    }
}
