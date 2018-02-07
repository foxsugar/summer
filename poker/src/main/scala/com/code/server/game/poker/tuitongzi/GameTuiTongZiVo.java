package com.code.server.game.poker.tuitongzi;

import com.code.server.constant.response.IfaceGameVo;
import com.code.server.constant.response.IfacePlayerInfoVo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameTuiTongZiVo implements IfaceGameVo{
//
//    var playerCardInfos =  new util.HashMap[Long,IfacePlayerInfoVo]()
//
//    var bankerId: Long = -1L
//



//    //状态
//    var state: Int = 0
//
//    var lastCards:util.List[Int] = _
//
//    var bankerInitScore:Int = 0

    protected Map<Long, IfacePlayerInfoVo> playerCardInfos = new HashMap<>();
    protected  Long bankerId ;
    protected Integer state = TuiTongZiConstant.STATE_START;
    protected long potBottom;
    protected long firstBanerCount;
    protected long zhuangCount;
    protected List<Integer> cards = new ArrayList<>();


}
