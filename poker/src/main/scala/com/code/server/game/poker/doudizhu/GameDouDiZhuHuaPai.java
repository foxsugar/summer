package com.code.server.game.poker.doudizhu;

import java.util.Collections;
import java.util.List;

/**
 * Created by sunxianping on 2018/8/29.
 */
public class GameDouDiZhuHuaPai extends GameDouDiZhuLinFenLongQi {


    @Override
    public void init(List<Long> users, long dizhuUser) {
        this.initCardNum = 17;
        //初始化玩家
        for (Long uid : users) {
            PlayerCardInfoDouDiZhu playerCardInfo = getGameTypePlayerCardInfo();
            playerCardInfo.userId = uid;
            playerCardInfos.put(uid, playerCardInfo);
        }
        this.users.addAll(users);


        shuffle();
        deal();
        //第一局 第一个玩家做地主
        dizhuUser = dizhuUser != 0 ? dizhuUser : users.get(0);
        chooseDizhu(dizhuUser);

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
