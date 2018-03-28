package com.code.server.game.poker.tuitongzi;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TuiTongZiCardUtilsTest {

    @Test
    public void text1() throws Exception {

        PlayerTuiTongZi pA = new PlayerTuiTongZi(1, 32, 33);
        PlayerTuiTongZi pB = new PlayerTuiTongZi(2, 34, 35);
        Boolean ret = TuiTongZiCardUtils.zhuangIsBiggerThanXian(pA, pB);
        Assert.assertNotEquals(false, ret);
    }

    @Test
    public void test2() throws Exception {

        PlayerTuiTongZi pA = new PlayerTuiTongZi(1, 28, 29);
        PlayerTuiTongZi pB = new PlayerTuiTongZi(2, 34, 35);
        Boolean ret = TuiTongZiCardUtils.zhuangIsBiggerThanXian(pA, pB);
        Assert.assertEquals(false, ret);
    }

    @Test
    public void test3() throws Exception {

        PlayerTuiTongZi pA = new PlayerTuiTongZi(1, 28, 29);
        PlayerTuiTongZi pB = new PlayerTuiTongZi(2, 34, 35);
        Boolean ret = TuiTongZiCardUtils.zhuangIsBiggerThanXian(pB, pA);
        Assert.assertEquals(true, ret);
    }

    @Test
    public void test4() throws Exception {

        PlayerTuiTongZi pA = new PlayerTuiTongZi(0, 0, 4);
        PlayerTuiTongZi pB = new PlayerTuiTongZi(2, 8, 12);
        Boolean ret = TuiTongZiCardUtils.zhuangIsBiggerThanXian(pB, pA);
        Assert.assertEquals(true, ret);
    }

    @Test
    public void test5() throws Exception {

        PlayerTuiTongZi pA = new PlayerTuiTongZi(0, 27, 26);
        PlayerTuiTongZi pB = new PlayerTuiTongZi(2, 8, 12);
        Boolean ret = TuiTongZiCardUtils.zhuangIsBiggerThanXian(pB, pA);
        Assert.assertEquals(true, ret);
    }

    @Test
    public void zhuangIsBiggerThanXian() throws Exception {

        PlayerTuiTongZi pA = new PlayerTuiTongZi(0, 15, 34);
        PlayerTuiTongZi pB = new PlayerTuiTongZi(2, 14, 21);
        Boolean ret = TuiTongZiCardUtils.zhuangIsBiggerThanXian(pA, pB);
        Assert.assertEquals(true, ret);
    }
}