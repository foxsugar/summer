package com.code.server.constant.response;

import com.code.server.constant.club.ClubMember;
import com.code.server.constant.club.RoomModel;
import com.code.server.constant.club.Statistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private int applyNum;
    private String image;
    private int roomModelNum;
    private double score;
    private double playMinScore;
    private List<String> floorDesc = new ArrayList<>();

    private List<ClubMember> member = new ArrayList<>();//成员


    private List<ClubMember> applyList = new ArrayList<>();//申请列表


    private List<RoomModel> roomModels = new ArrayList<>();//房间信息


    private List<RoomInstanceVo> roomInstance = new ArrayList<>();//房间实例

    private List<RoomInstanceVo> playingRoom = new ArrayList<>();

    private Statistics statistics;

    private List<Long> admin = new ArrayList<>();
    private List<Long> partner = new ArrayList<>();

    private boolean autoJoin = false;

    private Map<String, Object> creditInfo = new HashMap<>();


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

    public List<RoomInstanceVo> getRoomInstance() {
        return roomInstance;
    }

    public ClubVo setRoomInstance(List<RoomInstanceVo> roomInstance) {
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

    public int getApplyNum() {
        return applyNum;
    }

    public ClubVo setApplyNum(int applyNum) {
        this.applyNum = applyNum;
        return this;
    }

    public List<RoomInstanceVo> getPlayingRoom() {
        return playingRoom;
    }

    public ClubVo setPlayingRoom(List<RoomInstanceVo> playingRoom) {
        this.playingRoom = playingRoom;
        return this;
    }

    public List<String> getFloorDesc() {
        return floorDesc;
    }

    public ClubVo setFloorDesc(List<String> floorDesc) {
        this.floorDesc = floorDesc;
        return this;
    }

    public String getImage() {
        return image;
    }

    public ClubVo setImage(String image) {
        this.image = image;
        return this;
    }

    public Statistics getStatistics() {
        return statistics;
    }

    public ClubVo setStatistics(Statistics statistics) {
        this.statistics = statistics;
        return this;
    }

    public List<Long> getAdmin() {
        return admin;
    }

    public ClubVo setAdmin(List<Long> admin) {
        this.admin = admin;
        return this;
    }

    public boolean isAutoJoin() {
        return autoJoin;
    }

    public ClubVo setAutoJoin(boolean autoJoin) {
        this.autoJoin = autoJoin;
        return this;
    }

    public int getRoomModelNum() {
        return roomModelNum;
    }

    public ClubVo setRoomModelNum(int roomModelNum) {
        this.roomModelNum = roomModelNum;
        return this;
    }

    public List<Long> getPartner() {
        return partner;
    }

    public ClubVo setPartner(List<Long> partner) {
        this.partner = partner;
        return this;
    }

    public Map<String, Object> getCreditInfo() {
        return creditInfo;
    }

    public ClubVo setCreditInfo(Map<String, Object> creditInfo) {
        this.creditInfo = creditInfo;
        return this;
    }

    public double getScore() {
        return score;
    }

    public ClubVo setScore(double score) {
        this.score = score;
        return this;
    }

    public double getPlayMinScore() {
        return playMinScore;
    }

    public ClubVo setPlayMinScore(double playMinScore) {
        this.playMinScore = playMinScore;
        return this;
    }
}
