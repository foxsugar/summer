package com.code.server.game.poker.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by sunxianping on 2017/3/17.
 */
@ConfigurationProperties(prefix = "serverConfig")
public class ServerConfig {
    private String serverType;
    private int serverId;
    private int port;
    //机器人执行周期
    private int robotExeCycle = 1000;
    private int isStartRobot = 1;
    private String dataFile;
    private int dissloutionRoomMustAllAgree = 0;
    private int paijiuServiceMoney = 6;
    private int paijiuMinJoinMoney = 200;
    private int addClubMoney = 0;
    private int autoInterval = 0;//托管后执行时间
    private int commonInterval = 10000;//普通托管执行时间
    private int isZhanglebao = 0;


    public int getIsZhanglebao() {
        return isZhanglebao;
    }

    public ServerConfig setIsZhanglebao(int isZhanglebao) {
        this.isZhanglebao = isZhanglebao;
        return this;
    }

    public String getServerType() {
        return serverType;
    }

    public ServerConfig setServerType(String serverType) {
        this.serverType = serverType;
        return this;
    }

    public int getIsStartRobot() {
        return isStartRobot;
    }

    public ServerConfig setIsStartRobot(int isStartRobot) {
        this.isStartRobot = isStartRobot;
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


    public int getRobotExeCycle() {
        return robotExeCycle;
    }

    public ServerConfig setRobotExeCycle(int robotExeCycle) {
        this.robotExeCycle = robotExeCycle;
        return this;
    }

    public String getDataFile() {
        return dataFile;
    }

    public ServerConfig setDataFile(String dataFile) {
        this.dataFile = dataFile;
        return this;
    }

    public int getDissloutionRoomMustAllAgree() {
        return dissloutionRoomMustAllAgree;
    }

    public ServerConfig setDissloutionRoomMustAllAgree(int dissloutionRoomMustAllAgree) {
        this.dissloutionRoomMustAllAgree = dissloutionRoomMustAllAgree;
        return this;
    }

    public int getPaijiuServiceMoney() {
        return paijiuServiceMoney;
    }

    public ServerConfig setPaijiuServiceMoney(int paijiuServiceMoney) {
        this.paijiuServiceMoney = paijiuServiceMoney;
        return this;
    }

    public int getPaijiuMinJoinMoney() {
        return paijiuMinJoinMoney;
    }

    public ServerConfig setPaijiuMinJoinMoney(int paijiuMinJoinMoney) {
        this.paijiuMinJoinMoney = paijiuMinJoinMoney;
        return this;
    }

    public int getAddClubMoney() {
        return addClubMoney;
    }

    public ServerConfig setAddClubMoney(int addClubMoney) {
        this.addClubMoney = addClubMoney;
        return this;
    }

    public int getAutoInterval() {
        return autoInterval;
    }

    public ServerConfig setAutoInterval(int autoInterval) {
        this.autoInterval = autoInterval;
        return this;
    }

    public int getCommonInterval() {
        return commonInterval;
    }

    public ServerConfig setCommonInterval(int commonInterval) {
        this.commonInterval = commonInterval;
        return this;
    }
}
