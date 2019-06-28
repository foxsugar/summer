package com.code.server.db.model;

import com.code.server.db.utils.BaseEntity;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by sunxianping on 2019-06-21.
 */
@DynamicUpdate
@Entity
@Table(name = "rebate_detail",
        indexes = {@Index(name = "id", columnList = "id"),
                @Index(name="userId",columnList = "userId"),
                @Index(name="agentId",columnList = "agentId"),
        })
public class RebateDetail extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private long userId;
    private long agentId;
    private double num;
    private Date date;

    private double beforeNum;
    private double afterNum;
    private int type;



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

    public Date getDate() {
        return date;
    }

    public RebateDetail setDate(Date date) {
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

    public int getType() {
        return type;
    }

    public RebateDetail setType(int type) {
        this.type = type;
        return this;
    }
}
