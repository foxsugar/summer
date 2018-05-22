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

    private Integer isSanJia;

    private Integer isWinner;

    private boolean canJieFeng;

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

    public Integer getIsSanJia() {
        return isSanJia;
    }

    public void setIsSanJia(Integer isSanJia) {
        this.isSanJia = isSanJia;
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
}
