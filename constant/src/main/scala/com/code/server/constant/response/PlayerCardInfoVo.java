package com.code.server.constant.response;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunxianping on 2017/3/20.
 */
public class PlayerCardInfoVo implements IfacePlayerInfoVo {
    public long userId;
    public List<Integer> cards = new ArrayList<>();//手上的牌
    public List<Integer> allCards = new ArrayList<>();//手上的牌
    public int cardNum;
    public boolean isQiang;
    public double score;
    public int zhaCount;



    public PlayerCardInfoVo() {
    }


}
