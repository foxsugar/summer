package com.code.server.game.poker.paijiu;

import com.code.server.constant.exception.DataNotFoundException;
import com.code.server.constant.response.ResponseVo;
import com.code.server.game.poker.config.ServerConfig;
import com.code.server.game.room.Room;
import com.code.server.game.room.kafka.MsgSender;
import com.code.server.game.room.service.RoomManager;
import com.code.server.redis.service.RedisManager;
import com.code.server.util.IdWorker;
import com.code.server.util.SpringUtil;

import java.util.HashMap;
import java.util.Map;

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
public class RoomGoldPaijiu extends RoomPaijiu {

    protected int isGold;
    protected int goldType;

    public static int createGoldRoom(Long userId,String roomType,String gameType,int gameNumber,int isGold,int goldType) throws DataNotFoundException {
        RoomGoldPaijiu roomGoldPaijiu = new RoomGoldPaijiu();
        roomGoldPaijiu.setRoomId(Room.getRoomIdStr(Room.genRoomId()));
        roomGoldPaijiu.setRoomType(roomType);
        roomGoldPaijiu.setGameType(gameType);
        roomGoldPaijiu.setGameNumber(gameNumber);
        roomGoldPaijiu.setBankerId(userId);
        roomGoldPaijiu.setCreateUser(userId);
        roomGoldPaijiu.setPersonNumber(4);
        roomGoldPaijiu.setIsGold(isGold);
        roomGoldPaijiu.setGoldType(goldType);
        roomGoldPaijiu.init(gameNumber, 1);
        int code = roomGoldPaijiu.joinRoom(userId, true);
        if (code != 0){
            return code;
        }

        ServerConfig serverConfig = SpringUtil.getBean(ServerConfig.class);
        RoomManager.addRoom(roomGoldPaijiu.getRoomId(), "" + serverConfig.getServerId(), roomGoldPaijiu);
        IdWorker idword = new IdWorker(serverConfig.getServerId(), 0);
        roomGoldPaijiu.setUuid(idword.nextId());

        MsgSender.sendMsg2Player(new ResponseVo("pokerRoomService", "createPaijiuRoom", roomGoldPaijiu.toVo(userId)), userId);
        return 0;
    }

    public void pushScoreChange() {
        Map<Long, Double> userMoneys = new HashMap<>();
        for (Long l: users) {
            userMoneys.put(l, RedisManager.getUserRedisService().getUserMoney(l));
        }
        MsgSender.sendMsg2Player(new ResponseVo("gameService", "scoreChange", userMoneys), this.getUsers());
    }


    public int getIsGold() {
        return isGold;
    }

    public void setIsGold(int isGold) {
        this.isGold = isGold;
    }

    public int getGoldType() {
        return goldType;
    }

    public void setGoldType(int goldType) {
        this.goldType = goldType;
    }
}
