package com.code.server.game.cow;

import org.junit.Assert;
import org.junit.Test;

public class NiuNiuPlayerTest {

//    public final static int TONG_HUA_SHUN = 1;
//    public final static int ZHA_DAN_NIU = 2;
//    public final static int WU_HUA_NIU = 3;
//    public final static int WU_XIAO_NIU = 4;
//    public final static int HU_LU = 5;
//    public final static int TONG_HUA = 6;
//    public final static int SHUN_ZI = 7;
//    public final static int NIU_NIUI = 8;
//    public final static int NIU_JIU  = 9;
//    public final static int NIU_BA = 10;
//    public final static int NIU_QI = 11;
//    public final static int NIU_Liu = 12;
//    public final static int NIIU_WU = 13;
//    public final static int NIU_SI = 14;
//    public final static int NIU_SAN = 15;
//    public final static int NIU_ER = 16;
//    public final static int NIU_YI = 17;
//    public final static int WU_NIU = 18;

    @Test
    public void compareWithOtherPlayer() {
    }

    @Test
    public void compare() {
    }

    @Test
    public void findWinner() throws Exception {

        NiuNiuPlayer p1 = new NiuNiuPlayer(1, 0,1,2,3,4, true);
        NiuNiuPlayer p2 = new NiuNiuPlayer(2, 5,6,7,8,9, true);
        NiuNiuPlayer p = CardUtils.findWinner(p1, p2);
        Assert.assertTrue(p == p1);

    }

    @Test
    public void findWinner2() throws Exception {

        NiuNiuPlayer p1 = new NiuNiuPlayer(1, 1,2,3,4,5, false);
        NiuNiuPlayer p2 = new NiuNiuPlayer(2, 10,6,7,8,9, false);
        NiuNiuPlayer p = CardUtils.findWinner(p1, p2);
        Assert.assertTrue(p == p1);

    }

    @Test
    public void findWinner3() throws Exception {

        NiuNiuPlayer p1 = new NiuNiuPlayer(0, 0,36,40,44,48, true);
        NiuNiuPlayer p2 = new NiuNiuPlayer(2, 1,37,41,45,49, true);
        NiuNiuPlayer p = CardUtils.findWinner(p1, p2);
        Assert.assertTrue(p == p1);

    }

    @Test
    public void findWinner4() throws Exception {

        NiuNiuPlayer p1 = new NiuNiuPlayer(0, 0,36,40,44,48, true);
        NiuNiuPlayer p2 = new NiuNiuPlayer(2, 1,37,41,45,50, true);
        NiuNiuPlayer p = CardUtils.findWinner(p1, p2);
        Assert.assertTrue(p == p1);

    }

     //炸弹
    @Test
    public void findWinner5() throws Exception {

        NiuNiuPlayer p1 = new NiuNiuPlayer(0, 0,1,2,3,4, true);
        NiuNiuPlayer p2 = new NiuNiuPlayer(1, 8,9,10,11,12, true);
        NiuNiuPlayer p = CardUtils.findWinner(p1, p2);
        Assert.assertTrue(p == p1);

    }

    //五花牛
    @Test
    public void findWinner6() throws Exception {

        NiuNiuPlayer p1 = new NiuNiuPlayer(0, 4,5,8,9,12, true);
        NiuNiuPlayer p2 = new NiuNiuPlayer(1, 6,7,10,11,13, true);
        NiuNiuPlayer p = CardUtils.findWinner(p1, p2);
        Assert.assertTrue(p == p1);

    }

    //葫芦
    @Test
    public void findWinner7() throws Exception {

        NiuNiuPlayer p1 = new NiuNiuPlayer(0, 0,1,2,8,9, true);
        NiuNiuPlayer p2 = new NiuNiuPlayer(1, 4,5,6,10,11, true);
        NiuNiuPlayer p = CardUtils.findWinner(p1, p2);
        Assert.assertTrue(p == p1);

    }

    //同花
    @Test
    public void findWinner8() throws Exception {

        NiuNiuPlayer p1 = new NiuNiuPlayer(0, 0,4,12,28,32, true);
        NiuNiuPlayer p2 = new NiuNiuPlayer(1, 1,5,9,13,25, true);
        NiuNiuPlayer p = CardUtils.findWinner(p1, p2);
        Assert.assertTrue(p == p1);

    }

    //顺子
    @Test
    public void findWinner9() throws Exception {

        NiuNiuPlayer p1 = new NiuNiuPlayer(0, 0,4,8,12,17, true);
        NiuNiuPlayer p2 = new NiuNiuPlayer(1, 1,5,9,13,19, true);
        NiuNiuPlayer p = CardUtils.findWinner(p1, p2);
        Assert.assertTrue(p == p1);

    }

    //牛牛
    @Test
    public void findWinner10() throws Exception {

        NiuNiuPlayer p1 = new NiuNiuPlayer(0, 0,4,8,12,17, true);
        NiuNiuPlayer p2 = new NiuNiuPlayer(1, 1,5,9,13,19, true);
        NiuNiuPlayer p = CardUtils.findWinner(p1, p2);
        Assert.assertTrue(p == p1);

    }
}