package com.code.server.game.poker.hitgoldflower;

import com.code.server.constant.exception.DataNotFoundException;
import com.code.server.constant.game.UserBean;
import com.code.server.game.poker.config.ServerConfig;
import com.code.server.game.poker.zhaguzi.RoomYSZ;
import com.code.server.game.room.kafka.MsgSender;
import com.code.server.redis.service.RedisManager;
import com.code.server.util.IdWorker;
import com.code.server.util.SpringUtil;

import java.util.*;

/**
 * Created by sunxianping on 2019-06-05.
 */
public class RoomYSZLongcheng extends RoomYSZ {

    private Map<Long, Long> playerParentMap = new HashMap<>();



    public static RoomYSZLongcheng createYSZRoom_(long userId, int gameNumber, int personNumber, int cricleNumber, int multiple, int caiFen,
                                         int menPai, String gameType, String roomType, boolean isAA, boolean isJoin,
                                         String clubId, String clubRoomModel, int goldRoomType, int goldRoomPermission,int otherMode) throws DataNotFoundException {
        ServerConfig serverConfig = SpringUtil.getBean(ServerConfig.class);

        RoomYSZLongcheng room = new RoomYSZLongcheng();

        room.personNumber = personNumber;
        room.roomId = getRoomIdStr(genRoomId(serverConfig.getServerId()));
        room.createUser = userId;
        room.gameType = gameType;
        room.roomType = roomType;
        room.isAA = isAA;
        room.isCreaterJoin = isJoin;
        room.multiple = multiple;
        room.caiFen = caiFen;
        room.menPai = menPai;
        room.bankerId = userId;
        room.cricleNumber = cricleNumber;
        room.setClubId(clubId);
        room.setClubRoomModel(clubRoomModel);
//        room.isRobotRoom = true;
        room.goldRoomType = goldRoomType;
        room.goldRoomPermission = goldRoomPermission;
        room.otherMode = otherMode;
        room.init(gameNumber, multiple);

//        RoomManager.addRoom(room.roomId, "" + serverConfig.getServerId(), room);
        IdWorker idWorker = new IdWorker(serverConfig.getServerId(), 0);
        room.setUuid(idWorker.nextId());

        return room;
    }

    @Override
    protected boolean isCanJoinCheckMoney(long userId) {
        if (!super.isCanJoinCheckMoney(userId)) {
            return false;
        }
        UserBean userBean = RedisManager.getUserRedisService().getUserBean(userId);
        long parentId = userBean.getReferee();
        if (parentId == 0) {
            return false;
        }
        long need = getSameParentNum(parentId) + 1;
        if (goldRoomType == 20) {
            need *= 2;
        }
        if(RedisManager.getUserRedisService().getUserMoney(parentId)<need){
            return false;
        }
        return true;
    }

    @Override
    public int joinRoom(long userId, boolean isJoin) {
        int rtn =  super.joinRoom(userId, isJoin);
        if (rtn == 0) {
            UserBean userBean = RedisManager.getUserRedisService().getUserBean(userId);
            playerParentMap.put(userId, (long) userBean.getReferee());
            //自动准备
            getReady(userId);
        }
        return rtn;
    }

    public void spendMoney() {

    }

    public int getNeedMoney() throws DataNotFoundException {

     return 0;
    }

    protected void goldRoomStart() {
        for (long userId : users) { //扣除费用
            long parentId = playerParentMap.get(userId);
            int need = 1;
            if (goldRoomType == 20) {
                need *= 2;
            }
            RedisManager.getUserRedisService().addUserMoney(parentId, -need);
            sendSpendMoneyLongcheng(parentId, -need);
        }
    }

    @Override
    public void addUserSocre(long userId, double score) {
        double s = userScores.get(userId);
        userScores.put(userId, s + score);
        RedisManager.getUserRedisService().addUserGold(userId, score);
    }



    public void clearReadyStatusGoldRoom(boolean isAddGameNum) {
        if (isGoldRoom()) {
            Set<Long> removeList = new HashSet<>();
            List<Long> removePartner = new ArrayList<>();
            Map<Long, Integer> joinNextPartnerPlayerNum = new HashMap<>();
            for (long userId : this.users) {
                boolean isOnLine = RedisManager.getUserRedisService().getGateId(userId) != null;
                if (!isOnLine) {
                    removeList.add(userId);
                }
                double gold = RedisManager.getUserRedisService().getUserGold(userId);
                //小于goldtype
                if (gold < getOutGold()) {
                    removeList.add(userId);
                }else{
                    int num = joinNextPartnerPlayerNum.getOrDefault(playerParentMap.get(userId), 0);
                    num += 1;
                    joinNextPartnerPlayerNum.put( playerParentMap.get(userId), num);
                }
            }

            joinNextPartnerPlayerNum.forEach((parentId, num)->{
                if (RedisManager.getUserRedisService().getUserMoney(parentId) < num) {
                    removePartner.add(parentId);
                }
            });


            this.users.forEach(uid->{
                long parentId = playerParentMap.get(uid);
                if (removePartner.contains(parentId)) {
                    removeList.add(uid);
                }
            });
            for (long userId : removeList) {
                this.quitRoom(userId);
                MsgSender.sendMsg2Player("roomService", "quitRoomKick", "quit", userId);
            }
        }
    }


    /**
     * 房间中同样的上级玩家个数
     * @param pid
     * @return
     */
    private long getSameParentNum(long pid) {
        return getPlayerParentMap().values().stream().filter(parentId->pid == parentId).count();
    }



    public Map<Long, Long> getPlayerParentMap() {
        return playerParentMap;
    }

    public RoomYSZLongcheng setPlayerParentMap(Map<Long, Long> playerParentMap) {
        this.playerParentMap = playerParentMap;
        return this;
    }
}
