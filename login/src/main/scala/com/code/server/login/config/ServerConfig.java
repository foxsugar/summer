package com.code.server.login.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by sunxianping on 2017/3/17.
 */
@ConfigurationProperties(prefix = "serverConfig")
public class ServerConfig {
    private String serverType;
    private int serverId;

    private int port;
    private int isCenter;

    private int gameRpcServerPort;
    private String adminRpcHost;
    private int adminRpcPort;


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

    public int getPort() {
        return port;
    }

    public ServerConfig setPort(int port) {
        this.port = port;
        return this;
    }

    public int getIsCenter() {
        return isCenter;
    }

    public ServerConfig setIsCenter(int isCenter) {
        this.isCenter = isCenter;
        return this;
    }

    public int getGameRpcServerPort() {
        return gameRpcServerPort;
    }

    public ServerConfig setGameRpcServerPort(int gameRpcServerPort) {
        this.gameRpcServerPort = gameRpcServerPort;
        return this;
    }

    public String getAdminRpcHost() {
        return adminRpcHost;
    }

    public ServerConfig setAdminRpcHost(String adminRpcHost) {
        this.adminRpcHost = adminRpcHost;
        return this;
    }

    public int getAdminRpcPort() {
        return adminRpcPort;
    }

    public ServerConfig setAdminRpcPort(int adminRpcPort) {
        this.adminRpcPort = adminRpcPort;
        return this;
    }
}
