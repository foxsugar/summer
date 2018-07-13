package com.code.server.login.vo;

/**
 * Created by dajuejinxian on 2018/5/16.
 */
public class OneLevelInfoVo {

    private long uid;

    private String categoryName;

    private String image;

    private String username;

    private String money;

    private String gold;

    public String getGold() {
        return gold;
    }

    public void setGold(String gold) {
        this.gold = gold;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    @Override
    public String toString() {
        return "OneLevelInfoVo{" +
                "uid=" + uid +
                ", categoryName='" + categoryName + '\'' +
                ", image='" + image + '\'' +
                ", username='" + username + '\'' +
                ", money='" + money + '\'' +
                ", gold='" + gold + '\'' +
                '}';
    }
}
