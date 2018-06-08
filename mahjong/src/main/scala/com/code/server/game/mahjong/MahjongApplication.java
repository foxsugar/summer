package com.code.server.game.mahjong;

import com.code.server.constant.data.DataManager;
import com.code.server.constant.exception.RegisterFailedException;
import com.code.server.game.mahjong.config.ServerConfig;
import com.code.server.redis.config.IConstant;
import com.code.server.redis.service.RedisManager;
import com.code.server.util.SpringUtil;
import com.code.server.util.ThreadPool;
import com.code.server.util.timer.GameTimer;
import com.code.server.util.timer.TimerNode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.io.IOException;

@SpringBootApplication(scanBasePackages = {"com.code.server.*"})
@EnableConfigurationProperties({ServerConfig.class})
public class MahjongApplication {

    public static void main(String[] args) throws RegisterFailedException, IOException {
        SpringApplication.run(MahjongApplication.class, args);

        ServerConfig serverConfig = SpringUtil.getBean(ServerConfig.class);
        //加载数据
        DataManager.initData(serverConfig.getDataFile());
        //注册
        RedisManager.getGameRedisService().register(serverConfig.getServerType(),serverConfig.getServerId());
        //timer
        ThreadPool.execute(GameTimer.getInstance()::fire);
        //心跳
        System.out.println("启动心跳");
        GameTimer.addTimerNode(new TimerNode(System.currentTimeMillis(), IConstant.SECOND_5,true,()->RedisManager.getGameRedisService().heart(serverConfig.getServerId())));

        //机器人


//        GameTimer.addTimerNode(serverConfig.getRobotExeCycle(),true, RobotManager::excute);
//        MsgConsumer.startAConsumer("gameLogicService", serverConfig.getServerId(), MsgDispatch::dispatch);
//        MsgConsumer.startAConsumer("reconnService", serverConfig.getServerId(), MsgDispatch::dispatch);
//        MsgConsumer.startAConsumer("mahjongRoomService", serverConfig.getServerId(), MsgDispatch::dispatch);
//        MsgConsumer.startAConsumer("roomService", serverConfig.getServerId(), RoomMsgDispatch::dispatch);
    }
}
