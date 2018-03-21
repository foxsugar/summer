package com.code.server.game.poker.pullmice;

import org.junit.Test;

public class PlayerPullMiceTest {

    @Test
    public void toVo() {

        PlayerPullMice playerPullMice = new PlayerPullMice();
        playerPullMice.setUserId(1);
        playerPullMice.setPoint(11);
        playerPullMice.setPxId(2);
        PlayerPullMiceVo vo = (PlayerPullMiceVo) playerPullMice.toVo();
        System.out.println(vo);
    }

}