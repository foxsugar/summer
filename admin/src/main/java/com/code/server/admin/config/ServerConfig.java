package com.code.server.admin.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by sunxianping on 2017/3/17.
 */
@ConfigurationProperties(prefix = "serverConfig")
public class ServerConfig {
    private String serverType;
    private int serverId;


    private int adminRpcServerPort;
    private String gameRpcHost;
    private int gameRpcPort;



    public String getServerType() {
        return serverType;
    }

    public ServerConfig setServerType(String serverType) {
        this.serverType = serverType;
        return this;
    }

    public int getServerId() {
        return serverId;
    }

    public ServerConfig setServerId(int serverId) {
        this.serverId = serverId;
        return this;
    }

    public int getAdminRpcServerPort() {
        return adminRpcServerPort;
    }

    public ServerConfig setAdminRpcServerPort(int adminRpcServerPort) {
        this.adminRpcServerPort = adminRpcServerPort;
        return this;
    }

    public String getGameRpcHost() {
        return gameRpcHost;
    }

    public ServerConfig setGameRpcHost(String gameRpcHost) {
        this.gameRpcHost = gameRpcHost;
        return this;
    }

    public int getGameRpcPort() {
        return gameRpcPort;
    }

    public ServerConfig setGameRpcPort(int gameRpcPort) {
        this.gameRpcPort = gameRpcPort;
        return this;
    }
}
