package com.code.server.db.model;


import javax.persistence.*;
import java.util.Date;

/**
 * Created by win7 on 2017/3/8.
 */

@Entity
@Table(name = "server_info")
public class ServerInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private long areaId;

    @Column(nullable = false)
    private String serverName;

    private int serverType;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String port;

    private int serverStatus;

    private Date openServerDate;//开服时间

    private Date LastedUpdateDate;//上次更新时间

    @Column(columnDefinition = "varchar(4000)")
    private String notice1;//告示1

    @Column(columnDefinition = "varchar(4000)")
    private String notice2;//告示2


    private String versionOfAndroid;//安卓版本

    private String versionOfIos;//IOS版本


    private int appleCheck;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getAreaId() {
        return areaId;
    }

    public void setAreaId(long areaId) {
        this.areaId = areaId;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public int getServerType() {
        return serverType;
    }

    public void setServerType(int serverType) {
        this.serverType = serverType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public int getServerStatus() {
        return serverStatus;
    }

    public void setServerStatus(int serverStatus) {
        this.serverStatus = serverStatus;
    }

    public Date getOpenServerDate() {
        return openServerDate;
    }

    public void setOpenServerDate(Date openServerDate) {
        this.openServerDate = openServerDate;
    }

    public Date getLastedUpdateDate() {
        return LastedUpdateDate;
    }

    public void setLastedUpdateDate(Date lastedUpdateDate) {
        LastedUpdateDate = lastedUpdateDate;
    }

    public String getNotice1() {
        return notice1;
    }

    public void setNotice1(String notice1) {
        this.notice1 = notice1;
    }

    public String getNotice2() {
        return notice2;
    }

    public void setNotice2(String notice2) {
        this.notice2 = notice2;
    }


    public String getVersionOfAndroid() {
        return versionOfAndroid;
    }

    public void setVersionOfAndroid(String versionOfAndroid) {
        this.versionOfAndroid = versionOfAndroid;
    }

    public String getVersionOfIos() {
        return versionOfIos;
    }

    public void setVersionOfIos(String versionOfIos) {
        this.versionOfIos = versionOfIos;
    }


    public int getAppleCheck() {
        return appleCheck;
    }

    public ServerInfo setAppleCheck(int appleCheck) {
        this.appleCheck = appleCheck;
        return this;
    }
}
