package com.code.server.admin.model;

import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/**
 * Created by sunxianping on 2017/8/17.
 */
@DynamicUpdate
@Entity
@Table(name = "agent_user",
        indexes = {@Index(name = "id", columnList = "id")})
public class AgentUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;


    public long getId() {
        return id;
    }

    public AgentUser setId(long id) {
        this.id = id;
        return this;
    }
}
