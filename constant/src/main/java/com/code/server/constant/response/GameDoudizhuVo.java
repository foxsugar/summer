package com.code.server.constant.response;

import com.code.server.cardgame.core.Game;
import com.code.server.cardgame.core.doudizhu.CardStruct;
import com.code.server.cardgame.core.doudizhu.GameDouDiZhu;
import com.code.server.cardgame.core.doudizhu.GameDouDiZhuLinFen;
import com.code.server.cardgame.core.doudizhu.PlayerCardInfoDouDiZhu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sunxianping on 2017/4/19.
 */
public class GameDoudizhuVo extends GameVo {

    protected List<Integer> tableCards = new ArrayList<>();//底牌
    protected Map<Long,PlayerCardInfoVo> playerCardInfos = new HashMap<>();
    protected long dizhu;//地主

    protected long canJiaoUser;//可以叫地主的人
    protected long canQiangUser;//可以抢地主的人
    protected long jiaoUser;//叫的人
    protected long qiangUser;//抢的人

    protected long playTurn;//该出牌的人
    protected CardStruct lastCardStruct;

    protected int step;//步骤
    protected int curMultiple;
    protected int tableScore;


    public static GameVo getGameVo(Game game, long uid){
        GameDoudizhuVo vo = new GameDoudizhuVo();
        if (game instanceof GameDouDiZhu) {
            GameDouDiZhu douDiZhu = (GameDouDiZhu) game;

            //设置地主
            vo.dizhu = douDiZhu.getDizhu();
            vo.step = douDiZhu.getStep();
            vo.canJiaoUser = douDiZhu.getCanJiaoUser();
            vo.canQiangUser = douDiZhu.getCanQiangUser();
            vo.jiaoUser = douDiZhu.getJiaoUser();
            vo.qiangUser = douDiZhu.getQiangUser();
            vo.lastCardStruct = douDiZhu.getLastCardStruct();
            //该出牌的玩家
            vo.playTurn = douDiZhu.getPlayTurn();
            vo.curMultiple = douDiZhu.getMultiple();
            vo.tableScore = douDiZhu.getTableScore();
            if(uid == douDiZhu.getDizhu() || !(game instanceof GameDouDiZhuLinFen)){//玩家是地主 并且是临汾斗地主
                vo.tableCards.addAll(douDiZhu.getTableCards());
            }

            //玩家牌信息
            for (PlayerCardInfoDouDiZhu playerCardInfo : douDiZhu.getPlayerCardInfos().values()) {
                vo.playerCardInfos.put(playerCardInfo.userId, new PlayerCardInfoVo(playerCardInfo, uid));
            }

        }
        return vo;

    }
}
