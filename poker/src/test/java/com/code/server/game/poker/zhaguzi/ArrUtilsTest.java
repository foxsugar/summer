package com.code.server.game.poker.zhaguzi;

import org.junit.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by dajuejinxian on 2018/9/5.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class ArrUtilsTest {

    @Test
    public void testChouMa(){

       List<Double> list = ArrUtils.transformChouMa(50d, 50);
        System.out.println(list);

    }

}