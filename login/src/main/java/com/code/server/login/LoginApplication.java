package com.code.server.login;

import com.code.server.login.config.ServerConfig;
import com.code.server.login.service.CenterService;
import com.code.server.login.service.CheckHeart;
import com.code.server.redis.config.IConstant;
import com.code.server.redis.service.RedisManager;
import com.code.server.util.SpringUtil;
import com.code.server.util.ThreadPool;
import com.code.server.util.timer.GameTimer;
import com.code.server.util.timer.TimerNode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(scanBasePackages={"com.code.server.*"})
@EnableConfigurationProperties({ServerConfig.class})
public class LoginApplication {

	public static void main(String[] args) {
		SpringApplication.run(LoginApplication.class, args);

		ServerConfig serverConfig = SpringUtil.getBean(ServerConfig.class);

		//中心服务器有的职能
		if (serverConfig.getIsCenter() == 1) {

			CenterService.work();
		}
		//心跳
		//GameTimer.addTimerNode(new TimerNode(System.currentTimeMillis(), IConstant.SECOND_5,true,()-> RedisManager.getGameRedisService().heart(serverConfig.getServerId())));


//		MsgConsumer.startAConsumer("userService",0,new UserServiceConsumer());
//		MsgConsumer.startAConsumer("userService",1,new UserServiceConsumer());
//		MsgConsumer.startAConsumer("userService",2,new UserServiceConsumer());
//		MsgConsumer.startAConsumer("userService",3,new UserServiceConsumer());
//		MsgConsumer.startAConsumer("userService",4,new UserServiceConsumer());


	}


}
