package com.code.server.db.model;

import com.code.server.constant.club.ClubInfo;
import com.code.server.db.utils.BaseEntity;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import javax.persistence.*;

/**
 * Created by sunxianping on 2018/1/12.
 */
@DynamicUpdate
@Entity
@Table(name = "club")
public class Club extends BaseEntity {

    @Id
    private String id;

    @Column(columnDefinition = "varchar(255)")
    private String name;//俱乐部名称


    private long president;//会长

    private String presidentName;

    @Column(columnDefinition = "varchar(255)")
    private String presidentWx;//会长id

    @Column(columnDefinition = "varchar(4000)")
    private String area;//区域

    @Column(columnDefinition = "varchar(4000)")
    private String clubDesc;//描述

    @Column(columnDefinition = "varchar(4000)")
    private String image;//图片


    private int money;//钱

    @Type(type = "json")
    @Lob
    @Column(columnDefinition = "json")
    private ClubInfo ClubInfo = new ClubInfo();

    public transient final Object lock = new Object();


    public String getId() {
        return id;
    }

    public Club setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Club setName(String name) {
        this.name = name;
        return this;
    }

    public long getPresident() {
        return president;
    }

    public Club setPresident(long president) {
        this.president = president;
        return this;
    }

    public String getPresidentWx() {
        return presidentWx;
    }

    public Club setPresidentWx(String presidentWx) {
        this.presidentWx = presidentWx;
        return this;
    }

    public String getArea() {
        return area;
    }

    public Club setArea(String area) {
        this.area = area;
        return this;
    }

    public String getClubDesc() {
        return clubDesc;
    }

    public Club setClubDesc(String clubDesc) {
        this.clubDesc = clubDesc;
        return this;
    }

    public String getImage() {
        return image;
    }

    public Club setImage(String image) {
        this.image = image;
        return this;
    }

    public int getMoney() {
        return money;
    }

    public Club setMoney(int money) {
        this.money = money;
        return this;
    }

    public String getPresidentName() {
        return presidentName;
    }

    public Club setPresidentName(String presidentName) {
        this.presidentName = presidentName;
        return this;
    }

    public com.code.server.constant.club.ClubInfo getClubInfo() {
        return ClubInfo;
    }

    public Club setClubInfo(com.code.server.constant.club.ClubInfo clubInfo) {
        ClubInfo = clubInfo;
        return this;
    }
}
