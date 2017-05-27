package com.code.server.constant.response;



import java.util.*;

/**
 * Created by sunxianping on 2017/3/15.
 */
public class GameTianDaKengVo extends GameVo{


    protected List<Integer> cards = new ArrayList<>();//牌

    protected List<Integer> tableCards = new ArrayList<>();//剩余牌
    protected Map<Long,PlayerCardInfoTianDaKengVo> playerCardInfos = new HashMap<>();
    protected List<Long> users = new ArrayList<>();
    protected Random rand = new Random();

    protected Map<Long,Double> allChip = new HashMap<>();//总下注数
    protected Map<Long,Double> curChip = new HashMap<>();//当前下注数


    protected long currentTurn;//当前操作人
    protected int chip;//下注
    protected int trunNumber;//第几张牌了


    protected List<Long> aliveUser = new ArrayList<>();//存活的人
    protected List<Long> curUser = new ArrayList<>();//本轮的人
    protected List<Long> canRaiseUser = new ArrayList<>();//可以反踢的人

    protected Map<Long,Integer> gameuserStatus = new HashMap<>();//玩家游戏中的状态


}
