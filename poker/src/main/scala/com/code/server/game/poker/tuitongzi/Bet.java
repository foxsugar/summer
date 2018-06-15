package com.code.server.game.poker.tuitongzi;

public class Bet
{
    public static final int Wu = 1;
    public static final int SHI = 2;
    public static final int SHI_WU = 3;
    public static final int ER_SHI = 4;
    public static final int GUO_BAN = 5;
    public static final int MAN_ZHU = 6;
    public static final int STATE_FREE_BET = 1000; //自由下注 从 1000开始 比如 1009 就是 9 注

    private int zhu;
    public int getZhu() {
        return zhu;
    }
    public void setZhu(int zhu) {
        this.zhu = zhu;
    }

}

