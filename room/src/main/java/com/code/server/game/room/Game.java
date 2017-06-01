package com.code.server.game.room;


import com.code.server.constant.response.IfaceGameVo;

import java.util.List;

/**
 * Created by sunxianping on 2017/3/14.
 */
public class Game implements IfaceGame{

    public int number;

    public void startGame(List<Long> users,Room room){

    }

    @Override
    public IfaceGameVo toVo() {
        return null;
    }

    @Override
    public IfaceGameVo toVo(long watchUser) {
        return null;
    }
}
