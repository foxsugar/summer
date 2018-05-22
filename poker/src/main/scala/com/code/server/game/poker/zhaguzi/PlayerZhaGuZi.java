package com.code.server.game.poker.zhaguzi;
import com.code.server.constant.response.IfacePlayerInfoVo;
import com.code.server.game.poker.pullmice.IfCard;
import com.code.server.game.room.IfacePlayerInfo;
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

    private Integer isSanJia;

    private double score;

    private Integer isWinner;

    //是不是能接风
    private boolean canJieFeng;

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
        return this.rank == 0 ? false : true;
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

    public Integer getIsSanJia() {

        if (isSanJia > 0){
            return isSanJia;
        }

        if (roomPersonNum == 5){

            Integer hongtao = 7;
            Integer fangpian = 9;

            if (this.cards.size() < 10){
                return UNKNOW;
            }

            if (this.cards.contains(hongtao) || this.cards.contains(fangpian)){
                isSanJia = SAN_JIA;

            }else {
                isSanJia = GU_JIA;
            }

            return isSanJia;

        }else if (roomPersonNum == 6){

            Integer hongtao = 7;
            Integer fangpian = 9;
            Integer heitao = 6;

            if (this.cards.size() < 9){
                return UNKNOW;
            }

            if (this.cards.contains(hongtao) || this.cards.contains(fangpian) || this.cards.contains(heitao)){
                if (this.cards.contains(hongtao) || this.cards.contains(fangpian)){
                    isSanJia = SAN_JIA;
                }else {
                    isSanJia = GU_JIA;
                }

                return isSanJia;
            }
        }

        return UNKNOW;

    }

    @Override
    public IfacePlayerInfoVo toVo() {

        PlayerZhaGuZiVo playerZhaGuZiVo = new PlayerZhaGuZiVo();
        BeanUtils.copyProperties(this, playerZhaGuZiVo);

        playerZhaGuZiVo.cards.clear();

        for (int i = 0; i < playerZhaGuZiVo.cards.size(); i++){

            Integer ret = CardUtils.local2Client(playerZhaGuZiVo.cards.get(i), new IfCard() {
                @Override
                public Map<Integer, Integer> cardDict() {
                    return CardUtils.getCardDict();
                }
            });
            playerZhaGuZiVo.cards.add(ret);
        }

        return playerZhaGuZiVo;
    }

    @Override
    public IfacePlayerInfoVo toVo(long watchUser) {
        return this.toVo();
    }
}
