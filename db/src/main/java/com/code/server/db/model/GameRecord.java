package com.code.server.db.model;

import com.code.server.constant.game.Record;
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
        indexes = {@Index(name = "room_uuid", columnList = "room_uuid")})
public class GameRecord {

    @Id
    private long id;

    private long room_uuid;

    private Date date;

    private int leftCount;

    @Type(type = "json")
    @Lob
    @Column(columnDefinition = "json")
    private Record.GameRecord gameRecord;


    public long getId() {
        return id;
    }

    public GameRecord setId(long id) {
        this.id = id;
        return this;
    }

    public Record.GameRecord getGameRecord() {
        return gameRecord;
    }

    public GameRecord setGameRecord(Record.GameRecord gameRecord) {
        this.gameRecord = gameRecord;
        return this;
    }

    public long getRoom_uuid() {
        return room_uuid;
    }

    public GameRecord setRoom_uuid(long room_uuid) {
        this.room_uuid = room_uuid;
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
}
