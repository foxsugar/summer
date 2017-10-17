package com.code.server.constant.response;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sunxianping on 2017/4/19.
 */
public class GameHitGoldFlowerVo extends GameVo {

    public List<Integer> cards = new ArrayList<>();//牌
    public Map<Long,IfacePlayerInfoVo> playerCardInfos = new HashMap<>();
    public int curRoundNumber;//当前轮数
    public Double chip;

    public List<Integer> leaveCards = new ArrayList<>();//剩余的牌，暂时无用
    public List<Long> aliveUser = new ArrayList<>();//存活的人
    public List<Long> seeUser = new ArrayList<>();//看牌的人
    public Long curUserId;
    public Double allTableChip;
}
