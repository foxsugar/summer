package com.code.server.db.model;

import com.code.server.constant.db.AgentInfo;
import com.code.server.constant.db.AgentInfoRecord;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by sunxianping on 2017/9/13.
 */

@DynamicUpdate
@Entity
public class AgentUser {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String username;
    private String password;
    private String invite_code;//邀请码
    private String realName;//真实姓名
    private int level;//级别
    private int parentId;//上级id
    private String email;//邮箱
    private Date createTime;//创建时间
    private String idCard;//身份证
    private String cell;//电话
    private String area;//所属区域
    private String address;//地址
    private double money;//钱
    private double gold;//
    private double payDeduct;//支付提成
    private double shareDeduct;//分享提成
    private double parentPayDeduct;//上级支付提成
    private double parentShareDeduct;//上级分享提成
    private int agentType;

    @Type(type = "json")
    @Lob
    @Column(columnDefinition = "json")
    private AgentInfo agentInfo = new AgentInfo();

    @Type(type = "json")
    @Lob
    @Column(columnDefinition = "json")
    private AgentInfoRecord agentInfoRecord = new AgentInfoRecord();

    public AgentInfoRecord getAgentInfoRecord() {
        return agentInfoRecord;
    }

    public void setAgentInfoRecord(AgentInfoRecord agentInfoRecord) {
        this.agentInfoRecord = agentInfoRecord;
    }

    public AgentInfo getAgentInfo() {
        return agentInfo;
    }

    public void setAgentInfo(AgentInfo agentInfo) {
        this.agentInfo = agentInfo;
    }

    public int getId() {
        return id;
    }

    public AgentUser setId(int id) {
        this.id = id;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public AgentUser setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public AgentUser setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getInvite_code() {
        return invite_code;
    }

    public AgentUser setInvite_code(String invite_code) {
        this.invite_code = invite_code;
        return this;
    }

    public String getRealName() {
        return realName;
    }

    public AgentUser setRealName(String realName) {
        this.realName = realName;
        return this;
    }

    public int getLevel() {
        return level;
    }

    public AgentUser setLevel(int level) {
        this.level = level;
        return this;
    }

    public int getParentId() {
        return parentId;
    }

    public AgentUser setParentId(int parentId) {
        this.parentId = parentId;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public AgentUser setEmail(String email) {
        this.email = email;
        return this;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public AgentUser setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }

    public String getIdCard() {
        return idCard;
    }

    public AgentUser setIdCard(String idCard) {
        this.idCard = idCard;
        return this;
    }

    public String getCell() {
        return cell;
    }

    public AgentUser setCell(String cell) {
        this.cell = cell;
        return this;
    }

    public String getArea() {
        return area;
    }

    public AgentUser setArea(String area) {
        this.area = area;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public AgentUser setAddress(String address) {
        this.address = address;
        return this;
    }

    public double getMoney() {
        return money;
    }

    public AgentUser setMoney(double money) {
        this.money = money;
        return this;
    }

    public double getGold() {
        return gold;
    }

    public AgentUser setGold(double gold) {
        this.gold = gold;
        return this;
    }

    public double getPayDeduct() {
        return payDeduct;
    }

    public AgentUser setPayDeduct(double payDeduct) {
        this.payDeduct = payDeduct;
        return this;
    }

    public double getShareDeduct() {
        return shareDeduct;
    }

    public AgentUser setShareDeduct(double shareDeduct) {
        this.shareDeduct = shareDeduct;
        return this;
    }

    public double getParentPayDeduct() {
        return parentPayDeduct;
    }

    public AgentUser setParentPayDeduct(double parentPayDeduct) {
        this.parentPayDeduct = parentPayDeduct;
        return this;
    }

    public double getParentShareDeduct() {
        return parentShareDeduct;
    }

    public AgentUser setParentShareDeduct(double parentShareDeduct) {
        this.parentShareDeduct = parentShareDeduct;
        return this;
    }

    public int getAgentType() {
        return agentType;
    }

    public AgentUser setAgentType(int agentType) {
        this.agentType = agentType;
        return this;
    }

    @Override
    public String toString() {
        return "AgentUser{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", invite_code='" + invite_code + '\'' +
                ", realName='" + realName + '\'' +
                ", level=" + level +
                ", parentId=" + parentId +
                ", email='" + email + '\'' +
                ", createTime=" + createTime +
                ", idCard='" + idCard + '\'' +
                ", cell='" + cell + '\'' +
                ", area='" + area + '\'' +
                ", address='" + address + '\'' +
                ", money=" + money +
                ", gold=" + gold +
                ", payDeduct=" + payDeduct +
                ", shareDeduct=" + shareDeduct +
                ", parentPayDeduct=" + parentPayDeduct +
                ", parentShareDeduct=" + parentShareDeduct +
                ", agentInfo=" + agentInfo +
                ", agentInfoRecord=" + agentInfoRecord +
                '}';
    }
}
