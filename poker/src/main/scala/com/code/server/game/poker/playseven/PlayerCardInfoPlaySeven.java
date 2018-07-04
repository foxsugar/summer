package com.code.server.game.poker.playseven;

import com.code.server.constant.response.IfacePlayerInfoVo;
import com.code.server.game.room.IfacePlayerInfo;

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
public class PlayerCardInfoPlaySeven implements IfacePlayerInfo {

    public long userId;
    public List<Integer> handCards = new ArrayList<>();//手上的牌
    public List<Integer> playCards = new ArrayList<>();//当前出的牌


    @Override
    public IfacePlayerInfoVo toVo() {
        PlayerCardInfoPlaySevenVo vo = new PlayerCardInfoPlaySevenVo();
        return vo;
    }

    @Override
    public IfacePlayerInfoVo toVo(long watchUser) {
        PlayerCardInfoPlaySevenVo vo = new PlayerCardInfoPlaySevenVo();
        return vo;
    }
}
