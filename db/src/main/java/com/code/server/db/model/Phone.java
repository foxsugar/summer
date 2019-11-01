package com.code.server.db.model;

import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

/**
 * Created by sunxianping on 2019-11-01.
 */
@DynamicUpdate
@Entity
@Table(name = "phone",
        indexes = {@Index(name = "id", columnList = "id")
        })
public class Phone {
    @Id
    private String id;

    private String gameType;

    public String getId() {
        return id;
    }

    public Phone setId(String id) {
        this.id = id;
        return this;
    }

    public String getGameType() {
        return gameType;
    }

    public Phone setGameType(String gameType) {
        this.gameType = gameType;
        return this;
    }
}
