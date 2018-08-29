package com.code.server.game.poker.doudizhu;

import java.util.Collections;
import java.util.List;

/**
 * Created by sunxianping on 2018/8/29.
 */
public class GameDouDiHuaPai extends GameDouDiZhuLinFenLongQi {


    @Override
    public void init(List<Long> users, long dizhuUser) {
        this.initCardNum = 17;
        super.init(users, dizhuUser);

        //生成操作列表
        operateNode = OperateNode.initOperate(canJiaoUser, users);

    }

    @Override
    protected void shuffle(){
        for(int i=1;i<=55;i++){//多个癞子
            cards.add(i);
        }
        Collections.shuffle(cards);
    }


}
