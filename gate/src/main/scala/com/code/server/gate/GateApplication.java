package com.code.server.gate;

import com.code.server.gate.bootstarp.SocketServer;
import com.code.server.gate.config.ServerConfig;
import com.code.server.gate.config.ServerState;
import com.code.server.util.SpringUtil;
import com.code.server.util.ThreadPool;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(scanBasePackages = {"com.code.server.*"})
@EnableConfigurationProperties({ServerConfig.class})
public class GateApplication {

	public static void main(String[] args) {
		SpringApplication.run(GateApplication.class, args);
		SpringUtil.getBean(ServerConfig.class);

		ThreadPool.getInstance().executor.execute(new SocketServer());

		ServerState.isWork = true;
	}
}
