package com.code.server.constant.response;

import com.code.server.constant.club.ClubMember;
import com.code.server.constant.club.RoomInstance;
import com.code.server.constant.club.RoomModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunxianping on 2018/1/17.
 */
public class ClubVo {
    private String id;
    private String name;
    private long president;
    private String presidentName;
    private int num;
    private int money;
    private String area;
    private String presidentWx;

    private List<ClubMember> member = new ArrayList<>();//成员


    private List<ClubMember> applyList = new ArrayList<>();//申请列表


    private List<RoomModel> roomModels = new ArrayList<>();//房间信息


    private List<RoomInstance> roomInstance = new ArrayList<>();//房间实例



    public String getId() {
        return id;
    }

    public ClubVo setId(String id) {
        this.id = id;
        return this;
    }

    public long getPresident() {
        return president;
    }

    public ClubVo setPresident(long president) {
        this.president = president;
        return this;
    }

    public String getPresidentName() {
        return presidentName;
    }

    public ClubVo setPresidentName(String presidentName) {
        this.presidentName = presidentName;
        return this;
    }

    public int getNum() {
        return num;
    }

    public ClubVo setNum(int num) {
        this.num = num;
        return this;
    }

    public String getName() {
        return name;
    }

    public ClubVo setName(String name) {
        this.name = name;
        return this;
    }

    public List<ClubMember> getMember() {
        return member;
    }

    public ClubVo setMember(List<ClubMember> member) {
        this.member = member;
        return this;
    }

    public List<ClubMember> getApplyList() {
        return applyList;
    }

    public ClubVo setApplyList(List<ClubMember> applyList) {
        this.applyList = applyList;
        return this;
    }

    public List<RoomModel> getRoomModels() {
        return roomModels;
    }

    public ClubVo setRoomModels(List<RoomModel> roomModels) {
        this.roomModels = roomModels;
        return this;
    }

    public List<RoomInstance> getRoomInstance() {
        return roomInstance;
    }

    public ClubVo setRoomInstance(List<RoomInstance> roomInstance) {
        this.roomInstance = roomInstance;
        return this;
    }

    public int getMoney() {
        return money;
    }

    public ClubVo setMoney(int money) {
        this.money = money;
        return this;
    }

    public String getArea() {
        return area;
    }

    public ClubVo setArea(String area) {
        this.area = area;
        return this;
    }

    public String getPresidentWx() {
        return presidentWx;
    }

    public ClubVo setPresidentWx(String presidentWx) {
        this.presidentWx = presidentWx;
        return this;
    }
}
