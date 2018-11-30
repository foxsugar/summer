package com.code.server.constant.club;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sunxianping on 2018-11-28.
 */
public class UpScoreInfo {
    private Map<String, List<UpScoreItem>> info = new HashMap<>();


    public Map<String, List<UpScoreItem>> getInfo() {
        return info;
    }

    public UpScoreInfo setInfo(Map<String, List<UpScoreItem>> info) {
        this.info = info;
        return this;
    }
}
