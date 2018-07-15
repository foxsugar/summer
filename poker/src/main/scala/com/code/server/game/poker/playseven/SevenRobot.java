package com.code.server.game.poker.playseven;

import com.code.server.constant.game.IGameConstant;
import com.code.server.constant.kafka.KafkaMsgKey;
import com.code.server.game.poker.config.ServerConfig;
import com.code.server.game.poker.robot.ResponseRobotVo;
import com.code.server.game.room.Room;
import com.code.server.game.room.service.RoomManager;
import com.code.server.kafka.MsgProducer;
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
public class SevenRobot implements ISevenRobot,IGameConstant {

    @Override
    public void execute() {
        RoomManager.getInstance().getRobotRoom().forEach(this::doExecute);
    }

    public void doExecute(Room room) {
        if (room == null && room.getGame()==null) {
            return;
        }

        if (room.getGame() instanceof GamePlaySeven) {
            GamePlaySeven game = (GamePlaySeven) room.getGame();
            long now = System.currentTimeMillis();
            //执行
            if(now > game.lastOperateTime + SECOND * 5){
                switch (game.step) {
                    case STEP_RENSHU:
                        renShu(game);
                        break;
                    case STEP_FANZHU:
                        fanZhu(game);
                        break;
                    case STEP_GET_CARD_FINISH:
                        renShu(game);
                        break;
                }
            }
        }
    }

    @Override
    public void shouQi(GamePlaySeven game) {

    }

    @Override
    public void danLiang(GamePlaySeven game) {

    }

    @Override
    public void shuangLiang(GamePlaySeven game) {

    }

    @Override
    public void fanZhu(GamePlaySeven game) {
        String roomId = game.getRoom().getRoomId();
        int partition = SpringUtil.getBean(ServerConfig.class).getServerId();
        KafkaMsgKey msgKey = new KafkaMsgKey();

        msgKey.setRoomId(roomId);
        msgKey.setPartition(partition);

        Map<String, Object> put = new HashMap();
        put.put("fan",false);
        put.put("fan",0);

        for (PlayerCardInfoPlaySeven p : game.getPlayerCardInfos().values()) {
            if("1".equals(p.fanZhu)){
                msgKey.setUserId(p.getUserId());
                put.put("userId",p.getUserId());
            }
        }

        ResponseRobotVo result = new ResponseRobotVo("gameService", "fanZhu",put);
        SpringUtil.getBean(MsgProducer.class).send2Partition("gameService",partition, msgKey, result);
    }

    @Override
    public void renShu(GamePlaySeven game) {
        String roomId = game.getRoom().getRoomId();
        int partition = SpringUtil.getBean(ServerConfig.class).getServerId();
        KafkaMsgKey msgKey = new KafkaMsgKey();

        msgKey.setRoomId(roomId);
        msgKey.setPartition(partition);

        Map<String, Object> put = new HashMap();
        put.put("renshu",false);

        for (PlayerCardInfoPlaySeven p : game.getPlayerCardInfos().values()) {
            if("1".equals(p.renShu)){
                msgKey.setUserId(p.getUserId());
                put.put("userId",p.getUserId());
            }
        }

        ResponseRobotVo result = new ResponseRobotVo("gameService", "renShu",put);
        SpringUtil.getBean(MsgProducer.class).send2Partition("gameService",partition, msgKey, result);
    }


    @Override
    public void noticeGetCardAgain(GamePlaySeven game) {
        String roomId = game.getRoom().getRoomId();
        int partition = SpringUtil.getBean(ServerConfig.class).getServerId();
        KafkaMsgKey msgKey = new KafkaMsgKey();

        msgKey.setRoomId(roomId);
        msgKey.setPartition(partition);


        ResponseRobotVo result = new ResponseRobotVo("gameService", "dealAgain",0);
        SpringUtil.getBean(MsgProducer.class).send2Partition("gameService",partition, msgKey, result);
    }

    @Override
    public void changeTableCards(GamePlaySeven game) {

    }

    @Override
    public void play(GamePlaySeven game) {

    }
}
