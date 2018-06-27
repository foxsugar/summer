package com.code.server.db.model;

import com.code.server.constant.db.GameNumData;
import com.code.server.constant.db.GoldRoomIncomeData;
import com.code.server.constant.db.LogInfo;
import com.code.server.constant.db.OnlineData;
import com.code.server.db.utils.BaseEntity;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import javax.persistence.*;

/**
 * Created by sunxianping on 2018/2/28.
 */

@DynamicUpdate
@Entity
@Table(name = "log_record")
public class LogRecord extends BaseEntity {

    @Id
    private String id;

    @Type(type = "json")
    @Lob
    @Column(columnDefinition = "json")
    private OnlineData onlineData = new OnlineData();

    @Type(type = "json")
    @Lob
    @Column(columnDefinition = "json")
    private GameNumData gameNumData = new GameNumData();

    @Type(type = "json")
    @Lob
    @Column(columnDefinition = "json")
    private GoldRoomIncomeData goldRoomIncomeData = new GoldRoomIncomeData();

    private double chargeRebate;

    @Type(type = "json")
    @Lob
    @Column(columnDefinition = "json")
    private LogInfo logInfo = new LogInfo();



    public String getId() {
        return id;
    }

    public LogRecord setId(String id) {
        this.id = id;
        return this;
    }

    public OnlineData getOnlineData() {
        return onlineData;
    }

    public LogRecord setOnlineData(OnlineData onlineData) {
        this.onlineData = onlineData;
        return this;
    }

    public GameNumData getGameNumData() {
        return gameNumData;
    }

    public LogRecord setGameNumData(GameNumData gameNumData) {
        this.gameNumData = gameNumData;
        return this;
    }

    public GoldRoomIncomeData getGoldRoomIncomeData() {
        return goldRoomIncomeData;
    }

    public LogRecord setGoldRoomIncomeData(GoldRoomIncomeData goldRoomIncomeData) {
        this.goldRoomIncomeData = goldRoomIncomeData;
        return this;
    }

    public double getChargeRebate() {
        return chargeRebate;
    }

    public LogRecord setChargeRebate(double chargeRebate) {
        this.chargeRebate = chargeRebate;
        return this;
    }

    public LogInfo getLogInfo() {
        return logInfo;
    }

    public LogRecord setLogInfo(LogInfo logInfo) {
        this.logInfo = logInfo;
        return this;
    }
}
