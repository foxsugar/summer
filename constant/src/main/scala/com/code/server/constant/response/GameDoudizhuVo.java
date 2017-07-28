package com.code.server.constant.response;


import com.code.server.constant.game.CardStruct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sunxianping on 2017/4/19.
 */
public class GameDoudizhuVo extends GameVo {

    public List<Integer> tableCards = new ArrayList<>();//底牌
    public Map<Long,IfacePlayerInfoVo> playerCardInfos = new HashMap<>();
    public long dizhu;//地主

    public long canJiaoUser;//可以叫地主的人
    public long canQiangUser;//可以抢地主的人
    public long jiaoUser;//叫的人
    public long qiangUser;//抢的人

    public long playTurn;//该出牌的人
    public CardStruct lastCardStruct;

    public int step;//步骤
    public int curMultiple;
    public int tableScore;


}
