package com.code.server.constant.club;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by sunxianping on 2018/1/19.
 */
public class ClubInfo {

    private Map<String,ClubMember> member = new HashMap<>();//成员
    private List<ClubMember> applyList = new ArrayList<>();//申请列表
    private List<RoomModel> roomModels = new ArrayList<>();//房间信息
    private Map<String, RoomInstance> roomInstance = new HashMap<>();//房间实例
    private List<RoomInstance> playingRoom = new CopyOnWriteArrayList<>();
    private List<String> floorDesc = new ArrayList<>();
    private List<Long> admin = new ArrayList<>();
    //合伙人
    private List<Long> partner = new ArrayList<>();
    private boolean autoJoin = false;
    private Map<String, Object> creditInfo = new HashMap<>();


    public Map<String, ClubMember> getMember() {
        return member;
    }

    public ClubInfo setMember(Map<String, ClubMember> member) {
        this.member = member;
        return this;
    }

    public List<ClubMember> getApplyList() {
        return applyList;
    }

    public ClubInfo setApplyList(List<ClubMember> applyList) {
        this.applyList = applyList;
        return this;
    }

    public List<RoomModel> getRoomModels() {
        return roomModels;
    }

    public ClubInfo setRoomModels(List<RoomModel> roomModels) {
        this.roomModels = roomModels;
        return this;
    }

    public Map<String, RoomInstance> getRoomInstance() {
        return roomInstance;
    }

    public ClubInfo setRoomInstance(Map<String, RoomInstance> roomInstance) {
        this.roomInstance = roomInstance;
        return this;
    }

    public List<RoomInstance> getPlayingRoom() {
        return playingRoom;
    }

    public ClubInfo setPlayingRoom(List<RoomInstance> playingRoom) {
        this.playingRoom = playingRoom;
        return this;
    }

    public List<String> getFloorDesc() {
        return floorDesc;
    }

    public ClubInfo setFloorDesc(List<String> floorDesc) {
        this.floorDesc = floorDesc;
        return this;
    }

    public List<Long> getAdmin() {
        return admin;
    }

    public ClubInfo setAdmin(List<Long> admin) {
        this.admin = admin;
        return this;
    }

    public boolean isAutoJoin() {
        return autoJoin;
    }

    public ClubInfo setAutoJoin(boolean autoJoin) {
        this.autoJoin = autoJoin;
        return this;
    }

    public List<Long> getPartner() {
        return partner;
    }

    public ClubInfo setPartner(List<Long> partner) {
        this.partner = partner;
        return this;
    }

    public Map<String, Object> getCreditInfo() {
        return creditInfo;
    }

    public ClubInfo setCreditInfo(Map<String, Object> creditInfo) {
        this.creditInfo = creditInfo;
        return this;
    }
}
