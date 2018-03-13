package com.code.server.db.model;

import com.code.server.db.utils.BaseEntity;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunxianping on 2018/3/13.
 */

@DynamicUpdate
@Entity
public class GameAgent extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private double rebate;


    private long fatherId;

    @Type(type = "json")
    @Lob
    @Column(columnDefinition = "json")
    private List<Long> childList = new ArrayList<>();

    public long getId() {
        return id;
    }

    public GameAgent setId(long id) {
        this.id = id;
        return this;
    }

    public double getRebate() {
        return rebate;
    }

    public GameAgent setRebate(double rebate) {
        this.rebate = rebate;
        return this;
    }

    public long getFatherId() {
        return fatherId;
    }

    public GameAgent setFatherId(long fatherId) {
        this.fatherId = fatherId;
        return this;
    }

    public List<Long> getChildList() {
        return childList;
    }

    public GameAgent setChildList(List<Long> childList) {
        this.childList = childList;
        return this;
    }
}
