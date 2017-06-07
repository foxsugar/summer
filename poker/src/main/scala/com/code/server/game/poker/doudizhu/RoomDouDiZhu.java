package com.code.server.game.poker.doudizhu;



import com.code.server.constant.response.*;
import com.code.server.game.room.Game;
import com.code.server.game.room.Room;
import com.code.server.util.ThreadPool;

import java.util.ArrayList;

/**
 * Created by sunxianping on 2017/3/13.
 */
public class RoomDouDiZhu extends Room {



    public static final int PERSONNUM = 3;




        @Override
        protected Game getGameInstance() {
            switch (gameType) {
                case GAMETYPE_LINFEN:
                    return new GameDouDiZhuLinFen();
                case GAMETYPE_QIANAN:
                    return new GameDouDiZhuQianAn();
                default:
                    return new GameDouDiZhu();
            }

        }

    public RoomDouDiZhu getRoomInstance(String gameType){
        RoomDouDiZhu room = new RoomDouDiZhu();
        return room;
    }

    public static int createRoom(long userId, int gameNumber, int multiple, String gameType) {
        if (GameManager.getInstance().userRoom.containsKey(userId)) {
            return ErrorCode.CANNOT_CREATE_ROOM_ROLE_HAS_IN_ROOM;
        }
        int needMoney = getNeedMoney(gameNumber);
        if (player.getUser().getMoney() < needMoney) {
            return ErrorCode.CANNOT_CREATE_ROOM_MONEY;
        }
        RoomDouDiZhu room = new RoomDouDiZhu();
        room.personNumber = PERSONNUM;

        room.roomId = getRoomIdStr(genRoomId());
        room.createUser = userId;
        room.gameType = gameType;
        room.init(gameNumber, multiple);
        //房间加入列表
        room.roomAddUser(userId);
        GameManager.getInstance().rooms.put(room.roomId, room);

        player.sendMsg(new ResponseVo("roomService", "createRoom", new RoomVo(room, player)));

        return 0;
    }

    public void spendMoney() {
        User user = userMap.get(this.createUser);
        if (user != null) {
            user.setMoney(user.getMoney() - createNeedMoney);
            GameManager.getInstance().getSaveUser2DB().add(user);

            //临汾斗地主 抽成
            if(GAMETYPE_LINFEN.equals(gameType)){
                ThreadPool.getInstance().executor.execute(() -> {
                    List<Rebate> list = new ArrayList<>();
                    list.add(getRebate(user, createNeedMoney));
                    RpcManager.getInstance().sendRpcRebat(list);
                });
            }
        }
    }

    private Rebate getRebate(User user, int num) {
        return new Rebate().setId(GameManager.getInstance().nextId())
                .setUserId(user.getUserId())
                .setRefereeId(user.getReferee())
                .setTime(System.currentTimeMillis())
                .setRebateNum(num)
                .setIsHasReferee(user.getReferee() != 0);

    }

    @Override
    public IfaceRoomVo toVo() {
        IfaceGameVo vo = super.toVo();
        return super.toVo();
    }
}
