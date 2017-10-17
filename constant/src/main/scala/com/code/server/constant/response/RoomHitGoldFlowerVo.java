package com.code.server.constant.response;

/**
 * Created by sunxianping on 2017/8/17.
 */
public class RoomHitGoldFlowerVo extends RoomVo {
    public long bankerId = 0;

    public long getBankerId() {
        return bankerId;
    }

    public RoomHitGoldFlowerVo setBankerId(long bankerId) {
        this.bankerId = bankerId;
        return this;
    }
}
