package com.code.server.game.poker.zhaguzi;

import com.code.server.constant.response.IfaceGameVo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dajuejinxian on 2018/5/7.
 */
public class GameZhaGuZiVo implements IfaceGameVo {

    protected Map<Long, PlayerZhaGuZi> playerCardInfos = new HashMap<>();

    protected RoomZhaGuZi room;

    protected List<Integer> cards = new ArrayList<Integer>();

//    protected List<Map<String, Object>> leaveCards = new ArrayList<>();

    protected Integer status = ZhaGuZiConstant.START_GAME;
}
