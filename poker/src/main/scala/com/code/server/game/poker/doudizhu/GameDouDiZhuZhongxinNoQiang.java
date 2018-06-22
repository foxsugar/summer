package com.code.server.game.poker.doudizhu;

/**
 * Created by sunxianping on 2018/6/20.
 */
public class GameDouDiZhuZhongxinNoQiang extends GameDouDiZhuZhongxin {

    @Override
    protected void qiangStepStart() {
        //选定地主
        chooseDizhu();

        startPlay(dizhu);

    }
}
