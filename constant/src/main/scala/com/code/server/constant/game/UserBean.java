package com.code.server.constant.game;

import com.code.server.constant.db.UserInfo;
import com.code.server.constant.response.UserVo;

import java.util.Date;

/**
 * Created by sunxianping on 2017/5/27.
 */
public class UserBean implements IUserBean {
    private long id;
    private String username;
    private String image;
    private String seatId;
    private String account;
    private String password;
    private String ipConfig;
    private double money;
    private double gold;
    private String roomId;
    private int vip;
    private String uuid;
    private String openId;
    private int sex;
    private int referee;
    private UserInfo userInfo;
    private String coord = "";

    private Date registDate;
    private Date lastLoginDate;
    private String unionId;

    @Override
    public UserVo toVo() {
        UserVo userVo = new UserVo();
        userVo.setId(this.id);
        userVo.setUsername(this.username);// required
        userVo.setImage(this.image);// required
        userVo.setSeatId(this.seatId);// required
        userVo.setAccount(this.account);// required
        userVo.setIpConfig(this.ipConfig);// required
        userVo.setMoney(this.money);// required
        userVo.setRoomId(this.roomId);// required
        userVo.setVip(this.vip);//required
        userVo.setUuid(this.uuid);// required
        userVo.setOpenId(this.openId);// required
        userVo.setSex(this.sex);//required
        userVo.setReferee(this.referee);
        userVo.setUserInfo(this.userInfo);
        userVo.setGold(this.getGold());
        userVo.setCoord(coord);
        return userVo;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSeatId() {
        return seatId;
    }

    public void setSeatId(String seatId) {
        this.seatId = seatId;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIpConfig() {
        return ipConfig;
    }

    public void setIpConfig(String ipConfig) {
        this.ipConfig = ipConfig;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public double getGold() {
        return gold;
    }

    public void setGold(double gold) {
        this.gold = gold;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public int getVip() {
        return vip;
    }

    public void setVip(int vip) {
        this.vip = vip;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getReferee() {
        return referee;
    }

    public void setReferee(int referee) {
        this.referee = referee;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public String getCoord() {
        return coord;
    }

    public UserBean setCoord(String coord) {
        this.coord = coord;
        return this;
    }

    public Date getLastLoginDate() {
        return lastLoginDate;
    }

    public UserBean setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
        return this;
    }

    public Date getRegistDate() {
        return registDate;
    }

    public UserBean setRegistDate(Date registDate) {
        this.registDate = registDate;
        return this;
    }

    public String getUnionId() {
        return unionId;
    }

    public UserBean setUnionId(String unionId) {
        this.unionId = unionId;
        return this;
    }
}
