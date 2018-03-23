package com.code.server.game.poker.paijiu;

import com.code.server.constant.exception.DataNotFoundException;
import com.code.server.constant.game.RoomStatistics;
import com.code.server.constant.response.GameOfResult;
import com.code.server.constant.response.ResponseVo;
import com.code.server.constant.response.UserOfResult;
import com.code.server.game.poker.config.ServerConfig;
import com.code.server.game.room.Room;
import com.code.server.game.room.kafka.MsgSender;
import com.code.server.game.room.service.RoomManager;
import com.code.server.redis.service.RedisManager;
import com.code.server.util.IdWorker;
import com.code.server.util.SpringUtil;
import com.code.server.util.timer.GameTimer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

        MsgSender.sendMsg2Player(new ResponseVo("pokerRoomService", "createPaijiuGoldRoom", roomGoldPaijiu.toVo(userId)), userId);
        return 0;
    }

    @Override
    public void roomAddUser(long userId) {

        this.users.add(userId);
        this.userStatus.put(userId, 0);
        this.userScores.put(userId, RedisManager.getUserRedisService().getUserMoney(userId));
        this.roomStatisticsMap.put(userId, new RoomStatistics(userId));
        this.canStartUserId = users.get(0);
        addUser2RoomRedis(userId);
    }

    /**
     * 解散房间
     */
    @Override
    public void dissolutionRoom() {
        //庄家初始分 再减掉
        RoomManager.removeRoom(this.roomId);
        // 结果类
        List<UserOfResult> userOfResultList = getUserOfResult();

        //代开房 并且游戏未开始
        if (!isCreaterJoin && !this.isInGame && (this.curGameNumber == 1)) {
            drawBack();
            GameTimer.removeNode(this.prepareRoomTimerNode);
        }
        this.isInGame = false;

        // 存储返回
        GameOfResult gameOfResult = new GameOfResult();
        for (UserOfResult u:userOfResultList) {
            double d = Double.parseDouble(u.getScores());
            u.setScores(d-RedisManager.getUserRedisService().getUserMoney(u.getUserId())+"");
            RedisManager.getUserRedisService().setUserMoney(u.getUserId(), d);//userId-money
        }
        gameOfResult.setUserList(userOfResultList);
        gameOfResult.setEndTime(LocalDateTime.now().toString());
        MsgSender.sendMsg2Player(new ResponseVo("gameService", "askNoticeDissolutionResult", gameOfResult), users);

        //庄家初始分 再减掉
        this.addUserSocre(this.getBankerId(), -this.bankerInitScore());

        //战绩
        genRoomRecord();
    }

    //房间列表
    public static int getAllRoom(long userId){
        List<Map<String,Object>> rooms = new ArrayList<>();
        RoomManager.getInstance().getRooms().values().forEach(r->{
            Map<String, Object> result = new HashMap<>();
            RoomGoldPaijiu roomGoldPaijiu = (RoomGoldPaijiu) r;
            result.put("roomId", roomGoldPaijiu.getRoomId());
            result.put("nickName",RedisManager.getUserRedisService().getUserBean(userId).getUsername());
            result.put("persionNum", roomGoldPaijiu.getUsers().size());
            result.put("goldType", roomGoldPaijiu.getGoldType());
            rooms.add(result);
        });
        MsgSender.sendMsg2Player("pokerRoomService", "getAllGoldPaijiuRoom", rooms, userId);
        return 0;
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
