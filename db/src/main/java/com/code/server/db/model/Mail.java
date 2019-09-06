package com.code.server.db.model;

import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/**
 * Created by sunxianping on 2019-09-06.
 */
@DynamicUpdate
@Entity
@Table(name = "mail")
public class Mail {

    @Id
    private String id;

    private long userId;
    private long mailDate;
    private int mailType;
    private int isRead;

    @Column(columnDefinition = "varchar(4000)")
    private String content;

    public String getId() {
        return id;
    }

    public Mail setId(String id) {
        this.id = id;
        return this;
    }

    public long getUserId() {
        return userId;
    }

    public Mail setUserId(long userId) {
        this.userId = userId;
        return this;
    }


    public int getMailType() {
        return mailType;
    }

    public Mail setMailType(int mailType) {
        this.mailType = mailType;
        return this;
    }

    public String getContent() {
        return content;
    }

    public Mail setContent(String content) {
        this.content = content;
        return this;
    }

    public long getMailDate() {
        return mailDate;
    }

    public Mail setMailDate(long mailDate) {
        this.mailDate = mailDate;
        return this;
    }

    public int getIsRead() {
        return isRead;
    }

    public Mail setIsRead(int isRead) {
        this.isRead = isRead;
        return this;
    }
}


