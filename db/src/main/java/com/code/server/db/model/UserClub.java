package com.code.server.db.model;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sunxianping on 2018/1/16.
 */
@DynamicUpdate
@Entity
@Table(name = "user_club")
public class UserClub {

    @Id
    private int id;

    @Type(type = "json")
    @Lob
    @Column(columnDefinition = "json")
    private Map<String, List<String>> user_club= new HashMap<>();//房间信息

    public int getId() {
        return id;
    }

    public UserClub setId(int id) {
        this.id = id;
        return this;
    }

    public Map<String, List<String>> getUser_club() {
        return user_club;
    }

    public UserClub setUser_club(Map<String, List<String>> user_club) {
        this.user_club = user_club;
        return this;
    }
}
