package com.code.server.game.room;


import com.code.server.constant.game.GameLogKey;
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



    int ROOMTYPE_麻将 = 1;
    int ROOMTYPE_斗地主 = 2;
    int ROOMTYPE_牌九 = 3;
    int ROOMTYPE_扎金花 = 4;
    int ROOMTYPE_牛牛 = 5;
    int ROOMTYPE_推筒子 = 6;
    int ROOMTYPE_拉老鼠 = 7;
    int ROOMTYPE_毛三 = 8;
    int ROOMTYPE_宣琪琪 = 9;
    int ROOMTYPE_扎谷子 = 10;
    int ROOMTYPE_打七 = 11;
    int ROOMTYPE_填大坑 = 12;
    int ROOMTYPE_跑得快 = 13;
    int ROOMTYPE_鱼虾蟹 = 14;
    int ROOMTYPE_wzq = 20;

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

    boolean isGoldRoom();

    boolean isRobotRoom();

    GameLogKey getGameLogKey();

    int dissolutionRoom(long userId);
}
