package com.code.server.db.model;

import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by sunxianping on 2019-04-29.
 */

@DynamicUpdate
@Entity
@Table(name = "upScoreRecord")//上下分记录
public class UpScoreRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;//id

    private Date date;//日期

    private String roomId;//房间id


    public long getId() {
        return id;
    }

    public UpScoreRecord setId(long id) {
        this.id = id;
        return this;
    }

    public Date getDate() {
        return date;
    }

    public UpScoreRecord setDate(Date date) {
        this.date = date;
        return this;
    }

    public String getRoomId() {
        return roomId;
    }

    public UpScoreRecord setRoomId(String roomId) {
        this.roomId = roomId;
        return this;
    }
}
