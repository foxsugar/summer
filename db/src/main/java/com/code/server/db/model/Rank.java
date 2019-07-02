package com.code.server.db.model;

import com.code.server.constant.db.PlayerRank;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import javax.persistence.*;

/**
 * Created by sunxianping on 2019-07-01.
 */

@DynamicUpdate
@Entity
@Table(name = "rank",
        indexes = {@Index(name = "id", columnList = "id")
        })
public class Rank {
    @Id
    private String id;

    @Type(type = "json")
    @Lob
    @Column(columnDefinition = "json")
    private PlayerRank playerRank = new PlayerRank();


    public String getId() {
        return id;
    }

    public Rank setId(String id) {
        this.id = id;
        return this;
    }

    public PlayerRank getPlayerRank() {
        return playerRank;
    }

    public Rank setPlayerRank(PlayerRank playerRank) {
        this.playerRank = playerRank;
        return this;
    }
}
