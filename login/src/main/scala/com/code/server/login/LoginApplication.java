package com.code.server.login;

import com.code.server.login.config.ServerConfig;
import com.code.server.login.rpc.RpcManager;
import com.code.server.login.service.CenterService;
import com.code.server.login.service.ServerManager;
import com.code.server.util.SpringUtil;
import com.code.server.util.ThreadPool;
import com.code.server.util.timer.GameTimer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(scanBasePackages={"com.code.server.*"})
@EnableConfigurationProperties({ServerConfig.class})
public class LoginApplication {

	public static void main(String[] args) {
		SpringApplication.run(LoginApplication.class, args);

		ServerConfig serverConfig = SpringUtil.getBean(ServerConfig.class);

		ServerManager.init();
		System.out.println(ServerManager.constant.toString());
		//中心服务器有的职能
		if (serverConfig.getIsCenter() == 1) {
			//timer
			ThreadPool.execute(GameTimer.getInstance()::fire);
			CenterService.work();
			//rpc服务
			RpcManager.getInstance().startGameRpcServer();

			//检测rpc
			RpcManager.getInstance().checkGameRpcServerWork();
		}

//		MsgConsumer.startAConsumer("userService",0,new UserServiceConsumer());
//		MsgConsumer.startAConsumer("userService",1,new UserServiceConsumer());
//		MsgConsumer.startAConsumer("userService",2,new UserServiceConsumer());
//		MsgConsumer.startAConsumer("userService",3,new UserServiceConsumer());
//		MsgConsumer.startAConsumer("userService",4,new UserServiceConsumer());


	}


}
