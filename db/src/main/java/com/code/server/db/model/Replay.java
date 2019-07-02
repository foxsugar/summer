package com.code.server.db.model;

import com.code.server.db.utils.BaseEntity;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Administrator on 2017/7/3.
 */
@DynamicUpdate
@Entity
@Table(name = "replay",
        indexes = {
            @Index(name = "id", columnList = "id"),
            @Index(name = "date", columnList = "date"),
            @Index(name = "leftCount", columnList = "leftCount"),
        }
        )
public class Replay extends BaseEntity {
    @Id
    private long id;//id

    @Lob
    @Column(columnDefinition = "longtext")
    private String data;//回放数据

    private Date date;//日期

    private long roomUuid;//房间uuid

    private int leftCount;//计数

    public int getLeftCount() {
        return leftCount;
    }

    public void setLeftCount(int leftCount) {
        this.leftCount = leftCount;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public long getRoomUuid() {
        return roomUuid;
    }

    public Replay setRoomUuid(long roomUuid) {
        this.roomUuid = roomUuid;
        return this;
    }

    public Date getDate() {
        return date;
    }

    public Replay setDate(Date date) {
        this.date = date;
        return this;
    }
}
