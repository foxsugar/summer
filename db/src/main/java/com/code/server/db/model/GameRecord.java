package com.code.server.db.model;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by sunxianping on 2017/9/6.
 */

@DynamicUpdate
@Entity
@Table(name = "game_record",
        indexes = {
        @Index(name = "uuid", columnList = "uuid"),
        @Index(name = "date", columnList = "date"),
})
public class GameRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;//id

    private long uuid;//uuid

    private Date date;//时间

    private int leftCount;//计数

    private long replayId;//回放id

    @Type(type = "json")
    @Lob
    @Column(columnDefinition = "json")
    private com.code.server.constant.game.GameRecord gameRecord;//记录


    public long getId() {
        return id;
    }

    public GameRecord setId(long id) {
        this.id = id;
        return this;
    }

    public com.code.server.constant.game.GameRecord getGameRecord() {
        return gameRecord;
    }

    public GameRecord setGameRecord(com.code.server.constant.game.GameRecord gameRecord) {
        this.gameRecord = gameRecord;
        return this;
    }

    public long getUuid() {
        return uuid;
    }

    public GameRecord setUuid(long uuid) {
        this.uuid = uuid;
        return this;
    }

    public Date getDate() {
        return date;
    }

    public GameRecord setDate(Date date) {
        this.date = date;
        return this;
    }

    public int getLeftCount() {
        return leftCount;
    }

    public GameRecord setLeftCount(int leftCount) {
        this.leftCount = leftCount;
        return this;
    }

    public long getReplayId() {
        return replayId;
    }

    public GameRecord setReplayId(long replayId) {
        this.replayId = replayId;
        return this;
    }
}
