package com.code.server.db.model;

/**
 * Created by sunxianping on 2017/4/1.
 */
public class Model {
    public Model(){}

    private int id;
    private String name;

    public int getId() {
        return id;
    }

    public Model setId(int id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Model setName(String name) {
        this.name = name;
        return this;
    }
}
