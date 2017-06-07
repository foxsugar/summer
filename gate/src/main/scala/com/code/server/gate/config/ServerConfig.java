package com.code.server.gate.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by sunxianping on 2017/3/17.
 */
@ConfigurationProperties(prefix = "serverConfig")
public class ServerConfig {

    private int gateId;

    private int port;







    public int getGateId() {
        return gateId;
    }

    public ServerConfig setGateId(int gateId) {
        this.gateId = gateId;
        return this;
    }


    public int getPort() {
        return port;
    }

    public ServerConfig setPort(int port) {
        this.port = port;
        return this;
    }


}
