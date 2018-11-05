package com.code.server.game.poker.tiandakeng;

import com.code.server.constant.response.IfaceGameVo;
import com.code.server.constant.response.PlayerCardInfoTDKVo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sunxianping on 2018-11-05.
 */
public class GameTDKVo implements IfaceGameVo {

    protected Map<Long, PlayerCardInfoTDKVo> playerVo = new HashMap<>();
    //所有的牌
//    protected List<Integer> cards = new ArrayList<>();
    protected int remainCardSize = 0;
    //还存活的玩家
    protected List<Long> aliveUserList = new ArrayList<>();
    //弃牌的人
    protected List<Long> giveUpList = new ArrayList<>();
    //下的注
    protected List<Integer> bets = new ArrayList<>();
    //下注信息
    protected BetInfo betInfo;
    //踢牌信息
    protected KickInfo kickInfo;
    //状态
    protected int state = 0;
    //手牌数
    protected int handCardNum = 0;

    protected long openUser = 0;


    public Map<Long, PlayerCardInfoTDKVo> getPlayerVo() {
        return playerVo;
    }

    public GameTDKVo setPlayerVo(Map<Long, PlayerCardInfoTDKVo> playerVo) {
        this.playerVo = playerVo;
        return this;
    }

//    public List<Integer> getCards() {
//        return cards;
//    }
//
//    public GameTDKVo setCards(List<Integer> cards) {
//        this.cards = cards;
//        return this;
//    }

    public List<Long> getAliveUserList() {
        return aliveUserList;
    }

    public GameTDKVo setAliveUserList(List<Long> aliveUserList) {
        this.aliveUserList = aliveUserList;
        return this;
    }

    public List<Long> getGiveUpList() {
        return giveUpList;
    }

    public GameTDKVo setGiveUpList(List<Long> giveUpList) {
        this.giveUpList = giveUpList;
        return this;
    }

    public List<Integer> getBets() {
        return bets;
    }

    public GameTDKVo setBets(List<Integer> bets) {
        this.bets = bets;
        return this;
    }

    public BetInfo getBetInfo() {
        return betInfo;
    }

    public GameTDKVo setBetInfo(BetInfo betInfo) {
        this.betInfo = betInfo;
        return this;
    }

    public KickInfo getKickInfo() {
        return kickInfo;
    }

    public GameTDKVo setKickInfo(KickInfo kickInfo) {
        this.kickInfo = kickInfo;
        return this;
    }

    public int getState() {
        return state;
    }

    public GameTDKVo setState(int state) {
        this.state = state;
        return this;
    }

    public int getHandCardNum() {
        return handCardNum;
    }

    public GameTDKVo setHandCardNum(int handCardNum) {
        this.handCardNum = handCardNum;
        return this;
    }

    public int getRemainCardSize() {
        return remainCardSize;
    }

    public GameTDKVo setRemainCardSize(int remainCardSize) {
        this.remainCardSize = remainCardSize;
        return this;
    }

    public long getOpenUser() {
        return openUser;
    }

    public GameTDKVo setOpenUser(long openUser) {
        this.openUser = openUser;
        return this;
    }
}
