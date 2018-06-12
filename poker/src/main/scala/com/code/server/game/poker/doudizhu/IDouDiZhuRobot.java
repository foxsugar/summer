package com.code.server.game.poker.doudizhu;


import com.code.server.game.room.service.IRobot;

/**
 * Created by sunxianping on 2017/5/16.
 */
public interface IDouDiZhuRobot extends IRobot {


   void jiaoDizhu(GameDouDiZhu game);

   void qiangDizhu(GameDouDiZhu game);

   void play(GameDouDiZhu game);

   void pass(GameDouDiZhu game);
}
