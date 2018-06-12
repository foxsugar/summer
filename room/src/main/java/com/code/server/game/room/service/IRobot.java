package com.code.server.game.room.service;

import com.code.server.game.room.Room;

/**
 * Created by sunxianping on 2017/5/16.
 */
public interface IRobot {
    void execute();

    void doExecute(Room room);
}
