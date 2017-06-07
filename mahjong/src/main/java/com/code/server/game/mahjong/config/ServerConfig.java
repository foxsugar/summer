package com.code.server.game.mahjong.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by sunxianping on 2017/3/17.
 */
@ConfigurationProperties(prefix = "serverConfig")
public class ServerConfig {
    private int serverId;
    private int port;
    //机器人执行周期
    private int robotExeCycle = 1000;
    private int isStartRobot = 1;


    public int getServerId() {
        return serverId;
    }

    public ServerConfig setServerId(int serverId) {
        this.serverId = serverId;
        return this;
    }


    public int getPort() {
        return port;
    }

    public ServerConfig setPort(int port) {
        this.port = port;
        return this;
    }


    public int getRobotExeCycle() {
        return robotExeCycle;
    }

    public ServerConfig setRobotExeCycle(int robotExeCycle) {
        this.robotExeCycle = robotExeCycle;
        return this;
    }
}
