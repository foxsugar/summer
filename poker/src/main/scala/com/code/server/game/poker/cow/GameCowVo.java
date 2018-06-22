package com.code.server.game.poker.cow;

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
public class GameCowVo extends GameVo {

    public Map<Long, PlayerCowVo> playerCardInfos = new HashMap<>();
    public Map<Long, Double> setMultipleForGetBankers = new HashMap<>();

    public Map<Long, Double> getSetMultipleForGetBankers() {
        return setMultipleForGetBankers;
    }

    public void setSetMultipleForGetBankers(Map<Long, Double> setMultipleForGetBankers) {
        this.setMultipleForGetBankers = setMultipleForGetBankers;
    }
}
