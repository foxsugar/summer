package com.code.server.game.poker.cow;

import org.junit.Assert;
import org.junit.Test;

public class CowPlayerTest {

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

        CowPlayer p1 = new CowPlayer(1L, 0,1,2,3,4);
        CowPlayer p2 = new CowPlayer(2L, 5,6,7,8,9);
        CowPlayer p = CowCardUtils.findWinner(p1, p2);
        Assert.assertTrue(p == p1);

    }

    @Test
    public void findWinner2() throws Exception {

        CowPlayer p1 = new CowPlayer(1L, 1,2,3,4,5, false);
        CowPlayer p2 = new CowPlayer(2L, 10,6,7,8,9, false);
        CowPlayer p = CowCardUtils.findWinner(p1, p2);
        Assert.assertTrue(p == p1);

    }

    @Test
    public void findWinner3() throws Exception {

        CowPlayer p1 = new CowPlayer(0L, 0,36,40,44,48, true);
        CowPlayer p2 = new CowPlayer(2L, 1,37,41,45,49, true);
        CowPlayer p = CowCardUtils.findWinner(p1, p2);
        Assert.assertTrue(p == p1);

    }

    @Test
    public void findWinner4() throws Exception {

        CowPlayer p1 = new CowPlayer(0L, 0,36,40,44,48, true);
        CowPlayer p2 = new CowPlayer(2L, 1,37,41,45,50, true);
        CowPlayer p = CowCardUtils.findWinner(p1, p2);
        Assert.assertTrue(p == p1);

    }

     //炸弹
    @Test
    public void findWinner5() throws Exception {

        CowPlayer p1 = new CowPlayer(0L, 0,1,2,3,4, true);
        CowPlayer p2 = new CowPlayer(1L, 8,9,10,11,12, true);
        CowPlayer p = CowCardUtils.findWinner(p1, p2);
        Assert.assertTrue(p == p1);

    }

    //五花牛
    @Test
    public void findWinner6() throws Exception {

        CowPlayer p1 = new CowPlayer(0L, 4,5,8,9,12, true);
        CowPlayer p2 = new CowPlayer(1L, 6,7,10,11,13, true);
        CowPlayer p = CowCardUtils.findWinner(p1, p2);
        Assert.assertTrue(p == p1);

    }

    //葫芦
    @Test
    public void findWinner7() throws Exception {

        CowPlayer p1 = new CowPlayer(0L, 0,1,2,8,9, true);
        CowPlayer p2 = new CowPlayer(1L, 4,5,6,10,11, true);
        CowPlayer p = CowCardUtils.findWinner(p1, p2);
        Assert.assertTrue(p == p1);

    }

    //同花
    @Test
    public void findWinner8() throws Exception {

        CowPlayer p1 = new CowPlayer(0L, 0,4,12,28,32, true);
        CowPlayer p2 = new CowPlayer(1L, 1,5,9,13,25, true);
        CowPlayer p = CowCardUtils.findWinner(p1, p2);
        Assert.assertTrue(p == p1);

    }

    //顺子
    @Test
    public void findWinner9() throws Exception {

        CowPlayer p1 = new CowPlayer(0L, 0,4,8,12,17, true);
        CowPlayer p2 = new CowPlayer(1L, 1,5,9,13,19, true);
        CowPlayer p = CowCardUtils.findWinner(p1, p2);
        Assert.assertTrue(p == p1);

    }

    //牛牛
    @Test
    public void findWinner10() throws Exception {

        CowPlayer p1 = new CowPlayer(0L, 0,4,8,12,17, true);
        CowPlayer p2 = new CowPlayer(1L, 1,5,9,13,19, true);
        CowPlayer p = CowCardUtils.findWinner(p1, p2);
        Assert.assertTrue(p == p1);

    }
}