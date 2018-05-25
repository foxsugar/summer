package com.code.server.game.poker.doudizhu;

/**
 * Created by sunxianping on 2018/5/24.
 */
public class GameDouDiZhuMaoSanNoQiang extends GameDouDiZhuMaoSan {


    @Override
    protected void qiangStepStart() {
        //选定地主
        chooseDizhu();

        startPlay(dizhu);


//        step = STEP_QIANG_DIZHU;
//        long nextId = nextTurnId(dizhu);
//        this.canQiangUser = nextId;
//        noticeCanQiang(nextId);
    }
}
