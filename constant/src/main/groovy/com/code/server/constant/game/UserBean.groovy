package com.code.server.constant.game

import com.code.server.constant.db.UserInfo
import com.code.server.constant.response.UserVo

/**
 * Created by sunxianping on 2017/5/27.
 */
class UserBean implements IUserBean {
    long id; // required
    String username; // required
    String image; // required
    String seatId; // required
    String account; // required
    String password;
    String ipConfig; // required
    double money; // required
    double gold;
    String roomId; // required
    int vip; // required
    String uuid; // required
    String openId; // required
    int sex; // required
    String marquee; // required
    int referee;
    UserInfo userInfo;
    String download2;

    @Override
    UserVo toVo() {
        UserVo userVo = new UserVo()
        userVo.id = this.id
        userVo.username = this.username // required
        userVo.image = this.image// required
        userVo.seatId  = this.seatId// required
        userVo.account= this.account // required
        userVo.ipConfig = this.ipConfig // required
        userVo.money = this.money// required
        userVo.roomId = this.roomId// required
        userVo.vip  = this.vip//required
        userVo.uuid = this.uuid// required
        userVo.openId = this.openId// required
        userVo.sex = this.sex//required
        userVo.marquee= this.marquee // required
        userVo.referee= this.referee
        userVo.userInfo = this.userInfo
        userVo.download2 = this.download2
        return userVo
    }
}
