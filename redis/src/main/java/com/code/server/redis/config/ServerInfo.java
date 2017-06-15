package com.code.server.redis.config;

import java.time.LocalDateTime;

/**
 * Created by sunxianping on 2017/6/13.
 */
public class ServerInfo {
    private int serverId;
    private String serverType;
    private String startTime;
    private String host;
    private int port;
    private int sort;

    public ServerInfo() {
    }


    public ServerInfo(String serverType,int serverId, String host, int port) {
        this.serverType = serverType;
        this.serverId = serverId;
        this.startTime = LocalDateTime.now().toString();
        this.host = host;
        this.port = port;
    }


    public int getSort() {
        return sort;
    }

    public ServerInfo setSort(int sort) {
        this.sort = sort;
        return this;
    }

    public int getServerId() {
        return serverId;
    }

    public ServerInfo setServerId(int serverId) {
        this.serverId = serverId;
        return this;
    }

    public String getStartTime() {
        return startTime;
    }

    public ServerInfo setStartTime(String startTime) {
        this.startTime = startTime;
        return this;
    }

    public String getHost() {
        return host;
    }

    public ServerInfo setHost(String host) {
        this.host = host;
        return this;
    }

    public int getPort() {
        return port;
    }

    public ServerInfo setPort(int port) {
        this.port = port;
        return this;
    }

    public String getServerType() {
        return serverType;
    }

    public ServerInfo setServerType(String serverType) {
        this.serverType = serverType;
        return this;
    }
}
