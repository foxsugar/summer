package com.code.server.login.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dajuejinxian on 2018/5/29.
 */
public class ThreeLevelVo {

    private String categoryName;

    @JsonProperty("totalMoney")
    private double money;

    @JsonProperty("items")
    private List<ThreeLevelInfoVo> list = new ArrayList<>();

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

    public List<ThreeLevelInfoVo> getList() {
        return list;
    }

    public void setList(List<ThreeLevelInfoVo> list) {
        this.list = list;
    }
}
