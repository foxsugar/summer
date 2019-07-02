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
    private String unionId;//unionId

    private long agentId;//代理id

    public String getUnionId() {
        return unionId;
    }

    public Recommend setUnionId(String unionId) {
        this.unionId = unionId;
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
