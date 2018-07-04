package com.code.server.game.poker.playseven;

import com.code.server.constant.response.RoomVo;

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
public class RoomPlaySevenVo extends RoomVo{

    public Integer fengDing;//封顶
    public boolean kouDiJiaJi;//扣底加级
    public boolean zhuangDanDaJiaBei;//庄单打加倍

    public long fanZhuUserId=0l;

}
