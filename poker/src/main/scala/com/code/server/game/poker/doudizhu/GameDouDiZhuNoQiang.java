package com.code.server.game.poker.doudizhu;

/**
 * Created by sunxianping on 2017/10/17.
 */
public class GameDouDiZhuNoQiang extends GameDouDiZhu {

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
