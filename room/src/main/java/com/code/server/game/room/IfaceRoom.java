package com.code.server.game.room;


import com.code.server.constant.game.IGameConstant;
import com.code.server.constant.response.IfaceRoomVo;

import java.util.List;

/**
 * Created by sunxianping on 2017/5/24.
 */
public interface IfaceRoom extends IGameConstant {
    int joinRoom(long userId,boolean isJoin);

    int quitRoom(long userId);

    int getReady(long userid);

    int dissolution(long userId,boolean agreeOrNot, String method);

    IfaceGame getGame();

    IfaceRoomVo toVo(long userId);

    List<Long> getUsers();
}
