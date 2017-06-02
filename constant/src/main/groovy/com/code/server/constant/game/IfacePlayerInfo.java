package com.code.server.constant.game;


import com.code.server.constant.response.IfacePlayerInfoVo;

/**
 * Created by sunxianping on 2017/5/24.
 */
public interface IfacePlayerInfo extends IGameConstant {

    IfacePlayerInfoVo toVo();
    IfacePlayerInfoVo toVo(long watchUser);
}
