package com.code.server.game.poker.pullmice;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class CardUtilsTest {

    @Test
    public void testList(){

        List<Integer> aList = new ArrayList<>();
        aList.add(1);
        aList.add(1);

        Integer a = 1;
        aList.remove(a);
        System.out.println(aList);

    }
}