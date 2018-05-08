package com.code.server.constant.response;

/**
 * Created by sunxianping on 2018/4/23.
 */
public class RoomDoudizhuVo extends RoomVo {
    public int jiaoScoreMax;
    public int shuanglong;

    public int getJiaoScoreMax() {
        return jiaoScoreMax;
    }

    public RoomDoudizhuVo setJiaoScoreMax(int jiaoScoreMax) {
        this.jiaoScoreMax = jiaoScoreMax;
        return this;
    }

    public int getShuanglong() {
        return shuanglong;
    }

    public RoomDoudizhuVo setShuanglong(int shuanglong) {
        this.shuanglong = shuanglong;
        return this;
    }
}
