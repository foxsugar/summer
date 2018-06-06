package com.code.server.game.poker.xuanqiqi;

import com.code.server.constant.response.RoomVo;

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
public class RoomXuanQiQiVo extends RoomVo {

    protected int cricleNumber;//轮数
    protected Map<Long, Integer> numThree = new HashMap<>();
    protected Map<Long, Integer> numFive = new HashMap<>();
    protected Map<Long, Integer> numSix = new HashMap<>();

}
