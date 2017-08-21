package com.code.server.constant.kafka;

/**
 * Created by sunxianping on 2017/5/27.
 */
public class KickUser implements IKafkaMsg {
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    private long id;
}
