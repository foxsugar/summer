package com.code.server.login.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dajuejinxian on 2018/5/16.
 */
public class OneLevelVo {

    private String categoryName;

    @JsonProperty("totalMoney")
    private double money;
    @JsonProperty("totalGold")
    private double gold;

    public double getGold() {
        return gold;
    }

    public void setGold(double gold) {
        this.gold = gold;
    }

    @JsonProperty("items")
    private List<OneLevelInfoVo> list = new ArrayList<>();

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public List<OneLevelInfoVo> getList() {
        return list;
    }

    public void setList(List<OneLevelInfoVo> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "OneLevelVo{" +
                "categoryName='" + categoryName + '\'' +
                ", money=" + money +
                ", gold=" + gold +
                ", list=" + list +
                '}';
    }
}
