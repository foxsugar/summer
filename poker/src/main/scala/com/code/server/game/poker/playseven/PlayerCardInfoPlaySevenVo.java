package com.code.server.game.poker.playseven;

import com.code.server.constant.response.IfacePlayerInfoVo;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：${project_name}
 * 类名称：${type_name}
 * 类描述：
 * 创建人：Clark
 * 创建时间：${date} ${time}
 * 修改人：Clark
 * 修改时间：${date} ${time}
 * 修改备注：
 *
 * @version 1.0
 */
public class PlayerCardInfoPlaySevenVo implements IfacePlayerInfoVo {

    public long userId;
    public List<Integer> handCards = new ArrayList<>();//手上的牌
    public List<Integer> playCards = new ArrayList<>();//当前出的牌


    public String shouQi;//首七     0默认，1提示可操作，2已操作，3过期，4之后可以操作
    public String danLiang;//单亮
    public String shuangLiang;//双亮
    public String fanZhu;//反主
    public String renShu;//认输
    public String seeTableCard;//看底牌
    public int fen = 0;//得分 5，10，K

    protected double score;

    public PlayerCardInfoPlaySevenVo(){

    }

}
