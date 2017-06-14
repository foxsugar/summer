package com.code.server.constant.response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sunxianping on 2017/3/15.
 */
public class RoomVo implements IfaceRoomVo {


    public String roomType;
    public String roomId;
    public int multiple;//倍数
    public int gameNumber;
    public long createUser;
    public IfaceGameVo game;
    public int curGameNumber;
    public int createType;
    public double goldRoomType;

    //填大坑专用
    public boolean isLastDraw;//是否平局
    public int drawForLeaveChip;//平局留下筹码
    public int hasNine;

    public Map<Long, Integer> userStatus = new HashMap<>();//用户状态
    public List<UserVo> userList = new ArrayList<>();//用户列表
    public Map<Long, Double> userScores = new HashMap<>();
    public int personNumber;

    public RoomVo() {
    }


}
