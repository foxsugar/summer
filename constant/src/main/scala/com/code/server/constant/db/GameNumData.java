package com.code.server.constant.db;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/6/3.
 */
public class GameNumData {
    private Map<String, Integer> info = new HashMap<>();

    public Map<String, Integer> getInfo() {
        return info;
    }

    public GameNumData setInfo(Map<String, Integer> info) {
        this.info = info;
        return this;
    }
}
