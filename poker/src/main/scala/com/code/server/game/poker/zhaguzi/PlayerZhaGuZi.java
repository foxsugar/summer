package com.code.server.game.poker.zhaguzi;
import com.code.server.constant.response.IfacePlayerInfoVo;
import com.code.server.game.poker.pullmice.IfCard;
import com.code.server.game.room.IfacePlayerInfo;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.springframework.beans.BeanUtils;
import scala.Int;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlayerZhaGuZi implements IfacePlayerInfo {

    public static Integer UNKNOW = 0;
    public static Integer SAN_JIA = 1;
    public static Integer GU_JIA = 2;
    public static Integer DRAW = 3;

    protected long userId;

    protected List<Integer> cards = new ArrayList<>();

    //发话时的操作
    private int op = Operator.MEI_LIANG;

    protected List<Integer> opList = new ArrayList<>();

    //自己是第几个出完牌的人
    protected int rank = 0;

    //房间人数
    private Integer roomPersonNum;

    private Integer sanJia;

    private double score;

    private Integer isWinner;

    //是不是能接风
    private boolean canJieFeng;

    //是不是改自己出牌
    private boolean selfTurn;

    //自己发牌之后持有的三 算分用 其他情况不用
    private List<Integer> retain3List = new ArrayList<>();

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public boolean isCanJieFeng() {
        return canJieFeng;
    }

    public void setCanJieFeng(boolean canJieFeng) {
        this.canJieFeng = canJieFeng;
    }

    public int getOp() {
        return op;
    }

    public void setOp(int op) {
        this.op = op;
    }
    //是否出完牌
    public boolean isOver(){
        return this.cards.size() == 0 ? true : false;
    }

    public Integer getIsWinner() {
        return isWinner;
    }

    public void setIsWinner(Integer isWinner) {
        this.isWinner = isWinner;
    }

    private List<Integer> liangList = new ArrayList<>();

    public List<Integer> getLiangList() {
        return liangList;
    }

    public void setLiangList(List<Integer> liangList) {
        this.liangList = liangList;
    }

    public Integer getRoomPersonNum() {
        return roomPersonNum;
    }

    public void setRoomPersonNum(Integer roomPersonNum) {
        this.roomPersonNum = roomPersonNum;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public Integer getSanJia() {
        return sanJia;
    }

    public void setSanJia(Integer sanJia) {
        this.sanJia = sanJia;
    }

    public List<Integer> getRetain3List() {
        return retain3List;
    }

    public boolean isSelfTurn() {
        return selfTurn;
    }

    public void setSelfTurn(boolean selfTurn) {
        this.selfTurn = selfTurn;
    }

    public void setRetain3List(List<Integer> retain3List) {
        this.retain3List = retain3List;
    }

    @Override
    public IfacePlayerInfoVo toVo() {

        PlayerZhaGuZiVo playerZhaGuZiVo = new PlayerZhaGuZiVo();
        BeanUtils.copyProperties(this, playerZhaGuZiVo);
        playerZhaGuZiVo.setIsSanJia_(this.sanJia);
        playerZhaGuZiVo.cards = new ArrayList<>();

        IfCard ifCard = new IfCard() {
            @Override
            public Map<Integer, Integer> cardDict() {
                return CardUtils.getCardDict();
            }
        };

        for (int i = 0; i < this.cards.size(); i++){

            Integer ret = CardUtils.local2Client(this.cards.get(i), ifCard);
            playerZhaGuZiVo.cards.add(ret);
        }

        List<Integer> aList = new ArrayList<>();

        for (Integer card : this.getRetain3List()){

            Integer ret = CardUtils.local2Client(card, ifCard);

            aList.add(ret);
        }

        playerZhaGuZiVo.setRetain3List(aList);

        List<Integer> bList = new ArrayList<>();

        for (Integer liang : this.liangList){

            if (liang == -1){
                bList.add(liang);
                continue;
            }
            Integer ret = CardUtils.local2Client(liang, ifCard);
            bList.add(ret);
        }

        playerZhaGuZiVo.setLiangList(bList);

        return playerZhaGuZiVo;
    }



    @Override
    public IfacePlayerInfoVo toVo(long watchUser) {
        return this.toVo();
    }
}
