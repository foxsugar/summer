package com.code.server.game.poker.zhaguzi;

import com.code.server.constant.response.IfacePlayerInfoVo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dajuejinxian on 2018/5/2.
 */
public class PlayerZhaGuZiVo implements IfacePlayerInfoVo{

    protected long userId;

    protected List<Integer> cards = new ArrayList<>();

    //发话时的操作
    private int op = Operator.MEI_LIANG;

    protected List<Integer> opList = new ArrayList<>();

    //自己是第几个出完牌的人
    protected int rank = 0;

    //房间人数
    private Integer roomPersonNum;

    private Integer isSanJia_;

    private Integer isWinner;

    private boolean canJieFeng;

    //自己发牌之后持有的三 算分用 其他情况不用
    private List<Integer> retain3List = new ArrayList<>();

    private List<Integer> liangList = new ArrayList<>();

    //是不是改自己出牌
    private boolean selfTurn;

    public boolean isSelfTurn() {
        return selfTurn;
    }

    public void setSelfTurn(boolean selfTurn) {
        this.selfTurn = selfTurn;
    }

    public List<Integer> getLiangList() {
        return liangList;
    }

    public void setLiangList(List<Integer> liangList) {
        this.liangList = liangList;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public List<Integer> getCards() {
        return cards;
    }

    public void setCards(List<Integer> cards) {
        this.cards = cards;
    }

    public int getOp() {
        return op;
    }

    public void setOp(int op) {
        this.op = op;
    }

    public List<Integer> getOpList() {
        return opList;
    }

    public void setOpList(List<Integer> opList) {
        this.opList = opList;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public Integer getRoomPersonNum() {
        return roomPersonNum;
    }

    public void setRoomPersonNum(Integer roomPersonNum) {
        this.roomPersonNum = roomPersonNum;
    }

    public Integer getIsSanJia_() {
        return isSanJia_;
    }

    public void setIsSanJia_(Integer isSanJia_) {
        this.isSanJia_ = isSanJia_;
    }

    public Integer getIsWinner() {
        return isWinner;
    }

    public void setIsWinner(Integer isWinner) {
        this.isWinner = isWinner;
    }

    public boolean isCanJieFeng() {
        return canJieFeng;
    }

    public void setCanJieFeng(boolean canJieFeng) {
        this.canJieFeng = canJieFeng;
    }

    public List<Integer> getRetain3List() {
        return retain3List;
    }

    public void setRetain3List(List<Integer> retain3List) {
        this.retain3List = retain3List;
    }
}
