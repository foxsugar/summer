package com.code.server.game.poker.doudizhu;


import com.code.server.constant.response.*;
import com.code.server.game.poker.config.ServerConfig;
import com.code.server.game.room.Game;
import com.code.server.game.room.kafka.MsgSender;
import com.code.server.game.room.Room;
import com.code.server.game.room.service.RoomManager;
import com.code.server.redis.service.RedisManager;
import com.code.server.util.SpringUtil;

/**
 * Created by sunxianping on 2017/3/13.
 */
public class RoomDouDiZhu extends Room {


    public static final int PERSONNUM = 3;


    @Override
    public void drawBack() {
//        RedisManager.getUserRedisService().addUserMoney(this.createUser, createNeedMoney);
//        User user = userMap.get(this.createUser);
//        if (user != null) {
//            user.setMoney(user.getMoney() + createNeedMoney);
//            GameManager.getInstance().getSaveUser2DB().add(user);
//        }
    }

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

    public RoomDouDiZhu getRoomInstance(String gameType) {
        RoomDouDiZhu room = new RoomDouDiZhu();
        return room;
    }

    public static int createRoom(long userId, int gameNumber, int multiple, String gameType, String roomType) {

        RoomDouDiZhu room = new RoomDouDiZhu();
        room.personNumber = PERSONNUM;

        room.roomId = getRoomIdStr(genRoomId());
        room.createUser = userId;
        room.gameType = gameType;
        room.roomType = roomType;
        room.init(gameNumber, multiple);


        int code = room.joinRoom(userId);
        if (code != 0) {
            return code;
        }


        ServerConfig serverConfig = SpringUtil.getBean(ServerConfig.class);
        RoomManager.addRoom(room.roomId, "" + serverConfig.getServerId(), room);

        MsgSender.sendMsg2Player(new ResponseVo("pokerRoomService", "createRoom", room.toVo(userId)), userId);

        return 0;
    }

    public void spendMoney() {
        RedisManager.getUserRedisService().addUserMoney(this.createUser, -createNeedMoney);

        //临汾斗地主 抽成
//        if(GAMETYPE_LINFEN.equals(gameType)){
//            ThreadPool.getInstance().executor.execute(() -> {
//                List<Rebate> list = new ArrayList<>();
//                list.add(getRebate(user, createNeedMoney));
//                RpcManager.getInstance().sendRpcRebat(list);
//            });
//        }


    }

//    private Rebate getRebate(User user, int num) {
//        return new Rebate().setId(GameManager.getInstance().nextId())
//                .setUserId(user.getUserId())
//                .setRefereeId(user.getReferee())
//                .setTime(System.currentTimeMillis())
//                .setRebateNum(num)
//                .setIsHasReferee(user.getReferee() != 0);
//
//    }

}
