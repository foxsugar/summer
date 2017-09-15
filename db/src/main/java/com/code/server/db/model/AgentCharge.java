package com.code.server.db.model;

import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

/**
 * Created by sunxianping on 2017/9/13.
 */
@DynamicUpdate
@Entity
public class AgentCharge {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private int agentId;//代理id
    private int chargeSrcAgent;//充值原代理id
    private int chargeType;
    private double chargeNum;
    private Date chargeTime;

    public int getId() {
        return id;
    }

    public AgentCharge setId(int id) {
        this.id = id;
        return this;
    }

    public int getAgentId() {
        return agentId;
    }

    public AgentCharge setAgentId(int agentId) {
        this.agentId = agentId;
        return this;
    }

    public int getChargeSrcAgent() {
        return chargeSrcAgent;
    }

    public AgentCharge setChargeSrcAgent(int chargeSrcAgent) {
        this.chargeSrcAgent = chargeSrcAgent;
        return this;
    }

    public int getChargeType() {
        return chargeType;
    }

    public AgentCharge setChargeType(int chargeType) {
        this.chargeType = chargeType;
        return this;
    }

    public double getChargeNum() {
        return chargeNum;
    }

    public AgentCharge setChargeNum(double chargeNum) {
        this.chargeNum = chargeNum;
        return this;
    }

    public Date getChargeTime() {
        return chargeTime;
    }

    public AgentCharge setChargeTime(Date chargeTime) {
        this.chargeTime = chargeTime;
        return this;
    }
}
