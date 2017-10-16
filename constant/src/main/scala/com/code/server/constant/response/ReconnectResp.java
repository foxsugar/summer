package com.code.server.constant.response;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by win7 on 2016/12/5.
 */
public class ReconnectResp {

    private IfaceRoomVo room;

    private IfaceGameVo game;

    private boolean isExist;

    private Map<Long, Boolean> offlineStatus = new HashMap<>();//在线状态


    public IfaceRoomVo getRoom() {
        return room;
    }

    public ReconnectResp setRoom(IfaceRoomVo room) {
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

    public IfaceGameVo getGame() {
        return game;
    }

    public void setGame(IfaceGameVo game) {
        this.game = game;
    }

    public Map<Long, Boolean> getOfflineStatus() {
        return offlineStatus;
    }

    public ReconnectResp setOfflineStatus(Map<Long, Boolean> offlineStatus) {
        this.offlineStatus = offlineStatus;
        return this;
    }
}
