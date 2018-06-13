package com.code.server.game.poker.zhaguzi;

import com.code.server.constant.response.GameVo;
import com.code.server.constant.response.IfacePlayerInfoVo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dajuejinxian on 2018/6/12.
 */
public class GameYSZVo extends GameVo{

    public List<Integer> cards = new ArrayList<>();//牌
    public Map<Long,IfacePlayerInfoVo> playerCardInfos = new HashMap<>();
    public int curRoundNumber;//当前轮数
    public Double chip;

    public List<Integer> leaveCards = new ArrayList<>();//剩余的牌，暂时无用
    public List<Long> aliveUser = new ArrayList<>();//存活的人
    public List<Long> seeUser = new ArrayList<>();//看牌的人
    public List<Long> loseUser = new ArrayList<>();//看牌的人
    public Long curUserId;
    public Double allTableChip;

    public List<Integer> zhuList = new ArrayList<>();

    public List<Integer> getZhuList() {
        return zhuList;
    }

    public void setZhuList(List<Integer> zhuList) {
        this.zhuList = zhuList;
    }

    public List<Integer> getCards() {
        return cards;
    }

    public void setCards(List<Integer> cards) {
        this.cards = cards;
    }

    public Map<Long, IfacePlayerInfoVo> getPlayerCardInfos() {
        return playerCardInfos;
    }

    public void setPlayerCardInfos(Map<Long, IfacePlayerInfoVo> playerCardInfos) {
        this.playerCardInfos = playerCardInfos;
    }

    public int getCurRoundNumber() {
        return curRoundNumber;
    }

    public void setCurRoundNumber(int curRoundNumber) {
        this.curRoundNumber = curRoundNumber;
    }

    public Double getChip() {
        return chip;
    }

    public void setChip(Double chip) {
        this.chip = chip;
    }

    public List<Integer> getLeaveCards() {
        return leaveCards;
    }

    public void setLeaveCards(List<Integer> leaveCards) {
        this.leaveCards = leaveCards;
    }

    public List<Long> getAliveUser() {
        return aliveUser;
    }

    public void setAliveUser(List<Long> aliveUser) {
        this.aliveUser = aliveUser;
    }

    public List<Long> getSeeUser() {
        return seeUser;
    }

    public void setSeeUser(List<Long> seeUser) {
        this.seeUser = seeUser;
    }

    public List<Long> getLoseUser() {
        return loseUser;
    }

    public void setLoseUser(List<Long> loseUser) {
        this.loseUser = loseUser;
    }

    public Long getCurUserId() {
        return curUserId;
    }

    public void setCurUserId(Long curUserId) {
        this.curUserId = curUserId;
    }

    public Double getAllTableChip() {
        return allTableChip;
    }

    public void setAllTableChip(Double allTableChip) {
        this.allTableChip = allTableChip;
    }
}
