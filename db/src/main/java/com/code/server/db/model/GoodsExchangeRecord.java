package com.code.server.db.model;

import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by sunxianping on 2019-01-28.
 */
@DynamicUpdate
@Entity
@Table(name = "goods_exchange_record")
public class GoodsExchangeRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private long usersId;

    private int goodsId;

    private Date createDate;

    private Date lastModifiedDate;

    @Column(columnDefinition = "varchar(4000)")
    private String name;

    @Column(columnDefinition = "varchar(4000)")
    private String location;

    private int status;

    private String phone;

    private String idCard;


    public long getId() {
        return id;
    }

    public GoodsExchangeRecord setId(long id) {
        this.id = id;
        return this;
    }

    public long getUsersId() {
        return usersId;
    }

    public GoodsExchangeRecord setUsersId(long usersId) {
        this.usersId = usersId;
        return this;
    }

    public int getGoodsId() {
        return goodsId;
    }

    public GoodsExchangeRecord setGoodsId(int goodsId) {
        this.goodsId = goodsId;
        return this;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public GoodsExchangeRecord setCreateDate(Date createDate) {
        this.createDate = createDate;
        return this;
    }

    public String getName() {
        return name;
    }

    public GoodsExchangeRecord setName(String name) {
        this.name = name;
        return this;
    }

    public String getLocation() {
        return location;
    }

    public GoodsExchangeRecord setLocation(String location) {
        this.location = location;
        return this;
    }

    public int getStatus() {
        return status;
    }

    public GoodsExchangeRecord setStatus(int status) {
        this.status = status;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public GoodsExchangeRecord setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getIdCard() {
        return idCard;
    }

    public GoodsExchangeRecord setIdCard(String idCard) {
        this.idCard = idCard;
        return this;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public GoodsExchangeRecord setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
        return this;
    }
}
