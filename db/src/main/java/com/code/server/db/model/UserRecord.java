package com.code.server.db.model;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import javax.persistence.*;


@DynamicUpdate
@Entity
@Table(name = "user_record",
        indexes = {@Index(name = "userId", columnList = "userId")})
public class UserRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long userId;

    @Type(type = "json")
    @Lob
    @Column(columnDefinition = "longtext")
    private Record record = new Record();
}
