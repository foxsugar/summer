package com.code.server.game.poker.cow;

import com.code.server.constant.response.RoomVo;

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
public class RoomCowVo extends RoomVo {

    public List<PlayerCowVo> playerList = new ArrayList<>();//用户列表


    public long countDown;

    public List<PlayerCowVo> getPlayerList() {
        return playerList;
    }

    public void setPlayerList(List<PlayerCowVo> playerList) {
        this.playerList = playerList;
    }



    public long getCountDown() {
        return countDown;
    }

    public void setCountDown(long countDown) {
        this.countDown = countDown;
    }
}
