package com.code.server.game.poker.guess;

import com.code.server.constant.response.GameVo;

import java.util.HashMap;
import java.util.Map;

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
public class GameGuessCarVo extends GameVo {

    public BankerCardInfoGuessCar bankerCardInfos = new BankerCardInfoGuessCar();
    public Map<Long, PlayerCardInfoGuessCar> playerCardInfos = new HashMap<>();
    protected int color = -1;
    protected double redScore;//red 0 green 1
    protected double greenScore;

}
