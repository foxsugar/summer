package com.code.server.constant.response;

import com.code.server.cardgame.core.Game;
import com.code.server.cardgame.core.tiandakeng.GameTianDaKeng;
import com.code.server.cardgame.core.tiandakeng.PlayerCardInfoTianDaKeng;

import java.util.*;

/**
 * Created by sunxianping on 2017/3/15.
 */
public class GameTianDaKengVo extends GameVo{


    protected List<Integer> cards = new ArrayList<>();//牌

    protected List<Integer> tableCards = new ArrayList<>();//剩余牌
    protected Map<Long,PlayerCardInfoTianDaKengVo> playerCardInfos = new HashMap<>();
    protected List<Long> users = new ArrayList<>();
    protected Random rand = new Random();

    protected Map<Long,Double> allChip = new HashMap<>();//总下注数
    protected Map<Long,Double> curChip = new HashMap<>();//当前下注数


    protected long currentTurn;//当前操作人
    protected int chip;//下注
    protected int trunNumber;//第几张牌了


    protected List<Long> aliveUser = new ArrayList<>();//存活的人
    protected List<Long> curUser = new ArrayList<>();//本轮的人
    protected List<Long> canRaiseUser = new ArrayList<>();//可以反踢的人

    protected Map<Long,Integer> gameuserStatus = new HashMap<>();//玩家游戏中的状态


    public static GameTianDaKengVo getGameTianDaKengVo(Game game, long uid){
        GameTianDaKengVo vo = new GameTianDaKengVo();
        if(game!=null){
            GameTianDaKeng tianDaKeng = (GameTianDaKeng) game;

            vo.cards = tianDaKeng.getCards();
            vo.tableCards = tianDaKeng.getTableCards();
            vo.users = tianDaKeng.getUsers();
            vo.allChip = tianDaKeng.getAllChip();
            vo.curChip = tianDaKeng.getCurChip();
            vo.currentTurn = tianDaKeng.getCurrentTurn();
            vo.chip = tianDaKeng.getChip();
            vo.trunNumber = tianDaKeng.getTrunNumber();
            vo.aliveUser = tianDaKeng.getAliveUser();
            vo.curUser = tianDaKeng.getCurUser();
            vo.canRaiseUser = tianDaKeng.getCanRaiseUser();
            vo.gameuserStatus = tianDaKeng.getGameuserStatus();

            //玩家牌信息
            for (PlayerCardInfoTianDaKeng playerCardInfo : tianDaKeng.getPlayerCardInfos().values()) {
                /*PlayerCardInfoTianDaKeng temp = playerCardInfo;
                if(temp.getUserId()!=uid){
                    temp.setMyselfCards(null);
                    temp.setAllCards(null);
                }*/
                vo.playerCardInfos.put(playerCardInfo.userId, new PlayerCardInfoTianDaKengVo(playerCardInfo, uid));
            }
        }
        return vo;

    }
}
