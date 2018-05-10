package com.code.server.game.poker.zhaguzi;

import com.code.server.constant.response.IfacePlayerInfoVo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dajuejinxian on 2018/5/2.
 */
public class PlayerZhaGuZiVo implements IfacePlayerInfoVo{

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

    private Integer isWinner;
}
