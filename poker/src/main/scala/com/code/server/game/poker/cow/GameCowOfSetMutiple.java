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
public class GameCowOfSetMutiple extends GameCow {

    @Override
    public int setMultipleForGetBanker(long userId,double multiple){
        playerCardInfos.get(userId).setSetMultipleForGetBanker(0);
        if(-1.0==setMultipleForGetBankers.get(userId)){
            setMultipleForGetBankers.put(userId,multiple);
        }
        MsgSender.sendMsg2Player("gameService", "setMultipleForGetBanker", setMultipleForGetBankers, users);
        if(canSetRoomMultiple()){//设置roomMultiple
            room.setBankerId(maxMultiple());
        }
        MsgSender.sendMsg2Player("gameService", "tellBankerId", room.getBankerId(), users);
        noticePlayerBet();//继续原来的步骤
        updateLastOperateTime();
        return 0;
    }

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

    //是否全设置过倍数
    private boolean canSetRoomMultiple(){
        boolean b = true;
        a:for (Long l:setMultipleForGetBankers.keySet()) {
            if(-1==setMultipleForGetBankers.get(l)){
                b = false;
                break a;
            }
        }
        return b;
    }

    //选择庄
    private Long chooseBanker(){
        long l = 0l;

        return l;
    }

    //几个人叫最大分
    private long maxMultiple(){
        int one = 0;
        int two = 0;
        int three = 0;
        long banker = 0l;
        int random = Math.abs((int)System.currentTimeMillis());
        for (Long l:setMultipleForGetBankers.keySet()) {
            if(setMultipleForGetBankers.get(l)==1.0){
                one++;
            }else if(setMultipleForGetBankers.get(l)==2.0){
                two++;
            }else if(setMultipleForGetBankers.get(l)==3.0){
                three++;
            }
        }
        if (one>0){
            banker = users.get(random%one);
        }else if(two>0){
            banker = users.get(random%two);
        }else if(three>0){
            banker = users.get(random%three);
        }
        return banker;
    }
}
