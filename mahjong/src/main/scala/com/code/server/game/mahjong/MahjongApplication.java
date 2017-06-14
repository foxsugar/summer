package com.code.server.game.mahjong;

import com.code.server.game.mahjong.config.ServerConfig;
import com.code.server.game.mahjong.service.MsgDispatch;
import com.code.server.game.room.RoomMsgDispatch;
import com.code.server.kafka.MsgConsumer;
import com.code.server.util.SpringUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(scanBasePackages = {"com.code.server.*"})
@EnableConfigurationProperties({ServerConfig.class})
public class MahjongApplication {

    public static void main(String[] args) {
        SpringApplication.run(MahjongApplication.class, args);

        ServerConfig serverConfig = SpringUtil.getBean(ServerConfig.class);
        MsgConsumer.startAConsumer("gameLogicService", serverConfig.getServerId(), MsgDispatch::dispatch);
        MsgConsumer.startAConsumer("reconnService", serverConfig.getServerId(), MsgDispatch::dispatch);
        MsgConsumer.startAConsumer("mahjongRoomService", serverConfig.getServerId(), MsgDispatch::dispatch);
        MsgConsumer.startAConsumer("roomService", serverConfig.getServerId(), RoomMsgDispatch::dispatch);
    }
}
