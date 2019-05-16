package com.code.server.game.mahjong.logic;

import com.code.server.game.mahjong.response.OperateReqResp;
import com.code.server.game.mahjong.response.ResultResp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sunxianping on 2017/7/3.
 */
public class ReplayMj {
    private long id;
    private int count;
    private long room_uuid;

    private Map<Long, List<String>> cards = new HashMap<>();
    private List<OperateReqResp> operate = new ArrayList<>();
    private ResultResp result;
    private Map<String, Object> roomInfo = new HashMap<>();

    private List<Integer> hun = new ArrayList<>();

    private Map<Long, String> koutingCard = new HashMap<>();
    private Map<Long, Integer> paofen = new HashMap<>();


    private int rand;

    // 不能加gameInfo进来 会循环引用

    public Map<Long, List<String>> getCards() {
        return cards;
    }

    public ReplayMj setCards(Map<Long, List<String>> cards) {
        this.cards = cards;
        return this;
    }

    public List<OperateReqResp> getOperate() {
        return operate;
    }

    public ReplayMj setOperate(List<OperateReqResp> operate) {
        this.operate = operate;
        return this;
    }

    public ResultResp getResult() {
        return result;
    }

    public ReplayMj setResult(ResultResp result) {
        this.result = result;
        return this;
    }

    public long getId() {
        return id;
    }

    public ReplayMj setId(long id) {
        this.id = id;
        return this;
    }

    public int getCount() {
        return count;
    }

    public ReplayMj setCount(int count) {
        this.count = count;
        return this;
    }

    public Map<String, Object> getRoomInfo() {
        return roomInfo;
    }

    public ReplayMj setRoomInfo(Map<String, Object> roomInfo) {
        this.roomInfo = roomInfo;
        return this;
    }

    public long getRoom_uuid() {
        return room_uuid;
    }

    public ReplayMj setRoom_uuid(long room_uuid) {
        this.room_uuid = room_uuid;
        return this;
    }

    public List<Integer> getHun() {
        return hun;
    }

    public ReplayMj setHun(List<Integer> hun) {
        this.hun = hun;
        return this;
    }

    public int getRand() {
        return rand;
    }

    public ReplayMj setRand(int rand) {
        this.rand = rand;
        return this;
    }

    public Map<Long, String> getKoutingCard() {
        return koutingCard;
    }

    public ReplayMj setKoutingCard(Map<Long, String> koutingCard) {
        this.koutingCard = koutingCard;
        return this;
    }

    public Map<Long, Integer> getPaofen() {
        return paofen;
    }

    public ReplayMj setPaofen(Map<Long, Integer> paofen) {
        this.paofen = paofen;
        return this;
    }
}
