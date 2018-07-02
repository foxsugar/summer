package com.code.server.game.poker.tuitongzi;

import com.code.server.game.poker.zhaguzi.CardUtils;
import com.code.server.util.DateUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

/**
 * Created by dajuejinxian on 2018/5/16.
 */

@SpringBootTest
@RunWith(SpringRunner.class)
public class TuiTongTongCardUtilsTest {
    @Test
    public void zhuangIsBiggerThanXian1() throws Exception {

        PlayerTuiTongZi pA = new PlayerTuiTongZi(1, 8,12);
        PlayerTuiTongZi pB = new PlayerTuiTongZi(2, 32, 28);
        Boolean ret = TuiTongTongCardUtils.zhuangIsBiggerThanXian(pB, pA);
        Assert.assertNotEquals(false, ret);
    }

    @Test
    public void mAIsBiggerThanB2() throws Exception {

//        CardUtils.string2Local("'",)
        HashMap map = new HashMap();


    }

    public static boolean isNumber(String str) {
        //采用正则表达式的方式来判断一个字符串是否为数字，这种方式判断面比较全
        //可以判断正负、整数小数
        boolean isInt = Pattern.compile("^-?[1-9]\\d*$").matcher(str).find();
        boolean isDouble = Pattern.compile("^-?([1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*|0?\\.0+|0)$").matcher(str).find();

        return isInt || isDouble;
    }

    @Test
    public void ttt(){
        String str = "-11111";
        boolean isNumber = isNumber(str);
        System.out.println(isNumber);
    }

    @Test
    public void mAIsBiggerThanB3() throws Exception {
    }


    @Test
    public void zhuangIsBiggerThanXian() throws Exception {


////        Date startStr = DateUtil.convert2DayDate("2018-06-06");
//        Date startStr = DateUtil.convertDay2Date("2018-06-06");
//
//        String str = DateUtil.convert2DayString(startStr);
//        System.out.println(startStr);



    }

    @Test
    public void mAIsBiggerThanB1() throws Exception {

        // 87   // 96

        List<Integer> aList = new ArrayList<>();
        aList.add(24);
        aList.add(28);

        List<Integer> bList = new ArrayList<>();
        bList.add(32);
        bList.add(20);

        PlayerTuiTongZi playerZ = new PlayerTuiTongZi();
        PlayerTuiTongZi playerX = new PlayerTuiTongZi();

        playerX.setPlayerCards(bList);
        playerZ.setPlayerCards(aList);

        boolean  rrr =  TuiTongTongCardUtils.zhuangIsBiggerThanXian(playerZ, playerX);

        System.out.println(rrr);
        Math.sin(1);

    }

    @Test
    public void mAIsBiggerThanB() throws Exception {

        List<Integer> aList = new ArrayList<>();
        aList.add(0);
        aList.add(8);

        List<Integer> bList = new ArrayList<>();
        bList.add(11);
        bList.add(22);
        int ret = TuiTongTongCardUtils.mAIsBiggerThanB(aList, bList);

        System.out.println(ret);
    }

    @Test
    public void mAisBiggerTB() throws Exception {

        List<Integer> aList = new ArrayList<>();
        aList.add(0);
        aList.add(8);

        List<Integer> bList = new ArrayList<>();
        bList.add(11);
        bList.add(22);

        PlayerTuiTongZi playerTuiTongZi1 = new PlayerTuiTongZi();
        PlayerTuiTongZi playerTuiTongZi2 = new PlayerTuiTongZi();

        playerTuiTongZi1.setPlayerCards(aList);
        playerTuiTongZi2.setPlayerCards(bList);

        int ret = TuiTongTongCardUtils.mAIsBiggerThanB(playerTuiTongZi1, playerTuiTongZi2);

        System.out.println(ret);
    }

}