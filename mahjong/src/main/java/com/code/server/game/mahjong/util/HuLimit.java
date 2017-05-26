package com.code.server.game.mahjong.util;

/**
 * Created by win7 on 2016/12/9.
 */
public class HuLimit {

    public boolean isLimitFan;
    public int fan;

    public HuLimit( boolean isLimitFan, int fan) {

        this.isLimitFan = isLimitFan;
        this.fan = fan;
    }

    public HuLimit(int fan) {
        this.isLimitFan = true;
        this.fan = fan;
    }
}
