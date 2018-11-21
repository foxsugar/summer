package com.code.server.constant.db;

/**
 * Created by sunxianping on 2017/5/26.
 */
public class UserInfo {
    private int totalPlayGameNumber;

    private int playGameTime = 0;
    private int shareWXCount = 0;
    private long lastShareTime = 0;
    private boolean isInputAccessCode = false;
    private boolean isHasAppleCharge = false;
    private double chargeMoneyNum = 0;
    private double chargeGoldNum = 0;
    private String name;
    private String idCard;

    public int getTotalPlayGameNumber() {
        return totalPlayGameNumber;
    }

    public UserInfo setTotalPlayGameNumber(int totalPlayGameNumber) {
        this.totalPlayGameNumber = totalPlayGameNumber;
        return this;
    }

    public int getPlayGameTime() {
        return playGameTime;
    }

    public UserInfo setPlayGameTime(int playGameTime) {
        this.playGameTime = playGameTime;
        return this;
    }

    public int getShareWXCount() {
        return shareWXCount;
    }

    public UserInfo setShareWXCount(int shareWXCount) {
        this.shareWXCount = shareWXCount;
        return this;
    }

    public long getLastShareTime() {
        return lastShareTime;
    }

    public UserInfo setLastShareTime(long lastShareTime) {
        this.lastShareTime = lastShareTime;
        return this;
    }

    public boolean isInputAccessCode() {
        return isInputAccessCode;
    }

    public UserInfo setInputAccessCode(boolean inputAccessCode) {
        isInputAccessCode = inputAccessCode;
        return this;
    }

    public boolean isHasAppleCharge() {
        return isHasAppleCharge;
    }

    public UserInfo setHasAppleCharge(boolean hasAppleCharge) {
        isHasAppleCharge = hasAppleCharge;
        return this;
    }

    public double getChargeMoneyNum() {
        return chargeMoneyNum;
    }

    public UserInfo setChargeMoneyNum(double chargeMoneyNum) {
        this.chargeMoneyNum = chargeMoneyNum;
        return this;
    }

    public double getChargeGoldNum() {
        return chargeGoldNum;
    }

    public UserInfo setChargeGoldNum(double chargeGoldNum) {
        this.chargeGoldNum = chargeGoldNum;
        return this;
    }

    public String getName() {
        return name;
    }

    public UserInfo setName(String name) {
        this.name = name;
        return this;
    }

    public String getIdCard() {
        return idCard;
    }

    public UserInfo setIdCard(String idCard) {
        this.idCard = idCard;
        return this;
    }
}
