package com.code.server.game.poker.playseven;

import com.code.server.constant.response.UserOfRoom;

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
public class UserOfRoomSeven extends UserOfRoom {

    public Integer fengDing;//封顶
    public boolean kouDiJiaJi;//抠底加级
    public boolean zhuangDanDaJiaBei;//庄单打加倍

    public Integer getFengDing() {
        return fengDing;
    }

    public void setFengDing(Integer fengDing) {
        this.fengDing = fengDing;
    }

    public boolean isKouDiJiaJi() {
        return kouDiJiaJi;
    }

    public void setKouDiJiaJi(boolean kouDiJiaJi) {
        this.kouDiJiaJi = kouDiJiaJi;
    }

    public boolean isZhuangDanDaJiaBei() {
        return zhuangDanDaJiaBei;
    }

    public void setZhuangDanDaJiaBei(boolean zhuangDanDaJiaBei) {
        this.zhuangDanDaJiaBei = zhuangDanDaJiaBei;
    }
}
