package com.code.server.game.mahjong.logic;

import com.code.server.constant.response.ErrorCode;
import com.code.server.game.room.service.RoomManager;

/**
 * Created by sunxianping on 2019-07-09.
 */
public class RoomInfoZLB extends RoomInfo {


    @Override
    public boolean isGoldRoom() {
        return false;
    }

    @Override
    public int joinRoom(long userId, boolean isJoin) {
        //随机匹配的金币房

        int rtn = super.joinRoom(userId, isJoin);
        if (rtn != 0) {
            return rtn;
        }

        //如果房间已满 加入已满房间
        if (goldRoomPermission == GOLD_ROOM_PERMISSION_DEFAULT && this.isRoomFull()) {
            RoomManager.getInstance().moveGoldRoomNotFull2Full(this);
        }

//            getReady(userId);
        return 0;


    }


    @Override
    public int quitRoom(long userId) {

        if (!this.users.contains(userId)) {
            return ErrorCode.CANNOT_QUIT_ROOM_NOT_EXIST;
        }

        if (isInGame && this.game.users.contains(userId)) {
            return ErrorCode.CANNOT_QUIT_ROOM_IS_IN_GAME;
        }

//            List<Long> noticeList = new ArrayList<>();
//            noticeList.addAll(this.getUsers());

        //删除玩家房间映射关系
        roomRemoveUser(userId);


        if (goldRoomPermission == GOLD_ROOM_PERMISSION_DEFAULT) {
            RoomManager.getInstance().moveFull2NotFullRoom(this);
        }

        //todo 如果都退出了  删除房间
        if (this.users.size() == 0) {

            RoomManager.removeRoom(this.roomId);
        }
        noticeQuitRoom(userId);
        return 0;

    }
}
