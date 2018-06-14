package com.code.server.game.poker.zhaguzi;

import com.code.server.game.room.service.IRobot;

/**
 * Created by dajuejinxian on 2018/6/8.
 */
public interface YSZRobot extends IRobot {
    void pass(GameYSZ game);
    void bet(GameYSZ game);
}
