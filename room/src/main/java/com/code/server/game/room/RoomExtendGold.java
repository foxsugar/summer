package com.code.server.game.room;

import com.code.server.constant.data.DataManager;
import com.code.server.constant.data.StaticDataProto;
import com.code.server.constant.exception.DataNotFoundException;
import com.code.server.constant.game.UserBean;
import com.code.server.game.room.service.RoomManager;
import com.code.server.redis.service.RedisManager;

/**
 * Created by sunxianping on 2018/4/11.
 */
public class RoomExtendGold extends Room {


    public void init(int gameNumber, int multiple) throws DataNotFoundException {
        super.init(gameNumber, multiple);
        this.isRobotRoom = true;
    }

    @Override
    public void startGame() {
        //todo 庄家退出游戏的处理
        if (isGoldRoom() && !this.users.contains(this.bankerId)) {
            this.bankerId = this.users.get(0);
        }
        if (isGiveAgentRebate()) {

            int cost = this.getGoldRoomType() / 10;
            for (long userId : users) {
                //扣除费用
                RedisManager.getUserRedisService().addUserGold(userId, -cost);
                UserBean userBean = RedisManager.getUserRedisService().getUserBean(userId);
                RedisManager.getAgentRedisService().addRebate(userId, userBean.getReferee(), 1, cost / 100);
            }
        }
        super.startGame();
    }

    @Override
    public void addUserSocre(long userId, double score) {
        super.addUserSocre(userId, score);
        //todo 金币改变
        if (isGoldRoom()) {
            RedisManager.getUserRedisService().addUserGold(userId, score);

        }

    }

    /**
     * 是否给代理返利
     *
     * @return
     */
    protected boolean isGiveAgentRebate() {
        return false;
    }

    @Override
    public boolean isGoldRoom() {
        return goldRoomPermission != GOLD_ROOM_PERMISSION_NONE;
    }

    @Override
    public int joinRoom(long userId, boolean isJoin) {
        //随机匹配的金币房
        if (isGoldRoom() && goldRoomPermission == GOLD_ROOM_PERMISSION_DEFAULT) {
            int rtn = super.joinRoom(userId, isJoin);
            if (rtn != 0) {
                return rtn;
            }
            //如果房间已满 加入已满房间
            if (this.isRoomFull()) {
                RoomManager.getInstance().moveGoldRoomNotFull2Full(this);
            }
            return 0;
        } else {
            return super.joinRoom(userId, isJoin);
        }

    }

    @Override
    protected boolean isCanJoinCheckMoney(long userId) {
        //todo 检验金币
        if (isGoldRoom()) {
            double gold = RedisManager.getUserRedisService().getUserGold(userId);
            if (gold < getEnterGold()) {
                return false;
            }
        } else {

            return super.isCanJoinCheckMoney(userId);
        }
        return true;
    }

    @Override
    public int quitRoom(long userId) {
        if (isGoldRoom()) {
            int rtn = super.quitRoom(userId);
            if (rtn != 0) {
                return rtn;
            }
            if (goldRoomPermission == GOLD_ROOM_PERMISSION_DEFAULT) {
                RoomManager.getInstance().moveFull2NotFullRoom(this);
            }

            //todo 如果都退出了 并且不是第一局 删除房间
            if (this.users.size() == 0 && this.curGameNumber > 1) {

                RoomManager.removeRoom(this.roomId);
            }
            return 0;
        } else return super.quitRoom(userId);
    }

    protected boolean isRoomFull() {
        return this.users.size() >= personNumber;
    }


    @Override
    public boolean isRoomOver() {
        //金币房 不解散
        return !isGoldRoom() && super.isRoomOver();
    }

    @Override
    public void clearReadyStatus(boolean isAddGameNum) {
        //todo 如果 金币不够 退出
        clearReadyStatusGoldRoom(isAddGameNum);

        super.clearReadyStatus(isAddGameNum);
    }


    public void clearReadyStatusGoldRoom(boolean isAddGameNum) {
        if (isGoldRoom()) {
            int minGold = getOutGold();
            for (long userId : this.users) {
                double gold = RedisManager.getUserRedisService().getUserGold(userId);
                if (gold < minGold) {
                    this.quitRoom(userId);
                }
            }

            //

        }
    }

    /**
     * 最小金币
     *
     * @return
     */
    protected int getOutGold() {
        StaticDataProto.RoomData roomData = DataManager.data.getRoomDataMap().get(gameType);
        if (roomData != null) {
            return roomData.getOutGoldMap().get(goldRoomType);
        }
        return this.getMultiple() * 20;
    }

    /**
     * 最小进场金币
     *
     * @return
     */
    protected int getEnterGold() {
        StaticDataProto.RoomData roomData = DataManager.data.getRoomDataMap().get(gameType);
        if (roomData != null) {

            return roomData.getEnterGoldMap().get(goldRoomType);
        }
        return this.getMultiple() * 20;
    }

    @Override
    public boolean isRobotRoom() {
        return true;
    }
}
