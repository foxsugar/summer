package com.code.server.game.poker.tuitongzi;

import com.code.server.constant.response.RoomVo;

public class RoomTuiTongZiVo extends RoomVo {

    protected long potBottom;
    protected long bankerId;
    protected long zhuangCount;
    protected long firstBanerCount = 0;
    //作弊的那个人的id
    protected long cheatId = -1;
}
