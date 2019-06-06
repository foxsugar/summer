package com.code.server.game.poker.hitgoldflower;

import com.code.server.constant.game.UserBean;
import com.code.server.game.poker.zhaguzi.RoomYSZ;
import com.code.server.redis.service.RedisManager;

import java.util.*;

/**
 * Created by sunxianping on 2019-06-05.
 */
public class RoomYszLongcheng extends RoomYSZ {

    private Map<Long, Long> playerParentMap = new HashMap<>();

    @Override
    protected boolean isCanJoinCheckMoney(long userId) {
        if (RedisManager.getUserRedisService().getUserMoney(userId) < goldRoomType) {
            return false;
        }
        UserBean userBean = RedisManager.getUserRedisService().getUserBean(userId);
        long parentId = userBean.getReferee();
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





    public Map<Long, Long> getPlayerParentMap() {
        return playerParentMap;
    }

    public RoomYszLongcheng setPlayerParentMap(Map<Long, Long> playerParentMap) {
        this.playerParentMap = playerParentMap;
        return this;
    }
}
