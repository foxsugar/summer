package com.code.server.game.poker.doudizhu;

import com.code.server.constant.response.GameResultDouDizhu;
import com.code.server.constant.response.IfaceRoomVo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sunxianping on 2017/7/4.
 */
public class ReplayDouDiZhu {
    private long id;
    private int count;
    private long room_uuid;

    private Map<Long, List<Integer>> cards = new HashMap<>();
    private List<Operate> operate = new ArrayList<>();

    private IfaceRoomVo roomInfo ;
    private GameResultDouDizhu result;


    public long getId() {
        return id;
    }

    public ReplayDouDiZhu setId(long id) {
        this.id = id;
        return this;
    }

    public int getCount() {
        return count;
    }

    public ReplayDouDiZhu setCount(int count) {
        this.count = count;
        return this;
    }

    public Map<Long, List<Integer>> getCards() {
        return cards;
    }

    public ReplayDouDiZhu setCards(Map<Long, List<Integer>> cards) {
        this.cards = cards;
        return this;
    }

    public List<Operate> getOperate() {
        return operate;
    }


    public ReplayDouDiZhu setOperate(List<Operate> operate) {
        this.operate = operate;
        return this;
    }

    public IfaceRoomVo getRoomInfo() {
        return roomInfo;
    }

    public ReplayDouDiZhu setRoomInfo(IfaceRoomVo roomInfo) {
        this.roomInfo = roomInfo;
        return this;
    }

    public GameResultDouDizhu getResult() {
        return result;
    }

    public ReplayDouDiZhu setResult(GameResultDouDizhu result) {
        this.result = result;
        return this;
    }

    public long getRoom_uuid() {
        return room_uuid;
    }

    public ReplayDouDiZhu setRoom_uuid(long room_uuid) {
        this.room_uuid = room_uuid;
        return this;
    }
}
