package com.code.server.constant.response;

/**
 * Created by sunxianping on 2017/8/17.
 */
public class RoomPaijiuVo extends RoomVo {
    //庄家设置的分
    public int bankerScore = 0;
    public int bankerInitScore = 0;
    public long bankerId = 0;

    public int getBankerScore() {
        return bankerScore;
    }

    public RoomPaijiuVo setBankerScore(int bankerScore) {
        this.bankerScore = bankerScore;
        return this;
    }

    public int getBankerInitScore() {
        return bankerInitScore;
    }

    public RoomPaijiuVo setBankerInitScore(int bankerInitScore) {
        this.bankerInitScore = bankerInitScore;
        return this;
    }

    public long getBankerId() {
        return bankerId;
    }

    public RoomPaijiuVo setBankerId(long bankerId) {
        this.bankerId = bankerId;
        return this;
    }
}
