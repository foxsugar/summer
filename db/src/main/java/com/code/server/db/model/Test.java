package com.code.server.db.model;

import com.code.server.db.utils.BaseEntity;
import org.hibernate.annotations.Type;

import javax.persistence.*;

/**
 * Created by sunxianping on 2017/4/1.
 */
@Entity
@Table(name = "test")
public class Test extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Type(type = "json")
    @Lob
    @Column(columnDefinition = "longtext")
    private Model location;



    public Model getLocation() {
        return location;
    }

    public Test setLocation(Model location) {
        this.location = location;
        return this;
    }

    public int getId() {
        return id;
    }

    public Test setId(int id) {
        this.id = id;
        return this;
    }
}
