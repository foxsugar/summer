package com.code.server.game.poker.tuitongzi;

import com.code.server.game.poker.zhaguzi.CardUtils;
import com.code.server.util.DateUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
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

        TuiTongTongCardUtilsTest self = this;
    }

    @Test
    public void dateTest(){

        String today = DateUtil.convert2DayString(new Date());
        System.out.println(today);

        Map<String, Object> rs = new HashMap<>();
        rs.put("a", 1);
        System.out.println(rs.get("b"));
        System.out.println("xxx");
    }

    @Test
    public void mAIsBiggerThanB2() throws Exception {

//        CardUtils.string2Local("'",)
        HashMap map = new HashMap();


    }

    @Test
    public void demo2(){

        List<String> list = DateUtil.getDateListIn("2018-07-31", "2018-07-31");
        System.out.println(list);
    }

    @Test
    public void demo(){
        Date date = new Date();
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();

        // atZone()方法返回在指定时区从此Instant生成的ZonedDateTime。
        LocalDate localDate = instant.atZone(zoneId).toLocalDate();
        System.out.println("Date = " + date);
        System.out.println("LocalDate = " + localDate);
    }

//    @Test
    public void dateToLocalDate(Date date){
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        // atZone()方法返回在指定时区从此Instant生成的ZonedDateTime。
        LocalDate localDate = instant.atZone(zoneId).toLocalDate();
        System.out.println("Date = " + date);
        System.out.println("LocalDate = " + localDate);
    }

    @Test
    public void previousDay(){
        Date date = new Date();
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        // atZone()方法返回在指定时区从此Instant生成的ZonedDateTime。
        LocalDate localDate = instant.atZone(zoneId).toLocalDate();
        LocalDate ll = localDate.plusDays(-2);
        System.out.println("LocalDate = " + ll);
    }

    @Test
    public void testLocalDate(){
        String e = "2018-1-3";
        String s = "2018-07-4";

//        List<String> list = DateUtil.getDateListIn(s, e);
//        System.out.println(list);
//        System.out.println("==");

        String ss = DateUtil.becomeStandardSTime(e);
        System.out.println(ss);
//        testDate(null, null);
    }
    public void testDate(String current, String end){

        current = DateUtil.getPreviousDay("2018-07-25");
        end = DateUtil.getPreviousDay("2018-07-01");
//        System.out.println(current);
        List<String> list = new ArrayList<>();
        list.add(current);
        for (int i = 1; i < 90; i++){
            current = DateUtil.getPreviousDay(current);
            if (current.equals(end)){
                break;
            }
            list.add(current);
        }
        System.out.println("======");
        System.out.println(list);

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