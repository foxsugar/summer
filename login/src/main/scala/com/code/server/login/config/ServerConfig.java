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
    private int startNewGameRpc=0;


    private  String appId;//应用id

    private   String mchId;//商户号

    private  String key;//API秘钥

    private  String notifyUrl;
    private String dataFile;

    private int clubCreateMoney = 5000;

    private int clubRoomModelLimit = 3;

    private int clubLimit = 5;

    private String domain = "";


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

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getMchId() {
        return mchId;
    }

    public void setMchId(String mchId) {
        this.mchId = mchId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public int getStartNewGameRpc() {
        return startNewGameRpc;
    }

    public ServerConfig setStartNewGameRpc(int startNewGameRpc) {
        this.startNewGameRpc = startNewGameRpc;
        return this;
    }

    public String getDataFile() {
        return dataFile;
    }

    public ServerConfig setDataFile(String dataFile) {
        this.dataFile = dataFile;
        return this;
    }

    public int getClubCreateMoney() {
        return clubCreateMoney;
    }

    public ServerConfig setClubCreateMoney(int clubCreateMoney) {
        this.clubCreateMoney = clubCreateMoney;
        return this;
    }

    public String getDomain() {
        return domain;
    }

    public ServerConfig setDomain(String domain) {
        this.domain = domain;
        return this;
    }

    public int getClubRoomModelLimit() {
        return clubRoomModelLimit;
    }

    public ServerConfig setClubRoomModelLimit(int clubRoomModelLimit) {
        this.clubRoomModelLimit = clubRoomModelLimit;
        return this;
    }

    public int getClubLimit() {
        return clubLimit;
    }

    public ServerConfig setClubLimit(int clubLimit) {
        this.clubLimit = clubLimit;
        return this;
    }
}
