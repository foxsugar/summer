package com.code.server.constant.db;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/6/3.
 */
public class GameNumData {
    private Map<String, String> info = new HashMap<>();

    public Map<String, String> getInfo() {
        return info;
    }

    public GameNumData setInfo(Map<String, String> info) {
        this.info = info;
        return this;
    }
}
