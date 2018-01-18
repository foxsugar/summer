package com.code.server.game.poker.cow;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;


public class CardUtilsTest {

    @Test
    public void getPaiXing() throws Exception {

        List<Integer> list = new ArrayList<>();
        list.add(4);
        list.add(8);
        list.add(12);
        list.add(13);
        list.add(14);

        Integer value = CardUtils.getPaiXing(list);
        System.out.println(value);
    }

    @Test
    public void calculateDianShu() {
    }

    @Test
    public void isTongHuaShun() throws Exception {

        List<Integer> list = new ArrayList<>();

        list.add(0);
        list.add(4);
        list.add(8);
        list.add(12);
        list.add(16);

        Integer value = CardUtils.getPaiXing(list);
        Assert.assertEquals(value.intValue(), CardUtils.TONG_HUA_SHUN);

    }

    @Test
    public void isZhaDanNiu() throws Exception {

        List<Integer> list = new ArrayList<>();

        list.add(4);
        list.add(5);
        list.add(6);
        list.add(7);
        list.add(3);

        Integer value = CardUtils.getPaiXing(list);
        Assert.assertEquals(value.intValue(), CardUtils.ZHA_DAN_NIU);
    }

    @Test
    public void isWuHuaNiu() throws Exception {

        List<Integer> list = new ArrayList<>();

        list.add(4);
        list.add(8);
        list.add(12);

        list.add(5);
        list.add(6);


        Integer value = CardUtils.getPaiXing(list);
        Assert.assertEquals(value.intValue(), CardUtils.WU_HUA_NIU);
    }

    @Test
    public void isWuXiaoNiu() throws Exception {

        List<Integer> list = new ArrayList<>();

        list.add(0);
        list.add(1);
        list.add(49);

        list.add(48);
        list.add(44);

        Integer value = CardUtils.getPaiXing(list);
        Assert.assertEquals(CardUtils.WU_XIAO_NIU, value.intValue());
    }

    @Test
    public void isHuLu() throws Exception {

        List<Integer> list = new ArrayList<>();
        list.add(4);
        list.add(8);
        list.add(12);
        list.add(13);
        list.add(14);

        Integer value = CardUtils.getPaiXing(list);
        System.out.println(value);

    }

    @Test
    public void isTongHua() throws Exception {

        List<Integer> list = new ArrayList<>();
        list.add(4);
        list.add(0);
        list.add(16);
        list.add(12);
        list.add(20);

        Integer value = CardUtils.getPaiXing(list);
        System.out.println(value);

    }

    @Test
    public void isShunZi() throws Exception {

        List<Integer> list = new ArrayList<>();
        list.add(5);
        list.add(0);
        list.add(8);
        list.add(12);
        list.add(16);

        Integer value = CardUtils.getPaiXing(list);
        Assert.assertEquals(CardUtils.SHUN_ZI, value.intValue());

    }

    @Test
    public void niu_x() throws Exception {


        List<Integer> list = new ArrayList<>();
        list.add(40);
        list.add(20);
        list.add(25);
        list.add(31);
        list.add(23);

        Integer value = CardUtils.getPaiXing(list);

        System.out.println(value);
    }
}