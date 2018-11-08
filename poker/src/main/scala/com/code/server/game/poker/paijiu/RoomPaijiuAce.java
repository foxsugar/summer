package com.code.server.game.poker.paijiu;

import com.code.server.constant.exception.DataNotFoundException;
import com.code.server.constant.response.ErrorCode;
import com.code.server.constant.response.ResponseVo;
import com.code.server.game.poker.config.ServerConfig;
import com.code.server.game.room.Room;
import com.code.server.game.room.kafka.MsgSender;
import com.code.server.game.room.service.RoomManager;
import com.code.server.redis.config.IConstant;
import com.code.server.redis.service.RedisManager;
import com.code.server.util.IdWorker;
import com.code.server.util.SpringUtil;
import com.code.server.util.timer.GameTimer;
import com.code.server.util.timer.TimerNode;

/**
 * Created by sunxianping on 2018-10-22.
 */
public class RoomPaijiuAce extends RoomPaijiu {


    public static final int minMoney = 100;


    public static int createRoom(long userId, String roomType, String gameType, Integer gameNumber, boolean isCreaterJoin,
                                          String clubId, String clubRoomModel, boolean isAA) throws DataNotFoundException {
        ServerConfig serverConfig = SpringUtil.getBean(ServerConfig.class);
        RoomPaijiuAce roomPaijiu = new RoomPaijiuAce();
        roomPaijiu.setRoomId(Room.getRoomIdStr(Room.genRoomId(serverConfig.getServerId())));
        roomPaijiu.setRoomType(roomType);
        roomPaijiu.setGameType(gameType);
        roomPaijiu.setGameNumber(gameNumber);
        roomPaijiu.setBankerId(userId);
        roomPaijiu.setCreateUser(userId);
        roomPaijiu.setPersonNumber(4);
        roomPaijiu.setCreaterJoin(isCreaterJoin);
        roomPaijiu.setClubId(clubId);
        roomPaijiu.setClubRoomModel(clubRoomModel);
        roomPaijiu.setAA(isAA);
        roomPaijiu.init(gameNumber, 1);


        if (!isCreaterJoin) {
            roomPaijiu.setCreateUser(userId);
        }

        //代建房 定时解散
        if (!isCreaterJoin && !roomPaijiu.isClubRoom()) {
            //给代建房 开房者 扣钱
            if (RedisManager.getUserRedisService().getUserMoney(userId) < roomPaijiu.getCreateNeedMoney()) {
                RoomManager.removeRoom(roomPaijiu.getRoomId());
                return ErrorCode.CANNOT_JOIN_ROOM_NO_MONEY;
            }

            roomPaijiu.spendMoney();
            TimerNode prepareRoomNode = new TimerNode(System.currentTimeMillis(), IConstant.HOUR_1, false, roomPaijiu::dissolutionRoom);
            roomPaijiu.prepareRoomTimerNode = prepareRoomNode;
            GameTimer.addTimerNode(prepareRoomNode);
        }


        int code = roomPaijiu.joinRoom(userId, isCreaterJoin);
        if (code != 0) {
            return code;
        }

        RoomManager.addRoom(roomPaijiu.getRoomId(), "" + serverConfig.getServerId(), roomPaijiu);
        IdWorker idword = new IdWorker(serverConfig.getServerId(), 0);
        roomPaijiu.setUuid(idword.nextId());


        MsgSender.sendMsg2Player(new ResponseVo("pokerRoomService", "createPaijiuAceRoom", roomPaijiu.toVo(userId)), userId);

        return 0;
    }


    protected boolean isCanJoinCheckMoney(long userId) {

        //代建房
        if (!isCreaterJoin) {
            if (RedisManager.getUserRedisService().getUserMoney(userId) < minMoney) {
                return false;
            }
            return true;
        }
        if (isAA) {
            if (RedisManager.getUserRedisService().getUserMoney(userId) < createNeedMoney + minMoney) {
                return false;
            }
        } else {
            if (userId == createUser) {
                if (RedisManager.getUserRedisService().getUserMoney(userId) < createNeedMoney + minMoney) {
                    return false;
                }
            }else{
                if (RedisManager.getUserRedisService().getUserMoney(userId) <  minMoney) {
                    return false;
                }
            }
        }
        return true;
    }


    @Override
    public void addUserSocre(long userId, double score) {
        super.addUserSocre(userId, score);

        RedisManager.getUserRedisService().addUserMoney(userId, score);
    }


    @Override
    public void pushScoreChange() {
        if (isGoldRoom()) {
            for(long userId : users){
                userScores.put(userId, RedisManager.getUserRedisService().getUserMoney(userId));
            }
        }
        MsgSender.sendMsg2Player(new ResponseVo("gameService", "scoreChange", userScores), this.getUsers());
    }

    private void serviceMoney(){
        //抽水
        double max = this.userScores.values().stream().mapToDouble(Double::doubleValue).max().getAsDouble();
        if (max > 0) {
            long size = this.userScores.values().stream().filter(score -> score == max).count();
            ServerConfig serverConfig = SpringUtil.getBean(ServerConfig.class);
            long each = serverConfig.getPaijiuServiceMoney()/size;

            this.userScores.forEach((userId, score)->{
                if (score == max) {
                    RedisManager.getUserRedisService().addUserMoney(userId, -each);
                }
            });
        }

    }



    @Override
    public boolean isRoomOver() {
        //有小于5房卡的游戏结束
        for (long userId : this.users) {
            double money = RedisManager.getUserRedisService().getUserMoney(userId);
            if (money < 5) {
                return true;
            }
        }
        return super.isRoomOver();
    }


    @Override
    public void genRoomRecord() {
        //庄家初始分 再减掉
        RedisManager.getUserRedisService().addUserMoney(this.getBankerId(), this.bankerInitScore());
        serviceMoney();
        super.genRoomRecord();
    }
}
