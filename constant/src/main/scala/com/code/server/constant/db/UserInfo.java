package com.code.server.constant.db;

import com.code.server.constant.club.ScoreItem;
import com.code.server.constant.game.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private String phone;
    private String bankCard;
    private String bankName;
    private Map<String, Integer> playGameNum = new HashMap<>();
    private int coupon = 0;
    private Map<String, Map<Integer, Integer>> taskComplete = new HashMap<>();
    private List<Message> messageBox = new ArrayList<>();
    private  Map<Long, ScoreItem> rebate = new HashMap<>();
    private double allRebate;
    private long newMessageNum = 0;



    public UserInfo toVoSimple(){
        UserInfo userInfo = new UserInfo();
        return userInfo;

    }

    public void handleData(){
        this.newMessageNum = messageBox.stream().filter(message -> !message.isRead()).count();

    }

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

    public Map<String, Integer> getPlayGameNum() {
        return playGameNum;
    }

    public UserInfo setPlayGameNum(Map<String, Integer> playGameNum) {
        this.playGameNum = playGameNum;
        return this;
    }

    public int getCoupon() {
        return coupon;
    }

    public UserInfo setCoupon(int coupon) {
        this.coupon = coupon;
        return this;
    }

    public Map<String, Map<Integer, Integer>> getTaskComplete() {
        return taskComplete;
    }

    public UserInfo setTaskComplete(Map<String, Map<Integer, Integer>> taskComplete) {
        this.taskComplete = taskComplete;
        return this;
    }

    public List<Message> getMessageBox() {
        return messageBox;
    }

    public UserInfo setMessageBox(List<Message> messageBox) {
        this.messageBox = messageBox;
        return this;
    }

    public Map<Long, ScoreItem> getRebate() {
        return rebate;
    }

    public UserInfo setRebate(Map<Long, ScoreItem> rebate) {
        this.rebate = rebate;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public UserInfo setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getBankCard() {
        return bankCard;
    }

    public UserInfo setBankCard(String bankCard) {
        this.bankCard = bankCard;
        return this;
    }

    public String getBankName() {
        return bankName;
    }

    public UserInfo setBankName(String bankName) {
        this.bankName = bankName;
        return this;
    }

    public double getAllRebate() {
        return allRebate;
    }

    public UserInfo setAllRebate(double allRebate) {
        this.allRebate = allRebate;
        return this;
    }

    public long getNewMessageNum() {
        return newMessageNum;
    }

    public UserInfo setNewMessageNum(long newMessageNum) {
        this.newMessageNum = newMessageNum;
        return this;
    }
}
