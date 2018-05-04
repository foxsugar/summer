package com.code.server.game.poker.zhaguzi;

import com.code.server.game.poker.doudizhu.CardUtil;
import com.code.server.game.poker.pullmice.IfCard;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;
import scala.Int;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by dajuejinxian on 2018/5/2.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CardUtilsTest {

//    @Autowired
    private IfCard ifCard = new IfCard() {
    @Override
    public Map<Integer, Integer> cardDict() {
        return CardUtils.getCardDict();
    }
};

    @Test
    public void getCardDict() throws Exception {

        for (int i = 0; i < 54; i++){

            String str = CardUtils.local2String(i, new IfCard() {
                @Override
                public Map<Integer, Integer> cardDict() {
                    return CardUtils.getCardDict();
                }
            });

            System.out.println(str);
        }
    }
    //测试大小王
    @Test
    public void test1(){


        IfCard ifCard = new IfCard() {
            @Override
            public Map<Integer, Integer> cardDict() {
                return CardUtils.getCardDict();
            }
        };

        List<Integer> aList = new ArrayList<>();
        List<Integer> bList = new ArrayList<>();
        int a = CardUtils.string2Local("大王", this.ifCard);
        int b = CardUtils.string2Local("小王", this.ifCard);

        aList.add(a);
        aList.add(b);

        int c = CardUtils.string2Local("♠️-4", ifCard);
        int d = CardUtils.string2Local("♥️️-4", ifCard);
        int e = CardUtils.string2Local("♦️-4", ifCard);
        int f = CardUtils.string2Local("♣️-4", ifCard);

        bList.add(c);
        bList.add(d);
        bList.add(e);
        bList.add(f);

        int ret = CardUtils.cardsCompare(aList, bList);

        Assert.assertEquals(0, ret);
    }

    @Test
    public void test2(){

        List<Integer> aList = new ArrayList<>();
        List<Integer> bList = new ArrayList<>();
        int a = CardUtils.string2Local("大王", this.ifCard);
        int b = CardUtils.string2Local("小王", this.ifCard);

        aList.add(a);
        aList.add(b);

        int c = CardUtils.string2Local("♠️-4", ifCard);
        int d = CardUtils.string2Local("♥️️-4", ifCard);
        int e = CardUtils.string2Local("♦️-4", ifCard);
        bList.add(c);
        bList.add(d);
        bList.add(e);

        int ret = CardUtils.cardsCompare(bList, aList);

        Assert.assertEquals(2, ret);
    }

    @Test
    public void test3(){

        List<Integer> aList = new ArrayList<>();
        List<Integer> bList = new ArrayList<>();
        int a = CardUtils.string2Local("大王", this.ifCard);
        int b = CardUtils.string2Local("小王", this.ifCard);

        aList.add(a);
        aList.add(b);

        int c = CardUtils.string2Local("♠️-4", ifCard);
        int d = CardUtils.string2Local("♥️️-4", ifCard);
        bList.add(c);
        bList.add(d);

        int ret = CardUtils.cardsCompare(bList, aList);

        Assert.assertEquals(2, ret);
    }

    @Test
    public void test4(){

        List<Integer> aList = new ArrayList<>();
        List<Integer> bList = new ArrayList<>();
        int a = CardUtils.string2Local("大王", this.ifCard);
        int b = CardUtils.string2Local("小王", this.ifCard);

        aList.add(a);
        aList.add(b);

        int c = CardUtils.string2Local("♠️-4", ifCard);
        bList.add(c);

        int ret = CardUtils.cardsCompare(bList, aList);
        Assert.assertEquals(2, ret);
    }

    @Test
    public void test5(){

        List<Integer> aList = new ArrayList<>();
        List<Integer> bList = new ArrayList<>();

        int a1 = CardUtils.string2Local("♠-2", ifCard);
        int a2 = CardUtils.string2Local("♥-2", ifCard);
        int a3 = CardUtils.string2Local("♦-2", ifCard);
        int a4 = CardUtils.string2Local("♣-2", ifCard);
        aList.add(a1);
        aList.add(a2);
        aList.add(a3);
        aList.add(a4);

        int c = CardUtils.string2Local("♠-4", ifCard);
        int d = CardUtils.string2Local("♥-4", ifCard);
        int e = CardUtils.string2Local("♦-4", ifCard);
        int f = CardUtils.string2Local("♣-4", ifCard);
        bList.add(c);
        bList.add(d);
        bList.add(e);
        bList.add(f);

        int ret = CardUtils.cardsCompare(bList, aList);
        Assert.assertEquals(0, ret);
    }

    @Test
    public void test6(){

        List<Integer> aList = new ArrayList<>();
        List<Integer> bList = new ArrayList<>();

        int a1 = CardUtils.string2Local("♠-2", ifCard);
        int a2 = CardUtils.string2Local("♥-2", ifCard);
        int a3 = CardUtils.string2Local("♦-2", ifCard);
        aList.add(a1);
        aList.add(a2);
        aList.add(a3);

        int c = CardUtils.string2Local("♠-4", ifCard);
        int d = CardUtils.string2Local("♥-4", ifCard);
        bList.add(c);
        bList.add(d);

        int ret = CardUtils.cardsCompare(bList, aList);
        Assert.assertEquals(2, ret);
    }

    @Test
    //测试牌型
    public void demo1(){

        List<Integer> aList = new ArrayList<>();
//        int a1 = CardUtils.string2Local("♠-2", ifCard);
//        int a2 = CardUtils.string2Local("♥-2", ifCard);
//        int a3 = CardUtils.string2Local("♦-2", ifCard);

        int a1 = CardUtils.string2Local("♠-A", ifCard);
        int a2 = CardUtils.string2Local("♥-A", ifCard);
        int a3 = CardUtils.string2Local("♦-A", ifCard);
        aList.add(a1);
        aList.add(a2);
        aList.add(a3);

       String str = CardUtils.cardsTypeDesc(aList);

        System.out.println(str);
    }

    @Test
    //测试牌型
    public void demo2(){

        List<Integer> aList = new ArrayList<>();
        int a1 = CardUtils.string2Local("♠-A", ifCard);
        int a2 = CardUtils.string2Local("♥-A", ifCard);
        aList.add(a1);
        aList.add(a2);

        String str = CardUtils.cardsTypeDesc(aList);
        Assert.assertEquals(str, "对子");
    }

    @Test
    //测试牌型
    public void demo3(){

        List<Integer> aList = new ArrayList<>();
        int a1 = CardUtils.string2Local("♠-9", ifCard);
        aList.add(a1);

        String str = CardUtils.cardsTypeDesc(aList);
        Assert.assertEquals(str, "单子");
    }

    @Test
    //测试牌型
    public void demo4(){

        List<Integer> aList = new ArrayList<>();
        int a1 = CardUtils.string2Local("♠-A", ifCard);
        int a2 = CardUtils.string2Local("♥-A", ifCard);
        int a3 = CardUtils.string2Local("♦-A", ifCard);
        int a4 = CardUtils.string2Local("♣-A", ifCard);
        aList.add(a1);
        aList.add(a2);
        aList.add(a3);
        aList.add(a4);

        String str = CardUtils.cardsTypeDesc(aList);
        Assert.assertEquals(str, "四炸");

    }

    @Test
    //测试牌型
    public void demo5(){

        List<Integer> aList = new ArrayList<>();
        int a1 = CardUtils.string2Local("大王", ifCard);
        int a2 = CardUtils.string2Local("小王", ifCard);
        aList.add(a1);
        aList.add(a2);
        String str = CardUtils.cardsTypeDesc(aList);
        Assert.assertEquals(str, "王炸");
//        类型错误
    }
    @Test
    //测试牌型
    public void demo6(){

        List<Integer> aList = new ArrayList<>();
        int a1 = CardUtils.string2Local("大王", ifCard);
        int a2 = CardUtils.string2Local("小王", ifCard);
        aList.add(a1);
        aList.add(a2);
        String str = CardUtils.cardsTypeDesc(aList);
        Assert.assertEquals(str, "王炸");
//        类型错误
    }



}