package com.code.server.game.poker.service;

import com.code.server.constant.exception.DataNotFoundException;
import com.code.server.constant.kafka.IKafaTopic;
import com.code.server.constant.kafka.IkafkaMsgId;
import com.code.server.constant.kafka.KafkaMsgKey;
import com.code.server.game.poker.config.ServerConfig;
import com.code.server.game.poker.doudizhu.RoomDouDiZhu;
import com.code.server.game.poker.doudizhu.RoomDouDiZhuGold;
import com.code.server.game.poker.doudizhu.RoomDouDiZhuPlus;
import com.code.server.game.poker.hitgoldflower.RoomYSZLongcheng;
import com.code.server.game.poker.paijiu.RoomTuitongziGold;
import com.code.server.game.poker.zhaguzi.RoomYSZ;
import com.code.server.game.room.IfaceRoom;
import com.code.server.game.room.Room;
import com.code.server.game.room.RoomExtendGold;
import com.code.server.game.room.service.RoomManager;
import com.code.server.kafka.MsgProducer;
import com.code.server.util.SpringUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sunxianping on 2018/6/5.
 */
public class PokerGoldRoom extends RoomExtendGold {


    @Override
    public Room getDefaultGoldRoomInstance(long userId, String roomType, String gameType, Integer goldRoomType) {
        Room room =  create(userId, roomType, gameType, goldRoomType);
        int serverId = SpringUtil.getBean(ServerConfig.class).getServerId();
        String roomId = Room.getRoomIdStr(Room.genRoomId(serverId));
        room.setRoomId(roomId);
        room.setGameType(gameType);
        room.setRoomType(roomType);
        room.setGoldRoomType(goldRoomType);
        room.setGoldRoomPermission(GOLD_ROOM_PERMISSION_DEFAULT);
        room.setMultiple(goldRoomType);
        return room;
    }

    protected boolean isCanAgreeDissloution(int agreeNum) {
        ServerConfig serverConfig = SpringUtil.getBean(ServerConfig.class);
        if (serverConfig.getDissloutionRoomMustAllAgree() == 1) {
            return agreeNum >= personNumber  && agreeNum >= 2;
        }else{
            return agreeNum >= personNumber - 1 && agreeNum >= 2;
        }
    }


    /**
     * 发送返利
     * @param userId
     * @param money
     */
    public void sendCenterAddRebateLongcheng(long userId, double money){
        Map<String, Object> addMoney = new HashMap<>();
        addMoney.put("userId", userId);
        addMoney.put("money", money);

        KafkaMsgKey kafkaMsgKey = new KafkaMsgKey().setMsgId(IkafkaMsgId.KAFKA_MSG_ID_ADD_REBATE_LONGCHENG);
        MsgProducer msgProducer = SpringUtil.getBean(MsgProducer.class);
        msgProducer.send(IKafaTopic.CENTER_TOPIC, kafkaMsgKey, addMoney);
    }



    public void add2GoldPool() {
        int serverId = SpringUtil.getBean(ServerConfig.class).getServerId();
        RoomManager.getInstance().addNotFullGoldRoom(this);
        RoomManager.addRoom(this.getRoomId(), "" + serverId, this);
    }

    public static Room create(long userId, String roomType, String gameType, int goldRoomType) {

        Room room = null;
        switch (roomType) {
            case "2":
                room = new RoomDouDiZhuGold();
                break;
            case "3":
                room = new RoomDouDiZhuPlus();
                break;


            default:

                break;
        }

        switch (gameType) {
            case "285":

                int gameNumber = 285;
                int personNumber = 5;
                int cricleNumber = 15;
                int multiple = goldRoomType;
                int fen = 0;
                int hidden = 0;
                boolean isAA = true;
                boolean isJoin = true;
                int goldRoomPermission = IfaceRoom.GOLD_ROOM_PERMISSION_DEFAULT;

                RoomYSZ roomYSZ = null;

                try {
                    roomYSZ = RoomYSZ.createYSZRoom_(userId, gameNumber, personNumber, cricleNumber, multiple, fen, hidden,
                            gameType, roomType, isAA, isJoin, null, null, goldRoomType, goldRoomPermission);
                } catch (DataNotFoundException e) {
                    e.printStackTrace();
                }


                return roomYSZ;

            case "450":
                RoomYSZ roomlc = null;
                try {
                    roomlc = RoomYSZLongcheng.createYSZRoom_(userId, 100, 6, 20, goldRoomType, 0, 1,
                            gameType, roomType, true, true, null, null, goldRoomType, IfaceRoom.GOLD_ROOM_PERMISSION_DEFAULT,2);
                } catch (DataNotFoundException e) {
                    e.printStackTrace();
                }
                return roomlc;

            case "456":
                RoomTuitongziGold roomtuitongzi = null;
                roomtuitongzi = RoomTuitongziGold.createRoom_(userId,"3","456",4,"","",0,
                        false,2,5,0,false,0,200,goldRoomType);
                return roomtuitongzi;

            case "403":

                Room roomDoudizhuZLB = RoomDouDiZhu.createRoom_(userId,4,-1,"403","2",
                        true,false,true,3,3,0,"","",0,0);
                return roomDoudizhuZLB;
        }

//        ConsumerRecord(topic = pokerRoomService, partition = 1, offset = 6930, CreateTime = 1562809072703, checksum = 2539413064, serialized key size = 36, serialized value size = 190, key = {"msgId":0,"userId":3,"partition":0}, value = {"service":"pokerRoomService","method":"createRoom",
//                "params":{"gameType":"403","gameNumber":"8","maxMultiple":"-1","roomType":"2","otherMode":"1","isAA":false,"showChat":true,"isJoin":true}})

        return room;
    }
}
