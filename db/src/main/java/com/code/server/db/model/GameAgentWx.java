package com.code.server.db.model;

import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by sunxianping on 2018/5/9.
 */
@DynamicUpdate
@Entity
@Table(name = "game_agent_wx")
public class GameAgentWx {
    private String unionId;
    private String openId;

    public String getUnionId() {
        return unionId;
    }

    public GameAgentWx setUnionId(String unionId) {
        this.unionId = unionId;
        return this;
    }

    public String getOpenId() {
        return openId;
    }

    public GameAgentWx setOpenId(String openId) {
        this.openId = openId;
        return this;
    }
}
