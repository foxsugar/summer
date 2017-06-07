package com.code.server.game.poker.doudizhu;



import com.code.server.constant.response.ErrorCode;
import com.code.server.game.room.Game;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sunxianping on 2017/3/13.
 */
public class RoomDouDiZhuGold extends RoomDouDiZhu{


    private static final Map<Double, Integer> needMoney = new HashMap<>();

    static{
        needMoney.put(0.2,3);
        needMoney.put(1D,15);
        needMoney.put(2D,32);
        needMoney.put(5D,80);
        needMoney.put(10D,160);
    }

    protected boolean isCanJoinCheckMoney(long userId) {
        Integer i = needMoney.get(this.goldRoomType);
        if (i == null) {
            return false;
        }
        User user = player.getUser();
        if(user.getMoney()>= i){

            return true;
        }
        return false;
    }

    protected Game getGameInstance(){
        return new GameDouDiZhuLinFenGold();
    }


    public int quitRoom(long userId) {
        long userId = player.getUserId();
        if (!this.users.contains(userId)) {
            return ErrorCode.CANNOT_QUIT_ROOM_NOT_EXIST;
        }

        if (isInGame) {
            return ErrorCode.CANNOT_QUIT_ROOM_IS_IN_GAME;
        }

        roomRemoveUser(player);

        boolean isInFullRoom = false;
        if(this.userMap.size() == personNumber){
            isInFullRoom = true;
        }
        if (isInFullRoom) {
            GoldRoomPool.removeRoomFromMap(GoldRoomPool.getInstance().fullRoom,this);
            GoldRoomPool.addRoom2Map(GoldRoomPool.getInstance().notFullRoom,this);
        }
        //删除
        if (this.userMap.size() == 0) {
            GameManager.getInstance().rooms.remove(this.getRoomId());
            GoldRoomPool.removeRoomFromMap(GoldRoomPool.getInstance().notFullRoom,this);

        }
        noticeQuitRoom(player);

        return 0;
    }

}
