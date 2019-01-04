package com.code.server.game.poker.yuxiaxie;

import com.code.server.constant.kafka.KafkaMsgKey;
import com.code.server.constant.response.ResponseVo;
import com.code.server.game.poker.config.ServerConfig;
import com.code.server.game.room.Room;
import com.code.server.game.room.service.IRobot;
import com.code.server.kafka.MsgProducer;
import com.code.server.util.SpringUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sunxianping on 2019-01-03.
 */
public class RoomYuxiaxieRobot implements IRobot {
    long time = 60000 ;
    @Override
    public void execute() {

    }

    @Override
    public void doExecute(Room room) {
        if (room == null) {
            return;
        }

        if (!(room instanceof RoomYuxiaxie)) {
            return;
        }

        RoomYuxiaxie roomYuxiaxie = (RoomYuxiaxie) room;

        if (roomYuxiaxie.getGame() != null ) {

            long now = System.currentTimeMillis();
            if (roomYuxiaxie.getLastOperateTime() + time > now) {
                GameYuxiaxie gameYuxiaxie = (GameYuxiaxie)room.getGame();
                if (gameYuxiaxie.getState() == GameYuxiaxie.STATE_BET) {
                    gameOver(room);
                }
            }
        }
    }



    private void gameOver(Room room){
        String roomId = room.getRoomId();
        int partition = SpringUtil.getBean(ServerConfig.class).getServerId();
        KafkaMsgKey msgKey = new KafkaMsgKey();

        msgKey.setRoomId(roomId);
        msgKey.setPartition(partition);
        msgKey.setUserId(room.getBankerId());

        Map<String, Object> put = new HashMap();


        ResponseVo result = new ResponseVo("gameService", "gameOver", put);
        SpringUtil.getBean(MsgProducer.class).send2Partition("gameService", partition, msgKey, result);
    }

}
