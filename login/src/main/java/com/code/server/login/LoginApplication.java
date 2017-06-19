package com.code.server.login;

import com.code.server.login.config.ServerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(scanBasePackages={"com.code.server.*"})
@EnableConfigurationProperties({ServerConfig.class})
public class LoginApplication {

	public static void main(String[] args) {
		SpringApplication.run(LoginApplication.class, args);
		//timer
		//ThreadPool.execute(()-> GameTimer.getInstance().fire());

		//CheckHeart.check();
		//心跳
		//GameTimer.addTimerNode(new TimerNode(System.currentTimeMillis(), IConstant.SECOND_5,true,()-> RedisManager.getGameRedisService().heart(serverConfig.getServerId())));


//		MsgConsumer.startAConsumer("userService",0,new UserServiceConsumer());
//		MsgConsumer.startAConsumer("userService",1,new UserServiceConsumer());
//		MsgConsumer.startAConsumer("userService",2,new UserServiceConsumer());
//		MsgConsumer.startAConsumer("userService",3,new UserServiceConsumer());
//		MsgConsumer.startAConsumer("userService",4,new UserServiceConsumer());


	}
}
