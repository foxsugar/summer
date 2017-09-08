package com.code.server.game.poker.doudizhu;


import com.code.server.constant.exception.DataNotFoundException;
import com.code.server.constant.response.ResponseVo;
import com.code.server.game.poker.config.ServerConfig;
import com.code.server.game.room.Game;
import com.code.server.game.room.Room;
import com.code.server.game.room.kafka.MsgSender;
import com.code.server.game.room.service.RoomManager;
import com.code.server.redis.config.IConstant;
import com.code.server.util.IdWorker;
import com.code.server.util.SpringUtil;
import com.code.server.util.timer.GameTimer;
import com.code.server.util.timer.TimerNode;

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
            case GAMETYPE_LONGQI:
                return new GameDouDiZhu();
            case GAMETYPE_LONGQI_LINFEN:
                return new GameDouDiZhuLinFenLongQi();
            case GAMETYPE_LONGQI_LINFEN_NO_QIANG:
                return new GameDouDiZhuLinFenNoQiang();

            default:
                return new GameDouDiZhu();
        }

    }

    public static int createRoom(long userId, int gameNumber, int multiple, String gameType, String roomType, boolean isAA, boolean isJoin) throws DataNotFoundException {
        RoomDouDiZhu room = null;
        switch (roomType) {
            case "1":  room = new RoomDouDiZhu();
            case "3":  room = new RoomDouDiZhuPlus();
            default: room = new RoomDouDiZhu();
        }
        room.personNumber = PERSONNUM;

        room.roomId = getRoomIdStr(genRoomId());
        room.createUser = userId;
        room.gameType = gameType;
        room.roomType = roomType;
        room.isAA = isAA;
        room.isCreaterJoin = isJoin;

        room.init(gameNumber, multiple);



        int code = room.joinRoom(userId,isJoin);
        if (code != 0) {
            return code;
        }


        //代建房 定时解散
        if(!isJoin){
            //给代建房 开房者 扣钱
            room.spendMoney();
            TimerNode prepareRoomNode = new TimerNode(System.currentTimeMillis(),IConstant.HOUR_1,false, room::dissolutionRoom);
            room.prepareRoomTimerNode = prepareRoomNode;
            GameTimer.addTimerNode(prepareRoomNode);
        }

        ServerConfig serverConfig = SpringUtil.getBean(ServerConfig.class);
        RoomManager.addRoom(room.roomId, "" + serverConfig.getServerId(), room);

        IdWorker idWorker = new IdWorker(serverConfig.getServerId(), 0);
        room.setUuid(idWorker.nextId());

        MsgSender.sendMsg2Player(new ResponseVo("pokerRoomService", "createRoom", room.toVo(userId)), userId);

        return 0;
    }

    public void spendMoney() {
        super.spendMoney();
//        RedisManager.getUserRedisService().addUserMoney(this.createUser, -createNeedMoney);

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
