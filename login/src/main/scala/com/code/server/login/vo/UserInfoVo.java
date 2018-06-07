package com.code.server.login.vo;

import com.code.server.db.model.User;

/**
 * Created by dajuejinxian on 2018/6/7.
 */
public class UserInfoVo {

    private String image;

    private Long userId;

    private String username;

    private String createTime;

    private String isDelegate;

    //邀请码
    private long referee;
    //玩家总数
    private long userCount;
    //代理总数
    private long delegateCount;
    //可用金额
    private double canUseMoney;
    //
    private double totalMoney;

    private double rebate;

    public double getRebate() {
        return rebate;
    }

    public void setRebate(double rebate) {
        this.rebate = rebate;
    }

    public long getUserCount() {
        return userCount;
    }

    public void setUserCount(long userCount) {
        this.userCount = userCount;
    }

    public long getDelegateCount() {
        return delegateCount;
    }

    public void setDelegateCount(long delegateCount) {
        this.delegateCount = delegateCount;
    }

    public double getCanUseMoney() {
        return canUseMoney;
    }

    public void setCanUseMoney(double canUseMoney) {
        this.canUseMoney = canUseMoney;
    }

    public long getReferee() {
        return referee;
    }

    public void setReferee(long referee) {
        this.referee = referee;
    }

    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getIsDelegate() {
        return isDelegate;
    }

    public void setIsDelegate(String isDelegate) {
        this.isDelegate = isDelegate;
    }

    public double getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(double totalMoney) {
        this.totalMoney = totalMoney;
    }
}
