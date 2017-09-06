package com.code.server.db.model;

import com.code.server.constant.game.Record;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

/**
 * Created by sunxianping on 2017/9/6.
 */

@DynamicUpdate
@Entity
public class UserRoomRecord {

    @Id
    private long id;

    @Type(type = "json")
    @Lob
    @Column(columnDefinition = "json")
    private Record record = new Record();


    public long getId() {
        return id;
    }

    public UserRoomRecord setId(long id) {
        this.id = id;
        return this;
    }

    public Record getRecord() {
        return record;
    }

    public UserRoomRecord setRecord(Record record) {
        this.record = record;
        return this;
    }
}
