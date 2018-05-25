package com.code.server.game.poker.zhaguzi;

import com.code.server.constant.response.IfaceGameVo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dajuejinxian on 2018/5/7.
 */
public class GameZhaGuZiVo implements IfaceGameVo {

    protected Map<Long, PlayerZhaGuZiVo> playerCardInfos = new HashMap<>();

    protected RoomZhaGuZi room;

    protected long currentTalkId;

    public long getCurrentTalkId() {
        return currentTalkId;
    }

    public void setCurrentTalkId(long currentTalkId) {
        this.currentTalkId = currentTalkId;
    }

    protected List<Integer> cards = new ArrayList<Integer>();

    protected List<Map<String, Object>> leaveCards = new ArrayList<>();

    public List<Map<String, Object>> getLeaveCards() {
        return leaveCards;
    }

    public void setLeaveCards(List<Map<String, Object>> leaveCards) {
        this.leaveCards = leaveCards;
    }

    protected Integer status = ZhaGuZiConstant.START_GAME;

    public Map<Long, PlayerZhaGuZiVo> getPlayerCardInfos() {
        return playerCardInfos;
    }

    public void setPlayerCardInfos(Map<Long, PlayerZhaGuZiVo> playerCardInfos) {
        this.playerCardInfos = playerCardInfos;
    }



    public RoomZhaGuZi getRoom() {
        return room;
    }

    public void setRoom(RoomZhaGuZi room) {
        this.room = room;
    }

    public List<Integer> getCards() {
        return cards;
    }

    public void setCards(List<Integer> cards) {
        this.cards = cards;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
