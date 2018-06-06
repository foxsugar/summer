package com.code.server.constant.game;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by sunxianping on 2018/3/13.
 */
public class AgentBean {
    private long id;

    private String openId;

    private String unionId;

    private double rebate;

    private long partnerId;


    private long parentId;

    private int isPartner;

    private String qrTicket;

    private String image;

    private Set<Long> childList = new HashSet<>();

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

    public Set<Long> getChildList() {
        return childList;
    }

    public AgentBean setChildList(Set<Long> childList) {
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

    public String getQrTicket() {
        return qrTicket;
    }

    public AgentBean setQrTicket(String qrTicket) {
        this.qrTicket = qrTicket;
        return this;
    }

    public String getOpenId() {
        return openId;
    }

    public AgentBean setOpenId(String openId) {
        this.openId = openId;
        return this;
    }

    public String getUnionId() {
        return unionId;
    }

    public AgentBean setUnionId(String unionId) {
        this.unionId = unionId;
        return this;
    }

    public String getImage() {
        return image;
    }

    public AgentBean setImage(String image) {
        this.image = image;
        return this;
    }

    @Override
    public String toString() {
        return "AgentBean{" +
                "id=" + id +
                ", openId='" + openId + '\'' +
                ", unionId='" + unionId + '\'' +
                ", rebate=" + rebate +
                ", partnerId=" + partnerId +
                ", parentId=" + parentId +
                ", isPartner=" + isPartner +
                ", qrTicket='" + qrTicket + '\'' +
                ", image='" + image + '\'' +
                ", childList=" + childList +
                '}';
    }
}
