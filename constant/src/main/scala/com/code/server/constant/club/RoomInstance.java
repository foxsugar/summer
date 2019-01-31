package com.code.server.constant.club;

/**
 * Created by sunxianping on 2018/1/18.
 */
public class RoomInstance {
    private String roomModelId;
    private String roomId;
    private String createCommand;
    private int money;

    public String getRoomModelId() {
        return roomModelId;
    }

    public RoomInstance setRoomModelId(String roomModelId) {
        this.roomModelId = roomModelId;
        return this;
    }

    public String getRoomId() {
        return roomId;
    }

    public RoomInstance setRoomId(String roomId) {
        this.roomId = roomId;
        return this;
    }

    public String getCreateCommand() {
        return createCommand;
    }

    public RoomInstance setCreateCommand(String createCommand) {
        this.createCommand = createCommand;
        return this;
    }

    public int getMoney() {
        return money;
    }

    public RoomInstance setMoney(int money) {
        this.money = money;
        return this;
    }
}
