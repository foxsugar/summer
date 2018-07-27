package com.code.server.game.poker.pullmice;

import org.junit.Test;

public class PlayerPullMiceTest {

    PlayerPullMiceTest self = this;
    @Test
    public void toVo() {

        PlayerPullMice playerPullMice = new PlayerPullMice();
        playerPullMice.setUserId(1);
        playerPullMice.setPoint(11);
        playerPullMice.setPxId(2);
        PlayerPullMiceVo vo = (PlayerPullMiceVo) playerPullMice.toVo();
        System.out.println(vo);
        self.testSelf();
    }

    public void testSelf(){
        System.out.println("我就想用self");
    }

}