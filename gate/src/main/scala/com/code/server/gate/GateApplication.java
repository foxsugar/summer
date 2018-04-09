package com.code.server.gate;

import com.code.server.constant.exception.RegisterFailedException;
import com.code.server.gate.bootstarp.SocketServer;
import com.code.server.gate.config.ServerConfig;
import com.code.server.gate.config.ServerState;
import com.code.server.redis.config.IConstant;
import com.code.server.redis.service.RedisManager;
import com.code.server.util.SpringUtil;
import com.code.server.util.ThreadPool;
import com.code.server.util.timer.GameTimer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(scanBasePackages = {"com.code.server.*"})
@EnableConfigurationProperties({ServerConfig.class})
public class GateApplication {

    public static void main(String[] args) throws RegisterFailedException {
        SpringApplication.run(GateApplication.class, args);
        SpringUtil.getBean(ServerConfig.class);

        ThreadPool.getInstance().executor.execute(new SocketServer());

        ServerState.isWork = true;

        //配置文件
        ServerConfig serverConfig = SpringUtil.getBean(ServerConfig.class);

        //注册服务
        RedisManager.getGateRedisService().register(serverConfig.getServerType(), serverConfig.getServerId(), serverConfig.getHost(), serverConfig.getDomain(), serverConfig.getNetPort());
        //心跳

        //定时器
        ThreadPool.execute(() -> GameTimer.getInstance().fire());

        GameTimer.addTimerNode( IConstant.SECOND_5, true, () -> RedisManager.getGateRedisService().heart(serverConfig.getServerId()));
//		//kafka消费者
//		MsgConsumer.startAConsumer(IKafaTopic.GATE_TOPIC,serverConfig.getServerId(), new GateConsumer());


    }
}
