package com.code.server.gate.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by sunxianping on 2017/3/17.
 */
@ConfigurationProperties(prefix = "serverConfig")
public class ServerConfig {
    private String serverType;
    private int serverId;
    private String host;
    private String domain;

    private int port;


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

    public String getHost() {
        return host;
    }

    public ServerConfig setHost(String host) {
        this.host = host;
        return this;
    }

    public String getDomain() {
        return domain;
    }

    public ServerConfig setDomain(String domain) {
        this.domain = domain;
        return this;
    }
}
