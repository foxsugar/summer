package com.code.server.constant.db;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sunxianping on 2018/3/1.
 */
public class OnlineData {
    private Map<String, OnlineInfo> info = new HashMap<>();

    public Map<String, OnlineInfo> getInfo() {
        return info;
    }

    public OnlineData setInfo(Map<String, OnlineInfo> info) {
        this.info = info;
        return this;
    }
}
