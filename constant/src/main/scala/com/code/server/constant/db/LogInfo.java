package com.code.server.constant.db;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sunxianping on 2018/6/27.
 */
public class LogInfo {

    private Map<String, Double> chargeInfo = new HashMap<>();

    private int registerUser = 0;

    private int registerAgent = 0;

    private double takeOutNum = 0;

    public Map<String, Double> getChargeInfo() {
        return chargeInfo;
    }

    public LogInfo setChargeInfo(Map<String, Double> chargeInfo) {
        this.chargeInfo = chargeInfo;
        return this;
    }

    public int getRegisterUser() {
        return registerUser;
    }

    public LogInfo setRegisterUser(int registerUser) {
        this.registerUser = registerUser;
        return this;
    }

    public int getRegisterAgent() {
        return registerAgent;
    }

    public LogInfo setRegisterAgent(int registerAgent) {
        this.registerAgent = registerAgent;
        return this;
    }

    public double getTakeOutNum() {
        return takeOutNum;
    }

    public LogInfo setTakeOutNum(double takeOutNum) {
        this.takeOutNum = takeOutNum;
        return this;
    }
}
