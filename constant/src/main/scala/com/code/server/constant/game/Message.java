package com.code.server.constant.game;

import java.util.Date;

/**
 * Created by sunxianping on 2019-03-15.
 */
public class Message {
    private long id;
    private String content;
    private Date date;
    private boolean read;

    public String getContent() {
        return content;
    }

    public Message setContent(String content) {
        this.content = content;
        return this;
    }

    public Date getDate() {
        return date;
    }

    public Message setDate(Date date) {
        this.date = date;
        return this;
    }

    public boolean isRead() {
        return read;
    }

    public Message setRead(boolean read) {
        this.read = read;
        return this;
    }

    public long getId() {
        return id;
    }

    public Message setId(long id) {
        this.id = id;
        return this;
    }
}
