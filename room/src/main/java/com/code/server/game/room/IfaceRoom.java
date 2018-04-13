package com.code.server.game.room;


import com.code.server.constant.game.IGameConstant;
import com.code.server.constant.game.PrepareRoom;
import com.code.server.constant.response.IfaceRoomVo;

import java.util.List;

/**
 * Created by sunxianping on 2017/5/24.
 */
public interface IfaceRoom extends IGameConstant {
    int GOLD_ROOM_PERMISSION_NONE = 0;
    int GOLD_ROOM_PERMISSION_DEFAULT = 1;
    int GOLD_ROOM_PERMISSION_PUBLIC = 2;
    int GOLD_ROOM_PERMISSION_PRIVATE = 3;

    /**
     * 加入房间
     * @param userId
     * @param isJoin
     * @return
     */
    int joinRoom(long userId,boolean isJoin);

    /**
     * 退出房间
     * @param userId
     * @return
     */
    int quitRoom(long userId);

    /**
     * 准备
     * @param userid
     * @return
     */
    int getReady(long userid);

    /**
     * 解散
     * @param userId
     * @param agreeOrNot
     * @param method
     * @return
     */
    int dissolution(long userId,boolean agreeOrNot, String method);

    /**
     * 客户端触发开始游戏
     * @param userId
     * @return
     */
    int startGameByClient(long userId);


    int getPrepareRoom(long userId);

    /**
     * 获得游戏
     * @return
     */
    IfaceGame getGame();

    IfaceRoomVo toVo(long userId);

    List<Long> getUsers();

    PrepareRoom getPrepareRoomVo();

    int getRoomClubByUser(long userId);
}
