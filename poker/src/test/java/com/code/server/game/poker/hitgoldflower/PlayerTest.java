package com.code.server.game.poker.hitgoldflower;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * Created by dajuejinxian on 2018/5/18.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class PlayerTest {

    @Test
    public void test1(){

        Player p1 = new Player(1l, "HEI", "A", "HEI", "A", "HEI", "A");
        Player p2 = new Player(2l, "HONG", "2", "HONG", "3", "HONG", "4");
    }
}