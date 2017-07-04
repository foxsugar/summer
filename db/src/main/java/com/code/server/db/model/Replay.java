package com.code.server.db.model;

import com.code.server.db.utils.BaseEntity;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/**
 * Created by Administrator on 2017/7/3.
 */
@DynamicUpdate
@Entity
@Table(name = "replay",
        indexes = {@Index(name = "id", columnList = "id")})
public class Replay extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Lob
    @Column(columnDefinition = "longtext")
    private String data;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    private int count;

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
}
