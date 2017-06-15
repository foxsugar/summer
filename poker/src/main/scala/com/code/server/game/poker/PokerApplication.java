package com.code.server.game.poker;

import com.code.server.constant.exception.RegisterFailedException;
import com.code.server.game.poker.config.ServerConfig;
import com.code.server.game.poker.service.MsgDispatch;
import com.code.server.game.room.RoomMsgDispatch;
import com.code.server.kafka.MsgConsumer;
import com.code.server.redis.config.IConstant;
import com.code.server.redis.service.RedisManager;
import com.code.server.util.SpringUtil;
import com.code.server.util.ThreadPool;
import com.code.server.util.timer.GameTimer;
import com.code.server.util.timer.TimerNode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(scanBasePackages = {"com.code.server.*"})
@EnableConfigurationProperties({ServerConfig.class})
public class PokerApplication {

	public static void main(String[] args) throws RegisterFailedException {
		SpringApplication.run(PokerApplication.class, args);
		ServerConfig serverConfig = SpringUtil.getBean(ServerConfig.class);
		//注册
		RedisManager.getGameRedisService().register(serverConfig.getServerType(),serverConfig.getServerId());
		//timer
		ThreadPool.execute(()->GameTimer.getInstance().fire());
		//心跳
		GameTimer.addTimerNode(new TimerNode(System.currentTimeMillis(), IConstant.SECOND_5,true,()->RedisManager.getGameRedisService().heart(serverConfig.getServerId())));

//		MsgConsumer.startAConsumer("gameService", serverConfig.getServerId(), MsgDispatch::dispatch);
//		MsgConsumer.startAConsumer("reconnService", serverConfig.getServerId(), MsgDispatch::dispatch);
//		MsgConsumer.startAConsumer("pokerRoomService", serverConfig.getServerId(), MsgDispatch::dispatch);
//		MsgConsumer.startAConsumer("roomService", serverConfig.getServerId(), RoomMsgDispatch::dispatch);
	}
}
