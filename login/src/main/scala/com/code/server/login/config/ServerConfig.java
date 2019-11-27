package com.code.server.login.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

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

    private String notifyUrl;
    //数据地址
    private String dataFile;

    //俱乐部创建消耗
    private int clubCreateMoney = 5000;

    //俱乐部玩法限制
    private int clubRoomModelLimit = 3;

    //俱乐部个数限制
    private int clubLimit = 5;

    //域名
    private String domain = "";

    //加入俱乐部个数限制
    private int clubJoinLimit = 5;

    //龙七 http 推送
    private String lq_http_url = "";
    private int send_lq_http = 0;

    //俱乐部是否推送玩家离开进入房间
    private int clubPushUserRoomInfo = 0;

    //是否保存回放
    private int saveReplay = 1;

    private String domainMapKey = "nx";

    private Map<String,String> domainMap = new HashMap<>();

    private Map<Integer, Integer> chargeMap = new HashMap<>();

    private int bindRefereeReward = 0;

    private int deleteRecordTask = 0;

    private int hasClubMoney = 0;

    private Map<Integer, Integer> discount = new HashMap<>();

    private Map<Integer, Integer> agentFirstRebate = new HashMap<>();
    private Map<Integer, Integer> agentSecondRebate = new HashMap<>();

    private Map<Integer, Integer> zlbRebate = new HashMap<>();
    private Map<Integer, Integer> zlbRebate2 = new HashMap<>();
    private Map<Integer, Integer> zlbRebate3 = new HashMap<>();
    private Map<Integer, Integer> zlbAAOne = new HashMap<>();
    private Map<Integer, Integer> zlbAATwo = new HashMap<>();
    private Map<Integer, Integer> zlbAAThree = new HashMap<>();


    private String paysapiUrl = "";
    private String paysapiNotifyUrl = "";
    private String paysapiReturnUrl = "";


    private String cftPayId = "";
    private String cftPayKey = "";

    private int startRobot = 0;

    private String qrDir = "";
    private String qrUrl = "";

    private int loadAllUser = 0;
    private int loginCreateNewUser = 1;
    private int sendDuoliao = 0;


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

    public int getClubJoinLimit() {
        return clubJoinLimit;
    }

    public ServerConfig setClubJoinLimit(int clubJoinLimit) {
        this.clubJoinLimit = clubJoinLimit;
        return this;
    }

    public String getLq_http_url() {
        return lq_http_url;
    }

    public ServerConfig setLq_http_url(String lq_http_url) {
        this.lq_http_url = lq_http_url;
        return this;
    }

    public int getClubPushUserRoomInfo() {
        return clubPushUserRoomInfo;
    }

    public ServerConfig setClubPushUserRoomInfo(int clubPushUserRoomInfo) {
        this.clubPushUserRoomInfo = clubPushUserRoomInfo;
        return this;
    }

    public int getSaveReplay() {
        return saveReplay;
    }

    public ServerConfig setSaveReplay(int saveReplay) {
        this.saveReplay = saveReplay;
        return this;
    }

    public int getSend_lq_http() {
        return send_lq_http;
    }

    public ServerConfig setSend_lq_http(int send_lq_http) {
        this.send_lq_http = send_lq_http;
        return this;
    }

    public String getDomainMapKey() {
        return domainMapKey;
    }

    public ServerConfig setDomainMapKey(String domainMapKey) {
        this.domainMapKey = domainMapKey;
        return this;
    }

    public Map<String, String> getDomainMap() {
        return domainMap;
    }

    public ServerConfig setDomainMap(Map<String, String> domainMap) {
        this.domainMap = domainMap;
        return this;
    }

    public Map<Integer, Integer> getChargeMap() {
        return chargeMap;
    }

    public ServerConfig setChargeMap(Map<Integer, Integer> chargeMap) {
        this.chargeMap = chargeMap;
        return this;
    }

    public int getBindRefereeReward() {
        return bindRefereeReward;
    }

    public ServerConfig setBindRefereeReward(int bindRefereeReward) {
        this.bindRefereeReward = bindRefereeReward;
        return this;
    }

    public int getDeleteRecordTask() {
        return deleteRecordTask;
    }

    public ServerConfig setDeleteRecordTask(int deleteRecordTask) {
        this.deleteRecordTask = deleteRecordTask;
        return this;
    }

    public int getHasClubMoney() {
        return hasClubMoney;
    }

    public ServerConfig setHasClubMoney(int hasClubMoney) {
        this.hasClubMoney = hasClubMoney;
        return this;
    }

    public Map<Integer, Integer> getDiscount() {
        return discount;
    }

    public ServerConfig setDiscount(Map<Integer, Integer> discount) {
        this.discount = discount;
        return this;
    }

    public Map<Integer, Integer> getAgentFirstRebate() {
        return agentFirstRebate;
    }

    public ServerConfig setAgentFirstRebate(Map<Integer, Integer> agentFirstRebate) {
        this.agentFirstRebate = agentFirstRebate;
        return this;
    }

    public Map<Integer, Integer> getAgentSecondRebate() {
        return agentSecondRebate;
    }

    public ServerConfig setAgentSecondRebate(Map<Integer, Integer> agentSecondRebate) {
        this.agentSecondRebate = agentSecondRebate;
        return this;
    }

    public String getPaysapiUrl() {
        return paysapiUrl;
    }

    public ServerConfig setPaysapiUrl(String paysapiUrl) {
        this.paysapiUrl = paysapiUrl;
        return this;
    }

    public String getPaysapiNotifyUrl() {
        return paysapiNotifyUrl;
    }

    public ServerConfig setPaysapiNotifyUrl(String paysapiNotifyUrl) {
        this.paysapiNotifyUrl = paysapiNotifyUrl;
        return this;
    }

    public String getPaysapiReturnUrl() {
        return paysapiReturnUrl;
    }

    public ServerConfig setPaysapiReturnUrl(String paysapiReturnUrl) {
        this.paysapiReturnUrl = paysapiReturnUrl;
        return this;
    }

    public int getStartRobot() {
        return startRobot;
    }

    public ServerConfig setStartRobot(int startRobot) {
        this.startRobot = startRobot;
        return this;
    }

    public String getCftPayId() {
        return cftPayId;
    }

    public ServerConfig setCftPayId(String cftPayId) {
        this.cftPayId = cftPayId;
        return this;
    }

    public String getCftPayKey() {
        return cftPayKey;
    }

    public ServerConfig setCftPayKey(String cftPayKey) {
        this.cftPayKey = cftPayKey;
        return this;
    }

    public String getQrDir() {
        return qrDir;
    }

    public ServerConfig setQrDir(String qrDir) {
        this.qrDir = qrDir;
        return this;
    }

    public int getLoadAllUser() {
        return loadAllUser;
    }

    public ServerConfig setLoadAllUser(int loadAllUser) {
        this.loadAllUser = loadAllUser;
        return this;
    }

    public String getQrUrl() {
        return qrUrl;
    }

    public ServerConfig setQrUrl(String qrUrl) {
        this.qrUrl = qrUrl;
        return this;
    }

    public int getLoginCreateNewUser() {
        return loginCreateNewUser;
    }

    public void setLoginCreateNewUser(int loginCreateNewUser) {
        this.loginCreateNewUser = loginCreateNewUser;
    }

    public int getSendDuoliao() {
        return sendDuoliao;
    }

    public ServerConfig setSendDuoliao(int sendDuoliao) {
        this.sendDuoliao = sendDuoliao;
        return this;
    }

    public Map<Integer, Integer> getZlbRebate() {
        return zlbRebate;
    }

    public ServerConfig setZlbRebate(Map<Integer, Integer> zlbRebate) {
        this.zlbRebate = zlbRebate;
        return this;
    }

    public Map<Integer, Integer> getZlbAAOne() {
        return zlbAAOne;
    }

    public ServerConfig setZlbAAOne(Map<Integer, Integer> zlbAAOne) {
        this.zlbAAOne = zlbAAOne;
        return this;
    }

    public Map<Integer, Integer> getZlbAATwo() {
        return zlbAATwo;
    }

    public ServerConfig setZlbAATwo(Map<Integer, Integer> zlbAATwo) {
        this.zlbAATwo = zlbAATwo;
        return this;
    }

    public Map<Integer, Integer> getZlbAAThree() {
        return zlbAAThree;
    }

    public ServerConfig setZlbAAThree(Map<Integer, Integer> zlbAAThree) {
        this.zlbAAThree = zlbAAThree;
        return this;
    }

    public Map<Integer, Integer> getZlbRebate2() {
        return zlbRebate2;
    }

    public ServerConfig setZlbRebate2(Map<Integer, Integer> zlbRebate2) {
        this.zlbRebate2 = zlbRebate2;
        return this;
    }

    public Map<Integer, Integer> getZlbRebate3() {
        return zlbRebate3;
    }

    public ServerConfig setZlbRebate3(Map<Integer, Integer> zlbRebate3) {
        this.zlbRebate3 = zlbRebate3;
        return this;
    }
}
