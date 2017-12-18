package com.code.server.game.cow;

import com.code.server.constant.response.IfacePlayerInfoVo;
import com.code.server.game.room.IfacePlayerInfo;

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
public class PlayerCow  implements IfacePlayerInfo {
    @Override
    public IfacePlayerInfoVo toVo() {
        PlayerCowVo vo = new PlayerCowVo();

        return vo;
    }

    @Override
    public IfacePlayerInfoVo toVo(long watchUser) {
        PlayerCowVo vo = new PlayerCowVo();

        return vo;
    }
}
