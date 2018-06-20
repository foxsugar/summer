package com.code.server.login.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dajuejinxian on 2018/5/29.
 */
public class HomeChargeVo {

    private String onelevel;
    private String twoLevel;
    private String threeLevel;

    private String oneLevelGold;
    private String twoLevelGold;
    private String threeLevelGold;

    private String totalGold;
    private String total;
    private String start;
    private String end;

    @JsonProperty("list1")
    private List<OneLevelInfoVo> oneLevelVoList = new ArrayList<>();
    @JsonProperty("list2")
    private List<TwoLevelInfoVo> twoLevelInfoVoList = new ArrayList<>();
    @JsonProperty("list3")
    private List<ThreeLevelInfoVo> threeLevelInfoVoList = new ArrayList<>();


    public String getTotalGold() {
        return totalGold;
    }

    public void setTotalGold(String totalGold) {
        this.totalGold = totalGold;
    }

    public String getOneLevelGold() {
        return oneLevelGold;
    }

    public void setOneLevelGold(String oneLevelGold) {
        this.oneLevelGold = oneLevelGold;
    }

    public String getTwoLevelGold() {
        return twoLevelGold;
    }

    public void setTwoLevelGold(String twoLevelGold) {
        this.twoLevelGold = twoLevelGold;
    }

    public String getThreeLevelGold() {
        return threeLevelGold;
    }

    public void setThreeLevelGold(String threeLevelGold) {
        this.threeLevelGold = threeLevelGold;
    }

    public List<OneLevelInfoVo> getOneLevelVoList() {
        return oneLevelVoList;
    }

    public void setOneLevelVoList(List<OneLevelInfoVo> oneLevelVoList) {
        this.oneLevelVoList = oneLevelVoList;
    }

    public List<TwoLevelInfoVo> getTwoLevelInfoVoList() {
        return twoLevelInfoVoList;
    }

    public void setTwoLevelInfoVoList(List<TwoLevelInfoVo> twoLevelInfoVoList) {
        this.twoLevelInfoVoList = twoLevelInfoVoList;
    }

    public List<ThreeLevelInfoVo> getThreeLevelInfoVoList() {
        return threeLevelInfoVoList;
    }

    public void setThreeLevelInfoVoList(List<ThreeLevelInfoVo> threeLevelInfoVoList) {
        this.threeLevelInfoVoList = threeLevelInfoVoList;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getOnelevel() {
        return onelevel;
    }

    public void setOnelevel(String onelevel) {
        this.onelevel = onelevel;
    }

    public String getTwoLevel() {
        return twoLevel;
    }

    public void setTwoLevel(String twoLevel) {
        this.twoLevel = twoLevel;
    }

    public String getThreeLevel() {
        return threeLevel;
    }

    public void setThreeLevel(String threeLevel) {
        this.threeLevel = threeLevel;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "HomeChargeVo{" +
                "onelevel='" + onelevel + '\'' +
                ", twoLevel='" + twoLevel + '\'' +
                ", threeLevel='" + threeLevel + '\'' +
                ", total='" + total + '\'' +
                ", start='" + start + '\'' +
                ", end='" + end + '\'' +
                ", oneLevelVoList=" + oneLevelVoList +
                ", twoLevelInfoVoList=" + twoLevelInfoVoList +
                ", threeLevelInfoVoList=" + threeLevelInfoVoList +
                '}';
    }
}
