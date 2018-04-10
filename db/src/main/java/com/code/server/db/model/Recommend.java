package com.code.server.db.model;

import com.code.server.db.utils.BaseEntity;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by sunxianping on 2018/4/3.
 */

@DynamicUpdate
@Entity
public class Recommend extends BaseEntity {

    @Id
    private String openId;

    private long agentId;

    public String getOpenId() {
        return openId;
    }

    public Recommend setOpenId(String openId) {
        this.openId = openId;
        return this;
    }

    public long getAgentId() {
        return agentId;
    }

    public Recommend setAgentId(long agentId) {
        this.agentId = agentId;
        return this;
    }
}
