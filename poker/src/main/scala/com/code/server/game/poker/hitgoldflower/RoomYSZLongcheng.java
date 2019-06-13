package com.code.server.game.poker.hitgoldflower;

import com.code.server.constant.exception.DataNotFoundException;
import com.code.server.constant.game.UserBean;
import com.code.server.game.poker.config.ServerConfig;
import com.code.server.game.poker.zhaguzi.RoomYSZ;
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
                                         String clubId, String clubRoomModel, int goldRoomType, int goldRoomPermission) throws DataNotFoundException {
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
        room.init(gameNumber, multiple);

//        RoomManager.addRoom(room.roomId, "" + serverConfig.getServerId(), room);
        IdWorker idWorker = new IdWorker(serverConfig.getServerId(), 0);
        room.setUuid(idWorker.nextId());

        return room;
    }

    @Override
    protected boolean isCanJoinCheckMoney(long userId) {
        if (RedisManager.getUserRedisService().getUserMoney(userId) < goldRoomType) {
            return false;
        }
        UserBean userBean = RedisManager.getUserRedisService().getUserBean(userId);
        long parentId = userBean.getReferee();
        if (parentId == 0) {
            return false;
        }
        if(RedisManager.getUserRedisService().getUserMoney(parentId)<1){
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
        }
        return rtn;
    }


    //    @Override
//    protected int getOutGold() {
//        if (isGoldRoom() && this.goldRoomPermission != GOLD_ROOM_PERMISSION_DEFAULT) {
//            //todo 根据闷牌 得到出场限制
////            return super.getOutGold();
//            return computeEnterGold() / 2;
//        } else{
//            return super.getOutGold();
//        }
//    }


    public void clearReadyStatusGoldRoom(boolean isAddGameNum) {
        if (isGoldRoom()) {
            Set<Long> removeList = new HashSet<>();
            List<Long> removePartner = new ArrayList<>();
            Map<Long, Integer> joinNextPartnerPlayerNum = new HashMap<>();
            for (long userId : this.users) {
                double gold = RedisManager.getUserRedisService().getUserGold(userId);
                //小于goldtype
                if (gold < goldRoomType) {
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
            }
        }
    }


    protected void goldRoomStart() {
        //房卡消耗
//        if (isGoldRoom()) {
//            if (!this.users.contains(this.bankerId)) {
//                this.bankerId = this.users.get(0);
//            }
//            double cost = this.getGoldRoomType() / 10;
//            //50底分 抽成翻倍
//            if (this.getGoldRoomType() == 50) {
//                cost *= 2;
//            }
//
//            for (long userId : users) {
//                //扣除费用
//                RedisManager.getUserRedisService().addUserGold(userId, -cost);
//                //返利
//                UserBean userBean = RedisManager.getUserRedisService().getUserBean(userId);
//                RedisManager.getAgentRedisService().addRebate(userId, userBean.getReferee(), 1, cost / 100,cost);
//            }
//            //
//            RedisManager.getLogRedisService().addGoldIncome(getGameLogKeyStr(), cost * users.size());
//        }
    }


    public Map<Long, Long> getPlayerParentMap() {
        return playerParentMap;
    }

    public RoomYSZLongcheng setPlayerParentMap(Map<Long, Long> playerParentMap) {
        this.playerParentMap = playerParentMap;
        return this;
    }
}
