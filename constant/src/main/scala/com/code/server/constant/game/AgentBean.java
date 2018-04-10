package com.code.server.constant.game;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunxianping on 2018/3/13.
 */
public class AgentBean {
    private long id;

    private double rebate;

    private long partnerId;

    private long parentId;

    private int isPartner;

    private List<Long> childList = new ArrayList<>();

    public long getId() {
        return id;
    }

    public AgentBean setId(long id) {
        this.id = id;
        return this;
    }

    public double getRebate() {
        return rebate;
    }

    public AgentBean setRebate(double rebate) {
        this.rebate = rebate;
        return this;
    }

    public long getPartnerId() {
        return partnerId;
    }

    public AgentBean setPartnerId(long partnerId) {
        this.partnerId = partnerId;
        return this;
    }

    public long getParentId() {
        return parentId;
    }

    public AgentBean setParentId(long parentId) {
        this.parentId = parentId;
        return this;
    }

    public List<Long> getChildList() {
        return childList;
    }

    public AgentBean setChildList(List<Long> childList) {
        this.childList = childList;
        return this;
    }

    public int getIsPartner() {
        return isPartner;
    }

    public AgentBean setIsPartner(int isPartner) {
        this.isPartner = isPartner;
        return this;
    }
}
