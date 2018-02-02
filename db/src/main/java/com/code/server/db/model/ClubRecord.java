package com.code.server.db.model;

import com.code.server.constant.game.RoomRecord;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunxianping on 2018/1/29.
 */

@DynamicUpdate
@Entity
@Table(name = "club_record",
        indexes = {@Index(name = "id", columnList = "id")})
public class ClubRecord {

    @Id
    private String id;



    @Type(type = "json")
    @Lob
    @Column(columnDefinition = "json")
    private List<RoomRecord> records= new ArrayList<>();

    public String getId() {
        return id;
    }

    public ClubRecord setId(String id) {
        this.id = id;
        return this;
    }

    public List<RoomRecord> getRecords() {
        return records;
    }

    public ClubRecord setRecords(List<RoomRecord> records) {
        this.records = records;
        return this;
    }
}
