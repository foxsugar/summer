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
public class Replay {
    private Map<Long, List<String>> cards = new HashMap<>();
    private List<OperateReqResp> operate = new ArrayList<>();
    private ResultResp result;

    public Map<Long, List<String>> getCards() {
        return cards;
    }

    public Replay setCards(Map<Long, List<String>> cards) {
        this.cards = cards;
        return this;
    }

    public List<OperateReqResp> getOperate() {
        return operate;
    }

    public Replay setOperate(List<OperateReqResp> operate) {
        this.operate = operate;
        return this;
    }

    public ResultResp getResult() {
        return result;
    }

    public Replay setResult(ResultResp result) {
        this.result = result;
        return this;
    }
}
