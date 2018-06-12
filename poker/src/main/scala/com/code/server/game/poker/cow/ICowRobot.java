package com.code.server.game.poker.cow;

import com.code.server.game.room.service.IRobot;

/**
 * 项目名称：${project_name}
 * 类名称：${type_name}
 * 类描述：
 * 创建人：Clark
 * 创建时间：${date} ${time}
 * 修改人：Clark
 * 修改时间：${date} ${time}
 * 修改备注：
 *
 * @version 1.0
 */
public interface ICowRobot extends IRobot {

    void raise(GameCow gameCow);
    void compare(GameCow gameCow);
    void setMultipleForGetBanker(GameCow gameCow);

    void getReady(RoomCow roomCow);
}
