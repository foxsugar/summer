package com.code.server.game.poker.doudizhu;


import com.code.server.constant.exception.DataNotFoundException;
import com.code.server.constant.game.UserBean;
import com.code.server.constant.response.ErrorCode;
import com.code.server.constant.response.Notice;
import com.code.server.constant.response.ResponseVo;
import com.code.server.constant.response.UserOfRoom;
import com.code.server.game.poker.config.ServerConfig;
import com.code.server.game.room.Game;
import com.code.server.game.room.Room;
import com.code.server.game.room.kafka.MsgSender;
import com.code.server.game.room.service.RoomManager;
import com.code.server.redis.service.RedisManager;
import com.code.server.util.IdWorker;
import com.code.server.util.SpringUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Clark on 2017/8/7.
 */
public class RoomDouDiZhuGold extends RoomDouDiZhu {

    public static final int PERSONNUM = 3;

    private static final Map<Double,Integer> needMoney = new HashMap<>();
    private static final Map<Double,Integer> useMoney = new HashMap<>();

    static {
        needMoney.put(10D,60);
        needMoney.put(50D,300);
        needMoney.put(100D,1000);

        useMoney.put(10D,2);
        useMoney.put(50D,4);
        useMoney.put(100D,6);
    }

    @Override
    protected Game getGameInstance() {
        return new GameDouDiZhuGold();
    }


    public void spendMoney() {
        this.users.forEach(userId -> {
            RedisManager.getUserRedisService().addUserMoney(userId, - useMoney.get(goldRoomType));
            if (isAddGold()) RedisManager.addGold(userId, useMoney.get(goldRoomType) / 10);
        });
    }


    /*public static String getRoomIdStr() {
        return System.currentTimeMillis()+"" + random.nextInt(999999);
    }*/

    //金币场加入房间
    public static int joinGoldRoom(long userId,Double goldRoomType,String roomType,String gameType) throws DataNotFoundException {

        if (RedisManager.getUserRedisService().getRoomId(userId) != null) {
            return ErrorCode.CANNOT_CREATE_ROOM_USER_HAS_IN_ROOM;
        }
        if (RedisManager.getUserRedisService().getUserMoney(userId) < needMoney.get(goldRoomType)) {
            return ErrorCode.CANNOT_JOIN_ROOM_NO_MONEY;
        }

        Room nullRoom = RoomManager.getNullRoom(gameType, goldRoomType);
        if(nullRoom==null){
            switch (roomType) {
                case "2":
                    nullRoom = new RoomDouDiZhuGold();
                    break;
                case "3":
                    nullRoom = new RoomDouDiZhuPlus();
                    break;
                default:
                    nullRoom = new RoomDouDiZhuPlus();
                    break;
            }
            nullRoom.setPersonNumber(PERSONNUM);
            nullRoom.setRoomId(getRoomIdStr(genRoomId()));
            nullRoom.setCreateUser(userId);
            nullRoom.setGoldRoomType(goldRoomType);
            nullRoom.setGameType(GAMETYPE_LONGQI);
            nullRoom.setRoomType(roomType);
            nullRoom.setAA(true);
            nullRoom.setCreaterJoin(true);
            nullRoom.init(1,-1);
            nullRoom.setCreateType(1);
        }
        nullRoom.joinRoom(userId,true);

        /*ArrayList<Long> userslist = new ArrayList<>();
        userslist.add(userId);
        HashMap<Long, Integer> userStatusMap = new HashMap<>();
        userStatusMap.put(userId, 0);
        HashMap<Long, Double> userScoresMap = new HashMap<>();
        userScoresMap.put(userId, 0D);
        HashMap<Long, RoomStatistics> roomStatisticsMap = new HashMap<>();
        roomStatisticsMap.put(userId, new RoomStatistics(userId));

        room.setUsers(userslist);
        room.setUserStatus(userStatusMap);
        room.setUserScores(userScoresMap);
        room.setRoomStatisticsMap(roomStatisticsMap);*/
        //room.getReady(userId);//准备
        //list.add(room);

        ServerConfig serverConfig = SpringUtil.getBean(ServerConfig.class);
        RoomManager.addRoom(nullRoom.getRoomId(), "" + serverConfig.getServerId(), nullRoom);
        IdWorker idWorker = new IdWorker(serverConfig.getServerId(),0);
        nullRoom.setUuid(idWorker.nextId());

        MsgSender.sendMsg2Player(new ResponseVo("pokerRoomService", "joinGoldRoom", nullRoom.toVo(userId)), userId);

        return 0;
    }

    @Override
    public int quitRoom(long userId) {
        if (!this.users.contains(userId)) {
            return ErrorCode.CANNOT_QUIT_ROOM_NOT_EXIST;

        }
        if (isInGame) {
            return ErrorCode.CANNOT_QUIT_ROOM_IS_IN_GAME;
        }
        //删除玩家房间映射关系
        roomRemoveUser(userId);

        boolean isInFullRoom = false;
        if(this.users.size() == personNumber){
            isInFullRoom = true;
        }
        if (isInFullRoom) {
            RoomManager.getInstance().removeFromFullRoom(this);
        }
        //删除
        if (this.users.size() == 0) {
            RoomManager.getInstance().removeRoomFromMap(this);
        }

        Notice n = new Notice();
        n.setMessage("quit room success!");

        ResponseVo result = new ResponseVo("roomService", "quitRoom", n);
        MsgSender.sendMsg2Player(result, userId);

        UserOfRoom userOfRoom = new UserOfRoom();
        List<Long> noticeList = this.getUsers();
        int inRoomNumber = this.getUsers().size();
        int readyNumber = 0;

        for (int i : this.getUserStatus().values()) {
            if (i == STATUS_READY) {
                readyNumber++;
            }
        }
        for (UserBean userBean : RedisManager.getUserRedisService().getUserBeans(users)) {
            userOfRoom.getUserList().add(userBean.toVo());
        }
        userOfRoom.setInRoomNumber(inRoomNumber);
        userOfRoom.setReadyNumber(readyNumber);

        ResponseVo noticeResult = new ResponseVo("roomService", "roomNotice", userOfRoom);
        MsgSender.sendMsg2Player(noticeResult, noticeList);

        return 0;
    }

    @Override
    public void init(int gameNumber, int multiple) throws DataNotFoundException {
        this.multiple = multiple;
        this.gameNumber = gameNumber;
        this.isInGame = false;
        this.maxZhaCount = multiple;
    }

    public void pushScoreChange() {
        Map<Long, Double> userMoneys = new HashMap<>();
        for (Long l: users) {
            userMoneys.put(l,RedisManager.getUserRedisService().getUserMoney(l));
        }
        MsgSender.sendMsg2Player(new ResponseVo("gameService", "scoreChange", userMoneys), this.getUsers());
    }
}