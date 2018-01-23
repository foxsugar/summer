package com.code.server.game.poker.cow;

import com.code.server.game.room.kafka.MsgSender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class GameCowOfChangeBanker extends GameCow {

    /**
     * 发送战绩
     */
    protected void sendResult() {
        GameResultCow gameResultCow  = new GameResultCow();
        List<Long> winnerList = new ArrayList<>();
        Map<Long, Double> userScores = new HashMap<>();
        for (PlayerCow p : playerCardInfos.values()) {
            if(p.getFinalScore()>0){
                winnerList.add(p.getUserId());
            }
            gameResultCow.getPlayerCardInfos().add( p.toVo());
            userScores.put(p.getUserId(),p.getFinalScore());
        }
        gameResultCow.setWinnerList(winnerList);

        //庄家无牛下庄
        if(18==playerCardInfos.get(room.getBankerId()).getPlayer().getGrade()){
            room.setBankerId(nextTurnId(room.getBankerId()));
        }
        gameResultCow.setBankerId(room.getBankerId());
        MsgSender.sendMsg2Player("gameService", "gameResult", gameResultCow, users);
    }

    protected long nextTurnId(long curId) {
        int index = users.indexOf(curId);

        int nextId = index + 1;
        if (nextId >= users.size()) {
            nextId = 0;
        }
        return users.get(nextId);
    }
}
