package com.code.server.db.model;

import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

/**
 * Created by sunxianping on 2018/7/27.
 */
@DynamicUpdate
@Entity
@Table(indexes = {@Index(name = "agentId", columnList = "agentId"),
                @Index(name="date",columnList = "date")})
public class AgentRecords {
    @Id
    private String id;

    private int agentId;

    private String date;

    private double moneyRebate;

    private double goldRebate;

    private double allRebate;

    private double childCost;

    public String getId() {
        return id;
    }

    public AgentRecords setId(String id) {
        this.id = id;
        return this;
    }

    public int getAgentId() {
        return agentId;
    }

    public AgentRecords setAgentId(int agentId) {
        this.agentId = agentId;
        return this;
    }

    public String getDate() {
        return date;
    }

    public AgentRecords setDate(String date) {
        this.date = date;
        return this;
    }

    public double getMoneyRebate() {
        return moneyRebate;
    }

    public AgentRecords setMoneyRebate(double moneyRebate) {
        this.moneyRebate = moneyRebate;
        return this;
    }

    public double getGoldRebate() {
        return goldRebate;
    }

    public AgentRecords setGoldRebate(double goldRebate) {
        this.goldRebate = goldRebate;
        return this;
    }

    public double getAllRebate() {
        return allRebate;
    }

    public AgentRecords setAllRebate(double allRebate) {
        this.allRebate = allRebate;
        return this;
    }

    public double getChildCost() {
        return childCost;
    }

    public AgentRecords setChildCost(double childCost) {
        this.childCost = childCost;
        return this;
    }
}
