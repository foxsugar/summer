package com.code.server.login.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import jdk.nashorn.internal.objects.annotations.Property;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dajuejinxian on 2018/5/15.
 */

public class TwoLevelVo {

    private String categoryName;

    @JsonProperty("totalMoney")
    private double money;

    @JsonProperty("items")
    private List<TwoLevelInfoVo> list = new ArrayList<>();

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

    public List<TwoLevelInfoVo> getList() {
        return list;
    }

    public void setList(List<TwoLevelInfoVo> list) {
        this.list = list;
    }
}
