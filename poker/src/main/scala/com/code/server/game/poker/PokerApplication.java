package com.code.server.game.poker;

import com.code.server.game.poker.config.ServerConfig;
import com.code.server.game.poker.service.MsgDispatch;
import com.code.server.game.room.RoomMsgDispatch;
import com.code.server.kafka.MsgConsumer;
import com.code.server.util.SpringUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(scanBasePackages = {"com.code.server.*"})
@EnableConfigurationProperties({ServerConfig.class})
public class PokerApplication {

	public static void main(String[] args) {
		SpringApplication.run(PokerApplication.class, args);
		ServerConfig serverConfig = SpringUtil.getBean(ServerConfig.class);
		MsgConsumer.startAConsumer("gameService", serverConfig.getServerId(), MsgDispatch::dispatch);
		MsgConsumer.startAConsumer("reconnService", serverConfig.getServerId(), MsgDispatch::dispatch);
		MsgConsumer.startAConsumer("pokerRoomService", serverConfig.getServerId(), MsgDispatch::dispatch);
		MsgConsumer.startAConsumer("roomService", serverConfig.getServerId(), RoomMsgDispatch::dispatch);
	}
}
