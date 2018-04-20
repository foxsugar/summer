package com.code.server.constant.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by sunxianping on 2018/1/25.
 */
public class RoomInstanceVo {
    private int num;
    private String roomId;
    private String clubRoomModel;
    private List<Map<String, Object>> players = new ArrayList<>();



    public int getNum() {
        return num;
    }

    public RoomInstanceVo setNum(int num) {
        this.num = num;
        return this;
    }

    public String getRoomId() {
        return roomId;
    }

    public RoomInstanceVo setRoomId(String roomId) {
        this.roomId = roomId;
        return this;
    }

    public String getClubRoomModel() {
        return clubRoomModel;
    }

    public RoomInstanceVo setClubRoomModel(String clubRoomModel) {
        this.clubRoomModel = clubRoomModel;
        return this;
    }

    public List<Map<String, Object>> getPlayers() {
        return players;
    }

    public RoomInstanceVo setPlayers(List<Map<String, Object>> players) {
        this.players = players;
        return this;
    }
}
