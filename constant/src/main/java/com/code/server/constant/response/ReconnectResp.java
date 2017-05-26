package com.code.server.constant.response;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by win7 on 2016/12/5.
 */
public class ReconnectResp {

    private RoomVo room;

    private boolean isExist;

    private Map<Integer, Integer> offlineStatus = new HashMap<>();//在线状态


    public RoomVo getRoom() {
        return room;
    }

    public ReconnectResp setRoom(RoomVo room) {
        this.room = room;
        return this;
    }

    public boolean isExist() {
        return isExist;
    }

    public ReconnectResp setExist(boolean exist) {
        isExist = exist;
        return this;
    }


    public Map<Integer, Integer> getOfflineStatus() {
        return offlineStatus;
    }

    public ReconnectResp setOfflineStatus(Map<Integer, Integer> offlineStatus) {
        this.offlineStatus = offlineStatus;
        return this;
    }
}
