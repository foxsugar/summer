package com.code.server.game.poker.playseven;

import com.code.server.game.room.service.IRobot;

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
public interface ISevenRobot extends IRobot {

    void shouQi(GamePlaySeven gamePlaySeven);
    void danLiang(GamePlaySeven gamePlaySeven);
    void shuangLiang(GamePlaySeven gamePlaySeven);
    void fanZhu(GamePlaySeven gamePlaySeven);
    void renShu(GamePlaySeven gamePlaySeven);
    void changeTableCards(GamePlaySeven gamePlaySeven);
    void play(GamePlaySeven gamePlaySeven);
    void noticeGetCardAgain(GamePlaySeven gamePlaySeven);
}
