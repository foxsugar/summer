package com.code.server.constant.club;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sunxianping on 2018/1/30.
 */
public class Statistics {

    private Map<String, ClubStatistics> statistics = new HashMap<>();


    private double consume;



    public Map<String, ClubStatistics> getStatistics() {
        return statistics;
    }

    public Statistics setStatistics(Map<String, ClubStatistics> statistics) {
        this.statistics = statistics;
        return this;
    }
}
