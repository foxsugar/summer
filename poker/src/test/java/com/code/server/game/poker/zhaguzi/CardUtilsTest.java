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
import org.springframework.util.CollectionUtils;
import scala.Int;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Created by dajuejinxian on 2018/5/2.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CardUtilsTest {
    @Test
    public void transfromStringToCards1() throws Exception {

        List<Integer> a = new ArrayList<>();
        a.add(1);
        a.add(2);
        a.add(3);
//        a.add(4);

        String ss = CardUtils.transfromCardsToString(a);
        System.out.println(ss);
    }

    @Test
    public void transfromStringToCards() throws Exception {


        List<Integer> list = CardUtils.transfromStringToCards("_-1_2____3_");

        System.out.println(list);
    }

    @Test
    public void compare() throws Exception {
    }

    @Test
    public void cardsCompare() throws Exception {
    }

    @Test
    public void cardsTypeDesc() throws Exception {
    }

    @Test
    public void cardsTypeDesc1() throws Exception {
    }

    @Test
    public void computeCardType() throws Exception {
    }

    @Test
    public void getCardDict1() throws Exception {
    }

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

    @Test
    //测试牌型
    public void demo7(){

        List<Integer> aList = new ArrayList<>();
        int a1 = 7;
        int a2 = 9;
        aList.add(a1);
        aList.add(a2);
        String str = CardUtils.cardsTypeDesc(aList);
        Assert.assertEquals(str, "双三");

    }

    //测试base
    @Test
    public void base1(){


        for (int i = 1; i < 55; i++){

            String str = CardUtils.client2String(i);
            System.out.println(str);
        }

    }

    @Test
    public void base2(){


        for (int i = 0; i < 54; i++){

            String str = CardUtils.local2String(i , ifCard);
            System.out.println(str);
        }

    }

    public void testTalk(List<PlayerZhaGuZi> list){


        Integer hongtao = CardUtils.string2Local("红桃-3", ifCard);
        Integer fangpian = CardUtils.string2Local("方片-3 ", ifCard);
        for (int i = 0; i < list.size(); i++){
            PlayerZhaGuZi playerZhaGuZi = list.get(i);

            if (playerZhaGuZi.getOp() != 0){
                continue;
            }
            //包含红桃三或者方片三
            if (playerZhaGuZi.cards.contains(hongtao) || playerZhaGuZi.cards.contains(fangpian)){
                playerZhaGuZi.setOp(Operator.LIANG_SAN);

                if (playerZhaGuZi.cards.contains(hongtao)){
                    playerZhaGuZi.getLiangList().add(hongtao);
                }else {
                    playerZhaGuZi.getLiangList().add(fangpian);
                }

            }else {
                playerZhaGuZi.setOp(Operator.ZHA_GU);
            }
        }
    }

    //模拟发牌
    public List<PlayerZhaGuZi> testStart(){

        List<PlayerZhaGuZi> list = new ArrayList<>();
        for (int i = 0; i < 5; i++){
            PlayerZhaGuZi playerZhaGuZi = new PlayerZhaGuZi();
            playerZhaGuZi.setRoomPersonNum(5);
            list.add(playerZhaGuZi);
        }

        List<Integer> cards = new ArrayList<>();

        for (int i = 0; i < 54; i++){
            cards.add(i);
        }

        //发牌
        Collections.shuffle(cards);

        PlayerZhaGuZi playerCurrent = null;
        Integer idx = 0;
        while (true){
            playerCurrent = list.get(idx);
            if (playerCurrent.cards.size() >= 10){
                idx++;
                if (idx >= list.size()){
                    break;
                }
                playerCurrent = list.get(idx);
            }

            Integer card = cards.get(0);
            playerCurrent.cards.add(card);
        }

        return list;

    }

    @Test
    public void testCompare(){

        List<PlayerZhaGuZi> list = testStart();
        testTalk(list);

    }

    @Test
    public void testA(){

        Integer local = 9;

        IfCard ifCard = new IfCard() {
            @Override
            public Map<Integer, Integer> cardDict() {
                return CardUtils.getCardDict();
            }
        };

        Integer client6 = CardUtils.local2Client(6, ifCard);
        Integer client7 = CardUtils.local2Client(7, ifCard);
        Integer client8 = CardUtils.local2Client(8, ifCard);
        Integer client9 = CardUtils.local2Client(9, ifCard);

        System.out.println(client6);
        System.out.println(client7);
        System.out.println(client8);
        System.out.println(client9);
    }

}