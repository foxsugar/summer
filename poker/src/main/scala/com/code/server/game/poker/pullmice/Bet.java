package com.code.server.game.poker.pullmice;

public class Bet {

    public static final int YI = 1;
    public static final int ER = 2;
    public static final int SAN = 3;
    public static final int SI = 4;
    public static final int WU = 5;
    //封
    public static final int FENG = 6;

    public static final int WU_BU_FENG = 7;

    //跑
    public static final int ESCAPE = 8;
    //跟注
    public static final int FOLLOW = 9;

    private int zhu;

    public int getZhu() {
        return zhu;
    }

    public void setZhu(int zhu) {
        this.zhu = zhu;
    }
}
