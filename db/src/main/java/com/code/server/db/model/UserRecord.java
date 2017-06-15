package com.code.server.db.model;

import com.code.server.constant.game.Record;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import javax.persistence.*;


@DynamicUpdate
@Entity
@Table(name = "user_record",
        indexes = {@Index(name = "userId", columnList = "userId")})
public class UserRecord {
    @Id
    private long userId;

    @Type(type = "json")
    @Lob
    @Column(columnDefinition = "json")
    private Record record = new Record();


    public long getUserId() {
        return userId;
    }

    public UserRecord setUserId(long userId) {
        this.userId = userId;
        return this;
    }

    public Record getRecord() {
        return record;
    }

    public UserRecord setRecord(Record record) {
        this.record = record;
        return this;
    }
}
