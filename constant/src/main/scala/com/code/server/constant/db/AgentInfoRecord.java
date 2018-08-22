package com.code.server.constant.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by dajuejinxian on 2018/8/22.
 */
public class AgentInfoRecord {

    //清算记录
    List<Map<String, Object>> clearingRecord = new ArrayList<>();

    public List<Map<String, Object>> getClearingRecord() {
        return clearingRecord;
    }

    public void setClearingRecord(List<Map<String, Object>> clearingRecord) {
        this.clearingRecord = clearingRecord;
    }
}
