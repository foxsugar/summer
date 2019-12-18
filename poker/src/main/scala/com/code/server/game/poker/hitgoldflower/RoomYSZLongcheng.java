package com.code.server.game.poker.hitgoldflower;

import com.code.server.constant.exception.DataNotFoundException;
import com.code.server.constant.game.UserBean;
import com.code.server.constant.response.ErrorCode;
import com.code.server.constant.response.IfaceRoomVo;
import com.code.server.constant.response.ResponseVo;
import com.code.server.game.poker.config.ServerConfig;
import com.code.server.game.poker.zhaguzi.RoomYSZ;
import com.code.server.game.room.IfaceRoom;
import com.code.server.game.room.Room;
import com.code.server.game.room.kafka.MsgSender;
import com.code.server.game.room.service.RoomManager;
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
    public int quitRoom(long userId) {
        if (isGoldRoom()) {
            if (!this.users.contains(userId)) {
                return ErrorCode.CANNOT_QUIT_ROOM_NOT_EXIST;
            }

            if (isInGame && this.game.users.contains(userId)) {
                return ErrorCode.CANNOT_QUIT_ROOM_IS_IN_GAME;
            }

//            List<Long> noticeList = new ArrayList<>();
//            noticeList.addAll(this.getUsers());

            //删除玩家房间映射关系
            roomRemoveUser(userId);


            if (goldRoomPermission == GOLD_ROOM_PERMISSION_DEFAULT) {
                RoomManager.getInstance().moveFull2NotFullRoom(this);
            }

            //todo 如果都退出了  删除房间
//            if (this.users.size() == 0 ) {
//
//                RoomManager.removeRoom(this.roomId);
//            }
            noticeQuitRoom(userId);
            return 0;
        } else return super.quitRoom(userId);
    }

    public static int getAllRoom(long userId, String gameType){
        List<IfaceRoomVo> roomList = new ArrayList<>();
        for(IfaceRoom room : RoomManager.getInstance().getRooms().values()){
            Room r = (Room)room;
            if (r.getGameType().equals( gameType)) {
                roomList.add(r.toVo(0));
            }
        }
        MsgSender.sendMsg2Player(new ResponseVo("pokerRoomService", "getYSZRoom", roomList), userId);
        return 0;
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
//        if(RedisManager.getUserRedisService().getUserMoney(parentId)<need){
//            return false;
//        }
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
//            RedisManager.getUserRedisService().addUserMoney(parentId, -need);
//            sendSpendMoneyLongcheng(parentId, -need);
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
